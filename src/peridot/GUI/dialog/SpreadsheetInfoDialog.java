/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.GUI.component.Table;
import peridot.GUI.component.BigLabel;
import peridot.GUI.component.BiggerLabel;
import peridot.GUI.component.Panel;
import peridot.GUI.component.BigButton;
import peridot.GUI.component.RadioButton;
import peridot.GUI.component.CheckBox;
import peridot.Archiver.Spreadsheet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import peridot.GUI.JTableUtils;
import peridot.GUI.MainGUI;
import peridot.GUI.WrapLayout;
/**
 *
 * @author pitagoras
 */
public class SpreadsheetInfoDialog extends JDialog{
    public Spreadsheet.Info info;
    public File file;
    public SpreadsheetInfoDialog(java.awt.Frame parent, File file, Spreadsheet.Info info){
        super(parent, true);
        this.info = info;
        this.file = file;
        initComponents();
    }
    
    public void initComponents(){
        initBasicComponents();
        initUpperPanel();
        initMiddlePanel();
        initBottomPanel();
        
        add(upperPanel, BorderLayout.PAGE_START);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.PAGE_END);
    }
    
    public void initBasicComponents(){
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(650, 400));
        setMinimumSize(this.getPreferredSize());
        getContentPane().setLayout(new BorderLayout(30, 10));
        this.setResizable(false);
        setLocationRelativeTo(null);
        String question = "Give us some info on this data: ";
        this.setTitle(question);
        titleLabel = new peridot.GUI.component.BiggerLabel(question);
    }
    
    public void initTableScroller(){
        dataTable = JTableUtils.getTableWithoutHeader(file, false);
        scroller = new JScrollPane(dataTable);
        scroller.getViewport().setBackground(Color.white);
        scroller.setPreferredSize(new Dimension(getPreferredSize().width-50, 200));
    }
    
    public void initDataTypeQuestion(){
        dataTypeLabel = new BigLabel("Only Integers or Rational numbers included?");
        this.dataTypeRadios = new ButtonGroup();
        this.onlyIntegersButton = new RadioButton("Only Integers");
        onlyIntegersButton.setActionCommand("Only Integers");
        if(info.dataType == Spreadsheet.DataType.Int){
            onlyIntegersButton.setSelected(true);
        }
        this.rationalsTooButton = new RadioButton("Rationals too");
        rationalsTooButton.setActionCommand("Rationals too");
        if(info.dataType == Spreadsheet.DataType.Float){
            rationalsTooButton.setSelected(true);
        }
        dataTypeRadios.add(rationalsTooButton);
        dataTypeRadios.add(onlyIntegersButton);
    }
    
    public void initOkButton(){
        okButton = new BigButton();
        okButton.setText("OK");
        okButton.addActionListener((java.awt.event.ActionEvent evt) -> 
            {
                if(rationalsTooButton.isSelected()){
                    info.dataType = Spreadsheet.DataType.Float;
                }else{
                    info.dataType = Spreadsheet.DataType.Int;
                }
                info.setHeaderOnFirstLine(this.headerOnFirstLine.isSelected());
                info.setLabelsOnFirstCol(this.labelsOnFirstColumn.isSelected());
                //info.firstCellPresent = (this.firstCellPresent.isSelected());
                
                setVisible(false);
            }
        );
    }
    
    public void initCheckBoxes(){
        this.headerOnFirstLine = new CheckBox("Header on the first row");
        this.headerOnFirstLine.setSelected(false);
        if(info.getHeaderOnFirstLine()){
            this.headerOnFirstLine.doClick();
        }
        //headerOnFirstLine.setPreferredSize(new Dimension(300, 50));
        //headerOnFirstLine.set
        this.labelsOnFirstColumn = new CheckBox("Labels on first column");
        if(info.getLabelsOnFirstCol()){
            this.labelsOnFirstColumn.doClick();
        }
        
        /*this.firstCellPresent = new CheckBox("First cell not empty");
        if(info.firstCellPresent){
            this.firstCellPresent.doClick();
        }*/
    }
    
    private void initUpperPanel(){
        upperPanel = new Panel();
        upperPanel.setLayout(new WrapLayout());
        this.initDataTypeQuestion();
        this.initTableScroller();
        upperPanel.add(titleLabel);
        upperPanel.add(scroller);
        upperPanel.add(dataTypeLabel);
        upperPanel.add(onlyIntegersButton);
        upperPanel.add(rationalsTooButton);
    }
    
    private void initMiddlePanel(){
        middlePanel = new Panel();
        middlePanel.setLayout(new WrapLayout());
        //middlePanel.setMaximumSize(new Dimension(200, 400));
        this.initCheckBoxes();
        middlePanel.add(headerOnFirstLine);
        middlePanel.add(labelsOnFirstColumn);
        //middlePanel.add(firstCellPresent);
    }
    
    private void initBottomPanel(){
        bottomPanel = new Panel();
        bottomPanel.setLayout(new WrapLayout());
        initOkButton();
        bottomPanel.add(okButton, BorderLayout.PAGE_END);
    }
    
    public static Spreadsheet.Info promptUserForSpreadsheetInfo(File tableFile, Spreadsheet.Info info){
        SpreadsheetInfoDialog dialog = new peridot.GUI.dialog.SpreadsheetInfoDialog(MainGUI._instance, 
                tableFile, info);
        dialog.setVisible(true);
        
        return dialog.info;
    }
    
    public static Spreadsheet.Info getInfo(File tableFile) throws IOException{
        Spreadsheet.Info guessedInfo = Spreadsheet.getInfo(tableFile);
        return promptUserForSpreadsheetInfo(tableFile, guessedInfo);
    }
    
    private CheckBox headerOnFirstLine;
    private CheckBox labelsOnFirstColumn;
    //private CheckBox firstCellPresent;
    
    private BigLabel dataTypeLabel;
    private RadioButton onlyIntegersButton;
    private RadioButton rationalsTooButton;
    private ButtonGroup dataTypeRadios;
    
    private Table dataTable;
    private JScrollPane scroller;
    private BiggerLabel titleLabel;
    private BigButton okButton;
    
    private Panel upperPanel;
    private Panel middlePanel;
    private Panel bottomPanel;
}
