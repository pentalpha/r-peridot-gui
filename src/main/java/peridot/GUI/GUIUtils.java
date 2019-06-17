package peridot.GUI;

import peridot.GUI.component.Label;
import peridot.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by pentalpha on 23/03/2018.
 */
public class GUIUtils {

    private GUIUtils(){

    }

    /**
     * Changes the size of a JLabel to actually fit it's text.
     */
    public static void setToIdealTextSize(JLabel label){
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        //FontMetrics metrics = graphics.getFontMetrics(label.getFont());
        // get the height of a line of text in this font and render context
        int hgt = metrics.getHeight();
        // get the advance of my text in this font and render context
        int adv = metrics.stringWidth(label.getText());
        // calculate the size of a box to hold the text with some padding.
        Dimension size = new Dimension(adv+2, hgt+2);

        label.setPreferredSize(size);
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

    public static boolean showImageDialog(File file){
        JLabel logoLabel = Label.getImageLabel(file);
        if(logoLabel != null){
            JDialog frame = new JDialog();
            frame.getContentPane().add(logoLabel);
            frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            frame.setSize(600, 615);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setModal(false);
            frame.setVisible(true);
            return true;
        }else{
            Log.logger.info("Could not load image " + file.getAbsolutePath());
            return false;
        }
    }

    public static void showErrorMessageInDialog(String title, String longMessage, Frame frame) {
        SwingUtilities.invokeLater(() -> {
            JTextArea textArea = new JTextArea(6, 25);
            textArea.setText(longMessage);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(frame, scrollPane, title, JOptionPane.ERROR_MESSAGE);
        });
    }
}
