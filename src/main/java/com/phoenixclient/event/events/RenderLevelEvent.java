package com.phoenixclient.event.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;

public class RenderLevelEvent extends Event {

    private PoseStack matrix;
    private float partialTicks;

    @Override
    public void post(Object... args) {
        this.matrix = (PoseStack) args[0];
        this.partialTicks = (float) args[1];
        super.post(args);
    }

    public PoseStack getLevelPositionStack() {
        return matrix;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
