/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.panel;

import javafx.scene.control.ComboBox;
import peridot.*;
import peridot.GUI.GUIUtils;
import peridot.GUI.component.CheckBox;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;

import javax.swing.*;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

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
                                floatValue = Float.valueOf(0);
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
                                intValue = Integer.valueOf(0);
                            }
                            values.passParameter(pair.getKey(), intValue);
                        }
                    }
                }
                else if(inputField instanceof JComboBox)
                {
                    //Log.logger.info("comboboxfield field");
                    JComboBox comboBox = (JComboBox)inputField;
                    if(pair.getValue() == GeneIdType.class) {
                        GeneIdType selectedValue = (GeneIdType) comboBox.getSelectedItem();
                        if (!values.passParameter(pair.getKey(), selectedValue)) {
                            Log.logger.info("could not pass: " + pair.getKey() + " -> " + selectedValue);
                        } else {
                            //Log.logger.info("passing: " + pair.getKey() + " -> " + selectedValue);
                        }
                    }else if(pair.getValue() == Organism.class)
                    {
                        Organism selectedValue = (Organism)comboBox.getSelectedItem();
                        if(!values.passParameter(pair.getKey(), selectedValue)){
                            Log.logger.info("could not pass: " + pair.getKey() + " -> " + selectedValue);
                        }else{
                            //Log.logger.info("passing: " + pair.getKey() + " -> " + selectedValue);
                        }
                    }else if(pair.getValue() == Float.class){
                        Object fieldValue = comboBox.getSelectedItem();
                        if(fieldValue instanceof Float){
                            values.passParameter(pair.getKey(), (Float)fieldValue);
                        }else if(fieldValue instanceof String){
                            Float floatValue = null;
                            try{
                                floatValue = Float.parseFloat((String)fieldValue);
                            }catch(Exception ex){
                                floatValue = Float.valueOf(0);
                            }
                            values.passParameter(pair.getKey(), floatValue);
                        }
                    }else if(pair.getValue() == ConsensusThreshold.class){
                        Object fieldValue = comboBox.getSelectedItem();
                        if(fieldValue instanceof ConsensusThreshold){
                            values.passParameter(pair.getKey(), (ConsensusThreshold)fieldValue);
                        }else if(fieldValue instanceof String){
                            ConsensusThreshold value = null;
                            try{
                                value = new ConsensusThreshold((String)fieldValue);
                            }catch(Exception ex){
                                value = new ConsensusThreshold("5");
                            }
                            values.passParameter(pair.getKey(), value);
                        }
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
    
    private SpinnerModel getModel(Class type, String name, Object value){
        SpinnerModel model = null;
        if(type == Float.class)
        {
            if(value == null){
                if(this.defaultValues.containsKey(name)){
                    Float defaultValue = (Float)defaultValues.get(name);
                    if(defaultValue.equals(Float.valueOf(0))){
                        model = getSpinnerNotUseOrFloatModel();
                    }else{
                        model = new javax.swing.SpinnerNumberModel(
                               defaultValue.floatValue(), 0f, null, 0.1f);
                    }
                }else{
                    model = new javax.swing.SpinnerNumberModel(
                                                        0.01f, 0f, null, 0.1f);
                }
            }else{
                if(value.equals(Float.valueOf(0))){
                    model = getSpinnerNotUseOrFloatModel();
                }else{
                    model = new javax.swing.SpinnerNumberModel(
                            ((Float)value).floatValue(), 0f, null, 0.01f);
                }
            }
        }
        else if(type == Integer.class)
        {
            if(value == null){
                if(this.defaultValues.containsKey(name)){
                    Integer defaultValue = (Integer)defaultValues.get(name);
                    if(defaultValue.equals(Integer.valueOf(0))){
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
                if(value.equals(Integer.valueOf(0))){
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
        //Log.logger.info(""+params.requiredParameters.size());
        for(Map.Entry<String, Class> pair : params.requiredParameters.entrySet()){

            JPanel panel = new Panel();
            JLabel label = new Label();
            JComponent field;
            JCheckBox checkbox = new CheckBox();
            //Log.logger.info("getting label");
            String labelText = Global.getNaturallyWritenString(pair.getKey());
            label.setText(labelText);
            GUIUtils.setToIdealTextSize(label);
            //Log.logger.info("defining field");
            if(pair.getKey().equals("pValue") || pair.getKey().equals("fdr")){
                //Log.logger.info("defining pvalue field");
                JComboBox comboBox = new JComboBox();
                comboBox.addItem("0.01");
                comboBox.addItem("0.05");
                comboBox.addItem("not-use");
                field = comboBox;
            }else if(pair.getValue() == Integer.class || pair.getValue() == Float.class){
                //Log.logger.info("defining numeric field");
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
                //Log.logger.info("defining geneid field");
                JComboBox comboBox = new JComboBox();
                for(String idType : GeneIdType.defaultIDTypes){
                    comboBox.addItem(new GeneIdType(idType));
                }
                field = comboBox;
            }else if(pair.getValue() == Organism.class){
                //Log.logger.info("defining organism field");
                JComboBox comboBox = new JComboBox();
                for(String idType : Organism.defaultDBs){
                    comboBox.addItem(new Organism(idType));
                }
                field = comboBox;
            }else if(pair.getValue() == ConsensusThreshold.class){
                //Log.logger.info("defining consensus field");
                JComboBox comboBox = new JComboBox();
                LinkedList<String> list = peridot.ConsensusThreshold.getDefaultValues();
                Iterator<String> iter = list.descendingIterator();
                while(iter.hasNext()){
                    comboBox.addItem(new ConsensusThreshold(iter.next()));
                }
                field = comboBox;
            }else{
                //Log.logger.info("defining unknown field");
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

    public void updateConsensusThreshold(int n){
        JComboBox field = (JComboBox)paramFields.get("minimumPackagesForConsensus");
        for (int i = 0; i < field.getItemCount(); i++){
            ConsensusThreshold c = (ConsensusThreshold)field.getItemAt(i);
            if(c.toString().equals(new ConsensusThreshold(n).toString())){
                field.setSelectedIndex(i);
                //Log.logger.info("selecting index " + i);
                break;
            }
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
}
