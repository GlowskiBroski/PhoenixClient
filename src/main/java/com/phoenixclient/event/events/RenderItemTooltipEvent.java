package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.phoenixclient.PhoenixClient.MC;

public class RenderItemTooltipEvent extends Event {

    private boolean cancelled = false;

    private GuiGraphics graphics;
    private ItemStack item;
    private List<Component> list;

    private Vector mousePos;

    @Override
    public void post(Object... args) {
        this.graphics = (GuiGraphics) args[0];
        this.item = (ItemStack) args[1];
        this.list = (List<Component>) args[2];
        this.mousePos = new Vector((int) args[3], (int) args[4]);
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

    public List<Component> getList() {
        return list;
    }

    public Vector getMousePos() {
        return mousePos;
    }

    public GuiGraphics getGraphics() {
        return graphics;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
