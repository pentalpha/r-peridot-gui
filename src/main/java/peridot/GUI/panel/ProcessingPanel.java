/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.AnalysisData;
import peridot.AnalysisParameters;
import peridot.GUI.MainGUI;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.dialog.ScriptOutputDialog;
import peridot.Log;
import peridot.script.AnalysisModule;
import peridot.script.RModule;
import peridot.script.Task;
import peridot.tree.PipelineNode;
import peridot.GUI.Resources;
import peridot.GUI.GUIUtils;

import javax.swing.*;

//import com.sun.xml.internal.ws.api.pipe.Pipe;

import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
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
        
        failIcon = Resources.getImageIcon("Delete-icon-24.png");
        failIconBig = Resources.getImageIcon("Delete-icon-32.png");
        clearIcon = Resources.getImageIcon("Clear-Green-Button-icon24.png");
        clearIconBig = Resources.getImageIcon("Clear-Green-Button-icon32.png");
        stopIcon = Resources.getImageIcon("Stop-icon24.png");
        stopAllIconBig = Resources.getImageIcon("Stop-All-icon32.png");
        loaderIcon = Resources.getImageIcon("loading40.gif");
        isProcessing = new AtomicBoolean(false);
        this.parent = parent;
        customInit();
        //this.abortAllButton.setEnabled(false);
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
            //GUIUtils.setToIdealTextSize(_instance.finalResultDescription);
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
        
        for(String module_name : task.getModules()){
            ScriptOutputDialog outputDialog = 
                            new ScriptOutputDialog(parent,
                            false,
                            module_name);
            ScriptProgressMonitorPanel monitorPanel = new
                    ScriptProgressMonitorPanel(module_name, task.getPipeline(), outputDialog);
            outputDialogs.put(module_name, outputDialog);
            scripts.add(module_name);
            //Log.info("Calling to add monitor panel for " + pair.getKey());
            addScriptMonitor(module_name, monitorPanel);
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
            Thread.sleep(300);
        }catch(java.lang.InterruptedException ex){
            ex.printStackTrace();
        }
        updateMonitorState();
        allFinished();
        isProcessing.set(false);
    }
    
    private void updateMonitorState(){
        for(ScriptOutputDialog outputDialog : outputDialogs.values()){
            outputDialog.updateText();
        }

        for(Map.Entry<String, PipelineNode.Status> pair : task.getModuleStatus().entrySet()){
            ScriptProgressMonitorPanel panel = scriptMonitor.get(pair.getKey());
            if(panel != null){
                if(pair.getValue().equals(PipelineNode.Status.QUEUE) 
                || pair.getValue().equals(PipelineNode.Status.READY)){
                    panel.switchToWaitingIcon();
                }else if(pair.getValue().equals(PipelineNode.Status.FAILED)){
                    panel.switchToFailIcon();
                }else if(pair.getValue().equals(PipelineNode.Status.RUNNING)){
                    panel.switchToStopIcon();
                }else if(pair.getValue().equals(PipelineNode.Status.DONE)){
                    panel.switchToSuccessIcon();
                }
            }
        }
    }
    
    private void allFinished(){
        //abortAllButton.setEnabled(false);
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
        //GUIUtils.setToIdealTextSize(finalResultDescription);
        
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
    
    public void addScriptMonitor(String name, ScriptProgressMonitorPanel watcher){
        //Log.info("Defining monitor panel for " + name + "...");
        SwingUtilities.invokeLater(() -> {
            JPanel outerPanel;
            if(RModule.availableModules.get(name) instanceof AnalysisModule){
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
        GUIUtils.setToIdealTextSize(packagesLabel);
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
        GUIUtils.setToIdealTextSize(othersLabel);
        scriptsBarsPanel = new Panel();
        scriptsBarsPanel.setMaximumSize(new java.awt.Dimension(470, 3000));
        scriptsBarsPanel.setLayout(new BoxLayout(scriptsBarsPanel, BoxLayout.PAGE_AXIS));
        otherResultsScroller = new javax.swing.JScrollPane(scriptsBarsPanel);
        otherResultsScroller.setPreferredSize(innerScriptPanelSize);
        rightPanel.add(othersLabel);
        rightPanel.add(otherResultsScroller);
        
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator2.setPreferredSize(new java.awt.Dimension(500, 2));
    
        finalResultDescription = new BigLabel();
        finalResultDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        finalResultDescription.setText("No analysis running");
        finalResultDescription.setPreferredSize(new java.awt.Dimension(500, 45));
        //GUIUtils.setToIdealTextSize(finalResultDescription);

        add(leftPanel);
        add(jSeparator1);
        add(rightPanel);
        add(jSeparator2);
        add(finalResultDescription);
    }

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
            + "This Analysis Module did not found genes "
            + "on the given data.";
}
