package com.phoenixclient.util.setting;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.interfaces.ISettingParent;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingGUI<T> extends Setting<T> {

    private final ISettingParent parent;

    private final String name;
    private final String description;

    private Double min;
    private Double max;
    private Double step;

    private T[] modes;
    private String[] modeDescriptions;

    private boolean numbersOnly;

    private Dependency<?> dependency;

    private final OnChange<T> onChange;

    public SettingGUI(ISettingParent parent, String name, String description, T defaultValue) {
        super(PhoenixClient.getSettingManager(), parent.getSettingsKey() + "_" + name, defaultValue);
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.onChange = new OnChange<>();
    }

    public SettingGUI<T> setSliderData(double min, double max, double step) {
        this.min = min;
        this.max = max;
        this.step = step;
        return this;
    }

    @SafeVarargs
    public final SettingGUI<T> setModeData(T... modes) {
        this.modes = modes;
        return this;
    }

    //TODO: Implement this
    public final SettingGUI<T> setModeDescriptions(String... descriptions) {
        this.modeDescriptions = descriptions;
        return this;
    }

    public SettingGUI<T> setTextData(boolean numbersOnly) {
        this.numbersOnly = numbersOnly;
        return this;
    }

    public <E> SettingGUI<T> setDependency(Container<E> setting, E value) {
        this.dependency = new Dependency<>(setting, value);
        return this;
    }

    public OnChange<T> getChangeDetector() {
        return onChange;
    }

    public void runOnChange(Runnable runnable) {
        onChange.run(get(),runnable);
    }

    public String getTitle() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getStep() {
        return step;
    }

    public ArrayList<T> getModes() {
        try {
            return new ArrayList<>(Arrays.asList(modes));
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public boolean isNumbersOnly() {
        return numbersOnly;
    }

    public ISettingParent getParent() {
        return parent;
    }

    public Dependency<?> getDependency() {
        return dependency;
    }


    public record Dependency<E>(Container<E> container, E value) {

        public boolean isValidated() {
            return (container.get().equals(value));
        }

    }

}
