package com.phoenixclient.mixin.mixins;

import com.phoenixclient.mixin.MixinHooks;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements DataComponentHolder {

    @Shadow @Nullable public abstract <T> T set(DataComponentType<? super T> dataComponentType, @Nullable T object);

    @Shadow public abstract boolean isDamageableItem();

    @Shadow public abstract int getDamageValue();

    @Shadow private int count;

    @Inject(method = "getDamageValue", at = @At(value = "HEAD"), cancellable = true)
    public void getDamageValue(CallbackInfoReturnable<Integer> cir) {
        if (MixinHooks.showTrueDurability) cir.setReturnValue(getOrDefault(DataComponents.DAMAGE, 0));
    }

    @Inject(method = "setDamageValue", at = @At(value = "HEAD"), cancellable = true)
    public void setDamageValue(int i, CallbackInfo ci) {
        if (MixinHooks.showTrueDurability) {
            set(DataComponents.DAMAGE, i);
            ci.cancel();
        }
    }

    @Inject(method = "isDamaged", at = @At(value = "HEAD"), cancellable = true)
    public void isDamaged(CallbackInfoReturnable<Boolean> cir) {
        if (MixinHooks.showTrueDurability) cir.setReturnValue(isDamageableItem() && getDamageValue() != 0);
    }

    /*
    @Inject(method = "getCount", at = @At(value = "HEAD"), cancellable = true)
    public void getCount(CallbackInfoReturnable<Integer> cir) {
        if (!ItemStack.class.cast(this).getItem().equals(Items.AIR) && count == 0) cir.setReturnValue(99);
    } */

}
