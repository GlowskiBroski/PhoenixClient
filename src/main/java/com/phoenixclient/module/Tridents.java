package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.KeyPressEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

import static com.phoenixclient.PhoenixClient.MC;

public class Tridents extends Module {


    public Tridents() {
        super("Tridents", "Allows the player to always be able to riptide your trident", Category.MOTION, false, -1);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_KEY_PRESS,this::onKeyPress);
    }

    public void onPlayerUpdate(Event event) {
        MixinHooks.alwaysRiptideTrident = true;
    }

    public void onKeyPress(KeyPressEvent event) {
        if (MC.player == null) return;
        if (!MC.player.getMainHandItem().getItem().equals(Items.TRIDENT)) return;
        if (event.getKey() == Key.KEY_SPACE.getId() && event.getState() == 1) {
            MC.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM,MC.player.getBlockPosBelowThatAffectsMyMovement(), Direction.DOWN));
            float f = EnchantmentHelper.getTridentSpinAttackStrength(MC.player.getMainHandItem(), MC.player);
            float g = MC.player.getYRot();
            float h = MC.player.getXRot();
            float k = -Mth.sin(g * ((float)Math.PI / 180)) * Mth.cos(h * ((float)Math.PI / 180));
            float l = -Mth.sin(h * ((float)Math.PI / 180));
            float m = Mth.cos(g * ((float)Math.PI / 180)) * Mth.cos(h * ((float)Math.PI / 180));
            float n = Mth.sqrt(k * k + l * l + m * m);
            MC.player.push(k *= f / n, l *= f / n, m *= f / n);
            MC.player.startAutoSpinAttack(20,80,MC.player.getMainHandItem());
            if (MC.player.onGround()) MC.player.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));

        }
    }

    @Override
    public void onDisabled() {
        MixinHooks.alwaysRiptideTrident = false;
    }
}
