package com.phoenixclient.gui.hud.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.input.Mouse;
import com.phoenixclient.util.interfaces.IToggleableEventSubscriber;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.interfaces.ISettingParent;
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

import static com.phoenixclient.PhoenixClient.MC;

//TODO: This class needs to be cleaned. It is messy

public abstract class GuiWindow extends GuiWidget implements IToggleableEventSubscriber, ISettingParent {

    private final ArrayList<EventAction> eventActionList = new ArrayList<>();
    private final ArrayList<SettingGUI<?>> settingList = new ArrayList<>();

    private final String title;
    private final String description;

    private final Container<Boolean> enabled;

    protected Container<Boolean> pinned;
    protected  Container<Vector> posScale;
    private final Container<String> anchorX;
    private final Container<String> anchorY;
    protected Container<Boolean> drawBackground;

    private SettingsWindow settingsWindow;
    private boolean settingsOpen;

    private boolean dragging;
    private Vector dragOffset;

    private double pinFade;

    protected final OnChange<Double> onWidthChange = new OnChange<>();
    protected final OnChange<Double> onHeightChange = new OnChange<>();

    public GuiWindow(Screen screen, String title, String description, Vector size, boolean defaultEnabled) {
        super(screen, Vector.NULL(), size);
        this.title = title;
        this.description = description;
        this.settingsOpen = false;

        this.dragOffset = Vector.NULL();

        this.pinFade = 0;

        boolean defaultPinned = false;
        Vector defaultPosScale = new Vector(.1,.1); //Start windows at 10% of the screens height/width
        String defaultAnchorX = "NONE";
        String defaultAnchorY = "NONE";
        boolean defaultDrawBackground = true;

        if (isSettingsWindow()) {
            this.pinned = new Container<>(defaultPinned);
            this.posScale = new Container<>(defaultPosScale);
            this.anchorX = new Container<>(defaultAnchorX);
            this.anchorY = new Container<>(defaultAnchorY);
            this.drawBackground = new Container<>(false);
            this.enabled = new Container<>(true);
        } else {
            SettingManager manager = PhoenixClient.getSettingManager();
            this.pinned = new Setting<>(manager, title + "_pinned", defaultPinned);
            this.posScale = new Setting<>(manager, title + "_posScale", defaultPosScale);
            this.anchorX = new Setting<>(manager, title + "_anchorX", defaultAnchorX);
            this.anchorY = new Setting<>(manager, title + "_anchorY", defaultAnchorY);

            this.enabled = new SettingGUI<>(this,"enabled","Is the window enabled",defaultEnabled);
            this.drawBackground = new SettingGUI<>(this, "Draw Background", "Draws a dark background around a HUD element", defaultDrawBackground);
            addSettings((SettingGUI<?>) drawBackground);
        }
    }

    @Override
    public Container<Boolean> getEnabledContainer() {
        return enabled;
    }

    protected abstract void drawWindow(GuiGraphics graphics, Vector mousePos);

    @Override
    public void draw(GuiGraphics graphics, Vector mousePos) {
        if (!isEnabled()) return;
        super.draw(graphics, mousePos);
    }

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
        if (!isEnabled()) return;
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
        if (!isEnabled()) return;
        if (settingsOpen) settingsWindow.keyPressed(key, scancode, modifiers);
    }

    @Override
    public void runAnimation(int speed) {
        if (!isEnabled()) return;
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
            case "R" -> DrawUtil.drawRectangle(graphics, getPos().getAdded(getSize().getX() - 1, 0), new Vector(1, getSize().getY()), Color.RED);
        }

        switch (anchorY.get()) {
            case "U" -> DrawUtil.drawRectangle(graphics, getPos(), new Vector(getSize().getX(), 1), Color.RED);
            case "D" -> DrawUtil.drawRectangle(graphics, getPos().getAdded(0, getSize().getY() - 1), new Vector(getSize().getX(), 1), Color.RED);
        }
    }

    private void drawPin(GuiGraphics graphics, Vector mousePos) {
        if (MC.screen == PhoenixClient.getGuiManager().getHudGui() && shouldDrawPin()) {
            Color pinColor = ColorManager.getRedGreenScaledColor(pinFade);
            DrawUtil.drawRectangleRound(graphics, getPos(), new Vector(10, 10), new Color(pinColor.getRed(), pinColor.getGreen(), pinColor.getBlue(), 125));
        }
    }


    public void openSettingWindow() {
        this.settingsWindow = new SettingsWindow(getScreen(), this) {
            @Override
            public boolean shouldDrawPin() {
                return false;
            }
        };
        this.settingsWindow.setPos(new Vector(getScreen().width / 2f - 100 / 2f, getScreen().height / 2f - 100 / 2f));
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

    //Interface Getters

    @Override
    public String getKey() {
        return getTitle();
    }

    @Override
    public ArrayList<EventAction> getEventActions() {
        return eventActionList;
    }

    @Override
    public ArrayList<SettingGUI<?>> getSettings() {
        return settingList;
    }

    @Override
    public void onEnabled() {}

    @Override
    public void onDisabled() {
        this.pinned.set(false);
        this.posScale.set(new Vector(.1,.1));
        this.anchorX.set("NONE");
        this.anchorY.set("NONE");
        this.setSettingsOpen(false);
    }

    //Native Getters

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

}