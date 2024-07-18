package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ModuleKeybindListWindow extends ListWindow {

    public ModuleKeybindListWindow(Screen screen, Vector pos) {
        super(screen, "KeybindListWindow", pos);
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

        for (Module module : sortedDisplayList) {
            String first = module.getTitle() + ": ";
            String second = "";
            try {
                second = second.concat(module.getKeyBind() == -1 ? "NONE" : GLFW.glfwGetKeyName(module.getKeyBind(), -1).toUpperCase());
            } catch (NullPointerException e) {
                second = second.concat("Bound (Null Key Name)");
            }
            map.put(first,new ListInfo(second,Color.WHITE,module.isEnabled() ? Color.GREEN : Color.RED));
        }

        return map;
    }

}