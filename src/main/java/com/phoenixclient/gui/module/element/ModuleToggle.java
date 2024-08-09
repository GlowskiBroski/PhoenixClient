package com.phoenixclient.gui.module.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.element.GuiToggle;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

//TODO: Make buttons smaller once more modules are added

public class ModuleToggle extends GuiToggle {

    private final Module module;

    public boolean selectedSettings;
    public int selectionFade = 200;

    public ModuleToggle(Screen screen, Module module, Vector pos, Vector size, ColorManager colorManager) {
        super(screen,module.getTitle(),(SettingGUI<Boolean>) module.getEnabledContainer(),pos,size,colorManager);
        this.module = module;
        this.selectedSettings = false;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {

        //Tooltip Override
        setTooltipVisible(false);
        if (isMouseOver()) hoverWatch.start();
        else hoverWatch.stop();

        //super.drawWidget(graphics,mousePos);

        //*
        //Draw Background (THIS IS DIFFERENT, THEREFORE WE IGNORE THE SUPER CALL)
        Color bgc = colorManager.getBackgroundColor();
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(bgc.getRed()/2,bgc.getGreen()/2,bgc.getBlue()/2,100));

        //Draw Fill
        Color wColor = colorManager.getWidgetColor();
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(wColor.getRed(),wColor.getGreen(),wColor.getBlue(),(int) toggleFade));

        //Draw Text
        double scale = 1;
        if (DrawUtil.getFontTextWidth(getTitle()) > getSize().getX() - 2) scale = (getSize().getX() - 2)/(DrawUtil.getFontTextWidth(getTitle()) + 2);

        TextBuilder.start(getTitle(),getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE).scale((float)scale).draw(graphics);

         //*/
        //Draw Selection Blip
        if (selectedSettings) DrawUtil.drawRectangleRound(graphics,pos.get(),size.get(),new Color(255,255,255, MathUtil.getBoundValue(selectionFade,0,255).intValue()));

    }


    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (isMouseOver() && state == 1) {
            if (Key.KEY_LSHIFT.isKeyDown() || button == 1) {
                selectedSettings = true;
                PhoenixClient.getGuiManager().getModuleGui().moduleOptionsMenu.queueSetModule(getModule());
            } else {
                if (button == 0) getModule().toggle();
            }
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        if (selectionFade <= 0) {
            selectedSettings = false;
            selectionFade = 200;
        }
        if (selectedSettings) selectionFade -= speed;
    }

    public Module getModule() {
        return module;
    }
}
