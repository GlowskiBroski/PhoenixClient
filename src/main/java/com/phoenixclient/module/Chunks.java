package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.gui.hud.element.ListWindow;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.EmptyLevelChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.phoenixclient.PhoenixClient.MC;
import static net.minecraft.util.Mth.clamp;

//TODO: Have a CSV file save/load the data of each chunk so you know whether or not a chunk was new, even after restarting the client... forever

public class Chunks extends Module {

    public Chunks() {
        super("Chunks", "Send information of chunks upon load", Category.SERVER, false, -1);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }

    public void onPacket(PacketEvent event) {
        if (MC.level == null) return;
        if (event.getPacket() instanceof ClientboundLevelChunkWithLightPacket p) { //This is the only packet sent to the client with chunk data
            ChunkPos pos = new ChunkPos(p.getX(),p.getZ());
            LevelChunk chunk = new LevelChunk(MC.level,pos);
            chunk.replaceWithPacketData(p.getChunkData().getReadBuffer(),p.getChunkData().getHeightmaps(),p.getChunkData().getBlockEntitiesTagsConsumer(p.getX(),p.getZ()));
            //System.out.println("--------------");
            //System.out.println("ChunkPos: " + pos);
            //System.out.println(chunk.getStatus());
        }
    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {
    }
}
