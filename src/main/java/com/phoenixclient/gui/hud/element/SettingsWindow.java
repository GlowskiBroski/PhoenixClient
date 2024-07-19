package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.WidgetUtil;
import com.phoenixclient.gui.element.GuiButton;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;

public class SettingsWindow extends GuiWindow {

    private ArrayList<GuiWidget> widgetList = new ArrayList<>();

    private final GuiButton closeButton;
    private final GuiWindow parentWindow;

    public SettingsWindow(Screen screen, GuiWindow parentWindow, Vector pos) {
        super(screen, parentWindow.getTitle() + "_settings", pos, new Vector(100, 20));
        this.parentWindow = parentWindow;
        ColorManager closeButtonManager = new ColorManager(new Color(200, 0, 0));
        this.closeButton = new GuiButton(getScreen(), "X", getPos(), getSize(), closeButtonManager, (args) -> {
            parentWindow.setSettingsOpen(false);
        });

        this.widgetList = WidgetUtil.generateWidgetList(getScreen(),getParentWindow().getSettings());
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        //Draw Header
        DrawUtil.drawRectangleRound(graphics,getPos(),getSize(), PhoenixClient.getColorManager().getBaseColor());

        updateXButton(graphics, mousePos);

        //Draw Title
        TextBuilder.start(getParentWindow().getTitle().replace("Window",""), getPos().getAdded(new Vector(closeButton.getSize().getX() + 4 + 2,1 + getSize().getY()/2 - DrawUtil.getFontTextHeight()/2)), Color.WHITE).draw(graphics);

        //Draw Background
        int backgroundHeight = 1;
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            backgroundHeight += 18;
        }
        Color BGC = colorManager.getBackgroundColor();
        DrawUtil.drawRectangleRound(graphics,getPos().getAdded(0,getSize().getY()),new Vector(getSize().getX(),backgroundHeight),new Color(BGC.getRed(), BGC.getGreen(), BGC.getBlue(), BGC.getAlpha() / 2));

        drawWidgets(graphics, mousePos);
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        super.mousePressed(button, state, mousePos);
        this.closeButton.mousePressed(button,state,mousePos);
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.mousePressed(button,state,mousePos);
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        super.keyPressed(key, scancode, modifiers);
        this.closeButton.keyPressed(key,scancode,modifiers);
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.keyPressed(key,scancode,modifiers);
        }
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        closeButton.runAnimation(speed);
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.runAnimation(speed);
        }
    }

    private void drawWidgets(GuiGraphics graphics, Vector mousePos) {
        int yOffset = 1;
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.setPos(getPos().getAdded(new Vector(2,getSize().getY())));
            widget.setPos(widget.getPos().getAdded(new Vector(0,yOffset)));
            widget.setSize(getSize().getAdded(new Vector(-4,-4)));
            widget.draw(graphics,mousePos);
            yOffset += getSize().getY() - 2;
        }
    }

    private void updateXButton(GuiGraphics graphics, Vector mousePos) {
        this.closeButton.setPos(getPos().getAdded(new Vector(4,4)));
        this.closeButton.setSize(new Vector(getSize().getY() - 8, getSize().getY() - 8));
        this.closeButton.draw(graphics,mousePos);
        if (this.closeButton.isMouseOver()) updateMouseOver(new Vector(-1,-1)); //Stops the window from being draggable if over X button
    }

    public GuiWindow getParentWindow() {
        return parentWindow;
    }

    public ArrayList<GuiWidget> getWidgetList() {
        return widgetList;
    }

}
