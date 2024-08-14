package com.phoenixclient.gui;

import com.phoenixclient.event.Event;
import com.phoenixclient.module.Module;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
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


    public final SettingGUI<Integer> maximumNotifications = new SettingGUI<>(
            this,
            "Maximum Size",
            "The maximum notifications allowed in the list. If notification exceed, the top ones will be removed",
            5).setSliderData(0,10,1);

    public NotificationManager() {
        super("Notifications", "Notification manager for PhoenixClient. Disable to disable all notifications", Category.MANAGERS, true, -1);
        addSettings(enableDisable,maximumNotifications);
        addEventSubscriber(Event.EVENT_RENDER_HUD, this::renderHud);
    }

    @Override
    public boolean showInList() {
        return false;
    }

    private void renderHud(Event event) {
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

        int removals = Math.clamp(notificationList.size() - maximumNotifications.get(),0,notificationList.size());
        for (int i = 0; i < removals; i++) {
            notificationRemovalQueue.add(notificationList.get(i));
        }

        for (Notification notification : notificationRemovalQueue) notificationList.remove(notification);

        int yOff = 2;
        for (Notification notification : notificationList) {
            double size = DrawUtil.getDefaultTextWidth(notification.text);
            Vector pos = new Vector(MC.getWindow().getGuiScaledWidth() / 2 - size / 2, yOff);
            DrawUtil.drawRectangleRound(graphics, pos.getSubtracted(2,2), new Vector(size + 4,12), new Color(0, 0, 0,(int)Math.clamp(notification.fade,0,175)));
            DrawUtil.drawRectangleRound(graphics, pos.getSubtracted(2,2), new Vector(size + 4,12), new Color(150, 150, 150, (int)Math.clamp(notification.fade,0,175)), true);
            TextBuilder.start().text(notification.text).pos(new Vector(0, yOff)).centerX().color(notification.getFadeColor()).draw(graphics);
            notification.updateFadeOut();
            yOff += 12;
            if (notification.fade <= 0) notificationRemovalQueue.add(notification);
        }
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