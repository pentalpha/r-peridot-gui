/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.GUI.component.CheckBox;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import peridot.AnalysisParameters;
import peridot.Archiver.Places;
import peridot.GeneIdType;
import peridot.Global;
import peridot.Log;

/**
 *
 * @author pentalpha
 */
public class ParametersPanel extends Panel {
    AnalysisParameters params;
    Map<String, JPanel> paramPanels;
    Map<String, JCheckBox> paramCheckboxes;
    Map<String, JLabel> paramLabels;
    Map<String, JComponent> paramFields;
    Map<String, Object> defaultValues = AnalysisParameters.getDefaultValues();
    private boolean defaultValueButtons;
    /**
     * Creates new form ParametersPanel
     */
    public ParametersPanel(boolean defaultValueButtons) {
        super();
        paramPanels = new HashMap<>();
        paramCheckboxes = new HashMap<>();
        paramLabels = new HashMap<>();
        paramFields = new HashMap<>();
        params = new AnalysisParameters();
        this.defaultValueButtons = defaultValueButtons;
    }
    
    public ParametersPanel(AnalysisParameters initialValues, boolean defaultValueButtons) {
        super();
        paramPanels = new HashMap<>();
        paramCheckboxes = new HashMap<>();
        paramLabels = new HashMap<>();
        paramFields = new HashMap<>();
        params = new AnalysisParameters();
        this.defaultValueButtons = defaultValueButtons;
        this.setParams(initialValues);
    }
    

    
    public void setParams(AnalysisParameters initialValues){
        if(initialValues != null){
            params = initialValues;
        }
        customInitComponents();
        for(Map.Entry<String, JCheckBox> pair : paramCheckboxes.entrySet()){
            pair.getValue().doClick();
        }
        if(initialValues == null){
            for(Map.Entry<String, JCheckBox> pair : paramCheckboxes.entrySet()){
                pair.getValue().doClick();
            }
        }else{
            for(Map.Entry<String, JCheckBox> pair : paramCheckboxes.entrySet()){
                if(initialValues.parameters.containsKey(pair.getKey())){
                    pair.getValue().doClick();
                }
            }
        }
        
        if(!defaultValueButtons){
            removeCheckboxes();
        }
    }
    
    public boolean fieldIsDefault(String name){
        if(this.paramCheckboxes.containsKey(name)){
            if(paramCheckboxes.get(name).isSelected()){
                return true;
            }
        }
        return false;
    }
    
    public AnalysisParameters getParameters(){
        AnalysisParameters values = new AnalysisParameters(params.requiredParameters);
        
        for(Map.Entry<String, Class> pair : params.requiredParameters.entrySet()){
            if(fieldIsDefault(pair.getKey()) == false){
                JComponent inputField = paramFields.get(pair.getKey());
                if(inputField instanceof JSpinner){
                    //Log.logger.info("jspinner field");
                    JSpinner spinner = (JSpinner) inputField;
                    Object fieldValue = spinner.getModel().getValue();
                    if(pair.getValue() == Float.class)
                    {
                        if(fieldValue instanceof Float){
                            values.passParameter(pair.getKey(), (Float)fieldValue);
                        }else if(fieldValue instanceof String){
                            Float floatValue = null;
                            try{
                                floatValue = Float.parseFloat((String)fieldValue);
                            }catch(Exception ex){
                                floatValue = new Float(0);
                            }
                            values.passParameter(pair.getKey(), floatValue);
                        }
                    }
                    else if(pair.getValue() == Integer.class)
                    {
                        if(fieldValue instanceof Integer){
                            values.passParameter(pair.getKey(), (Integer)fieldValue);
                        }else if(fieldValue instanceof String){
                            Integer intValue = null;
                            try{
                                intValue = Integer.parseInt((String)fieldValue);
                            }catch(Exception ex){
                                intValue = new Integer(0);
                            }
                            values.passParameter(pair.getKey(), intValue);
                        }
                    }
                }
                else if(inputField instanceof JComboBox)
                {
                    //Log.logger.info("comboboxfield field");
                    JComboBox comboBox = (JComboBox)inputField;
                    if(pair.getValue() == GeneIdType.class)
                    {
                        GeneIdType selectedValue = (GeneIdType)comboBox.getSelectedItem();
                        if(!values.passParameter(pair.getKey(), selectedValue)){
                            Log.logger.info("could not pass: " + pair.getKey() + " -> " + selectedValue);
                        }else{
                            //Log.logger.info("passing: " + pair.getKey() + " -> " + selectedValue);
                        }
                    }else{
                        //Log.logger.info(pair.getValue().getName() + " != " + GeneIdType.class.getName());
                    }
                }else{
                    Log.logger.info("unidentified field");
                }
            }
        }
        
        return values;
    }
    
    public void removeCheckboxes(){
        LinkedList<String> toremove = new LinkedList<String>();
        for(Map.Entry<String, JCheckBox> pair : paramCheckboxes.entrySet()){
            if(pair.getValue().isSelected()){
                pair.getValue().doClick();
            }
            toremove.add(pair.getKey());
            paramPanels.get(pair.getKey()).remove(pair.getValue());
        }
        for(String name : toremove){
            paramCheckboxes.remove(name);
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

        setMinimumSize(new java.awt.Dimension(348, 73));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 12, 2));
    }// </editor-fold>//GEN-END:initComponents
    
    private SpinnerModel getModel(Class type, String name, Object value){
        SpinnerModel model = null;
        if(type == Float.class)
        {
            if(value == null){
                if(this.defaultValues.containsKey(name)){
                    Float defaultValue = (Float)defaultValues.get(name);
                    if(defaultValue.equals(new Float(0))){
                        model = getSpinnerNotUseOrFloatModel();
                    }else{
                        model = new javax.swing.SpinnerNumberModel(
                               defaultValue.floatValue(), 0f, null, 0.001f);
                    }
                }else{
                    model = new javax.swing.SpinnerNumberModel(
                                                        0.01f, 0f, null, 0.001f);
                }
            }else{
                if(value.equals(new Float(0))){
                    model = getSpinnerNotUseOrFloatModel();
                }else{
                    model = new javax.swing.SpinnerNumberModel(
                            ((Float)value).floatValue(), 0f, null, 0.001f);
                }
            }
        }
        else if(type == Integer.class)
        {
            if(value == null){
                if(this.defaultValues.containsKey(name)){
                    Integer defaultValue = (Integer)defaultValues.get(name);
                    if(defaultValue.equals(new Integer(0))){
                        model = getSpinnerNotUseOrIntModel();
                    }else{
                        model = new javax.swing.SpinnerNumberModel(
                                defaultValue.intValue(), Integer.valueOf(0), 
                                null, Integer.valueOf(1));
                    }
                }else{
                    model = getSpinnerNotUseOrIntModel();
                }
            }else{
                if(value.equals(new Integer(0))){
                    model = getSpinnerNotUseOrIntModel();
                }else{
                    model = new javax.swing.SpinnerNumberModel(
                            ((Integer)value).intValue(), Integer.valueOf(0), null, Integer.valueOf(1));
                }
            }
        }
        return model;
    }
    
    private void customInitComponents(){
        
        for(Map.Entry<String, Class> pair : params.requiredParameters.entrySet()){
            JPanel panel = new Panel();
            JLabel label = new Label();
            JComponent field;
            JCheckBox checkbox = new CheckBox();
            
            label.setText(Global.getNaturallyWritenString(pair.getKey()));
            
            if(pair.getValue() == Integer.class || pair.getValue() == Float.class){
                JSpinner spinner = new JSpinner();
                spinner.setPreferredSize(new java.awt.Dimension(90, 28));
                Class type = pair.getValue();
                Object value = null;
                if(params.parameters.containsKey(pair.getKey())){
                    value = params.parameters.get(pair.getKey());
                }
                spinner.setModel(getModel(type, pair.getKey(), value));

                spinner.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        if(type == Float.class){
                            checkForNotUseFloat(spinner);
                        } else if(type == Integer.class){
                            checkForNotUseInt(spinner);
                        }
                    }
                });
                field = spinner;
            }else if(pair.getValue() == GeneIdType.class){
                JComboBox comboBox = new JComboBox();
                for(String idType : GeneIdType.defaultIDTypes){
                    comboBox.addItem(new GeneIdType(idType));
                }
                field = comboBox;
            }else{
                field = null;
            }
            
            checkbox.setText("default:");
            checkbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    if(checkbox.isSelected()){
                        field.setEnabled(false);
                    }else{
                        field.setEnabled(true);
                    }
                }
            });
            
            panel.add(checkbox);
            panel.add(label);
            panel.add(field);
            
            this.paramCheckboxes.put(pair.getKey(), checkbox);
            this.paramFields.put(pair.getKey(), field);
            this.paramLabels.put(pair.getKey(), label);
            this.paramPanels.put(pair.getKey(), panel);
            add(panel);
        }
    }
    
    private void checkForNotUseFloat(javax.swing.JSpinner Adjust){
        if(Adjust.getValue().toString().contentEquals("0.001"))
        {
            Adjust.setModel(getSpinnerFloatModel());
        }
        else if(Adjust.getValue().toString().contentEquals("0.0"))
        {
            Adjust.setModel(getSpinnerNotUseOrFloatModel());
        }
    }
    
    private void checkForNotUseInt(javax.swing.JSpinner Adjust){
        if(Adjust.getValue().toString().contentEquals("1")){
            Adjust.setModel(getSpinnerIntModel());
        }
        else if(Adjust.getValue().toString().contentEquals("0")){
            Adjust.setModel(getSpinnerNotUseOrIntModel());
        }
    }
    
    private SpinnerNumberModel getSpinnerIntModel(){
        return new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(0), null, Integer.valueOf(1));
    }
    
    private SpinnerNumberModel getSpinnerFloatModel(){
        return new javax.swing.SpinnerNumberModel(0.001f, 0.0f, null, 0.001f);
    }
    
    private SpinnerListModel getSpinnerNotUseOrFloatModel(){
        String setDefault[] = new String[2];
        setDefault[0] = "Not Use";
        setDefault[1] = "0.001";
        return new SpinnerListModel(setDefault);
    }
    
    private SpinnerListModel getSpinnerNotUseOrIntModel(){
        String setDefault[] = new String[2];
        setDefault[0] = "Not Use";
        setDefault[1] = "1";
        return new SpinnerListModel(setDefault);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
