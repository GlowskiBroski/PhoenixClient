package com.phoenixclient.mixin.mixins;

import com.phoenixclient.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MixinMouseHandler {

    @Shadow private double xpos;

    @Shadow private double ypos;

    @Inject(method = "onPress", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V"), cancellable = true)
    private void onPressGame(long l, int i, int j, int k, CallbackInfo ci) {
        double x = this.xpos * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth();
        double y = this.ypos * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight();
        Event.EVENT_MOUSE_CLICK.post(x,y,i,j);
    }


    // THIS IS FOR INSIDE OF MENU SCREENS ONLY
    @Inject(method = "onPress", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"), cancellable = true)
    private void onPressMenu(long l, int i, int j, int k, CallbackInfo ci) {
        double x = this.xpos * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth();
        double y = this.ypos * (double)Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight();
        Event.EVENT_MOUSE_CLICK.post(x,y,i,j);
    }
}

