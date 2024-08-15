package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.phoenixclient.PhoenixClient.MC;

public class SafeWalk extends Module {

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of SafeWalk",
            "Legit").setModeData("Legit");


    public SafeWalk() {
        super("SafeWalk", "Stops you from walking off of the edges of blocks", Category.MOTION, false, -1);
        addSettings(mode);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
    }

    private final DoOnce doOnce = new DoOnce();

    public void onPlayerUpdate(Event event) {
        Block belowBlock = MC.level.getBlockState(BlockPos.containing(MC.player.position()).below()).getBlock();
        switch (mode.get()) {
            case "Legit" -> {
                if ((belowBlock.equals(Blocks.AIR) || belowBlock.equals(Blocks.WATER)) && MC.player.onGround()) {
                    MC.options.keyShift.setDown(true);
                    doOnce.reset();
                } else {
                    doOnce.run(() -> MC.options.keyShift.setDown(false));
                }
            }
            case "IdkSomethingElseIDidntAddYet"-> {}
        }
    }

}

