/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package peridot.GUI.dialog.modulesManager;

import peridot.GUI.GUIUtils;
import peridot.GUI.component.*;
import peridot.GUI.component.Button;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.Global;
import peridot.script.RModule;
import peridot.script.r.Package;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author pentalpha
 */
public class ModuleDetailsDialog extends Dialog {
    RModule script;
    String info;
    /** Creates new form ModuleDetailsDialog */
    public ModuleDetailsDialog(String scriptName, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.script = RModule.availableModules.get(scriptName);
        this.info = script.info;
        
        initComponents();
    }

    private void initComponents(){
        this.setTitle(script.name + " Details");
        /*try{
            this.setIconImage(ImageIO.read(getClass().getClassLoader().getResource("peridot/GUI/icons/logo64.png")));
        }catch(IOException ex){
            Log.logger.log(Level.SEVERE, ex.getMessage(), ex);
        }*/
        dialogSize = new Dimension(560, 700);
        int wGap = 5;
        int hGap = 5;
        availableSize = new Dimension(dialogSize.width-20, dialogSize.height-20);
        componentPanelHeight = (availableSize.height-(hGap*2)-20)/3;
        componentPanelWidth = (availableSize.width-wGap)/2;
        scrollerSize = new Dimension(componentPanelWidth, componentPanelHeight-35);
        defaultPanelSize = new Dimension(componentPanelWidth, componentPanelHeight);
        this.setMinimumSize(dialogSize);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, wGap, hGap));
        setResizable(false);
        
        initGeneralInfo();
        initResults();
        initInputs();
        initParams();
        initPackages();
        initInfoArea();
        
        add(generalInfoPanel);
        add(resultsPanel);
        add(inputPanel);
        add(paramsPanel);
        add(packagesPanel);
        add(infoScroller);

        Dimension loc = GUIUtils.getCenterLocation(dialogSize.width, dialogSize.height);
        setLocation(loc.width, loc.height);
    }
    
    private void initGeneralInfo(){
        generalInfoPanel = new Panel();
        generalInfoPanel.setLayout(new BoxLayout(generalInfoPanel, BoxLayout.PAGE_AXIS));
        generalInfoPanel.setPreferredSize(
                new Dimension(componentPanelWidth, componentPanelHeight));
        generalInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scriptNameLabel = new Label("Name: " + script.name);
        scriptNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fileNameLabel = new Label("Filename: " + script.getScriptFile().getName());
        fileNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        openFileButton = new Button();
        openFileButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            Global.openFileWithSysApp(script.getScriptFile());
        });
        openFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openFileButton.setText("  Open " + script.getScriptFile().getName() + "  ");
        openFileButton.setMinimumSize(new Dimension(150, 45));
        openFileButton.setFocusable(false);
        max2CondLabel = new Label("Maximum of 2 conditions: " + script.max2Conditions);
        max2CondLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        needsReplicatesLabel = new Label("Needs replicates: " + script.needsReplicates);
        needsReplicatesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        //generalInfoPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        generalInfoPanel.add(Box.createVerticalGlue());
        generalInfoPanel.add(scriptNameLabel);
        generalInfoPanel.add(fileNameLabel);
        generalInfoPanel.add(openFileButton);
        generalInfoPanel.add(max2CondLabel);
        generalInfoPanel.add(needsReplicatesLabel);
        generalInfoPanel.add(Box.createVerticalGlue());
    }
    
    private String[] getResultsArray(){
        String[] array = new String[script.results.size()];
        int counter = 0;
        for(String s : script.results){
            if(script.mandatoryResults.contains(s)){
                array[counter] = s + ModulesManager.mandatoryString;
            }else{
                array[counter] = s;
            }
            counter++;
        }
        return array;
    }
    
    private void initResults(){
        resultsList = new JList(getResultsArray());
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setLayoutOrientation(JList.VERTICAL);
        resultsList.setVisibleRowCount(-1);
        resultsLabel = new BigLabel("Results: ");
        
        resultsPanel = new Panel();
        resultsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        resultsPanel.setPreferredSize(new Dimension(componentPanelWidth, componentPanelHeight));
        
        resultsScroller = new JScrollPane(resultsList);
        resultsScroller.setPreferredSize(new Dimension(scrollerSize.width, scrollerSize.height));
        
        resultsPanel.add(resultsLabel);
        resultsPanel.add(resultsScroller);
    }
    
    private void initInputs(){
        inputList = new JList(script.getInputFiles());
        inputList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inputList.setLayoutOrientation(JList.VERTICAL);
        inputList.setVisibleRowCount(-1);
        inputLabel = new BigLabel("Input Files: ");
        
        inputPanel = new Panel();
        inputPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        inputPanel.setPreferredSize(defaultPanelSize);
        
        inputScroller = new JScrollPane(inputList);
        inputScroller.setPreferredSize(scrollerSize);
        
        inputPanel.add(inputLabel);
        inputPanel.add(inputScroller);
    }

    private void initPackages(){
        String[] packStrs = new String[script.requiredPackages.size()];
        int i = 0;
        for(Package pack : script.requiredPackages){
            packStrs[i] = pack.name + " " + pack.version.toString();
        }

        packagesList = new JList(packStrs);
        packagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packagesList.setLayoutOrientation(JList.VERTICAL);
        packagesList.setVisibleRowCount(-1);
        packagesLabel = new BigLabel("Required Packages:");

        packagesPanel = new Panel();
        packagesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        packagesPanel.setPreferredSize(defaultPanelSize);

        packagesScroller = new JScrollPane(packagesList);
        packagesScroller.setPreferredSize(scrollerSize);

        packagesPanel.add(packagesLabel);
        packagesPanel.add(packagesScroller);
    }
    
    private void initParams(){
        paramsList = new JList(script.getParamsDescription());
        paramsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paramsList.setLayoutOrientation(JList.VERTICAL);
        paramsList.setVisibleRowCount(-1);
        paramsLabel = new BigLabel("Parameters: ");
        
        paramsPanel = new Panel();
        paramsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        paramsPanel.setPreferredSize(defaultPanelSize);
        
        paramsScroller = new JScrollPane(paramsList);
        paramsScroller.setPreferredSize(scrollerSize);
        
        paramsPanel.add(paramsLabel);
        paramsPanel.add(paramsScroller);
    }
    
    private void initInfoArea(){
        infoArea = new JTextArea();
        infoArea.setText(this.info);
        infoArea.setEditable(false);
        infoArea.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoScroller = new JScrollPane(infoArea);
        infoScroller.setPreferredSize(new Dimension(componentPanelWidth, componentPanelHeight-10));

        //infoScroller.setMaximumSize(new Dimension(componentPanelWidth, 1000));
    }
    
    Panel generalInfoPanel;
    Label scriptNameLabel;
    Label fileNameLabel;
    Button openFileButton;
    Label max2CondLabel, needsReplicatesLabel;
    
    Panel resultsPanel;
    Label resultsLabel;
    JScrollPane resultsScroller;
    JList resultsList;
    
    Panel inputPanel;
    Label inputLabel;
    JScrollPane inputScroller;
    JList inputList;
    
    Panel paramsPanel;
    Label paramsLabel;
    JScrollPane paramsScroller;
    JList paramsList;

    Panel packagesPanel;
    Label packagesLabel;
    JScrollPane packagesScroller;
    JList packagesList;
    
    JTextArea infoArea;
    JScrollPane infoScroller;
    
    Dimension dialogSize, availableSize, defaultPanelSize, scrollerSize;
    int componentPanelHeight, componentPanelWidth;
}
