/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import peridot.script.RScript;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import peridot.GUI.component.Label;

/**
 *
 * @author pentalpha
 */
public class Main {
    public static final String deployType = "simple";
    public static JDialog logoLoadingFrame;
    //public static final String deployType = "plus";
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        showLogoLoadingScreen();
        SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.GraphiteSkin());
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Aesthetics.loadFonts();
            if(deployType.equals("simple")){
                MainGUI gui = new MainGUI();
                gui.setVisible(true);
            }else if (deployType.equals("plus")){
                //new MainGUIPlus().setVisible(true);
            }
            Main.clean();
        });
    }
    
    public static void clean(){
        for(Map.Entry<String, RScript> pair : RScript.availableScripts.entrySet()){
            pair.getValue().cleanTempFiles();
        }
    }
    
    public static void showLogoLoadingScreen(){
        logoLoadingFrame = new JDialog();
        ImageIcon logoImage;
        logoImage = new ImageIcon(logoLoadingFrame.getClass().getResource("/peridot/GUI/icons/logo.png"));
        JLabel logoLabel = new Label();
        logoLabel.setIcon(logoImage);
        logoLoadingFrame.getContentPane().add(logoLabel);
        logoLoadingFrame.setUndecorated(true);
        logoLoadingFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        logoLoadingFrame.setSize(logoImage.getIconWidth(), logoImage.getIconHeight());
        logoLoadingFrame.pack();
        logoLoadingFrame.setLocationRelativeTo(null);
        logoLoadingFrame.setAlwaysOnTop(true);
        logoLoadingFrame.setModal(false);
        logoLoadingFrame.setVisible(true);
    }
}
