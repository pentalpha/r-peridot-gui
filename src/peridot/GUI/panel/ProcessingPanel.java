/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.BigButton;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import peridot.AnalysisParameters;
import peridot.AnalysisData;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import peridot.GUI.MainGUI;
import peridot.GUI.dialog.ScriptOutputDialog;
import peridot.script.RModule;
import peridot.script.ScriptExec;
import javax.swing.JSeparator;
import peridot.Log;
import peridot.script.AnalysisModule;
import peridot.script.DiffExpressionModule;
import peridot.script.Task;
/**
 *
 * @author pentalpha
 */
public class ProcessingPanel extends Panel {
    
    public ImageIcon failIcon, failIconBig, 
            clearIcon, clearIconBig, 
            stopIcon, stopAllIconBig,
            loaderIcon;
    
    JFrame parent;
    private static ProcessingPanel _instance;
    
    private ConcurrentHashMap<String, ScriptOutputDialog> outputDialogs;
    Set<String> scripts;
    private ConcurrentHashMap<String, ScriptProgressMonitorPanel> scriptMonitor;
    protected Thread scriptsStatusWatcher;
    public Runnable statusWatcher;
    public AtomicBoolean isProcessing;
    
    public Task task = null;
    /**
     * Creates new form ProcessingPanel
     */
    public ProcessingPanel(JFrame parent) {
        super();
        
        failIcon = new ImageIcon(getClass().getResource("/peridot/GUI/icons/Delete-icon-24.png"));
        failIconBig = new ImageIcon(getClass().getResource("/peridot/GUI/icons/Delete-icon-32.png"));
        clearIcon = new ImageIcon(getClass().getResource("/peridot/GUI/icons/Clear-Green-Button-icon24.png"));
        clearIconBig = new ImageIcon(getClass().getResource("/peridot/GUI/icons/Clear-Green-Button-icon32.png"));
        stopIcon = new ImageIcon(getClass().getResource("/peridot/GUI/icons/Stop-icon24.png"));
        stopAllIconBig = new ImageIcon(getClass().getResource("/peridot/GUI/icons/Stop-All-icon32.png"));
        loaderIcon = new ImageIcon(getClass().getResource("/peridot/GUI/icons/loading40.gif"));
        isProcessing = new AtomicBoolean(false);
        this.parent = parent;
        customInit();
        this.abortAllButton.setEnabled(false);
        _instance = this;
    }
    
    public static void start(Set<String> scriptsToExec, AnalysisParameters params,
                            Map<String, AnalysisParameters> specificParams,
                            AnalysisData expression){
        if(_instance == null){
            return;
        }
        _instance.process(scriptsToExec, params, specificParams, expression);
    }
    
    public static void cleanMonitorPanels(){
        if(_instance == null){
            return;
        }
        SwingUtilities.invokeLater(() -> {
            _instance.packagesBarsPanel.removeAll();
            _instance.scriptsBarsPanel.removeAll();
        });
    }
    
    public static void preparePanelToStart(){
        if(_instance == null){
            return;
        }
        SwingUtilities.invokeLater(() -> {
            _instance.packagesBarsPanel.revalidate();
            _instance.packagesBarsPanel.repaint();
            _instance.scriptsBarsPanel.revalidate();
            _instance.scriptsBarsPanel.repaint();
            _instance.finalResultDescription.setIcon(_instance.loaderIcon);
            _instance.finalResultDescription.setText("Processing");
        });
    }
    
    public void process(Set<String> scriptsToExec, AnalysisParameters params,
                            Map<String, AnalysisParameters> specificParams,
                            AnalysisData expression){
        MainGUI.goToProcessingPanel();
        scripts = new HashSet<>();
        outputDialogs = new ConcurrentHashMap<>();
        scriptMonitor = new ConcurrentHashMap<>();
        task = new Task(scriptsToExec, params, specificParams, expression);
        scriptMonitor = new ConcurrentHashMap<>();
        cleanMonitorPanels();
        
        isProcessing.set(true);
        task.start();
        MainGUI.updateResultsPanel();
        
        for(Map.Entry<String, ScriptExec> pair : task.scriptExecs.entrySet()){
            ScriptOutputDialog outputDialog = 
                            new ScriptOutputDialog(parent,
                            false,
                            pair.getKey(),
                            pair.getValue().output);
            ScriptProgressMonitorPanel monitorPanel = new
                    ScriptProgressMonitorPanel(pair.getValue(), outputDialog);
            outputDialogs.put(pair.getKey(), outputDialog);
            scripts.add(pair.getKey());
            //Log.info("Calling to add monitor panel for " + pair.getKey());
            addScriptMonitor(pair.getKey(), monitorPanel, pair.getValue());
        }
        
        preparePanelToStart();
        
        scriptsStatusWatcher = new Thread(() -> {
            watchForUpdatesOnTask();
        });
        scriptsStatusWatcher.start();
    }
    
    public static boolean isProcessing(){
        return _instance.isProcessing.get();
    }
    
    public void watchForUpdatesOnTask(){
        /**
         * -2 = waiting
         * -1 = processing
         * 0 = failed
         * 1 = some failed
         * 2 = success
         */
        //Map<String, Integer> scriptState;
        //scriptState = new HashMap<>();
        while(task.isProcessing() || task.isNotStarted()){
            try{
                Thread.sleep(500);
            }catch(java.lang.InterruptedException ex){
                ex.printStackTrace();
                break;
            }
            if(task.isProcessing()){
                updateMonitorState();
            }
        }
        try{
            Thread.sleep(500);
        }catch(java.lang.InterruptedException ex){
            ex.printStackTrace();
        }
        updateMonitorState();
        allFinished();
        isProcessing.set(false);
    }
    
    private void updateMonitorState(){
        for(Map.Entry<String, ScriptExec> pair : task.scriptExecs.entrySet()){
            Task.WaitState waitState = task.waitState.get(pair.getKey());
            ScriptProgressMonitorPanel panel = scriptMonitor.get(pair.getKey());
            if(panel != null){
                if(waitState.equals(Task.WaitState.WAITING) || waitState.equals(Task.WaitState.READY)){
                    panel.switchToWaitingIcon();
                }else if(waitState.equals(Task.WaitState.PRE_FAILED)){
                    panel.switchToFailIcon();
                }else{
                    if(task.successfulScripts.contains(pair.getKey())){
                        panel.switchToSuccessIcon();
                    }else if(task.failedScripts.contains(pair.getKey())){
                        panel.switchToFailIcon();
                        /*if(pair.getValue().script instanceof AnalysisModule){
                            if(((AnalysisModule)pair.getValue().script).mandatoryFailed){
                                if(!pair.getValue().output.getText().contains(noGenesFoundStr)){
                                    pair.getValue().output.appendLine(noGenesFoundStr);
                                }
                            }
                        }*/
                    }else{
                        panel.switchToStopIcon();
                    }
                }
            }else{
                //Log.info("No monitor panel for " + pair.getKey());
            }
            
        }
    }
    
    private void allFinished(){
        abortAllButton.setEnabled(false);
        if(task.isFailed()){
            this.finalResultDescription.setIcon(this.failIcon);
            this.finalResultDescription.setText("Failed");
        }else if(task.isSomeFailed()){
            this.finalResultDescription.setIcon(this.failIcon);
            this.finalResultDescription.setText("Some failed");
        }else if(task.isSuccess()){
            this.finalResultDescription.setIcon(this.clearIcon);
            this.finalResultDescription.setText("Success");
        }
        
        SwingUtilities.invokeLater(() ->{
            Log.logger.info("Updating resultsPanel now");
            MainGUI.updateResultsPanel();
            Log.logger.info("Switching to resultsPanel now");
            MainGUI.goToResultsPanel();
        });
        
        for(Map.Entry<String, ScriptOutputDialog> pair : outputDialogs.entrySet()){
            pair.getValue().stopFlag.set(false);
        }
    }
    
    public void addScriptMonitor(String name, ScriptProgressMonitorPanel watcher, ScriptExec exec){
        //Log.info("Defining monitor panel for " + name + "...");
        SwingUtilities.invokeLater(() -> {
            JPanel outerPanel;
            if(exec.script instanceof AnalysisModule){
                outerPanel = packagesBarsPanel;
            }else{
                outerPanel = scriptsBarsPanel;
            }
            outerPanel.add(watcher);
            outerPanel.revalidate();
            outerPanel.repaint();
            //Log.info("Finished defining monitor panel for " + name + ".");
        });
        
        scriptMonitor.put(name, watcher);
    }
    
    private void customInit(){
        setMinimumSize(new java.awt.Dimension(546, 495));
        setPreferredSize(MainGUI.defaultSize);
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 13, 6));
        
        Dimension scriptPanelSize = new Dimension(250, MainGUI.defaultSize.height-70);
        Dimension innerScriptPanelSize = new Dimension (scriptPanelSize.width-5, 
                                                     scriptPanelSize.height-36);
        
        Panel leftPanel = new Panel();
        leftPanel.setPreferredSize(scriptPanelSize);
        packagesLabel = new BiggerLabel();
        packagesLabel.setText("Analysis Modules:");
        packagesBarsPanel = new Panel();
        packagesBarsPanel.setMaximumSize(new java.awt.Dimension(470, 3000));
        packagesBarsPanel.setLayout(new BoxLayout(packagesBarsPanel, BoxLayout.PAGE_AXIS));
        packagesScroller = new javax.swing.JScrollPane(packagesBarsPanel);
        packagesScroller.setPreferredSize(innerScriptPanelSize);
        leftPanel.add(packagesLabel);
        leftPanel.add(packagesScroller);
        
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator1.setPreferredSize(new java.awt.Dimension(2, innerScriptPanelSize.height));
        jSeparator1.setOrientation(JSeparator.VERTICAL);
        
        Panel rightPanel = new Panel();
        rightPanel.setPreferredSize(scriptPanelSize);
        othersLabel = new BiggerLabel();
        othersLabel.setText("Post Analysis Modules:");
        scriptsBarsPanel = new Panel();
        scriptsBarsPanel.setMaximumSize(new java.awt.Dimension(470, 3000));
        scriptsBarsPanel.setLayout(new BoxLayout(scriptsBarsPanel, BoxLayout.PAGE_AXIS));
        otherResultsScroller = new javax.swing.JScrollPane(scriptsBarsPanel);
        otherResultsScroller.setPreferredSize(innerScriptPanelSize);
        rightPanel.add(othersLabel);
        rightPanel.add(otherResultsScroller);
        
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator2.setPreferredSize(new java.awt.Dimension(500, 2));
        abortAllButton = new BigButton();
        abortAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/peridot/GUI/icons/Stop-All-icon32.png"))); // NOI18N
        abortAllButton.setText("Abort All");
        abortAllButton.setPreferredSize(new java.awt.Dimension(180, 45));
        abortAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                abortAllButtonActionPerformed(evt);
            }
        });
        finalResultDescription = new BigLabel();
        finalResultDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        finalResultDescription.setText("Success/Some errors/Failed");
        finalResultDescription.setPreferredSize(new java.awt.Dimension(160, 45));
        
        add(leftPanel);
        add(jSeparator1);
        add(rightPanel);
        add(jSeparator2);
        add(abortAllButton);
        add(finalResultDescription);
    }
    
    private void abortAllButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        // TODO add your handling code here:
        for(Map.Entry<String, ScriptProgressMonitorPanel> pair : this.scriptMonitor.entrySet()){
            pair.getValue().requireToAbort();
        }
    }
    
    private javax.swing.JButton abortAllButton;
    private javax.swing.JLabel finalResultDescription;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JScrollPane otherResultsScroller;
    private javax.swing.JLabel othersLabel;
    private javax.swing.JPanel packagesBarsPanel;
    private javax.swing.JLabel packagesLabel;
    private javax.swing.JScrollPane packagesScroller;
    private javax.swing.JPanel scriptsBarsPanel;

    String noGenesFoundStr = "Warning: "
            + "This Analysis Module dit not found genes "
            + "on the given data.";
    
}
