package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.event.events.RenderLevelEvent;
import com.phoenixclient.gui.hud.element.ListWindow;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.file.CSVFile;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.Draw3DUtil;
import com.phoenixclient.util.setting.Container;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.phys.AABB;

import java.awt.*;
import java.util.*;

import static com.phoenixclient.PhoenixClient.MC;
import static net.minecraft.util.Mth.clamp;

/**
 * Pallet mode taken from Trouser-Streak Meteor Addon.
 */
public class Chunks extends Module {

    private HashMap<Vector, Boolean> loadedChunksMap = new HashMap<>(); //Key: ChunkPos, Value isNewChunk -- Save/Load this data as a CSV every time the client is loaded

    //TODO: Make these server dependent
    private final CSVFile palletFileOW = new CSVFile("PhoenixClient/chunks", "newChunksPalletOverworld.csv");
    private final CSVFile palletFileNE = new CSVFile("PhoenixClient/chunks", "newChunksPalletNether.csv");
    private final CSVFile palletFileEN = new CSVFile("PhoenixClient/chunks", "newChunksPalletEnd.csv");

    private final CSVFile liquidFileOW = new CSVFile("PhoenixClient/chunks", "newChunksLiquidOverworld.csv");
    private final CSVFile liquidFileNE = new CSVFile("PhoenixClient/chunks", "newChunksLiquidNether.csv");
    private final CSVFile liquidFileEN = new CSVFile("PhoenixClient/chunks", "newChunksLiquidEnd.csv");

    private final CSVFile copperFileOW = new CSVFile("PhoenixClient/chunks", "newChunksCopperOW.csv");
    private final CSVFile copperFileNE = new CSVFile("PhoenixClient/chunks", "newChunksCopperNE.csv");
    private final CSVFile copperFileEN = new CSVFile("PhoenixClient/chunks", "newChunksCopperEN.csv");

    private final OnChange<ResourceKey<Level>> onDimensionChange = new OnChange<>();

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Type of algorithm for new chunk detection",
            "Pallet").setModeData("Pallet", "Liquid", "Copper");

    private final SettingGUI<Integer> range = new SettingGUI<>(
            this,
            "Extended Range",
            "Extra range added on to the render distance for chunks",
            0).setSliderData(0, 16, 1);

    public Chunks() {
        super("Chunks", "Send information of chunks upon load", Category.SERVER, false, -1);
        addSettings(mode, range);
        addEventSubscriber(Event.EVENT_PACKET, this::onPacket);
        addEventSubscriber(Event.EVENT_RENDER_LEVEL, this::onRender);
    }

    public void onPacket(PacketEvent event) {
        if (MC.level == null) return;
        if (event.getPacket() instanceof ClientboundLevelChunkWithLightPacket p) { //This is the only packet sent to the client with chunk data
            ChunkPos pos = new ChunkPos(p.getX(), p.getZ());
            LevelChunk chunk = new LevelChunk(MC.level, pos);
            chunk.replaceWithPacketData(p.getChunkData().getReadBuffer(), p.getChunkData().getHeightmaps(), p.getChunkData().getBlockEntitiesTagsConsumer(p.getX(), p.getZ()));

            boolean isNewChunk = switch (mode.get()) {
                case "Pallet" -> palletData(p, chunk);
                case "Liquid" -> liquidData(p, chunk);
                case "Copper" -> copperData(p, chunk);
                default -> throw new IllegalStateException("Unexpected value: " + mode.get());
            };

            Vector keyPos = new Vector(pos.x, 0, pos.z);
            loadedChunksMap.putIfAbsent(keyPos, isNewChunk);

            //Save the CSV with the previous mode
            //Load the CSV of the current mode
            mode.runOnChange(() -> {
                if (mode.getPrevious() != null) {
                    System.out.println("Saving: " + mode.getPrevious() + " " + MC.level.dimension());
                    getProperFile(mode.getPrevious(), MC.level.dimension()).save(loadedChunksMap);
                }
                loadCurrentFile();
            });
            onDimensionChange.run(MC.level.dimension(), () -> {
                if (onDimensionChange.getPrevValue() != null) {
                    System.out.println("Saving: " + mode.get() + " " + onDimensionChange.getPrevValue());
                    getProperFile(mode.get(), onDimensionChange.getPrevValue()).save(loadedChunksMap);
                }
                loadCurrentFile();
            });
        }
    }

    public void onRender(RenderLevelEvent event) {
        try {
            for (Vector chunkPos : getLoadedChunkPositions()) {
                if (!loadedChunksMap.containsKey(chunkPos)) continue;
                //New chunks are RED
                //Old chunks are GREEN
                Color color = loadedChunksMap.get(chunkPos) ? Color.RED : Color.GREEN;
                Draw3DUtil.drawOutlineBox(event.getLevelPoseStack(), new AABB(0, 0, 0, 16, 0, 16), chunkPos.getMultiplied(16).y(-64), color);
            }
        } catch (NullPointerException | ConcurrentModificationException e) {
            //This shouldn't happen, but it may due to the desync between the render thread and game thread with the packets
        }
    }

    @Override
    public String getModTag() {
        return mode.get();
    }

    @Override
    public void onEnabled() {
        if (updateDisableOnEnabled()) return;
        //Load Current File
        setMapFromFile(getProperFile(mode.get(), MC.level.dimension()));
    }

    @Override
    public void onDisabled() {
        //Save Current File
        getProperFile(mode.get(), MC.level.dimension()).save(loadedChunksMap);

        //Release memory
        loadedChunksMap = new HashMap<>();
        System.gc();
    }

    private ArrayList<Vector> getLoadedChunkPositions() {
        ArrayList<Vector> chunkList = new ArrayList<>();
        int renderDistance = MC.options.renderDistance().get() + range.get();

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                Vector keyPos = new Vector((int) MC.player.getX() / 16 + x, 0, (int) MC.player.getZ() / 16 + z);
                chunkList.add(keyPos);
            }
        }
        return chunkList;
    }


    private CSVFile getProperFile(String mode, ResourceKey<Level> dimension) {
        ResourceKey<Level> ow = Level.OVERWORLD;
        ResourceKey<Level> ne = Level.NETHER;
        ResourceKey<Level> en = Level.END;
        switch (mode) {
            case "Pallet" -> {
                if (dimension.equals(ow)) return palletFileOW;
                if (dimension.equals(ne)) return palletFileNE;
                if (dimension.equals(en)) return palletFileEN;
            }
            case "Liquid" -> {
                if (dimension.equals(ow)) return liquidFileOW;
                if (dimension.equals(ne)) return liquidFileNE;
                if (dimension.equals(en)) return liquidFileEN;
            }
            case "Copper" -> {
                if (dimension.equals(ow)) return copperFileOW;
                if (dimension.equals(ne)) return copperFileNE;
                if (dimension.equals(en)) return copperFileEN;
            }
        }
        throw new IllegalStateException("Unexpected value: " + mode);
    }

    private void loadCurrentFile() {
        System.out.println("Loading: " + mode.get() + " " + MC.level.dimension());
        setMapFromFile(getProperFile(mode.get(), MC.level.dimension()));
    }

    private void setMapFromFile(CSVFile file) {
        HashMap<String, String[]> loadedMap = file.getDataAsMap();
        HashMap<Vector, Boolean> newMap = new HashMap<>();
        for (Map.Entry<String, String[]> set : loadedMap.entrySet()) {
            newMap.put(Vector.getFromString(set.getKey()), Boolean.parseBoolean(set.getValue()[0]));
        }
        loadedChunksMap = newMap;
    }
















    //TODO: Review this, and streamline this. it is VERY difficult to read
    private boolean palletData(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk) {
        FriendlyByteBuf buf = packet.getChunkData().getReadBuffer();
        boolean isNewChunk = false;
        boolean chunkIsBeingUpdated = false;
        Container<Boolean> beingUpdatedDetector = new Container<>(false);


        if (buf.readableBytes() < 3) return false; // Ensure we have at least 3 bytes (short + byte)

        boolean firstchunkappearsnew = false;
        int loops = 0;
        int newChunkQuantifier = 0;
        int oldChunkQuantifier = 0;

        try {
            while (buf.readableBytes() > 0 && loops < 8) {
                // Chunk Section structure
                short blockCount = buf.readShort();
                //System.out.println("Section: " + loops + " | Block count: " + blockCount);

                // Block states Paletted Container
                if (buf.readableBytes() < 1) break;
                int blockBitsPerEntry2 = buf.readUnsignedByte();
                //System.out.println("Section: " + loops + " | Block Bits Per Entry: " + blockBitsPerEntry2);

                if (blockBitsPerEntry2 == 0) {
                    // Single valued palette
                    int singleBlockValue = buf.readVarInt();
                    //BlockState blockState = Block.STATE_IDS.get(singleBlockValue);
                    //System.out.println("Section: " + loops + " | Single Block Value: " + singleBlockValue + " | Blockstate: " + blockState);
                    buf.readVarInt(); // Data Array Length (should be 0)
                } else if (blockBitsPerEntry2 >= 4 && blockBitsPerEntry2 <= 8) {
                    LevelChunkSection section = chunk.getSections()[loops];
                    PalettedContainer<BlockState> palettedContainer = section.getStates();
                    Set<BlockState> bstates = new HashSet<>();
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                bstates.add(palettedContainer.get(x, y, z));
                            }
                        }
                    }
                    // Indirect palette
                    int blockPaletteLength = buf.readVarInt();
                    //System.out.println("Section: " + loops + " | Block palette length: " + blockPaletteLength);
                    //System.out.println("Section: " + loops + " | bstates.size() "+bstates.size());
                    //System.out.println("Section: " + loops + " | blockPaletteLength"+blockPaletteLength);
                    int isNewSection = 0;
                    int isBeingUpdatedSection = 0;
                    int bstatesSize = bstates.size();
                    if (bstatesSize <= 1) bstatesSize = blockPaletteLength;
                    if (bstatesSize < blockPaletteLength) {
                        isNewSection = 2;
                        //System.out.println("Section: " + loops + " | smaller bstates size!!!!!!!");
                        newChunkQuantifier++; //double the weight of this
                    }
                    for (int i = 0; i < blockPaletteLength; i++) {
                        int blockPaletteEntry = buf.readVarInt();
                        //BlockState blockState = Block.STATE_IDS.get(blockPaletteEntry);
                        //System.out.println("Section: " + loops + " | Block palette entry " + i + ": " + blockPaletteEntry + " | Blockstate: " + blockState);
                        if (i == 0 && loops == 0 && blockPaletteEntry == 0 && MC.level.dimension() != Level.END)
                            firstchunkappearsnew = true;
                        if (i == 0 && blockPaletteEntry == 0 && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END)
                            isNewSection++;
                        if (i == 1 && (blockPaletteEntry == 80 || blockPaletteEntry == 1 || blockPaletteEntry == 9 || blockPaletteEntry == 5781) && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END)
                            isNewSection++;
                        if (i == 2 && (blockPaletteEntry == 5781 || blockPaletteEntry == 10 || blockPaletteEntry == 22318) && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END)
                            isNewSection++;
                        if (loops == 4 && blockPaletteEntry == 79 && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END) {
                            //System.out.println("CHUNK IS BEING UPDATED!!!!!!");
                            if (!chunkIsBeingUpdated && beingUpdatedDetector.get()) chunkIsBeingUpdated = true;
                        }
                        if (blockPaletteEntry == 0 && (MC.level.dimension() == Level.NETHER || MC.level.dimension() == Level.END))
                            isBeingUpdatedSection++;
                    }
                    if (isBeingUpdatedSection >= 2) oldChunkQuantifier++;
                    if (isNewSection >= 2) newChunkQuantifier++;

                    // Data Array
                    int blockDataArrayLength = buf.readVarInt();
                    //System.out.println("Section: " + loops + " | Block Data Array Length: " + blockDataArrayLength);
                    if (buf.readableBytes() >= blockDataArrayLength * 8) {
                        buf.skipBytes(blockDataArrayLength * 8);
                    } else {
                        //System.out.println("Section: " + loops + " | Not enough data for block array, skipping remaining: " + buf.readableBytes());
                        buf.skipBytes(buf.readableBytes());
                        break;
                    }
                } else if (blockBitsPerEntry2 == 15) {
                    // Direct palette (no palette sent)
                    int blockDataArrayLength = buf.readVarInt();
                    //System.out.println("Section: " + loops + " | Block Data Array Length (Direct): " + blockDataArrayLength);
                    if (buf.readableBytes() >= blockDataArrayLength * 8) {
                        buf.skipBytes(blockDataArrayLength * 8);
                    } else {
                        //System.out.println("Section: " + loops + " | Not enough data for block array, skipping remaining: " + buf.readableBytes());
                        buf.skipBytes(buf.readableBytes());
                        break;
                    }
                } else {
                    //System.out.println("Section: " + loops + " | Invalid block bits per entry: " + blockBitsPerEntry2);
                    break;
                }

                // Biomes Paletted Container
                if (buf.readableBytes() < 1) {
                    //System.out.println("Section: " + loops + " | No biome data available");
                    break;
                }

                int biomeBitsPerEntry = buf.readUnsignedByte();
                //System.out.println("Section: " + loops + " | Biome Bits Per Entry: " + biomeBitsPerEntry);

                if (biomeBitsPerEntry == 0) {
                    // Single valued palette
                    int singleBiomeValue = buf.readVarInt();
                    //Registry<Biome> biomeRegistry = MC.level.getRegistryManager().get(RegistryKeys.BIOME);
                    //Biome biome = biomeRegistry.get(singleBiomeValue);
                    //Identifier biomeId = biomeRegistry.getId(biome);
                    //System.out.println("Section: " + loops + " | Single Biome Value: " + singleBiomeValue + " | Biome: " + biomeId.toString());
                    if (singleBiomeValue == 39 && MC.level.dimension() == Level.END) isNewChunk = true;
                    buf.readVarInt(); // Data Array Length (should be 0)
                } else if (biomeBitsPerEntry >= 1 && biomeBitsPerEntry <= 3) {
                    // Indirect palette
                    int biomePaletteLength = buf.readVarInt();
                    //System.out.println("Section: " + loops + " | Biome palette length: " + biomePaletteLength);
                    for (int i = 0; i < biomePaletteLength; i++) {
                        if (buf.readableBytes() < 1) {
                            //System.out.println("Section: " + loops + " | Incomplete biome palette data");
                            break;
                        }
                        int biomePaletteEntry = buf.readVarInt();
                        //Registry<Biome> biomeRegistry = MC.level.getRegistryManager().get(RegistryKeys.BIOME);
                        //Biome biome = biomeRegistry.get(biomePaletteEntry);
                        //Identifier biomeId = biomeRegistry.getId(biome);
                        //System.out.println("Section: " + loops + " | Biome palette entry " + i + ": " + biomePaletteEntry + " | Biome: " + biomeId.toString());
                        if (i == 0 && biomePaletteEntry == 39 && MC.level.dimension() == Level.END)
                            isNewChunk = true;
                        if (!isNewChunk && i == 0 && biomePaletteEntry != 55 && MC.level.dimension() == Level.END)
                            isNewChunk = false;
                    }

                    // Data Array
                    if (buf.readableBytes() >= 1) {
                        int biomeDataArrayLength = buf.readVarInt();
                        //System.out.println("Section: " + loops + " | Biome Data Array Length: " + biomeDataArrayLength);
                        if (buf.readableBytes() >= biomeDataArrayLength * 8) {
                            buf.skipBytes(biomeDataArrayLength * 8);
                        } else {
                            //System.out.println("Section: " + loops + " | Not enough data for biome array, skipping remaining: " + buf.readableBytes());
                            buf.skipBytes(buf.readableBytes());
                            break;
                        }
                    } else {
                        //System.out.println("Section: " + loops + " | Not enough data for biome array length");
                        break;
                    }
                } else if (biomeBitsPerEntry == 6) {
                    // Direct palette (no palette sent)
                    int biomeDataArrayLength = buf.readVarInt();
                    //System.out.println("Section: " + loops + " | Biome Data Array Length (Direct): " + biomeDataArrayLength);
                    if (buf.readableBytes() >= biomeDataArrayLength * 8) {
                        buf.skipBytes(biomeDataArrayLength * 8);
                    } else {
                        //System.out.println("Section: " + loops + " | Not enough data for biome array, skipping remaining: " + buf.readableBytes());
                        buf.skipBytes(buf.readableBytes());
                        break;
                    }
                } else {
                    //System.out.println("Section: " + loops + " | Invalid biome bits per entry: " + biomeBitsPerEntry);
                    break;
                }

                loops++;
            }

            //System.out.println("newChunkQuantifier: " + newChunkQuantifier + ", loops: " + loops);
            if (loops > 0) {
                if (beingUpdatedDetector.get() && (MC.level.dimension() == Level.NETHER || MC.level.dimension() == Level.END)) {
                    double oldpercentage = ((double) oldChunkQuantifier / loops) * 100;
                    //System.out.println("Percentage: " + oldpercentage);
                    if (oldpercentage >= 25) chunkIsBeingUpdated = true;
                } else if (MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END) {
                    double percentage = ((double) newChunkQuantifier / loops) * 100;
                    //System.out.println("Percentage: " + percentage);
                    if (percentage >= 65) isNewChunk = true;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            if (beingUpdatedDetector.get() && (MC.level.dimension() == Level.NETHER || MC.level.dimension() == Level.END)) {
                double oldpercentage = ((double) oldChunkQuantifier / loops) * 100;
                //System.out.println("Percentage: " + oldpercentage);
                if (oldpercentage >= 25) chunkIsBeingUpdated = true;
            } else if (MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END) {
                double percentage = ((double) newChunkQuantifier / loops) * 100;
                //System.out.println("Percentage: " + percentage);
                if (percentage >= 65) isNewChunk = true;
            }
        }

        if (firstchunkappearsnew) isNewChunk = true;
        /*
        boolean bewlian = (MC.level.dimension() == Level.END) ? isNewChunk : !isOldGeneration;
        if (isNewChunk && !chunkIsBeingUpdated && bewlian) {
            try {
                if (!OldGenerationOldChunks.contains(oldpos) && !beingUpdatedOldChunks.contains(oldpos) && !tickexploitChunks.contains(oldpos) && !oldChunks.contains(oldpos) && !newChunks.contains(oldpos)) {
                    newChunks.add(oldpos);
                    if (save.get()) {
                        saveData("/NewChunkData.txt", oldpos);
                    }
                    return;
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } else if (!isNewChunk && !chunkIsBeingUpdated && isOldGeneration) {
            try {
                if (!OldGenerationOldChunks.contains(oldpos) && !beingUpdatedOldChunks.contains(oldpos) && !oldChunks.contains(oldpos) && !tickexploitChunks.contains(oldpos) && !newChunks.contains(oldpos)) {
                    OldGenerationOldChunks.add(oldpos);
                    if (save.get()) {
                        saveData("/OldGenerationChunkData.txt", oldpos);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } else if (chunkIsBeingUpdated) {
            try {
                if (!OldGenerationOldChunks.contains(oldpos) && !beingUpdatedOldChunks.contains(oldpos) && !oldChunks.contains(oldpos) && !tickexploitChunks.contains(oldpos) && !newChunks.contains(oldpos)) {
                    beingUpdatedOldChunks.add(oldpos);
                    if (save.get()) {
                        saveData("/BeingUpdatedChunkData.txt", oldpos);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } else if (!isNewChunk) {
            try {
                if (!OldGenerationOldChunks.contains(oldpos) && !beingUpdatedOldChunks.contains(oldpos) && !tickexploitChunks.contains(oldpos) && !oldChunks.contains(oldpos) && !newChunks.contains(oldpos)) {
                    oldChunks.add(oldpos);
                    if (save.get()) {
                        saveData("/OldChunkData.txt", oldpos);
                    }
                    return;
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        */

        return isNewChunk || chunkIsBeingUpdated;
    }

    private boolean liquidData(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk) {
        return true;
    }

    private boolean copperData(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk) {
        return true;
    }


}
