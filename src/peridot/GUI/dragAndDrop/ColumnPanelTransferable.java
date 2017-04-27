/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peridot.GUI.dragAndDrop;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import peridot.GUI.panel.ColumnPanel;

/**
 *
 * @author pentalpha
 */
public class ColumnPanelTransferable  implements Transferable {
    public static final DataFlavor COLUMN_PANEL_DATA_FLAVOR = new DataFlavor(ColumnPanel.class, "java/GUI/ColumnPanel");
    private ColumnPanel columnPanel;

    public ColumnPanelTransferable(ColumnPanel columnPanel) {
        this.columnPanel = columnPanel;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{COLUMN_PANEL_DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(COLUMN_PANEL_DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return columnPanel;
    }
}
