package com.phoenixclient.event.events;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phoenixclient.PhoenixClient.MC;

public class RenderScreenEvent extends Event {

    @Override
    public void post(Object... args) {
        super.post(args);
    }

    public Vector getMousePos() {
        int i = (int)(MC.mouseHandler.xpos() * (double)MC.getWindow().getGuiScaledWidth() / (double)MC.getWindow().getScreenWidth());
        int j = (int)(MC.mouseHandler.ypos() * (double)MC.getWindow().getGuiScaledHeight() / (double)MC.getWindow().getScreenHeight());
        return new Vector(i,j);
    }

    //TODO: Replace this with an argument
    public GuiGraphics getGraphics() {
        return new GuiGraphics(MC, MC.renderBuffers().bufferSource());
    }


}
