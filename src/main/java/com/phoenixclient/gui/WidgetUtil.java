package com.phoenixclient.gui;

import com.phoenixclient.gui.element.*;

import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorUtil;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.Comparator;

public class WidgetUtil {

    /**
     * Generates an ordered list of widgets for any list of Gui Settings
     * @param screen
     * @param settings
     * @return
     */
    public static ArrayList<GuiWidget> generateWidgetList(Screen screen, ArrayList<SettingGUI<?>> settings) {
        ArrayList<GuiWidget> widgetList = new ArrayList<>();
        for (SettingGUI setting : settings) {
            if (!setting.getModes().isEmpty()) {
                widgetList.add(new GuiModeCycle<>(screen, setting, Vector.NULL(), Vector.NULL(), ColorUtil.getTheme().getWidgetColor()));
                continue;
            }
            switch (setting.getType()) {
                case "boolean" -> widgetList.add(new GuiToggle(screen, setting, Vector.NULL(), Vector.NULL(), ColorUtil.getTheme().getWidgetColor()));
                case "integer" -> widgetList.add(new GuiSlider<Integer>(screen, setting, Vector.NULL(), Vector.NULL(), ColorUtil.getTheme().getWidgetColor()));
                case "double", "float" -> widgetList.add(new GuiSlider<Double>(screen, setting, Vector.NULL(), Vector.NULL(), ColorUtil.getTheme().getWidgetColor()));
                case "string" -> widgetList.add(new GuiTextField(screen, setting, Vector.NULL(), Vector.NULL()));
            }
        }

        widgetList.sort(new WidgetComparator());
        return widgetList;
    }

    public static class WidgetComparator implements Comparator<GuiWidget> {

        @Override
        public int compare(GuiWidget widget1, GuiWidget widget2) {
            int typeWeight = getTypeWeight(widget1) - getTypeWeight(widget2);
            int letterWeight = getIndexFromLetter(widget1.getSetting().getName().charAt(0)) - getIndexFromLetter(widget2.getSetting().getName().charAt(0));

            return typeWeight + letterWeight;
        }

        private int getIndexFromLetter(char letter) {
            return switch (letter) {
                case 'a', 'A' -> 0;
                case 'b', 'B' -> 1;
                case 'c', 'C' -> 2;
                case 'd', 'D' -> 3;
                case 'e', 'E' -> 4;
                case 'f', 'F' -> 5;
                case 'g', 'G' -> 6;
                case 'h', 'H' -> 7;
                case 'i', 'I' -> 8;
                case 'j', 'J' -> 9;
                case 'k', 'K' -> 10;
                case 'l', 'L' -> 11;
                case 'm', 'M' -> 12;
                case 'n', 'N' -> 13;
                case 'o', 'O' -> 14;
                case 'p', 'P' -> 15;
                case 'q', 'Q' -> 16;
                case 'r', 'R' -> 17;
                case 's', 'S' -> 18;
                case 't', 'T' -> 19;
                case 'u', 'U' -> 20;
                case 'v', 'V' -> 21;
                case 'w', 'W' -> 22;
                case 'x', 'X' -> 23;
                case 'y', 'Y' -> 24;
                case 'z', 'Z' -> 25;
                default -> -1;
            };
        }

        private int getTypeWeight(GuiWidget widget) {
            //Lower number = higher in list
            int weightMul = 1000;
            if (!widget.getSetting().getModes().isEmpty()) return weightMul;
            return weightMul * switch (widget.getSetting().getType()) {
                case "boolean" -> 4;
                case "integer", "float", "double" -> 2;
                case "string" -> 3;
                default -> -1;
            };
        }
    }
}
