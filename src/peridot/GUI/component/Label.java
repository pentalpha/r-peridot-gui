/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import peridot.GUI.Aesthetics;
import peridot.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Pitágoras Alves
 */
public class Label extends JLabel {
    public Label(){
        super();
        doStuff();
    }
    public Label(String string){
        super(string);
        doStuff();
    }
    private void doStuff(){
        this.setFont(Aesthetics.defaultFont);
        this.setForeground(Color.white);
    }
    
    public static JLabel getImageLabel(File file){
        ImageIcon imageIcon;
        try {
            imageIcon = new ImageIcon(ImageIO.read(file));
        }catch (IOException ex){
            imageIcon = new ImageIcon(file.getAbsolutePath());
        }
        //ImageIcon
        Image image = imageIcon.getImage();
        if(image.getWidth(null) >= 800){
            Log.logger.info("Scaling down image with width of " + image.getWidth(null));
            int heightScale = 800 / image.getWidth(null);
            int newHeight = image.getHeight(null)*heightScale;
            if(newHeight <= 0){
                newHeight = 800;
            }
            Image smallImg = image.getScaledInstance(800,
                    newHeight,
                    Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(smallImg);
        }
        JLabel img = new JLabel();
        img.setIcon(imageIcon);
        imageIcon.getImage().flush();
        return img;
    }
}
