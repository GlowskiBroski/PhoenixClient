package com.phoenixclient.mixin.mixins;

import com.phoenixclient.gui.GuiManager;
import com.phoenixclient.gui.hud.element.ListWindow;
import com.phoenixclient.mixin.mixins.accessors.AccessorKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.GameType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(Options.class)
public abstract class MixinOptions {

    @Shadow @Final @Mutable public KeyMapping[] keyMappings;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;keyMappings:[Lnet/minecraft/client/KeyMapping;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void onInitAfterKeysAll(Minecraft minecraft, File file, CallbackInfo ci) {
        Map<String, Integer> categories = AccessorKeyMapping.getCategoryOrderMap();

        int index = 0;
        for (Integer i : categories.values()) if (i > index) index = i;
        index++;
        categories.put("Phoenix Client", index);

        ArrayList<KeyMapping> newMappings = new ArrayList<>();
        newMappings.addAll(Arrays.asList(keyMappings));
        newMappings.add(GuiManager.MODULE_KEY_MAPPING);
        newMappings.add(GuiManager.HUD_KEY_MAPPING);
        keyMappings = newMappings.toArray(new KeyMapping[0]);
    }
}
