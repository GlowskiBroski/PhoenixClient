package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.gui.hud.HUDGUI;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;

import static com.phoenixclient.PhoenixClient.MC;

public class WindowToggleMenu extends GuiWidget {

    private final Key holdBind = Key.KEY_SPACE;

    private final ArrayList<WindowToggle> buttonList = new ArrayList<>();

    private int openFade = 0;

    public WindowToggleMenu(Screen screen, Vector pos, Vector size) {
        super(screen, pos, size);
        for (GuiWindow window : ((HUDGUI)screen).getWindows()) {
            buttonList.add(new WindowToggle(getScreen(),window,Vector.NULL(),Vector.NULL(),colorManager));
        }
    }

    @Override
    public void draw(GuiGraphics graphics, Vector mousePos) {
        //Need to override draw so that updateMouseOver is not called to protect underneath settings windows
        int speed = 64;
        openFade = Math.clamp(openFade + (holdBind.isKeyDown() ? speed : -speed),0,255);
        if (openFade <= 0) return;
        super.draw(graphics, mousePos);
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Setup window
        setSize(new Vector(266, 200));
        setPos(new Vector(MC.getWindow().getGuiScaledWidth() / 2 - getSize().getX() / 2, MC.getWindow().getGuiScaledHeight() / 2 - getSize().getY() / 2));
        setTooltipVisible(false);
        setHoverHighlightVisible(false);
        graphics.setColor(1, 1, 1, openFade / 255f);

        //Draw Main Background
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(0, 0, 0, 175));
        DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), colorManager.getBaseColor(), true);

        //Draw Title/Hint Background
        int border = 6;
        DrawUtil.drawRectangleRound(graphics,getPos().getAdded(border,5),getSize().getSubtracted(border * 2,0).y(44),new Color(50,50,50,175));

        //Draw Title
        TextBuilder.start().text("Windows").pos(getPos().getAdded(0,11)).centerX().color(Color.WHITE).draw(graphics);

        //Render Hint Text
        Color hintColor = Color.LIGHT_GRAY;
        String hint1 = "Toggle buttons to remove/add from editor view";
        String hint2 = "Right click windows to pin/unpin to HUD";
        TextBuilder.start().text(hint1).pos(getPos().getAdded(getSize().getX() / 2 - DrawUtil.getFontTextWidth(hint1) / 2,23)).color(hintColor).draw(graphics);
        TextBuilder.start().text(hint2).pos(getPos().getAdded(getSize().getX() / 2 - DrawUtil.getFontTextWidth(hint2) / 2,36)).color(hintColor).draw(graphics);

        renderToggles(graphics,mousePos);

        //Draw window tooltips
        for (WindowToggle tog : buttonList) {
            if (tog.isMouseOver()) tog.hoverWatch.start();
            else tog.hoverWatch.stop();
            tog.drawTooltip(graphics,mousePos,tog.getWindow().getDescription());
        }

    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (openFade <= 0) return;
        for (WindowToggle w : buttonList) w.mousePressed(button,state,mousePos);
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        if (openFade <= 0) return;
        for (WindowToggle w : buttonList) w.keyPressed(key, scancode, modifiers);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        for (WindowToggle w : buttonList) w.runAnimation(speed);
    }

    private void renderToggles(GuiGraphics graphics, Vector mousePos) {
        int y = 54;
        int x = 4;
        for (WindowToggle tog : buttonList) {
            tog.setTooltipVisible(false);
            tog.setSize(new Vector(60, 20));
            tog.setPos(getPos().getAdded(x, y));
            graphics.setColor(1, 1, 1, openFade / 255f);
            tog.draw(graphics, mousePos);
            x += 66;
            if (x >= 66 * 4 + 4) {
                x = 4;
                y += 26;
            }
        }
    }
}
