package com.phoenixclient.module;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.RenderLevelEvent;
import com.phoenixclient.event.events.RenderNameTagEvent;
import com.phoenixclient.event.events.RenderScreenEvent;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.*;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class Tracers extends Module {

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

    public Tracers() {
        super("Tracers", "Draws a line to nearby entities", Category.RENDER, false, -1);
        addSettings(items,players,passive,hostile);
        addEventSubscriber(Event.EVENT_RENDER_HUD, this::onRenderHUD);
    }

    public void onRenderHUD(RenderScreenEvent event) {
        GuiGraphics graphics = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

        for (Entity e : MC.level.entitiesForRendering()) {
            boolean doItems = items.get() && e instanceof ItemEntity;
            boolean doPlayers = players.get() && e instanceof Player && !e.equals(MC.player);
            boolean doHostile = hostile.get() && e instanceof Monster;
            boolean doPassive = passive.get() && e instanceof Animal;

            if (doItems || doHostile || doPassive || doPlayers) drawTracer(graphics,e);
        }
    }

    private void drawTracer(GuiGraphics graphics, Entity entity) {
        double height = entity.getBbHeight();
        Vector screenCenter = new Vector(MC.getWindow().getGuiScaledWidth(),MC.getWindow().getGuiScaledHeight()).getMultiplied(.5);

        Vector fromPos = new Vector(MC.getWindow().getGuiScaledWidth(),MC.getWindow().getGuiScaledHeight()).getMultiplied(.5).getAdded(0,-.5f);
        ProjectionManager.Projection projection = PhoenixClient.getProjectionManager().get2DProjection(entity,new Vector(0, height/2, 0));
        Vector toPos = projection.pos2D();

        if (!projection.onScreen()) {
            toPos.setX(screenCenter.getX() + (screenCenter.getX() - toPos.getX()));
            toPos.setY(screenCenter.getY() + (screenCenter.getY() - toPos.getY()));
        }

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Color c = Color.LIGHT_GRAY;
        bufferBuilder.addVertex(matrix, (float) fromPos.getX() + .5f,(float) fromPos.getY() + .5f, 0).setColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());

        if (projection.onScreen()) {
            bufferBuilder.addVertex(matrix, (float) toPos.getX() + .5f, (float) toPos.getY() + .5f, 0).setColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        } else {
            Vector extend = toPos.getSubtracted(screenCenter).getUnitVector().getMultiplied(MC.getWindow().getGuiScaledWidth()).getAdded(screenCenter);
            bufferBuilder.addVertex(matrix, (float) extend.getX() + .5f, (float) extend.getY() + .5f, 0).setColor(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        }
        BufferUploader.drawWithShader(bufferBuilder.build());

    }

}
