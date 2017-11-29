package peridot.GUI.dragAndDrop;

import peridot.IndexedString;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
 * Created by pentalpha on 29/11/2017.
 */
public class IndexedStringListTransferable implements Transferable {
    public static final DataFlavor LIST_ITEMS_DATA_FLAVOR = new DataFlavor(List.class, "java/List");
    private List<IndexedString> items;

    public IndexedStringListTransferable(List<IndexedString> items) {
        this.items = items;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{LIST_ITEMS_DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(LIST_ITEMS_DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return items;
    }
}
