package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.setting.ISettingParent;
import com.phoenixclient.util.input.Key;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.gui.element.GuiWidget;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.Container;
import com.phoenixclient.util.setting.Setting;
import com.phoenixclient.util.setting.SettingManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static com.phoenixclient.PhoenixClient.MC;


public abstract class GuiWindow extends GuiWidget implements ISettingParent {

    private final ArrayList<SettingGUI<?>> settingList;

    private final String title;

    private final Container<Boolean> pinned;
    protected final Container<Vector> posScale;
    private final Container<String> anchorX;
    private final Container<String> anchorY;

    private final Container<Boolean> drawBackground;

    private boolean dragging;
    private Vector dragOffset;

    private double pinFade;

    private SettingsWindow settingsWindow;
    private boolean settingsOpen;

    protected final OnChange<Double> onWidthChange = new OnChange<>();
    protected final OnChange<Double> onHeightChange = new OnChange<>();

    public GuiWindow(Screen screen, String title, Vector pos, Vector size) {
        super(screen, pos, size);
        this.title = title;
        this.settingList = new ArrayList<>();
        this.settingsOpen = false;

        this.dragOffset = Vector.NULL();

        this.pinFade = 0;

        if (this instanceof SettingsWindow) {
            this.pinned = new Container<>(false);
            this.posScale = new Container<>(new Vector(.1, .1));
            this.anchorX = new Container<>("NONE");
            this.anchorY = new Container<>("NONE");
            this.drawBackground = new Container<>(false);
        } else {
            SettingManager manager = PhoenixClient.getSettingManager();
            this.pinned = new Setting<>(manager, title + "_pinned", false);
            this.posScale = new Setting<>(manager, title + "_posScale", new Vector(.1, .1)); //this.pos = new Setting<>(manager, title + "_pos",pos);
            this.anchorX = new Setting<>(manager, title + "_anchorX", "NONE");
            this.anchorY = new Setting<>(manager, title + "_anchorY", "NONE");

            this.drawBackground = new SettingGUI<>(this, "Draw Background", "Draws a dark background around a HUD element", false);
            addSettings((SettingGUI<?>) drawBackground);

            //TODO: Add a setting for each window to the GUI manager
            //PhoenixClient.getGuiManager().addSettings();
        }
    }

    protected abstract void drawWindow(GuiGraphics graphics, Vector mousePos);

    @Override
    public void drawWidget(GuiGraphics graphics, Vector mousePos) {
        if (!isSettingsWindow()) {
            updateWindowCoordinatesFromScale();
            updateAnchoredCoordinates();
        }

        boolean isHudGui = MC.screen == PhoenixClient.getGuiManager().getHudGui();

        if (isHudGui && dragging) {
            setPos(mousePos.getAdded(dragOffset));
            //Scale to Corner
            posScale.set(getPos().getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 / MC.getWindow().getGuiScaledHeight()));

            //Scale to Center (Feels a little more inaccurate, will use top left corner instead)
            //posScale.set(getPos().getAdded(getSize().getMultiplied(.5)).getScaled((double) 1 / MC.getWindow().getGuiScaledWidth(), (double) 1 /MC.getWindow().getGuiScaledHeight()));
        }

        if (isHudGui) bindWindowCoordinates();

        if (drawBackground.get()) {
            DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(0, 0, 0, 175));
            DrawUtil.drawRectangleRound(graphics, getPos(), getSize(), new Color(150, 150, 150, 175), true);
        }

        if (isHudGui) drawAnchoredLines(graphics, mousePos);

        drawWindow(graphics, mousePos);
        drawPin(graphics, mousePos);
        if (settingsOpen) settingsWindow.draw(graphics, mousePos);

        updateWindowPositionFromSize();
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (settingsOpen) settingsWindow.mousePressed(button, state, mousePos);

        if (state == Mouse.ACTION_CLICK && isMouseOver()) {
            switch (button) {
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                    updateDragState(button, state, mousePos);

                    //NOTE: If the window is not focused, it will not be able to detect if shift is down
                    if (!settingsOpen && Key.KEY_LSHIFT.isKeyDown() && !isSettingsWindow())
                        openSettingWindow();
                }
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> pinned.set(!pinned.get());
            }
        }

        if (button == Mouse.BUTTON_LEFT.getId() && !Key.KEY_LSHIFT.isKeyDown())
            updateAnchored(button, state, mousePos);

        if (dragging && state == Mouse.ACTION_RELEASE) dragging = false;
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        if (settingsOpen) settingsWindow.keyPressed(key, scancode, modifiers);
    }

    @Override
    public void runAnimation(int speed) {
        super.runAnimation(speed);
        if (settingsOpen) settingsWindow.runAnimation(speed);
        if (isPinned()) {
            if (pinFade < 1) pinFade += .01 * speed;
            else pinFade = 1;
        } else {
            if (pinFade > 0) pinFade -= .01 * speed;
            else pinFade = 0;
        }
    }

    private void updateDragState(int button, int state, Vector mousePos) {
        dragOffset = getPos().getAdded(mousePos.getMultiplied(-1));
        dragging = !Key.KEY_LSHIFT.isKeyDown() && isMouseOver();
    }

    private void updateWindowCoordinatesFromScale() {
        //Scale to Corner
        setPos(new Vector(MC.getWindow().getGuiScaledWidth(), MC.getWindow().getGuiScaledHeight()).getScaled(posScale.get()));

        //Scale to Center
        //Vector oldCenter = getPos().getAdded(getSize().getMultiplied(.5f));
        //Vector newCenter = new Vector(MC.getWindow().getGuiScaledWidth(),MC.getWindow().getGuiScaledHeight()).getScaled(posScale.get());
        //setPos(getPos().getAdded(newCenter.getSubtracted(oldCenter)));
    }

    private void bindWindowCoordinates() {
        if (getPos().getX() < 0)
            setPos(new Vector(0, getPos().getY()));

        if (getPos().getY() < 0)
            setPos(new Vector(getPos().getX(), 0));

        if (getPos().getX() + getSize().getX() > getScreen().width)
            setPos(new Vector(getScreen().width - getSize().getX(), getPos().getY()));

        if (getPos().getY() + getSize().getY() > getScreen().height)
            setPos(new Vector(getPos().getX(), getScreen().height - getSize().getY()));
    }

    private void updateAnchored(int button, int state, Vector mousePos) {
        if (isSettingsWindow() || (isMouseOver() && state == Mouse.ACTION_CLICK)) {
            anchorX.set("NONE");
            anchorY.set("NONE");
        } else if (dragging) {

            if (getPos().getX() <= 0)
                anchorX.set("L");
            else if (getPos().getX() + getSize().getX() >= getScreen().width)
                anchorX.set("R");

            if (getPos().getY() <= 0)
                anchorY.set("U");
            else if (getPos().getY() + getSize().getY() >= getScreen().height)
                anchorY.set("D");

        }
    }

    //This method will also be called in the set position method every time the size is changed
    private void updateAnchoredCoordinates() {
        switch (anchorX.get()) {
            case "L" -> setPos(new Vector(0, getPos().getY()));
            case "R" -> setPos(new Vector(MC.getWindow().getGuiScaledWidth() - getSize().getX(), getPos().getY()));
        }

        switch (anchorY.get()) {
            case "U" -> setPos(new Vector(getPos().getX(), 0));
            case "D" -> setPos(new Vector(getPos().getX(), MC.getWindow().getGuiScaledHeight() - getSize().getY()));
        }
    }

    //If a mode requires to move the window's position from a size change, do that here
    protected void updateWindowPositionFromSize() {
    }

    private void drawAnchoredLines(GuiGraphics graphics, Vector mousePos) {
        switch (anchorX.get()) {
            case "L" -> DrawUtil.drawRectangle(graphics, getPos(), new Vector(1, getSize().getY()), Color.RED);
            case "R" ->
                    DrawUtil.drawRectangle(graphics, getPos().getAdded(getSize().getX() - 1, 0), new Vector(1, getSize().getY()), Color.RED);
        }

        switch (anchorY.get()) {
            case "U" -> DrawUtil.drawRectangle(graphics, getPos(), new Vector(getSize().getX(), 1), Color.RED);
            case "D" ->
                    DrawUtil.drawRectangle(graphics, getPos().getAdded(0, getSize().getY() - 1), new Vector(getSize().getX(), 1), Color.RED);
        }
    }

    private void drawPin(GuiGraphics graphics, Vector mousePos) {
        if (MC.screen == PhoenixClient.getGuiManager().getHudGui() && shouldDrawPin()) {
            Color pinColor = ColorManager.getRedGreenScaledColor(pinFade);
            DrawUtil.drawRectangleRound(graphics, getPos(), new Vector(10, 10), new Color(pinColor.getRed(), pinColor.getGreen(), pinColor.getBlue(), 125));
        }
    }

    private void openSettingWindow() {
        this.settingsWindow = new SettingsWindow(getScreen(), this, new Vector(getScreen().width / 2f - 100 / 2f, getScreen().height / 2f - 100 / 2f)) {
            @Override
            public boolean shouldDrawPin() {
                return false;
            }
        };
        setSettingsOpen(true);
    }

    public void setSettingsOpen(boolean settingsOpen) {
        this.settingsOpen = settingsOpen;
        if (!settingsOpen && settingsWindow != null) this.settingsWindow = null;
    }

    @Override
    public void setSize(Vector vector) {
        super.setSize(vector);
        updateAnchoredCoordinates();
    }


    public String getTitle() {
        return title;
    }

    public boolean isPinned() {
        return pinned.get();
    }

    protected boolean shouldDrawPin() {
        return true;
    }

    private boolean isSettingsWindow() {
        return this instanceof SettingsWindow;
    }


    protected void addSettings(SettingGUI<?>... settings) {
        settingList.addAll(Arrays.asList(settings));
    }

    protected ArrayList<SettingGUI<?>> getSettings() {
        return settingList;
    }

}