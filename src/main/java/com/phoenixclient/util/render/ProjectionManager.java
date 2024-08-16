package com.phoenixclient.util.render;

import com.mojang.math.Axis;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.mixin.mixins.accessors.IMixinGameRenderer;
import com.phoenixclient.module.NoRender;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.texture.TextureUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

public class ProjectionManager {

    private float partialTicks;

    public final EventAction partialTickUpdater = new EventAction(Event.EVENT_RENDER_LEVEL,() -> {
       partialTicks = Event.EVENT_RENDER_LEVEL.getPartialTicks();
    });


    public Projection get2DProjection(Entity entity,Vector offset3D) {
        return get2DProjection(Draw3DUtil.getLerpPos(entity, PhoenixClient.getProjectionManager().getPartialTicks()).getAdded(offset3D));
    }

    public Projection get2DProjection(Vector position3D) {
        Camera cam = MC.gameRenderer.getMainCamera();
        Vector camPos = new Vector(cam.getPosition());
        Quaternionf camRot = cam.rotation();
        Quaternionf camRotConjugate = new Quaternionf(-camRot.x, -camRot.y, -camRot.z, camRot.w);

        Vector relativePos = camPos.getSubtracted(position3D);
        Vector3f rotatedPos = new Vector3f((float) relativePos.getX(), (float) relativePos.getY(), (float) relativePos.getZ()).rotate(camRotConjugate);

        if (MC.options.bobView().get() && !((NoRender) PhoenixClient.getModule("NoRender")).noBob.get()) {
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

        return new Projection(new Vector(-rotatedPos.x, rotatedPos.y)
                .getMultiplied(1 / rotatedPos.z)
                .getMultiplied(scale)
                .getAdded(screenCenter),rotatedPos.z >= 0);
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public record Projection(Vector pos2D, boolean onScreen) {}
}
