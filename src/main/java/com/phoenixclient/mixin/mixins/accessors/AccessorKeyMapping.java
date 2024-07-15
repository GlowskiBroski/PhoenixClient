package com.phoenixclient.mixin.mixins.accessors;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(KeyMapping.class)
public interface AccessorKeyMapping {

    @Accessor("CATEGORY_SORT_ORDER")
    static Map<String, Integer> getCategoryOrderMap(){return null;};

}
