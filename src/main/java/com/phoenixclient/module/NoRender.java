package com.phoenixclient.module;

import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.event.Event;
import com.phoenixclient.util.setting.SettingGUI;

import static com.phoenixclient.PhoenixClient.MC;

//TODO:
// NoPumpkin
// No Vignette
// No Totem Animation
// No Potion Overlay
// No Weather
// Blindness - Maybe separate into AntiPotion
// No Warden Fog
// No Pumpkin Overlay

public class NoRender extends Module {

    public final SettingGUI<Boolean> noBob = new SettingGUI<>(
            this,
            "No Bob",
            "Stops bobbing while walking",
            false);

    private final SettingGUI<Boolean> noHurtCam = new SettingGUI<>(
            this,
            "No Hurt Cam",
            "Stops camera tilting on hurt",
            false);

    private final SettingGUI<Boolean> noConfusion = new SettingGUI<>(
            this,
            "No Confusion",
            "Stops the confusion effect",
            false);

    private final SettingGUI<Boolean> noFireOverlay = new SettingGUI<>(
            this,
            "No Fire",
            "Stops the Fire Overlay",
            false);

    private final SettingGUI<Boolean> noSuffocationHud = new SettingGUI<>(
            this,
            "No Suffocation Hud",
            "Removes the block overlay",
            false);

    private final SettingGUI<Boolean> noCaveCulling = new SettingGUI<>(
            this,
            "No Cave Culling",
            "Stops culling to allow for view of all caves in loaded chunks",
            false);

    private final SettingGUI<Boolean> noFog = new SettingGUI<>(
            this,
            "No Fog",
            "Stops all fog from being rendered",
            false);

    private final SettingGUI<Boolean> noTips = new SettingGUI<>(
            this,
            "No Tips Window",
            "Removes the \"Chat Message Verification\" and \"Mine Wood\" notifications",
            true);

    private final SettingGUI<Boolean> noBossHealth = new SettingGUI<>(
            this,
            "No Boss Health",
            "Removes the boss health bars",
            false);

    public NoRender() {
        super("NoRender", "Disables rendering of certain things", Category.RENDER, true, -1);
        addSettings(noBob, noHurtCam, noConfusion, noFireOverlay, noSuffocationHud, noCaveCulling, noFog,noTips,noBossHealth);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (noBob.get()) {
            MixinHooks.noCameraBob = true;
            noBob.runOnChange(() -> {});
        } else {
            noBob.runOnChange(() -> MixinHooks.noCameraBob = noBob.get());
        }
        if (noHurtCam.get()) {
            MixinHooks.noHurtCam = true;
            noHurtCam.runOnChange(() -> {});
        } else {
            noHurtCam.runOnChange(() -> MixinHooks.noHurtCam = noHurtCam.get());
        }
        if (noConfusion.get()) {
            MC.player.spinningEffectIntensity = 0;
            MC.player.oSpinningEffectIntensity = 0;
        }
        if (noFireOverlay.get()) {
            MixinHooks.noFireHud = true;
            noFireOverlay.runOnChange(() -> {});
        } else {
            noFireOverlay.runOnChange(() -> MixinHooks.noFireHud = noFireOverlay.get());
        }
        if (noSuffocationHud.get()) {
            MixinHooks.noSuffocationHud = true;
            noSuffocationHud.runOnChange(() -> {});
        } else {
            noSuffocationHud.runOnChange(() -> MixinHooks.noSuffocationHud = noSuffocationHud.get());
        }
        if (noCaveCulling.get()) {
            MixinHooks.noCaveCulling = true;
            noCaveCulling.runOnChange(() -> {});
        } else {
            noCaveCulling.runOnChange(() -> MixinHooks.noCaveCulling = noCaveCulling.get());
        }
        if (noFog.get()) {
            MixinHooks.noFog = true;
            noFog.runOnChange(() -> {});
        } else {
            noFog.runOnChange(() -> MixinHooks.noFog = noFog.get());
        }
        if (noTips.get()) {
            MixinHooks.noTipsWindow = true;
            noTips.runOnChange(() -> {});
        } else {
            noTips.runOnChange(() -> MixinHooks.noTipsWindow = noTips.get());
        }
        if (noBossHealth.get()) {
            MixinHooks.noBossHealth = true;
            noBossHealth.runOnChange(() -> {});
        } else {
            noBossHealth.runOnChange(() -> MixinHooks.noBossHealth = noBossHealth.get());
        }
    }

    @Override
    public void onEnabled() {
        for (SettingGUI<?> setting : getSettings()) setting.getChangeDetector().reset();
    }

    @Override
    public void onDisabled() {
        MixinHooks.noCameraBob = false;
        MixinHooks.noHurtCam = false;
        MixinHooks.noFireHud = false;
        MixinHooks.noSuffocationHud = false;
        MixinHooks.noCaveCulling = false;
        MixinHooks.noFog = false;
        MixinHooks.noTipsWindow = false;
        MixinHooks.noBossHealth = false;
    }

    //TODO: Implement this, find a way to get around variable local assignment. Maybe use a container
    private void runSettingChange(SettingGUI<Boolean> setting) {
        if (setting.get()) {
            MixinHooks.noTipsWindow = true;
            setting.runOnChange(() -> {});
        } else {
            setting.runOnChange(() -> MixinHooks.noTipsWindow = setting.get());
        }
    }
}
