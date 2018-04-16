/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Dialog;
import peridot.GUI.panel.ViewResultsPanel;
import peridot.Log;
import peridot.script.RModule;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author Pitágoras Alves
 */
public class ScriptResultsDialog extends Dialog {
    private static HashMap<String, File> countPlots = null;

    public static void cleanCountPlots(){
        if(countPlots != null){
            countPlots.clear();
        }
    }

    public static void fillCountPlots(File vennDiagramDir){
        countPlots = new HashMap<>();
        File countPlotsDir = new File(vennDiagramDir.getAbsolutePath() + File.separator + "countPlots");
        File[] files = countPlotsDir.listFiles();
        if(files != null){
            if(files.length >= 1){
                for(File file : files){
                    if(file.isFile()){
                        String fileName = file.getName();
                        fileName = fileName.substring(0, fileName.length()-4);
                        countPlots.put(fileName, file);
                        //Log.logger.info(fileName + " -> " + file.getName());
                    }
                }
            }
        }
    }

    public static File getCountPlotFile(String key){
        if(countPlots != null){
            if(countPlots.containsKey(key)){
                return countPlots.get(key);
            }else{
                Log.logger.info("Could not find " + key);
                return null;
            }
        }else{
            Log.logger.info("Count plots not listed yet.");
            return null;
        }
    }

    ViewResultsPanel results;
    /**
     * Creates new form ScriptResultsViewerDialog
     */
    public ScriptResultsDialog(java.awt.Frame parent, boolean modal, String scriptName, File resultsDir) {
        super(parent, modal);
        initComponents();
        setTitle(scriptName + " results");
        results = new ViewResultsPanel(scriptName, resultsDir, RModule.getAvailableAnalysisModules().contains(scriptName));
        this.getContentPane().add(results);
        pack();
    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.CardLayout());

        pack();
    }
}
