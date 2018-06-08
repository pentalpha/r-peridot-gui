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
        return Resources.class.getResourceAsStream("/font/" + name);
    }

    public static BufferedImage getImage(Class receiver, String name) throws IOException{
        return ImageIO.read(receiver.getResource("/icons/" + name));
    }

    public static ImageIcon getImageIcon(Class receiver, String name){
        return new ImageIcon(receiver.getResource("/icons/"+name));
    }

    public static javafx.scene.Parent getFXML(Class receiver, String name) throws IOException{
        return FXMLLoader.load(receiver.getResource("/fxml/" + name));
    }
}
