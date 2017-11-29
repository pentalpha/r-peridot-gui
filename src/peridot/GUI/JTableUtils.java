/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import static peridot.Archiver.Spreadsheet.fileIsCSVorTSV;
import static peridot.Archiver.Spreadsheet.getDefaultHeader;
import static peridot.Archiver.Spreadsheet.getRowsFromCSV;
import static peridot.Archiver.Spreadsheet.getRowsFromTSV;
import static peridot.Archiver.Spreadsheet.lineIsSampleNames;
import peridot.GUI.component.Table;
import peridot.Log;

/**
 *
 * @author pentalpha
 */
public class JTableUtils {
    public JTableUtils(){
        
    }

    /**
     * Generates a JTable based on a table file
     * @param tableFile Spreadsheet table file
     * @param columnNamesOnFirstLine    True if you are sure that the first line of the file is the names
     *                                 of the columns (header)
     * @return  JTable object. If the table is empty, returns NULL.
     */
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
        if(allRows.size() > 0){
            data = new Object[allRows.size()-1][];
            headers = new Object[allRows.get(allRows.size()-1).length];
            String[] firstRow = allRows.get(0);
            boolean lessItensOnFirstRow = headers.length == firstRow.length + 1;
            boolean firstRowIsHeader = lineIsSampleNames(firstRow);
            if(columnNamesOnFirstLine || lessItensOnFirstRow || firstRowIsHeader){
                if(allRows.size() == 1){
                    return null;
                }
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
        }else{
            return null;
        }

    }

    private static String[] subStrArray(String[] array, int maxElements){
        boolean equal = (maxElements == array.length);
        String[] newArray = Arrays.copyOfRange(array, 0, maxElements);
        if(!equal){
            newArray[newArray.length-1] = "...";
        }
        return newArray;
    }

    private static List<String[]> cutTableMatrixCols(List<String[]> allRows, int maxCols){
        for(int i = 0; i < allRows.size(); i++){
            if(allRows.get(i).length > maxCols){
                allRows.set(i, subStrArray(allRows.get(i), maxCols));
            }
        }
        return allRows;
    }

    private static List<String[]> cutTableMatrixRows(List<String[]> allRows, int maxRows){
        if(allRows.size() > maxRows){
            while(allRows.size() > maxRows){
                allRows.remove(allRows.size()-1);
            }
            allRows.remove(allRows.size()-1);
            String[] lastRow = new String[allRows.get(0).length];
            for(int i = 0; i < allRows.get(0).length; i++){
                lastRow[i] = "...";
            }
            allRows.add(lastRow);
        }
        return allRows;
    }

    public static Table getTableWithoutHeader(File tableFile, boolean defaultHeader, int maxCols, int maxLines){
        Table table = null;
        List<String[]> allRows;
        if(tableFile.getName().contains(".csv")){
            allRows = getRowsFromCSV(tableFile);
            Log.logger.fine("Loading a CSV file with " + (allRows.get(0).length) + " columns.");
        }else if (tableFile.getName().contains(".tsv")){
            allRows = getRowsFromTSV(tableFile);
        }else{
            allRows = null;
        }

        if(allRows != null){
            if(allRows.size() > 0){
                if(allRows.get(0).length > 0){
                    if(maxCols > 0){
                        allRows = cutTableMatrixCols(allRows, maxCols);
                    }
                    if(maxLines > 0){
                        allRows = cutTableMatrixRows(allRows, maxLines);
                    }
                }
            }
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
