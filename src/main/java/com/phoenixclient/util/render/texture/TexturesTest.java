package com.phoenixclient.util.render.texture;

import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: DELETE THIS CLASS OR DO SOMETHING ELSE WITH IT

@Deprecated
public class TexturesTest {

    private static OnChange<Double> onGuiChange = new OnChange<>();
    private static ResourceLocation windowHead = ResourceLocation.fromNamespaceAndPath("phoenixclient", UUID.randomUUID().toString());

    private static void initWindowHead(Color color) {
        int size = 50;
        int stretchX = 3;

        int width = size + size / 4 + stretchX + 1;
        int height = size + 2;

        int scale = (int) MC.getWindow().getGuiScale();

        width *= scale;
        height *= scale;

        BufferedImage img = TextureUtil.getBufferedImage(width,height,(g) -> {

            g.setColor(color);

            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Polygon p = new Polygon();

            int lsize = size * (int)MC.getWindow().getGuiScale();

            int x = 0;
            int y = 0;
            p.addPoint(x, y);
            p.addPoint(x + lsize + stretchX, y);
            p.addPoint(x + lsize + lsize / 4 + stretchX, y + lsize / 2);
            p.addPoint(x + lsize + stretchX, y + lsize);
            p.addPoint(x, y + lsize);
            p.addPoint(x + lsize / 4, y + lsize / 2);
            p.addPoint(x, y);
            g.fillPolygon(p);

        });
        DynamicTexture tex = TextureUtil.getDynamicTexture(img);
        tex.upload();
        MC.getTextureManager().register(windowHead, tex);
    }

    // --- delete later
    public static void drawArrow(GuiGraphics graphics, Vector pos, int size, Color color) {
        //*
        onGuiChange.run(MC.getWindow().getGuiScale(), () -> {
            initWindowHead(color);
        });
        int stretchX = 3;
        int scale = (int) MC.getWindow().getGuiScale();

        graphics.pose().scale((float) 1 /scale, (float) 1 /scale,1);

        int width = size + size / 4 + stretchX + 1;
        int height = size + 2;
        width *= scale;
        height *= scale;

        DrawUtil.drawTexturedRect(graphics,windowHead,pos.getMultiplied((double) scale),new Vector(width,height));

        graphics.pose().scale((float)scale, (float) scale,1);
        //*/

        /*
        int stretchX = 3;

        int width = size + size / 4 + stretchX + 1;
        int height = size + 2;

        int scale = (int) MC.getWindow().getGuiScale();

        width *= scale;
        height *= scale;

        graphics.pose().scale((float) 1 /scale, (float) 1 /scale,1);

        ResourceLocation resourceLocation = null;
        BufferedImage img = TextureUtil.getBufferedImage(width,height,(g) -> {

            g.setColor(color);

            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Polygon p = new Polygon();

            int lsize = size * scale;

            int x = 0;
            int y = 0;
            p.addPoint(x, y);
            p.addPoint(x + lsize + stretchX, y);
            p.addPoint(x + lsize + lsize / 4 + stretchX, y + lsize / 2);
            p.addPoint(x + lsize + stretchX, y + lsize);
            p.addPoint(x, y + lsize);
            p.addPoint(x + lsize / 4, y + lsize / 2);
            p.addPoint(x, y);
            g.fillPolygon(p);

        });
        DynamicTexture tex = TextureUtil.getDynamicTexture(img);
        tex.upload();
        MC.getTextureManager().register(resourceLocation, tex);
        DrawUtil.drawTexturedRect(graphics,resourceLocation,pos.getMultiplied((double) scale),new Vector(width,height));
        graphics.pose().scale((float)scale, (float) scale,1);

         //*/
    }
}
