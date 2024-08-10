package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.Packet;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PacketFlowListWindow extends ListWindow {

    private final LinkedHashMap<Packet<?>, StopWatch> packetList = new LinkedHashMap<>();
    private LinkedHashMap<Packet<?>, StopWatch> prevPacketList = new LinkedHashMap<>();

    private final SettingGUI<String> mode;
    private final SettingGUI<Integer> history;

    public PacketFlowListWindow(Screen screen) {
        super(screen, "PacketInflowWindow", "Shows all packets send/received, with their counts, from the last time period", false);
        this.mode = new SettingGUI<>(this, "Mode", "Type of packets to log", "Inflow").setModeData("Inflow", "Outflow");
        this.history = new SettingGUI<>(this, "History", "The amount of time a packet will stay on the window after being sent", 5).setSliderData(1, 60, 1);
        addSettings(mode, history);
        addEventSubscriber(Event.EVENT_PACKET, this::onPacket); //TODO: Since this comes before modules, cancelled packets are still shown on screen
    }

    @Override
    protected String getLabel() {
        return "Packet Flow";
    }

    @Override
    protected LinkedHashMap<String, ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();

        try {
            for (Packet<?> packet : packetList.keySet()) {
                String rawName = packet.type().toString();
                String packetName = rawName;
                if (packetList.get(packet).hasTimePassedS(history.get())) {
                    this.packetList.remove(packet);
                    continue;
                }

                if (currentList.containsKey(packetName)) {
                    ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(packetName).tag().replace("(", "").replace(")", "")) + 1) + ")", Color.WHITE, Color.CYAN);
                    currentList.put(packetName, count);
                    continue;
                }
                currentList.putIfAbsent(packetName, new ListInfo("(1)", Color.WHITE, Color.CYAN));
            }
        } catch (ConcurrentModificationException e) {

        }
        this.prevPacketList = packetList;
        return forceAddedToBottom(currentList);
    }

    //TODO: This kinda broke the packet event. Im guessing it caused exceptions in the thread
    public void onPacket(PacketEvent event) {
        synchronized (packetList) { //TODO: Look into synchronization
            StopWatch watch = new StopWatch();
            watch.start();
            switch (mode.get()) {
                case "Inflow" -> {
                    if (event.getType().equals(PacketEvent.Type.RECEIVE)) {
                        packetList.put(event.getPacket(), watch);
                    }
                }
                case "Outflow" -> {
                    if (event.getType().equals(PacketEvent.Type.SEND)) {
                        packetList.put(event.getPacket(), watch);
                    }
                }
            }
        }
    }

}
