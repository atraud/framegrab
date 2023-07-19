package org.traud.video;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {
    public static BufferedImage resize(BufferedImage img, int w, int h) {
        if (img == null) return img;
        BufferedImage out = new BufferedImage(w, h, img.getType());
        Graphics2D g = out.createGraphics();
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    interface RGBMapper {
        int apply(int rgb);
    }

    public static BufferedImage map(BufferedImage img, RGBMapper map)
    {
        int width = img.getWidth();
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < width; x++) {
                out.setRGB(x, y, map.apply(img.getRGB(x, y)));
            }
        }
        return out;
    }

    public static BufferedImage contrast(BufferedImage img, int c)
    {
        double F = c * 255.0; // 259.0*(255.0+c)/255.0*(259.0-c);
        return map(img, rgb -> {
            int R = (rgb >> 16) & 0xff;
            int G = (rgb >>  8) & 0xff;
            int B = (rgb >>  0) & 0xff;
            R = (int)(F*(R-128) + 128);
            G = (int)(F*(G-128) + 128);
            B = (int)(F*(B-128) + 128);

//            return rgb(R, G, B);
            return rgb;
        });
    }

    private static int rgb(int r, int g, int b) {
        r = clamp(r);
        g = clamp(g);
        b = clamp(b);
        return ((r << 16) | (g << 8) | b);
    }

    private static int clamp(int i) {
        return i < 0 ? 0 : i > 255 ? 255 : i;
    }
}
