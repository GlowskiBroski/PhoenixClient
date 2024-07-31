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

    private final HashMap<String, ResourceLocation> bakedStaticStringList = new HashMap<>();
    private final HashMap<Character, ResourceLocation> bakedDynamicCharList = new HashMap<>();

    protected final OnChange<Double> onGuiScaleChangedDynamic = new OnChange<>();
    protected final OnChange<Double> onGuiScaleChangedStatic = new OnChange<>();

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
     * This method is much more efficient for strings that are constantly changing
     */
    public void drawDynamicString(GuiGraphics graphics, String text, Vector pos, Color color) {
        if (font == null) {
            MC.font.drawInBatch(text, ((float) pos.getX()), ((float) pos.getY()), color.hashCode(), false, graphics.pose().last().pose(), graphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880, MC.font.isBidirectional());
            graphics.flush();
            return;
        }

        double guiScale = MC.getWindow().getGuiScale();
        onGuiScaleChangedDynamic.run(MC.getWindow().getGuiScale(), () -> {
            bakedDynamicCharList.clear();
            setFont(new Font(font.getFontName(), font.getStyle(), (int) (10 * guiScale)));
        });

        //Draws each character individually using their own resource locations and textured rectangles
        pos = pos.clone();
        for (char c : text.toCharArray()) {
            ResourceLocation resourceLocation;
            if (bakedDynamicCharList.containsKey(c)) {
                resourceLocation = bakedDynamicCharList.get(c);
            } else {
                resourceLocation = ResourceLocation.fromNamespaceAndPath("phoenixclient", UUID.randomUUID().toString());
                BufferedImage img = getBufferedImage(String.valueOf(c));
                if (img != null) {
                    DynamicTexture tex = TextureUtil.getDynamicTexture(img);
                    tex.upload();
                    MC.getTextureManager().register(resourceLocation, tex);
                }
                bakedDynamicCharList.put(c, resourceLocation);
            }

            float width = (float) fontMetrics.stringWidth(String.valueOf(c));
            float height = (float) fontMetrics.getHeight();
            int yOff = switch (getFont().getFontName()) {
                case "Segoe Print" -> 5;
                case "Arial" -> 2;
                default -> 3;
            };
            graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
            if (color.getAlpha() <= 0) return;
            DrawUtil.drawTexturedRect(graphics, resourceLocation, pos.getSubtracted(0, yOff), new Vector(width / guiScale, height / guiScale));
            graphics.setColor(1f,1f,1f,1f);
            pos.add(new Vector(width / guiScale, 0));
        }
    }

    /**
     * Renders a new texture for each string created. It draws less textures, but has to render more and never has a full baked list of chars.
     * This method is much more efficient for strings that do not change.
     * @param graphics
     * @param text
     * @param pos
     * @param color
     */
    public void drawStaticString(GuiGraphics graphics, String text, Vector pos, Color color) {
        if (getFont() == null) {
            MC.font.drawInBatch(text, ((float) pos.getX()), ((float) pos.getY()), color.hashCode(), false, graphics.pose().last().pose(), graphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880, MC.font.isBidirectional());
            graphics.flush();
            return;
        }

        double guiScale = MC.getWindow().getGuiScale();
        onGuiScaleChangedStatic.run(MC.getWindow().getGuiScale(), () -> {
            bakedStaticStringList.clear();
            setFont(new Font(getFont().getFontName(), getFont().getStyle(), (int) (10 * guiScale)));
        });

        ResourceLocation resourceLocation;
        if (bakedStaticStringList.containsKey(text)) {
            resourceLocation = bakedStaticStringList.get(text);
        } else {
            resourceLocation = ResourceLocation.fromNamespaceAndPath("phoenixclient", UUID.randomUUID().toString());
            BufferedImage img = getBufferedImage(text);
            if (img != null) {
                DynamicTexture tex = TextureUtil.getDynamicTexture(img);
                tex.upload();
                MC.getTextureManager().register(resourceLocation, tex);
            }
            bakedStaticStringList.put(text, resourceLocation);
        }

        int width = getFontMetrics().stringWidth(text);
        int height = getFontMetrics().getHeight();
        int yOff = switch (getFont().getFontName()) {
            case "Segoe Print" -> 5;
            case "Arial" -> 2;
            default -> 3;
        };
        graphics.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        DrawUtil.drawTexturedRect(graphics, resourceLocation, pos.getSubtracted(0, yOff), new Vector(width / guiScale, height / guiScale));
        graphics.setColor(1f,1f,1f,1f);
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

            int x = 0;
            for (char c : text.toCharArray()) { //Drawing each character individually fixed the text spacing issue
                graphics.drawString(String.valueOf(c), x, fontMetrics.getAscent());
                x += fontMetrics.stringWidth(String.valueOf(c));
            }
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
