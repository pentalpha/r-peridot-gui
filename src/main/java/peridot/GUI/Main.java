/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import peridot.Archiver.PeridotConfig;
import peridot.GUI.component.Label;
import peridot.GUI.javaFXPanels.InterpreterManagerSwingDialog;
import peridot.Log;
import peridot.Operations;
import peridot.script.RModule;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import java.net.URL;
import java.net.URLClassLoader;
/**
 *
 * @author pentalpha
 */
public class Main {
    public static final String deployType = "simple"; //or "plus"
    public static final int maxColsSimple = 100;
    public static final int maxColsPlus = 10000;
    public static JDialog logoLoadingFrame;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //ClassLoader cl = Main.class.getClassLoader();
        //URL url =  cl.getResource("icons");
        //String path = url.getPath();
        //System.out.println(path);
        //File[] files = new File(path).listFiles();// ((URLClassLoader)cl).getURLs();
        //String[] files = System.getProperty("java.class.path").split(":");
        //for(File f: files){
        //    System.out.println(f.getPath());
        //}
        showLogoLoadingScreen();
        SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.GraphiteSkin());
        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            Aesthetics.loadFonts();

            Operations.createNecessaryDirs();

            java.util.function.BooleanSupplier func = () -> {
                return false;
            };

            if(Operations.loadModules()){
                if(Operations.loadInterpreters(func)){
                    MainGUI gui = new MainGUI();
                    gui.setVisible(true);
                }else{
                    logoLoadingFrame.setVisible(false);
                    InterpreterManagerSwingDialog.openInterpreterManager(() -> {
                        MainGUI gui = new MainGUI();
                        gui.setVisible(true);
                    });
                }
            }else{
                endMain();
            }
        });
        //Log.logger.info("Really finishing R-Peridot-GUI.");
    }

    /*public static boolean launchInterpreterManagerGUI(){


        boolean defined = Interpreter.isDefaultInterpreterDefined();
        if(defined){
            Log.logger.info("R environment selected.");
            logoLoadingFrame.setVisible(true);
        }
        return defined;
    }*/
    
    public static void clean(){
        for(Map.Entry<String, RModule> pair : RModule.availableModules.entrySet()){
            pair.getValue().cleanTempFiles();
        }
    }
    
    public static void showLogoLoadingScreen(){
        logoLoadingFrame = new JDialog();
        ImageIcon logoImage;
        logoImage = Resources.getImageIcon("logo.png");
        
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

    public static void endMain(){
        Log.logger.info("Cleaning temporary files.");
        Main.clean();
        try {
            Log.logger.info("Saving configurations.");
            PeridotConfig.save();
        }catch (IOException ex){
            Log.logger.severe("Error while saving the current configurations.");
            ex.printStackTrace();
        }
        Log.logger.info("Finishing R-Peridot-GUI.");
        System.exit(0);
    }
}
