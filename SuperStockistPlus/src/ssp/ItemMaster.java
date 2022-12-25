package ssp;

import conn.dBConnection;
import dto.UserProfile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import query.Query;
import utilities.Settings;

public class ItemMaster extends javax.swing.JInternalFrame {

    private JDesktopPane jDesktopPane1;
    private boolean isFromOtherWindow;
    private UserProfile up;
    private Settings settings=new Settings();
    private DecimalFormat df3 = new DecimalFormat("###.###");
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    
    private String newHSN;
    private String newCompnm;
    private String newMunm;
    private String newItemnm;
    private String itemidArray[];
    private String currentItemid;
    private String compidArray[];
    private String muidArray[];
    private String icidArrayAdd[];
    private String currentIcidAdd;
    private String icidArrayAlter[];
    private String currentIcidAlter;
    
    public ItemMaster(JDesktopPane jDesktopPane1, boolean isFromOtherWindow, UserProfile up) {
        super("Item Master",false,true,false,true);
        initComponents();
        this.isFromOtherWindow = isFromOtherWindow;
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/item.png")));
        
        this.getActionMap().put("test", new AbstractAction(){     //ESCAPE
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Escape Pressed");
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        map.put(stroke,"test");
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)jTable2.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)jTable3.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox3.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox4.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField7);
        settings.numvalidatorFloat(jTextField8);

        Fetch();
        populateCombo1n2();
        populateCombo3n4();

        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                {
                    newHSN = null;
                    newCompnm = null;
                    newMunm = null;
                    currentItemid=null;
                    Fetch();
                    jTextField5.setText("");
                    jComboBox4.setSelectedIndex(0);
                    jTextField6.setText("");
                    clearTable(jTable3);
                    jComboBox2.setSelectedIndex(0);
                    jCheckBox1.setSelected(false);
                }
            }
        });
        
        jTabbedPane1.setMnemonicAt(0, KeyEvent.VK_A);
        jTabbedPane1.setMnemonicAt(1, KeyEvent.VK_L);
        jTabbedPane1.setMnemonicAt(2, KeyEvent.VK_D);
   
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jTextField3.requestFocusInWindow();
                }
            }
        );
    }
    
    public String getNewItemnm()
    {
        return newItemnm;
    }
    
    private void populateCombo1n2() // Measuring Unit
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in MeasuringUnit: 3
        /* muid, munm, isactive */
        String query="select muid, munm from MeasuringUnit where isactive=1 order by munm asc";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            int total = 0;
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            jComboBox1.removeAllItems();
            jComboBox2.removeAllItems();
            if(total != 0)
            {
                muidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    muidArray[i++]=rs.getString("muid");
                    String ghnm=rs.getString("munm");
                    jComboBox1.addItem(ghnm);
                    jComboBox2.addItem(ghnm);
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }
    
    private void populateCombo3n4() // Company
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyMaster: 5
        /* compid, compnm, compcontact, compmail, isactive */
        String query="select compid, compnm from CompanyMaster where isactive=1 order by compnm asc";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            int total = 0;
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            jComboBox3.removeAllItems();
            jComboBox4.removeAllItems();
            if(total != 0)
            {
                compidArray=new String[total];
                jComboBox3.addItem("-- Select --");
                jComboBox4.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    compidArray[i++]=rs.getString("compid");
                    String compunm=rs.getString("compnm");
                    jComboBox3.addItem(compunm);
                    jComboBox4.addItem(compunm);
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e) {}
        }
    }

    private void clearTable(JTable table)
    {
        for(int i=table.getRowCount()-1; i>=0; i--)
        {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch()
    {
        String a="", b="", c="";
        String itemnm=jTextField1.getText().trim().toUpperCase();
        if(itemnm.length()!=0)
        {
            a=" and itemnm like '%"+itemnm+"%'";
        }
        String hsn=jTextField2.getText().trim().toUpperCase();
        if(hsn.length()!=0)
        {
            b=" and hsn like '"+hsn+"%'";
        }
        
        if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ADD"))
        {
            itemnm=jTextField3.getText().trim().toUpperCase().replaceAll("'", "\\'");
            if(itemnm.length() != 0)
            {
                 c=" and itemnm like '%"+itemnm+"%'";
            }
        }
        
        // No. Of Columns: 7
        /* SLN., DESCRIPTION, COMPANY, HSN, U O M, RE-ORD-QTY, IS ACTIVE? */
        int slno1=0;
        clearTable(jTable1);
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in CompanyMaster: 5
        /* compid, compnm, compcontact, compmail, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        // Number of columns in MeasuringUnit: 3
        /* muid, munm, isactive */
        String query="select itemid, itemnm, compnm, hsn, munm, reordqty, isactive from ItemMaster, "
                + "(select compid, compnm from CompanyMaster) x, (select icid, hsn from ItemCategory) y,"
                + " (select muid, munm from MeasuringUnit) z where ItemMaster.compid=x.compid"
                + " and ItemMaster.icid=y.icid and ItemMaster.muid=z.muid"+a+b+c+" order by compnm, itemnm";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0)
            {
                itemidArray=new String[total];
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    itemidArray[i++]=rs.getString("itemid");
                    /*
                    itemid, itemnm, compnm, hsn, munm, reordqty, isactive
                    */
                    row.addElement(rs.getString("itemnm").replaceAll("\\'", "'"));
                    row.addElement(rs.getString("compnm").replace("\\'", "'"));
                    row.addElement(rs.getString("hsn"));
                    row.addElement(rs.getString("munm"));
                    row.addElement(format.format(Double.parseDouble(rs.getString("reordqty"))));
                    String isactive=rs.getString("isactive");
                    row.addElement(isactive.equals("0")?"In-Active":"Active");
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // No. Of Columns: 7
        /* SLN., DESCRIPTION, COMPANY, HSN, U O M, RE-ORD-QTY, IS ACTIVE? */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// DESCRIPTION
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(160);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// COMPANY
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// HSN
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// U O M
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// RE-ORD-QTY
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// IS ACTIVE?
	jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        // Align Funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("U O M").setCellRenderer( centerRenderer );
        jTable1.getColumn("IS ACTIVE?").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("RE-ORD-QTY").setCellRenderer( centerRenderer );
    }
    
    private void FetchAdd()
    {
        String a="";
        String hsn=jTextField4.getText().trim().toUpperCase();
        if(hsn.length()!=0)
        {
            a=" and hsn like '"+hsn+"%'";
        } else {
            clearTable(jTable2);
            return;
        }
        
        // NO. OF COLUMNS: 2
        // HSN CODE, HSN DESC.
        clearTable(jTable2);
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        String query="select icid, hsn, icnm from ItemCategory where isactive=1"
                + a+" order by hsn";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0)
            {
                icidArrayAdd=new String[total];
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    icidArrayAdd[i++]=rs.getString("icid");
                    row.addElement(rs.getString("hsn"));
                    row.addElement(rs.getString("icnm"));
                    ((DefaultTableModel)jTable2.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }

        jTable2.setDragEnabled(false);
        // Disable auto resizing
        jTable2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable2.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // NO. OF COLUMNS: 2
        // HSN CODE, HSN DESC.
        jTable2.getColumnModel().getColumn(0).setMinWidth(0);// HSN CODE
	jTable2.getColumnModel().getColumn(0).setPreferredWidth(150);
	jTable2.getColumnModel().getColumn(1).setMinWidth(0);// HSN DESC.
	jTable2.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable2.getColumn("HSN CODE").setCellRenderer( centerRenderer );
    }
    
    private void FetchAlter()
    {
        String a="";
        String hsn=jTextField6.getText().trim().toUpperCase();
        if(hsn.length()!=0)
        {
            a=" and hsn like '"+hsn+"%'";
        } else {
            clearTable(jTable3);
            return;
        }
        
        // NO. OF COLUMNS: 2
        // HSN CODE, HSN DESC.
        clearTable(jTable3);
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        String query="select icid, hsn, icnm from ItemCategory where isactive=1"
                + a+" order by hsn";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0)
            {
                icidArrayAlter=new String[total];
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    icidArrayAlter[i++]=rs.getString("icid");
                    row.addElement(rs.getString("hsn"));
                    row.addElement(rs.getString("icnm"));
                    ((DefaultTableModel)jTable3.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }

        jTable3.setDragEnabled(false);
        // Disable auto resizing
        jTable3.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable3.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // NO. OF COLUMNS: 2
        // HSN CODE, HSN DESC.
        jTable3.getColumnModel().getColumn(0).setMinWidth(0);// HSN CODE
	jTable3.getColumnModel().getColumn(0).setPreferredWidth(150);
	jTable3.getColumnModel().getColumn(1).setMinWidth(0);// HSN DESC.
	jTable3.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable3.getColumn("HSN CODE").setCellRenderer( centerRenderer );
    }
    
    private void insertToDatabase()
    {
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        String itemnm = jTextField3.getText().trim().toUpperCase().replace("'", "\\'");
        if( itemnm.length() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Enter Name Of The Item!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField3.requestFocusInWindow();
            return;
        }
        if ( jComboBox3.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null,"Company is mandatory!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField3.requestFocusInWindow();
            return;
        }
        String compid = compidArray[jComboBox3.getSelectedIndex()-1];
        if ( currentIcidAdd == null ) {
            JOptionPane.showMessageDialog(null,"HSN is mandatory!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocusInWindow();
            return;
        }
        String icid = currentIcidAdd;
        if ( jComboBox1.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null,"Measuring Unit is mandatory!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String muid = muidArray[jComboBox1.getSelectedIndex()-1];
        String reordqty = jTextField7.getText().trim();
        String isactive = "1";
        
        // Duplicate Testing
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        String query="select * from ItemMaster where itemnm='"+itemnm+"' and compid="+compid;
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            int total = 0;
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0)
            {
                JOptionPane.showMessageDialog(null,"Duplicate Entry! Action Denied.","Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField3.selectAll();
                jTextField3.requestFocusInWindow();
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        int itemid=q.getMaxId("ItemMaster", "itemid");
        itemid++;
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        query="insert into ItemMaster (itemid, itemnm, compid, icid, muid, reordqty, isactive) values"
                + " ("+itemid+", '"+itemnm+"', "+compid+", "+icid+", "+muid+", "+reordqty+", "+isactive+")";
        conn=db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        // Number of columns in ItemStock: 3
        /* isid, itemid, qtyonhand */
        int isid=q.getMaxId("ItemStock", "isid");
        isid++;
        query="insert into ItemStock (isid, itemid, qtyonhand) values ("+isid+", "+itemid+", 0)";
        conn=db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        } 
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        // Number of columns in PSRateFormula: 6
        /* psrfid, itemid, pformulaid, sformulaid, status, isactive */
        int psrfid=q.getMaxId("PSRateFormula", "psrfid");
        psrfid++;
        query="insert into PSRateFormula (psrfid, itemid, pformulaid, sformulaid, status, "
                + "isactive) values ("+psrfid+", "+itemid+", 0, 0, 0, 1)";
        conn=db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        } 
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        if (isFromOtherWindow)
        {
            newItemnm = itemnm;
            setVisible(true);
            dispose();
        }
        else
        {
            newCompnm = null;
            newHSN = null;
            newMunm = null;
            currentIcidAdd = null;
            jTextField3.setText("");
            jComboBox3.setSelectedIndex(0);
            jTextField4.setText("");
            clearTable(jTable2);
            jComboBox1.setSelectedIndex(0);
            jTextField7.setText("0");
            jTextField3.requestFocusInWindow();
            Fetch();
        }
    }
    
    private void keyReleasedTable1()
    {
        if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
        {
            int row=jTable1.getSelectedRow();
            currentItemid=itemidArray[row];
            jTextField5.setText((String)jTable1.getModel().getValueAt(row, 1));
            jComboBox4.setSelectedItem((String)jTable1.getModel().getValueAt(row, 2));
            jTextField6.setText((String)jTable1.getModel().getValueAt(row, 3));
            FetchAlter();
            if ( jTable2.getRowCount() != 0 ) {
                jTable2.changeSelection(0, 0, false, false);
                jTable2.requestFocusInWindow();
            }
            jComboBox2.setSelectedItem((String)jTable1.getModel().getValueAt(row, 4));
            jTextField8.setText((String)jTable1.getModel().getValueAt(row, 5));
            boolean checkBoxFlag=((String)jTable1.getModel().getValueAt(row, 6)).equals("Active");
            jCheckBox1.setSelected(checkBoxFlag);
        }
    }
    
    private void updateToDatabase()
    {
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        String itemnm = jTextField5.getText().trim().toUpperCase().replace("'", "\\'");
        if( itemnm.length() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Enter Name Of The Item!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField5.requestFocusInWindow();
            return;
        }
        if ( jComboBox4.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null,"Company is mandatory!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocusInWindow();
            return;
        }
        String compid = compidArray[jComboBox4.getSelectedIndex()-1];
        if ( currentIcidAlter == null ) {
            JOptionPane.showMessageDialog(null,"HSN is mandatory!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField6.requestFocusInWindow();
            return;
        }
        String icid = currentIcidAlter;
        if ( jComboBox2.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null,"Measuring Unit is mandatory!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        String muid = muidArray[jComboBox2.getSelectedIndex()-1];
        String reordqty = jTextField8.getText().trim();
        String isactive=jCheckBox1.isSelected()?"1":"0";
        
        // Duplicate Testing
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        String query="select * from ItemMaster where itemnm='"+itemnm+"' and compid="
                + compid+" and itemid<>"+currentItemid;
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            int total = 0;
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0)
            {
                JOptionPane.showMessageDialog(null,"Duplicate Entry! Action Denied.","Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField5.selectAll();
                jTextField5.requestFocusInWindow();
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
                return;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        query="update ItemMaster set itemnm='"+itemnm+"', compid="+compid+", icid="+icid+", muid="
                + muid+", reordqty="+reordqty+", isactive="+isactive+" where itemid="+currentItemid;
        conn = db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
        
        if (isFromOtherWindow)
        {
            newItemnm = itemnm;
            setVisible(true);
            dispose();
        }
        else
        {
            newCompnm = null;
            newHSN = null;
            newMunm = null;
            currentIcidAlter = null;
            jTextField5.setText("");
            jComboBox4.setSelectedIndex(0);
            jTextField6.setText("");
            clearTable(jTable3);
            jComboBox2.setSelectedIndex(0);
            jTextField8.setText("0");
            jCheckBox1.setSelected(false);
            Fetch();
        }
    }
    
    private void deleteFromDatabase()
    {
        if(jTable1.getSelectedRow()!=-1&&jTable1.getSelectedColumn()!=-1) {
            String itemid=itemidArray[jTable1.getSelectedRow()];
            String ObjButtons[] = {"Yes","Cancel"};
            int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Item Details!","Delete Record",
                    JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
            if(PromptResult==0) {
                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();
                String query="delete from ItemMaster where itemid="+itemid;
                try {
                    Statement smt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    smt.executeUpdate(query);
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException e){}
                    Fetch();
                    return;
                }
                
                // Number of columns in ItemStock: 3
                /* isid, itemid, qtyonhand */
                query="delete from ItemStock where itemid="+itemid;
                try {
                    Statement smt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    smt.executeUpdate(query);
                } 
                catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException e){}
                    Fetch();
                    return;
                }

                // Number of columns in PSRateFormula: 6
                /* psrfid, itemid, pformulaid, sformulaid, status, isactive */
                query="delete from PSRateFormula where itemid="+itemid;
                try {
                    Statement smt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    smt.executeUpdate(query);
                } 
                catch(SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"ItemMaster ex?: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                finally {
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException e){}
                    Fetch();
                }
            } else
                JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,"Select a Record and then try to Delete","Error Found",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addAlterCompany()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final CompanyMaster ref=new CompanyMaster(true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newCompnm=ref.getNewCompnm();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            ItemMaster.this.setVisible(true);
                            if(newCompnm!=null)
                            {
                                populateCombo3n4();
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ADD"))
                                {
                                    jComboBox3.setSelectedItem(newCompnm);
                                }
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                                {
                                    jComboBox4.setSelectedItem(newCompnm);
                                }
                            }
                        }
                    });
                    ref.setVisible(true);
                    jDesktopPane1.add(ref);
                    ref.show();
                    ref.setIcon(false);
                    ref.setSelected(true);
                }
                catch(PropertyVetoException e){}
            }
        });
        t.start();
        try
        {
            t.join();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void addAlterMeasuringUnit()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final MeasuringUnitMaster ref=new MeasuringUnitMaster(true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newMunm=ref.getNewMunm();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            ItemMaster.this.setVisible(true);
                            if(newMunm!=null)
                            {
                                populateCombo1n2();
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ADD"))
                                {
                                    jComboBox1.setSelectedItem(newMunm);
                                }
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                                {
                                    jComboBox2.setSelectedItem(newMunm);
                                }
                            }
                        }
                    });
                    ref.setVisible(true);
                    jDesktopPane1.add(ref);
                    ref.show();
                    ref.setIcon(false);
                    ref.setSelected(true);
                }
                catch(PropertyVetoException e){}
            }
        });
        t.start();
        try
        {
            t.join();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }
    
    private void addAlterHSN()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final ItemCategoryMaster ref=new ItemCategoryMaster(true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newHSN=ref.getNewHSN();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            ItemMaster.this.setVisible(true);
                            if(newHSN!=null)
                            {
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ADD"))
                                {
                                    jTextField4.setText(newHSN);
                                    FetchAdd();
                                    jTextField4.requestFocusInWindow();
                                }
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                                {
                                    jTextField6.setText(newHSN);
                                    FetchAlter();
                                    jTextField6.requestFocusInWindow();
                                }
                            }
                        }
                    });
                    ref.setVisible(true);
                    jDesktopPane1.add(ref);
                    ref.show();
                    ref.setIcon(false);
                    ref.setSelected(true);
                }
                catch(PropertyVetoException e){}
            }
        });
        t.start();
        try
        {
            t.join();
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameIconified(evt);
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBackground(new java.awt.Color(226, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "SEARCH", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("NAME");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("HSN");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "DESCRIPTION", "COMPANY", "HSN", "U O M", "RE-ORD-QTY", "IS ACTIVE?"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("DESCRIPTION");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("HSN SEARCH");

        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "HSN CODE DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "HSN CODE", "HSN DESC."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable2KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("MEASURING UNIT");

        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("ADD");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("RE-ORDER QUANTITY");

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField7.setText("0");
        jTextField7.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField7FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField7FocusLost(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("COMPANY");

        jComboBox3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox3KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ADD", jPanel2);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("DESCRIPTION");

        jTextField5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField5FocusGained(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("HSN SEARCH");

        jTextField6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField6FocusGained(evt);
            }
        });
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField6KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "HSN CODE DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "HSN CODE", "HSN DESC."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jTable3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable3KeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("MEASURING UNIT");

        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("IS ACTIVE ?");
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("UPDATE");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton2KeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("RE-ORDER QUANTITY");

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField8.setText("0");
        jTextField8.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField8FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField8FocusLost(evt);
            }
        });
        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField8KeyPressed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("COMPANY");

        jComboBox4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox4KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel11)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jCheckBox1)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(4, 4, 4)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(4, 4, 4)
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(6, 6, 6)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ALTER", jPanel3);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("SELECT A RECORD FROM THE TABLE, THEN PROCEED TO DELETE ...");

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("DELETE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jButton3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton3KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 469, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DELETE", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable2.getRowCount() != 0 )
            {
                jTable2.changeSelection(0, 0, false, false);
                jTable2.requestFocusInWindow();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterHSN();
        }
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        FetchAdd();
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jTable2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            int row=jTable2.getSelectedRow();
            currentIcidAdd=icidArrayAdd[row];
            jComboBox1.requestFocusInWindow();
            evt.consume();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterHSN();
        }
    }//GEN-LAST:event_jTable2KeyPressed

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable3.getRowCount() != 0 )
            {
                jTable3.changeSelection(0, 0, false, false);
                jTable3.requestFocusInWindow();
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterHSN();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        FetchAlter();
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jTable3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            int row=jTable3.getSelectedRow();
            currentIcidAlter=icidArrayAlter[row];
            jComboBox2.requestFocusInWindow();
            evt.consume();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterHSN();
        }
    }//GEN-LAST:event_jTable3KeyPressed

    private void jComboBox3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterCompany();
        }
    }//GEN-LAST:event_jComboBox3KeyPressed

    private void jComboBox4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField6.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterCompany();
        }
    }//GEN-LAST:event_jComboBox4KeyPressed

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jComboBox3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jComboBox4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField8.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterMeasuringUnit();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField7.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterMeasuringUnit();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jTextField7FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusGained
        String s=jTextField7.getText().trim();
        if(Double.parseDouble(s)==0.0) {
            jTextField7.setText("");
        }
    }//GEN-LAST:event_jTextField7FocusGained

    private void jTextField7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusLost
        String s=jTextField7.getText().trim();
        if(s.length()==0) {
            jTextField7.setText("0");
        }
    }//GEN-LAST:event_jTextField7FocusLost

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField8FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField8FocusGained
        String s=jTextField8.getText().trim();
        if(Double.parseDouble(s)==0.0) {
            jTextField8.setText("");
        } else {
            jTextField8.selectAll();
        }
    }//GEN-LAST:event_jTextField8FocusGained

    private void jTextField8FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField8FocusLost
        String s=jTextField8.getText().trim();
        if(s.length()==0) {
            jTextField8.setText("0");
        }
    }//GEN-LAST:event_jTextField8FocusLost

    private void jTextField8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jCheckBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField8KeyPressed

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        updateToDatabase();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            updateToDatabase();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        deleteFromDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            deleteFromDatabase();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
            {
                jTextField5.requestFocusInWindow();
                evt.consume();
            }
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("DELETE"))
            {
                jButton3.requestFocusInWindow();
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        keyReleasedTable1();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2)
        {
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
            {
                jTextField5.requestFocusInWindow();
                evt.consume();
            }
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("DELETE"))
            {
                jButton3.requestFocusInWindow();
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        keyReleasedTable1();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        if (evt.getClickCount() == 2)
        {
            int row=jTable2.getSelectedRow();
            currentIcidAdd=icidArrayAdd[row];
            jComboBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        if (evt.getClickCount() == 2)
        {
            int row=jTable3.getSelectedRow();
            currentIcidAlter=icidArrayAlter[row];
            jComboBox2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jTextField5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusGained
        jTextField5.selectAll();
    }//GEN-LAST:event_jTextField5FocusGained

    private void jTextField6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusGained
        jTextField6.selectAll();
    }//GEN-LAST:event_jTextField6FocusGained

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable1.getRowCount() != 0 )
            {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
            else
                jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField3KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
