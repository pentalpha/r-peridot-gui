package peridot.GUI.dialog.modulesManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.apache.commons.io.FileUtils;
import peridot.Archiver.Spreadsheet;
import peridot.GUI.MainGUI;
import peridot.GUI.component.*;
import peridot.Global;
import peridot.Log;
import peridot.script.AnalysisScript;
import peridot.script.PostAnalysisScript;
import peridot.script.RNASeqPackage;
import peridot.script.RScript;

/**
 *
 * @author pentalpha
 */
public class NewModuleDialog extends Dialog {
    RScript script = null;
    String info;
    File scriptFile;
    File scriptInTemp;
    String name; 
    //String scriptFile;
    boolean max2Conditions;
    Map<String, Class> requiredParameters;
    Set<String> requiredExternalFiles;
    Set<String> results;
    Set<String> mandatory;
    Set<String> requiredScripts;
    boolean canHandleFloatValues;
    Class scriptType;
    String originalScript;
    boolean editing, changedScript, editedScript;
    
    /** Creates new form ScriptDetailsDialog */
    public NewModuleDialog(java.awt.Frame parent, boolean modal, Class type, RScript baseScript) {
        super(parent, modal);
        assert(type != null) : "No Script Type specified for the creation";
        this.scriptType = type;
        this.script = baseScript;
        
        initComponents();
        
        if(baseScript != null){
            editing = true;
            File temp = new File(System.getProperty("java.io.tmpdir"));
            File scriptInTemp = new File(temp.getAbsolutePath() + "/" + script.getScriptFile().getName());
            originalScript = script.getScriptFile().getAbsolutePath();
            if(scriptInTemp.exists()){
                scriptInTemp.delete();
            }
            try{
                FileUtils.copyFile(script.getScriptFile(), scriptInTemp);
                this.scriptInTemp = scriptInTemp;
            }catch(IOException ex){
                Log.logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }else{
            editing = false;
            originalScript = "";
        }
        changedScript = false;
        editedScript = false;
    }
    
    
    private void listModelFromResults(Set<String> res, Set<String> mandatory){
        for(String s : res){
            String text = s;
            if(mandatory.contains(s)){
                text += ModulesManager.mandatoryString;
            }
            resultsListModel.addElement(text);
        }
    }
    
    private void resultsFromListModel(){
        results = new HashSet<>();
        mandatory = new HashSet<>();
        for(int i = 0; i < resultsListModel.size(); i++){
            String text = resultsListModel.get(i);
            if(text.contains(ModulesManager.mandatoryString)){
                text = text.replace(ModulesManager.mandatoryString, "");
                mandatory.add(text);
            }
            results.add(text);
        }
    }
    
    private void inputsListModelFromInputList(Set<String> inputs){
        for(String file : inputs){
            String toAdd = null;
            String analysisClassName = "." + AnalysisScript.class.getSimpleName();
            String postAnalysisClassName = "." + PostAnalysisScript.class.getSimpleName();
            if(file.contains(analysisClassName) || file.contains(postAnalysisClassName)){
                System.out.println(file);
                String[] splited = null;
                if(file.contains(File.separator)){
                    splited = file.split(Pattern.quote(File.separator));
                }else if(file.contains("/")){
                    splited = file.split("/");
                }
                String scriptFolder = splited[0];
                String scriptName = "";
                if(file.contains(analysisClassName)){
                    scriptName = scriptFolder.replace(analysisClassName, "");
                }else{
                    scriptName = scriptFolder.replace(postAnalysisClassName, "");
                }
                String finalInput = scriptName + ": " + splited[1];
                for(int i = 2; i < splited.length; i++){
                    finalInput += File.separator + splited[i];
                }
                toAdd = finalInput;
            }else{
                toAdd = file;
            }
            this.inputsListModel.addElement(toAdd);
        }
    }
    
    private void requiredFilesAndScriptsFromListModel(){
        requiredExternalFiles = new HashSet<>();
        requiredScripts = new HashSet<>();
        for(int i = 0; i < this.inputsListModel.size(); i++){
            String inputRaw = inputsListModel.get(i);
            if(inputRaw.contains(": ")){
                String[] splited = inputRaw.split(": ");
                String first = splited[0];
                RScript scr = RScript.availableScripts.get(first);
                String x = scr.name + ".";
                if(scr instanceof AnalysisScript){
                    x += AnalysisScript.class.getSimpleName();
                }else if(scr instanceof PostAnalysisScript){
                    x += PostAnalysisScript.class.getSimpleName();
                }
                x += File.separator;
                x += splited[1];
                requiredScripts.add(scr.name);
                requiredExternalFiles.add(x);
            }else{
                requiredExternalFiles.add(inputRaw);
            }
        }
    }
    
    
    private void listModelFromRequiredParameters(Map<String, Class> params){
        for(String key : params.keySet()){
            String className = params.get(key).getSimpleName();
            paramsListModel.addElement(className + "::" + key);
        }
    }
    
    private void requiredParametersFromListModel(){
        requiredParameters = new HashMap<>();
        for(int i = 0; i < this.paramsListModel.size(); i++){
            String rawParam = paramsListModel.get(i);
            String[] typeAndName = rawParam.split("::");
            if(typeAndName.length == 2){
                String param = typeAndName[1];
                Class type = RScript.availableParamTypes.get(typeAndName[0]);
                //System.out.println(param);
                //System.out.println("puting: " + param + ", " + type.getSimpleName());
                requiredParameters.put(param, type);
            }
        }
    }
    
    private void basicInfoFromUI(){
        name = scriptNameField.getText();
        max2Conditions = this.max2CondOption.isSelected();
    }
    
    private void scriptToBasicInfo(){
        scriptNameField.setText(script.name);
        if(script.max2Conditions){
            max2CondOption.doClick();
        }
        scriptFile = script.getScriptFile();
        this.fileNameLabel.setText("Script file: " + scriptFile.getName());
    }
    
    private void infoAreaFromString(String s){
        this.infoArea.setText(s);
    }
    private void infoFromTextArea(){
        String lineBreaker = null;
        if(infoArea.getText().contains("\r\n")){
            lineBreaker = "\r\n";
        }else if(infoArea.getText().contains("\n")){
            lineBreaker = "\n";
        }
        if(infoArea.getText().contains("\r")){
            lineBreaker = "\r";
        }
        
        if(lineBreaker == null){
            this.info = infoArea.getText();
        }else{
            String multiLineInfo = infoArea.getText();
            this.info = multiLineInfo;
        }
    }
    
    private void gatherInfoFromUI(){
        basicInfoFromUI();
        resultsFromListModel();
        requiredFilesAndScriptsFromListModel();
        requiredParametersFromListModel();
        infoFromTextArea();
    }
    
    private boolean validateFields(){
        if(scriptNameField.getText().length() == 0){
            JOptionPane.showMessageDialog(rootPane, "Please, write a name for the script", 
                    "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(!Global.stringIsLettersAndDigits(scriptNameField.getText())){
            JOptionPane.showMessageDialog(rootPane, "Use only letters and digits"
                    + " on the script name", "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if(scriptFile == null){
            JOptionPane.showMessageDialog(rootPane, "You must choose a script file", 
                    "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }else if(scriptFile.exists() == false){
            JOptionPane.showMessageDialog(rootPane, "The script file does not exist", 
                    "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if(this.resultsListModel.size() == 0){
            JOptionPane.showMessageDialog(rootPane, "The new script must have at "
                    + "least one result", "INPUT ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    
    private void cancel(){
        this.setVisible(false);
        this.dispose();
    }
    
    private void create(){
        if(this.validateFields()){
            this.gatherInfoFromUI();
            if(scriptType == AnalysisScript.class){
                script = new AnalysisScript(this.name, this.scriptFile.getName(), true,
                                            this.requiredParameters, 
                                            this.requiredExternalFiles,
                                            this.results);
            }else{
                script = new PostAnalysisScript(this.name, this.scriptFile.getName(), true,
                                            this.requiredParameters, 
                                            this.requiredExternalFiles,
                                            this.results,
                                            this.requiredScripts);
            }
            script.max2Conditions = this.max2Conditions;
            script.info = this.info;
            for(String res : results){
                if(mandatory.contains(res)){
                    script.setResultAsMandatory(res);
                }
            }
            if(script.workingDirectory.exists()){
                boolean answer = MainGUI.showYesNoDialog("Do you wish to overwrite the " + script.name + " module?");
                if(answer){
                    RScript.deleteScript(script.name);
                }else{
                    script = null;
                    return;
                }
            }
            if(editing && !changedScript && scriptInTemp != null){
                script.createEnvironment(scriptInTemp.getAbsolutePath());
            }else{
                script.createEnvironment(scriptFile.getAbsolutePath());
            }
            editedScript = true;
        }
        this.setVisible(false);
    }

    private void populateFieldsWithValuesFromScript(){
        assert(script != null);
        scriptToBasicInfo();
        listModelFromResults(script.results, script.mandatoryResults);
        inputsListModelFromInputList(script.requiredExternalFiles);
        listModelFromRequiredParameters(script.requiredParameters);
        infoAreaFromString(script.info);
    }
    
    private void populateFieldWithAnalysisScriptDefaults(){
        listModelFromRequiredParameters(RNASeqPackage.getDefaultParameters());
        inputsListModelFromInputList(RNASeqPackage.getDefaultRequiredFiles());
        listModelFromResults(RNASeqPackage.getDefaultResults(), 
                RNASeqPackage.getMandatoryResults());
    }
    
    private void initComponents(){
        this.setTitle("New " + scriptType.getSimpleName() + " Module");
        dialogSize = new Dimension(500, 620);
        int wGap = 5;
        int hGap = 5;
        int rows = 3;
        int cols = 2;
        buttonPanelHeight = 32;
        availableSize = new Dimension(dialogSize.width-20, dialogSize.height-20);
        componentPanelHeight = (availableSize.height-(hGap*(rows))-buttonPanelHeight)/rows;
        componentPanelWidth = (availableSize.width-(wGap*(cols-1)))/cols;
        componentPanelSize = new Dimension(componentPanelWidth, componentPanelHeight);
        scrollerSize = new Dimension(componentPanelWidth, componentPanelHeight-70);
        this.setMinimumSize(dialogSize);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, wGap, hGap));
        setResizable(false);
        
        initGeneralInfo();
        initResults();
        initInputs();
        initInfoArea();
        initParams();
        initButtons();
        
        add(generalInfoPanel);
        add(resultsPanel);
        add(inputsPanel);
        add(paramsPanel);
        add(infoScroller);
        add(buttonsPanel);
        
        if(this.script != null){
            populateFieldsWithValuesFromScript();
        }else if(this.scriptType == AnalysisScript.class){
            populateFieldWithAnalysisScriptDefaults();
        }
    }
    
    private void initGeneralInfo(){
        generalInfoPanel = new Panel();
        generalInfoPanel.setLayout(new BoxLayout(generalInfoPanel, BoxLayout.PAGE_AXIS));
        generalInfoPanel.setPreferredSize(
                new Dimension(componentPanelWidth, componentPanelHeight));
        generalInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scriptNamePanel = new Panel();
        scriptNamePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 3, 6));
        scriptNameLabel = new Label("Name: ");
        scriptNameField = new JTextField();
        scriptNameField.setPreferredSize(new Dimension(120, 25));
        scriptNamePanel.add(scriptNameLabel);
        scriptNamePanel.add(scriptNameField);
        scriptNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        fileNamePanel = new Panel();
        fileNamePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 3, 6));
        if(script == null){
            fileNameLabel = new Label("Script file:     [No Script]    ");
        }else{
            fileNameLabel = new Label("Filename: " + script.getScriptFile().getName());
        }
        selectScriptFileButton = new Button();
        selectScriptFileButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            selectScriptFile();
        });
        //selectScriptFileButton.setText(" ");
        selectScriptFileButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/open-icon-24.png")));
        fileNamePanel.add(fileNameLabel);
        fileNamePanel.add(selectScriptFileButton);
        fileNamePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        max2CondOption = new CheckBox();
        max2CondOption.setText("Maximum of 2 conditions");
        max2CondOption.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        generalInfoPanel.add(Box.createVerticalGlue());
        generalInfoPanel.add(scriptNamePanel);
        generalInfoPanel.add(fileNamePanel);
        generalInfoPanel.add(max2CondOption);
        generalInfoPanel.add(Box.createVerticalGlue());
    }
    
    private void initResults(){
        resultsPanel = new Panel();
        resultsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        resultsPanel.setPreferredSize(new Dimension(componentPanelWidth, componentPanelHeight));
        
        resultsLabel = new BigLabel("Results: ");
        
        resultsListModel = new DefaultListModel<String>();
        resultsList = new JList(resultsListModel);
        resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsList.setLayoutOrientation(JList.VERTICAL);
        resultsList.setVisibleRowCount(-1);
        resultsList.addListSelectionListener((ListSelectionEvent e) -> {
            String selected = (String)resultsList.getSelectedValue();
            if(selected == null){
                eraseResultButton.setEnabled(false);
            }else{
                eraseResultButton.setEnabled(true);
            }
        });
        resultsScroller = new JScrollPane(resultsList);
        resultsScroller.setPreferredSize(new Dimension(scrollerSize.width, scrollerSize.height));
        
        addNewResultButton = new Button();
        addNewResultButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/add-icon-24.png")));
        addNewResultButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addNewResult();
        });
        
        eraseResultButton = new Button();
        eraseResultButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/Delete-icon-24.png")));
        eraseResultButton.setEnabled(false);
        eraseResultButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            eraseResult();
        });
        
        resultsPanel.add(resultsLabel);
        resultsPanel.add(resultsScroller);
        resultsPanel.add(addNewResultButton);
        resultsPanel.add(eraseResultButton);
    }
    
    private void initInputs(){
        inputsPanel = new Panel();
        inputsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        inputsPanel.setPreferredSize(new Dimension(componentPanelWidth, componentPanelHeight));
        
        inputsLabel = new BigLabel("Input Files: ");
        
        inputsListModel = new DefaultListModel<String>();
        inputsList = new JList(inputsListModel);
        inputsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inputsList.setLayoutOrientation(JList.VERTICAL);
        inputsList.setVisibleRowCount(-1);
        inputsList.addListSelectionListener((ListSelectionEvent e) -> {
            String selected = (String)inputsList.getSelectedValue();
            if(selected == null){
                eraseInputButton.setEnabled(false);
            }else{
                eraseInputButton.setEnabled(true);
            }
        });
        inputsScroller = new JScrollPane(inputsList);
        inputsScroller.setPreferredSize(new Dimension(scrollerSize.width, scrollerSize.height));
        
        addNewInputButton = new Button();
        addNewInputButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/add-icon-24.png")));
        addNewInputButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addNewInput();
        });
        addNewInputButton.setEnabled(scriptType != AnalysisScript.class);
        
        eraseInputButton = new Button();
        eraseInputButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/Delete-icon-24.png")));
        eraseInputButton.setEnabled(false);
        eraseInputButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            eraseInput();
        });
        
        inputsPanel.add(inputsLabel);
        inputsPanel.add(inputsScroller);
        inputsPanel.add(addNewInputButton);
        inputsPanel.add(eraseInputButton);
    }
    
    private void initParams(){
        paramsPanel = new Panel();
        paramsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 5));
        paramsPanel.setPreferredSize(new Dimension(componentPanelWidth, componentPanelHeight));
        
        paramsLabel = new BigLabel("Input Parameters: ");
        
        paramsListModel = new DefaultListModel<String>();
        paramsList = new JList(paramsListModel);
        paramsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paramsList.setLayoutOrientation(JList.VERTICAL);
        paramsList.setVisibleRowCount(-1);
        paramsList.addListSelectionListener((ListSelectionEvent e) -> {
            String selected = (String)paramsList.getSelectedValue();
            if(selected == null){
                eraseParamButton.setEnabled(false);
            }else{
                eraseParamButton.setEnabled(true);
            }
        });
        paramsScroller = new JScrollPane(paramsList);
        paramsScroller.setPreferredSize(new Dimension(scrollerSize.width, scrollerSize.height));
        
        addNewParamButton = new Button();
        addNewParamButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/add-icon-24.png")));
        addNewParamButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            addNewParam();
        });
        
        eraseParamButton = new Button();
        eraseParamButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/Delete-icon-24.png")));
        eraseParamButton.setEnabled(false);
        eraseParamButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            eraseParam();
        });
        
        paramsPanel.add(paramsLabel);
        paramsPanel.add(paramsScroller);
        paramsPanel.add(addNewParamButton);
        paramsPanel.add(eraseParamButton);
    }
    
    private void initInfoArea(){
        infoArea = new JTextArea();
        infoArea.setText(this.info);
        infoArea.setEditable(true);
        infoArea.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        infoArea.setLineWrap(true);
        infoScroller = new JScrollPane(infoArea);
        infoScroller.setPreferredSize(new Dimension(availableSize.width, componentPanelHeight-20));
        infoArea.setText("[describe the module here]");
    }
    
    private void initButtons(){
        buttonsPanel = new Panel();
        buttonsPanel.setPreferredSize(new Dimension(availableSize.width, this.buttonPanelHeight));
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        Dimension buttonSize = new Dimension((availableSize.width-20)/2, this.buttonPanelHeight);
        createButton = new BigButton();
        createButton.setText("Create");
        createButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/check-icon-32.png")));
        createButton.setPreferredSize(buttonSize);
        createButton.addActionListener((java.awt.event.ActionEvent evt) ->{
            create();
        });
        
        cancelButton = new BigButton();
        cancelButton.setText("Cancel");
        cancelButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/cancel-icon-32.png")));
        cancelButton.setPreferredSize(buttonSize);
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) ->{
            cancel();
        });
        buttonsPanel.add(createButton);
        buttonsPanel.add(cancelButton);
    }
    
    private void selectScriptFile() {
        JFileChooser fileChooser = new JFileChooser(){
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.isFile() && f.getName().contains(".R")) {
                    super.approveSelection();
                } else{
                    return;
                }
            }
        };
        
        if(fileChooser.showDialog(null, "Select Script File") == JFileChooser.APPROVE_OPTION){
            this.scriptFile = fileChooser.getSelectedFile();
            this.fileNameLabel.setText("Script file: " + scriptFile.getName());
            changedScript = (this.scriptFile.getAbsolutePath().equals(originalScript) == false);
        }
    }
    
    private void eraseResult(){
        int index = resultsList.getSelectedIndex();
        if(index >= 0 && index < resultsListModel.getSize()){
            resultsListModel.remove(index);
        }
    }
    
    private void eraseInput(){
        int index = inputsList.getSelectedIndex();
        if(index >= 0 && index < inputsListModel.getSize()){
            inputsListModel.remove(index);
        }
    }
    
    private void eraseParam(){
        int index = paramsList.getSelectedIndex();
        if(index >= 0 && index < paramsListModel.getSize()){
            paramsListModel.remove(index);
        }
    }
    
    private void addNewResult() {
        //String resultName = JOptionPane.showInputDialog("Insert the name of a script result file: ");
        GetNewResultDialog dialog = new GetNewResultDialog(publicParent, true);
        dialog.setVisible(true);
        if(dialog.validInfo()){
            String toAdd = dialog.name;
            if(dialog.mandatory){
                toAdd += ModulesManager.mandatoryString;
                //mandatory.add(dialog.name);
            }
            resultsListModel.addElement(toAdd);
        }
    }
    
    private void addNewInput(){
        if(this.scriptType == AnalysisScript.class){
            return;
        }
        String result = GetFileFromTreeDialog.getAResult(publicParent, true);
        if(result != null){
            inputsListModel.addElement(result);
        }
    }
    
    private void addNewParam(){
        NewParamDialog dialog = new NewParamDialog(publicParent);
        dialog.setVisible(true);
        if(dialog.isSuccessful()){
            String newParam = dialog.getParamType() + "::" + dialog.getParamName();
            this.paramsListModel.addElement(newParam);
        }
    }
    
    
    Panel scriptNamePanel, fileNamePanel, generalInfoPanel, resultsPanel, 
            inputsPanel, buttonsPanel, paramsPanel;
    JScrollPane resultsScroller, infoScroller, inputsScroller, paramsScroller;
    
    Label scriptNameLabel, fileNameLabel, resultsLabel, paramsLabel, inputsLabel;
    JTextField scriptNameField, fileNameField, newResultField;
    JTextArea infoArea;
    CheckBox max2CondOption;
    DefaultListModel<String> resultsListModel, inputsListModel, paramsListModel;
    JList resultsList, inputsList, paramsList;
    JButton selectScriptFileButton, 
            addNewResultButton, eraseResultButton, 
            eraseInputButton, addNewInputButton,
            eraseParamButton, addNewParamButton,
            createButton, cancelButton;
    
    Dimension dialogSize, availableSize, componentPanelSize, scrollerSize;
    int componentPanelHeight, componentPanelWidth, buttonPanelHeight;
}
