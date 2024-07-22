package com.phoenixclient.util.render;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Organize this class its ugly
public class DrawUtil {

    // -------------------- TEXT ----------------------

    public static double getFontTextWidth(String text, double scale) {
        return PhoenixClient.getFontRenderer().getWidth(text) * scale;
    }

    public static double getFontTextWidth(String text) {
        return getFontTextWidth(text, 1);
    }

    public static double getFontTextHeight(double scale) {
        return PhoenixClient.getFontRenderer().getHeight() * scale;
    }

    public static double getFontTextHeight() {
        return getFontTextHeight(1);
    }

    public static double getDefaultTextWidth(String text, double scale) {
        return MC.font.width(text) * scale;
    }

    public static double getDefaultTextWidth(String text) {
        return getDefaultTextWidth(text, 1);
    }

    public static double getDefaultTextHeight(double scale) {
        return MC.font.lineHeight * scale;
    }

    public static double getDefaultTextHeight() {
        return getDefaultTextHeight(1);
    }


    // -------------------- RECTANGLES ----------------

    public static void drawRectangle(GuiGraphics graphics, Vector pos, Vector size, Color color, boolean outlined) {
        float x = (float) pos.getX();
        float y = (float) pos.getY();
        float width = (float) size.getX();
        float height = (float) size.getY();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        bufferBuilder.begin(outlined ? VertexFormat.Mode.DEBUG_LINE_STRIP : VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, y + height, 0).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(matrix, x + width, y + height, 0).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(matrix, x + width, y, 0).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();

        //Redraw first vertex to complete outline
        if (outlined) bufferBuilder.vertex(matrix, x, y + height, 0).color(r, g, b, a).endVertex();

        BufferBuilder.RenderedBuffer buff = bufferBuilder.end();
        BufferUploader.drawWithShader(buff);
    }

    public static void drawRectangle(GuiGraphics graphics, Vector pos, Vector size, Color color) {
        drawRectangle(graphics, pos, size, color, false);
    }

    public static void drawRectangleRound(GuiGraphics graphics, Vector pos, Vector size, Color color, double radius, boolean outlined) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        bufferBuilder.begin(outlined ? VertexFormat.Mode.DEBUG_LINE_STRIP : VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        //Mark the 4 quarter circle centers
        Vector[] corners = {
                new Vector(pos.getX() + radius, pos.getY() + size.getY() - radius), //Bottom Left
                new Vector(pos.getX() + size.getX() - radius, pos.getY() + size.getY() - radius), //Bottom Right
                new Vector(pos.getX() + size.getX() - radius, pos.getY() + radius), //Upper Right
                new Vector(pos.getX() + radius, pos.getY() + radius) //Upper Left
        };

        Vector firstVertex = new Vector(0,0,0);
        DoOnce setFirstVertex = new DoOnce();

        double vertexCount = 5;
        Angle range = new Angle(Math.PI/2);

        //For each corner, draw a quarter circle
        for (int c = 0; c < 4; c++) {
            Vector center = corners[c];

            //Draw Quarter Circle
            double radianIncrement = range.getRadians() / vertexCount;
            double rotation = new Angle(Math.PI).getAdded(range.getMultiplied(c)).getRadians();
            for (int i = 0; i <= vertexCount; i++) {
                Vector vertex = new Vector(Math.cos(radianIncrement*i + rotation),-Math.sin(radianIncrement*i + rotation)).getMultiplied(radius).getAdded(center);
                setFirstVertex.run(() -> firstVertex.setX(vertex.getX()).setY(vertex.getY()).setZ(vertex.getZ()));
                bufferBuilder.vertex(matrix, (float)vertex.getX(),(float)vertex.getY(), 0).color(color.getRed()/255f, color.getGreen()/255f,color.getBlue()/255f, color.getAlpha()/255f).endVertex();
            }
        }

        //Redraw first vertex to complete outline
        if (outlined) bufferBuilder.vertex(matrix, (float)firstVertex.getX(),(float)firstVertex.getY(), 0).color(color.getRed()/255f, color.getGreen()/255f,color.getBlue()/255f, color.getAlpha()/255f).endVertex();

        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }

    public static void drawRectangleRound(GuiGraphics graphics, Vector pos, Vector size, Color color, boolean outlined) {
        drawRectangleRound(graphics,pos,size,color,3, outlined);
    }

    public static void drawRectangleRound(GuiGraphics graphics, Vector pos, Vector size, Color color) {
        drawRectangleRound(graphics,pos,size,color,3, false);
    }

    public static void drawTexturedRect(GuiGraphics graphics, ResourceLocation texture, Vector pos, Vector size, Vector texturePos, Vector textureSize) {
        //drawRectangle(graphics,pos,size,Color.WHITE,true); //Debug See Outline Code
        float x = (float) pos.getX();
        float y = (float) pos.getY();
        float width = (float) size.getX();
        float height = (float) size.getY();

        float texX = (float) texturePos.getX();
        float texY = (float) texturePos.getY();
        float texWidth = (float) textureSize.getX();
        float texHeight = (float) textureSize.getY();

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, texture);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferbuilder.vertex(matrix, x, y + height, 0).uv(texX / texWidth, (texY + height) / texHeight).endVertex();
        bufferbuilder.vertex(matrix, x + width, y + height, 0).uv((texX + width) / texWidth, (texY + height) / texHeight).endVertex();
        bufferbuilder.vertex(matrix, x + width, y, 0).uv((texX + width) / texWidth, texY / texHeight).endVertex();
        bufferbuilder.vertex(matrix, x, y, 0).uv(texX / texWidth, texY / texHeight).endVertex();

        BufferBuilder.RenderedBuffer buff = bufferbuilder.end();
        BufferUploader.drawWithShader(buff);
    }

    /**
     * Quick version of texture rectangle. Automatically sets the texture position and size to be that of the rectangle
     */
    public static void drawTexturedRect(GuiGraphics graphics, ResourceLocation texture, Vector pos, Vector size) {
        drawTexturedRect(graphics, texture, pos, size, Vector.NULL(), size);
    }

    // ------------------------- ALT SHAPES -------------------------

    public static void drawArrow(GuiGraphics graphics, Vector pos, float size, Color color, boolean outlined) {
        float x = (float) pos.getX();
        float y = (float) pos.getY();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        float stretchX = 2;

        if (outlined) {
            bufferBuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            //Outline
            bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + stretchX, y, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + size / 4 + stretchX, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + stretchX, y + size, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x, y + size, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size / 4, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
        } else {
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            //FillU
            bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size / 4, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + size / 4 + stretchX, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + stretchX, y, 0).color(r, g, b, a).endVertex();

            //FillL
            bufferBuilder.vertex(matrix, x + size / 4, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x, y + size, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + stretchX, y + size, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size + size / 4 + stretchX, y + size / 2, 0).color(r, g, b, a).endVertex();
        }

        BufferBuilder.RenderedBuffer buff = bufferBuilder.end();
        BufferUploader.drawWithShader(buff);

        RenderSystem.disableBlend();
    }

    public static void drawArrowHead(GuiGraphics graphics, Vector pos, float size, Color color, boolean outlined, boolean backwards) {
        float x = (float) pos.getX();
        float y = (float) pos.getY();

        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float a = color.getAlpha() / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        bufferBuilder.begin(outlined ? VertexFormat.Mode.DEBUG_LINE_STRIP : VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        //Outline
        if (backwards) {
            x -= size/4;
            bufferBuilder.vertex(matrix, x + size / 2, y, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size / 4, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size/2, y + size, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size/2, y, 0).color(r, g, b, a).endVertex();
        } else {
            bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x, y + size, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x + size / 4, y + size / 2, 0).color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
        }

        BufferBuilder.RenderedBuffer buff = bufferBuilder.end();
        BufferUploader.drawWithShader(buff);

        RenderSystem.disableBlend();
    }

    // -------------------------- EXTRA -----------------------

    public static void drawItemStack(GuiGraphics graphics, ItemStack stack, Vector pos) {
        int x = (int) Math.round(pos.getX());
        int y = (int) Math.round(pos.getY());
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(MC.font, stack, x, y);
    }

}
