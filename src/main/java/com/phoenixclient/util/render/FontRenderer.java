package com.phoenixclient.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.phoenixclient.util.actions.OnChange;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.texture.TextureUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Custom fonts do cause a good chunk of lag. Try and figure this out
public class FontRenderer {

    private final HashMap<String, ResourceLocation> bakedStringList = new HashMap<>();
    private final HashMap<Character, ResourceLocation> bakedCharList = new HashMap<>();

    protected final OnChange<Double> onGuiScaleChanged = new OnChange<>();

    private FontMetrics fontMetrics;
    private Font font;

    public FontRenderer(String font, int style) {
        if (font.equals("Default")) {
            this.font = null;
            return;
        }
        this.font = new Font(font, style, 10);
        this.fontMetrics = generateFontMetrics();
    }

    /**
     * Draws using baked characters, but draws more textures. It has its advantages, but drawbacks.
     * If we can figure out to streamline this, it is optimal.
     */
    public void drawString(GuiGraphics graphics, String text, Vector pos, Color color) {
        if (font == null) {
            MC.font.drawInBatch(text, ((float) pos.getX()), ((float) pos.getY()), color.hashCode(), false, graphics.pose().last().pose(), graphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880, MC.font.isBidirectional());
            graphics.flush();
            return;
        }

        ResourceLocation resourceLocation;

        double guiScale = MC.getWindow().getGuiScale();

        onGuiScaleChanged.run(MC.getWindow().getGuiScale(), () -> {
            bakedCharList.clear();
            setFont(new Font(font.getFontName(), font.getStyle(), (int) (10 * guiScale)));
        });

        graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        //Draws each character individually
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
            int yOff = switch (getFont().getFontName()) {
                case "Segoe Print" -> 5;
                case "Arial" -> 2;
                default -> 3;
            };

            //If you can combine all of the resource locations into 1 texture, then call drawTexturedRect once, it will be very good
            DrawUtil.drawTexturedRect(graphics, resourceLocation, pos.getSubtracted(0, yOff), new Vector(width / guiScale, height / guiScale));
            pos.add(new Vector(width / guiScale, 0));
        }
    }

    /**
     * Renders a new texture for each string created. It draws less textures, but has to render more and never has a full baked list of chars
     *
     * @param graphics
     * @param text
     * @param pos
     * @param color
     */
    public void drawStringAlt(GuiGraphics graphics, String text, Vector pos, Color color) {
        if (getFont() == null) {
            MC.font.drawInBatch(text, ((float) pos.getX()), ((float) pos.getY()), color.hashCode(), false, graphics.pose().last().pose(), graphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880, MC.font.isBidirectional());
            graphics.flush();
            return;
        }
        ResourceLocation resourceLocation;

        double guiScale = MC.getWindow().getGuiScale();

        onGuiScaleChanged.run(MC.getWindow().getGuiScale(), () -> {
            bakedStringList.clear();
            setFont(new Font(getFont().getFontName(), getFont().getStyle(), (int) (10 * guiScale)));
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

        int width = getFontMetrics().stringWidth(text);
        int height = getFontMetrics().getHeight();
        graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        int yOff = switch (getFont().getFontName()) {
            case "Segoe Print" -> 5;
            case "Arial" -> 2;
            default -> 3;
        };
        DrawUtil.drawTexturedRect(graphics, resourceLocation, pos.getSubtracted(0, yOff), new Vector(width / guiScale, height / guiScale));
    }

    protected BufferedImage getBufferedImage(String text) {
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

    protected void setFont(Font font) {
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
        if (font == null) return MC.font.width(text);
        return (int) (fontMetrics.stringWidth(text) / MC.getWindow().getGuiScale());
    }

    public int getHeight() {
        if (font == null) return MC.font.lineHeight;
        return (int) (fontMetrics.getFont().getSize() / MC.getWindow().getGuiScale());
    }
}
