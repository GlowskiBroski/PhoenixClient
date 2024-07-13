package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class GuiModeCycle<T> extends GuiWidget {

    private final SettingGUI<T> setting;

    private Color baseColor;
    private Color colorR;
    private Color colorL;

    private int arrayNumber;

    public GuiModeCycle(Screen screen, SettingGUI<T> setting, Vector pos, Vector size, Color color) {
        super(screen, pos, size);
        this.setting = setting;
        this.baseColor = color;
        this.colorL = color;
        this.colorR = color;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics,getPos(),getSize(), BGC);

        //Draw Mode Arrows
        DrawUtil.drawArrowHead(graphics,getPos().getAdded(2,2),(float)getSize().getY()-4,colorL,false,true);
        DrawUtil.drawArrowHead(graphics,getPos().getAdded(-2 + getSize().getX() - 3,2),(float)getSize().getY()-4,colorR,false,false);

        //Draw the text
        double scale = 1;
        String text = getSetting().getName() + ": " + getSetting().get();
        if (DrawUtil.getFontTextWidth(text) > getSize().getX()) scale = getSize().getX()/DrawUtil.getFontTextWidth(text);
        DrawUtil.drawFontText(graphics,text,getPos().getAdded(getSize().getX()/2 - DrawUtil.getFontTextWidth(text)/2,1 + getSize().getY()/2 - DrawUtil.getFontTextHeight()/2),Color.WHITE,true,(float)scale);
    }

    @Override
    public void mousePressed(int button, int action, Vector mousePos) {
        int[] pressColor = {getColor().getRed(), getColor().getGreen(), getColor().getBlue()};
        for (int i = 0; i < pressColor.length; i++) {
            int col = pressColor[i] - 50;
            col = MathUtil.getBoundValue(col, 0, 255).intValue();
            pressColor[i] = col;
        }

        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                switch (action) {
                    case Mouse.ACTION_CLICK -> {
                        if (isMouseOver()) colorR = new Color(pressColor[0], pressColor[1], pressColor[2], getColor().getAlpha());
                    }
                    case Mouse.ACTION_RELEASE -> {
                        if (isMouseOver()) switchMode(true);
                        colorR = baseColor;
                    }
                }
            }

            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                switch (action) {
                    case Mouse.ACTION_CLICK -> {
                        if (isMouseOver()) colorL = new Color(pressColor[0], pressColor[1], pressColor[2], getColor().getAlpha());
                    }
                    case Mouse.ACTION_RELEASE -> {
                        if (isMouseOver()) switchMode(false);
                        colorL = baseColor;
                    }
                }
            }
        }
    }

    private void switchMode(boolean forward) {
        int increment = forward ? 1 : -1;
        int wrapAroundIndex = forward ? getSetting().getModes().size() : - 1;
        int endIndex = forward ? 0 : getSetting().getModes().size() - 1;

        int arrayNumber = getSetting().getModes().indexOf(getSetting().get()) + increment;
        getSetting().set(getSetting().getModes().get(arrayNumber == wrapAroundIndex ? endIndex : arrayNumber));
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        //TODO: Add a left/right scroll animation when changing modes
    }

    public void setColor(Color color) {
        this.baseColor = color;
    }

    public Color getColor() {
        return baseColor;
    }

    @Override
    public SettingGUI<T> getSetting() {
        return setting;
    }
}
