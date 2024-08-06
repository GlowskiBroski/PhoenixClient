package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.Packet;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;

public class PacketFlowListWindow extends ListWindow {

    private final LinkedHashMap<Packet<?>, StopWatch> packetList = new LinkedHashMap<>();
    private LinkedHashMap<Packet<?>, StopWatch> prevPacketList = new LinkedHashMap<>();

    private SettingGUI<String> mode;
    private SettingGUI<Integer> history;

    public PacketFlowListWindow(Screen screen) {
        super(screen, "PacketInflowWindow", "Shows all packets send/received, with their counts, from the last time period",false);
        this.mode = new SettingGUI<>(this, "Mode", "Type of packets to log", "Inflow").setModeData("Inflow", "Outflow");
        this.history = new SettingGUI<>(this, "History", "The amount of time a packet will stay on the window after being sent", 5).setSliderData(1, 60, 1);
        addSettings(mode, history);
        //addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }

    @Override
    protected String getLabel() {
        return "Packet Flow";
    }

    @Override
    protected LinkedHashMap<String, ListInfo> getListMap() {
        LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();

        LinkedHashMap<Packet<?>, StopWatch> packetList;
        try {
            packetList = (LinkedHashMap<Packet<?>, StopWatch>) this.packetList.clone();
        } catch (ConcurrentModificationException e) {
            packetList = prevPacketList;
        }

        for (Map.Entry<Packet<?>, StopWatch> prevSet : packetList.entrySet()) {
            String rawName = prevSet.getKey().type().toString();
            String packetName = rawName;

            if (currentList.containsKey(packetName)) {
                ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(packetName).tag().replace("(", "").replace(")", "")) + 1) + ")", Color.WHITE, Color.CYAN);
                currentList.put(packetName, count);
                continue;
            }
            currentList.putIfAbsent(packetName, new ListInfo("(1)", Color.WHITE, Color.CYAN));
        }

        this.prevPacketList = packetList;

        return forceAddedToBottom(currentList);
    }

    //TODO: This kinda broke the packet event. Im guessing it caused exceptions in the thread
    public void onPacket(PacketEvent event) {
        switch (mode.get()) {
            case "Inflow" -> {
                if (event.getType().equals(PacketEvent.Type.RECEIVE)) {
                    packetList.put(event.getPacket(), new StopWatch());
                }
            }
            case "Outflow" -> {
                if (event.getType().equals(PacketEvent.Type.SEND)) {
                    packetList.put(event.getPacket(), new StopWatch());
                }
            }
        }

        for (Map.Entry<Packet<?>, StopWatch> set : this.packetList.entrySet()) {
            set.getValue().start();
            if (set.getValue().hasTimePassedS(history.get())) this.packetList.remove(set.getKey());
        }
    }

}
