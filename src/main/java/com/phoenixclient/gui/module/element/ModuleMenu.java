package com.phoenixclient.gui.module.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.gui.element.GuiButton;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.util.ArrayList;

import static com.phoenixclient.PhoenixClient.MC;

public class ModuleMenu extends GuiWidget {

    private final GuiButton mainButton;
    private final Setting<Boolean> open;
    private float scaling;

    private final ArrayList<ModuleToggle> buttonList;

    public ModuleMenu(Screen screen, Module.Category category, Vector pos) {
        super(screen, pos, new Vector(60, 50));
        this.scaling = 0;
        this.buttonList = new ArrayList<>();
        this.open = new Setting<>(PhoenixClient.getSettingManager(), category.getTitle() + "_open", true);
        this.mainButton = new GuiButton(getScreen(), category.getTitle(), getPos(), getSize(), colorManager, (f) -> open.set(!open.get())) {
            @Override
            protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
                DrawUtil.drawArrow(graphics, getPos(), (float) getSize().getY(), getColor(), false);
                DrawUtil.drawArrow(graphics, getPos().getAdded(getSize().getMultiplied(.25 / 1.5)).getAdded(2, 0), (float) getSize().getY() / 1.5f, colorManager.getDepthColor(), false);

                double scale = 1;
                if (DrawUtil.getFontTextWidth(getTitle()) > getSize().getX())
                    scale = getSize().getX() / DrawUtil.getFontTextWidth(getTitle());
                Vector pos = getPos().getAdded(new Vector(getSize().getX() / 2 - DrawUtil.getFontTextWidth(getTitle()) / 2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2));
                TextBuilder.start(getTitle(), pos.getAdded(7, 0), Color.WHITE).scale((float) scale).draw(graphics);
                //Arrow Hover Fade
                this.setHoverHighlightVisible(false);
                DrawUtil.drawArrow(graphics, getPos(), (float) getSize().getY(), new Color(255, 255, 255, (int) hoverFade), false);
            }
        };

        setHoverHighlightVisible(false);
        addToggles(category);
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Gui Scale Correction Begin
        float guiScaled = MC.getWindow().getGuiScale() >= 2 ? (float) (2 / MC.getWindow().getGuiScale()) : 1;
        graphics.pose().scale(guiScaled, guiScaled, 1f);
        mousePos = mousePos.getMultiplied(1 / guiScaled);

        float tempScaling = scaling; //Clones the scaling to prevent animation thread from de-syncing
        graphics.pose().translate((1 - tempScaling) * 10, 0, 0);
        graphics.pose().scale(tempScaling, 1f, 1f);

        //Window Background
        Vector backgroundPos = getPos().getAdded(getPos().getX() + getSize().getX() - 9, 3);
        Vector backgroundSize = new Vector((double) (62 * (buttonList.size() % 2 == 0 ? buttonList.size() : buttonList.size() + 1)) / 2 + 9 + 4, getSize().getY() - 6);
        ColorManager cm = colorManager;
        Color backgroundColor = new Color(cm.getDepthColor().getRed(), cm.getDepthColor().getGreen(), cm.getDepthColor().getBlue(), 220);
        //Color backgroundColor = new Color(0, 0, 0, 175);
        DrawUtil.drawRectangleRound(graphics, backgroundPos, backgroundSize, backgroundColor, 1.5, false);
        //DrawUtil.drawRectangleRound(graphics, backgroundPos, backgroundSize, colorManager.getBaseColor(), 1.5, true);
        DrawUtil.drawArrowHead(graphics, backgroundPos.getAdded(backgroundSize).y(getPos().getY() + 3), (float) getSize().getY() - 6, backgroundColor, false, false);

        //Buttons
        for (ModuleToggle toggle : buttonList) toggle.draw(graphics, mousePos);

        //Draw Button Tooltips
        for (ModuleToggle toggle : buttonList)
            toggle.drawTooltip(graphics, mousePos, toggle.getModule().getDescription());

        graphics.pose().scale(1 / tempScaling, 1f, 1f);
        graphics.pose().translate(-(1 - tempScaling) * 10, 0, 0);

        //Window Head
        setHoverHighlightVisible(false);
        mainButton.draw(graphics, mousePos);

        //Gui Scale Correction End
        graphics.pose().scale(1 / guiScaled, 1 / guiScaled, 1f);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        mainButton.runAnimation(speed);
        if (open.get()) {
            for (ModuleToggle toggle : buttonList) toggle.runAnimation(speed);
        }
        if (open.get() && scaling < 1) scaling += .005f * speed;
        if (!open.get() && scaling > .1f) scaling -= .005f * speed;
        if (scaling <= 0) scaling = .1f;
        if (scaling > 1) scaling = 1f;
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        mainButton.mousePressed(button, state, mousePos);
        if (open.get()) {
            for (ModuleToggle toggle : buttonList) {
                toggle.mousePressed(button, state, mousePos);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        mainButton.keyPressed(key, scancode, modifiers);
        if (open.get()) {
            for (ModuleToggle toggle : buttonList) {
                toggle.keyPressed(key, scancode, modifiers);
            }
        }
    }

    private void addToggles(Module.Category category) {
        boolean even;
        int i = 0;
        int j = 0;
        for (Module module : PhoenixClient.getModules()) {
            if (module.getCategory().equals(category)) {
                even = j % 2 == 0;
                buttonList.add(new ModuleToggle(getScreen(), module, getPos().getAdded(getPos().getX() + getSize().getX() + 4 + (62 * (even ? j - i : i)), even ? 5 : 26), new Vector(60, 19), colorManager));
                j++;
                if (!even) i++;
            }
        }
    }

    public void setScaling(float scaling) {
        this.scaling = scaling;
    }
}
