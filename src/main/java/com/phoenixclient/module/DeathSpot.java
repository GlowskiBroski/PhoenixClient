package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderLevelEvent;
import com.phoenixclient.util.ConsoleUtil;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.Draw3DUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.world.phys.AABB;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;


public class DeathSpot extends Module {

    private final SettingGUI<Boolean> waypoint = new SettingGUI<>(
            this,
            "Waypoint",
            "Spawns a waypoint to your last death location",
            true
    );

    private final SettingGUI<Boolean> chatMessage = new SettingGUI<>(
            this,
            "Chat",
            "Sends your coordinates to chat upon death",
            true
    );

    private final DoOnce doOnce = new DoOnce();

    private Vector previousDeathLocation = Vector.NULL();

    public DeathSpot() {
        super("DeathSpot", "Tells you your death location", Category.SERVER, false, -1);
        addSettings(waypoint, chatMessage);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_RENDER_LEVEL,this::onRender);
    }

    /*
    @Override
    public String getModTag() {
        String tag = "";
        if (chatMessage.get()) tag = tag.concat("C");
        if (chatMessage.get() && waypoint.get()) tag = tag.concat(", ");
        if (waypoint.get()) tag = tag.concat("W");
        return tag;
    }
    */

    public void onRender(RenderLevelEvent event) {
        if (MC.player == null) previousDeathLocation = Vector.NULL();
        if (waypoint.get() && !previousDeathLocation.equals(Vector.NULL()))
            Draw3DUtil.drawOutlineBox(event.getLevelPoseStack(),new AABB(0,0,0,.6,1.8,.6),previousDeathLocation,new Color(132, 0, 255, 255));
    }

    public void onPlayerUpdate(Event event) {
        if (MC.player.isAlive()) {
            doOnce.reset();
        } else {
            doOnce.run(() -> {
                Vector pos = (previousDeathLocation = new Vector(MC.player.getPosition(0)));
                if (chatMessage.get()) {
                    ConsoleUtil.sendMessage("Death Location: (" + Math.floor(pos.getX()) + ", " + Math.floor(pos.getY()) + ", " + Math.floor(pos.getZ()) + ")");
                }
            });
        }
    }




}
