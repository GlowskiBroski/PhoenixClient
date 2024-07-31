package com.phoenixclient.event.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;

public class RenderDebugEvent extends Event {

    private PoseStack matrix;
    private MultiBufferSource.BufferSource bufferSource;
    private Camera camera;
    private float partialTicks;

    @Override
    public void post(Object... args) {
        this.matrix = (PoseStack) args[0];
        this.bufferSource = (MultiBufferSource.BufferSource) args[1];
        this.camera = (Camera) args[2];
        this.partialTicks = (float) args[3];
        super.post(args);
    }

    public PoseStack getLevelPoseStack() {
        return matrix;
    }

    public MultiBufferSource.BufferSource getBufferSource() {
        return bufferSource;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
