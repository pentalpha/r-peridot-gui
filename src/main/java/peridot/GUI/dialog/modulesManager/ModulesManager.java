/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog.modulesManager;

import peridot.GUI.GUIUtils;
import peridot.GUI.MainGUI;
import peridot.GUI.component.BigButton;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Panel;
import peridot.Global;
import peridot.Log;
import peridot.GUI.Resources;
import peridot.script.AnalysisModule;
import peridot.script.PostAnalysisModule;
import peridot.script.RModule;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;
import java.util.logging.Level;

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
        try{
            frameIcon = Resources.getImage("logo64.png");
        }catch(Exception ex){
            ex.printStackTrace();
            Log.logger.info("Default ImageIcon not loaded");
        }
        return frameIcon;
    }
    
    private void makeModulesContainer(){
        modulesContainer = new Panel();
        modulesContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));
        
        
        JList list = new JList(RModule.getAvailableAnalysisModules());
        JList list2 = new JList(RModule.getAvailablePostAnalysisModules());
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
        
        /*addButton = getButton("Create Module", buttonSize, true,
                Resources.getImageIcon("add-green-button-icon-24.png"));
        addButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addScript();
        });*/
        
        importButton = getButton("Import", buttonSize, true,
                Resources.getImageIcon("import-icon-24.png"));
        importButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            boolean success = importScript();
            if(success){
                MainGUI.showRestartPeridotDialog();
                closeThisAndMainGUI();
            }
        });
        
        exportButton = getButton("Export", buttonSize, false,
                Resources.getImageIcon("export-icon-24.png"));
        exportButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            exportScript(selectedScript);
        });
        
        editButton = getButton("Edit", buttonSize, false,
                Resources.getImageIcon("write-icon-24.png"));
        editButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            editScript(selectedScript);
        });
        
        detailsButton = getButton("Details", buttonSize, false,
                Resources.getImageIcon("Document-icon-24.png"));
        detailsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            scriptDetails(selectedScript);
        });
        
        deleteButton = getButton("Delete", buttonSize, false, 
                Resources.getImageIcon("Delete-icon-24.png"));
        deleteButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            deleteScript(selectedScript);
        });
        
        //buttonsContainer.add(addButton);
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
        if(RModule.availableModules.keySet().contains(selectedScript)){
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
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(publicParent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();
            if (folder.exists()) {
                peridot.Operations.exportModule(scriptName, folder.getAbsolutePath());
            }
        }
    }

    private Class askUserForType(){
        AskModuleType dialog = new AskModuleType(publicParent);
        dialog.setVisible(true);
        return dialog.type;
    }

    private boolean importScript(){
        Class moduleType = askUserForType();
        String extension;
        if(moduleType == PostAnalysisModule.class){
            extension = ".PostAnalysisModule";
        }else if(moduleType == AnalysisModule.class){
            extension = ".AnalysisModule";
        }else{
            return false;
        }

        JFileChooser fileChooser = new JFileChooser(){
            public void approveSelection() {
                File f = getSelectedFile();
                if(f.isDirectory()
                    && (new File(f.getAbsolutePath() + File.separator + RModule.moduleFileName)).exists()
                    && f.getName().contains(extension))
                {
                    super.approveSelection();
                }
            }
        };

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if(fileChooser.showDialog(null, "Select a module to import:") == JFileChooser.APPROVE_OPTION){
            File dir = fileChooser.getSelectedFile();
            RModule module;
            try{
                if(extension.equals(".PostAnalysisModule")){
                    module = new PostAnalysisModule(dir);
                }else{
                    module = new AnalysisModule(dir);
                }
                module.createEnvironment(dir + File.separator + module.scriptName);
                RModule.availableModules.put(module.name, module);
                return true;
            }catch (Exception ex){
                Log.logger.log(Level.SEVERE, "Could not load RModule");
                GUIUtils.showErrorMessageInDialog("Could not load RModule", ex.toString(), this.publicParent);
                return false;
            }
        }
        return false;
    }
    
    private void editScript(String scriptName){
        RModule script = RModule.availableModules.get(scriptName);
        Global.openFileWithSysApp(script.getScriptFile());
        Global.openFileWithSysApp(script.getDescriptionFile());
        JOptionPane.showMessageDialog(null,
                "Restart R-Peridot in order to load any changes made to the module.");
        /*NewModuleDialog dialog = new NewModuleDialog(publicParent, true, script.getClass(), script);
        dialog.setVisible(true);
        if(dialog.script != null && dialog.editedScript){
            MainGUI.showRestartPeridotDialog();
            closeThisAndMainGUI();
        }*/
    }

    private void scriptDetails(String script){
        new ModuleDetailsDialog(script, publicParent, true).setVisible(true);
    }
    
    private void deleteScript(String script){
        boolean deleted = RModule.deleteScript(script);
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
    
    //private BigButton addButton;
    private BigButton editButton;
    private BigButton detailsButton;
    private BigButton deleteButton;
    private BigButton importButton, exportButton;
    
    public static final String mandatoryString = " (mandatory)";

    /*

    */
}
