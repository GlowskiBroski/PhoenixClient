package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.screens.Screen;

import java.util.LinkedHashMap;

public class EffectsWindow extends ListWindow {


    public EffectsWindow(Screen screen) {
        super(screen, "EffectsWindow","Shows the player's current potion effects and their times",true);
    }

    @Override
    protected LinkedHashMap<String, ListInfo> getListMap() {
        return null;
    }

    @Override
    protected String getLabel() {
        return "Effects List";
    }
}
