package com.phoenixclient.util.render;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.math.MathUtil;

import java.awt.*;

public class ColorManager {

    private boolean rainbow;
    private boolean custom;

    private Color baseColor;
    private Color depthColor;
    private Color widgetColor;
    private Color backgroundColor;
    private Color hudLabelColor;

    public ColorManager(Theme theme) {
        this(theme.getBaseColor(),theme.getDepthColor(),theme.getWidgetColor(),theme.getBackgroundColor());
    }

    public ColorManager(Color generalColor) {
        this(generalColor,generalColor,generalColor,generalColor);
    }

    private ColorManager(Color baseColor, Color depthColor, Color widgetColor, Color backgroundColor) {
        this.baseColor = baseColor;
        this.depthColor = depthColor;
        this.widgetColor = widgetColor;
        this.backgroundColor = backgroundColor;
        this.rainbow = false;
        this.custom = false;
    }

    public void setTheme(Theme theme) {
        this.baseColor = theme.getBaseColor();
        this.depthColor = theme.getDepthColor();
        this.widgetColor = theme.getWidgetColor();
        this.backgroundColor = theme.getBackgroundColor();
        this.hudLabelColor = theme.getHudLabelColor();
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }


    public Color getBaseColor() {
        if (rainbow) return getRainbowColor(2f,0,1,200,200);
        if (custom) return new Color(Color.HSBtoRGB(PhoenixClient.getGuiManager().baseColorHue.get().floatValue(),200/255f,200/255f));
        return baseColor;
    }

    public Color getDepthColor() {
        if (rainbow) return getRainbowColor(2f,0,1,175,100);
        if (custom) return new Color(Color.HSBtoRGB(PhoenixClient.getGuiManager().depthColorHue.get().floatValue(),175/255f,100/255f));
        return depthColor;
    }

    public Color getWidgetColor() {
        if (rainbow) return getRainbowColor(2f,0,1f,200,255);
        if (custom) return new Color(Color.HSBtoRGB(PhoenixClient.getGuiManager().widgetColorHue.get().floatValue(),200/255f,255/255f));
        return widgetColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getHudLabelColor() {
        return hudLabelColor;
    }


    public static Color getRedGreenScaledColor(double scale) {
        int r = MathUtil.getBoundValue((1 - scale) * 255,0,255).intValue();
        int g = MathUtil.getBoundValue(scale * 255,0,255).intValue();
        int b = 0;

        return new Color(r, g, b);
    }

    public static Color getRainbowColor(float rainbowSpeed, long offset, float strength, float saturation, float brightness) {
        float speed = (float)(rainbowSpeed * Math.pow(10,10));
        float hue = (float) (System.nanoTime() - offset * Math.pow(10,8)) / speed;
        long colorHex = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, (saturation / 255), (brightness / 255))), 16);
        Color color = new Color((int) colorHex);
        return new Color((float) color.getRed() / 255.0f * strength, (float) color.getGreen() / 255.0f * strength, (float) color.getBlue() / 255.0f * strength, (float) color.getAlpha() / 255.0f);
    }

    public static Color getRainbowColor(long offset) {
        return getRainbowColor(.75f,offset,1,255,255);
    }

    public enum Theme {
        RED(new Color(220, 54, 54),new Color(98, 12, 12),new Color(190, 92, 92)),
        ORANGE(new Color(225,100,0), new Color(129, 41, 12),new Color(250,120,0)),
        GREEN(new Color(54, 220, 66),new Color(23, 98, 12),new Color(92, 190, 100)),
        SEAGREEN(new Color(40,215,165),new Color(17,56,88),new Color(40,215,215)),
        BLUE(new Color(50, 150, 255),new Color(8, 67, 117),new Color(50, 175, 250)),
        //LIGHTBLUE(new Color(Color.HSBtoRGB(.52f,200/255f,200/255f)),new Color(Color.HSBtoRGB(.59f,175/255f,100/255f)),new Color(Color.HSBtoRGB(.52f,200/255f,255/255f))),
        LIGHTBLUE(new Color(Color.HSBtoRGB(.50f,200/255f,200/255f)),new Color(Color.HSBtoRGB(.61f,175/255f,100/255f)),new Color(Color.HSBtoRGB(.54f,200/255f,255/255f))),
        PURPLE(new Color(192, 56, 239), new Color(72, 12, 98),new Color(172, 92, 190));

        final Color baseColor;
        final Color depthColor;
        final Color widgetColor;
        final Color backgroundColor;
        final Color hudLabel;

        Theme(Color baseColor, Color depthColor, Color widgetColor) {
            this.baseColor = baseColor;
            this.depthColor = depthColor;
            this.widgetColor = widgetColor;
            this.backgroundColor = new Color(50,50,50,200);
            this.hudLabel = new Color(190, 190, 190, 215);
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
            return hudLabel;
        }
    }
}
