package com.phoenixclient.gui;

import com.phoenixclient.event.Event;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.util.ArrayList;

import static com.phoenixclient.PhoenixClient.MC;

public class NotificationManager extends Module {

    private final ArrayList<Notification> notificationList = new ArrayList<>();
    private final ArrayList<Notification> notificationRemovalQueue = new ArrayList<>();

    public final SettingGUI<Boolean> enableDisable = new SettingGUI<>(
            this,
            "Module Status",
            "Shows module enable/disable notifications",
            false); //TODO: Maybe make this true? depending on if you like it or not

    public NotificationManager() {
        super("Notifications", "GUI manager for Phoenix Client", Category.RENDER, true, -1);
        addSettings(enableDisable);
        addEventSubscriber(Event.EVENT_RENDER_HUD, this::renderHud);
    }

    @Override
    public boolean showInList() {
        return false;
    }

    private void renderHud(Event event) {
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

        int yOff = 2;
        for (Notification notification : notificationList) {
            TextBuilder.start().text(notification.text).pos(new Vector(0, yOff)).centerX().color(notification.getFadeColor()).draw(graphics);
            notification.updateFadeOut();
            yOff += 12;
            if (notification.fade <= 0) notificationRemovalQueue.add(notification);
        }

        for (Notification notification : notificationRemovalQueue) notificationList.remove(notification);
    }


    public void sendNotification(String text, Color color, float fadeSpeed) {
        notificationList.add(new Notification(text, color, fadeSpeed, 255));
    }

    public void sendNotification(String text, Color color) {
        sendNotification(text,color,1);
    }


    private static class Notification {
        public final String text;
        public final Color color;
        public final float fadeSpeed;

        public float fade;

        public Notification(String text, Color color, float fadeSpeed, int fade) {
            this.text = text;
            this.color = color;
            this.fade = fade;
            this.fadeSpeed = fadeSpeed;
        }

        public void updateFadeOut() {
            fade = Math.clamp(fade - fadeSpeed, 0, 255);
        }

        public Color getFadeColor() {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) fade);
        }
    }

}