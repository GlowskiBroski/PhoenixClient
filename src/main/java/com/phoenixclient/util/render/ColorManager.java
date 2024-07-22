package com.phoenixclient.util.render;

import com.phoenixclient.util.math.MathUtil;

import java.awt.*;

public class ColorManager {

    private boolean rainbow;

    private Color baseColor;
    private Color depthColor;
    private Color widgetColor;
    private Color backgroundColor;

    public ColorManager(Theme theme) {
        this(theme.getBaseColor(),theme.getBorderColor(),theme.getWidgetColor(),theme.getBackgroundColor());
    }

    public ColorManager(Color generalColor) {
        this(generalColor,generalColor,generalColor,generalColor);
    }

    private ColorManager(Color baseColor, Color depthColor, Color widgetColor, Color backgroundColor) {
        this.baseColor = baseColor;
        this.depthColor = depthColor;
        this.widgetColor = widgetColor;
        this.backgroundColor = backgroundColor;
    }

    public Color getBaseColor() {
        if (rainbow) return getRainbowColor(0,1,200,200);
        return baseColor;
    }

    public Color getDepthColor() {
        if (rainbow) return getRainbowColor(0,1,125,125);
        return depthColor;
    }

    public Color getWidgetColor() {
        if (rainbow) return getRainbowColor(0,1f,125,255);
        return widgetColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getHudLabelColor() {
        return new Color(190, 190, 190, 215);
    }

    public void setTheme(Theme theme) {
        this.baseColor = theme.getBaseColor();
        this.depthColor = theme.getBorderColor();
        this.widgetColor = theme.getWidgetColor();
        this.backgroundColor = theme.getBackgroundColor();
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public static Color getRedGreenScaledColor(double scale) {
        int r = MathUtil.getBoundValue((1 - scale) * 255,0,255).intValue();
        int g = MathUtil.getBoundValue(scale * 255,0,255).intValue();
        int b = 0;

        return new Color(r, g, b);
    }

    public static Color getRainbowColor(long offset, float strength, float saturation, float brightness) {
        float rainbowSpeed = .75f;

        float speed = (float)(rainbowSpeed * Math.pow(10,10));
        float hue = (float) (System.nanoTime() - offset * Math.pow(10,8)) / speed;
        long colorHex = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, (saturation / 255), (brightness / 255))), 16);
        Color color = new Color((int) colorHex);
        return new Color((float) color.getRed() / 255.0f * strength, (float) color.getGreen() / 255.0f * strength, (float) color.getBlue() / 255.0f * strength, (float) color.getAlpha() / 255.0f);
    }

    public static Color getRainbowColor(long offset) {
        return getRainbowColor(offset,1,255,255);
    }

    public enum Theme {
        RED(new Color(220, 54, 54),new Color(98, 12, 12),new Color(190, 92, 92),new Color(50,50,50,200)),
        ORANGE(new Color(225,100,0), new Color(129, 41, 12),new Color(250,120,0),new Color(50,50,50,200)),
        GREEN(new Color(54, 220, 66),new Color(23, 98, 12),new Color(92, 190, 100),new Color(50,50,50,200)),
        SEABLUE(new Color(40,215,165),new Color(17,56,88),new Color(40,215,215),new Color(50,50,50,200)),
        BLUE(new Color(50, 150, 255),new Color(5, 37, 171),new Color(50, 175, 250),new Color(50,50,50,200)),
        PURPLE(new Color(192, 56, 239), new Color(72, 12, 98),new Color(172, 92, 190),new Color(50,50,50,200));

        final Color baseColor;
        final Color borderColor;
        final Color widgetColor;
        final Color backgroundColor;

        Theme(Color baseColor, Color borderColor, Color widgetColor, Color backgroundColor) {
            this.baseColor = baseColor;
            this.borderColor = borderColor;
            this.widgetColor = widgetColor;
            this.backgroundColor = backgroundColor;
        }

        public Color getBaseColor() {
            return baseColor;
        }

        public Color getBorderColor() {
            return borderColor;
        }

        public Color getWidgetColor() {
            return widgetColor;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }
    }
}
