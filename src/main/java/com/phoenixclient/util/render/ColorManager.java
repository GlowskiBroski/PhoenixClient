package com.phoenixclient.util.render;

import com.phoenixclient.util.math.MathUtil;

import java.awt.*;

public class ColorManager {

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
        return baseColor;
    }

    public Color getDepthColor() {
        return depthColor;
    }

    public Color getWidgetColor() {
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

    public enum Theme {

        ORANGE(new Color(225,100,0), new Color(129, 41, 12),new Color(250,120,0),new Color(50,50,50,200)),
        BLUE(new Color(0,120,255),new Color(5, 37, 171),new Color(0, 125, 200),new Color(50,50,50,200)),
        SEABLUE(new Color(40,215,165),new Color(17,56,88),new Color(40,215,215),new Color(50,50,50,200));

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

    public static Color getRedGreenScaledColor(double scale) {
        int r = MathUtil.getBoundValue((1 - scale) * 255,0,255).intValue();
        int g = MathUtil.getBoundValue(scale * 255,0,255).intValue();
        int b = 0;

        return new Color(r, g, b);
    }
}
