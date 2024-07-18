package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class GuiButton extends GuiWidget {

    private String title;
    private ActionParams action;
    private boolean pressing;

    public GuiButton(Screen screen, String title, Vector pos, Vector size, ColorManager colorManager, ActionParams action) {
        super(screen, pos, size);
        this.title = title;
        this.action = action;
        this.colorManager = colorManager;
        this.pressing = false;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), getColor());

        //Draw Text
        double scale = 1;
        if (DrawUtil.getFontTextWidth(getTitle()) > getSize().getX() - 2) scale = (getSize().getX() - 2)/(DrawUtil.getFontTextWidth(getTitle()) + 2);
        Vector pos = getPos().getAdded(new Vector(getSize().getX() / 2 - DrawUtil.getFontTextWidth(getTitle()) / 2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2));
        DrawUtil.drawFontText(graphics, getTitle(), pos, Color.WHITE,true,(float)scale);
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (button != 0) return;
        switch (state) {
            case Mouse.ACTION_CLICK -> {
                if (isMouseOver())
                    pressing = true;
            }
            case Mouse.ACTION_RELEASE -> {
                pressing = false;
                if (isMouseOver()) getAction().run(mousePos, button);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAction(ActionParams action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    private ActionParams getAction() {
        return action;
    }

    public Color getColor() {
        if (pressing) {
            int[] colVal = {colorManager.getBaseColor().getRed(), colorManager.getBaseColor().getGreen(), colorManager.getBaseColor().getBlue()};
            for (int i = 0; i < colVal.length; i++) {
                colVal[i] = (int)MathUtil.getBoundValue(colVal[i] - 50,0,255);
            }
            return new Color(colVal[0],colVal[1],colVal[2],colorManager.getBaseColor().getAlpha());
        } else {
            return colorManager.getBaseColor();
        }
    }


    @FunctionalInterface
    public interface ActionParams {
        void run(Object... params);
    }

}
