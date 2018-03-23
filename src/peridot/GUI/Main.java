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
import peridot.script.r.Interpreter;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

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
        showLogoLoadingScreen();
        SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.GraphiteSkin());
        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            Aesthetics.loadFonts();

            Operations.createNecessaryDirs();

            java.util.function.BooleanSupplier func = () -> {
                return launchInterpreterManagerGUI();
            };

            if(Operations.loadModules()){
                if(Operations.loadInterpreters(func)){
                    MainGUI gui = new MainGUI();
                    gui.setVisible(true);
                }else{
                    endMain();
                }
            }else{
                endMain();
            }
        });
        //Log.logger.info("Really finishing R-Peridot-GUI.");
    }

    public static boolean launchInterpreterManagerGUI(){
        logoLoadingFrame.setVisible(false);
        try {
            InterpreterManagerSwingDialog.DummyFrame dummy = new InterpreterManagerSwingDialog.DummyFrame(
                    InterpreterManagerSwingDialog.titleString
            );//just so that the dialog can have an taskbar icon
            InterpreterManagerSwingDialog managerGUI = new InterpreterManagerSwingDialog(dummy);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        boolean defined = Interpreter.isDefaultInterpreterDefined();
        if(defined){
            Log.logger.info("R environment selected.");
            logoLoadingFrame.setVisible(true);
        }
        return defined;
    }
    
    public static void clean(){
        for(Map.Entry<String, RModule> pair : RModule.availableModules.entrySet()){
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
