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

import java.awt.*;

public class GuiSlider<T extends Number> extends GuiWidget {

    private final SettingGUI<T> setting;
    private final String title;

    private boolean sliding;

    private GuiSlider(Screen screen, String title, SettingGUI<T> setting, Vector pos, Vector size, ColorManager colorManager) {
        super(screen, pos, size);
        this.setting = setting;
        this.title = title;

        this.colorManager = colorManager;

        this.sliding = false;
    }

    public GuiSlider(Screen screen, SettingGUI<T> setting, Vector pos, Vector size, ColorManager colorManager) {
        this(screen, setting.getTitle(), setting, pos, size, colorManager);
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        if (sliding) updateSliderValue(mousePos);

        //Draw the background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), colorManager.getBackgroundColor());

        //Draw the slider fill
        double valueRange = getSetting().getMax() - getSetting().getMin();
        double sliderWidth = getSize().getX() / valueRange * (getSetting().get().doubleValue() - getSetting().getMin());
        int border = (int) getSize().getX() / 40;
        DrawUtil.drawRectangleRound(graphics, getPos().getAdded(new Vector(border, border)), new Vector(sliderWidth - (sliderWidth > border ? border : 0), getSize().getY() - border * 2), new Color(colorManager.getWidgetColor().getRed(), colorManager.getWidgetColor().getGreen(), colorManager.getWidgetColor().getBlue(), 150), 1.5, false);

        //Draw the slider node
        int nodeWidth = (int) getSize().getY() / 4;
        double nodeX = sliderWidth - nodeWidth / 2f;
        if (nodeX + nodeWidth > getSize().getX()) nodeX = getSize().getX() - nodeWidth;
        if (nodeX < 0) nodeX = 0;
        DrawUtil.drawRectangleRound(graphics, getPos().getAdded(new Vector(nodeX, 0)), new Vector(nodeWidth, getSize().getY()), new Color(colorManager.getWidgetColor().getRed(), colorManager.getWidgetColor().getGreen(), colorManager.getWidgetColor().getBlue(), 255), 1.5, false);

        //Draw the slider text
        double scale = 1;
        String title = getTitle() + ": " + (getSetting().getType().equals("integer") ? getSetting().get().intValue() : String.format("%.2f", getSetting().get()));
        if (DrawUtil.getFontTextWidth(title) + border + 1 > getSize().getX() - 2) scale = (getSize().getX() - 2)/(DrawUtil.getFontTextWidth(title) + border + 2);
        TextBuilder.start( title, getPos().getAdded(new Vector(border + 1, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE).scale((float) scale).dynamic().draw(graphics);
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

    public String getTitle() {
        return title;
    }

    @Override
    public SettingGUI<T> getSetting() {
        return setting;
    }
}
