package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class GuiSlider<T extends Number> extends GuiWidget {

    private final SettingGUI<T> setting;
    private final String title;

    private Color color;

    private boolean sliding;

    private GuiSlider(Screen screen, String title, SettingGUI<T> setting, Vector pos, Vector size, Color color) {
        super(screen, pos, size);
        this.setting = setting;
        this.title = title;

        this.color = color;

        this.sliding = false;
    }

    public GuiSlider(Screen screen, SettingGUI<T> setting, Vector pos, Vector size, Color color) {
        this(screen, setting.getName(), setting, pos, size, color);
    }


    //TODO: Clean the slider class draw method.
    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        if (sliding) updateSliderValue(mousePos);

        //Draw the background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), BGC);

        //Draw the slider fill
        double valueRange = getSetting().getMax() - getSetting().getMin();
        double sliderWidth = getSize().getX() / valueRange * (getSetting().get().doubleValue() - getSetting().getMin());
        int border = (int) getSize().getX() / 40;
        DrawUtil.drawRectangleRound(graphics, getPos().getAdded(new Vector(border, border)), new Vector(sliderWidth - (sliderWidth > border ? border : 0), getSize().getY() - border * 2), new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 150), 1.5, false);

        //Draw the slider node
        int nodeWidth = (int) getSize().getY() / 4;
        double nodeX = sliderWidth - nodeWidth / 2f;
        if (nodeX + nodeWidth > getSize().getX()) nodeX = getSize().getX() - nodeWidth;
        if (nodeX < 0) nodeX = 0;
        DrawUtil.drawRectangleRound(graphics, getPos().getAdded(new Vector(nodeX, 0)), new Vector(nodeWidth, getSize().getY()), new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), 255), 1.5, false);

        //Draw the slider text
        double scale = 1;
        String title = getTitle() + ": " + (getSetting().getType().equals("integer") ? getSetting().get().intValue() : String.format("%.2f", getSetting().get()));
        if (DrawUtil.getFontTextWidth(title) + border + 1 > getSize().getX())
            scale = getSize().getX() / (DrawUtil.getFontTextWidth(title) + border + 1);
        DrawUtil.drawFontText(graphics, title, getPos().getAdded(new Vector(border + 1, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE, true, (float) scale);
    }


    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (button != 0) return;
        switch (state) {
            case Mouse.ACTION_CLICK -> {
                if (isMouseOver()) setSliding(true);
            }
            case Mouse.ACTION_RELEASE -> {
                if (sliding) setSliding(false);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
    }

    private void updateSliderValue(Vector mousePos) {
        double settingRange = getSetting().getMax() - getSetting().getMin();

        double sliderWidth = mousePos.getX() - getPos().getX();
        double sliderPercent = MathUtil.getBoundValue(sliderWidth, 0, getSize().getX()).doubleValue() / getSize().getX();

        double calculatedValue = getSetting().getMin() + sliderPercent * settingRange;
        double val = MathUtil.roundDouble((((Math.round(calculatedValue / getSetting().getStep())) * getSetting().getStep())), 2); //Rounds the slider based off of the step val

        T value = getSetting().getType().equals("integer") ? (T) (Integer) (int) val : (T) (Double) val;
        getSetting().set(value);
    }


    public void setSliding(boolean sliding) {
        this.sliding = sliding;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public SettingGUI<T> getSetting() {
        return setting;
    }
}
