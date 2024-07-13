package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.Packet;

import java.awt.*;
import java.util.LinkedList;

//TODO: This window is not implemented. I plan to add it in the future
//TODO: Make this a list window
public class PacketInflowListWindow extends GuiWindow {

    LinkedList<Packet<?>> packetList = new LinkedList<>();

    private SettingGUI<String> mode;
    private final SettingGUI<Integer> history;

    public PacketInflowListWindow(Screen screen, Vector pos) {
        super(screen, "PacketInflowWindow", pos, new Vector(72,0));
        this.mode = new SettingGUI<>(this,"Mode","Type of packets to log","Inflow").setModeData("Inflow","Outflow");
        this.history = new SettingGUI<>(this,"History","Range of history to appear",20).setSliderData(1,200,1);
        addSettings(mode,history);
        packetEvent.subscribe();
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        int yOff = 0;
        for (Packet<?> packet : packetList) {
            DrawUtil.drawFontText(graphics, packet.type().id().toString(), getPos().getAdded(0, yOff), Color.WHITE);
            yOff += 12;
        }
        setSize(new Vector(72, yOff + 12));
    }


    public EventAction packetEvent = new EventAction(Event.EVENT_PACKET, () -> {
        PacketEvent event = Event.EVENT_PACKET;
        switch (mode.get()) {
            case "Inflow" -> {
                if (event.getType().equals(PacketEvent.Type.RECEIVE)) {

                }
            }
            case "Outflow" -> {
                if (event.getType().equals(PacketEvent.Type.SEND)) {

                }
            }
        }
    });
}
