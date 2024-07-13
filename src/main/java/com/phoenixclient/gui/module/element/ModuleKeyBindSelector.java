package com.phoenixclient.gui.module.element;

import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;

import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ModuleKeyBindSelector extends GuiWidget {

    private Module module;

    private Color color;

    private boolean accepting;

    public ModuleKeyBindSelector(Screen screen, Module module, Vector pos, Vector size, Color color) {
        super(screen,pos,size);
        this.module = module;
        this.color = color;
        this.accepting = false;
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), BGC);

        //Draw Text
        double scale = 1;
        String msg = "KeyBind: ";
        if (accepting) {
            msg = "Press a key...";
        } else {
            try {
                msg = msg.concat(getModule().getKeyBind() == -1 ? "NONE" : GLFW.glfwGetKeyName(getModule().getKeyBind(), -1).toUpperCase());
            } catch (NullPointerException e) {
                msg = msg.concat("Bound (Null Key Name)");
            }
        }

        if (DrawUtil.getFontTextWidth(msg) + 2 > getSize().getX()) scale = getSize().getX()/(DrawUtil.getFontTextWidth(msg) + 2);
        DrawUtil.drawFontText(graphics, msg, getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE,true,(float)scale);
    }


    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (isMouseOver() && state == 1) {
            if (button == 0) {
                accepting = !accepting;
            }
            if (!accepting || button == 1) {
                accepting = false;
                getModule().setKeyBind(-1);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        if (accepting) {
            getModule().setKeyBind(key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE ? -1 : key);
            accepting = false;
        }
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        //TODO: Add a fade animation of select key out, and the new keybind name in
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setModule(Module module) {
        this.module = module;
        accepting = false;
    }


    public Color getColor() {
        return color;
    }

    public Module getModule() {
        return module;
    }
}
