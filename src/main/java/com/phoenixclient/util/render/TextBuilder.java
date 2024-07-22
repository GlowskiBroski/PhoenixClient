package com.phoenixclient.util.render;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

import static com.phoenixclient.PhoenixClient.MC;

public class TextBuilder {

    private FontRenderer fontRenderer;

    private String text;
    private Vector pos;
    private Color color;
    private float scale;
    private boolean shadow;
    private boolean dynamic;

    /**
     * Default: Shadow: true, Dynamic: false, Scale = 1
     */
    public TextBuilder() {
        this.fontRenderer = PhoenixClient.getFontRenderer();
        this.shadow = true; //Contains shadow by default
        this.dynamic = false; //Static string by default
        this.scale = 1; //Scale 1 by default
    }

    public static TextBuilder start() {
        return new TextBuilder();
    }

    //You have to end this after calling to actually draw it.
    public static TextBuilder start(String text, Vector pos, Color color) {
        return start().text(text).pos(pos).color(color);
    }

    public TextBuilder text(String text) {
        this.text = text;
        return this;
    }

    public TextBuilder pos(Vector pos) {
        this.pos = pos;
        return this;
    }

    public TextBuilder color(Color color) {
        this.color = color;
        return this;
    }

    public TextBuilder scale(float scale) {
        this.scale = scale;
        return this;
    }

    public TextBuilder shadowLess() {
        this.shadow = false;
        return this;
    }

    public TextBuilder dynamic() {
        this.dynamic = true;
        return this;
    }

    public TextBuilder font(FontRenderer renderer) {
        this.fontRenderer = renderer;
        return this;
    }

    public TextBuilder defaultFont() {
        this.fontRenderer = null;
        return this;

    }

    /**
     * Overrides the position of the next text to be directly next to the
     * @return
     */
    public TextBuilder next() {
        return TextBuilder.start().pos(pos.getAdded(fontRenderer == null ? new Vector((MC.font.width(text) + 1) * scale,0) : new Vector((fontRenderer.getWidth(text) + 1) * scale,0)));
    }

    //TODO: Potentially combine the shadow inside of the bufferedimage
    public TextBuilder draw(GuiGraphics graphics) {
        graphics.pose().scale(scale, scale, 1);
        graphics.setColor(1, 1, 1, color.getAlpha() / 255f);
        if (fontRenderer == null) {
            MC.font.drawInBatch(text, ((float) pos.getX()), ((float) pos.getY()), color.hashCode(), shadow, graphics.pose().last().pose(), graphics.bufferSource(), net.minecraft.client.gui.Font.DisplayMode.SEE_THROUGH, 0, 15728880, MC.font.isBidirectional());
            graphics.flush();
        } else {
            if (shadow) {
                int damp = 175;
                Color shadowColor = new Color(MathUtil.getBoundValue(color.getRed() - damp,0,255).intValue(), MathUtil.getBoundValue(color.getGreen() - damp,0,255).intValue(), MathUtil.getBoundValue(color.getBlue() - damp,0,255).intValue(), MathUtil.getBoundValue(color.getAlpha() - 50,0,255).intValue());
                if (dynamic) PhoenixClient.getFontRenderer().drawDynamicString(graphics,text,pos.getAdded(1,1).getMultiplied(1/scale),shadowColor);
                else PhoenixClient.getFontRenderer().drawStaticString(graphics,text,pos.getAdded(1,1).getMultiplied(1/scale),shadowColor);
            }

            if (dynamic) fontRenderer.drawDynamicString(graphics,text,pos.getMultiplied(1/scale),color);
            else fontRenderer.drawStaticString(graphics,text,pos.getMultiplied(1/scale),color);
        }
        graphics.pose().scale(1 / scale, 1 / scale, 1);
        graphics.setColor(1f, 1f, 1f, 1f);
        return this;
    }


}
