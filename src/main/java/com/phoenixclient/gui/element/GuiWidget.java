package com.phoenixclient.gui.element;

import com.phoenixclient.gui.GUI;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorUtil;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.Container;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public abstract class GuiWidget {

    protected static final Color BGC = ColorUtil.getTheme().getBackgroundColor();

    private final Screen screen;

    protected Container<Vector> pos;
    protected Container<Vector> size;

    private boolean mouseOver;

    private boolean drawTooltip;
    private boolean drawHoverHighlight;

    protected float hoverFade;

    public GuiWidget(Screen screen, Vector pos, Vector size) {
        this.screen = screen;
        this.pos = new Container<>(pos);
        this.size = new Container<>(size);
        this.drawTooltip = true;
        this.drawHoverHighlight = true;
        this.hoverFade = 0;
    }

    public void draw(GuiGraphics graphics, Vector mousePos) {
        this.mouseOver = updateMouseOver(mousePos);

        drawWidget(graphics, mousePos);

        if (drawHoverHighlight) drawHoverHighlight(graphics, mousePos);
        if (drawTooltip) drawTooltip(graphics,mousePos);
    }


    protected abstract void drawWidget(GuiGraphics graphics, Vector mousePos);

    public abstract void mousePressed(int button, int state, Vector mousePos);

    public abstract void keyPressed(int key, int scancode, int modifiers);


    public void runAnimation(int speed) {
        hoverFade = getNextFade(isMouseOver(), hoverFade, 0, 50, speed);
    }

    protected boolean updateMouseOver(Vector mousePos) {
        boolean mouseOverX = mousePos.getX() >= getPos().getX() && mousePos.getX() <= getPos().getX() + getSize().getX();
        boolean mouseOverY = mousePos.getY() >= getPos().getY() && mousePos.getY() <= getPos().getY() + getSize().getY();
        if (mouseOverX && mouseOverY) {
            if (getScreen() instanceof GUI gui) {
                gui.hoveredElements.add(this);
                return mouseOver = (!gui.prevHoveredElements.isEmpty() && gui.prevHoveredElements.getLast() == this);
            }
        }
        return mouseOver = false;
    }


    private void drawHoverHighlight(GuiGraphics graphics, Vector mousePos) {
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(255, 255, 255, (int) hoverFade));
    }

    private void drawTooltip(GuiGraphics graphics, Vector mousePos) {
        if (hasSetting() && isMouseOver()) {
            Vector pos = mousePos.getAdded(6, -8).clone();
            if (pos.getX() + DrawUtil.getFontTextWidth(getSetting().getDescription()) + 2 > MC.getWindow().getGuiScaledWidth())
                pos.setX(MC.getWindow().getGuiScaledWidth() - DrawUtil.getFontTextWidth(getSetting().getDescription()) - 2);

            DrawUtil.drawRectangleRound(graphics, pos, new Vector(DrawUtil.getFontTextWidth(getSetting().getDescription()) + 4, DrawUtil.getFontTextHeight() + 3), new Color(BGC.getRed(), BGC.getGreen(), BGC.getBlue(), BGC.getAlpha() * 2 / 3));
            DrawUtil.drawFontText(graphics, getSetting().getDescription(), pos.getAdded(2, 2), Color.WHITE);
        }
    }


    protected static float getNextFade(boolean condition, float fade, int min, int max, float speed) {
        if (condition) {
            if (fade < max) fade += speed;
        } else {
            if (fade > min) fade -= speed;
        }
        return MathUtil.getBoundValue(fade,0,255).floatValue();
    }


    public void setPos(Vector vector) {
        pos.set(vector);
    }

    public void setSize(Vector vector) {
        this.size.set(vector);
    }

    public void setHoverHighlightVisible(boolean drawHoverHighlight) {
        this.drawHoverHighlight = drawHoverHighlight;
    }

    public void setTooltipVisible(boolean drawTooltip) {
        this.drawTooltip = drawTooltip;
    }


    public Vector getPos() {
        return pos.get();
    }

    public Vector getSize() {
        return size.get();
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public Screen getScreen() {
        return screen;
    }

    public boolean shouldDrawSetting() {
        if (!hasSetting()) return true;
        if (hasSetting() && getSetting().getDependency() == null) return true;
        return (getSetting().getDependency().setting().get().equals(getSetting().getDependency().value()));
    }

    public boolean hasSetting() {
        return getSetting() != null;
    }

    public SettingGUI<?> getSetting() {
        return null;
    }

}
