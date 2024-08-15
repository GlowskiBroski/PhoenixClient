package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.KeyPressEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import static com.phoenixclient.PhoenixClient.MC;

public class TridentTravel extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of TridentTravel",
            "Timer").setModeData("Timer").setModeDescriptions("Automatically riptides every second interval while forward is held down");

    private final SettingGUI<Double> timerDelay = new SettingGUI<>(
            this,
            "Delay",
            "Delay interval between riptide",
            .8d).setSliderData(.5, 1.5, .1);

    private final SettingGUI<Boolean> alwaysRiptide = new SettingGUI<>(
            this,
            "Always Riptide",
            "Allows the player to riptide anywhere",
            false);

    private final StopWatch watch = new StopWatch();

    public TridentTravel() {
        super("TridentTravel", "Allow the player to travel easier with their trident", Category.MOTION, false, -1);
        addSettings(mode, alwaysRiptide, timerDelay);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
        //addEventSubscriber(Event.EVENT_KEY_PRESS,this::onKeyPress);
    }

    public void onPlayerUpdate(Event event) {
        MixinHooks.alwaysRiptideTrident = alwaysRiptide.get();
        switch (mode.get()) {
            case "Timer" -> {
                if (MC.options.keyUp.isDown() && MC.player.getMainHandItem().getItem().equals(Items.TRIDENT)) {
                    watch.start();
                    MC.options.keyUse.setDown(true);
                    if (watch.hasTimePassedS(timerDelay.get())) {
                        MC.options.keyUse.setDown(false);
                        watch.restart();
                    }
                }
            }
        }
    }

    @Deprecated
    public void onKeyPress(KeyPressEvent event) {
        if (MC.player == null) return;
        if (!MC.player.getMainHandItem().getItem().equals(Items.TRIDENT)) return;
        if (event.getKey() == Key.KEY_SPACE.getId() && event.getState() == 1) {
            MC.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, MC.player.getBlockPosBelowThatAffectsMyMovement(), Direction.DOWN));
            float f = EnchantmentHelper.getTridentSpinAttackStrength(MC.player.getMainHandItem(), MC.player);
            float g = MC.player.getYRot();
            float h = MC.player.getXRot();
            float k = -Mth.sin(g * ((float) Math.PI / 180)) * Mth.cos(h * ((float) Math.PI / 180));
            float l = -Mth.sin(h * ((float) Math.PI / 180));
            float m = Mth.cos(g * ((float) Math.PI / 180)) * Mth.cos(h * ((float) Math.PI / 180));
            float n = Mth.sqrt(k * k + l * l + m * m);
            MC.player.push(k *= f / n, l *= f / n, m *= f / n);
            MC.player.startAutoSpinAttack(20, 80, MC.player.getMainHandItem());
            if (MC.player.onGround()) MC.player.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));

        }
    }

    @Override
    public void onDisabled() {
        MixinHooks.alwaysRiptideTrident = false;
        watch.stop();
        MC.options.keyUse.setDown(false);
    }
}
