package com.phoenixclient.util.render.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;

public class TextureUtil {

    public static BufferedImage getBufferedImage(int width, int height, DrawGraphics drawCode) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        drawCode.run(graphics);
        return image;
    }

    public static DynamicTexture getDynamicTexture(BufferedImage bufferedImage) {
        if (bufferedImage == null) return null;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, height, false);

        WritableRaster bufferedRaster = bufferedImage.getRaster();
        ColorModel bufferedColor = bufferedImage.getColorModel();

        int numBands = bufferedRaster.getNumBands();
        Object bufferType = switch (bufferedRaster.getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE -> new byte[numBands];
            case DataBuffer.TYPE_USHORT -> new short[numBands];
            case DataBuffer.TYPE_INT -> new int[numBands];
            case DataBuffer.TYPE_FLOAT -> new float[numBands];
            case DataBuffer.TYPE_DOUBLE -> new double[numBands];
            default -> null;
        };

        //Assign Colors from BufferedImage pixels to NativeImage pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedRaster.getDataElements(x, y, bufferType);
                int red = bufferedColor.getRed(bufferType);
                int green = bufferedColor.getGreen(bufferType);
                int blue = bufferedColor.getBlue(bufferType);
                int alpha = bufferedColor.getAlpha(bufferType);
                int hash = alpha << 24 | blue << 16 | green << 8 | red;
                image.setPixelRGBA(x,y,hash);
            }
        }
        return new DynamicTexture(image);
    }

    @FunctionalInterface
    public interface DrawGraphics {
        void run(Graphics2D graphics);
    }

}
