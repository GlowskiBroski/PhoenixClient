package com.phoenixclient.util.render;

import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.texture.TextureUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

public class FontRenderer {

    private final HashMap<Character, ResourceLocation> bakedCharList = new HashMap<>();
    @Deprecated private final HashMap<String, ResourceLocation> bakedStringList = new HashMap<>();

    private final OnChange<Double> onGuiScaleChanged = new OnChange<>();

    private FontMetrics fontMetrics;
    private Font font;

    public FontRenderer(String font, int style) {
        this.font = new Font(font, style, 10);
        this.fontMetrics = generateFontMetrics();
    }

    /**
     * Draws using baked characters, but draws more textures. It has its advantages, but drawbacks.
     * If we can figure out to streamline this, it is optimal.
     */
    public void drawString(GuiGraphics graphics, String text, Vector pos, Color color) {
        ResourceLocation resourceLocation;

        double guiScale = MC.getWindow().getGuiScale();

        onGuiScaleChanged.run(MC.getWindow().getGuiScale(), () -> {
            bakedCharList.clear();
            setFont(new Font(font.getFontName(), font.getStyle(), (int) (10 * guiScale)));
        });

        graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        //Draws each character individually
        //TODO: Double check this to make sure we arent lagging the game
        pos = pos.clone();
        for (int i = 0; i < text.toCharArray().length; i++) {
            char c = text.toCharArray()[i];
            float width = (float) fontMetrics.stringWidth(String.valueOf(c));
            float height = (float) fontMetrics.getHeight();

            if (bakedCharList.containsKey(c)) {
                resourceLocation = bakedCharList.get(c);
            } else {
                resourceLocation = new ResourceLocation("phoenixclient", UUID.randomUUID().toString());
                BufferedImage img = getBufferedImage(String.valueOf(c));
                if (img != null) {
                    DynamicTexture tex = TextureUtil.getDynamicTexture(img);
                    tex.upload();
                    MC.getTextureManager().register(resourceLocation, tex);
                }
                bakedCharList.put(c, resourceLocation);
            }
            DrawUtil.drawTexturedRect(graphics, resourceLocation, pos.getSubtracted(0, 5), new Vector(width / guiScale, height / guiScale));
            pos.add(new Vector(width / guiScale, 0));
        }
    }

    @Deprecated
    public void drawStringO(GuiGraphics graphics, String text, Vector pos, Color color) {
        ResourceLocation resourceLocation;

        double guiScale = MC.getWindow().getGuiScale();

        onGuiScaleChanged.run(MC.getWindow().getGuiScale(), () -> {
            bakedStringList.clear();
            setFont(new Font(font.getFontName(), font.getStyle(), (int) (10 * guiScale)));
        });

        if (bakedStringList.containsKey(text)) {
            resourceLocation = bakedStringList.get(text);
        } else {
            resourceLocation = new ResourceLocation("phoenixclient", UUID.randomUUID().toString());
            BufferedImage img = getBufferedImage(text);
            if (img != null) {
                DynamicTexture tex = TextureUtil.getDynamicTexture(img);
                tex.upload();
                MC.getTextureManager().register(resourceLocation, tex);
            }
            bakedStringList.put(text, resourceLocation);
        }

        int width = fontMetrics.stringWidth(text);
        int height = fontMetrics.getHeight();
        graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        DrawUtil.drawTexturedRect(graphics, resourceLocation, pos.getSubtracted(0, 5), new Vector(width / guiScale, height / guiScale));
    }

    private BufferedImage getBufferedImage(String text) {
        if (text.isEmpty()) return null;
        int width = Math.max(1,fontMetrics.stringWidth(text));
        int height = Math.max(1,fontMetrics.getHeight());
        return TextureUtil.getBufferedImage(width,height,(graphics) -> {
            graphics.setFont(getFont());
            graphics.setColor(Color.WHITE);

            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.drawString(text, 0, fontMetrics.getAscent());
        });
    }

    private FontMetrics generateFontMetrics() {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = tempImage.getGraphics();
        graphics.setFont(font);
        return graphics.getFontMetrics();
    }

    private void setFont(Font font) {
        this.font = font;
        this.fontMetrics = generateFontMetrics();
    }

    public Font getFont() {
        return font;
    }

    public FontMetrics getFontMetrics() {
        return fontMetrics;
    }

    public int getWidth(String text) {
        return (int) (fontMetrics.stringWidth(text) / MC.getWindow().getGuiScale());
    }

    public int getHeight() {
        return (int) (fontMetrics.getFont().getSize() / MC.getWindow().getGuiScale());
    }
}
