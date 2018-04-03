/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.Archiver.Spreadsheet;
import peridot.CLI.Commands.RUN;
import peridot.GUI.JTableUtils;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.*;
import peridot.GUI.component.Label;
import peridot.GUI.component.Panel;
import peridot.Log;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author pitagoras
 */
public class SpreadsheetInfoDialog extends JDialog{
    public Spreadsheet table;
    public File file;
    public String separator;
    public boolean cancel = false;
    private boolean loadingInfo = true;

    public SpreadsheetInfoDialog(java.awt.Frame parent, File file){
        super(parent, true);
        Log.logger.info("Starting Spreadsheet Info Dialog");
        try {
            this.table = new Spreadsheet(file);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        this.file = file;
        initComponents();
        loadingInfo = false;
    }
    
    public void initComponents(){
        initBasicComponents();
        initUpperPanel();
        initMiddlePanel();
        initBottomPanel();

        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.PAGE_END);

        revalidate();
        repaint();
    }
    
    public void initBasicComponents(){
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(650, 400));
        setMinimumSize(this.getPreferredSize());
        getContentPane().setLayout(new BorderLayout(30, 5));
        this.setResizable(false);
        setLocationRelativeTo(null);
        String question = "Give us some info on this data: ";
        this.setTitle(question);
        titleLabel = new peridot.GUI.component.BiggerLabel(question);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cancel = true;
                setVisible(false);
            }
        });
    }

    public void loadTableInScroller(){
        //Log.logger.info("Reloading preview table...");
        java.util.List<String[]> rows = table.getRows(100);
        //Log.logger.info(rows.size() + " lines");
        //for(String[] line : rows){
        //    Log.logger.info(line.length + " cols");
        //}
        dataTable = JTableUtils.getTableWithoutHeader(rows,
                false, 9,100);
        scroller = new JScrollPane(dataTable);
        scroller.getViewport().setBackground(Color.white);
        scroller.setPreferredSize(new Dimension(getPreferredSize().width-50, 200));
        //Log.logger.info("... reloaded preview table.");
    }
    
    public void initOkButton(){
        okButton = new BigButton();
        okButton.setText("OK");
        okButton.addActionListener((java.awt.event.ActionEvent evt) -> 
            {
                table.getInfo().setHeaderOnFirstLine(this.headerOnFirstLine.isSelected());
                table.getInfo().setLabelsOnFirstCol(this.labelsOnFirstColumn.isSelected());
                table.setSeparator(this.separator);
                //info.firstCellPresent = (this.firstCellPresent.isSelected());
                
                setVisible(false);
            }
        );
    }
    
    public void initCheckBoxes(){
        checkBoxesPanel = new Panel();
        checkBoxesPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        this.headerOnFirstLine = new CheckBox("Header on the first row");
        this.headerOnFirstLine.setSelected(false);
        if(table.getInfo().getHeaderOnFirstLine()){
            this.headerOnFirstLine.doClick();
        }
        //headerOnFirstLine.setPreferredSize(new Dimension(300, 50));
        //headerOnFirstLine.set
        this.labelsOnFirstColumn = new CheckBox("Labels on first column");
        if(table.getInfo().getLabelsOnFirstCol()){
            this.labelsOnFirstColumn.doClick();
        }

        checkBoxesPanel.add(headerOnFirstLine);
        checkBoxesPanel.add(labelsOnFirstColumn);

        checkBoxesPanel.setPreferredSize(new Dimension(550, 30));
        /*this.firstCellPresent = new CheckBox("First cell not empty");
        if(info.firstCellPresent){
            this.firstCellPresent.doClick();
        }*/
    }

    private void sepOptionAction(){
        if(!loadingInfo){
            getSeparatorFromGUI();
            initUpperPanel();
        }
    }

    public void initSepOptions(){
        String sepFromInfo = table.getSeparator();
        this.separator = sepFromInfo;

        comma = new RadioButton("Comma ','");
        semicolon = new RadioButton("Semicolon ';'");
        space = new RadioButton("Space '   '");
        tab = new RadioButton("Tab '\\t'");
        custom = new RadioButton("Other: ");

        comma.addActionListener((evt) -> {
            sepOptionAction();
        });
        semicolon.addActionListener((evt) -> {
            sepOptionAction();
        });
        space.addActionListener((evt) -> {
            sepOptionAction();
        });
        tab.addActionListener((evt) -> {
            sepOptionAction();
        });
        custom.addActionListener((evt) -> {
            sepOptionAction();
        });
        sepOptions = new ButtonGroup();
        sepOptions.add(comma);
        sepOptions.add(semicolon);
        sepOptions.add(space);
        sepOptions.add(tab);
        sepOptions.add(custom);

        customSep = new JTextField(4);

        sepOptionsPanel = new Panel();
        sepOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
        sepOptionsPanel.add(tab);
        sepOptionsPanel.add(space);
        sepOptionsPanel.add(comma);
        sepOptionsPanel.add(semicolon);
        sepOptionsPanel.add(custom);
        sepOptionsPanel.add(customSep);
        sepOptionsPanel.setPreferredSize(new Dimension(550, 30));

        if(sepFromInfo.equals("\t")){
            tab.doClick();
        }else if(sepFromInfo.equals(" ")){
            space.doClick();
        }else if(sepFromInfo.equals(",")){
            comma.doClick();
        }else if(sepFromInfo.equals(";")){
            semicolon.doClick();
        }else{
            custom.doClick();
            customSep.setText(sepFromInfo);
        }
    }
    
    private void initUpperPanel(){
        if(upperPanel != null) {
            remove(upperPanel);
        }
        upperPanel = new Panel();
        upperPanel.setLayout(new WrapLayout(FlowLayout.CENTER, 0, 4));
        loadTableInScroller();
        upperPanel.add(titleLabel);
        upperPanel.add(scroller);

        add(upperPanel, BorderLayout.PAGE_START);
        revalidate();
        repaint();
    }
    
    private void initMiddlePanel(){
        middlePanel = new Panel();
        middlePanel.setLayout(new WrapLayout(FlowLayout.CENTER, 0, 5));
        //middlePanel.setMaximumSize(new Dimension(200, 400));
        this.initCheckBoxes();
        this.initSepOptions();
        middlePanel.add(checkBoxesPanel);
        middlePanel.add(new Label("Column Separator: "));
        middlePanel.add(sepOptionsPanel);
    }
    
    private void initBottomPanel(){
        bottomPanel = new Panel();
        bottomPanel.setLayout(new WrapLayout());
        initOkButton();
        bottomPanel.add(okButton, BorderLayout.PAGE_END);
    }

    private void getSeparatorFromGUI(){
        String sep;
        if(comma.isSelected()){
            sep = ",";
        }else if(tab.isSelected()){
            sep = "\t";
        }else if(semicolon.isSelected()){
            sep = ";";
        }else if(space.isSelected()){
            sep = " ";
        }else{
            sep = customSep.getText();
        }

        this.separator = sep;
        table.setSeparator(this.separator);
        Log.logger.info("Separator is '" + sep + "'");
    }
    
    private CheckBox headerOnFirstLine;
    private CheckBox labelsOnFirstColumn;
    private RadioButton tab, space, comma, semicolon, custom;
    private JTextField customSep;
    private ButtonGroup sepOptions;
    //private CheckBox firstCellPresent;
    
    private Table dataTable;
    private JScrollPane scroller;
    private BiggerLabel titleLabel;
    private BigButton okButton;
    
    private Panel upperPanel;
    private Panel middlePanel, checkBoxesPanel, sepOptionsPanel;
    private Panel bottomPanel;
}
