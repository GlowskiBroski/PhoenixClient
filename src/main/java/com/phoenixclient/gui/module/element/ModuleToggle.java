package com.phoenixclient.gui.module.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

//TODO: Make buttons smaller once more modules are added

public class ModuleToggle extends GuiWidget {

    private final String title;

    private final Module module;

    private Color color;

    protected float toggleFade;
    public boolean selectedSettings;

    public ModuleToggle(Screen screen, Module module, Vector pos, Vector size, Color color) {
        super(screen,pos,size);
        this.module = module;
        this.title = module.getTitle();
        this.color = color;
        this.toggleFade = 0;
        this.selectedSettings = false;
    }

    public int selectionFade = 200;

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        Color bgc = BGC;
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(bgc.getRed()/2,bgc.getGreen()/2,bgc.getBlue()/2,100));

        //Draw Fill
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(getColor().getRed(),getColor().getGreen(),getColor().getBlue(),(int) toggleFade));

        //Draw Text
        double scale = 1;
        if (DrawUtil.getFontTextWidth(getTitle()) + 2 > getSize().getX()) scale = getSize().getX()/(DrawUtil.getFontTextWidth(getTitle()) + 2);
        DrawUtil.drawFontText(graphics, getTitle(), getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE,true,(float)scale);

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
        toggleFade = getNextFade(module.isEnabled(), toggleFade, 0, 150, 2f * speed);
        if (selectionFade <= 0) {
            selectedSettings = false;
            selectionFade = 200;
        }
        if (selectedSettings) selectionFade -= speed;
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

    public Module getModule() {
        return module;
    }
}
