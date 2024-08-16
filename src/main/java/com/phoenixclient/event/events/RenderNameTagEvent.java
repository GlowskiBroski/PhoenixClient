package com.phoenixclient.event.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class RenderNameTagEvent extends CancellableEvent {

    private Entity entity;
    private Component component;

    @Override
    public void post(Object... args) {
        this.entity = (Entity) args[0];
        this.component = (Component) args[1];
        super.post(args);
    }

    public Entity getEntity() {
        return entity;
    }

    public Component getComponent() {
        return component;
    }
}
