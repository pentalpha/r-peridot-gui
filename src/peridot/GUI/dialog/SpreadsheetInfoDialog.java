/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dialog;

import peridot.Archiver.Spreadsheet;
import peridot.GUI.JTableUtils;
import peridot.GUI.MainGUI;
import peridot.GUI.WrapLayout;
import peridot.GUI.component.*;
import peridot.GUI.component.Panel;
import peridot.Log;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
/**
 *
 * @author pitagoras
 */
public class SpreadsheetInfoDialog extends JDialog{
    public Spreadsheet table;
    public File file;
    public boolean cancel = false;

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

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cancel = true;
                setVisible(false);
            }
        });
    }

    public void loadTableInScroller(){
        dataTable = JTableUtils.getTableWithoutHeader(table.getRows(100),
                false, 9,100, table.getSeparator());
        scroller = new JScrollPane(dataTable);
        scroller.getViewport().setBackground(Color.white);
    }

    public void initTableScroller(){
        Log.logger.info("Loading table scroller for " + table.tableFile.getName());
        loadTableInScroller();
        scroller.setPreferredSize(new Dimension(getPreferredSize().width-50, 200));
        Log.logger.info("Loaded table scroller for " + table.tableFile.getName());
    }
    
    public void initOkButton(){
        okButton = new BigButton();
        okButton.setText("OK");
        okButton.addActionListener((java.awt.event.ActionEvent evt) -> 
            {
                table.getInfo().setHeaderOnFirstLine(this.headerOnFirstLine.isSelected());
                table.getInfo().setLabelsOnFirstCol(this.labelsOnFirstColumn.isSelected());
                //info.firstCellPresent = (this.firstCellPresent.isSelected());
                
                setVisible(false);
            }
        );
    }
    
    public void initCheckBoxes(){
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
        
        /*this.firstCellPresent = new CheckBox("First cell not empty");
        if(info.firstCellPresent){
            this.firstCellPresent.doClick();
        }*/
    }
    
    private void initUpperPanel(){
        upperPanel = new Panel();
        upperPanel.setLayout(new WrapLayout());
        this.initTableScroller();
        upperPanel.add(titleLabel);
        upperPanel.add(scroller);
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
    
    private CheckBox headerOnFirstLine;
    private CheckBox labelsOnFirstColumn;
    //private CheckBox firstCellPresent;
    
    private Table dataTable;
    private JScrollPane scroller;
    private BiggerLabel titleLabel;
    private BigButton okButton;
    
    private Panel upperPanel;
    private Panel middlePanel;
    private Panel bottomPanel;
}
