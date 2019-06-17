/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import peridot.Archiver.PeridotConfig;
import peridot.Archiver.Places;
import peridot.GUI.component.Panel;
import peridot.GUI.component.TabbedPane;
import peridot.GUI.dialog.modulesManager.ModulesManager;
import peridot.GUI.javaFXPanels.InterpreterManagerSwingDialog;
import peridot.GUI.panel.NewAnalysisPanel;
import peridot.GUI.panel.ProcessingPanel;
import peridot.GUI.panel.ResultsPanel;
import peridot.Global;
import peridot.Log;
import peridot.script.RModule;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;

import static peridot.Log.logger;
/**
 *
 * @author pentalpha
 */
public class MainGUI extends javax.swing.JFrame {
    public static final java.awt.Dimension defaultSize = new java.awt.Dimension(570, 600);//previous width:546
    public static final String appName = "R-Peridot";
    protected NewAnalysisPanel analysisPanel;
    public ProcessingPanel processingPanel;
    protected ResultsPanel resultsPanel;
    public static MainGUI _instance;
    private boolean modManagerOpened = false;
    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        logger.info("Start MainGUI");
        //Places.createPeridotDir();
        //Places.updateModulesDir(false);
        //Util.getRPath();
        
        //Log.logger.info("trying to load scripts");
        //RModule.loadUserScripts();
        if(RModule.getAvailableModules().size() == 0){
            JOptionPane.showMessageDialog(null, "Scripts could not be loaded. We recommend using Menu > Tools > Reset User Scripts.");
        }
        //Log.logger.info("scripts loaded");
        this.setTitle(appName);
        //this.setLayout(new BorderLayout());

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                //setVisible(false);
                //dispose();
                Main.endMain();
            }
        });

        setResizable(false);
        makeMenuBar();
        Panel contentPane = new Panel();
        contentPane.setLayout(new BorderLayout());
        tabbedPanel = new TabbedPane();
        tabbedPanel.setUI(new org.pushingpixels.substance.internal.ui.SubstanceTabbedPaneUI() {
            @Override
            protected int calculateTabWidth(int i, int i1, FontMetrics fm){
                //return super.calculateTabWidth(i, i1, fm) + 30;
                return defaultSize.width/3;
            }
        });
        makeTabs();
        contentPane.add(tabbedPanel, java.awt.BorderLayout.CENTER);
        this.add(contentPane);
        pack();
        _instance = this;
        Main.logoLoadingFrame.setVisible(false);
        setLocationRelativeTo(null);
        this.setIconImage(getDefaultIcon(this));
    }
    
    public static MainGUI getInstance(){
        return MainGUI._instance;
    }
    
    public static Image getDefaultIcon(Object receiver){
        Image frameIcon = null;
        try{
            frameIcon = Resources.getImage(receiver.getClass(), "logo64.png");
        }catch(Exception ex){
            ex.printStackTrace();
            Log.logger.info("Default ImageIcon not loaded");
        }
        return frameIcon;
    }
    
    public void makeTabs(){
        RModule.updateUserScripts();
        tabbedPanel.removeAll();
        tabbedPanel.setFocusable(false);

        tabbedPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        analysisPanel = new NewAnalysisPanel(this);
        analysisPanel.setFocusable(false);
        processingPanel = new ProcessingPanel(this);
        processingPanel.setFocusable(false);
        resultsPanel = new ResultsPanel(this);
        resultsPanel.setFocusable(false);
        //setPreferredSize(new java.awt.Dimension(defaultSize.width+10, defaultSize.height+56));
        tabbedPanel.addTab("New Analysis", analysisPanel);
        tabbedPanel.addTab("Processing", processingPanel);
        tabbedPanel.addTab("Results", resultsPanel);
    }
    
    public void makeMenuBar(){
        JMenuBar bar = new JMenuBar();
        bar.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        ///////////////////////////////////////
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JMenuItem modules = new JMenuItem("Modules");
        modules.addActionListener((java.awt.event.ActionEvent evt1) ->{
            if(ProcessingPanel.isProcessing() == false){
                if(modManagerOpened == false){
                    modManagerOpened = true;
                    new ModulesManager(this, true).setVisible(true);
                    modManagerOpened = false;
                }
            }else{
                MainGUI.showCannotDoThisWhileProcessing();
            }
        });
        JMenuItem interpreters = new JMenuItem("R Environments");
        interpreters.addActionListener((ActionEvent evt1) -> {
            if(ProcessingPanel.isProcessing() == false){
                analysisPanel.createButton.setEnabled(false);
                InterpreterManagerSwingDialog.openInterpreterManager(() -> {
                    Log.logger.info("Restarting AnalysisPanel");
                    analysisPanel.createInterface();
                    analysisPanel.createButton.setEnabled(true);
                    analysisPanel.updateUnabledScripts();
                });
            }else{
                MainGUI.showCannotDoThisWhileProcessing();
            }
        });
        //JMenuItem settings = new JMenuItem("Configurations");
        //settings.setEnabled(false);
        JMenuItem resetScripts = new JMenuItem("Reset User Scripts");
        resetScripts.addActionListener((java.awt.event.ActionEvent evt1) -> {
            if(ProcessingPanel.isProcessing() == false){
                int reply = JOptionPane.showConfirmDialog(null, "This will erase user created scripts, erase not saved results and close R-Peridot.", "Reset User Scripts", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    //boolean result = peridot.CLI.PeridotCmd.clean();
                    //if(result){
                        close();
                    //}else{
                    //    JOptionPane.showMessageDialog(null, "Could not delete ~/sgs-remake-files/");
                    //}
                }
            }else{
                MainGUI.showCannotDoThisWhileProcessing();
            }
        });
        JMenuItem refreshResults = new JMenuItem("Refresh Results");
        refreshResults.addActionListener((java.awt.event.ActionEvent evt) -> {
            if(ProcessingPanel.isProcessing() == false){
                updateResultsPanel();
            }else{
                MainGUI.showCannotDoThisWhileProcessing();
            }
        });
        toolsMenu.add(modules);
        toolsMenu.add(interpreters);
        toolsMenu.add(resetScripts);
        toolsMenu.add(refreshResults);
        /////////////////////////////////
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JMenuItem about = new JMenuItem("About " + appName);
        about.setEnabled(java.awt.Desktop.isDesktopSupported());
        about.addActionListener((ActionEvent evt) -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI(PeridotConfig.get().rPeridotWebSite + "about.html"));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });

        JMenuItem developerManual = new JMenuItem("Guide for Advanced Usage");
        developerManual.setEnabled(true);
        developerManual.addActionListener((ActionEvent evt) -> {
            File devManualFile = new File(Places.jarFolder + File.separator + "advanced_guide.pdf");
            Global.openFileWithSysApp(devManualFile);
            Log.logger.info("Opening " + devManualFile.getAbsolutePath());
        });
        JMenuItem userManual = new JMenuItem("User Guide");
        userManual.setEnabled(true);
        userManual.addActionListener((ActionEvent evt) -> {
            File manualFile = new File(Places.jarFolder + File.separator + "user_guide.pdf");
            Global.openFileWithSysApp(manualFile);
            Log.logger.info("Opening " + manualFile.getAbsolutePath());
        });
        JMenuItem log = new JMenuItem("Read Log");
        log.setEnabled(true);
        log.addActionListener((ActionEvent evt) -> {
            File logFile = new File(System.getProperty("user.home") + File.separator 
                + ".r-peridot-files"
                + File.separator + "log.txt");
            Global.openFileWithSysApp(logFile);
            Log.logger.info("Opening " + logFile.getAbsolutePath());
        });
        helpMenu.add(userManual);
        helpMenu.add(developerManual);
        helpMenu.add(log);
        helpMenu.add(about);
        /////////////////////////////////
        bar.add(helpMenu);
        bar.add(toolsMenu);
        this.setJMenuBar(bar);
    }
    
    public static void showCannotDoThisWhileProcessing(){
        JOptionPane.showMessageDialog(_instance, "Cannot do this while processing the results", "Operation not permitted", JOptionPane.OK_OPTION);
    }
    
    public static void showRestartPeridotDialog(){
        JOptionPane.showMessageDialog(_instance, "Restart R-Peridot for changes to take effect", "Restart R-Peridot", JOptionPane.OK_OPTION);
    }

    public static void showErrorDialog(String title, String message){
        GUIUtils.showErrorMessageInDialog(title, message,_instance);
    }
    
    public static boolean showYesNoDialog(String msg){
        int reply = JOptionPane.showConfirmDialog(_instance, msg, "Yes or No?"
                , JOptionPane.YES_NO_OPTION);
        return (reply == JOptionPane.YES_OPTION);
    }
    
    public static void goToAnalysisPanel(){
        _instance.tabbedPanel.setSelectedComponent(_instance.analysisPanel);
    }
    
    public static void goToProcessingPanel(){
        _instance.tabbedPanel.setSelectedComponent(_instance.processingPanel);
    }
    
    public static void goToResultsPanel(){
        _instance.tabbedPanel.setSelectedComponent(_instance.resultsPanel);
    }

    public static void updateModulesEnabled(){
        if(_instance != null){
            if(_instance.analysisPanel != null){
                _instance.analysisPanel.updateUnabledScripts();
            }
        }
    }

    public static void updateResultsPanel(){
        _instance.resultsPanel.updateData();
        
    }
    
    public static void close(){
        _instance.dispatchEvent(new WindowEvent(_instance, WindowEvent.WINDOW_CLOSING));
    }
    
    public static void setComponentsEnabled(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
        }
        container.setEnabled(enable);
    }
    
    
    private javax.swing.JTabbedPane tabbedPanel;
}
