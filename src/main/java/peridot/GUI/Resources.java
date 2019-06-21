/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 *
 * @author Pitágoras Alves
 */
public final class Resources {
    private Resources(){
        throw new AssertionError();
    }

    public static InputStream getFontStream(String name){
        return Resources.class.getClassLoader().getResourceAsStream("font/" + name);
    }

    public static BufferedImage getImage(String name) throws IOException{
        return ImageIO.read(Resources.class.getClassLoader().getResource("icons/" + name));
    }

    public static ImageIcon getImageIcon(String name){
        return new ImageIcon(Resources.class.getClassLoader().getResource("icons/"+name));
    }

    public static javafx.scene.Parent getFXML(String name) throws IOException{
        return FXMLLoader.load(Resources.class.getClassLoader().getResource("fxml/" + name));
    }
}
