package com.phoenixclient.util.interfaces;

import com.phoenixclient.util.setting.Container;

public interface IToggleable {

    default void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }

    default void enable() {
        getEnabledContainer().set(true);
        onEnabled();
    }

    default void disable() {
        onDisabled();
        getEnabledContainer().set(false);
    }

    void onEnabled();

    void onDisabled();

    default boolean isEnabled() {
        return getEnabledContainer().get();
    }

    Container<Boolean> getEnabledContainer();

}
