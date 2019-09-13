/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.Archiver.Manager;
import peridot.Archiver.Spreadsheet;
import peridot.GUI.JTableUtils;
import peridot.GUI.component.BigButton;
import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.TabbedPane;
import peridot.Global;
import peridot.Log;
import peridot.GUI.Resources;
import peridot.script.RModule;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
/**
 *
 * @author pithagoras
 */
public class ViewResultsPanel extends Panel {
    HashMap<String, JPanel> resultViewers;
    String scriptName;
    File resultsDir;
    boolean analysisModule;
    /**
     * Creates new form PackageResultsPanel
     */
    public ViewResultsPanel(String scriptName, File resultsDir, boolean analysisModule) {
        super();
        this.scriptName = scriptName;
        this.resultsDir = resultsDir;
        this.analysisModule = analysisModule;
        initComponents();
        resultViewers = new HashMap<>();
        RModule script = RModule.availableModules.get(scriptName);
        Set<String> allResults = script.results;
        if(script.mandatoryResults.size() > 0)
        {
            String[] mandatoryResults = script.mandatoryResults.toArray(new String[script.mandatoryResults.size()]);
            Arrays.sort(mandatoryResults);
            for(int i = mandatoryResults.length-1; i >= 0; i--){
                addResultTab(mandatoryResults[i]);
            }
        }

        for(String result : allResults)
        {
            if(!script.mandatoryResults.contains(result)){
                addResultTab(result);
            }
        }
    }

    private void addResultTab(String result){
        JComponent content = null;
        File file = new File(resultsDir.getAbsolutePath() + File.separator + result);
        if(file.exists())
        {
            boolean unknownFormat = false;
            if(Manager.isImageFile(result)){
                content = peridot.GUI.component.Label.getImageLabel(file);
            }else if (Global.fileIsPlainText(file)){
                try {
                    Spreadsheet spreadsheet = new Spreadsheet(file, false);
                    content = JTableUtils.getJTable(spreadsheet);
                }catch (IOException ex){
                    Log.logger.warning("Could not open file " + file.getAbsolutePath());
                    ex.printStackTrace();
                }
                if(content == null){
                    content = ViewResultsPanel.getEmptyTableMessage(analysisModule);
                }
            }else{
                content = ViewResultsPanel.getUnknownFormatMessage(file);
                unknownFormat = true;
            }
            JScrollPane scroller = new JScrollPane(content);
            scroller.getViewport().setBackground(Color.white);
            //scroller.setLayout(new java.awt.CardLayout());
            JPanel panel = new Panel();
            panel.setLayout(new java.awt.CardLayout());
            panel.add(scroller);
            resultViewers.put(result, panel);
            if(unknownFormat){
                tabsPanel.add("Open " + result, panel);
            }else{
                tabsPanel.add(result, panel);
            }
        }
    }
    
    private static JPanel getUnknownFormatMessage(File file){
        JPanel panel = new Panel();
        panel.setMaximumSize(new java.awt.Dimension(300, 3000));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel message = new BiggerLabel();
        message.setText(file.getName() + " is in a format unknown to R-Peridot.");

        BigButton openButton = new BigButton();
        openButton.setText("Open With External Program");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Global.openFileWithSysApp(file);
            }
        });
        openButton.setSize(new Dimension(120, 70));
        openButton.setIcon(Resources.getImageIcon("Write-Document-icon16.png"));
        panel.add(message);
        //panel.add(message2);
        panel.add(openButton);
        
        return panel;
    }

    private static JPanel getEmptyTableMessage(boolean analysisModule){
        JPanel panel = new Panel();
        panel.setMaximumSize(new java.awt.Dimension(300, 3000));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel message = new BiggerLabel();
        if(analysisModule){
            message.setText("No differential expression found by this module.");
        }else{
            message.setText("Empty table or list.");
        }
        panel.add(message);
        return panel;
    }


    private void initComponents() {
        tabsPanel = new TabbedPane();
        setLayout(new java.awt.CardLayout());
        add(tabsPanel, "card2");
    }

    private javax.swing.JTabbedPane tabsPanel;
}
