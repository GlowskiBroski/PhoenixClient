package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phoenixclient.PhoenixClient.MC;

public class RenderItemTooltipEvent extends Event {

    private boolean cancelled = false;

    private ItemStack item;

    private Vector mousePos;

    @Override
    public void post(Object... args) {
        this.item = (ItemStack) args[0];
        this.mousePos = new Vector((int)args[1],(int)args[2]);
        super.post(args);
    }

    public void updateCancelled(CallbackInfo ci) {
        if (isCancelled()) {
            setCancelled(false);
            ci.cancel();
        }
    }

    public ItemStack getItemStack() {
        return item;
    }

    public Vector getMousePos() {
        return mousePos;
    }

    public GuiGraphics getGraphics() {
        return new GuiGraphics(MC, MC.renderBuffers().bufferSource());
    }


    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
