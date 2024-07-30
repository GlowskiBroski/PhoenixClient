package com.phoenixclient.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Draw3DUtil {

    public static Vector getLerpPos(Entity entity, float partialTicks) {
        double x = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double y = Mth.lerp(partialTicks, entity.yo, entity.getY());
        double z = Mth.lerp(partialTicks, entity.zo, entity.getZ());
        return  new Vector(x,y,z);
    }


    //TODO: Gets a little spazzy when coordinates get larger. Fix this
    public static void drawOutlineBox(PoseStack levelPoseStack, AABB bb, Vector lerpPos, Color c) {
        AABB unitCube = AABB.unitCubeFromLowerCorner(new Vec3(-.5,0,-.5));
        float minX = (float) unitCube.minX;
        float minY = (float) unitCube.minY;
        float minZ = (float) unitCube.minZ;
        float maxX = (float) unitCube.maxX;
        float maxY = (float) unitCube.maxY;
        float maxZ = (float) unitCube.maxZ;

        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;
        float a = c.getAlpha() / 255f;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        levelPoseStack.pushPose();
        levelPoseStack.translate(lerpPos.getX(), lerpPos.getY(), lerpPos.getZ());
        levelPoseStack.scale((float) (bb.maxX - bb.minX), (float) (bb.maxY - bb.minY), (float) (bb.maxZ - bb.minZ));

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = levelPoseStack.last().pose();

        bufferBuilder.addVertex(matrix, minX, minY, minZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, maxX, minY, minZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, maxX, minY, minZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, minX, minY, maxZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, minX, minY, maxZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, minX, minY, minZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, minX, minY, minZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, minX, maxY, minZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, maxX, minY, minZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, minX, minY, maxZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, minX, maxY, minZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(r,g,b,a);

        bufferBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(r,g,b,a);
        bufferBuilder.addVertex(matrix, minX, maxY, minZ).setColor(r,g,b,a);

        BufferUploader.drawWithShader(bufferBuilder.build());
        levelPoseStack.popPose();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

}
