/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.AnalysisData;
import peridot.GUI.MainGUI;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.Label;
import peridot.GUI.component.Dialog;
import peridot.GUI.component.Panel;
import peridot.GUI.component.Button;
import peridot.Archiver.Spreadsheet;
import peridot.Archiver.Manager;
import java.awt.Dimension;
import peridot.GUI.panel.ConditionPanel;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import peridot.IndexedString;

/**
 *
 * @author pentalpha
 */
public class NewExpressionDialog extends Dialog {
    public AnalysisData expression;
    int thresholdMin = 0;
    int thresholdMax = 10;
    int threshold = 5;
    String roundingMode = "HALF_UP";
    String[] roundingModes = {"HALF_UP","HALF_DOWN","UP","DOWN"};
    private File expressionFile, conditionsFile;
    private SortedMap<IndexedString, String> conditions;
    public boolean changedConditions = false;
    private HashMap<String, ConditionPanel> conditionPanels;
    private JButton addNewConditionButton;
    java.awt.Frame parent;
    //boolean headerOnFirstLine = false;
    //boolean noIDCell = false;
    private static NewExpressionDialog _instance = null;
    boolean loadedFromPrevious = false;
    
    Spreadsheet.Info info;

    static private Dimension dialogSize = new java.awt.Dimension(MainGUI.defaultSize.width+100, 700);
    static private Dimension jSeparatorSize = new java.awt.Dimension(dialogSize.width-60, 3);
    static private Dimension adjustPanelSize = new java.awt.Dimension(dialogSize.width-20, dialogSize.height-300);
    static private Dimension scrollPaneSize = new java.awt.Dimension(adjustPanelSize.width-10, adjustPanelSize.height-30);
    static public Dimension conditionsPaneSize = new java.awt.Dimension(scrollPaneSize.width-50, scrollPaneSize.height+10);
    
    /**
     * Creates new form newExpressionGUI
     */
    public NewExpressionDialog(java.awt.Frame parent, boolean modal, AnalysisData givenExpression) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.setFocusable(false);
        this.setTitle("Define Expression Data");
        
        expressionPathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                expressionFieldValueChanged();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
              expressionFieldValueChanged();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
              expressionFieldValueChanged();
            }
        });
        
        idAndConditionsField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                idAndConditionsFieldValueChanged();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                idAndConditionsFieldValueChanged();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                idAndConditionsFieldValueChanged();
            }
        });
        conditionPanels = new HashMap<String, ConditionPanel>();
        addNewConditionButton = new Button();
        addNewConditionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/peridot/GUI/icons/Add-Green-Button-icon-32.png"))); // NOI18N
        addNewConditionButton.setText("Add New Condition");
        addNewConditionButton.setPreferredSize(new java.awt.Dimension(conditionsPaneSize.width-50, 50));
        addNewConditionButton.setFocusable(false);
        addNewConditionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //Log.info("no stackoverflow at line 82");
                addNewConditionButtonActionPerformed(evt);
            }
        });
        
        expression = givenExpression;
        if(expression != null){
            loadedFromPrevious = true;
            //this.noIDCell = expression.noGeneIDCell;
            //this.headerOnFirstLine = expression.headerOnFirstLine;
            this.info = expression.info;
            if(expression.conditions != null){
                conditions = expression.conditions;
            }
            expressionPathField.setText(expression.expressionFile.getAbsolutePath());
            expressionFile = expression.expressionFile;
            
            if(expression.conditionsFile != null){
                idAndConditionsField.setText(expression.conditionsFile.getAbsolutePath());
                conditionsFile = expression.conditionsFile;
            }
            this.updateSetList();
        }
        
        _instance = this;
    }
    
    public static void setChangedConditions(boolean x){
        _instance.changedConditions = x;
    }
    
    public static void updateConditionName(String name, String newName){
        _instance.conditions = _instance.getConditionsFromUI();
        //Log.info("[RENAMING " + name + "TO" + newName + "]");
        ConditionPanel conditionPanel = _instance.conditionPanels.get(name);
        conditionPanel.setFocusable(false);
        _instance.conditionPanels.remove(name);
        _instance.conditionPanels.put(newName, conditionPanel);
        
        TreeSet<IndexedString> samplesToMove = new TreeSet<>();
        for(Map.Entry<IndexedString, String> pair : _instance.conditions.entrySet()){
            if(pair.getValue().equals(name)){
                samplesToMove.add(pair.getKey());
            }
        }
        
        for(IndexedString sample : samplesToMove){
            _instance.conditions.remove(sample);
        }
        for(IndexedString sample : samplesToMove){
            _instance.conditions.put(sample, newName);
        }
        _instance.changedConditions = true;
        _instance.updateSetList();
    }
    
    private void updateSetList(){
        //erase this later:
        //Log.info("[UPDATING CONDITION EDITOR PANEL]");
        //for(Map.Entry<IndexedString, String> pair : conditions.entrySet()){
            //Log.info(pair.getKey().toString() + " -> " + pair.getValue());
        //}
        
        HashMap<String, LinkedList<IndexedString>> namesByCondition = new HashMap<>();
        conditionPanels.clear();
        conditionsPane.removeAll();
        //LinkedHashMap<String, Integer> nameIndexes = new LinkedHashMap<String, Integer>();
        //Integer index = 1;
        for(Map.Entry<IndexedString, String> pair : conditions.entrySet()){
            if(namesByCondition.containsKey(pair.getValue())){
                namesByCondition.get(pair.getValue()).add(pair.getKey());
            }else{
                namesByCondition.put(pair.getValue(), new LinkedList<IndexedString>());
                namesByCondition.get(pair.getValue()).add(pair.getKey());
            }
            //nameIndexes.put(pair.getKey(), index);
           // index++;
        }
        for(Map.Entry<String, LinkedList<IndexedString>> pair : namesByCondition.entrySet()){
            
            Object[] objArray = pair.getValue().toArray();
            IndexedString[] arrayFinal = new IndexedString[objArray.length];
            for(int i = 0; i < objArray.length; i++){
                if(objArray[i] instanceof IndexedString){
                    arrayFinal[i] = (IndexedString) objArray[i];
                }else{
                    arrayFinal[i] = new IndexedString(-1 , "conversion-error-in:" + objArray[i].toString());
                }
            }
            ConditionPanel conditionPanel = new ConditionPanel(arrayFinal, pair.getKey(), true);
            conditionPanel.setFocusable(false);
            conditionPanels.put(pair.getKey(), conditionPanel);
            conditionsPane.add(conditionPanel);
        }
        if(!conditionPanels.containsKey("not-use")){
            ConditionPanel conditionPanel = new ConditionPanel(new IndexedString[0], "not-use", false);
            conditionPanel.setFocusable(false);
            conditionPanels.put("not-use", conditionPanel);
            conditionsPane.add(conditionPanel);
        }
        
        conditionsPane.add(addNewConditionButton);
        conditionsPane.revalidate();
        conditionsPane.repaint();
    }
    
    private SortedMap<IndexedString, String>  getConditionsFromUI(){
        SortedMap<IndexedString, String> newConditions = new TreeMap<>();
        //Log.info("[GETTING CONDITIONS FROM UI]");
        //int i = 1;
        for(Map.Entry<String, ConditionPanel> pane : conditionPanels.entrySet()){
            ListModel model = pane.getValue().contents.getModel();
            for(int i = 0; i < model.getSize(); i++){
                Object value = model.getElementAt(i);
                if(value instanceof IndexedString){
                    newConditions.put((IndexedString)value, pane.getKey());
                    //Log.info("Found " + value.toString() + " -> " + pane.getKey());
                }else{
                    //Log.info("Unidentified object in JList: " + value.toString());
                }
            }
        }
        
        return newConditions;
    }
    
    private boolean selectExpressionByFile(String filePath){
        File file = new File(filePath);
        if(file.canRead()){
            if(Spreadsheet.fileIsCSVorTSV(file)){
                conditions = AnalysisData.getConditionsFromExpressionFile(file, info);
                expressionFile = file;
                updateSetList();
                setChangedConditions(false);
                return true;
            }
            else{
                JOptionPane.showMessageDialog(null, "Please select the correct file format (.csv/.tsv)");
            }
        }else{
            JOptionPane.showMessageDialog(null, "The file cant be read.");
        }
        return false;
    }
    
    private boolean selectConditionsByFile(String filePath){
        File file = new File(filePath);
        if(file.canRead()){
            //conditions = AnalysisData.getConditionsFromExpressionFile(file, info);
            SortedMap<IndexedString, String> newConditions = AnalysisData.loadConditionsFromFile(file);
            for(Map.Entry<IndexedString, String> entry : newConditions.entrySet()){
                conditions.put(entry.getKey(), entry.getValue());
            }
            this.conditionsFile = file;
            updateSetList();
            setChangedConditions(false);
            return true;
        }else{
            JOptionPane.showMessageDialog(null, "The file cant be read.");
        }
        return false;
    }
    
    public void expressionFieldValueChanged(){
        if(loadedFromPrevious){
            loadedFromPrevious = false;
            updateSetList();
            setChangedConditions(false);
        }else if(Manager.fileExists(expressionPathField.getText())){
            selectExpressionByFile(expressionPathField.getText());
        }else{
            //Log.info("arquivo de expressao nao existe");
        }
    }
    
    public void idAndConditionsFieldValueChanged(){
        if(Manager.fileExists(idAndConditionsField.getText())){
            selectConditionsByFile(idAndConditionsField.getText());
        }else{
            //Log.info("arquivo de condiçoes nao existe");
        }
    }

    private void initComponents() {
        this.setResizable(false);
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        adjustPanel = new Panel();
        jLabel1 = new Label();
        conditionsScrollPane = new javax.swing.JScrollPane();
        conditionsPane = new Panel();
        bottomButtonsPanel = new Panel();
        createButton = new Button();
        cancelButton = new Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(dialogSize);
        getContentPane().setLayout(new java.awt.FlowLayout());

        makeSetFilesPanel();

        jSeparator1.setPreferredSize(jSeparatorSize);
        getContentPane().add(jSeparator1);

        adjustPanel.setPreferredSize(adjustPanelSize);

        jLabel1.setText("Adjust conditions of each sample (drag and drop):");
        adjustPanel.add(jLabel1);

        conditionsScrollPane.setFocusCycleRoot(false);
        conditionsScrollPane.setFocusTraversalPolicyProvider(true);
        conditionsScrollPane.setFocusable(false);
        //conditionsScrollPane.setMaximumSize(new java.awt.Dimension(100, 100));
        conditionsScrollPane.setPreferredSize(scrollPaneSize);

        conditionsPane.setFocusable(false);
        conditionsPane.setMinimumSize(conditionsPaneSize);
        conditionsPane.setMaximumSize(new java.awt.Dimension(conditionsPaneSize.width, 1930));
        conditionsPane.setLayout(new WrapLayout(java.awt.FlowLayout.CENTER, 0, 3));
        //conditionsPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 3, 3));
        conditionsScrollPane.setViewportView(conditionsPane);

        adjustPanel.add(conditionsScrollPane);

        getContentPane().add(adjustPanel);

        jSeparator2.setPreferredSize(jSeparatorSize);
        getContentPane().add(jSeparator2);

        roundingPanel = new Panel();
        roundingPanel.setPreferredSize(new java.awt.Dimension(dialogSize.width, 30));

        roundingModesLabel = new Label("Integer rounding: ");
        roundingModesComboBox = new JComboBox<>();
        for (String s : roundingModes){
            roundingModesComboBox.addItem(s);
        }
        roundingPanel.add(roundingModesLabel);
        roundingPanel.add(roundingModesComboBox);
        getContentPane().add(roundingPanel);

        thresholdPanel = new Panel();
        thresholdPanel.setPreferredSize(new java.awt.Dimension(dialogSize.width, 80));
        thresholdLabel = new Label("Count reads threshold: ");
        thresholdSlider = new JSlider(JSlider.HORIZONTAL,
                thresholdMin, thresholdMax, threshold);
        thresholdSlider.setMajorTickSpacing(2);
        thresholdSlider.setMinorTickSpacing(1);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setPaintLabels(true);

        thresholdPanel.add(thresholdLabel);
        thresholdPanel.add(thresholdSlider);
        getContentPane().add(thresholdPanel);

        jSeparator3.setPreferredSize(jSeparatorSize);
        getContentPane().add(jSeparator3);

        bottomButtonsPanel.setPreferredSize(new java.awt.Dimension(dialogSize.width, 40));
        bottomButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        createButton.setText("Create");
        createButton.setPreferredSize(new Dimension(70, 35));
        createButton.addActionListener(evt -> createButtonActionPerformed(evt));
        bottomButtonsPanel.add(createButton);

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new Dimension(70, 35));
        cancelButton.addActionListener(evt -> cancelButtonActionPerformed(evt));
        bottomButtonsPanel.add(cancelButton);

        getContentPane().add(bottomButtonsPanel);

        pack();
    }
    
    private void makeSetFilesPanel(){
        setFilesPanel = new Panel();
        Dimension size = new java.awt.Dimension(dialogSize.width-20, 60);
        setFilesPanel.setPreferredSize(size);
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0);
        flowLayout1.setAlignOnBaseline(true);
        setFilesPanel.setLayout(flowLayout1);

        Dimension labelsSize = new java.awt.Dimension(160, 70);
        labelsPanel = new Panel();
        labelsPanel.setMinimumSize(new java.awt.Dimension(labelsSize.width-40,
                labelsSize.height-10));
        labelsPanel.setPreferredSize(labelsSize);
        labelsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 6));
        
        geneExpressionLabel = new Label();
        geneExpressionLabel.setText("Count reads:");
        
        idAndConditionsLabel = new Label();
        idAndConditionsLabel.setText("Conditions (optional):");
        
        labelsPanel.add(geneExpressionLabel);
        labelsPanel.add(idAndConditionsLabel);

        Dimension pathsSize = new java.awt.Dimension(size.width-labelsSize.width-10, labelsSize.height);
        pathsPanel = new Panel();
        pathsPanel.setPreferredSize(pathsSize);
        pathsPanel.setRequestFocusEnabled(false);
        pathsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        
        expressionPathField = new javax.swing.JTextField();
        expressionPathField.setPreferredSize(new java.awt.Dimension(pathsSize.width-50, 25));
        expressionPathField.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                expressionPathFieldInputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        expressionPathField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expressionPathFieldActionPerformed(evt);
            }
        });
        expressionPathField.setEditable(false);
        
        selectExpressionFileButton = new Button();
        selectExpressionFileButton.setText("");
        selectExpressionFileButton.setPreferredSize(new Dimension(40, 25));
        selectExpressionFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectExpressionFileButtonActionPerformed(evt);
            }
        });
        selectExpressionFileButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/open-icon-24.png")));
        
        idAndConditionsField = new javax.swing.JTextField();
        idAndConditionsField.setPreferredSize(new java.awt.Dimension(pathsSize.width-50, 25));
        idAndConditionsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idAndConditionsFieldActionPerformed(evt);
            }
        });
        idAndConditionsField.setEditable(false);
        idAndConditionsField.setEnabled(false);
        
        selectConditionsFileButton = new Button();
        selectConditionsFileButton.setText("");
        selectConditionsFileButton.setPreferredSize(new Dimension(40, 25));
        selectConditionsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectConditionsFileButtonActionPerformed(evt);
            }
        });
        selectConditionsFileButton.setEnabled(false);
        selectConditionsFileButton.setIcon(new ImageIcon(getClass().getResource("/peridot/GUI/icons/open-icon-24.png")));
        
        pathsPanel.add(expressionPathField);
        pathsPanel.add(selectExpressionFileButton);
        pathsPanel.add(idAndConditionsField);
        pathsPanel.add(selectConditionsFileButton);
        
        setFilesPanel.add(labelsPanel);
        setFilesPanel.add(pathsPanel);

        getContentPane().add(setFilesPanel);
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // TODO add your handling code here:
        conditions = getConditionsFromUI();
        if(expressionFile != null){
            /*try{
                if(this.changedConditions){
                    SortedMap<IndexedString, String> editedConditions = getConditionsFromUI();
                    expression = new AnalysisData(expressionFile, editedConditions, info,
                            roundingModesComboBox.getItemAt(roundingModesComboBox.getSelectedIndex()),
                            thresholdSlider.getValue());
                }else if(conditionsFile != null){
                    expression = new AnalysisData(expressionFile, conditionsFile, info,
                            roundingModesComboBox.getItemAt(roundingModesComboBox.getSelectedIndex()),
                            thresholdSlider.getValue());
                }else if (conditionsFile == null){
                    expression = new AnalysisData(expressionFile, conditions, info,
                            roundingModesComboBox.getItemAt(roundingModesComboBox.getSelectedIndex()),
                            thresholdSlider.getValue());
                }
                this.setVisible(false);
            }catch(IOException ex){
                ex.printStackTrace();
            }*/
            this.setVisible(false);
        }else{
            JOptionPane.showMessageDialog(null, "Firstly, you must select a genetic expression file.");
        }
    }//GEN-LAST:event_createButtonActionPerformed
    
    public AnalysisData getResults(){
        try {
            AnalysisData expr = new AnalysisData(expressionFile, getConditionsFromUI(), info,
                    roundingModesComboBox.getItemAt(roundingModesComboBox.getSelectedIndex()),
                    thresholdSlider.getValue());
            return expr;
        }catch(IOException ex){
            ex.printStackTrace();
            return null;
        }
    }
    
    private void selectConditionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectConditionsFileButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser(){
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.isFile()) {
                    super.approveSelection();
                } else{
                    return;
                }
            }
        };
        
        if(fileChooser.showDialog(null, "Open File") == JFileChooser.APPROVE_OPTION){
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            //if(this.selectConditionsByFile(filePath) == true){
                idAndConditionsField.setText(filePath); 
            //}
        }
    }

    
    private void selectExpressionFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectExpressionFileButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser(){
            public void approveSelection() {
                File f = getSelectedFile();
                if (f.isFile()
                    &&(f.getName().contains(".tsv")
                       || f.getName().contains(".TSV")
                       || f.getName().contains(".csv")
                       || f.getName().contains(".csv"))) {
                    super.approveSelection();
                } else{
                    return;
                }
            }
        };
        //
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //fileChooser.setFileFilter(Places.Spreadsheet.getGeneFileFilter());
        if(fileChooser.showDialog(null, "Open File") == JFileChooser.APPROVE_OPTION){
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try{
                info = SpreadsheetInfoDialog.getInfo(fileChooser.getSelectedFile());
            }catch(Exception ex){
                
            }
            expressionPathField.setText(filePath);
            idAndConditionsField.setText(filePath + ".conditions");
            idAndConditionsField.setEnabled(true);
            selectConditionsFileButton.setEnabled(true);
        }
    }//GEN-LAST:event_selectExpressionFileButtonActionPerformed
    //i have no idea when exactly this event happens, so just leave it empty...
    private void expressionPathFieldInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_expressionPathFieldInputMethodTextChanged
        // TODO add your handling code here:
        
        
    }//GEN-LAST:event_expressionPathFieldInputMethodTextChanged
    //but those events happen after the user hits ENTER
    private void expressionPathFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expressionPathFieldActionPerformed
        // TODO add your handling code here:
        this.expressionFieldValueChanged();
    }//GEN-LAST:event_expressionPathFieldActionPerformed

    private void idAndConditionsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idAndConditionsFieldActionPerformed
        // TODO add your handling code here:
        this.idAndConditionsFieldValueChanged();
    }//GEN-LAST:event_idAndConditionsFieldActionPerformed

    private void addNewConditionButtonActionPerformed(java.awt.event.ActionEvent evt){
        conditionsPane.remove(this.addNewConditionButton);
        int newIndex = 1;
        while(conditionPanels.containsKey("condition"+Integer.toString(newIndex))){
            newIndex++;
        }
        
        ConditionPanel conditionPanel = new ConditionPanel(new IndexedString[0],
                                                           "condition"+Integer.toString(newIndex), true);
        conditionPanel.setFocusable(false);
        conditionPanels.put("condition"+Integer.toString(newIndex), conditionPanel);
        conditionsPane.add(conditionPanel);
        conditionsPane.add(this.addNewConditionButton);
        
        conditionsPane.revalidate();
        conditionsPane.repaint();
    }
    
    public static String inputConditionName(String oldName){
        InputConditionNameDialog dialog = new InputConditionNameDialog(_instance.parent, oldName);
        dialog.setVisible(true);
        
        return dialog.getInput();
    }

    private javax.swing.JPanel adjustPanel;
    private javax.swing.JPanel bottomButtonsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel conditionsPane;
    private javax.swing.JScrollPane conditionsScrollPane;
    private javax.swing.JButton createButton;
    private javax.swing.JTextField expressionPathField;
    private javax.swing.JLabel geneExpressionLabel;
    private javax.swing.JTextField idAndConditionsField;
    private javax.swing.JLabel idAndConditionsLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel labelsPanel;
    private javax.swing.JPanel pathsPanel;
    private javax.swing.JSeparator jSeparator1, jSeparator2, jSeparator3;
    private javax.swing.JButton selectConditionsFileButton;
    private javax.swing.JButton selectExpressionFileButton;
    private javax.swing.JPanel setFilesPanel;

    private javax.swing.JPanel roundingPanel;
    private javax.swing.JLabel roundingModesLabel;
    private javax.swing.JComboBox<String> roundingModesComboBox;

    private javax.swing.JPanel thresholdPanel;
    private javax.swing.JLabel thresholdLabel;
    private javax.swing.JSlider thresholdSlider;
}
