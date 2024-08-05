package com.phoenixclient.gui.hud.element;

import com.mojang.blaze3d.platform.NativeImage;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.file.CSVFile;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.Container;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.FluidState;

import java.awt.*;
import java.util.*;

import static com.phoenixclient.PhoenixClient.MC;

public class ChunkTrailsWindow extends GuiWindow {

    private final ResourceLocation chunkTrailResourceLocation = ResourceLocation.fromNamespaceAndPath("phoenixclient", "pc_chunktrailtexture");
    private DynamicTexture chunkTrailTexture = null;
    private DynamicTexture prevChunkTrailTexture = null;

    public HashMap<Vector, Boolean> loadedChunksMap = new HashMap<>(); //Key: ChunkPos, Value isNewChunk -- Save/Load this data as a CSV every time the client is loaded

    //TODO: Make these server dependent
    private final CSVFile paletteFileOW = new CSVFile("PhoenixClient/chunks", "newChunksPaletteOverworld.csv");
    private final CSVFile paletteFileNE = new CSVFile("PhoenixClient/chunks", "newChunksPaletteNether.csv");
    private final CSVFile paletteFileEN = new CSVFile("PhoenixClient/chunks", "newChunksPaletteEnd.csv");

    private final CSVFile liquidFileOW = new CSVFile("PhoenixClient/chunks", "newChunksLiquidOverworld.csv");
    private final CSVFile liquidFileNE = new CSVFile("PhoenixClient/chunks", "newChunksLiquidNether.csv");
    private final CSVFile liquidFileEN = new CSVFile("PhoenixClient/chunks", "newChunksLiquidEnd.csv");

    private final CSVFile copperFileOW = new CSVFile("PhoenixClient/chunks", "newChunksCopperOverworld.csv");
    private final CSVFile copperFileNE = new CSVFile("PhoenixClient/chunks", "newChunksCopperNether.csv");
    private final CSVFile copperFileEN = new CSVFile("PhoenixClient/chunks", "newChunksCopperEnd.csv");

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Type of algorithm for new chunk detection",
            "Palette").setModeData("Palette", "Liquid", "Copper");

    public final SettingGUI<Integer> windowSize = new SettingGUI<>(
            this,
            "Radar Size",
            "Window size/Range of the radar. Larger = more lag",
            64).setSliderData(32, 256, 16);

    public final SettingGUI<Double> scale = new SettingGUI<>(
            this,
            "Chunk Scale",
            "Size of chunks in pixel (2 is 1 chunk / 4 pixels, .2 is 5 chunks/pixel). Smaller = more lag",
            1d).setSliderData(.2, 2, .1);

    public final SettingGUI<Integer> updateDelay = new SettingGUI<>(
            this,
            "Update Delay",
            "Image update delay in milliseconds",
            750).setSliderData(250, 3000, 250);

    public final SettingGUI<Boolean> multiThreadUpdates = new SettingGUI<>(
            this,
            "MultiThread Updates",
            "Places image updating into a separate thread. GREATLY reduces lag. This may destroy your PC, only use this if you know you can!!",
            false);

    private final OnChange<ResourceKey<Level>> onDimensionChange = new OnChange<>();
    private final DoOnce init = new DoOnce();

    private final StopWatch chunkTrailUpdateWatch = new StopWatch();
    private final StopWatch chunkTrailSaveWatch = new StopWatch();

    public ChunkTrailsWindow(Screen screen, Vector pos) {
        super(screen, "ChunkTrailsWindow", pos, new Vector(65, 65));
        addSettings(mode, windowSize, scale, updateDelay, multiThreadUpdates);
        new EventAction(Event.EVENT_PLAYER_UPDATE, () -> onUpdate(Event.EVENT_PLAYER_UPDATE)).subscribe();
        new EventAction(Event.EVENT_PACKET, () -> onPacket(Event.EVENT_PACKET)).subscribe();
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        int size = windowSize.get();
        setSize(new Vector(size + 1, size + 1));

        //Render Chunk Trail Texture.
        chunkTrailUpdateWatch.start();
        if (chunkTrailUpdateWatch.hasTimePassedMS(updateDelay.get())) {
            if (multiThreadUpdates.get()) {
                Thread updateChunkTrailThread = new Thread(() -> chunkTrailTexture = getUpdatedChunkTrailTexture());
                updateChunkTrailThread.setDaemon(true);
                updateChunkTrailThread.start();
            } else {
                chunkTrailTexture = getUpdatedChunkTrailTexture();
            }
            chunkTrailUpdateWatch.restart();
        }

        if (prevChunkTrailTexture != null) MC.getTextureManager().register(chunkTrailResourceLocation, prevChunkTrailTexture);
        DrawUtil.drawTexturedRect(graphics, chunkTrailResourceLocation, getPos(), getSize());

        //Draw Crosshair
        Vector center = getPos().getAdded(size / 2, size / 2);
        DrawUtil.drawRectangle(graphics, center, new Vector(1, 1), Color.BLUE);
        DrawUtil.drawRectangle(graphics, center.getAdded(-1,0), new Vector(1, 1), Color.BLUE);
        DrawUtil.drawRectangle(graphics, center.getAdded(0,1), new Vector(1, 1), Color.BLUE);
        DrawUtil.drawRectangle(graphics, center.getAdded(1,0), new Vector(1, 1), Color.BLUE);
        DrawUtil.drawRectangle(graphics, center.getAdded(0,-1), new Vector(1, 1), Color.BLUE);

        //Draw Directions
        TextBuilder.start("N",getPos().getAdded(getSize().getX() / 2 - DrawUtil.getFontTextWidth("N",.5) / 2,2).multiply(1/.5),Color.WHITE).defaultFont().scale(.5f).draw(graphics);
        TextBuilder.start("W",getPos().getAdded(2,getSize().getY() / 2 - DrawUtil.getDefaultTextHeight(.5) / 2).multiply(1/.5),Color.WHITE).defaultFont().scale(.5f).draw(graphics);
        TextBuilder.start("S",getPos().getAdded(getSize().getX() / 2 - DrawUtil.getFontTextWidth("S",.5) / 2,getSize().getY() - DrawUtil.getFontTextHeight(.5) - 1).multiply(1/.5),Color.WHITE).defaultFont().scale(.5f).draw(graphics);
        TextBuilder.start("E",getPos().getAdded(getSize().getX() - DrawUtil.getFontTextWidth("E",.5) - 2,getSize().getY() / 2 - DrawUtil.getDefaultTextHeight(.5) / 2).multiply(1/.5),Color.WHITE).defaultFont().scale(.5f).draw(graphics);

        prevChunkTrailTexture = chunkTrailTexture;
    }

    public void onPacket(PacketEvent event) {
        if (!isPinned()) return;
        if (MC.level == null) return;
        if (MC.hasSingleplayerServer()) return;
        if (event.getPacket() instanceof ClientboundLevelChunkWithLightPacket p) { //This is the only packet sent to the client with chunk data
            ChunkPos pos = new ChunkPos(p.getX(), p.getZ());
            LevelChunk chunk = new LevelChunk(MC.level, pos);
            chunk.replaceWithPacketData(p.getChunkData().getReadBuffer(), p.getChunkData().getHeightmaps(), p.getChunkData().getBlockEntitiesTagsConsumer(p.getX(), p.getZ()));

            boolean isNewChunk = switch (mode.get()) {
                case "Palette" -> isNewChunkPalette(p, chunk);
                case "Liquid" -> isNewChunkLiquid(p, chunk);
                case "Copper" -> isNewChunkCopper(p, chunk);
                default -> throw new IllegalStateException("Unexpected value: " + mode.get());
            };

            Vector keyPos = new Vector(pos.x, 0, pos.z);
            loadedChunksMap.putIfAbsent(keyPos, isNewChunk);
        }
    }

    public void onUpdate(Event event) {
        if (!isPinned()) return;
        init.run(() -> {
            //Load Current File
            PhoenixClient.getNotificationManager().sendNotification("ChunkTrails: Loading " + mode.get() + " " + MC.level.dimension().location(), Color.WHITE);
            setMapFromFile(getProperFile(mode.get(), MC.level.dimension()));
            onDimensionChange.run(MC.level.dimension(), () -> {});
            mode.runOnChange(() -> {});
        });

        //Change save files when changing mode OR dimension
        mode.runOnChange(() -> {
            if (mode.getPrevious() != null) {
                //PhoenixClient.getNotificationManager().sendNotification("ChunkTrails: Saving " + mode.getPrevious() + " " + MC.level.dimension().location(), Color.WHITE);
                getProperFile(mode.getPrevious(), MC.level.dimension()).save(loadedChunksMap);
            }
            loadProperFile();
        });
        onDimensionChange.run(MC.level.dimension(), () -> {
            if (onDimensionChange.getPrevValue() != null) {
                //PhoenixClient.getNotificationManager().sendNotification("ChunkTrails: Saving " + mode.get() + " " + onDimensionChange.getPrevValue().location(), Color.WHITE);
                getProperFile(mode.get(), onDimensionChange.getPrevValue()).save(loadedChunksMap);
            }
            loadProperFile();
        });

        chunkTrailSaveWatch.start();
        if (chunkTrailSaveWatch.hasTimePassedS(30)) {
            saveProperFile();
            chunkTrailSaveWatch.restart();
        }
    }


    private DynamicTexture getUpdatedChunkTrailTexture() {
        int size = (int) (windowSize.get() / scale.get());
        NativeImage image = new NativeImage(NativeImage.Format.RGBA, size, size, false);

        Vector playerChunkPos = new Vector((int) MC.player.getX() / 16, 0, (int) MC.player.getZ() / 16);
        for (Vector vec : getSurroundingChunkPositions()) {
            Vector xzPos = vec.getSubtracted(playerChunkPos);
            Vector xyPos = xzPos.y(xzPos.getZ()).z(0);
            int x = Math.clamp((int) xyPos.getX() + size / 2,0,size - 1);
            int y = Math.clamp((int) xyPos.getY() + size / 2,0,size - 1);
            if (loadedChunksMap.containsKey(vec)) {
                boolean isNew = loadedChunksMap.get(vec);
                Color color = isNew ? Color.RED : Color.GREEN;
                int hash = color.getAlpha() << 24 | color.getBlue() << 16 | color.getGreen() << 8 | color.getRed();
                image.setPixelRGBA(x,y,hash);
            } else {
                image.setPixelRGBA(x,y,0);
            }
        }
        return new DynamicTexture(image);
    }

    private ArrayList<Vector> getSurroundingChunkPositions() {
        ArrayList<Vector> chunkList = new ArrayList<>();
        int renderDistance = (int) (windowSize.get() / 2 / scale.get());

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
            case "Palette" -> {
                if (dimension.equals(ow)) return paletteFileOW;
                if (dimension.equals(ne)) return paletteFileNE;
                if (dimension.equals(en)) return paletteFileEN;
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

    private void loadProperFile() {
        PhoenixClient.getNotificationManager().sendNotification("ChunkTrails: Loading " + mode.get() + " " + MC.level.dimension().location(), Color.WHITE);
        setMapFromFile(getProperFile(mode.get(), MC.level.dimension()));
    }

    private void saveProperFile() {
        Thread thread = new Thread(() -> {
            boolean shouldLoop;
            do {
                try {
                    getProperFile(mode.get(), MC.level.dimension()).save(loadedChunksMap);
                    shouldLoop = false;
                    System.out.println("saved!");
                } catch (ConcurrentModificationException e) {
                    //Loops if a concurrent modification happens to force the save
                    shouldLoop = true;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } while (shouldLoop);
        });
        thread.setDaemon(false);
        thread.setName("Chunk Trails Save Thread");
        thread.start();
    }

    private void setMapFromFile(CSVFile file) {
        HashMap<String, String[]> loadedMap = file.getDataAsMap();
        HashMap<Vector, Boolean> newMap = new HashMap<>();
        for (Map.Entry<String, String[]> set : loadedMap.entrySet()) {
            newMap.put(Vector.getFromString(set.getKey()), Boolean.parseBoolean(set.getValue()[0]));
        }
        loadedChunksMap = newMap;
    }

    //-----------------------------------------------------------------------

    private boolean isNewChunkLiquid(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int y = -64; y < 320; y++) {
                for (int z = 0; z < 16; z++) {
                    FluidState fluid = chunk.getFluidState(x, y, z);
                    if (!fluid.isEmpty() && !fluid.isSource()) return false;
                }
            }
        }
        return true;
    }

    private boolean isNewChunkCopper(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 112; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block.equals(Blocks.COPPER_ORE)) return true;
                }
            }
        }
        return false;
    }

    //TODO: Review this, and streamline this. it is VERY difficult to read. make less nesting...
    private boolean isNewChunkPalette(ClientboundLevelChunkWithLightPacket packet, LevelChunk chunk) {
        FriendlyByteBuf buf = packet.getChunkData().getReadBuffer();
        boolean isNewChunk = false;
        boolean chunkIsBeingUpdated = false;
        com.phoenixclient.util.setting.Container<Boolean> beingUpdatedDetector = new Container<>(false);


        if (buf.readableBytes() < 3) return false; // Ensure we have at least 3 bytes (short + byte)

        boolean firstchunkappearsnew = false;
        int loops = 0;
        int newChunkQuantifier = 0;
        int oldChunkQuantifier = 0;

        try {
            while (buf.readableBytes() > 0 && loops < 8) {
                // Chunk Section structure
                short blockCount = buf.readShort();

                // Block states Paletted Container
                if (buf.readableBytes() < 1) break;
                int blockBitsPerEntry2 = buf.readUnsignedByte();

                if (blockBitsPerEntry2 == 0) {
                    // Single valued palette
                    int singleBlockValue = buf.readVarInt();
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
                    int isNewSection = 0;
                    int isBeingUpdatedSection = 0;
                    int bstatesSize = bstates.size();
                    if (bstatesSize <= 1) bstatesSize = blockPaletteLength;
                    if (bstatesSize < blockPaletteLength) {
                        isNewSection = 2;
                        newChunkQuantifier++; //double the weight of this
                    }
                    for (int i = 0; i < blockPaletteLength; i++) {
                        int blockPaletteEntry = buf.readVarInt();
                        if (i == 0 && loops == 0 && blockPaletteEntry == 0 && MC.level.dimension() != Level.END)
                            firstchunkappearsnew = true;
                        if (i == 0 && blockPaletteEntry == 0 && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END)
                            isNewSection++;
                        if (i == 1 && (blockPaletteEntry == 80 || blockPaletteEntry == 1 || blockPaletteEntry == 9 || blockPaletteEntry == 5781) && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END)
                            isNewSection++;
                        if (i == 2 && (blockPaletteEntry == 5781 || blockPaletteEntry == 10 || blockPaletteEntry == 22318) && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END)
                            isNewSection++;
                        if (loops == 4 && blockPaletteEntry == 79 && MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END) {
                            if (!chunkIsBeingUpdated && beingUpdatedDetector.get()) chunkIsBeingUpdated = true;
                        }
                        if (blockPaletteEntry == 0 && (MC.level.dimension() == Level.NETHER || MC.level.dimension() == Level.END))
                            isBeingUpdatedSection++;
                    }
                    if (isBeingUpdatedSection >= 2) oldChunkQuantifier++;
                    if (isNewSection >= 2) newChunkQuantifier++;

                    // Data Array
                    int blockDataArrayLength = buf.readVarInt();
                    if (buf.readableBytes() >= blockDataArrayLength * 8) {
                        buf.skipBytes(blockDataArrayLength * 8);
                    } else {
                        buf.skipBytes(buf.readableBytes());
                        break;
                    }
                } else if (blockBitsPerEntry2 == 15) {
                    // Direct palette (no palette sent)
                    int blockDataArrayLength = buf.readVarInt();
                    if (buf.readableBytes() >= blockDataArrayLength * 8) {
                        buf.skipBytes(blockDataArrayLength * 8);
                    } else {
                        buf.skipBytes(buf.readableBytes());
                        break;
                    }
                } else {
                    break;
                }

                // Biomes Paletted Container
                if (buf.readableBytes() < 1) break;

                int biomeBitsPerEntry = buf.readUnsignedByte();

                if (biomeBitsPerEntry == 0) {
                    // Single valued palette
                    int singleBiomeValue = buf.readVarInt();
                    if (singleBiomeValue == 39 && MC.level.dimension() == Level.END) isNewChunk = true;
                    buf.readVarInt(); // Data Array Length (should be 0)
                } else if (biomeBitsPerEntry >= 1 && biomeBitsPerEntry <= 3) {
                    // Indirect palette
                    int biomePaletteLength = buf.readVarInt();
                    for (int i = 0; i < biomePaletteLength; i++) {
                        if (buf.readableBytes() < 1) break;
                        int biomePaletteEntry = buf.readVarInt();
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
                if (oldpercentage >= 25) chunkIsBeingUpdated = true;
            } else if (MC.level.dimension() != Level.NETHER && MC.level.dimension() != Level.END) {
                double percentage = ((double) newChunkQuantifier / loops) * 100;
                if (percentage >= 65) isNewChunk = true;
            }
        }

        if (firstchunkappearsnew) isNewChunk = true;

        return isNewChunk || chunkIsBeingUpdated;
    }

}
