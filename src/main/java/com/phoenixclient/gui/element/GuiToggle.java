package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class GuiToggle extends GuiWidget {

    private final SettingGUI<Boolean> setting;
    private final String title;

    protected float toggleFade = 0;

    public GuiToggle(Screen screen, String title, SettingGUI<Boolean> setting, Vector pos, Vector size, ColorManager colorManager) {
        super(screen,pos,size);
        this.setting = setting;
        this.title = title;
        this.colorManager = colorManager;
    }

    public GuiToggle(Screen screen, SettingGUI<Boolean> setting, Vector pos, Vector size, ColorManager colorManager) {
        this(screen,setting.getTitle(),setting,pos,size,colorManager);
    }


    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), colorManager.getBackgroundColor());

        //Draw Fill
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(),new Color(colorManager.getWidgetColor().getRed(),colorManager.getWidgetColor().getGreen(),colorManager.getWidgetColor().getBlue(),(int) toggleFade));

        //Draw Text
        double scale = 1;
        if (DrawUtil.getFontTextWidth(getTitle()) > getSize().getX() - 2) scale = (getSize().getX() - 2)/(DrawUtil.getFontTextWidth(getTitle()) + 2);
        TextBuilder.start(getTitle(), getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE).scale((float)scale).draw(graphics);
    }


    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (button == Mouse.BUTTON_LEFT.getId() && state == Mouse.ACTION_CLICK && isMouseOver()) {
            getSetting().set(!getSetting().get());
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        //NULL
    }


    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        toggleFade = getNextFade(getSetting().get(), toggleFade, 0, 150, 2f * speed);
    }


    public String getTitle() {
        return title;
    }

    @Override
    public SettingGUI<Boolean> getSetting() {
        return setting;
    }

}
