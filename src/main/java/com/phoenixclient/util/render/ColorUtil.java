package com.phoenixclient.util.render;

import com.phoenixclient.util.math.MathUtil;

import java.awt.*;

//I don't like this class at all. Find a way to completely rework it
// include rainbow and themes?
public class ColorUtil {

    public static final Color HUD_LABEL = new Color(190, 190, 190, 215);

    public static Color getRedGreenScaledColor(double scale) {
        int r = MathUtil.getBoundValue((1 - scale) * 255,0,255).intValue();
        int g = MathUtil.getBoundValue(scale * 255,0,255).intValue();
        int b = 0;

        return new Color(r, g, b);
    }

    public static Theme getTheme() {
        return Theme.SEABLUE;
    }

    public enum Theme {

        ORANGE(new Color(220,100,0), new Color(200,60,0),new Color(250,120,0),new Color(50,50,50,200)),
        BLUE(new Color(0,120,255),new Color(0,50,255),new Color(0, 125, 200),new Color(50,50,50,200)),
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

}
