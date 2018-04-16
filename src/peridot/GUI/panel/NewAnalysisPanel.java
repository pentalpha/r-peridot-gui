/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.AnalysisData;
import peridot.AnalysisParameters;
import peridot.Archiver.Manager;
import peridot.Archiver.Places;
import peridot.GUI.MainGUI;
import peridot.GUI.component.*;
import peridot.GUI.component.Button;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.dialog.NewExpressionDialog;
import peridot.GUI.dialog.SpecificParametersDialog;
import peridot.GUI.dialog.modulesManager.ModuleDetailsDialog;
import peridot.Global;
import peridot.Log;
import peridot.script.AnalysisModule;
import peridot.script.RModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author pentalpha
 */
public class NewAnalysisPanel extends Panel {
    
    private AnalysisData expression;
    private java.awt.Frame parentFrame;
    public TreeSet<String> selectedScripts;
    public TreeSet<String> availableScripts;
    public TreeSet<String> availablePackages;
    //public AnalysisParameters parameters;
    public TreeMap<String, AnalysisParameters> specificParameters;
    private Map<String, JCheckBox> scriptCheckboxes;
    private ParametersPanel parametersPanel;
    private NewExpressionDialog expressionGUI;

    /**
     * Creates new form NewAnalysisPanel
     */
    public NewAnalysisPanel(java.awt.Frame parentFrame) {
        super();
        //Log.logger.info("starting to build analysisPanel");
        createInterface();
        
        //Log.logger.info("no error");
        
    }

    public void createInterface(){
        this.removeAll();

        modulesAlwaysVisible = false;
        this.parentFrame = parentFrame;
        this.availableScripts = new TreeSet<String>();
        this.availablePackages = new TreeSet<String>();
        availableScripts.addAll(RModule.getAvailablePostAnalysisModules());
        availableScripts.addAll(RModule.getAvailableAnalysisModules());
        this.selectedScripts = new TreeSet<String>();
        this.specificParameters = new TreeMap<>();
        for(String pack : availableScripts){
            RModule aScript = RModule.availableModules.get(pack);
            //if(aScript == null){Log.logger.info(pack + " is null");}
            if(aScript instanceof AnalysisModule){
                AnalysisParameters params = new AnalysisParameters(
                        aScript.requiredParameters, aScript.parameters);
                specificParameters.put(pack, params);
                //Log.logger.info("package " + pack);
                this.availablePackages.add(pack);
            }
        }

        this.customInit();
    }
    
    public int getNPackagesSelected(){
        int n = 0;
        for(String scriptName : selectedScripts){
            RModule script = RModule.availableModules.get(scriptName);
            if(script instanceof AnalysisModule){
                n++;
            }
        }
        return n;
    }
    
    private void updateRNASeqDescription(){
        int nConditions = expression.getNumberOfConditions();
        String nLines;
        try{
            nLines = Integer.toString(Manager.countLines(Places.countReadsInputFile.getAbsolutePath())-1);
        }catch(IOException ex){
            nLines = "[could not read]";
        }
        this.expressionDescriptionLabel.setText(expression.getNumberOfSamples() + " samples, "
                + nConditions + " conditions and "
                + nLines + " lines.");
        if(expression.hasMoreThanTwoConditions()){
            this.multiConditionsLabel.setText("More than 2 conditions, some modules may be disabled.");
        }else{
            this.multiConditionsLabel.setText("");
        }

        if(expression.hasReplicatesInSamples() == false){
            this.needsReplicatesLabel.setText("No replicates, some modules may be disabled.");
        }else{
            this.needsReplicatesLabel.setText("");
        }
    }
    
    private boolean moreThan2Conditions(){
        if(expression == null){
            return false;
        }
        Vector<String> conditionsOriginal = new Vector<>(expression.conditions.values());
        TreeSet<String> conditions = new TreeSet<>();
        for(String string : conditionsOriginal){
            boolean present = false;
            for(String string2 : conditions){
                if(string.equals(string2)){
                    present = true;
                    break;
                }
            }
            if(present == false){
                conditions.add(string);
            }
        } 
        boolean notUsePresent = false;
        for(String string2 : conditions){
            if(string2.equals("not-use")){
                notUsePresent = true;
                break;
            }
        }
        int nConditions = conditions.size();
        if(notUsePresent){
            nConditions--;
        }
        return (nConditions > 2);
    }
    
    private void customInit(){
        setPreferredSize(MainGUI.defaultSize);
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 1));
        
        makeDefineExpressionContainer();
        separator1 = new JSeparator();
        separator1.setPreferredSize(new java.awt.Dimension(490, 2));
        makeModulesContainer();
        separator2 = new JSeparator();
        separator2.setPreferredSize(new java.awt.Dimension(490, 2));
        makeParametersContainer();
        separator3 = new JSeparator();
        separator3.setPreferredSize(new java.awt.Dimension(490, 2));
        makeCreateContainer();
        
        add(defineExpressionContainer);
        //add(defineGeneListContainer);
        add(separator1);
        separator1.setVisible(false);
        modulesContainer.setVisible(false);
        add(modulesContainer);
        separator2.setVisible(false);
        add(separator2);
        parametersContainer.setVisible(false);
        add(parametersContainer);
        separator3.setVisible(false);
        add(separator3);
        createContainer.setVisible(false);
        add(createContainer);
        
        tryToHideModules();
        tryToHideParamsAndCreate();

        JCheckBox box = scriptCheckboxes.get("VennDiagram");
        if(box != null){
            box.doClick();
        }
    }
    
    private void makeCreateContainer(){
        createContainer = new Panel();
        createContainer.setPreferredSize(new java.awt.Dimension(530, 75));
        createContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        createButton = new BigButton();
        createButton.setText("Start");
        createButton.setPreferredSize(new java.awt.Dimension(112, 70));
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        createButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/play-icon-32.png")));
        
        createContainer.add(createButton);
    }
    
    private void makeParametersContainer(){
        parametersContainer = new Panel();
        parametersContainer.setPreferredSize(new java.awt.Dimension(530, 138));
        parametersContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        parametersLabel = new BiggerLabel();
        parametersLabel.setText("Parameters");
        parametersContainer.add(parametersLabel);
        
        Map<String, Class> allParamsMap = new HashMap<>();
        for(String scriptName : availableScripts){
            Map<String, Class> scriptParams = null;
            RModule script = RModule.availableModules.get(scriptName);
            if(script != null){
                scriptParams = script.requiredParameters;
                allParamsMap.putAll(scriptParams);
            }
        }
        AnalysisParameters allParams = new AnalysisParameters(allParamsMap);
        parametersPanel = new ParametersPanel(false);
        parametersPanel.setPreferredSize(new java.awt.Dimension(parametersContainer.getPreferredSize().width-20, 30));
        parametersPanel.setMaximumSize(new java.awt.Dimension(parametersContainer.getPreferredSize().width-20, 3000));
        parametersPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        JScrollPane scroller = new JScrollPane(parametersPanel);
        scroller.setPreferredSize(new java.awt.Dimension(parametersContainer.getPreferredSize().width-10, parametersContainer.getPreferredSize().height-30));
        scroller.setBorder(BorderFactory.createEmptyBorder());
        this.parametersContainer.add(scroller);
        parametersPanel.setParams(allParams);
    }
    
    private void makeModulesContainer(){
        int sideH = 255;
        modulesContainer = new Panel();
        modulesContainer.setPreferredSize(new java.awt.Dimension(545, sideH+25));
        modulesContainer.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 1));
        modulesLabel = new BiggerLabel();
        modulesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        modulesLabel.setText("Modules");
        modulesLabel.setPreferredSize(new java.awt.Dimension(480, 20));
        
        
        modulesLeftSide = new Panel();
        modulesLeftSide.setPreferredSize(new java.awt.Dimension(245, sideH));
        modulesLeftSide.setRequestFocusEnabled(false);
        packagesLabel1 = new BigLabel();
        packagesLabel1.setText("Analysis:");
                
        packagesPanel = new Panel();
        packagesPanel.setMaximumSize(new java.awt.Dimension(modulesLeftSide.getPreferredSize().width-10, 3000));
        packagesPanel.setLayout(new BoxLayout(packagesPanel, BoxLayout.Y_AXIS));
        //packagesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JScrollPane packagesScroller = new JScrollPane(packagesPanel);
        packagesScroller.setPreferredSize(new java.awt.Dimension(modulesLeftSide.getPreferredSize().width,
                                                                 modulesLeftSide.getPreferredSize().height-30));
        packagesScroller.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //packagesScroller.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);

        modulesLeftSide.add(packagesLabel1);
        modulesLeftSide.add(packagesScroller);

        modulesSeparator = new JSeparator();
        modulesSeparator.setOrientation(SwingConstants.VERTICAL);
        modulesSeparator.setPreferredSize(new java.awt.Dimension(2, 120));
        
        modulesRightSide = new Panel();
        modulesRightSide.setPreferredSize(new java.awt.Dimension(240, sideH));
        modulesRightSide.setRequestFocusEnabled(false);
        othersPackages = new BigLabel();
        othersPackages.setText("Post Analysis:");
        
        scriptsPanel = new Panel();
        scriptsPanel.setMaximumSize(new java.awt.Dimension(modulesRightSide.getPreferredSize().width-10, 3000));;
        scriptsPanel.setLayout(new BoxLayout(scriptsPanel, BoxLayout.Y_AXIS));
        //scriptsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        //scriptsPanel.setLayout(new java.awt.FlowLayout(FlowLayout.LEFT, 10, 1));
        JScrollPane scriptsScroller = new JScrollPane(scriptsPanel);
        scriptsScroller.setPreferredSize(new java.awt.Dimension(modulesRightSide.getPreferredSize().width,
                                                                 modulesRightSide.getPreferredSize().height-30));
        scriptsScroller.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        //scriptsScroller.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);

        modulesRightSide.add(othersPackages);
        modulesRightSide.add(scriptsScroller);
        
        modulesContainer.add(modulesLabel);
        modulesContainer.add(modulesLeftSide);
        modulesContainer.add(modulesSeparator);
        modulesContainer.add(modulesRightSide);
      
        initScriptsCheckboxes();
    }
    
    private void makeDefineExpressionContainer(){
        defineExpressionContainer = new Panel();
        defineExpressionContainer.setPreferredSize(new java.awt.Dimension(530, 90));
        
        leftSide = new Panel();
        leftSide.setPreferredSize(new java.awt.Dimension(397, 90));
        leftSide.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        geneExprLabel1 = new BiggerLabel();
        geneExprLabel1.setText("Expression Data (count reads):");
        expressionDescriptionLabel = new Label();
        multiConditionsLabel = new Label();
        needsReplicatesLabel = new Label();
        expressionDescriptionLabel.setText("...");
        multiConditionsLabel.setText("");
        needsReplicatesLabel.setText("");
        expressionDescriptionLabel.setPreferredSize(new java.awt.Dimension(370, 15));
        multiConditionsLabel.setPreferredSize(new java.awt.Dimension(370, 15));
        needsReplicatesLabel.setPreferredSize(new java.awt.Dimension(370, 15));
        leftSide.add(geneExprLabel1);
        leftSide.add(expressionDescriptionLabel);
        leftSide.add(multiConditionsLabel);
        leftSide.add(needsReplicatesLabel);
        
        rightSide = new Panel();
        rightSide.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        defineButton = new BigButton();
        defineButton.setText("Open File");
        defineButton.setPreferredSize(new java.awt.Dimension(112, 70));
        defineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defineButtonActionPerformed(evt);
            }
        });
        defineButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/write-document-32.png")));
        rightSide.add(defineButton);
        
        defineExpressionContainer.add(leftSide);
        defineExpressionContainer.add(rightSide);
    }
    
    public void updateUnabledScripts(){
        for(String name : RModule.getAvailableAnalysisModules()){
            //Log.logger.info("Trying to unable " + name);
            updateAnalysisModuleUnabledNoRecursion(name);
        }
        for(String name : RModule.getAvailablePostAnalysisModules()){
            //Log.logger.info("Trying to unable " + name);
            updateModuleUnabled(name);
            //if(this.scriptCheckboxes.get(name).isEnabled() == false){
                //Log.logger.info("But could not.");
            //}
        }
        /*boolean multiConditions = this.moreThan2Conditions();
        if(multiConditions){
            for(Map.Entry<String,RModule> pair : RModule.availableScripts.entrySet()){
                if(pair.getValue().max2Conditions){
                    String name = pair.getKey();
                    JCheckBox checkbox = this.scriptCheckboxes.get(name);
                    if(checkbox != null){
                        if(checkbox.isSelected()){
                            checkbox.doClick();
                        }
                        checkbox.setEnabled(false);
                    }
                }
            }
        }else{
            for(Map.Entry<String, JCheckBox> pair : scriptCheckboxes.entrySet()){
                pair.getValue().setEnabled(true);
            }
        }
        
        for(Map.Entry<String,RModule> pair : RModule.availableScripts.entrySet()){
            String name = pair.getKey();
            JCheckBox checkbox = this.scriptCheckboxes.get(name);
            if(checkbox != null){
                updateModuleUnabled(name);
            }
        }*/
    }
    
    private void updateAnalysisModuleUnabledNoRecursion(String module){
        boolean unabled = true;
        RModule script = RModule.availableModules.get(module);
        JCheckBox checkbox = this.scriptCheckboxes.get(module);

        if(script instanceof AnalysisModule){
            if(this.expression != null){
                if((script.max2Conditions && this.expression.hasMoreThanTwoConditions())
                        ||(script.needsReplicates && this.expression.hasReplicatesInSamples() == false)){
                    unabled = false;
                }
            }
            /*if(expression != null){
                if(expression.info.dataType == Spreadsheet.DataType.Float){
                    if(script.canHandleFloatValues == false){
                        Log.logger.info(script.name + " cannot handle rational values, only integers.");
                        unabled = false;
                    }
                }
            }*/
        }

        if(!script.requiredPackagesInstalled()){
            unabled = false;
        }
        
        if(unabled == false){
            //Log.logger.info(module + " wont be unabled this time.");
        }
        if(checkbox != null){
        if(unabled != checkbox.isEnabled()){
            JButton paramsButton = editModuleParamsButtons.get(module);
            if(paramsButton != null){
                //if(unabled)
                //paramsButton.setEnabled(unabled);
                //this.updateUnabledScripts();
            }
            if(unabled){
                checkbox.setEnabled(true);
            }else{    
                if(checkbox.isSelected()){
                    checkbox.doClick();
                }
                checkbox.setEnabled(false);
                //paramsButton.setEnabled(false);
            }
        }
        }
    }
    
    private void updateModuleUnabled(String module){
        boolean unabled = true;
        RModule script = RModule.availableModules.get(module);
        JCheckBox checkbox = this.scriptCheckboxes.get(module);
        
        if(script instanceof AnalysisModule){
            if(this.expression != null){
                if((script.max2Conditions && this.expression.hasMoreThanTwoConditions())
                        ||(script.needsReplicates && this.expression.hasReplicatesInSamples() == false)){
                    unabled = false;
                }
            }
            /*if(expression != null){
                if(expression.info.dataType == Spreadsheet.DataType.Float){
                    if(script.canHandleFloatValues == false){
                        Log.logger.info(script.name + " cannot handle rational values.");
                        unabled = false;
                    }
                }
            }*/
        }else{
            if(this.nAnalysisModules < 1){
                unabled = false;
            }
            for(String name : RModule.availableModules.get(module).requiredScripts){
                if(selectedScripts.contains(name) == false){
                    unabled = false;
                    break;
                }
            }
        }

        if(!script.requiredPackagesInstalled()){
            unabled = false;
        }
        if(checkbox != null){
            if(unabled != checkbox.isEnabled()){
                JButton paramsButton = editModuleParamsButtons.get(module);
                if(paramsButton != null){
                    paramsButton.setEnabled(unabled);
                    this.updateUnabledScripts();
                }
                if(unabled){
                    checkbox.setEnabled(true);
                }else{
                    if(checkbox.isSelected()){
                        checkbox.doClick();
                    }
                    checkbox.setEnabled(false);
                }
                updateDependantModulesUnabled(module);
            }
        }else{
            unabled = false;
        }

    }
    
    private void updateDependantModulesUnabled(String module){
        //Log.logger.info("Updating dependencies of " + module);
        if(RModule.availableModules.get(module) instanceof AnalysisModule){
            //Log.logger.info("Which is a package.");
            for(String script : RModule.getAvailablePostAnalysisModules()){
                this.updateModuleUnabled(script);
            }
        }else{
            
            for(Map.Entry<String,RModule> pair : RModule.availableModules.entrySet()){
                boolean depends = false;
                for(String required : pair.getValue().requiredScripts){
                    if(required.equals(module)){
                        depends = true;
                        break;
                    }
                }
                if(depends){
                    //Log.logger.info(pair.getKey() + " depends of " + module);
                    updateModuleUnabled(pair.getKey());
                }
            }
        }
    }
    
    private void defineButtonActionPerformed(java.awt.event.ActionEvent evt) {   
        if(expressionGUI == null){
            expressionGUI = new NewExpressionDialog(parentFrame, true, expression);
        }
        
        expressionGUI.setVisible(true);
        if(expressionGUI.success == false){
            return;
        }
        expression = expressionGUI.getResults();
        if(expression != null){
            //FileUtil.createTempFolder();
            //expression.writeRNASeqWithConditions();
            try{
                expression.writeExpression();
            }catch (java.lang.ArrayIndexOutOfBoundsException ex){
                ex.printStackTrace();
                Log.logger.severe("Could not write count reads file for analysis!");
            }
            deleteTempFiles();
            //updateModuleUnabled("DESeq");
            updateUnabledScripts();
            RModule.removeScriptResults();
            MainGUI.updateResultsPanel();
            
            updateRNASeqDescription();
        }
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        AnalysisParameters parameters = parametersPanel.getParameters();
        //Log.logger.info(parameters);
        //Log.logger.info(parameters.toString());
        if(selectedScripts.size() == 0){
            JOptionPane.showMessageDialog(parentFrame, "Select at least 1 package from the list.");
            return;
        }
        for(String scriptName : selectedScripts){
            RModule script = RModule.availableModules.get(scriptName);
            if(script.requiredExternalFiles.contains(Places.countReadsInputFileName) && expression == null){
                JOptionPane.showMessageDialog(parentFrame, "To use a RNASeq module you need to define a count reads file.");
                return;
            }
        }
        
        try{
            ProcessingPanel.start(this.selectedScripts, 
                                parameters, this.specificParameters,
                                expression);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void deleteTempFiles(){
        for(Map.Entry<String, RModule> pair : RModule.availableModules.entrySet()){
            pair.getValue().cleanTempFiles();
            pair.getValue().cleanLocalResults();
        }
    }
    
    private void initScriptsCheckboxes(){
        this.scriptCheckboxes = new TreeMap<String, JCheckBox>();
        this.editModuleParamsButtons = new TreeMap<String, JButton>();
        this.moduleDetailButtons = new TreeMap<String, JButton>();
        for(String pack : this.availableScripts.descendingSet()){
            boolean isAnalysisModule =
                    (RModule.availableModules.get(pack) instanceof AnalysisModule);

            JPanel panel = new Panel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            //panel.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black, 1));
            JCheckBox checkBox = new CheckBox();
            checkBox.setText(pack);
            checkBox.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent evt) {
                    selectScript(checkBox.getText(), checkBox.isSelected(), isAnalysisModule);
                }
            });
            //checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            //checkBox.setHorizontalAlignment(JLabel.LEFT);

            JButton detailButton = new Button();
            detailButton.setIcon(
                    new ImageIcon(getClass()
                            .getResource("/peridot/GUI/icons/open-icon-16.png")
                    )
            );
            detailButton.setText("Detail");
            detailButton.setEnabled(true);
            detailButton.addActionListener((java.awt.event.ActionEvent evt) -> {
                ModuleDetailsDialog dialog = new ModuleDetailsDialog(pack,
                        MainGUI.getInstance(),false);
                dialog.setVisible(true);
            });
            //detailButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            //detailButton.setHorizontalAlignment(JLabel.LEFT);
            moduleDetailButtons.put(pack, detailButton);

            if(isAnalysisModule){
                JButton paramsButton = new Button();
                paramsButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/Write-Document-icon16.png"))); // NOI18N
                paramsButton.setText("");
                paramsButton.setEnabled(false);
                paramsButton.addActionListener((java.awt.event.ActionEvent evt) -> {
                    SpecificParametersDialog dialog = new SpecificParametersDialog(parentFrame, true,
                            specificParameters.get(pack), "Specific parameters for " + pack);
                    dialog.setVisible(true);
                    specificParameters.put(pack, dialog.parametersPanel.getParameters());
                });
                //paramsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                //paramsButton.setHorizontalAlignment(JLabel.LEFT);
                editModuleParamsButtons.put(pack, paramsButton);

                panel.add(checkBox);
                panel.add(paramsButton);
                panel.add(detailButton);
                //panel.setAlignmentX(Component.LEFT_ALIGNMENT);
                packagesPanel.add(panel, BorderLayout.WEST);
            }else{
                panel.add(checkBox);
                panel.add(detailButton);
                //panel.setAlignmentX(Component.LEFT_ALIGNMENT);
                scriptsPanel.add(panel, BorderLayout.WEST);
            }
            scriptCheckboxes.put(pack, checkBox);
            if(isAnalysisModule){
                updateModuleUnabled(pack);
            }
        }
        this.updateUnabledScripts();
    }

    private Component leftJustify( JPanel panel )  {
        Box  b = Box.createHorizontalBox();
        b.add( panel );
        b.add( Box.createHorizontalGlue() );
        // (Note that you could throw a lot more components
        // and struts and glue in here.)
        return b;
    }
    
    public void selectScript(String name, boolean add, boolean analysisScript){
        //VennDiagram specific behaviour:
        if(name.equals("VennDiagram")){
            if(add){
                if(!selectedScripts.contains("VennDiagram")){
                    selectedScripts.add(name);
                    nPostAnalysisModules++;
                    updateDependantModulesUnabled("VennDiagram");
                }
            }else{
                scriptCheckboxes.get("VennDiagram").doClick();
            }
            return;
        }
        //General behaviour:
        if(selectedScripts.contains(name) != add){
            if(add){
                selectedScripts.add(name);
                if(analysisScript){
                    nAnalysisModules++;
                }else{
                    nPostAnalysisModules++;
                }
            }else{
                selectedScripts.remove(name);
                if(analysisScript){
                    nAnalysisModules--;
                }else{
                    nPostAnalysisModules--;
                }
            }

            if(analysisScript){
                editModuleParamsButtons.get(name).setEnabled(add);
                Log.logger.info("nAnalysisModules: " + nAnalysisModules);
            }

            updateDependantModulesUnabled(name);
        }
        
    }
    
    public void tryToHideModules(){
        try{
            if((modulesContainer.isVisible() == false && modulesAlwaysVisible)
            || (expression != null && modulesContainer.isVisible() == false)){
                SwingUtilities.invokeLater(() -> {
                    separator1.setVisible(true);
                });
                Thread.sleep(sleepBetweenComponents);
                SwingUtilities.invokeLater(() -> {
                    modulesContainer.setVisible(true);
                });
            }else if(expression == null && modulesContainer.isVisible() && !modulesAlwaysVisible){
                SwingUtilities.invokeLater(() -> {
                    separator1.setVisible(false);
                });
                Thread.sleep(sleepBetweenComponents);
                SwingUtilities.invokeLater(() -> {
                    modulesContainer.setVisible(false);
                });
            }/*else if (expression != null && modulesContainer.isVisible() == false){
                SwingUtilities.invokeLater(() -> {
                    separator1.setVisible(true);
                });
                Thread.sleep(sleepBetweenComponents);
                SwingUtilities.invokeLater(() -> {
                    modulesContainer.setVisible(true);
                });
            }*/
        }catch(Exception ex){
            ex.printStackTrace();
        }
        Global.setTimeout(() -> tryToHideModules(), sleepBetweenChecks);
    }
    
    public void tryToHideParamsAndCreate(){
        try{
            if(nAnalysisModules >= 1){
                if(this.parametersContainer.isVisible() == false){
                    SwingUtilities.invokeLater(() -> {
                        separator2.setVisible(true);
                    });
                    Thread.sleep(sleepBetweenComponents);
                    SwingUtilities.invokeLater(() -> {
                        parametersContainer.setVisible(true);
                    });
                }
                Thread.sleep(sleepBetweenComponents);
                if(this.createContainer.isVisible() == false){
                    SwingUtilities.invokeLater(() -> {
                        separator3.setVisible(true);
                    });
                    Thread.sleep(sleepBetweenComponents);
                    SwingUtilities.invokeLater(() -> {
                        createContainer.setVisible(true);
                    });
                }
            }else{
                if(this.parametersContainer.isVisible() == true){
                    SwingUtilities.invokeLater(() -> {
                        separator2.setVisible(false);
                    });
                    Thread.sleep(sleepBetweenComponents);
                    SwingUtilities.invokeLater(() -> {
                        parametersContainer.setVisible(false);
                    });
                }
                Thread.sleep(sleepBetweenComponents);
                if(this.createContainer.isVisible() == true){
                    SwingUtilities.invokeLater(() -> {
                       separator3.setVisible(false);
                    });
                    Thread.sleep(sleepBetweenComponents);
                    SwingUtilities.invokeLater(() -> {
                        createContainer.setVisible(false);
                    });
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        Global.setTimeout(() -> tryToHideParamsAndCreate(), sleepBetweenChecks);
    }
    
    public int sleepBetweenComponents = 120;
    public int sleepBetweenChecks = 100;
    
    public int nPostAnalysisModules = 0;
    public int nAnalysisModules = 0;
    
    public JButton createButton;
    private JPanel createContainer;
    
    private JButton defineButton;
    private JPanel defineExpressionContainer;
    private JLabel expressionDescriptionLabel;
    private JLabel multiConditionsLabel, needsReplicatesLabel;
    private JLabel geneExprLabel1;
    
    private JButton defineGeneListButton;
    private JPanel defineGeneListContainer;
    private JLabel geneListDescriptionLabel;
    private JLabel geneListLabel;

    private boolean modulesAlwaysVisible;
    private JPanel leftSide;
    private JPanel modulesContainer;
    private JLabel modulesLabel;
    private JPanel modulesLeftSide;
    private JPanel modulesRightSide;
    
    private JSeparator modulesSeparator;
    
    private JLabel othersPackages;
    private JLabel packagesLabel1;
    private JPanel packagesPanel;
    private JPanel parametersContainer;
    private JLabel parametersLabel;
    private JPanel rightSide;
    
    private JPanel scriptsPanel;
    
    private JSeparator separator1;
    private JSeparator separator2;
    private JSeparator separator3;
    
    private TreeMap<String, JButton> editModuleParamsButtons;
    private TreeMap<String, JButton> moduleDetailButtons;
}
