package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.setting.ISettingParent;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.setting.SettingGUI;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.phoenixclient.PhoenixClient.MC;

public abstract class Module implements ISettingParent, Comparable<Module> {

    /*
     * Module Ideas List:
     * Mirror Mod - Creates two images on each side of the screen, rendering like car mirrors to show you the side and back of your view without turning
     * Tooltip mod to show the bytes of an item
     * Mod that scans the world for a tampered with spawner
     * Mod that potentially makes banners with infinite patterns to make oversized data???
     */

    private final ArrayList<EventAction> eventActionList = new ArrayList<>();

    private final ArrayList<SettingGUI<?>> settingList = new ArrayList<>();

    private final String name;
    private final String description;
    private final Category category;
    private final SettingGUI<Boolean> enabled;
    private final SettingGUI<Integer> keyBind;

    public Module(String name, String description, Category category, boolean defaultEnabled, int defaultKeyBind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = new SettingGUI<>(this,"enabled","Is the mod enabled",defaultEnabled);
        this.keyBind = new SettingGUI<>(this,"keyBind","Key binding to active the mod",defaultKeyBind);
    }

    public void toggle() {
        if (isEnabled()) {
            disable();
        } else {
            enable();
        }
    }

    public void enable() {
        enabled.set(true);
        onEnabled();
        for (EventAction action : getEventActions()) action.subscribe();
    }

    public void disable() {
        for (EventAction action : getEventActions()) action.unsubscribe();
        onDisabled();
        enabled.set(false);
    }

    public String getModTag() {
        return "";
    }

    public void onEnabled(){
    }

    public void onDisabled() {
    }

    @Deprecated
    private void addEventActions(EventAction... actions) {
        eventActionList.addAll(Arrays.asList(actions));
    }

    protected <T extends Event> void addEventSubscriber(T event, ActionEvent<T> subscriber) {
        addEventActions(new EventAction(event,() -> subscriber.run(event)));
    }

    protected void addSettings(SettingGUI<?>... settings) {
        settingList.addAll(Arrays.asList(settings));
    }


    public void setKeyBind(int keyBind) {
        this.keyBind.set(keyBind);
    }


    public String getTitle() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }


    public boolean isEnabled() {
        return enabled.get();
    }

    public int getKeyBind() {
        return keyBind.get();
    }


    protected ArrayList<EventAction> getEventActions() {
        return eventActionList;
    }

    public ArrayList<SettingGUI<?>> getSettings() {
        return settingList;
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
        SERVER("Server");

        final String name;
        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @FunctionalInterface
    public interface ActionEvent<T extends Event> {
        void run(T event);
    }
}
