/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.component;

import java.awt.Color;
import java.io.File;
import javax.swing.ImageIcon;
import peridot.GUI.Aesthetics;
import javax.swing.JLabel;
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
        ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
        JLabel img = new JLabel();
        img.setIcon(imageIcon);
        return img;
    }
}
