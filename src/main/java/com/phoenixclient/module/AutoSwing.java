package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.actions.StopWatch;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Random;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoSwing extends Module {

    private final SettingGUI<Integer> lowerBound = new SettingGUI<>(
            this,
            "Lower Bound",
            "Lowest possible click speed",
            15)
            .setSliderData( 10, 17, 1);

    private final SettingGUI<Integer> upperBound = new SettingGUI<>(
            this,
            "Upper Bound",
            "Highest possible click speed",
            20)
            .setSliderData( 17, 23, 1);

    private final SettingGUI<Boolean> accelerate = new SettingGUI<>(
            this,
            "Accelerate",
            "Accelerates up to the click speed",
            true);


    private final Random random = new Random();
    private final StopWatch watch = new StopWatch();
    private int accelerationStage = 0;

    public AutoSwing() {
        super("AutoSwing", "Swings at a varied rate while holding the mouse button", Category.COMBAT, false, -1);
        addSettings(lowerBound, upperBound, accelerate);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    @Override
    public String getModTag() {
        return "L: " + lowerBound.get() + ", R: " + upperBound.get();
    }

    public void onPlayerUpdate(Event event) {
        if (MC.options.keyAttack.isDown()) {
            int lowerCPS = lowerBound.get();
            int upperCPS = upperBound.get();

            int cps = random.nextInt(lowerCPS, upperCPS + 1);

            if (accelerate.get() && accelerationStage / lowerCPS < lowerCPS) {
                accelerationStage += 26;
                cps = accelerationStage / lowerCPS;
            }
            watch.run((int)((double) 1000/cps), this::startAttack);
        } else {
            accelerationStage = 0;
        }
    }

    //Stolen from Minecraft.class
    private boolean startAttack() {
        if (MC.hitResult == null) return false;
        if (MC.player.isHandsBusy()) return false;
        ItemStack itemStack = MC.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemStack.isItemEnabled(MC.level.enabledFeatures())) {
            return false;
        }
        boolean bl = false;
        switch (MC.hitResult.getType()) {
            case ENTITY: {
                MC.gameMode.attack(MC.player, ((EntityHitResult) MC.hitResult).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult blockHitResult = (BlockHitResult) MC.hitResult;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!MC.level.getBlockState(blockPos).isAir()) {
                    MC.gameMode.startDestroyBlock(blockPos, blockHitResult.getDirection());
                    if (!MC.level.getBlockState(blockPos).isAir()) break;
                    bl = true;
                    break;
                }
            }
            case MISS: {
                MC.player.resetAttackStrengthTicker();
            }
        }
        MC.player.swing(InteractionHand.MAIN_HAND);
        return bl;
    }

}
