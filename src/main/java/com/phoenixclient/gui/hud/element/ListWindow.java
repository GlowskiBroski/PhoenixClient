package com.phoenixclient.gui.hud.element;

import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.*;

/**
 * An abstract list window that allows extensions for any dual-colored text window with a label
 */
public abstract class ListWindow extends GuiWindow {

    /*
    These Methods loop through the entire map:
    drawWindow (equals, full) x2
    updateAnimation(updateAnimationOffset, ) x1
    */

    protected final SettingGUI<Boolean> label;
    protected final SettingGUI<Double> scale;
    protected final SettingGUI<String> side;

    protected LinkedHashMap<String,ListInfo> previousList = null;

    private final HashMap<String,Double> animationFadeInMap = new HashMap<>();
    private final HashMap<String,Double> animationFadeOutMap = new HashMap<>(); //TODO: Implement this

    private final HashMap<Integer,AnimationSet> animationLocationMap = new HashMap<>();

    public ListWindow(Screen screen, String title, Vector pos) {
        super(screen, title, pos, Vector.NULL());
        this.label = new SettingGUI<>(this, "Label", "Show the label", true);
        this.scale = new SettingGUI<>(this, "Scale", "The scale of the list", 1d).setSliderData(.25d, 1d, .05d);
        this.side = new SettingGUI<>(this, "Side", "The side of the list", "Left").setModeData("Left","Right");
        addSettings(label, scale, side);
    }

    protected abstract LinkedHashMap<String, ListInfo> getListMap();

    protected abstract String getLabel();

    //this MAY cause performance problems. I think I fixed some, but the animations may still be problematic

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        int yOff = 0;
        if (label.get()) {
            DrawUtil.drawFontText(graphics, getLabel() + ": ", getPos().getAdded(2, 2), ColorUtil.HUD_LABEL);
            yOff += 12;
        }

        float scale = this.scale.get().floatValue();
        graphics.pose().scale(scale, scale, 1f);
        int index = 0;

        LinkedHashMap<String, ListInfo> listMap = getListMap();
        boolean shouldUpdateAnimation = previousList != null && !previousList.equals(listMap);

        if (shouldUpdateAnimation) updateAnimationRetract(listMap,scale);
        for (Map.Entry<String , ListInfo> set : listMap.entrySet()) {
            animationLocationMap.putIfAbsent(-1,new AnimationSet(false,0));
            animationLocationMap.putIfAbsent(index,new AnimationSet(true,0));
            if (shouldUpdateAnimation) updateAnimationExpand(set,index);

            if (index == 0) yOff += animationLocationMap.get(-1).offset;

            float alpha = animationFadeInMap.containsKey(set.getKey()) ? MathUtil.getBoundValue(animationFadeInMap.get(set.getKey()).floatValue(),0,1).floatValue() : 1;
            Color c1 = new Color(set.getValue().colorMain.getRed() / 255f,set.getValue().colorMain.getGreen() / 255f,set.getValue().colorMain.getBlue() / 255f, alpha);
            Color c2 = new Color(set.getValue().colorTag.getRed() / 255f,set.getValue().colorTag.getGreen() / 255f,set.getValue().colorTag.getBlue() / 255f, alpha);

            int x = switch (side.get()) {
                case "Left" -> 2;
                case "Right" -> (int)(getSize().getX() - DrawUtil.getFontTextWidth(set.getKey() + set.getValue().tag) * scale) - 3;
                default -> throw new IllegalStateException("Unexpected value: " + side.get());
            };

            DrawUtil.drawDualColorFontText(graphics, set.getKey(), set.getValue().tag, getPos().getAdded(x, 2 + yOff).getMultiplied(1 / scale), c1, c2);

            yOff += (animationLocationMap.get(index).expand ? 0 : (int)(DrawUtil.getFontTextHeight(scale) + 2 * scale)) + animationLocationMap.get(index).offset;
            index ++;
        }
        this.previousList = getListMap();
        setSize(new Vector(72 * scale, yOff == 0 ? (int)(DrawUtil.getFontTextHeight(scale) + 2 * scale) : yOff + 1));
        graphics.pose().scale(1 / scale, 1 / scale, 1f);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        float scale = this.scale.get().floatValue();
        updateAnimationOffset(scale);
        animationFadeInMap.replaceAll((k, v) -> MathUtil.getBoundValue(v + .05, 0, 1).doubleValue());
    }

    private void updateAnimationExpand(Map.Entry<String, ListInfo> set, int index) {
        if (!previousList.containsKey(set.getKey())) {
            animationFadeInMap.put(set.getKey(), 0d);
            animationLocationMap.put(index,new AnimationSet(true,0));
        }
    }

    private void updateAnimationRetract(LinkedHashMap<String , ListInfo> listMap, float scale) {
        int removedIndex = 0;
        for (Map.Entry<String , ListInfo> prevSet : previousList.entrySet()) {
            if (!listMap.containsKey(prevSet.getKey())) {
                animationLocationMap.put(removedIndex - 1,new AnimationSet(false,(int)(DrawUtil.getFontTextHeight(scale) + 2 * scale)));
                break;
            }
            removedIndex ++;
        }
    }

    private void updateAnimationOffset(float scale) {
        double rate = 2;
        int max = (int)(DrawUtil.getFontTextHeight(scale) + 2 * scale);
        int min = 0;

        for (Map.Entry<Integer, AnimationSet> set : animationLocationMap.entrySet()) {
            if (set.getValue().expand) {
                set.getValue().offset += rate;
                if (set.getValue().offset >= max) set.getValue().offset = max;
            } else {
                set.getValue().offset -= rate;
                if (set.getValue().offset <= min) set.getValue().offset = min;
            }
        }
    }

    protected record ListInfo(String tag, Color colorMain, Color colorTag) {}

    public static class AnimationSet {
        public boolean expand;
        public int offset;
        public AnimationSet(boolean expand, int offset) {
            this.expand = expand;
            this.offset = offset;
        }
    }
}