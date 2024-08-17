package com.phoenixclient.util.render;

import com.mojang.blaze3d.platform.Lighting;
import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.actions.DoOnce;
import com.phoenixclient.util.math.Angle;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
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
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(outlined ? VertexFormat.Mode.DEBUG_LINE_STRIP : VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);//.getBuilder();

        bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(r, g, b, a);
        bufferBuilder.addVertex(matrix, x + width, y + height, 0).setColor(r, g, b, a);
        bufferBuilder.addVertex(matrix, x + width, y, 0).setColor(r, g, b, a);
        bufferBuilder.addVertex(matrix, x, y, 0).setColor(r, g, b, a);

        //Redraw first vertex to complete outline
        if (outlined) bufferBuilder.addVertex(matrix, x, y + height, 0).setColor(r, g, b, a);

        MeshData buff = bufferBuilder.build();
        BufferUploader.drawWithShader(buff);
    }

    public static void drawRectangle(GuiGraphics graphics, Vector pos, Vector size, Color color) {
        drawRectangle(graphics, pos, size, color, false);
    }

    public static void drawRectangleRound(GuiGraphics graphics, Vector pos, Vector size, Color color, double radius, boolean outlined) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(outlined ? VertexFormat.Mode.DEBUG_LINE_STRIP : VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

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
                bufferBuilder.addVertex(matrix, (float)vertex.getX(),(float)vertex.getY(), 0).setColor(color.getRed()/255f, color.getGreen()/255f,color.getBlue()/255f, color.getAlpha()/255f);
            }
        }

        //Redraw first vertex to complete outline
        if (outlined) bufferBuilder.addVertex(matrix, (float)firstVertex.getX(),(float)firstVertex.getY(), 0).setColor(color.getRed()/255f, color.getGreen()/255f,color.getBlue()/255f, color.getAlpha()/255f);

        BufferUploader.drawWithShader(bufferBuilder.build());
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
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        bufferbuilder.addVertex(matrix, x, y + height, 0).setUv(texX / texWidth, (texY + height) / texHeight);
        bufferbuilder.addVertex(matrix, x + width, y + height, 0).setUv((texX + width) / texWidth, (texY + height) / texHeight);
        bufferbuilder.addVertex(matrix, x + width, y, 0).setUv((texX + width) / texWidth, texY / texHeight);
        bufferbuilder.addVertex(matrix, x, y, 0).setUv(texX / texWidth, texY / texHeight);

        BufferUploader.drawWithShader(bufferbuilder.build());
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

        float stretchX = 2;
        BufferBuilder bufferBuilder;
        if (outlined) {
            bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            //Outline
            bufferBuilder.addVertex(matrix, x, y, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + stretchX, y, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + size / 4 + stretchX, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + stretchX, y + size, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x, y + size, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size / 4, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x, y, 0).setColor(r, g, b, a);
        } else {
            bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            //FillU
            bufferBuilder.addVertex(matrix, x, y, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size / 4, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + size / 4 + stretchX, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + stretchX, y, 0).setColor(r, g, b, a);

            //FillL
            bufferBuilder.addVertex(matrix, x + size / 4, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x, y + size, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + stretchX, y + size, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size + size / 4 + stretchX, y + size / 2, 0).setColor(r, g, b, a);
        }

        BufferUploader.drawWithShader(bufferBuilder.build());

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
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(outlined ? VertexFormat.Mode.DEBUG_LINE_STRIP : VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        //Outline
        if (backwards) {
            x -= size/4;
            bufferBuilder.addVertex(matrix, x + size / 2, y, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size / 4, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size/2, y + size, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size/2, y, 0).setColor(r, g, b, a);
        } else {
            bufferBuilder.addVertex(matrix, x, y, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x, y + size, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x + size / 4, y + size / 2, 0).setColor(r, g, b, a);
            bufferBuilder.addVertex(matrix, x, y, 0).setColor(r, g, b, a);
        }

        BufferUploader.drawWithShader(bufferBuilder.build());

        RenderSystem.disableBlend();
    }

    // -------------------------- EXTRA -----------------------

    public static void drawItemStack(GuiGraphics graphics, ItemStack stack, Vector pos) {
        /*
        int x = (int) Math.round(pos.getX());
        int y = (int) Math.round(pos.getY());
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(MC.font, stack, x, y);
         */
        renderItem(graphics,stack,pos.getX(),pos.getY());
        renderItemDecorations(graphics,MC.font,stack,pos.getX(),pos.getY());

    }

    /**
     * This method is taken from graphics, but modified to accept double values for x and y to stop jittering and be in line with other elements
     * @param graphics
     * @param itemStack
     * @param x
     * @param y
     */
    private static void renderItem(GuiGraphics graphics, ItemStack itemStack, double x, double y) {
        int k = 0;
        int l = 0;
        LivingEntity livingEntity = MC.player;
        Level level = MC.level;
        
        if (itemStack.isEmpty()) return;
        BakedModel bakedModel = MC.getItemRenderer().getModel(itemStack, level, livingEntity, k);
        graphics.pose().pushPose();
        graphics.pose().translate((float)(x + 8), (float)(y + 8), (float)(150 + (bakedModel.isGui3d() ? l : 0)));
        try {
            boolean bl;
            graphics.pose().scale(16.0f, -16.0f, 16.0f);
            boolean bl2 = bl = !bakedModel.usesBlockLight();
            if (bl) {
                Lighting.setupForFlatItems();
            }
            MC.getItemRenderer().render(itemStack, ItemDisplayContext.GUI, false, graphics.pose(), graphics.bufferSource(), 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);
            graphics.flush();
            if (bl) {
                Lighting.setupFor3DItems();
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.forThrowable(throwable, "Rendering item");
            CrashReportCategory crashReportCategory = crashReport.addCategory("Item being rendered");
            crashReportCategory.setDetail("Item Type", () -> String.valueOf(itemStack.getItem()));
            crashReportCategory.setDetail("Item Components", () -> String.valueOf(itemStack.getComponents()));
            crashReportCategory.setDetail("Item Foil", () -> String.valueOf(itemStack.hasFoil()));
            throw new ReportedException(crashReport);
        }
        graphics.pose().popPose();
    }

    public static void renderItemDecorations(GuiGraphics graphics, Font font, ItemStack itemStack, double x, double y) {
        String string = null;
        LocalPlayer localPlayer;
        float f;
        double n;
        double m;
        if (itemStack.isEmpty()) {
            return;
        }
        graphics.pose().pushPose();
        if (itemStack.getCount() != 1 || string != null) {
            String string2 = string == null ? String.valueOf(itemStack.getCount()) : string;
            graphics.pose().translate(0.0f, 0.0f, 200.0f);
            font.drawInBatch(string2, (float)x + 19 - 2 - font.width(string2), (float)y + 6 + 3, 0xFFFFFF, true, graphics.pose().last().pose(), (MultiBufferSource)graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, 0xF000F0, font.isBidirectional());
            graphics.flush();
        }
        if (itemStack.isBarVisible()) {
            int k = itemStack.getBarWidth();
            int l = itemStack.getBarColor();
            m = x + 2;
            n = y + 13;
            graphics.pose().translate(0,0,200);
            drawRectangle(graphics,new Vector(m,n),new Vector(13, 2),new Color(-16777216));
            drawRectangle(graphics,new Vector(m,n),new Vector(k, 1),new Color(l | 0xFF000000));
            graphics.flush();
        }
        float f2 = f = (localPlayer = MC.player) == null ? 0.0f : localPlayer.getCooldowns().getCooldownPercent(itemStack.getItem(), MC.getTimer().getGameTimeDeltaPartialTick(true));
        if (f > 0.0f) {
            m = y + Mth.floor(16.0f * (1.0f - f));
            n = m + Mth.ceil(16.0f * f);
            drawRectangle(graphics,new Vector(x,m),new Vector(16,0),new Color(Integer.MAX_VALUE));
        }
        graphics.pose().popPose();
    }

}
