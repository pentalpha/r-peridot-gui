package peridot.GUI;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by pentalpha on 23/03/2018.
 */
public class GUIUtils {

    private GUIUtils(){

    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
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

    public static Dimension getCenterLocation(int width, int height){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int)(screenSize.width - width) / 2;
        int y = (int)(screenSize.height - height) / 2;
        return new Dimension(x, y);
    }
}
