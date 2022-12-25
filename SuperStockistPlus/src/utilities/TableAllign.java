package utilities;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableAllign {

    static public void setTableAlignment(JTable table, int from){
        // table content alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.RIGHT );
        int rowNumber = table.getColumnCount();
        for(int i = from; i < rowNumber; i++){
            table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }
    }
}
