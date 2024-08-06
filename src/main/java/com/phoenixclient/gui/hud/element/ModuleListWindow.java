package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;

import com.phoenixclient.module.Module;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;

import static com.phoenixclient.PhoenixClient.MC;

public class ModuleListWindow extends ListWindow {

    protected final SettingGUI<String> vert;
    private final SettingGUI<Boolean> rainbow;

    public ModuleListWindow(Screen screen) {
        super(screen, "ModuleListWindow", "Lists all currently enabled modules and their tags",true);
        this.vert = new SettingGUI<>(this, "Vert", "The vertical side of the list", "Top").setModeData("Top","Bottom");
        this.rainbow = new SettingGUI<>(this, "Rainbow", "Make the mods list rainbow", false);
        addSettings(vert,rainbow);
    }

    @Override
    protected String getLabel() {
        return "Module List";
    }

    @Override
    protected LinkedHashMap<String, ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> map = new LinkedHashMap<>();

        ArrayList<Module> sortedDisplayList = new ArrayList<>();
        for (Module module : PhoenixClient.getModules())
            if (module.isEnabled() && module.showInList()) sortedDisplayList.add(module);
        Module[] mods = sortedDisplayList.toArray(new Module[0]);
        Arrays.sort(mods, new ModuleComparator());
        sortedDisplayList = new ArrayList<>(Arrays.asList(mods));

        int rainbowOffset = 0;
        for (Module module : sortedDisplayList) {
            String tag = module.getModTag().isEmpty() ? "" : "[" + module.getModTag() + "]";
            if (rainbow.get()) {
                map.put(module.getTitle(),new ListInfo(tag, ColorManager.getRainbowColor(rainbowOffset),Color.WHITE));
                rainbowOffset += switch (vert.get()) {
                    case "Top" -> 1;
                    case "Bottom" ->  -1;
                    default -> throw new IllegalStateException("Unexpected value: " + vert.get());
                };
            } else {
                map.put(module.getTitle(),new ListInfo(tag,colorManager.getBaseColor(), Color.WHITE));
            }
        }

        return map;
    }

    @Override
    protected void updateWindowPositionFromSize() {
        super.updateWindowPositionFromSize();
        switch (vert.get()) {
            case "Top" -> {
            }
            case "Bottom" -> onHeightChange.run(getSize().getY(), () -> {
                if (onHeightChange.getPrevValue() != null)
                    posScale.set(getPos().getSubtracted(0, getSize().getY() - onHeightChange.getPrevValue()).getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 / MC.getWindow().getGuiScaledHeight()));
            });
        }
    }

    public class ModuleComparator implements Comparator<Module> {

        @Override
        public int compare(Module module1, Module module2) {
            String tag1 = module1.getModTag().isEmpty() ? "" : "[" + module1.getModTag() + "]";
            String tag2 = module2.getModTag().isEmpty() ? "" : "[" + module2.getModTag() + "]";

            return switch (vert.get()) {
                case "Top" -> (int)(DrawUtil.getFontTextWidth(module2.getTitle() + tag2) - DrawUtil.getFontTextWidth(module1.getTitle() + tag1));
                case "Bottom" -> (int)(-DrawUtil.getFontTextWidth(module2.getTitle() + tag2) + DrawUtil.getFontTextWidth(module1.getTitle() + tag1));
                default -> throw new IllegalStateException("Unexpected value: " + vert.get());
            };
        }
    }
}