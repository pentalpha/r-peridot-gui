/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.TabbedPane;
import peridot.GUI.component.BigButton;
import peridot.Archiver.Spreadsheet;
import peridot.Archiver.Manager;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import peridot.script.RModule;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.text.View;

import org.apache.commons.lang3.SystemUtils;
import peridot.GUI.JTableUtils;
import peridot.Global;
import peridot.Log;
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
        RModule script = RModule.availableScripts.get(scriptName);
        Set<String> allResults = script.results;
        if(script.mandatoryResults.size() > 0){
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
            if(Manager.isImageFile(result)){
                content = peridot.GUI.component.Label.getImageLabel(file);
            }else if (Spreadsheet.fileIsCSVorTSV(file)){
                content = JTableUtils.getTable(file, analysisModule);
                if(content == null){
                    content = ViewResultsPanel.getEmptyTableMessage(analysisModule);
                }
            }else{
                content = ViewResultsPanel.getUnknownFormatMessage(file);
            }
            JScrollPane scroller = new JScrollPane(content);
            scroller.getViewport().setBackground(Color.white);
            //scroller.setLayout(new java.awt.CardLayout());
            JPanel panel = new Panel();
            panel.setLayout(new java.awt.CardLayout());
            panel.add(scroller);
            resultViewers.put(result, panel);
            tabsPanel.add(result, panel);
        }
    }
    
    private static JPanel getUnknownFormatMessage(File file){
        JPanel panel = new Panel();
        panel.setMaximumSize(new java.awt.Dimension(300, 3000));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel message = new BiggerLabel();
        message.setText(file.getName() + " is in a format unknown to R-Peridot.");
        //JLabel message2 = new Label();
        //message2.setText("Please use other program to view this result.");
        BigButton openButton = new BigButton();
        openButton.setText("Open With External Program");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Global.openFileWithSysApp(file);
            }
        });
        openButton.setSize(new Dimension(120, 70));
        openButton.setIcon(new ImageIcon(ViewResultsPanel.class.getClass().getResource("/peridot/GUI/icons/Write-Document-icon16.png")));
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
