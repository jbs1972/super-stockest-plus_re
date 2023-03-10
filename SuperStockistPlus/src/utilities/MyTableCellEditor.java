package utilities;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
    JComponent component = new JTextField();
    public Component getTableCellEditorComponent(JTable table, Object value,boolean isSelected, int rowIndex, int vColIndex)
    {
//        System.out.println("Called");
        ((JTextField)component).setText((String) value);
        return component;
    }
    @Override
    public Object getCellEditorValue()
    {
        return ((JTextField)component).getText();
    }
}
