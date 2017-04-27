/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog.modulesManager;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import peridot.GUI.MainGUI;
import static peridot.GUI.MainGUI._instance;
import peridot.GUI.component.*;
import peridot.Log;
import peridot.script.AnalysisScript;
import peridot.script.PostAnalysisScript;
import peridot.script.RScript;

/**
 *
 * @author pentalpha
 */
public class ModulesManager extends Dialog {

    public String selectedScript;
    Dimension dialogSize;
    Dimension containerSize;
    Dimension scrollerSize;
    Dimension buttonSize;
    Dimension buttonContainerSize;
    int wGap, hGap;
    public ModulesManager(JFrame parent, boolean modal) {
        super(parent, modal);
        selectedScript = "";
        init();
    }
    
    private void init() {
        //this.setPreferredSize(new Dimension(380, 250));
        //this.setSize(new Dimension(380, 250));
        wGap = 3;
        hGap = 3;
        containerSize = new Dimension(210, 200);
        scrollerSize = new Dimension(containerSize.width-10, containerSize.height-30);
        buttonContainerSize = new Dimension(containerSize.width-30, containerSize.height);
        buttonSize = new Dimension(buttonContainerSize.width - 5, (containerSize.height-hGap*5)/6-2);
        dialogSize = new Dimension(containerSize.width*2 + buttonContainerSize.width + wGap*2 + 110,
                containerSize.height+37);
        
        this.setTitle("Modules Manager");
        this.setIconImage(getDefaultIcon());
        this.setMinimumSize(dialogSize);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, wGap, 1));
        setResizable(false);
        this.setIconImage(MainGUI.getInstance().getDefaultIcon());
        makeModulesContainer();
        makeButtonsContainer();
        add(modulesContainer);
        add(buttonsContainer);
        
        this.setLocationRelativeTo(null);
    }
    
    public Image getDefaultIcon(){
        Image frameIcon = null;
        /*try{
            frameIcon = ImageIO.read(getClass().getClassLoader().getResource("peridot/GUI/icons/logo64.png"));
        }catch(Exception ex){
            ex.printStackTrace();
            Log.logger.info("Default ImageIcon not loaded");
        }*/
        return frameIcon;
    }
    
    private void makeModulesContainer(){
        modulesContainer = new Panel();
        modulesContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));
        
        
        JList list = new JList(RScript.getAvailablePackages());
        JList list2 = new JList(RScript.getAvailablePostAnalysisScripts());
        analysisModListContainer = new Panel();
        analysisModListContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        analysisModListContainer.setPreferredSize(containerSize);
        analysisModLabel = new BigLabel("Analysis");
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener((ListSelectionEvent e) -> {
            String selected = (String)list.getSelectedValue();
            if(selected == null){
                selectedScript = "";
            }else{
                if(list2.getSelectedValue() != null){
                    list2.clearSelection();
                }
                selectedScript = selected;
            }
            updateButtons();
        });
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(scrollerSize);
        analysisModListContainer.add(analysisModLabel);
        analysisModListContainer.add(listScroller);
        
        postAnalysisModListContainer = new Panel();
        postAnalysisModListContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        postAnalysisModListContainer.setPreferredSize(containerSize);
        postAnalysisModLabel = new BigLabel("Post Analysis");
        list2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list2.setLayoutOrientation(JList.VERTICAL);
        list2.setVisibleRowCount(-1);
        list2.addListSelectionListener((ListSelectionEvent e) -> {
            String selected = (String)list2.getSelectedValue();
            if(selected == null){
                selectedScript = "";
            }else{
                if(list.getSelectedValue() != null){
                    list.clearSelection();
                }
                selectedScript = selected;
            }
            updateButtons();
        });
        JScrollPane listScroller2 = new JScrollPane(list2);
        listScroller2.setPreferredSize(scrollerSize);
        postAnalysisModListContainer.add(postAnalysisModLabel);
        postAnalysisModListContainer.add(listScroller2);
        
        modulesContainer.add(analysisModListContainer);
        modulesContainer.add(postAnalysisModListContainer);
    }
    
    private void makeButtonsContainer(){
        buttonsContainer = new Panel();
        buttonsContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, hGap));
        buttonsContainer.setPreferredSize(containerSize);
        
        addButton = getButton("Create Module", buttonSize, true,
                new ImageIcon(getClass().getResource("/peridot/GUI/icons/add-green-button-icon-24.png")));
        addButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addScript();
        });
        
        importButton = getButton("Import", buttonSize, true,
                new ImageIcon(getClass().getResource("/peridot/GUI/icons/import-icon-24.png")));
        importButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            boolean success = importScript();
            if(success){
                MainGUI.showRestartPeridotDialog();
                closeThisAndMainGUI();
            }
        });
        
        exportButton = getButton("Export", buttonSize, false,
                new ImageIcon(getClass().getResource("/peridot/GUI/icons/export-icon-24.png")));
        exportButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            exportScript(selectedScript);
        });
        
        editButton = getButton("Edit", buttonSize, false,
                new ImageIcon(getClass().getResource("/peridot/GUI/icons/write-icon-24.png")));
        editButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            editScript(selectedScript);
        });
        
        detailsButton = getButton("Details", buttonSize, false,
                new ImageIcon(getClass().getResource("/peridot/GUI/icons/Document-icon-24.png")));
        detailsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            scriptDetails(selectedScript);
        });
        
        deleteButton = getButton("Delete", buttonSize, false, 
                new ImageIcon(getClass().getResource("/peridot/GUI/icons/Delete-icon-24.png")));
        deleteButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            deleteScript(selectedScript);
        });
        
        buttonsContainer.add(addButton);
        buttonsContainer.add(importButton);
        buttonsContainer.add(exportButton);
        buttonsContainer.add(editButton);
        buttonsContainer.add(detailsButton);
        buttonsContainer.add(deleteButton);
    }
    
    private BigButton getButton(String text, Dimension size, boolean enabled, ImageIcon icon){
        BigButton button = new BigButton();
        button.setText(text);
        button.setIcon(icon);
        button.setSize(size);
        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setEnabled(enabled);
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setHorizontalAlignment(SwingConstants.RIGHT);
        return button;
    }
    
    private void updateButtons(){
        if(RScript.availableScripts.keySet().contains(selectedScript)){
            deleteButton.setEnabled(true);
            detailsButton.setEnabled(true);
            exportButton.setEnabled(true);
            editButton.setEnabled(true);
        }else{
            Log.logger.info(selectedScript + " not available");
            deleteButton.setEnabled(false);
            detailsButton.setEnabled(false);
            exportButton.setEnabled(false);
            editButton.setEnabled(false);

        }
    }
    
    private void exportScript(String scriptName){
        //TODO
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(publicParent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            if (folder.exists()) {
                RScript s = RScript.availableScripts.get(scriptName);
                File file = new File(folder.getAbsolutePath()
                        + File.separator + s.name + "." + RScript.binExtension);
                try{
                    s.toBin(file);
                }catch (IOException ex){
                    Log.logger.log(Level.SEVERE, ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(publicParent,"Error: IOException. "
                        + "Maybe you don't have permission to export to this folder.",
                        "Cannot export " + s.name, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private boolean importScript(){
        JFileChooser fileChooser = new JFileChooser(){
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.isFile() && f.getName().contains("." + RScript.binExtension)) {
                    super.approveSelection();
                } else{
                    return;
                }
            }
        };
        
        if(fileChooser.showDialog(null, "Select a ." + RScript.binExtension + " file:") == JFileChooser.APPROVE_OPTION){
            File binFile = fileChooser.getSelectedFile();
            Object bin = peridot.Archiver.Persistence.loadObjectFromBin(binFile.getAbsolutePath());
            if(bin == null){
                Log.logger.log(Level.SEVERE, "Could not load RScript binary.");
                JOptionPane.showMessageDialog(publicParent, "Error loading module. "
                    + "Maybe you don't have permission to read this file.",
                    "Cannot import " + binFile.getName(), JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if(bin instanceof AnalysisScript || bin instanceof PostAnalysisScript){
                RScript script = null;
                Class type = null;
                if(bin instanceof AnalysisScript){
                    script = (AnalysisScript)bin;
                }else{
                    script = (PostAnalysisScript)bin;
                }
                script.createEnvironment(null);
                RScript.availableScripts.put(script.name, script);
                return true;
            }else{
                Log.logger.log(Level.SEVERE, "Could not load RScript binary. Unknown class.");
                JOptionPane.showMessageDialog(publicParent,"Error loading module"
                    + "The file is in an unknown format.",
                    "Cannot import " + binFile.getName(), JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    private void editScript(String scriptName){
        RScript script = RScript.availableScripts.get(scriptName);
        NewModuleDialog dialog = new NewModuleDialog(publicParent, true, script.getClass(), script);
        dialog.setVisible(true);
        if(dialog.script != null && dialog.editedScript){
            MainGUI.showRestartPeridotDialog();
            closeThisAndMainGUI();
        }
    }
    
    private void addScript(){
        Class scriptType = askUserForType();
        if(scriptType == null){
            return;
        }
        NewModuleDialog dialog = new NewModuleDialog(publicParent, true, scriptType, null);
        dialog.setVisible(true);
        if(dialog.script != null){
            MainGUI.showRestartPeridotDialog();
            closeThisAndMainGUI();
        }
    }
    
    private Class askUserForType(){
        AskModuleType dialog = new AskModuleType(publicParent);
        dialog.setVisible(true);
        return dialog.type;
    }
    
    private void scriptDetails(String script){
        new ScriptDetailsDialog(script, publicParent, true).setVisible(true);
    }
    
    private void deleteScript(String script){
        boolean deleted = RScript.deleteScript(script);
        if(deleted){
            MainGUI.showRestartPeridotDialog();
            closeThisAndMainGUI();
        }else{
            showCannotCompleteOperationError("delete(" + script + ")");
        }
    }
    
    private void showCannotCompleteOperationError(String operation){
        JOptionPane.showConfirmDialog(publicParent, "Cannot complete operation " + operation, "Error: " + operation, JOptionPane.ERROR_MESSAGE);
    }
    
    private void closeThisAndMainGUI(){
        SwingUtilities.invokeLater(()->{
            setVisible(false);
            MainGUI.close();
        });
    }
    
    private Panel modulesContainer;
    private Panel buttonsContainer;
    private Panel analysisModListContainer;
    private Panel postAnalysisModListContainer;
    
    private BigLabel analysisModLabel;
    private BigLabel postAnalysisModLabel;
    
    private BigButton addButton;
    private BigButton editButton;
    private BigButton detailsButton;
    private BigButton deleteButton;
    private BigButton importButton, exportButton;
    
    public static final String mandatoryString = " (mandatory)"; 
}
