package Client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.Line2D;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;

public class Utils {
    public static final String resourceFolder = "Resources/";

    public static URL getFileUrl(Class cl, String name) {
        System.out.println(resourceFolder + name);
        return cl.getClassLoader().getResource(resourceFolder + name);
    }

    public static Image getImage(Class cl, String name) {
        try {
            URL toRead = getFileUrl(cl, "Images/" + name);
            if (toRead == null) {
                System.out.println("Failed to load image " + name);
                return null;
            }
            return ImageIO.read(toRead);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    //массив для огрубления
    public static short[] getPosterizePal() {
        short[] ret = new short[256];
        for (int i = 0; i < 256; i++)
            ret[i] = (short) (i - (i % 32));
        return ret;
    }

    public static BufferedImage gray(BufferedImage img) {
        ColorSpace cs = ColorSpace.getInstance(
                ColorSpace.CS_GRAY);
        BufferedImageOp op = new ColorConvertOp(cs, null);
        return op.filter(img, null);
    }

    public static BufferedImage edges(BufferedImage img) {
        float val = 1.0f;
        float[] filter = {val, val, val, val, val, val, val, val, val};
        BufferedImageOp op =
                new ConvolveOp(new Kernel(3, 3, filter), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(img, null);
    }

    public static BufferedImage applyMask(BufferedImage sourceImage, BufferedImage maskImage, int method) {

        BufferedImage maskedImage = null;
        if (sourceImage != null) {

            int width = maskImage.getWidth();
            int height = maskImage.getHeight();

            maskedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D mg = maskedImage.createGraphics();

            int x = (width - sourceImage.getWidth()) / 2;
            int y = (height - sourceImage.getHeight()) / 2;

            mg.drawImage(sourceImage, x, y, null);
            mg.setComposite(AlphaComposite.getInstance(method));

            mg.drawImage(maskImage, 0, 0, null);

            mg.dispose();
        }

        return maskedImage;
    }


    //методы для движения объектов, а не игрока
    public static int toScreenX(double x, int playerX, int cameraX) {
        return (int) ((cameraX) + (x - playerX));
    }

    public static int toScreenY(double y, int cameraY) {
        //для передвижения камеры по y
        return (int) (y - cameraY);
    }

    public static double length(Line2D line) {
        return Point.distance(line.getX1(), line.getY1(), line.getX2(), line.getY2());
    }

    public static BufferedImage setTransparency(BufferedImage toTranspare, int transparency) {
        if (transparency >= 255)
            transparency = 255;
        if (transparency <= 0) {
            transparency = 0;
        }

        BufferedImage alphaMask = new BufferedImage(toTranspare.getWidth(null), toTranspare.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = alphaMask.createGraphics();

        g2d.setPaint(new Color(0, 0, 0, transparency));
        g2d.fillRect(0, 0, alphaMask.getWidth(), alphaMask.getHeight());
        g2d.dispose();
        return Utils.applyMask(Utils.toBufferedImage(toTranspare), alphaMask, AlphaComposite.DST_IN);
    }
}


