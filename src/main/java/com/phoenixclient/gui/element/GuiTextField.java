package com.phoenixclient.gui.element;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.setting.SettingGUI;
import com.phoenixclient.util.setting.Setting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class GuiTextField extends GuiWidget {

    private final SettingGUI<String> setting;
    private final String title;

    private final StopWatch cursorTimer = new StopWatch();
    private boolean typing;

    private GuiTextField(Screen screen, String title, SettingGUI<String> setting, Vector pos, Vector size) {
        super(screen, pos, size);
        this.setting = setting;
        this.title = title;

        this.typing = false;
    }

    public GuiTextField(Screen screen, SettingGUI<String> setting, Vector pos, Vector size) {
        this(screen,setting.getName(),setting,pos,size);
    }

    @Override
    protected void drawWidget(GuiGraphics graphics, Vector mousePos) {
        //Draw Background
        DrawUtil.drawRectangleRound(graphics,getPos(), getSize(), colorManager.getBackgroundColor());

        //Draw Underline
        DrawUtil.drawRectangle(graphics,
                getPos().getAdded(new Vector(2 + DrawUtil.getFontTextWidth(getTitle() + ": "), getSize().getY()- 2)),
                getSize().getAdded(new Vector(- 4 - DrawUtil.getFontTextWidth(getTitle() + ": "),1-getSize().getY())),
                Color.WHITE);

        String title = getTitle() + ": ";
        String msg = getSetting().get();

        //Draw Blinking Cursor
        if (typing) {
            cursorTimer.start();
            if (cursorTimer.hasTimePassedS(1)) cursorTimer.restart();
            int alpha = cursorTimer.hasTimePassedMS(500) ? 255 : 0;
            double x = (DrawUtil.getFontTextWidth(title.concat(msg)) > getSize().getX() - 4)
                    ? getPos().getX() + getSize().getX() - 1
                    : (getPos().getX() + 3 + DrawUtil.getFontTextWidth(getTitle() + ": ") + DrawUtil.getFontTextWidth(getSetting().get()));
            DrawUtil.drawRectangle(graphics, new Vector(x,getPos().getY() + 3),new Vector(1,getSize().getY() - 6),new Color(255,255,255,alpha));
        }

        //Draw Text
        double scale = 1;
        if (DrawUtil.getFontTextWidth(title.concat(msg)) > getSize().getX() - 2) scale = (getSize().getX() - 2 - DrawUtil.getFontTextWidth(title))/(DrawUtil.getFontTextWidth(msg));
        DrawUtil.drawDualColorFontText(graphics,title,msg,getPos().getAdded(new Vector(2, 1 + getSize().getY() / 2 - DrawUtil.getFontTextHeight() / 2)), Color.WHITE,typing ? Color.GREEN : Color.WHITE,true,1,(float)scale,true);
    }

    @Override
    public void mousePressed(int button, int state, Vector mousePos) {
        if (button == 0 && state == 1) {
            if (typing) typing = false;
            if (isMouseOver()) typing = true;
        }
    }

    @Override
    public void keyPressed(int key, int scancode, int modifiers) {
        String buttonText = getSetting().get();
        if (!typing) return;
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_KEY_ENTER -> typing = false;

            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (!buttonText.isEmpty()) buttonText = buttonText.substring(0, buttonText.length() - 1);
            }

            case GLFW.GLFW_KEY_SPACE -> {
                if (isValidPress(getSetting(), key)) buttonText = buttonText + " ";
            }

            default -> {
                if (GLFW.glfwGetKeyName(key, -1) == null || GLFW.glfwGetKeyName(key, -1).equals("null")) break;
                if (isValidPress(getSetting(), key)) buttonText = buttonText + GLFW.glfwGetKeyName(key, -1);
            }
        }
        getSetting().set(buttonText);
    }

    public static boolean isValidPress(Setting<?> setting, int key) {
        if (setting instanceof SettingGUI<?> settingw && settingw.isNumbersOnly()) {
            return key == GLFW.GLFW_KEY_PERIOD ||
                    key == GLFW.GLFW_KEY_KP_SUBTRACT ||
                    key == GLFW.GLFW_KEY_MINUS ||
                    key == GLFW.GLFW_KEY_0 ||
                    key == GLFW.GLFW_KEY_1 ||
                    key == GLFW.GLFW_KEY_2 ||
                    key == GLFW.GLFW_KEY_3 ||
                    key == GLFW.GLFW_KEY_4 ||
                    key == GLFW.GLFW_KEY_5 ||
                    key == GLFW.GLFW_KEY_6 ||
                    key == GLFW.GLFW_KEY_7 ||
                    key == GLFW.GLFW_KEY_8 ||
                    key == GLFW.GLFW_KEY_9 ||
                    key == GLFW.GLFW_KEY_KP_0 ||
                    key == GLFW.GLFW_KEY_KP_1 ||
                    key == GLFW.GLFW_KEY_KP_2 ||
                    key == GLFW.GLFW_KEY_KP_3 ||
                    key == GLFW.GLFW_KEY_KP_4 ||
                    key == GLFW.GLFW_KEY_KP_5 ||
                    key == GLFW.GLFW_KEY_KP_6 ||
                    key == GLFW.GLFW_KEY_KP_7 ||
                    key == GLFW.GLFW_KEY_KP_8 ||
                    key == GLFW.GLFW_KEY_KP_9;
        }
        return true;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public SettingGUI<String> getSetting() {
        return setting;
    }

}
