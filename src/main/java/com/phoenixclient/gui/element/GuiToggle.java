package com.phoenixclient.gui.element;

import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

public class GuiToggle extends GuiWidget {

    private final SettingGUI<Boolean> setting;
    private final String title;

    protected float toggleFade = 0;

    private Color color;

    public GuiToggle(Screen screen, String title, SettingGUI<Boolean> setting, Vector pos, Vector size, Color color) {
        super(screen,pos,size);
        this.setting = setting;
        this.title = title;
        this.color = color;
    }

    public GuiToggle(Screen screen, SettingGUI<Boolean> setting, Vector pos, Vector size, Color color) {
        this(screen,setting.getName(),setting,pos,size,color);
    }


    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), BGC);

        //Draw Fill
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(),new Color(getColor().getRed(),getColor().getGreen(),getColor().getBlue(),(int) toggleFade));

        //Draw Text
        double scale = 1;
        if (DrawUtil.getFontTextWidth(getTitle()) + 2 > getSize().getX()) scale = getSize().getX()/(DrawUtil.getFontTextWidth(getTitle()) + 2);
        DrawUtil.drawFontText(graphics, getTitle(), getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE,true,(float)scale);
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
    public SettingGUI<Boolean> getSetting() {
        return setting;
    }

}
