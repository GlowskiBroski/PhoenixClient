package com.phoenixclient.gui.module.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.WidgetUtil;
import com.phoenixclient.util.ConsoleUtil;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiButton;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.render.FontRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Take the description off of this menu and make it a tooltip. I need more room

public class ModuleOptionsMenu extends GuiWidget {

    private final FontRenderer descriptionFont = new FontRenderer(PhoenixClient.getFontRenderer().getFont().getFontName(),Font.BOLD);

    private ArrayList<GuiWidget> widgetList = new ArrayList<>();

    private final GuiButton mainButton;
    private final ModuleKeyBindSelector keyBindSelector;

    private Module module;
    private Module queueModule;

    private float scaling = 0;
    private boolean retract = false;
    private boolean nullify = false;

    private final OnChange<Module> onChange = new OnChange<>();

    public ModuleOptionsMenu(Screen screen, Vector pos) {
        super(screen, pos, new Vector(120,30));
        this.mainButton = new GuiButton(getScreen(),"", getPos(),getSize(), ColorUtil.getTheme().getBaseColor(),(f) -> {
            retract = true;
            nullify = true;
            setHoverHighlightVisible(false);
        });
        this.keyBindSelector = new ModuleKeyBindSelector(getScreen(),module,Vector.NULL(),new Vector(getSize().getX() - 4,14), Color.BLACK);
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Set to the top right corner of the screen
        setPos(new Vector(MC.getWindow().getGuiScaledWidth() - getSize().getX() - 2, 2));
        mainButton.setPos(getPos());

        float tempScaling = scaling; //Clones the scaling to prevent animation thread from de-syncing
        graphics.pose().scale(1f,tempScaling,1f);

        if (module != null) {
            try {
                //Draw Head
                mainButton.draw(graphics, mousePos);

                //Draw Title
                DrawUtil.drawFontText(graphics, module.getTitle(), getPos().getAdded(getSize().getMultiplied(.5).getSubtracted(DrawUtil.getFontTextWidth(module.getTitle()) / 2, DrawUtil.getFontTextHeight() / 2)).y(6), Color.WHITE);

                //Draw Description
                String desc = module.getDescription();
                float descScale = .5f;
                float width = descriptionFont.getWidth(desc) * descScale;
                float height = descriptionFont.getHeight() * descScale;
                Vector pos = getPos().getAdded(getSize().getMultiplied(descScale).getSubtracted(width / 2, height / 2)).y(20);
                DrawUtil.drawRectangleRound(graphics,pos.getSubtracted(1,1),new Vector(width,height).getAdded(3,2),new Color(50,50,50,120));
                graphics.pose().scale(descScale, descScale, 1);
                descriptionFont.drawString(graphics,desc,pos.getAdded(1,1).getMultiplied(1/descScale),new Color(25, 25, 25, 200));
                descriptionFont.drawString(graphics,desc,pos.getMultiplied(1/descScale),Color.WHITE);
                graphics.pose().scale(1 / descScale, 1 / descScale, 1);


                onChange.run(module,() -> {
                    this.widgetList = WidgetUtil.generateWidgetList(getScreen(), module.getSettings());
                    keyBindSelector.setModule(module);
                    scaling = .1f;
                });

                //Draw Drop Background
                int backgroundHeight = 17;
                for (GuiWidget widget : widgetList) {
                    if (!widget.shouldDrawSetting()) continue;
                    backgroundHeight += 16;
                }
                DrawUtil.drawRectangleRound(graphics, getPos().getAdded(0, getSize().getY()), new Vector(getSize().getX(), backgroundHeight), new Color(BGC.getRed(), BGC.getGreen(), BGC.getBlue(), BGC.getAlpha() / 2));

                drawWidgets(graphics, mousePos);
                keyBindSelector.setPos(getPos().getAdded(2,getSize().getY() + backgroundHeight - keyBindSelector.getSize().getY() - 2));
                keyBindSelector.draw(graphics,mousePos);
            } catch (NullPointerException e) {
                //This may occur due to the animation thread crossing over incorrectly
                ConsoleUtil.sendConsoleMessage("Module Null. This shouldn't happen...");
            }
        }

        graphics.pose().scale(1f,1/tempScaling,1f);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        mainButton.runAnimation(speed);
        keyBindSelector.runAnimation(speed);
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.runAnimation(speed);
        }
        runScaling(speed);
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        mainButton.mousePressed(button, state, mousePos);
        keyBindSelector.mousePressed(button, state, mousePos);
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.mousePressed(button,state,mousePos);
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        mainButton.keyPressed(key, scancode, modifiers);
        keyBindSelector.keyPressed(key, scancode, modifiers);
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            widget.keyPressed(key,scancode,modifiers);
        }
    }

    private void drawWidgets(GuiGraphics graphics, Vector mousePos) {
        int yOffset = 1;
        for (GuiWidget widget : widgetList) {
            if (!widget.shouldDrawSetting()) continue;
            int ySize = 20;
            widget.setPos(getPos().getAdded(new Vector(2, getSize().getY())));
            widget.setPos(widget.getPos().getAdded(new Vector(0, yOffset)));
            widget.setSize(new Vector(getSize().getX() - 4,ySize - 6));
            widget.draw(graphics, mousePos);
            yOffset += ySize - 4;
        }
    }

    private void runScaling(int speed) {
        if (queueModule != module) retract = true;
        if (retract) {
            scaling -= .01f * speed;
            if (scaling <= 0) {
                if (nullify) {
                    module = null;
                    queueModule = null;
                    nullify = false;
                } else {
                    module = queueModule;
                }
                retract = false;
            }
        } else {
            if (scaling < 1) scaling += .01f * speed;
            if (scaling > 1) scaling = 1f;
        }
    }

    public void queueSetModule(Module module) {
        this.queueModule = module;
        setHoverHighlightVisible(true);
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }
}
