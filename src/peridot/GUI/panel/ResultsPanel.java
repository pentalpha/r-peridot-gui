/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;
import peridot.CLI.PeridotCmd;
import peridot.GUI.component.Label;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.Button;
import peridot.GUI.component.BigButton;
import peridot.Log;
import java.awt.Dimension;
import peridot.GUI.dialog.PackagesResultsDialog;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import peridot.Archiver.Places;
import peridot.GUI.MainGUI;
import peridot.GUI.WrapLayout;
import peridot.GUI.dialog.ScriptResultsDialog;
import peridot.script.RModule;

/**
 *
 * @author pentalpha
 */
public class ResultsPanel extends Panel {

    JFrame parent;
    private HashMap<String, File> packages;
    private HashMap<String, File> scripts;

    /**
     * Creates new form ResultsPanel
     * @param parent
     */
    public ResultsPanel(JFrame parent) {
        super();
        this.scripts = new HashMap<>();
        this.packages = new HashMap<>();

        this.parent = parent;
        customInit();
        updateData();
    }

    public void updateData() {
        //Log.logger.info("updating available results");
        Set<File> scriptResultsFolders = this.getScriptResultsFolders();
        this.packages.clear();
        this.scripts.clear();
        updateScripts(scriptResultsFolders);
        updatePackages(scriptResultsFolders);

        this.packListArea.removeAll();
        if(packages.isEmpty()){
            this.packListArea.add(new Label("None"));
        }else{
            String[] packNames = packages.keySet().toArray(new String[0]);
            for (int i = 0; i < packNames.length-1; i++) {
                this.packListArea.add(new Label(packNames[i] + ","));
            }
            this.packListArea.add(new Label(packNames[packNames.length-1] + "."));
        }
        
        updateScriptsButtons();
    }

    private void updateScripts(Set<File> scriptResultsFolders) {
        //Log.logger.info("Updating available post analysis results");
        for (String scriptName : RModule.getAvailableScripts()) {
            File dir = null;
            for (File scriptDir : scriptResultsFolders) {
                if (scriptDir.getName().contains(".PostAnalysisModule")
                        && scriptDir.getName().contains(scriptName)) {
                    dir = scriptDir;
                    break;
                }
            }
            if (dir != null) {
                this.scripts.put(scriptName, dir);
                
            }
        }
    }

    private void updatePackages(Set<File> scriptResultsFolders) {
        //Log.logger.info("Updating available analysis results");
        for (String packName : RModule.getAvailablePackages()) {
            File dir = null;
            for (File packDir : scriptResultsFolders) {
                //if (packDir.getName().contains("." + Package.class.getSimpleName())
                //       && packDir.getName().contains(packName)) {
                if(packDir.getName().contains(packName + ".AnalysisModule"))
                {
                    dir = packDir;
                    break;
                }else{
                    //Log.logger.info(packDir.getName() + " not contains " + packName + ".AnalysisModule");
                }
            }
            if (dir != null) {
                this.packages.put(packName, dir);
            }
        }
    }
    
    private void updateScriptsButtons() {
        postAnalysisPanel.removeAll();
        if(scripts.isEmpty()){
            JButton button = new BigButton();
            button.setText("No post analysis available");
            button.setPreferredSize(new Dimension(400, 40));
            button.setEnabled(false);
            this.postAnalysisPanel.add(button);
        }else{    
            for (Map.Entry<String, File> pair : scripts.entrySet()) {
                JButton button = new BigButton();
                button.setText(pair.getKey());
                button.addActionListener((java.awt.event.ActionEvent evt) -> {
                    (new ScriptResultsDialog(parent, false, pair.getKey(), pair.getValue())).setVisible(true);
                });
                button.setPreferredSize(new Dimension(400, 40));
                button.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/Document-icon-32.png")));
                this.postAnalysisPanel.add(button);
            }
        }
    }

    private Set<File> getScriptResultsFolders() {
        Set<File> subFiles;
        Set<File> subDirs = new TreeSet<File>();
        Log.logger.info("trying to get final results");
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
        
        makeUpperPanel();
        makeMiddlePanel();
        makeBottomPanel();
    }
    
    private void makeUpperPanel(){
        upperPanel = new Panel();
        upperPanel.setPreferredSize(new java.awt.Dimension(546, 140));
        upperPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        
        successfulPacksLabel1 = new BiggerLabel();
        successfulPacksLabel1.setText("Successful Analysis:");
        
        viewResultsButton = new BigButton();
        viewResultsButton.setText("View All");
        viewResultsButton.setPreferredSize(new Dimension(190, 120));
        viewResultsButton.setMinimumSize(new Dimension(190, 120));
        //viewResultsButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        viewResultsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            viewResultsButtonActionPerformed(evt);
        });
        viewResultsButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/many-documents-32.png")));
        
        packListArea = new Panel();
        packListArea.setPreferredSize(new Dimension(140, 80));
        packListArea.setLayout(new WrapLayout());
        
        /*packListArea.setEditable(false);
        packListArea.setColumns(12);
        packListArea.setRows(5);
        packListArea.setTabSize(4);
        packListArea.setText("<pack> ");
        packListArea.setWrapStyleWord(true);*/
        
        innerUpperPanel = new Panel();
        
        javax.swing.GroupLayout innerUpperPanelLayout = new javax.swing.GroupLayout(innerUpperPanel);
        innerUpperPanel.setLayout(innerUpperPanelLayout);
        innerUpperPanelLayout.setHorizontalGroup(
                innerUpperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 354, Short.MAX_VALUE)
                .addGroup(innerUpperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(innerUpperPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(innerUpperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(successfulPacksLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(packListArea, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(viewResultsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        innerUpperPanelLayout.setVerticalGroup(
                innerUpperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 131, Short.MAX_VALUE)
                .addGroup(innerUpperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(innerUpperPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(innerUpperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(innerUpperPanelLayout.createSequentialGroup()
                                                .addComponent(successfulPacksLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(packListArea, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                        .addComponent(viewResultsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        
        topSeparator = new javax.swing.JSeparator();
        topSeparator.setPreferredSize(new java.awt.Dimension(520, 2));
        
        upperPanel.add(innerUpperPanel);
        upperPanel.add(topSeparator);

        add(upperPanel);
    }
    
    private void makeMiddlePanel(){
        middlePanel = new Panel();
        middlePanel.setPreferredSize(new java.awt.Dimension(546, 360));
        
        successfulOthersLabel = new BigLabel();
        successfulOthersLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        successfulOthersLabel.setText("Successful Post Analysis:");
        
        postAnalysisPanel = new Panel();
        postAnalysisPanel.setPreferredSize(new java.awt.Dimension(545, 300));
        
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
        openSaveFolderFileChooser.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/open-icon-24.png")));
        
        saveResultsButton = new Button();
        saveResultsButton.setText("Save");
        saveResultsButton.setPreferredSize(new Dimension(90, 30));
        saveResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResultsButtonActionPerformed(evt);
            }
        });
        saveResultsButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/save-icon-24.png")));
        saveResultsButton.setEnabled(false);
        
        bottomPanel.add(bottomSeparator);
        bottomPanel.add(saveInLabel);
        bottomPanel.add(saveFolderInputField);
        bottomPanel.add(openSaveFolderFileChooser);
        bottomPanel.add(saveResultsButton);

        add(bottomPanel);
    }

    private void openSaveFolderFileChooserActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
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
        // TODO add your handling code here:
        (new PackagesResultsDialog(parent, false, packages)).setVisible(true);
    }

    private void saveResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        try {
            File saveFolder = new File(saveFolderInputField.getText());
            PeridotCmd.saveResultsAt(saveFolder);
        } catch (Exception ex) {
            Log.logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JLabel successfulPacksLabel1;
    private javax.swing.JSeparator topSeparator;
    private javax.swing.JPanel upperPanel;
    private javax.swing.JButton viewResultsButton;
    // End of variables declaration//GEN-END:variables
}
