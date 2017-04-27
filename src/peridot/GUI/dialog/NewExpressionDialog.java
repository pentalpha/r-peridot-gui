/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import peridot.IndexedString;
import peridot.RNASeq;

/**
 *
 * @author pentalpha
 */
public class NewExpressionDialog extends Dialog {
    public RNASeq expression;
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
    
    /**
     * Creates new form newExpressionGUI
     */
    public NewExpressionDialog(java.awt.Frame parent, boolean modal, RNASeq givenExpression) {
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
        addNewConditionButton.setPreferredSize(new java.awt.Dimension(220, 60));
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
                conditions = RNASeq.getConditionsFromExpressionFile(file, info);
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
            conditions = RNASeq.loadConditionsFromFile(file);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSeparator1 = new javax.swing.JSeparator();
        adjustPanel = new Panel();
        jLabel1 = new Label();
        conditionsScrollPane = new javax.swing.JScrollPane();
        conditionsPane = new Panel();
        jSeparator2 = new javax.swing.JSeparator();
        bottomButtonsPanel = new Panel();
        createButton = new Button();
        cancelButton = new Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(450, 630));
        getContentPane().setLayout(new java.awt.FlowLayout());

        makeSetFilesPanel();

        jSeparator1.setPreferredSize(new java.awt.Dimension(390, 2));
        getContentPane().add(jSeparator1);

        adjustPanel.setPreferredSize(new java.awt.Dimension(430, 470));

        jLabel1.setText("Adjust conditions of each sample (drag and drop):");
        adjustPanel.add(jLabel1);

        conditionsScrollPane.setFocusCycleRoot(false);
        conditionsScrollPane.setFocusTraversalPolicyProvider(true);
        conditionsScrollPane.setFocusable(false);
        //conditionsScrollPane.setMaximumSize(new java.awt.Dimension(100, 100));
        conditionsScrollPane.setPreferredSize(new java.awt.Dimension(420, 440));

        conditionsPane.setFocusable(false);
        conditionsPane.setMaximumSize(new java.awt.Dimension(380, 2000));
        conditionsPane.setPreferredSize(new java.awt.Dimension(380, 800));
        conditionsPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 3, 3));
        conditionsScrollPane.setViewportView(conditionsPane);

        adjustPanel.add(conditionsScrollPane);

        getContentPane().add(adjustPanel);

        jSeparator2.setPreferredSize(new java.awt.Dimension(390, 2));
        getContentPane().add(jSeparator2);

        bottomButtonsPanel.setPreferredSize(new java.awt.Dimension(430, 40));
        bottomButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        createButton.setText("Create");
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        bottomButtonsPanel.add(createButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        bottomButtonsPanel.add(cancelButton);

        getContentPane().add(bottomButtonsPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void makeSetFilesPanel(){
        setFilesPanel = new Panel();
        setFilesPanel.setPreferredSize(new java.awt.Dimension(430, 60));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0);
        flowLayout1.setAlignOnBaseline(true);
        setFilesPanel.setLayout(flowLayout1);
        
        jPanel1 = new Panel();
        jPanel1.setMinimumSize(new java.awt.Dimension(120, 60));
        jPanel1.setPreferredSize(new java.awt.Dimension(160, 70));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 6));
        
        geneExpressionLabel = new Label();
        geneExpressionLabel.setText("Count reads:");
        
        idAndConditionsLabel = new Label();
        idAndConditionsLabel.setText("Conditions (optional):");
        
        jPanel1.add(geneExpressionLabel);
        jPanel1.add(idAndConditionsLabel);
        
        jPanel2 = new Panel();
        jPanel2.setPreferredSize(new java.awt.Dimension(270, 70));
        jPanel2.setRequestFocusEnabled(false);
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        
        expressionPathField = new javax.swing.JTextField();
        expressionPathField.setPreferredSize(new java.awt.Dimension(220, 25));
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
        idAndConditionsField.setPreferredSize(new java.awt.Dimension(220, 25));
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
        
        jPanel2.add(expressionPathField);
        jPanel2.add(selectExpressionFileButton);
        jPanel2.add(idAndConditionsField);
        jPanel2.add(selectConditionsFileButton);
        
        setFilesPanel.add(jPanel1);
        setFilesPanel.add(jPanel2);

        getContentPane().add(setFilesPanel);
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        // TODO add your handling code here:
        if(expressionFile != null){
            try{
                if(this.changedConditions){
                    SortedMap<IndexedString, String> editedConditions = getConditionsFromUI();
                    expression = new RNASeq(expressionFile, editedConditions, info);
                }else if(conditionsFile != null){
                    expression = new RNASeq(expressionFile, conditionsFile, info);
                }else if (conditionsFile == null){
                    expression = new RNASeq(expressionFile, conditions, info);
                }
                this.setVisible(false);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }else{
            JOptionPane.showMessageDialog(null, "Firstly, you must select a genetic expression file.");
        }
    }//GEN-LAST:event_createButtonActionPerformed
    
    public RNASeq getResults(){
        return expression;
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
    }//GEN-LAST:event_selectConditionsFileButtonActionPerformed

    
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton selectConditionsFileButton;
    private javax.swing.JButton selectExpressionFileButton;
    private javax.swing.JPanel setFilesPanel;
    // End of variables declaration//GEN-END:variables
}
