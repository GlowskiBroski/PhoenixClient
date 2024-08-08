package com.phoenixclient.util.interfaces;

import com.phoenixclient.util.setting.SettingGUI;

import java.util.ArrayList;
import java.util.Arrays;

public interface ISettingParent {

    default void addSettings(SettingGUI<?>... settings) {
        getSettings().addAll(Arrays.asList(settings));
    }

    ArrayList<SettingGUI<?>> getSettings();

    String getSettingsKey();

}
