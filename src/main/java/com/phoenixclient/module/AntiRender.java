package com.phoenixclient.module;

import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.event.Event;
import com.phoenixclient.util.setting.SettingGUI;

import static com.phoenixclient.PhoenixClient.MC;

public class AntiRender extends Module {

    private final SettingGUI<Boolean> noBob = new SettingGUI<>(
            this,
            "No Bob",
            "Stops bobbing while walking",
            true);

    private final SettingGUI<Boolean> noHurtCam = new SettingGUI<>(
            this,
            "No Hurt Cam",
            "Stops camera tilting on hurt",
            true);

    private final SettingGUI<Boolean> noConfusion = new SettingGUI<>(
            this,
            "No Confusion",
            "Stops the confusion effect - Currently Unimplemented",
            true);

    private final SettingGUI<Boolean> noFireOverlay = new SettingGUI<>(
            this,
            "No Fire",
            "Stops the Fire Overlay",
            true);

    private final SettingGUI<Boolean> noSuffocationHud = new SettingGUI<>(
            this,
            "No Suffocation Hud",
            "Removes the block overlay",
            true);

    private final SettingGUI<Boolean> noCaveCulling = new SettingGUI<>(
            this,
            "No Cave Culling",
            "Stops culling to allow for view when inside blocks - WARNING: Laggy",
            false);

    public AntiRender() {
        super("AntiRender", "Disables rendering of certain things", Category.RENDER, false, -1);
        addSettings(noBob, noHurtCam, noConfusion, noFireOverlay, noSuffocationHud, noCaveCulling);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (noBob.get()) {
            MixinHooks.noCameraBob = true;
        } else {
            noBob.runOnChange(() -> MixinHooks.noCameraBob = noBob.get());
        }
        if (noHurtCam.get()) {
            MixinHooks.noHurtCam = true;
        } else {
            noHurtCam.runOnChange(() -> MixinHooks.noHurtCam = noHurtCam.get());
        }
        if (noConfusion.get()) {
            MixinHooks.noConfusion = true;
        } else {
            noConfusion.runOnChange(() -> MixinHooks.noConfusion = noConfusion.get()); //TODO: Implement noConfusion
        }
        if (noFireOverlay.get()) {
            MixinHooks.noFireHud = true;
        } else {
            noFireOverlay.runOnChange(() -> MixinHooks.noFireHud = noFireOverlay.get());
        }
        if (noSuffocationHud.get()) {
            MixinHooks.noSuffocationHud = true;
        } else {
            noSuffocationHud.runOnChange(() -> MixinHooks.noSuffocationHud = noSuffocationHud.get());
        }
        if (noCaveCulling.get()) {
            MC.smartCull = false;
        } else {
            noCaveCulling.runOnChange(() -> MC.smartCull = !noCaveCulling.get());
        }
    }

    @Override
    public void onEnabled() {
        for (SettingGUI<?> setting : getSettings()) setting.resetOnChange();
    }

    @Override
    public void onDisabled() {
        MixinHooks.noCameraBob = false;
        MixinHooks.noHurtCam = false;
        MixinHooks.noConfusion = false;
        MixinHooks.noFireHud = false;
        MixinHooks.noSuffocationHud = false;
        MC.smartCull = true;
    }

}
