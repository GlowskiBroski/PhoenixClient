package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.Packet;

import java.awt.*;
import java.util.*;

//TODO: You need to finish this. Fix the concurrent modification errors, then add it to the list. Its ALMOST done

public class PacketFlowListWindow extends ListWindow {

    private final LinkedHashMap<Packet<?>, StopWatch> packetList = new LinkedHashMap<>();
    private LinkedHashMap<String, ListInfo> printableList = new LinkedHashMap<>();

    private final SettingGUI<String> mode;
    private final SettingGUI<Integer> history;

    public PacketFlowListWindow(Screen screen) {
        super(screen, "PacketInflowWindow", "Shows all packets send/received, with their counts, from the last time period - VERY EXPERIMENTAL", false);
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
        return forceAddedToBottom(printableList);
    }

    public void onPacket(PacketEvent event) {
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

        try {
            Set<Packet<?>> removalQueue = new HashSet<>();
            LinkedHashMap<String, ListInfo> currentList = new LinkedHashMap<>();
            Set<Packet<?>> cloneSet = packetList.keySet();
            for (Packet<?> packet : cloneSet) {
                String rawName = packet.type().toString();
                String packetName = rawName;
                if (packetList.get(packet).hasTimePassedS(history.get())) {
                    removalQueue.add(packet);
                    continue;
                }

                if (currentList.containsKey(packetName)) {
                    ListInfo count = new ListInfo("(" + (Integer.parseInt(currentList.get(packetName).tag().replace("(", "").replace(")", "")) + 1) + ")", Color.WHITE, Color.CYAN);
                    currentList.put(packetName, count);
                    continue;
                }
                currentList.putIfAbsent(packetName, new ListInfo("(1)", Color.WHITE, Color.CYAN));
            }

            for (Packet<?> packet : removalQueue) packetList.remove(packet);
            printableList = currentList;//(LinkedHashMap<String, ListInfo>) currentList.clone();

        } catch (NullPointerException e) {
            packetList.clear();
        } catch (ConcurrentModificationException e) {

        }
    }

}
