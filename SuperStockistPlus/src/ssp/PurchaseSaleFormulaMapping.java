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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utilities.GroupButtonUtils;

public class PurchaseSaleFormulaMapping extends javax.swing.JInternalFrame {

    private JDesktopPane jDesktopPane1;
    private boolean isFromOtherWindow;
    private UserProfile up;
    
    private String newPsrfid;
    private String pformulaidArray[];
    private String sformulaidArray[];
    private String psrfidArray[];
    private String newFormulaid;
    
    public PurchaseSaleFormulaMapping(boolean isFromOtherWindow, UserProfile up, JDesktopPane jDesktopPane1) {
        super("Purchase-Sale Formula Mapping",false,true,false,true);
        initComponents();
        this.isFromOtherWindow = isFromOtherWindow;
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/formula_mapping.png")));
        
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
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        Fetch();
        populateCombo1();
        populateCombo2();
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jComboBox1.requestFocusInWindow();
                }
            }
        );
    }
    
    public String getNewPsrfid()
    {
        return newPsrfid;
    }
    
    private void populateCombo1() // Purchase formula
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Formula: 5
	/* formulaid, forsale, onmrp, formula, isactive */
        String query="select formulaid, onmrp, formula from Formula where forsale=0"
                + " and isactive=1 order by onmrp asc";
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
            if(total != 0)
            {
                pformulaidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    pformulaidArray[i++]=rs.getString("formulaid");
                    jComboBox1.addItem((rs.getString("onmrp").equals("1")?"MRP":"Rate")
                            + rs.getString("formula"));
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseSaleFormulaMapping ex?: "+ex.getMessage(),
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
    
    private void populateCombo2() // Sale formula
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Formula: 5
	/* formulaid, forsale, onmrp, formula, isactive */
        String query="select formulaid, onmrp, formula from Formula where forsale=1"
                + " and isactive=1 order by onmrp asc";
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
            jComboBox2.removeAllItems();
            if(total != 0)
            {
                sformulaidArray=new String[total];
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    sformulaidArray[i++]=rs.getString("formulaid");
                    jComboBox2.addItem((rs.getString("onmrp").equals("1")?"MRP":"Rate")
                            + rs.getString("formula"));
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseSaleFormulaMapping ex?: "+ex.getMessage(),
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
        // No. Of Columns: 7
        /* SLN., SELECT, COMPANY, IETM, HSN, PURCHASE FORMULA, SALE FORMULA */
        String x="";
        String rButtonText = GroupButtonUtils.getSelectedButtonText(buttonGroup1);
        try {
            // 0 - No formula, 1 - Both formula, 2 - Only purchase formula, 3 - Only sale formula
            if ( !rButtonText.equals("ALL ITEMS") ) {
                if ( rButtonText.equals("WITHOUT FORMULA") ) {
                    x=" and status=0";
                } else if( rButtonText.equals("WITH FORMULA") ) {
                    x=" and status=1";
                } else if( rButtonText.equals("WITH PURCHASE FORMULA ONLY") ) {
                    x=" and status=2";
                } else if( rButtonText.equals("WITH SALE FORMULA ONLY") ) {
                    x=" and status=3";
                }
            }
        } catch ( NullPointerException ex ) {
            return;
        }
        String a="", b="", c="", d="", e="";
        String compnm=jTextField1.getText().trim().toUpperCase();
        if(compnm.length()!=0)
        {
            a=" and compnm like '"+compnm+"%'";
        }
        String itemnm=jTextField2.getText().trim().toUpperCase();
        if(itemnm.length()!=0)
        {
            b=" and itemnm like '%"+itemnm+"%'";
        }
        String hsn=jTextField3.getText().trim().toUpperCase();
        if(hsn.length()!=0)
        {
            c=" and hsn like '"+compnm+"%'";
        }
        String pformula=jTextField4.getText().trim().toUpperCase();
        if(pformula.length()!=0)
        {
            d=" and y.formula like '"+pformula+"%' and y.forsale=0";
        }
        String sformula=jTextField5.getText().trim().toUpperCase();
        if(sformula.length()!=0)
        {
            e=" and z.formula like '"+sformula+"%' and z.forsale=1";
        }
        // No. Of Columns: 7
        /* SLN., SELECT, COMPANY, IETM, HSN, PURCHASE FORMULA, SALE FORMULA */
        // Number of columns in Formula: 5
	/* formulaid, forsale, onmrp, formula, isactive */
        // Number of columns in PSRateFormula: 6
	/* psrfid, itemid, pformulaid, sformulaid, status, isactive */
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in CompanyMaster: 6
        /* compid, compnm, compabbr, compcontact, compmail, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        clearTable(jTable1);
        int slno=0,total=0;        
        String query="select psrfid, compnm, itemnm, hsn, y.onmrp as ponmrp, y.formula as pformula, z.onmrp as sonmrp, z.formula as "
                + "sformula from (select itemid, itemnm, icid, compid from ItemMaster where "
                + "isactive=1"+b+") p, (select compid, compnm from CompanyMaster where isactive=1"
                + a+") q, (select icid, hsn from ItemCategory where isactive=1"+c+") r, "
                + "PSRateFormula, Formula y, Formula z where PSRateFormula.isactive=1"
                + " and y.isactive=1 and z.isactive=1"+x+" and p.compid=q.compid and p.icid=r.icid"
                + " and p.itemid=PSRateFormula.itemid and PSRateFormula.pformulaid=y.formulaid"
                + " and PSRateFormula.sformulaid=z.formulaid"+d+e+" order by compnm, itemnm";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=null;
        try
        {
            conn=db.setConnection();
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
                psrfidArray=new String[total];                
                int i=0;
                while(rs.next())
                {
                    Vector<Object> row = new Vector<Object>();
                    row.addElement(++slno+"");//SLN.
                    if(jCheckBox1.isSelected())
                        row.addElement(true);
                    else
                        row.addElement(false);
                    psrfidArray[i++]=rs.getString("psrfid");
                    /* psrfid, compnm, itemnm, hsn, pformula, sformula */
                    row.addElement(rs.getString("compnm").replaceAll("\\'", "'"));
                    row.addElement(rs.getString("itemnm").replaceAll("\\'", "'"));
                    row.addElement(rs.getString("hsn"));
                    String pformula1 = rs.getString("pformula");
                    if ( pformula1.equals("-") ) {
                        row.addElement(pformula1);
                    } else {
                        row.addElement((rs.getString("ponmrp").equals("0")?"Rate":"MRP")+pformula1);
                    }
                    String sformula1 = rs.getString("sformula");
                    if ( sformula1.equals("-") ) {
                        row.addElement(sformula1);
                    } else {
                        row.addElement((rs.getString("sonmrp").equals("0")?"Rate":"MRP")+sformula1);
                    }
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseSaleFormulaMapping ex3: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        // No. Of Columns: 7
        /* SLN., SELECT, COMPANY, IETM, HSN, PURCHASE FORMULA, SALE FORMULA */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// SELECT
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// COMPANY
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// IETM
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(300);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// HSN
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// PURCHASE FORMULA
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// SALE FORMULA
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(150);    

        // Align Funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
    }
    
    private void keyRelease() {
        boolean allSelFlag=true;
        for(int row=0; row<jTable1.getRowCount(); row++)
        {
            if(!Boolean.parseBoolean(jTable1.getValueAt(row, 1).toString()))
            {
                jCheckBox1.setSelected(false);  
                allSelFlag=false;
            }
        }
        if(allSelFlag)
        {
            jCheckBox1.setSelected(true);
        }
    }
    
    private void saveSelected() {
        // 0 - No formula, 1 - Both formula, 2 - Only purchase formula, 3 - Only sale formula
        String status = "0";
        String pformulaid = "0";
        if ( jComboBox1.getSelectedIndex() != 0 ) {
            pformulaid = pformulaidArray[jComboBox1.getSelectedIndex()-1];
            
        }
        String sformulaid = "0";
        if ( jComboBox2.getSelectedIndex() != 0 ) {
            sformulaid = sformulaidArray[jComboBox2.getSelectedIndex()-1];
        }
        if ( jComboBox1.getSelectedIndex() != 0 && jComboBox2.getSelectedIndex() != 0 ) {
            status = "1";
        } 
        else
        {
            if ( jComboBox1.getSelectedIndex() != 0 && jComboBox2.getSelectedIndex() == 0 ) 
            {
                status = "2";
            }
            else
            {
                if ( jComboBox1.getSelectedIndex() == 0 && jComboBox2.getSelectedIndex() != 0 ) 
                {
                    status = "3";
                }
            }
        }
        ArrayList<String> psrfidAl=new ArrayList<String>();
        for(int row=0; row<jTable1.getRowCount(); row++)
        {
            if(Boolean.parseBoolean(jTable1.getValueAt(row, 1).toString()))
            {
                psrfidAl.add(psrfidArray[row]);
            }
        }
        if( psrfidAl.size() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select at least one Record, then try to update"
                    + " the record with formula.","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement pstm = null;
        // Number of columns in PSRateFormula: 6
	/* psrfid, itemid, pformulaid, sformulaid, status, isactive */
        String sql = "update PSRateFormula set pformulaid=?, sformulaid=?, status=? where psrfid=?";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            pstm = conn.prepareStatement(sql);
            for(String psrfid : psrfidAl)
            {
                pstm.setInt(1, Integer.parseInt(pformulaid));
                pstm.setInt(2, Integer.parseInt(sformulaid));
                pstm.setInt(3, Integer.parseInt(status));
                pstm.setInt(4, Integer.parseInt(psrfid));
                pstm.addBatch(); 
            }
            pstm.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseSaleFormulaMapping ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (pstm != null) 
            {
                try {
                    pstm.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        Fetch();
        jButton2.requestFocusInWindow();
    }
    
    private void flushSearch() {
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
        buttonGroup1.clearSelection();
        jRadioButton1.setSelected(true);
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        Fetch();
        jComboBox1.requestFocusInWindow();
    }
    
    private void addAlterFormula(int combono) {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final Formula ref=new Formula(true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newFormulaid=ref.getNewFormulaid();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            PurchaseSaleFormulaMapping.this.setVisible(true);
                            if(newFormulaid!=null)
                            {
                                if (combono==1) {
                                    populateCombo1();
                                    String formula = "";
                                    dBConnection db=new dBConnection();
                                    Connection conn=db.setConnection();
                                    // Number of columns in Formula: 5
                                    /* formulaid, forsale, onmrp, formula, isactive */
                                    String query="select onmrp, formula from Formula where formulaid="+newFormulaid;
                                    try {
                                        Statement stm=conn.createStatement();
                                        ResultSet rs=stm.executeQuery(query);
                                        if ( rs.next() ) {
                                            formula = (rs.getString("onmrp").equals("0")?"Rate":"MRP")+rs.getString("formula");
                                        }
                                    }
                                    catch(SQLException ex)
                                    {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(null,"PurchaseSaleFormulaMapping ex?: "+ex.getMessage(),
                                                "Error Found",JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    finally {
                                        try {
                                            if (conn!=null) conn.close();
                                        } catch(SQLException ex){}
                                    }
                                    jComboBox1.setSelectedItem(formula);
                                } else {
                                    populateCombo2();
                                    String formula = "";
                                    dBConnection db=new dBConnection();
                                    Connection conn=db.setConnection();
                                    // Number of columns in Formula: 5
                                    /* formulaid, forsale, onmrp, formula, isactive */
                                    String query="select onmrp, formula from Formula where formulaid="+newFormulaid;
                                    try {
                                        Statement stm=conn.createStatement();
                                        ResultSet rs=stm.executeQuery(query);
                                        if ( rs.next() ) {
                                            formula = (rs.getString("onmrp").equals("0")?"Rate":"MRP")+rs.getString("formula");
                                        }
                                    }
                                    catch(SQLException ex)
                                    {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(null,"PurchaseSaleFormulaMapping ex?: "+ex.getMessage(),
                                                "Error Found",JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }
                                    finally {
                                        try {
                                            if (conn!=null) conn.close();
                                        } catch(SQLException ex){}
                                    }
                                    jComboBox2.setSelectedItem(formula);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("PURCHASE FORMULA");

        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("SALE FORMULA");

        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(226, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ITEM SEARCH", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("COMPANY");

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

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("ITEM");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("HSN");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("PUR. FORMULA");

        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("SALE FORMULA");

        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("ALL ITEMS");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });
        jRadioButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton1KeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton2.setText("WITHOUT FORMULA");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });
        jRadioButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton2KeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton3.setText("WITH FORMULA");
        jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton3ItemStateChanged(evt);
            }
        });
        jRadioButton3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton3KeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton4.setText("WITH PURCHASE FORMULA ONLY");
        jRadioButton4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton4ItemStateChanged(evt);
            }
        });
        jRadioButton4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton4KeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton5.setText("WITH SALE FORMULA ONLY");
        jRadioButton5.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton5ItemStateChanged(evt);
            }
        });
        jRadioButton5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton5KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jRadioButton1)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton2)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton3)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton4)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "RATE FOMULA MAPPING DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "SELECT", "COMPANY", "IETM", "HSN", "PURCHASE FORMULA", "SALE FORMULA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("SAVE SELECTED");
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

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("SELECT ALL");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("FLUSH SEARCH");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addGap(225, 225, 225)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jCheckBox1)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jComboBox2.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterFormula(1);
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton1.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterFormula(2);
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jRadioButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton1KeyPressed

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton2ItemStateChanged

    private void jRadioButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton2KeyPressed

    private void jRadioButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton3ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton3ItemStateChanged

    private void jRadioButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton3KeyPressed

    private void jRadioButton4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton4ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton4ItemStateChanged

    private void jRadioButton4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton4KeyPressed

    private void jRadioButton5ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton5ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton5ItemStateChanged

    private void jRadioButton5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton5KeyPressed

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
            jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField3KeyReleased

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField4KeyReleased

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable1.getRowCount() != 0 ) {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        keyRelease();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        keyRelease();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        Fetch();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        saveSelected();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            saveSelected();
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        flushSearch();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            flushSearch();
        }
        if (evt.getKeyCode() == KeyEvent.VK_LEFT)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton2KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
