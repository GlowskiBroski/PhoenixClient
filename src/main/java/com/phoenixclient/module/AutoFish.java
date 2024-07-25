package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.actions.StopWatch;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;

import static com.phoenixclient.PhoenixClient.MC;

public class AutoFish extends Module {

    private final SettingGUI<Boolean> autoRecast = new SettingGUI<>(
            this,
            "Auto Recast",
            "Automatically recasts the rod after 15 seconds",
            false);

    private boolean shouldRecast = false;
    private final StopWatch autoFishTimer = new StopWatch();

    public AutoFish() {
        super("AutoFish", "Automatically catches fish for you", Category.PLAYER,false, -1);
        addSettings(autoRecast);
        addEventSubscriber(Event.EVENT_PACKET, this::onPacket);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
    }

    //TODO: Make sure to detect if its YOUR splash packet to avoid reeling in another persons rod
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof ClientboundSoundPacket soundPacket) {
            if (soundPacket.getSound().value().equals(SoundEvents.FISHING_BOBBER_SPLASH) && !shouldRecast) {
                autoFishTimer.start();
                autoFishTimer.restart();
                shouldRecast = true;
                clickRod();
            }
        }
    }

    public void onPlayerUpdate(Event event) {
        if (autoFishTimer.hasTimePassedS(1) && shouldRecast) {
            shouldRecast = false;
            clickRod();
        }

        if (autoRecast.get() && autoFishTimer.hasTimePassedS(15)) {
            clickRod();
        }
    }

    private void clickRod() {
        float yaw = MC.player.getYRot();
        float pitch = MC.player.getXRot();
        FreeCam fc = (FreeCam) PhoenixClient.getModule("FreeCam");
        if (fc.isEnabled() && fc.dummyPlayer != null) {
            yaw = fc.dummyPlayer.getYRot();
            pitch = fc.dummyPlayer.getXRot();
        }
        MC.getConnection().send(new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0,yaw,pitch)); //Retrieve
    }

}
