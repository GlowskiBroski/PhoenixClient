package com.phoenixclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

import static com.phoenixclient.PhoenixClient.MC;

public class ConsoleUtil {

    public static final String PREFIX = "[PhoenixClient] ";

    public static void sendMessage(String message) {
        try {
            //MC.getChatListener().handleSystemMessage(Component.translatable("\u00A7b" + PREFIX + "\u00A77" +  message), true);
            MC.gui.getChat().addMessage(Component.translatable("\u00A7b" + PREFIX + "\u00A77" +  message));
        } catch (Exception e) {
            System.out.println("Could Not Send Message!");
        }
    }

    public static void sendConsoleMessage(String message) {
        System.out.println(PREFIX + message);
    }

}
