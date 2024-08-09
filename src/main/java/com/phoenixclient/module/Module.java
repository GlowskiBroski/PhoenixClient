package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.interfaces.IToggleableEventSubscriber;
import com.phoenixclient.util.interfaces.ISettingParent;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.setting.Container;
import com.phoenixclient.util.setting.SettingGUI;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

import static com.phoenixclient.PhoenixClient.MC;

public abstract class Module implements IToggleableEventSubscriber, ISettingParent, Comparable<Module> {

    /*
     * Module Ideas List:
     * Mirror Mod - Creates two images on each side of the screen, rendering like car mirrors to show you the side and back of your view without turning
     * Tooltip mod to show the bytes of an item
     * Mod that scans the world for a tampered with spawner
     * Mod that potentially makes banners with infinite patterns to make oversized data???
     */

    private final ArrayList<EventAction> eventActionList = new ArrayList<>();
    private final ArrayList<SettingGUI<?>> settingList = new ArrayList<>();

    private final String title;
    private final String description;
    private final Category category;
    private final SettingGUI<Boolean> enabled;
    private final SettingGUI<Integer> keyBind;

    public boolean defaultEnabled = false;

    public Module(String title, String description, Category category, boolean defaultEnabled, int defaultKeyBind) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.enabled = new SettingGUI<>(this,"enabled","Is the mod enabled",defaultEnabled);
        this.keyBind = new SettingGUI<>(this,"keyBind","Key binding to active the mod",defaultKeyBind);
    }

    @Override
    public void enable() {
        IToggleableEventSubscriber.super.enable();
        if (!defaultEnabled && PhoenixClient.getNotificationManager().enableDisable.get())
            PhoenixClient.getNotificationManager().sendNotification(getTitle() + " Enabled!", new Color(220, 255, 220,255));
        defaultEnabled = false;
    }

    @Override
    public void disable() {
        IToggleableEventSubscriber.super.disable();
        if (PhoenixClient.getNotificationManager().enableDisable.get())
            PhoenixClient.getNotificationManager().sendNotification(getTitle() + " Disabled!", new Color(255,220,200,255));
    }

    @Override
    public void onEnabled(){
    }

    @Override
    public void onDisabled() {
    }

    @Override
    public ArrayList<EventAction> getEventActions() {
        return eventActionList;
    }

    @Override
    public Container<Boolean> getEnabledContainer() {
        return enabled;
    }

    @Override
    public ArrayList<SettingGUI<?>> getSettings() {
        return settingList;
    }

    @Override
    public String getSettingsKey() {
        return getTitle();
    }


    /**
     * To use this, place at the head of onEnabled().
     * Type: if (updateDisableOnEnabled()) return;
     */
    protected boolean updateDisableOnEnabled() {
        if (MC.player == null) {
            enabled.set(false);
            for (EventAction action : getEventActions()) action.unsubscribe();
            return true;
        }
        return false;
    }


    public String getModTag() {
        return "";
    }

    public boolean showInList() {
        return true;
    }


    public void setKeyBind(int keyBind) {
        this.keyBind.set(keyBind);
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKeyBind() {
        return keyBind.get();
    }


    @Override //Alphabetical Comparison
    public int compareTo(@NotNull Module o) {
        return getTitle().compareTo(o.getTitle());
    }


    public static EventAction MODULE_KEYBIND_ACTION = new EventAction(Event.EVENT_KEY_PRESS, () -> {
        if (MC.screen != null) return;
        for (Module module : PhoenixClient.getModules()) {
            if (Event.EVENT_KEY_PRESS.getKey() == module.getKeyBind() && Event.EVENT_KEY_PRESS.getState() == Key.ACTION_PRESS)
                module.toggle();
        }
    });

    public enum Category {
        COMBAT("Combat"),
        PLAYER("Player"),
        MOTION("Motion"),
        RENDER("Render"),
        SERVER("Server"),
        MANAGERS("Manager");

        final String name;
        Category(String name) {
            this.name = name;
        }

        public String getTitle() {
            return name;
        }
    }

    @FunctionalInterface
    public interface ActionEvent<T extends Event> {
        void run(T event);
    }
}
