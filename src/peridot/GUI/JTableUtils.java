/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import java.io.File;
import java.util.List;
import javax.swing.JTable;
import static peridot.Archiver.Spreadsheet.fileIsCSVorTSV;
import static peridot.Archiver.Spreadsheet.getDefaultHeader;
import static peridot.Archiver.Spreadsheet.getRowsFromCSV;
import static peridot.Archiver.Spreadsheet.getRowsFromTSV;
import static peridot.Archiver.Spreadsheet.lineIsSampleNames;
import peridot.GUI.component.Table;

/**
 *
 * @author pentalpha
 */
public class JTableUtils {
    public JTableUtils(){
        
    }
    public static JTable getTable(File tableFile, boolean columnNamesOnFirstLine){
        JTable table = null;
        List<String[]> allRows;
        if(tableFile.getName().contains(".csv")){
            allRows = getRowsFromCSV(tableFile);
        }else if (tableFile.getName().contains(".tsv")){
            allRows = getRowsFromTSV(tableFile);
        }else{
            allRows = null;
        }

        Object[][] data;
        Object[] headers;
        data = new Object[allRows.size()-1][];
        headers = new Object[allRows.get(allRows.size()-1).length];
        String[] firstRow = allRows.get(0);
        boolean lessItensOnFirstRow = headers.length == firstRow.length + 1;
        boolean firstRowIsHeader = lineIsSampleNames(firstRow);
        if(columnNamesOnFirstLine || lessItensOnFirstRow || firstRowIsHeader){
            if(headers.length == firstRow.length + 1){
                //Log.logger.info("less headers than columns");
                headers[0] = "ID";
                for(int i = 1; i < headers.length; i++){
                    headers[i] = firstRow[i-1];
                }
            }else {
                headers = firstRow;
            }
            for(int i = 0; i < headers.length; i++){
                System.out.print(headers[i] + ", ");
            }
            for(int i = 0; i < data.length; i++){
                data[i] = allRows.get(i+1);
            }
        }else{
            data = new Object[allRows.size()][];
            data = allRows.toArray(data);
            headers = getDefaultHeader(data[0].length);
        }

        table = new Table(data, headers);
        //table.setDefaultRenderer(Object.class, new NoHighlightCellRenderer());
        return table;
    }

    public static Table getTableWithoutHeader(File tableFile, boolean defaultHeader){
        Table table = null;
        List<String[]> allRows;
        if(tableFile.getName().contains(".csv")){
            allRows = getRowsFromCSV(tableFile);
        }else if (tableFile.getName().contains(".tsv")){
            allRows = getRowsFromTSV(tableFile);
        }else{
            allRows = null;
        }

        Object[][] data;
        Object[] headers;
        data = new Object[allRows.size()][];
        data = allRows.toArray(data);
        headers = getDefaultHeader(data[0].length);

        table = new Table(data, headers);
        if(!defaultHeader){
            table.setTableHeader(null);
        }
        return table;
    }
    
    public static javax.swing.filechooser.FileFilter getGeneFileFilter(){
        return new javax.swing.filechooser.FileFilter()
        {
            public boolean accept(File f){
               return fileIsCSVorTSV(f);
            }
            public String getDescription(){
                return "*.csv/.tsv";
            }
        };
    }
}
