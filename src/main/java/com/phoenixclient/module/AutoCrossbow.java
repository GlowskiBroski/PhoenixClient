package com.phoenixclient.module;

import com.mojang.blaze3d.vertex.PoseStack;
import com.phoenixclient.event.Event;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.render.Draw3DUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoCrossbow extends Module {

    //TODO: Rename this to AutoBow and allow it to work with a normal bow

    private final SettingGUI<Double> delay = new SettingGUI<>(
            this,
            "Delay",
            "Amount of time before releasing",
            1d).setSliderData(0, 10, .1);

    private final StopWatch watch = new StopWatch();

    public AutoCrossbow() {
        super("AutoCrossbow", "Automatically shoots a crossbow when right click is held", Category.COMBAT, false, -1);
        addSettings(delay);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onUpdate);
    }

    public void onUpdate(Event event) {
        watch.start();
        if (MC.player.getMainHandItem().getItem() instanceof CrossbowItem i) {
            if (watch.hasTimePassedS(delay.get())) {
                MC.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, MC.player.getBlockPosBelowThatAffectsMyMovement(), Direction.DOWN, 0));
                i.releaseUsing(MC.player.getMainHandItem(), MC.level, MC.player, 0);
                watch.restart();
            }
        } else {
            watch.restart();
        }
    }

}
