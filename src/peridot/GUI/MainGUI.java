/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import peridot.GUI.dialog.modulesManager.ModulesManager;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FontMetrics;
import peridot.GUI.component.TabbedPane;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import peridot.GUI.panel.ResultsPanel;
import peridot.GUI.panel.ProcessingPanel;
import peridot.GUI.panel.NewAnalysisPanel;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import peridot.Archiver.Places;
import peridot.script.RModule;
import java.lang.ClassNotFoundException;
import javax.imageio.ImageIO;
import javax.swing.*;

import peridot.GUI.component.Panel;
import peridot.Global;
import peridot.Log;
import static peridot.Log.logger;
/**
 *
 * @author pentalpha
 */
public class MainGUI extends javax.swing.JFrame {
    public static final java.awt.Dimension defaultSize = new java.awt.Dimension(546, 600);
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
        Places.createPeridotDir();
        Places.updateModulesDir(false);
        //Util.getRPath();
        
        //Log.logger.info("trying to load scripts");
        RModule.loadUserScripts();
        if(RModule.getAvailableScripts().size() == 0){
            JOptionPane.showMessageDialog(null, "Scripts could not be loaded. We recommend using Menu > Tools > Reset User Scripts.");
        }
        //Log.logger.info("scripts loaded");
        this.setTitle(appName);
        //this.setLayout(new BorderLayout());
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        makeMenuBar();
        Panel contentPane = new Panel();
        contentPane.setLayout(new BorderLayout());
        tabbedPanel = new TabbedPane();
        tabbedPanel.setUI(new org.pushingpixels.substance.internal.ui.SubstanceTabbedPaneUI() {
            @Override
            protected int calculateTabWidth(int i, int i1, FontMetrics fm){
                //return super.calculateTabWidth(i, i1, fm) + 30;
                return 184;
            }
        });
        makeTabs();
        contentPane.add(tabbedPanel, java.awt.BorderLayout.CENTER);
        this.add(contentPane);
        pack();
        _instance = this;
        Main.logoLoadingFrame.setVisible(false);
        if(Places.rExec == null){
            JOptionPane.showMessageDialog(null, "R portable not found, using system PATH instead.");
        }
        setLocationRelativeTo(null);
        this.setIconImage(getDefaultIcon());
    }
    
    public static MainGUI getInstance(){
        return MainGUI._instance;
    }
    
    public Image getDefaultIcon(){
        Image frameIcon = null;
        try{
            frameIcon = ImageIO.read(getClass().getClassLoader().getResource("peridot/GUI/icons/logo64.png"));
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
        
        //getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabbedPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        //getContentPane().add(tabbedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
        //contentPane.add(tabbedPanel, java.awt.BorderLayout.CENTER);
        //pack();
        
        //Log.logger.info("init components done");
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
        //toolsMenu.add(settings);
        toolsMenu.add(resetScripts);
        toolsMenu.add(refreshResults);
        /////////////////////////////////
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JMenuItem about = new JMenuItem("About " + appName);
        about.setEnabled(java.awt.Desktop.isDesktopSupported());
        about.addActionListener((ActionEvent evt) -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI("http://www.bioinformatics-brazil.org/r-peridot/about.html"));
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        JMenuItem developerManual = new JMenuItem("How do R-Peridot Works?");
        developerManual.setEnabled(true);
        developerManual.addActionListener((ActionEvent evt) -> {
            File devManualFile = new File(Places.jarFolder + File.separator + "how-do-r-peridot-modules-work.pdf");
            Global.openFileWithSysApp(devManualFile);
            Log.logger.info("Opening " + devManualFile.getAbsolutePath());
        });
        helpMenu.add(developerManual);
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
            /*if (component instanceof Container) {
                setComponentsEnabled((Container)component, enable);
            }*/
        }
        container.setEnabled(enable);
    }
    
    
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables
}
