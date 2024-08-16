package com.phoenixclient.module;

import com.mojang.math.Axis;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderLevelEvent;
import com.phoenixclient.event.events.RenderNameTagEvent;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.mixin.mixins.accessors.IMixinGameRenderer;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.Draw3DUtil;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class NameTags extends Module {

    private final SettingGUI<String> scaling = new SettingGUI<>(
            this,
            "Scaling",
            "Type of scaling for nametags",
            "Auto").setModeData("Value","Auto");

    private final SettingGUI<Double> scale = new SettingGUI<>(
            this,
            "Scale",
            "Manual nametag scale",
            1d).setSliderData(.25,1,.05).setDependency(scaling,"Value");

    private final SettingGUI<Boolean> players = new SettingGUI<>(
            this,
            "Players",
            "Renders player ESP",
            true
    );

    private final SettingGUI<Boolean> items = new SettingGUI<>(
            this,
            "Items",
            "Renders item ESP",
            false
    );

    private final SettingGUI<Boolean> passive = new SettingGUI<>(
            this,
            "Passive Mobs",
            "Renders passive mob ESP",
            false
    );

    private final SettingGUI<Boolean> hostile = new SettingGUI<>(
            this,
            "Hostile Mobs",
            "Renders hostile mob ESP",
            false
    );

    private float partialTicks = 0;

    public NameTags() {
        super("NameTags", "Draws Custom NameTags", Category.RENDER, false, -1);
        addSettings(items,players,passive,hostile,scaling,scale);
        addEventSubscriber(Event.EVENT_RENDER_HUD, this::onRenderHUD);
        addEventSubscriber(Event.EVENT_RENDER_LEVEL, this::onLevelRender);
        addEventSubscriber(Event.EVENT_RENDER_NAMETAG, this::onRenderNameTag);
    }

    public void onRenderHUD(RenderScreenEvent event) {
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

        for (Entity e : MC.level.entitiesForRendering()) {
            boolean doItems = items.get() && e instanceof ItemEntity;
            boolean doPlayers = players.get() && e instanceof Player && !e.equals(MC.player);
            boolean doHostile = hostile.get() && e instanceof Monster;
            boolean doPassive = passive.get() && e instanceof Animal;

            if (doItems || doHostile || doPassive) drawBasicNameTag(graphics,e);
            if (doPlayers) drawPlayerNameTag(graphics,(Player)e);
            if (doHostile) drawBasicNameTag(graphics,e);
        }
    }

    public void onRenderNameTag(RenderNameTagEvent event) {
        Entity e = event.getEntity();
        boolean doItems = items.get() && e instanceof ItemEntity;
        boolean doPlayers = players.get() && e instanceof Player && !e.equals(MC.player);
        boolean doHostile = hostile.get() && e instanceof Monster;
        boolean doPassive = passive.get() && e instanceof Animal;
        if (doItems || doPlayers || doHostile || doPassive) event.setCancelled(true);
    }

    public void onLevelRender(RenderLevelEvent event) {
        partialTicks = event.getPartialTicks();
    }

    private void drawBasicNameTag(GuiGraphics guiGraphics, Entity entity) {
        double height = entity.getBbHeight();
        Vector screenPos = get2DProjection(Draw3DUtil.getLerpPos(entity, partialTicks).getAdded(0, height, 0));

        if (screenPos.getX() < 0 || screenPos.getY() < 0) return;

        float scale = switch (scaling.get()) {
            case "Value" -> this.scale.get().floatValue();
            case "Auto" -> Math.clamp(1 / (entity.distanceTo(MC.player) + 15) * 20,.5f,1);
            default -> 1;
        };
        screenPos.add(new Vector(0,-14 * scale));

        guiGraphics.pose().scale(scale,scale,1);
        screenPos.multiply(1/scale);
        String name = entity.getName().getString();
        String tag = " [" + MathUtil.roundDouble(entity.distanceTo(MC.player), 1) + "m]";
        if (entity instanceof ItemEntity e) tag = " [x" + e.getItem().getCount() + "]";


        String nameplateText = name + tag;
        Vector size = new Vector(DrawUtil.getFontTextWidth(nameplateText) + 7, 14);

        Vector pos = screenPos.getSubtracted(size.getX() / 2, 0);
        Vector centerPos = pos.getAdded(size.getMultiplied(.5));

        //Draw Background
        DrawUtil.drawRectangleRound(guiGraphics, pos, size, new Color(0, 0, 0, 175));
        DrawUtil.drawRectangleRound(guiGraphics, pos, size, PhoenixClient.getColorManager().getBaseColor(), true);

        //Draw Player Name
        TextBuilder.start(name, pos.getAdded(0, 1).getAdded(size.getMultiplied(.5)).getSubtracted(new Vector(DrawUtil.getFontTextWidth(nameplateText), DrawUtil.getFontTextHeight()).getMultiplied(.5)), Color.WHITE).draw(guiGraphics).nextAdj().text(tag).color(PhoenixClient.getColorManager().getWidgetColor()).draw(guiGraphics);
        guiGraphics.pose().scale(1/scale,1/scale,1);
    }

    private void drawPlayerNameTag(GuiGraphics guiGraphics, Player player) {
        double height = player.getBbHeight();

        Vector screenPos = get2DProjection(Draw3DUtil.getLerpPos(player, partialTicks).getAdded(0, height, 0));
        if (screenPos.getX() < 0 || screenPos.getY() < 0) return;

        float scale = switch (scaling.get()) {
            case "Value" -> this.scale.get().floatValue();
            case "Auto" -> Math.clamp(1 / (player.distanceTo(MC.player) + 15) * 20,.5f,1);
            default -> 1;
        };
        screenPos.add(new Vector(0,-14 * scale));

        guiGraphics.pose().scale(scale,scale,1);
        screenPos.multiply(1/scale);

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
        Vector size = new Vector(DrawUtil.getFontTextWidth(nameplateText) + 7, 14);

        Vector pos = screenPos.getSubtracted(size.getX() / 2, 0);
        Vector centerPos = pos.getAdded(size.getMultiplied(.5));


        //Draw Background
        DrawUtil.drawRectangleRound(guiGraphics, pos, size, new Color(0, 0, 0, 175));
        DrawUtil.drawRectangleRound(guiGraphics, pos, size, PhoenixClient.getColorManager().getBaseColor(), true);

        //Draw Player Name
        TextBuilder.start(playerName, pos.getAdded(0, 1).getAdded(size.getMultiplied(.5)).getSubtracted(new Vector(DrawUtil.getFontTextWidth(nameplateText), DrawUtil.getFontTextHeight()).getMultiplied(.5)), Color.WHITE).draw(guiGraphics).nextAdj().text(healthString).color(ColorManager.getRedGreenScaledColor(playerHeath / maxHealth)).draw(guiGraphics).nextAdj().text(pingString).color(PhoenixClient.getColorManager().getWidgetColor()).draw(guiGraphics);


        Vector armorPos = centerPos.getSubtracted(32 + 7, 24);
        Vector addVec = new Vector(20, 0);
        for (int j = 0; j < 4; j++) {
            ItemStack stack = player.inventoryMenu.getSlot(5 + j).getItem();
            DrawUtil.drawItemStack(guiGraphics, stack, armorPos);
            if (stack != null && stack.isDamageableItem()) {
                String damage = stack.getMaxDamage() - stack.getDamageValue() + "";
                TextBuilder.start(damage, armorPos.getAdded(8, -8).getSubtracted(DrawUtil.getFontTextWidth(damage) / 2, 0), ColorManager.getRedGreenScaledColor((double) (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage())).draw(guiGraphics);
            }
            armorPos.add(addVec);
        }
        /* TODO: Figure out how you wanna format held items
        ItemStack main = player.getMainHandItem();
        DrawUtil.drawItemStack(guiGraphics,main,armorPos);
        armorPos.add(addVec);
        ItemStack off = player.getOffhandItem();
        DrawUtil.drawItemStack(guiGraphics,off,armorPos);
        armorPos.add(addVec);
         */

        guiGraphics.pose().scale(1/scale,1/scale,1);
    }

    public Vector get2DProjection(Vector position3D) {
        Camera cam = MC.gameRenderer.getMainCamera();
        Vector camPos = new Vector(cam.getPosition());
        Quaternionf camRot = cam.rotation();
        Quaternionf camRotConjugate = new Quaternionf(-camRot.x, -camRot.y, -camRot.z, camRot.w);

        Vector relativePos = camPos.getSubtracted(position3D);
        Vector3f rotatedPos = new Vector3f((float) relativePos.getX(), (float) relativePos.getY(), (float) relativePos.getZ()).rotate(camRotConjugate); //result3f

        if (MC.options.bobView().get() && !((NoRender)PhoenixClient.getModule("NoRender")).noBob.get()) {
            if (MC.getCameraEntity() instanceof Player player) {

                float g = player.walkDist - player.walkDistO;
                float h = -(player.walkDist + g * partialTicks);
                float i = Mth.lerp(partialTicks, player.oBob, player.bob);

                Vector3f inverseBob = new Vector3f((Mth.sin(h * (float) Math.PI) * i * 0.5F), (-Math.abs(Mth.cos(h * (float) Math.PI) * i)), 0.0f).mul(-1);
                rotatedPos.rotate(Axis.XP.rotationDegrees(Math.abs(Mth.cos(h * (float)Math.PI - 0.2f) * i) * 5.0f));
                rotatedPos.rotate(Axis.ZP.rotationDegrees(Mth.sin(h * (float)Math.PI) * i * 3.0f));
                rotatedPos.add(inverseBob);
            }
        }

        double FOV = ((IMixinGameRenderer)MC.gameRenderer).invokeGetFov(cam,partialTicks,true);
        float scale = (float) (MC.getWindow().getGuiScaledHeight() / (2 * Math.tan(Math.toRadians(FOV / 2))));

        Vector screenCenter = new Vector(MC.getWindow().getGuiScaledWidth(), MC.getWindow().getGuiScaledHeight()).getMultiplied(.5);

        if (rotatedPos.z < 0) return new Vector(-32767, -32767);
        return new Vector(-rotatedPos.x, rotatedPos.y)
                .getMultiplied(1 / rotatedPos.z)
                .getMultiplied(scale)
                .getAdded(screenCenter);
    }


}
