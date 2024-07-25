package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.setting.SettingGUI;

import static com.phoenixclient.PhoenixClient.MC;

public class FullBright extends Module {

    private final SettingGUI<Double> fadeSpeed = new SettingGUI<>(
            this,
            "Fade Speed",
            "Speed of light fading in/out",
            3d)
            .setSliderData( .1, 3, .1);

    private double gammaFade = 0;

    public FullBright() {
        super("FullBright", "Makes the world bright", Category.RENDER, false, -1);
        addSettings(fadeSpeed);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
    }

    public void onPlayerUpdate(Event event) {
        if (MC.options == null) return;
        if (gammaFade < 10d) gammaFade += .1d * fadeSpeed.get();
        MC.options.gamma().set(MathUtil.getBoundValue(gammaFade,0,10).doubleValue());
    }

    private final EventAction fadeOut = new EventAction(Event.EVENT_PLAYER_UPDATE, () -> {
        if (gammaFade > 1d) {
            gammaFade -= .1d * fadeSpeed.get();
            MC.options.gamma().set(gammaFade);
        }
    });

    @Override
    public void onEnabled() {
        gammaFade = MC.options != null ? MC.options.gamma().get() : 0;
        fadeOut.unsubscribe();
    }

    @Override
    public void onDisabled() {
        fadeOut.subscribe();
    }

}
