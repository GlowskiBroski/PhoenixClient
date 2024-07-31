package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;

import static com.phoenixclient.PhoenixClient.MC;

public class ModuleKeybindListWindow extends ListWindow {

    protected final SettingGUI<String> order;

    private final HashMap<Module,Double> animationFadeColorMap = new HashMap<>();

    public ModuleKeybindListWindow(Screen screen, Vector pos) {
        super(screen, "KeybindListWindow", pos);
        this.order = new SettingGUI<>(this, "Order", "The ordering of the list", "ABC").setModeData("Top","Bottom","ABC");
        addSettings(order);
    }

    @Override
    protected String getLabel() {
        return "Keybind List";
    }

    @Override
    protected LinkedHashMap<String, ListInfo> getListMap() {
        LinkedHashMap<String,ListInfo> map = new LinkedHashMap<>();
        ArrayList<Module> sortedDisplayList = new ArrayList<>();
        for (Module module : PhoenixClient.getModules()) {
            if (module.getKeyBind() != -1) sortedDisplayList.add(module);
        }

        if (!order.get().equals("ABC")) {
            Module[] mods = sortedDisplayList.toArray(new Module[0]);
            Arrays.sort(mods, new ModuleComparator());
            sortedDisplayList = new ArrayList<>(Arrays.asList(mods));
        }

        for (Module module : sortedDisplayList) {
            updateAnimationFade(module);
            String first = module.getTitle();
            String second = getKeybindString(module);
            map.put(first,new ListInfo(second,colorManager.getBaseColor(), new Color(Color.HSBtoRGB(80/255f,animationFadeColorMap.get(module).floatValue(),200/255f))));
        }

        return map;
    }

    private void updateAnimationFade(Module module) {
        animationFadeColorMap.putIfAbsent(module,0d);
        float speed = .025f;
        animationFadeColorMap.put(module,Math.clamp(animationFadeColorMap.get(module) + (speed * (module.isEnabled() ? 1 : -1)),0,1));
    }

    private String getKeybindString(Module module) {
        String str = "[";
        try {
            str = str.concat(module.getKeyBind() == -1 ? "NONE" : GLFW.glfwGetKeyName(module.getKeyBind(), -1).toUpperCase());
        } catch (NullPointerException e) {
            str = str.concat("Bound (Null Key Name)");
        }
        return str.concat("]");
    }

    @Override
    protected void updateWindowPositionFromSize() {
        super.updateWindowPositionFromSize();
        switch (order.get()) {
            case "Top","ABC" -> {
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
            String tag1 = module1.getTitle() + getKeybindString(module1);
            String tag2 = module2.getTitle() + getKeybindString(module2);

            return switch (order.get()) {
                case "Top" -> (int)(DrawUtil.getFontTextWidth(module2.getTitle() + tag2) - DrawUtil.getFontTextWidth(module1.getTitle() + tag1));
                case "Bottom" -> (int)(-DrawUtil.getFontTextWidth(module2.getTitle() + tag2) + DrawUtil.getFontTextWidth(module1.getTitle() + tag1));
                default -> throw new IllegalStateException("Unexpected value: " + order.get());
            };
        }
    }

}