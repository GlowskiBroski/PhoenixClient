package com.phoenixclient.module;

import com.mojang.authlib.GameProfile;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.ConsoleUtil;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Logouts extends Module {

    //TODO: Add logout spots here. Add a feature to save the logout spots to a csv file

    private final SettingGUI<Boolean> logoutMessages = new SettingGUI<>(
            this,
            "Logout Messages",
            "Sends messages to chat on player logouts/joins",
            true
    );

    public Logouts() {
        super("Logouts", "Provides information on player logouts on teh server", Category.SERVER, false, -1);
        addSettings(logoutMessages);
        addEventSubscriber(Event.EVENT_PLAYER_UPDATE,this::onPlayerUpdate);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }

    public void onPlayerUpdate(Event event) {
        //TODO: Implement a mixin for this
    }

    public void onPacket(PacketEvent event) {

        //Leave Messages
        if (event.getPacket() instanceof ClientboundPlayerInfoRemovePacket p) {
            for (UUID u : p.profileIds()) {
                //TODO: Find a way to make this not have to use the api, as requests are limited
                Thread thread = new Thread(() -> {
                    String print;
                    try {
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        String url = "https://api.mojang.com/user/profile/" + u.toString();
                        HttpGet httpGet = new HttpGet(url);
                        HttpResponse response = httpClient.execute(httpGet);
                        String jsonString = EntityUtils.toString(response.getEntity());
                        print = (String)new JSONObject(jsonString).get("name");
                    } catch (IOException | JSONException e) {
                        print = u.toString();
                    }
                    ConsoleUtil.sendMessage(print + " Left!");
                });
                thread.setDaemon(true);
                thread.start();
            }
        }

        //Join Messages
        if (event.getPacket() instanceof ClientboundPlayerInfoUpdatePacket p) {
            for (ClientboundPlayerInfoUpdatePacket.Entry e : p.entries()) {
                if (e.profile() != null) {
                    String name = e.profile().getName();
                    ConsoleUtil.sendMessage(name + " Joined!");
                }
            }
        }
    }

}
