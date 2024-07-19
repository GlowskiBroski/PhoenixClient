package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class GuiModeCycle<T> extends GuiWidget {

    private final SettingGUI<T> setting;

    private boolean pressingR;
    private boolean pressingL;

    private int arrayNumber;

    public GuiModeCycle(Screen screen, SettingGUI<T> setting, Vector pos, Vector size, ColorManager colorManager) {
        super(screen, pos, size);
        this.setting = setting;
        this.colorManager = colorManager;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics,getPos(),getSize(), colorManager.getBackgroundColor());

        //Draw Mode Arrows
        DrawUtil.drawArrowHead(graphics,getPos().getAdded(2,2),(float)getSize().getY()-4,getColorL(),false,true);
        DrawUtil.drawArrowHead(graphics,getPos().getAdded(-2 + getSize().getX() - 3,2),(float)getSize().getY()-4,getColorR(),false,false);

        //Draw the text
        double scale = 1;
        String text = getSetting().getName() + ": " + getSetting().get();
        if (DrawUtil.getFontTextWidth(text) > getSize().getX() - 2) scale = (getSize().getX() - 2)/(DrawUtil.getFontTextWidth(text) + 2);
        TextBuilder.start(text,getPos().getAdded(getSize().getX()/2 - DrawUtil.getFontTextWidth(text)/2,1 + getSize().getY()/2 - DrawUtil.getFontTextHeight()/2),Color.WHITE).scale((float)scale).draw(graphics);
    }

    @Override
    public void mousePressed(int button, int action, Vector mousePos) {
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                switch (action) {
                    case Mouse.ACTION_CLICK -> {
                        if (isMouseOver()) pressingR = true;
                    }
                    case Mouse.ACTION_RELEASE -> {
                        if (isMouseOver()) switchMode(true);
                        pressingR = false;
                    }
                }
            }

            case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                switch (action) {
                    case Mouse.ACTION_CLICK -> {
                        if (isMouseOver()) pressingL = true;
                    }
                    case Mouse.ACTION_RELEASE -> {
                        if (isMouseOver()) switchMode(false);
                        pressingL = false;
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

    public Color getColorR() {
        if (pressingR) {
            int[] colVal = {colorManager.getBaseColor().getRed(), colorManager.getBaseColor().getGreen(), colorManager.getBaseColor().getBlue()};
            for (int i = 0; i < colVal.length; i++) {
                colVal[i] = (int)MathUtil.getBoundValue(colVal[i] - 50,0,255);
            }
            return new Color(colVal[0],colVal[1],colVal[2],colorManager.getBaseColor().getAlpha());
        } else {
            return colorManager.getBaseColor();
        }
    }

    public Color getColorL() {
        if (pressingL) {
            int[] colVal = {colorManager.getBaseColor().getRed(), colorManager.getBaseColor().getGreen(), colorManager.getBaseColor().getBlue()};
            for (int i = 0; i < colVal.length; i++) {
                colVal[i] = (int)MathUtil.getBoundValue(colVal[i] - 50,0,255);
            }
            return new Color(colVal[0],colVal[1],colVal[2],colorManager.getBaseColor().getAlpha());
        } else {
            return colorManager.getBaseColor();
        }
    }

    @Override
    public SettingGUI<T> getSetting() {
        return setting;
    }
}
