package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.KeyPressEvent;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.MotionUtil;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

public class FreeCam extends Module {

    //TODO: Smart cull doesnt seem to be working on interact mode?

    private final OnChange<Vector> onChangeView = new OnChange<>();
    private final OnChange<Vector> onChangeSpoofedView = new OnChange<>();
    public AbstractClientPlayer dummyPlayer;
    private GameType gameMode;
    private ServerboundMovePlayerPacket interactRotationPacket = null;

    private final SettingGUI<String> mode = new SettingGUI<>(
            this,
            "Mode",
            "Mode of FreeCam",
            "Interact")
            .setModeData("Ghost", "Interact");

    private final SettingGUI<Double> speed = new SettingGUI<>(
            this,
            "Speed",
            "Flight Speed of FreeCam",
            1d)
            .setSliderData(.1, 2, .1);

    public FreeCam() {
        super("FreeCam", "Allows the camera to move out of the body (press <- -> to cycle through nearby players)", Category.PLAYER, false, -1);
        addSettings(mode, speed);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE, this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_PACKET, this::onPacket);
        addEventSubscriber(Event.EVENT_KEY_PRESS, this::onKeyPress);
    }

    public void onPlayerUpdate(Event event) {
        if (dummyPlayer != null) {
            updateDummyPlayerInventory();
            switch (mode.get()) {
                case "Ghost" -> {
                    MotionUtil.moveEntityStrafe(speed.get() + .052, MC.player);
                    Vector deltaM = new Vector(MC.player.getDeltaMovement());
                    MC.player.setDeltaMovement(deltaM.getX(), 0, deltaM.getZ());
                    if (MC.options.keyJump.isDown())
                        MC.player.setDeltaMovement(deltaM.getX(), speed.get(), deltaM.getZ());
                    if (MC.options.keyShift.isDown())
                        MC.player.setDeltaMovement(deltaM.getX(), -speed.get(), deltaM.getZ());
                    if (!MotionUtil.isInputActive(true)) MC.player.setDeltaMovement(0, 0, 0);
                }
                case "Interact" -> {
                    MixinHooks.noClip = true;
                    MC.player.getAbilities().flying = true;
                    MotionUtil.moveEntityStrafe(speed.get() + .052, MC.player);
                    Vector deltaM = new Vector(MC.player.getDeltaMovement());
                    MC.player.setDeltaMovement(deltaM.getX(), 0, deltaM.getZ());
                    if (MC.options.keyJump.isDown())
                        MC.player.setDeltaMovement(deltaM.getX(), speed.get(), deltaM.getZ());
                    if (MC.options.keyShift.isDown())
                        MC.player.setDeltaMovement(deltaM.getX(), -speed.get(), deltaM.getZ());
                    if (!MotionUtil.isInputActive(true)) MC.player.setDeltaMovement(0, 0, 0);
                    updateDummyPlayerSwinging();
                    updateDummyPlayerLookVector();
                }
            }
        }
        mode.runOnChange(this::disable);
    }

    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof ServerboundMovePlayerPacket.Rot) {
            if (!packet.equals(interactRotationPacket)) event.setCancelled(true);
        }

        if (packet instanceof ClientboundPlayerPositionPacket c) {
            if (dummyPlayer != null) dummyPlayer.setPos(new Vector(c.getX(), c.getY(), c.getZ()).getVec3());
            event.setCancelled(true);
        }
        if (packet instanceof ServerboundMovePlayerPacket.PosRot
                || packet instanceof ServerboundMovePlayerPacket.Pos
                || packet instanceof ServerboundAcceptTeleportationPacket
                || packet instanceof ServerboundPlayerInputPacket
                || packet instanceof ClientboundPlayerLookAtPacket
        ) {
            event.setCancelled(true);
        }
    }

    //Cycle through nearby players
    private int specPlayerIndex = 0;
    public void onKeyPress(KeyPressEvent event) {
        if (event.getKey() != GLFW.GLFW_KEY_LEFT && event.getKey() != GLFW.GLFW_KEY_RIGHT) return;
        if (event.getState() != 1) return;

        ArrayList<Player> playerList = new ArrayList<>();
        for (Entity entity : MC.level.entitiesForRendering()) {
            if (entity instanceof Player p && !p.equals(MC.player)) playerList.add(p);
        }
        playerList.sort(new PlayerNameComparator());

        switch (event.getKey()) {
            case GLFW.GLFW_KEY_LEFT -> {
                specPlayerIndex++;
                if (specPlayerIndex >= playerList.size()) {
                    specPlayerIndex = 0;
                }
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                specPlayerIndex--;
                if (specPlayerIndex < 0) {
                    specPlayerIndex = playerList.size() - 1;
                }
            }
        }
        Player specPlayer = playerList.get(specPlayerIndex);
        MC.player.setPos(specPlayer.getPosition(0));
        MC.player.setYHeadRot(specPlayer.getYHeadRot());
        MC.player.setYRot(specPlayer.getYRot());
        MC.player.setXRot(specPlayer.getXRot());
    }


    @Override
    public void onEnabled() {
        //Disable the mod if there is no player
        if (updateDisableOnEnabled()) return;

        //Set Mode Change Detector off to reset detector
        mode.runOnChange(() -> {});

        //Log Original Game Mode
        gameMode = MC.gameMode.getPlayerMode();

        //Set Mode Data
        switch (mode.get()) {
            case "Ghost" -> {
                MC.gameMode.setLocalMode(GameType.SPECTATOR);
                MC.player.onGameModeChanged(GameType.SPECTATOR);
                //Make it so you cannot attack entities here
            }
            case "Interact" -> {
                //TODO: Find a way to make block mining faster while flying
                MC.smartCull = false;
                MixinHooks.noClip = true;
                MixinHooks.noSuffocationHud = true;
            }
        }

        summonDummyPlayer();
    }

    @Override
    public void onDisabled() {
        if (MC.player == null || dummyPlayer == null) return;

        dummyPlayer.remove(Entity.RemovalReason.KILLED);

        resetPlayerData();

        MC.smartCull = true;
        MixinHooks.noClip = false;
        MixinHooks.noSuffocationHud = false;
    }


    private void summonDummyPlayer() {
        MC.level.addEntity(dummyPlayer = new AbstractClientPlayer(MC.level, MC.player.getGameProfile()) {
            public boolean isSpectator() {
                return false;
            }

            public UUID getUUID() {
                return UUID.randomUUID();
            }

            protected PlayerInfo getPlayerInfo() {
                return MC.getConnection().getPlayerInfo(MC.player.getUUID());
            }
        });
        dummyPlayer.setPos(MC.player.getPosition(0));
        dummyPlayer.setYRot(MC.player.getYRot());
        dummyPlayer.setYBodyRot(MC.player.getYRot());
        dummyPlayer.setYHeadRot(MC.player.getYHeadRot());
        dummyPlayer.setXRot(MC.player.getXRot());
        dummyPlayer.getAbilities().flying = MC.player.getAbilities().flying;
        if (MC.player.isFallFlying()) dummyPlayer.startFallFlying();
    }

    private void updateDummyPlayerSwinging() {
        if (MC.player.swinging) {
            dummyPlayer.swing(InteractionHand.MAIN_HAND);
            dummyPlayer.attackAnim = MC.player.attackAnim;
        } else {
            dummyPlayer.attackAnim = 0;
        }
    }

    private void updateDummyPlayerInventory() {
        dummyPlayer.setItemSlot(EquipmentSlot.HEAD, MC.player.getItemBySlot(EquipmentSlot.HEAD));
        dummyPlayer.setItemSlot(EquipmentSlot.CHEST, MC.player.getItemBySlot(EquipmentSlot.CHEST));
        dummyPlayer.setItemSlot(EquipmentSlot.LEGS, MC.player.getItemBySlot(EquipmentSlot.LEGS));
        dummyPlayer.setItemSlot(EquipmentSlot.FEET, MC.player.getItemBySlot(EquipmentSlot.FEET));
        dummyPlayer.setItemSlot(EquipmentSlot.MAINHAND, MC.player.getItemBySlot(EquipmentSlot.MAINHAND));
        dummyPlayer.setItemSlot(EquipmentSlot.OFFHAND, MC.player.getItemBySlot(EquipmentSlot.OFFHAND));
        dummyPlayer.calculateEntityAnimation(true);
    }

    private void updateDummyPlayerLookVector() {
        Vector lookVec = new Vector(MC.hitResult.getLocation()).getSubtracted(new Vector(dummyPlayer.getEyePosition()));
        float y = (float) lookVec.getYaw().getDegrees();
        float p = (float) lookVec.getPitch().getDegrees();
        if (!Float.isNaN(y) && !Float.isNaN(p) && !PhoenixClient.getRotationManager().isSpoofing()) {
            dummyPlayer.setYHeadRot(y);
            dummyPlayer.setYRot(y);
            dummyPlayer.setXRot(p);

            //After time, the server realizes we are "AFK", then the rotation packets are no longer accepted
            onChangeView.run(lookVec, () -> MC.getConnection().send(interactRotationPacket = new ServerboundMovePlayerPacket.Rot(y, p, true)));
        } else {
            float yS = PhoenixClient.getRotationManager().getSpoofedYaw();
            float pS = PhoenixClient.getRotationManager().getSpoofedPitch();
            dummyPlayer.setYHeadRot(yS);
            dummyPlayer.setYRot(yS);
            dummyPlayer.setXRot(pS);

            //It doesn't really matter what the angles are that I send here, as they are overwritten by the rotation manager
            onChangeSpoofedView.run(new Vector(yS, pS), () -> MC.getConnection().send(interactRotationPacket = new ServerboundMovePlayerPacket.Rot(yS, pS, true)));
        }
    }

    private void resetPlayerData() {
        MC.player.setPos(dummyPlayer.getPosition(0));
        MC.player.setYRot(dummyPlayer.getYRot());
        MC.player.setYHeadRot(dummyPlayer.getYHeadRot());
        MC.player.setXRot(dummyPlayer.getXRot());
        if (dummyPlayer.isFallFlying()) MC.player.startFallFlying();
        MC.player.getAbilities().flying = dummyPlayer.getAbilities().flying;
        MC.player.onUpdateAbilities();

        MC.player.setDeltaMovement(0, 0, 0);

        MC.gameMode.setLocalMode(gameMode);
        MC.player.onGameModeChanged(gameMode);
    }

    public class PlayerNameComparator implements Comparator<Player> {

        @Override
        public int compare(Player o1, Player o2) {
            return o1.getDisplayName().getString().compareTo(o2.getDisplayName().getString());
        }
    }
}
