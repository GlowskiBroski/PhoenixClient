package com.phoenixclient.module;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class NameTags extends Module {

    public NameTags() {
        super("NameTags", "Draws custom nametags - UNIMPLEMENTED", Category.RENDER, false, -1);
        addEventSubscriber(Event.EVENT_RENDER_HUD,this::onRenderHUD);
    }

    public void onRenderHUD(Event event) {
        GuiGraphics graphics = new GuiGraphics(MC,MC.renderBuffers().bufferSource());
        drawPlayerNameTag(graphics, new Vector(MC.getWindow().getGuiScaledWidth() / 2,120));
    }


    //TODO: Create a transformation matrix to draw these 2D on screen through projection
    private void drawPlayerNameTag(GuiGraphics guiGraphics, Vector nametagPos) {
        Player player = MC.player;

        boolean showHealth = true;
        boolean showPing = true;

        String playerName = player.getDisplayName().getString();

        float playerHeath = player.getHealth();
        float maxHealth = player.getMaxHealth();
        String healthString = " " + Math.round(playerHeath) + "/" + Math.round(maxHealth);

        int ping = 0;
        if (MC.getConnection() != null) {
            PlayerInfo info = MC.getConnection().getPlayerInfo(player.getUUID());
            if (info != null) ping = info.getLatency();
        }
        String pingString = " " + ping + "ms";


        //TODO: Add distance string?


        String nameplateText = playerName + healthString + pingString;
        Vector size = new Vector(DrawUtil.getFontTextWidth(nameplateText) + 7,14);

        Vector pos = nametagPos.getSubtracted(size.getX() / 2, 0);
        Vector centerPos = pos.getAdded(size.getMultiplied(.5));


        //Draw Background
        DrawUtil.drawRectangleRound(guiGraphics, pos, size, new Color(0, 0, 0, 175));
        DrawUtil.drawRectangleRound(guiGraphics, pos, size, PhoenixClient.getColorManager().getBaseColor(), true);

        //Draw Player Name
        TextBuilder
                .start(playerName,pos.getAdded(0,1).getAdded(size.getMultiplied(.5)).getSubtracted(new Vector(DrawUtil.getFontTextWidth(nameplateText),DrawUtil.getFontTextHeight()).getMultiplied(.5)),Color.WHITE).draw(guiGraphics)
                .nextAdj().text(healthString).color(ColorManager.getRedGreenScaledColor(playerHeath/maxHealth)).draw(guiGraphics)
                .nextAdj().text(pingString).color(PhoenixClient.getColorManager().getWidgetColor()).draw(guiGraphics)
        ;


        Vector armorPos = centerPos.getSubtracted(32 + 7,24);
        Vector addVec = new Vector(20,0);
        for (int j = 0; j < 4; j++) {
            ItemStack stack = player.inventoryMenu.getSlot(5 + j).getItem();
            DrawUtil.drawItemStack(guiGraphics,stack,armorPos);
            if (stack != null && stack.isDamageableItem()) {
                String damage = stack.getMaxDamage() - stack.getDamageValue() + "";
                TextBuilder.start(damage, armorPos.getAdded(8, -8).getSubtracted(DrawUtil.getFontTextWidth(damage) / 2, 0), ColorManager.getRedGreenScaledColor((double) (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage())).draw(guiGraphics);
            }
            armorPos.add(addVec);
        }
        /*
        ItemStack main = player.getMainHandItem();
        DrawUtil.drawItemStack(guiGraphics,main,armorPos);
        armorPos.add(addVec);
        ItemStack off = player.getOffhandItem();
        DrawUtil.drawItemStack(guiGraphics,off,armorPos);
        armorPos.add(addVec);
         */
    }

}
