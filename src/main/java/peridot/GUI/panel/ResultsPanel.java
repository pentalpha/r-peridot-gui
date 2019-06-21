/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import peridot.Archiver.Places;
import peridot.GUI.GUIUtils;
import peridot.GUI.MainGUI;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.*;
import peridot.GUI.component.Button;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.dialog.PackagesResultsDialog;
import peridot.GUI.dialog.ScriptResultsDialog;
import peridot.GUI.Resources;
import peridot.Log;
import peridot.Operations;
import peridot.script.RModule;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 *
 * @author pentalpha
 */
public class ResultsPanel extends Panel {

    JFrame parent;
    private HashMap<String, File> analysisModules;
    private HashMap<String, File> postAnalysisModules;
    private File vennDiagramDir = null;

    /**
     * Creates new form ResultsPanel
     * @param parent
     */
    public ResultsPanel(JFrame parent) {
        super();
        this.postAnalysisModules = new HashMap<>();
        this.analysisModules = new HashMap<>();

        this.parent = parent;
        customInit();
        updateData();
    }

    private void updateAvailableResults(){
        Set<File> scriptResultsFolders = this.getScriptResultsFolders();
        updatePostAnalysisModules(scriptResultsFolders);
        updateAnalysisModules(scriptResultsFolders);
    }

    private void updatePostAnalysisModules(Set<File> scriptResultsFolders) {
        //Log.logger.info("Updating available post analysis results");
        vennDiagramDir = null;
        for (String scriptName : RModule.getAvailablePostAnalysisModules()) {
            File dir = null;
            for (File scriptDir : scriptResultsFolders) {
                if (scriptDir.getName().contains(".PostAnalysisModule")
                        && scriptDir.getName().contains(scriptName)) {
                    dir = scriptDir;
                    break;
                }
            }
            if (dir != null) {
                if(scriptName.equals("VennDiagram")){
                    vennDiagramDir = dir;
                    ScriptResultsDialog.fillCountPlots(vennDiagramDir);
                }else {
                    this.postAnalysisModules.put(scriptName, dir);
                }
            }
        }
    }

    private void updateAnalysisModules(Set<File> scriptResultsFolders) {
        //Log.logger.info("Updating available analysis results");
        for (String packName : RModule.getAvailableAnalysisModules()) {
            File dir = null;
            for (File packDir : scriptResultsFolders) {
                //if (packDir.getName().contains("." + Package.class.getSimpleName())
                //       && packDir.getName().contains(packName)) {
                if(packDir.getName().contains(packName + ".AnalysisModule"))
                {
                    dir = packDir;
                    break;
                }
            }
            if (dir != null) {
                this.analysisModules.put(packName, dir);
            }
        }
    }

    public void updateData() {
        //Log.logger.info("updating available results");
        this.analysisModules.clear();
        this.postAnalysisModules.clear();
        updateAvailableResults();

        //updatePackListArea();
        updateUpperPanel();
        updatePostAnalysisButtons();
    }

    private void updateUpperPanel(){
        viewResultsButton.setEnabled(vennDiagramDir != null);
        individualPanel.removeAll();
        if(analysisModules.isEmpty()){
            this.individualPanel.add(new BigLabel("None"));
        }else{
            JLabel individual = new BigLabel("Individual Packages:");
            GUIUtils.setToIdealTextSize(individual);
            this.individualPanel.add(individual);
            for(Map.Entry<String, File> entry : analysisModules.entrySet()){
                Button newButton = new Button();
                newButton.setText(entry.getKey());
                newButton.addActionListener((java.awt.event.ActionEvent evt) -> {
                    (new ScriptResultsDialog(parent, false, entry.getKey(), entry.getValue())).setVisible(true);
                });
                newButton.setPreferredSize(smallButtonSize);
                individualPanel.add(newButton);
            }
        }
    }

    private void updatePackListArea(){
        this.packListArea.removeAll();
        if(analysisModules.isEmpty()){
            this.packListArea.add(new Label("None"));
        }else{
            String[] packNames = analysisModules.keySet().toArray(new String[0]);
            for (int i = 0; i < packNames.length-1; i++) {
                this.packListArea.add(new Label(packNames[i] + ","));
            }
            this.packListArea.add(new Label(packNames[packNames.length-1] + "."));
        }
    }
    
    private void updatePostAnalysisButtons() {
        postAnalysisPanel.removeAll();
        if(postAnalysisModules.isEmpty()){
            JButton button = new BigButton();
            button.setText("No post analysis available");
            button.setPreferredSize(new Dimension(400, 40));
            button.setEnabled(false);
            this.postAnalysisPanel.add(button);
        }else{    
            for (Map.Entry<String, File> pair : postAnalysisModules.entrySet()) {
                JButton button = new BigButton();
                button.setText(pair.getKey());
                button.addActionListener((java.awt.event.ActionEvent evt) -> {
                    (new ScriptResultsDialog(parent, false, pair.getKey(), pair.getValue())).setVisible(true);
                });
                button.setPreferredSize(new Dimension(400, 40));
                button.setIcon(Resources.getImageIcon("Document-icon-32.png"));
                this.postAnalysisPanel.add(button);
            }
        }
    }

    private Set<File> getScriptResultsFolders() {
        Set<File> subFiles;
        Set<File> subDirs = new TreeSet<File>();
        //Log.logger.info("trying to get final results");
        subFiles = new TreeSet<File>(FileUtils.listFilesAndDirs(Places.finalResultsDir,
                TrueFileFilter.TRUE, TrueFileFilter.TRUE));
        for (File file : subFiles) {
            String filePath = file.getAbsolutePath();
            if ((filePath.equals(Places.finalResultsDir.getAbsolutePath()) == false)
                    && (file.getAbsolutePath().contains(".AnalysisModule")
                    || file.getAbsolutePath().contains(".PostAnalysisModule"))
                    && (file.isDirectory() == true)) {
                subDirs.add(file);
            }else{
                //Log.logger.info(file.getName() + " is not a result folder");
            }
        }
        return subDirs;
    }

    private void customInit() {
        setMinimumSize(new java.awt.Dimension(546, 495));
        setPreferredSize(MainGUI.defaultSize);
        
        makeNewUpperPanel();
        makeMiddlePanel();
        makeBottomPanel();
    }

    private void makeNewUpperPanel(){
        Dimension size = new java.awt.Dimension(546, 160);
        Dimension innerSize = new Dimension(size.width, size.height-32);
        Dimension subSize = new java.awt.Dimension(160, innerSize.height);
        smallButtonSize = new Dimension((subSize.width-5)/2, 30);
        upperPanel = new Panel();
        upperPanel.setPreferredSize(size);
        upperPanel.setLayout(new WrapLayout(java.awt.FlowLayout.CENTER, 5, 5));

        topLabel = new BiggerLabel();
        topLabel.setText("Differential Expression:");
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setPreferredSize(new Dimension(250,25));
        upperPanel.add(topLabel);
        //start inner panel
        Panel innerPanel = new Panel();
        innerPanel.setPreferredSize(innerSize);
        innerPanel.setLayout(new WrapLayout(java.awt.FlowLayout.CENTER, 5, 0));

        viewResultsButton = new BigButton();
        viewResultsButton.setText("Consensus");
        viewResultsButton.setPreferredSize(subSize);
        viewResultsButton.setMinimumSize(subSize);

        viewResultsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if(vennDiagramDir != null) {
                (new ScriptResultsDialog(parent, false,
                        "VennDiagram", vennDiagramDir)).setVisible(true);
            }
        });
        viewResultsButton.setIcon(
                Resources.getImageIcon("Clear-Green-Button-icon32.png"));
        innerPanel.add(viewResultsButton);

        JSeparator middleSeparator = new JSeparator(JSeparator.VERTICAL);
        middleSeparator.setPreferredSize(new java.awt.Dimension(3, 90));
        innerPanel.add(middleSeparator);

        individualPanel = new Panel();
        individualPanel.setPreferredSize(new Dimension(subSize.width, subSize.height));
        individualPanel.setLayout(new WrapLayout(java.awt.FlowLayout.CENTER, 3, 3));
        innerPanel.add(individualPanel);
        //end inner panel
        upperPanel.add(innerPanel);

        topSeparator = new javax.swing.JSeparator();
        topSeparator.setPreferredSize(new java.awt.Dimension(520, 3));
        //upperPanel.add(topSeparator);

        add(upperPanel);
        add(topSeparator);
    }
    
    private void makeMiddlePanel(){
        middlePanel = new Panel();
        middlePanel.setPreferredSize(new java.awt.Dimension(546, 330));
        
        successfulOthersLabel = new BigLabel();
        successfulOthersLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        successfulOthersLabel.setText("Post Analysis:");
        GUIUtils.setToIdealTextSize(successfulOthersLabel);
        postAnalysisPanel = new Panel();
        postAnalysisPanel.setPreferredSize(new java.awt.Dimension(545, 270));
        
        middlePanel.add(successfulOthersLabel);
        middlePanel.add(postAnalysisPanel);

        add(middlePanel);
    }
    
    private void makeBottomPanel(){
        bottomPanel = new Panel();
        bottomPanel.setPreferredSize(new java.awt.Dimension(546, 80));
        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        
        bottomSeparator = new javax.swing.JSeparator();
        bottomSeparator.setPreferredSize(new java.awt.Dimension(540, 2));
        
        saveInLabel = new Label();
        saveInLabel.setText("Save in:");
        GUIUtils.setToIdealTextSize(saveInLabel);
        
        saveFolderInputField = new javax.swing.JTextField();
        saveFolderInputField.setMinimumSize(new java.awt.Dimension(40, 25));
        saveFolderInputField.setPreferredSize(new java.awt.Dimension(380, 25));
        saveFolderInputField.setEditable(false);
        
        openSaveFolderFileChooser = new Button();
        openSaveFolderFileChooser.setText("");
        openSaveFolderFileChooser.setPreferredSize(new Dimension(90, 30));
        openSaveFolderFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSaveFolderFileChooserActionPerformed(evt);
            }
        });
        openSaveFolderFileChooser.setIcon(Resources.getImageIcon("open-icon-24.png"));
        
        saveResultsButton = new Button();
        saveResultsButton.setText("Save");
        saveResultsButton.setPreferredSize(new Dimension(90, 30));
        saveResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResultsButtonActionPerformed(evt);
            }
        });
        saveResultsButton.setIcon(Resources.getImageIcon("save-icon-24.png"));
        saveResultsButton.setEnabled(false);
        
        bottomPanel.add(bottomSeparator);
        bottomPanel.add(saveInLabel);
        bottomPanel.add(saveFolderInputField);
        bottomPanel.add(openSaveFolderFileChooser);
        bottomPanel.add(saveResultsButton);

        add(bottomPanel);
    }

    private void openSaveFolderFileChooserActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            if (folder.exists()) {
                saveFolderInputField.setText(folder.getAbsolutePath());
                saveResultsButton.setEnabled(true);
            }
        }
    }

    private void viewResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        (new PackagesResultsDialog(parent, false, analysisModules)).setVisible(true);
    }

    private void saveResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            File saveFolder = new File(saveFolderInputField.getText());
            Operations.saveResultsAt(saveFolder);
        } catch (Exception ex) {
            Log.logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private javax.swing.JPanel bottomPanel;
    private javax.swing.JSeparator bottomSeparator;
    private javax.swing.JPanel innerUpperPanel;
    private javax.swing.JPanel middlePanel;
    private javax.swing.JButton openSaveFolderFileChooser;
    private javax.swing.JPanel packListArea;
    private javax.swing.JPanel postAnalysisPanel;
    private javax.swing.JTextField saveFolderInputField;
    private javax.swing.JLabel saveInLabel;
    private javax.swing.JButton saveResultsButton;
    private javax.swing.JLabel successfulOthersLabel;
    private javax.swing.JLabel topLabel;
    private javax.swing.JSeparator topSeparator;
    private javax.swing.JPanel upperPanel;
    private javax.swing.JButton viewResultsButton;

    private JPanel consensusPanel, individualPanel;
    private Dimension smallButtonSize;
}
