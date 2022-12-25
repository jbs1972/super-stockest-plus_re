package ssp;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.OperatorOperand;
import dto.PurchaseMaster;
import dto.PurchaseSub;
import dto.UserProfile;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import query.Query;
import utilities.Add0Padding2;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;
import utilities.SimplificationSpliter;

public class Purchase extends javax.swing.JInternalFrame implements AWTEventListener {

    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Settings settings=new Settings();
    private DecimalFormat format = new DecimalFormat("0.#");
    private Query q=new Query();
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    
    private String csidArray[];
    private String compidArray[];
    private String compnmArray[];
    private String currentCompid;
    private String itemidArray[];
    private String hsnArray[];
    private String newStockist;
    private String newItemnm;
    private ArrayList<PurchaseSub> psAl = new ArrayList<PurchaseSub>();
    private boolean isFromOtherWindow;
    private String probablyNewItem;
    private String rateformula;
    
    public Purchase(JDesktopPane jDesktopPane1, UserProfile up, boolean isFromOtherWindow) {
        super("Purchase",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.up = up;
        this.isFromOtherWindow = isFromOtherWindow;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/purchase01.png")));
        
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
        
        jDateChooser1.setDate(new Date());
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        String sDate1="01/01/2000";  
        Date date1=null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser2.setDate(date1);
        jDateChooser2.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser2.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        jDateChooser3.setDate(date1);
        jDateChooser3.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser3.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField7);
        settings.numvalidatorFloat(jTextField8);
        settings.numvalidatorFloat(jTextField9);
        settings.numvalidatorFloat(jTextField10);
        settings.numvalidatorFloat(jTextField11);
        settings.numvalidatorFloat(jTextField12);
        settings.numvalidatorFloatWithSign(jTextField13);
        settings.numvalidatorFloat(jTextField14);
        settings.numvalidatorFloat(jTextField16);
        
        populateCombo1();
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jTextField1.requestFocusInWindow();
                }
            }
        );
    }
    
    @Override
    public void eventDispatched(AWTEvent event) 
    {
        if(event instanceof KeyEvent)
        {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED)
            {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10)
                {
                    jComboBox1.requestFocusInWindow();
                } 
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10)
                {
                    jTextField5.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser3.getDateEditor())&&key.getKeyCode()==10)
                {
                    jButton1.requestFocusInWindow();
                }
            }
        }
    }
    
    public String getProbablyNewItem()
    {
        return probablyNewItem;
    }
    
    private void populateCombo1() // Company Stockist
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyStockist: 17
	/* csid, compid, stockist, sstreet, scity, sdist, sstate, sstatecode, spin, 
	scountry, scontact, smail, sgstno, sgstregntype, spanno, saadhaarno, isactive */
        // Number of columns in CompanyMaster: 5
        /* compid, compnm, compcontact, compmail, isactive */
        String query="select csid, stockist, compnm, x.compid as compid from (select csid, compid, stockist from "
                + "CompanyStockist where isactive=1) x, (select compid, compnm from CompanyMaster"
                + " where isactive=1) y where x.compid=y.compid order by stockist asc";
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
                csidArray=new String[total];
                compnmArray=new String[total];
                compidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    csidArray[i]=rs.getString("csid");
                    jComboBox1.addItem(rs.getString("stockist"));
                    compnmArray[i]=rs.getString("compnm");
                    compidArray[i]=rs.getString("compid");
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
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
    
    private void populateCombo2() // Item HSN and 
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        // Number of columns in SuperStockist: 17
        /* ssid, compid, stockist, sstreet, scity, sdist, sstate, sstatecode, spin, scountry, 
        scontact, smail, sgstno, sgstregntype, spanno, saadhaarno, isactive */
        String query="select itemid, hsn, itemnm from ItemMaster, (select icid, hsn from ItemCategory"
                + " where isactive=1) x where ItemMaster.icid=x.icid and compid="+currentCompid+" and"
                + " ItemMaster.isactive=1 order by itemnm";
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
                itemidArray=new String[total];
                hsnArray=new String[total];
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    itemidArray[i]=rs.getString("itemid");
                    jComboBox2.addItem(rs.getString("itemnm"));
                    hsnArray[i]=rs.getString("hsn");
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
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
    
    private void addAlterStockist()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final CompanyStockist ref=new CompanyStockist(jDesktopPane1, true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newStockist=ref.getNewStockist();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Purchase.this.setVisible(true);
                            if(newStockist!=null)
                            {
                                populateCombo1();
                                jComboBox1.setSelectedItem(newStockist);
                                try
                                {
                                    if(((String)jComboBox1.getSelectedItem()).equals("-- Select --"))
                                    {
                                        jLabel4.setText("N/A");
                                    }
                                    else
                                    {
                                        jLabel4.setText(compnmArray[jComboBox1.getSelectedIndex()-1]);
                                    }
                                }
                                catch(NullPointerException ex){}
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
    
    private void addAlterItem()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final ItemMaster ref=new ItemMaster(jDesktopPane1, true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newItemnm=ref.getNewItemnm();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Purchase.this.setVisible(true);
                            if(newItemnm!=null)
                            {
                                populateCombo2();
                                jComboBox2.setSelectedItem(newItemnm);
                                try
                                {
                                    if(((String)jComboBox2.getSelectedItem()).equals("-- Select --"))
                                    {
                                        jLabel13.setText("N/A");
                                    }
                                    else
                                    {
                                        jLabel13.setText(hsnArray[jComboBox2.getSelectedIndex()-1]);
                                    }
                                }
                                catch(NullPointerException ex){}
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
    
    private String getOpeningStockInvoiceNo()
    {
        int total=0;
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String financialcode="";
        String query="select financialcode from FinancialYear where isactive=1";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                financialcode=rs.getString("financialcode");
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        
        // Number of columns in PurchaseMaster: 26
        /* pmid, ssid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        query="select IFNULL(max(invno),'') as x from PurchaseMaster where invno like '"
                    + "OPNSTK/__/"+financialcode+"'";
        System.out.println(query);
        conn = db.setConnection();
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                String lastTotalID=rs.getString("x");
                if(lastTotalID.length()!=0)
                {
                    // Pattern: OPNSTK/01/17-18
                    String lastID=lastTotalID.substring(lastTotalID.indexOf("/")+1,lastTotalID.lastIndexOf("/"));
                    total=Integer.parseInt(lastID);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        total++;
        return "OPNSTK/"+Add0Padding2.add0Padding(total)+"/"+financialcode;
    }
    
    private void billingStart() {
        if ( jTextField1.getText().trim().length() == 0 ) {
            if ( jCheckBox1.isSelected() ) {
                JOptionPane.showMessageDialog(null,"Software error!! Contact with software vendor.",
                    "Software Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                JOptionPane.showMessageDialog(null,"Invoice No. is mandatory!!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField1.requestFocusInWindow();
                return;
            }
        }
        String invno = jTextField1.getText().trim().toUpperCase();
        try {
            currentCompid = compidArray[jComboBox1.getSelectedIndex()-1];
        } catch (NullPointerException ex) {
            return;
        }
        // Testing for duplicate invoice for the same company
        // Number of columns in PurchaseMaster: 27
        /* pmid, ssid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        String query="select pmid from PurchaseMaster where compid="+currentCompid+" and invno='"+invno+"'";
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
                JOptionPane.showMessageDialog(null,"Duplicate Invoice No. for the same company !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField1.selectAll();
                jTextField1.requestFocusInWindow();
                return;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date invDt =jDateChooser1.getDate();
        String invdt=null;
        try
        {
            invdt=sdf.format(invDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Purchase Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        if ( jComboBox1.getSelectedIndex() == 0 ) {
            JOptionPane.showMessageDialog(null,"Stockist mandatory!!!",
                "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        Date ordDt =jDateChooser2.getDate();
        String orddt=null;
        try
        {
            orddt=sdf.format(ordDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Order Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser2.requestFocusInWindow();
            return;
        }
        Date supplyDt =jDateChooser3.getDate();
        String supplydt=null;
        try
        {
            supplydt=sdf.format(supplyDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Supply Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser3.requestFocusInWindow();
            return;
        }
        jCheckBox1.setEnabled(false);
        jTextField1.setEnabled(false);
        jDateChooser1.setEnabled(false);
        jComboBox1.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField3.setEnabled(false);
        jTextField4.setEnabled(false);
        jDateChooser2.setEnabled(false);
        jTextField5.setEnabled(false);
        jTextField6.setEnabled(false);
        jDateChooser3.setEnabled(false);
        jButton1.setEnabled(false);
        jComboBox2.requestFocusInWindow();
    }
    
    private void addToList() {
        // Number of columns in PurchaseSub: 17
        /* psid, pmid, itemid, qty, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
        sgstper, igstper, igstamt, total, qtysold, retqty */
        if ( jComboBox2.getSelectedIndex() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select proper item !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        String itemid = itemidArray[jComboBox2.getSelectedIndex() - 1];
        String mrp = jTextField16.getText().trim();
        String qty=jTextField7.getText().trim();
        if(Double.parseDouble(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField7.requestFocusInWindow();
            return;
        }
        String rate=jTextField8.getText().trim();
        if(Double.parseDouble(rate) == 0)
        {
            JOptionPane.showMessageDialog(null,"Rate is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField8.requestFocusInWindow();
            return;
        }
        String amt = jLabel16.getText().trim().replaceAll(",", "");
        String discper = jTextField9.getText().trim();
        String discamt = jLabel18.getText().trim().replaceAll(",", "");
        String taxableamt = jLabel20.getText().trim().replaceAll(",", "");
        String cgstper = jTextField10.getText().trim();
        String cgstamt = jLabel22.getText().trim().replaceAll(",", "");
        String sgstper = jTextField11.getText().trim();
        String sgstamt = jLabel24.getText().trim().replaceAll(",", "");
        String igstper = jTextField12.getText().trim();
        String igstamt = jLabel26.getText().trim().replaceAll(",", "");
        if ( Double.parseDouble(cgstper) != 0 && Double.parseDouble(sgstper) != 0 
                && Double.parseDouble(igstper) != 0 ) {
            JOptionPane.showMessageDialog(null,"Either CGST+SGST or IGST !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField10.selectAll();
            jTextField10.requestFocusInWindow();
            return;
        } else {
            if ( (Double.parseDouble(cgstper) != 0 && Double.parseDouble(igstper) != 0) || 
                    (Double.parseDouble(sgstper) != 0 && Double.parseDouble(igstper) != 0) ) {
                JOptionPane.showMessageDialog(null,"Combination CGST+SGST or IGST !!!","Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField10.selectAll();
                jTextField10.requestFocusInWindow();
                return;
            } else {
                if ( (Double.parseDouble(cgstper) != 0 && Double.parseDouble(sgstper) == 0) || 
                        (Double.parseDouble(cgstper) == 0 && Double.parseDouble(sgstper) != 0) ) {
                    JOptionPane.showMessageDialog(null,"Combination must be CGST+SGST !!!","Error Found",JOptionPane.ERROR_MESSAGE);
                    jTextField10.selectAll();
                    jTextField10.requestFocusInWindow();
                    return;
                } else {
                    if ( Double.parseDouble(cgstper) != Double.parseDouble(sgstper) ) {
                        JOptionPane.showMessageDialog(null,"CGST must be equals to SGST !!!","Error Found",JOptionPane.ERROR_MESSAGE);
                        jTextField10.selectAll();
                        jTextField10.requestFocusInWindow();
                        return;
                    }
                }
            }
        }
        String total = jLabel27.getText().trim().replaceAll(",", "");
        String qtysold = "0";
        String retqty = "0";
        
        PurchaseSub ps = new PurchaseSub();
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        ps.setPsid(""); // At Insert
        ps.setPmid(""); // At Insert
        ps.setItemid(itemid);
        ps.setQty(qty);
        ps.setMrp(mrp);
        ps.setRate(rate);
        ps.setAmt(amt);
        ps.setDiscper(discper);
        ps.setDiscamt(discamt);
        ps.setTaxableamt(taxableamt);
        ps.setCgstper(cgstper);
        ps.setCgstamt(cgstamt);
        ps.setSgstper(sgstper);
        ps.setSgstamt(sgstamt);
        ps.setIgstper(igstper);
        ps.setIgstamt(igstamt);
        ps.setTotal(total);
        ps.setQtysold(qtysold);
        ps.setRetqty(retqty);
        psAl.add(ps);
        
        Fetch();
        
        jComboBox2.setSelectedIndex(0);
        jLabel13.setText("N/A");
        jTextField16.setText("0");
        jTextField7.setText("0");
        jTextField8.setText("0");
        jLabel16.setText("0");
        jTextField9.setText("0");
        jLabel18.setText("0");
        jLabel20.setText("0");
        jTextField10.setText("0");
        jLabel22.setText("0");
        jTextField11.setText("0");
        jLabel24.setText("0");
        jTextField12.setEnabled(true);
        jTextField12.setText("0");
        jLabel26.setText("0");
        jLabel27.setText("0");
        
        jComboBox2.requestFocusInWindow();
    }
    
    private void computation01()
    {
        double qty = 0.0;
        double rate = 0.0;
        double discper = 0.0;
        double cgstper = 0.0;
        double sgstper = 0.0;
        double igstper = 0.0;
        try
        {
            qty = Double.parseDouble(jTextField7.getText().trim());
            rate = Double.parseDouble(jTextField8.getText().trim());
            discper = Double.parseDouble(jTextField9.getText().trim());
            cgstper = Double.parseDouble(jTextField10.getText().trim());
            sgstper = Double.parseDouble(jTextField11.getText().trim());
            igstper = Double.parseDouble(jTextField12.getText().trim());
        }
        catch (NumberFormatException ex)
        {
            return;
        }
        double amt = qty * rate;
        jLabel16.setText(MyNumberFormat.rupeeFormat(amt));
        double discamt = amt * (discper / 100.0);
        jLabel18.setText(MyNumberFormat.rupeeFormat(discamt));
        double taxableamt = amt - discamt;
        jLabel20.setText(MyNumberFormat.rupeeFormat(taxableamt));
        double cgstamt = taxableamt * (cgstper / 100.0);
        jLabel22.setText(MyNumberFormat.rupeeFormat(cgstamt));
        double sgstamt = taxableamt * (sgstper / 100.0);
        jLabel24.setText(MyNumberFormat.rupeeFormat(sgstamt));
        double igstamt = taxableamt * (igstper / 100.0);
        jLabel26.setText(MyNumberFormat.rupeeFormat(igstamt));
        double total = taxableamt + (cgstamt + sgstamt + igstamt);
        jLabel27.setText(MyNumberFormat.rupeeFormat(total));
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
        double netqty = 0.0;
        double netamt = 0.0;
        double nettaxableamt = 0.0;
        double netcgst = 0.0;
        double netsgst = 0.0;
        double netigst = 0.0;
        double nettotal = 0.0;
        
        int slno1=0;
        clearTable(jTable1);
        
        // Number of columns in PurchaseSub: 18
	/* psid, pmid, itemid, qty, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (PurchaseSub ps :  psAl)
        {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno1+"");
            // No. Of Columns: 16
            /* SLN., ITEM, QTY., MRP, RATE, AMOUNT, DISC.%, DISC. AMT., TAXABLE, CGST%, CGST AMT., 
            SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
            // Number of columns in ItemMaster: 7
            /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
            // Number of columns in MeasuringUnit: 3
            /* muid, munm, isactive */
            // Number of columns in ItemCategory: 4
            /* icid, icnm, hsn, isactive */
            String query="select itemnm, munm, hsn from ItemMaster, MeasuringUnit, ItemCategory where ItemMaster.muid="
                    + "MeasuringUnit.muid and ItemMaster.icid=ItemCategory.icid and itemid="+ps.getItemid();
            System.out.println(query);
            String munm = "";
            try
            {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next())
                {
                    row.addElement(rs.getString("itemnm")+"["+rs.getString("hsn")+"]");
                    munm = rs.getString("munm");
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            netqty += Double.parseDouble(ps.getQty());
            row.addElement(format.format(Double.parseDouble(ps.getQty()))+munm);
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getMrp())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getRate())));
            netamt += Double.parseDouble(ps.getAmt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getAmt())));
            row.addElement(format.format(Double.parseDouble(ps.getDiscper())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getDiscamt())));
            nettaxableamt += Double.parseDouble(ps.getTaxableamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getTaxableamt())));
            row.addElement(format.format(Double.parseDouble(ps.getCgstper())));
            netcgst += Double.parseDouble(ps.getCgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getCgstamt())));
            row.addElement(format.format(Double.parseDouble(ps.getSgstper())));
            netsgst += Double.parseDouble(ps.getSgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getSgstamt())));
            row.addElement(format.format(Double.parseDouble(ps.getIgstper())));
            netigst += Double.parseDouble(ps.getIgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getIgstamt())));
            nettotal += Double.parseDouble(ps.getTotal());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ps.getTotal())));
            ((DefaultTableModel)jTable1.getModel()).addRow(row);
        }
        try {
            if (conn!=null) conn.close();
        }
        catch(SQLException e){}

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        //Start resize the table column
        // No. Of Columns: 16
        /* SLN., ITEM, QTY., MRP, RATE, AMOUNT, DISC.%, DISC. AMT., TAXABLE, CGST%, CGST AMT., 
        SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// ITEM
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(220);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// QTY.
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// AMOUNT
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// DISC.%
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// DISC. AMT.
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// TAXABLE
	jTable1.getColumnModel().getColumn(8).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// CGST%
	jTable1.getColumnModel().getColumn(9).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// CGST AMT.
	jTable1.getColumnModel().getColumn(10).setPreferredWidth(80);
	jTable1.getColumnModel().getColumn(11).setMinWidth(0);// SGST%
	jTable1.getColumnModel().getColumn(11).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// SGST AMT.
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(13).setMinWidth(0);// IGST%
        jTable1.getColumnModel().getColumn(13).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(14).setMinWidth(0);// IGST AMT.
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(15).setMinWidth(0);// TOTAL
        jTable1.getColumnModel().getColumn(15).setPreferredWidth(100);
        
        // No. Of Columns: 16
        /* SLN., ITEM, QTY., MRP, RATE, AMOUNT, DISC.%, DISC. AMT., TAXABLE, CGST%, CGST AMT., 
        SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMOUNT").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC.%").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC. AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TAXABLE").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("IGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("IGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TOTAL").setCellRenderer( rightRenderer );
        
        jLabel29.setText(format.format(netqty));
        jLabel31.setText(MyNumberFormat.rupeeFormat(netamt));
        jLabel33.setText(MyNumberFormat.rupeeFormat(nettaxableamt));
        jLabel35.setText(MyNumberFormat.rupeeFormat(netcgst));
        jLabel37.setText(MyNumberFormat.rupeeFormat(netsgst));
        jLabel39.setText(MyNumberFormat.rupeeFormat(netigst));
        jLabel41.setText(MyNumberFormat.rupeeFormat(nettotal));
        computation02();
    }
    
    private void computation02()
    {
        double nettotal = Double.parseDouble(jLabel41.getText().replaceAll(",", "").trim());
        double nettotalwithoutdecimal = Math.round(nettotal);
        double roundoff = nettotalwithoutdecimal - nettotal;
        jTextField13.setText(MyNumberFormat.rupeeFormat(roundoff));
        double netamt01 = nettotal + roundoff;
        jLabel45.setText(MyNumberFormat.rupeeFormat(netamt01));
        double advance = 0.0;
        try
        {
            advance = Double.parseDouble(jTextField14.getText().trim());
        }
        catch (NumberFormatException ex)
        {
            return;
        }
        double netamt02 = netamt01 - advance;
        jLabel47.setText(MyNumberFormat.rupeeFormat(netamt02));
    }
    
    private void insertToDatabase()
    {
        if (psAl.size() == 0)
        {
            JOptionPane.showMessageDialog(null,"Incomplete Data !!!","Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        // compid as currentCompid
        if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Super-Stockist!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String csid="";
        try
        {
            csid=csidArray[jComboBox1.getSelectedIndex()-1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return;
        }
        String invno=jTextField1.getText().trim().toUpperCase();
        if(invno.length()==0)
        {
            JOptionPane.showMessageDialog(null,"Enter the Invoice No.!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField1.requestFocusInWindow();
            return;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date invDt =jDateChooser1.getDate();
        String invdt=null;
        try
        {
            invdt=sdf.format(invDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Purchase Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        String deliverynote = jTextField2.getText().trim().toUpperCase();
        String payterm = jTextField3.getText().trim().toUpperCase();
        String ordno = jTextField4.getText().trim().toUpperCase();
        Date ordDt =jDateChooser2.getDate();
        String orddt=null;
        try
        {
            orddt=sdf.format(ordDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Order Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser2.requestFocusInWindow();
            return;
        }
        String transporter = jTextField5.getText().trim().toUpperCase();
        String vehicleno = jTextField6.getText().trim().toUpperCase();
        Date supplyDt =jDateChooser3.getDate();
        String supplydt=null;
        try
        {
            supplydt=sdf.format(supplyDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Order Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser3.requestFocusInWindow();
            return;
        }
        String netqty = jLabel29.getText().trim().replaceAll(",", "");
        String netamt = jLabel31.getText().trim().replaceAll(",", "");
        String nettaxableamt = jLabel33.getText().trim().replaceAll(",", "");
        String netcgst = jLabel35.getText().trim().replaceAll(",", "");
        String netsgst = jLabel37.getText().trim().replaceAll(",", "");
        String netigst = jLabel39.getText().trim().replaceAll(",", "");
        String nettotal = jLabel41.getText().trim().replaceAll(",", "");
        String roundoff = jTextField13.getText().trim();
        String netamt01 = jLabel45.getText().trim().replaceAll(",", "");
        String advance = jTextField14.getText().trim();
        String netamt02 = jLabel47.getText().trim().replaceAll(",", "");
        String isopening = (jCheckBox1.isSelected()?"1":"0");
        String amtpaid = "0";
        String isactive = "1";
        String remarks = jTextField15.getText().trim().toUpperCase().replace("'", "\\'");
        
        // Number of columns in PurchaseMaster: 27
        /* pmid, ssid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        PurchaseMaster pm = new PurchaseMaster();
        pm.setPmid(""); // At Insert
        pm.setCsid(csid);
        pm.setCompid(currentCompid);
        pm.setInvno(invno);
        pm.setInvdt(invdt);
        pm.setDeliverynote(deliverynote);
        pm.setPayterm(payterm);
        pm.setOrdno(ordno);
        pm.setOrddt(orddt);
        pm.setTransporter(transporter);
        pm.setVehicleno(vehicleno);
        pm.setSupplydt(supplydt);
        pm.setNetqty(netqty);
        pm.setNetamt(netamt);
        pm.setNettaxableamt(nettaxableamt);
        pm.setNetcgst(netcgst);
        pm.setNetsgst(netsgst);
        pm.setNetigst(netigst);
        pm.setNettotal(nettotal);
        pm.setRoundoff(roundoff);
        pm.setNetamt01(netamt01);
        pm.setAdvance(advance);
        pm.setNetamt02(netamt02);
        pm.setIsopening(isopening);
        pm.setAmtpaid(amtpaid);
        pm.setIsactive(isactive);
        pm.setRemarks(remarks);
        pm.setPsAl(psAl);
        
        int pmid = q.insertToPurchaseMaster(pm);
        if (pmid > 0)
        {
            pm = q.getPurchaseMaster(pmid+"");
            
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            
            // Number of columns in ItemRegister: 17
            /* irid, compid, itemid, pknm, pkval, actiondt, refno, description, type, qty, 
            mrp, rate, discper, cgstper, sgstper, igstper, total */
            int irid=q.getMaxId("ItemRegister", "irid");
            // Number of columns in PurchasePaymentRegister: 8
            /* pprid, pmid, pknm, pkval, actiondt, refno, type, amount */
            int pprid=q.getMaxId("PurchasePaymentRegister", "pprid");
            
            try
            {
                conn.setAutoCommit(false);
                
                // Number of columns in ItemRegister: 17
                /* irid, compid, itemid, pknm, pkval, actiondt, refno, description, type, qty, 
                mrp, rate, discper, cgstper, sgstper, igstper, total */
                String sql1 = "insert into ItemRegister (irid, compid, itemid, pknm, pkval, actiondt, refno, "
                        + "description, type, qty, mrp, rate, discper, cgstper, sgstper, igstper, total) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psmt1 = conn.prepareStatement(sql1);
                
                for( PurchaseSub ref : pm.getPsAl() )
                {
                    // Number of columns in ItemRegister: 17
                /* irid, compid, itemid, pknm, pkval, actiondt, refno, description, type, qty, 
                rate, discper, cgstper, sgstper, igstper, total */
                    psmt1.setInt(1, ++irid);
                    psmt1.setInt(2, Integer.parseInt(pm.getCompid()));
                    psmt1.setInt(3, Integer.parseInt(ref.getItemid()));
                    psmt1.setString(4, "psid");
                    psmt1.setString(5, ref.getPsid());
                    psmt1.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(invdt)));
                    psmt1.setString(7, pm.getInvno());
                    psmt1.setString(8, jComboBox1.getSelectedItem().toString());
                    psmt1.setString(9, "PQ");
                    psmt1.setDouble(10, Double.parseDouble(ref.getQty()));
                    psmt1.setDouble(11, Double.parseDouble(ref.getMrp()));
                    psmt1.setDouble(12, Double.parseDouble(ref.getRate()));
                    psmt1.setDouble(13, Double.parseDouble(ref.getDiscper()));
                    psmt1.setDouble(14, Double.parseDouble(ref.getCgstper()));
                    psmt1.setDouble(15, Double.parseDouble(ref.getSgstper()));
                    psmt1.setDouble(16, Double.parseDouble(ref.getIgstper()));
                    psmt1.setDouble(17, Double.parseDouble(ref.getTotal()));
                    psmt1.addBatch();
                }
                psmt1.executeBatch();
                
                // Number of columns in PurchasePaymentRegister: 8
                /* pprid, pmid, pknm, pkval, actiondt, refno, type, amount */
                String sql2 = "insert into PurchasePaymentRegister (pprid, pmid, pknm, pkval, actiondt,"
                        + " refno, type, amount) values (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psmt2 = conn.prepareStatement(sql2);
                psmt2.setInt(1, ++pprid);
                psmt2.setInt(2, pmid);
                psmt2.setString(3, "pmid");
                psmt2.setString(4, pmid+"");
                psmt2.setDate(5, java.sql.Date.valueOf(DateConverter.dateConverter1(invdt)));
                psmt2.setString(6, invno);
                psmt2.setInt(7, 0);
                psmt2.setDouble(8, Double.parseDouble(pm.getNetamt02()));
                psmt2.executeUpdate();
                
                conn.commit();
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                try {
                    conn.rollback();
                } catch (SQLException ex1) {
                    ex1.printStackTrace();
                }
            }
            finally
            {
                if (conn != null) 
                {
                    if ( isFromOtherWindow ) {
                        // Number of columns in PurchaseSub: 19
                        /* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
                        cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                        // Number of columns in ItemMaster: 7
                        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
                        String query="select itemnm From (select top 1 itemid from PurchaseSub where pmid="
                                + pm.getPmid()+" and itemid not in (select distinct itemid from PurchaseSub"
                                + " where pmid<>"+pm.getPmid()+")) x, (select itemid, itemnm from ItemMaster)"
                                + " y where x.itemid=y.itemid";
                        System.out.println(query);
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
                                if(rs.next())
                                {
                                    probablyNewItem = rs.getString("itemnm");
                                }
                            }
                        }
                        catch(SQLException ex)
                        {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    db=null;
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    if ( isFromOtherWindow ) {
                        q.adjustQty();
                        setVisible(true);
                        dispose();
                    }
                }
                q.adjustQty();
                formFlush();
            }
        }
    }
    
    private void formFlush() {
        currentCompid = null;
        itemidArray = null;
        hsnArray = null;
        newStockist = null;
        newItemnm = null;
        psAl = new ArrayList<PurchaseSub>();
    
        jCheckBox1.setEnabled(true);
        jCheckBox1.setSelected(false);
        jTextField1.setEnabled(true);
        jTextField1.setText("");
        jDateChooser1.setEnabled(true);
        jDateChooser1.setDate(new Date());
        jComboBox1.setEnabled(true);
        jComboBox1.setSelectedIndex(0);
        jLabel4.setText("N/A");
        jTextField2.setEnabled(true);
        jTextField2.setText("N/A");
        jTextField3.setEnabled(true);
        jTextField3.setText("N/A");
        jTextField4.setEnabled(true);
        jTextField4.setText("N/A");
        jDateChooser2.setEnabled(true);
        String sDate1="01/01/2000";  
        Date date1=null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Purchase ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser2.setDate(date1);
        jTextField5.setEnabled(true);
        jTextField5.setText("N/A");
        jTextField6.setEnabled(true);
        jTextField6.setText("N/A");
        jDateChooser3.setEnabled(true);
        jDateChooser3.setDate(date1);
        jButton1.setEnabled(true);
        
        jTextField16.setText("0");
        jLabel13.setText("N/A");
        jTextField7.setText("0");
        jTextField8.setText("0");
        jLabel16.setText("0");
        jTextField9.setText("0");
        jLabel18.setText("0");
        jLabel20.setText("0");
        jTextField10.setText("0");
        jLabel22.setText("0");
        jTextField11.setText("0");
        jLabel24.setText("0");
        jTextField12.setEnabled(true);
        jTextField12.setText("0");
        jLabel26.setText("0");
        jLabel27.setText("0");
        clearTable(jTable1);
        
        jLabel29.setText("0");
        jLabel31.setText("0");
        jLabel33.setText("0");
        jLabel35.setText("0");
        jLabel37.setText("0");
        jLabel39.setText("0");
        jLabel41.setText("0");
        
        jTextField13.setText("0");
        jLabel45.setText("0");
        jTextField14.setText("0");
        jLabel47.setText("0");
        jTextField14.setText("0");
        jTextField15.setText("N/A");
        
        jTextField1.requestFocusInWindow();
    }
    
    private void rateCalculation() {
        if ( rateformula == null ) {
            return;
        }
        System.out.println(rateformula);
        String smrp = "0";
        try
        {
            smrp = jTextField16.getText().trim();
            double dmrp = Double.parseDouble(smrp);
        }
        catch (NumberFormatException ex)
        {
            return;
        }

        int firstOperatorIndex = 0;
        for ( int i =0; i < rateformula.length(); i++ ) {
            char ch = rateformula.charAt(i);
            if ( ch == '/' || ch == '*' || ch == '+' || ch == '-' ) {
                firstOperatorIndex = i;
                break;
            }
        }
        if ( firstOperatorIndex == 0 ) {
            return;
        }
        rateformula = smrp+rateformula.substring(firstOperatorIndex);
        OperatorOperand oo = SimplificationSpliter.simplificationSpliter(rateformula);
        /*
            Operators:[/, /, /]
            Operands:[1000, 1.5, 1.28, 1.02]
            private ArrayList<String> operatorList;
            private ArrayList<Float> operandList;
        */
        ArrayList<String> operatorList = oo.getOperatorList();
        ArrayList<Float> operandList = oo.getOperandList();
        double operand1 = operandList.get(0);
        double operand2 = 0.0;
        double temp = 0.0;
        for ( int i = 0; i < operatorList.size(); i++ ) {
            String operator = operatorList.get(i);
            operand2 = operandList.get(i+1);
            switch ( operator ) {
                case "/": temp = operand1 / operand2;
                    break;
                case "*": temp = operand1 * operand2;
                    break;
                case "+": temp = operand1 + operand2;
                    break;
                case "-": temp = operand1 - operand2;
                    break;
            }
            operand1 = temp;
        }
        jTextField8.setText(format2afterDecimal.format(temp));
        computation01();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setBackground(new java.awt.Color(255, 0, 0));
        jMenuItem1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jMenuItem1.setText("### DELETE THIS ITEM ###");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

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
        jLabel1.setText("INV. NO.");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("INV. DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("SUPER STOCKIST");

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("IS OPENING STOSK ?");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(255, 255, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("N/A");
        jLabel4.setOpaque(true);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("DELIVERY NOTE");

        jTextField2.setText("N/A");
        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField2FocusLost(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("PAYMENT TERM");

        jTextField3.setText("N/A");
        jTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField3FocusLost(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("ORD. NO.");

        jTextField4.setText("N/A");
        jTextField4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField4FocusLost(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("ORD. DATE");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("TRANSPORTER");

        jTextField5.setText("N/A");
        jTextField5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField5FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField5FocusLost(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("VEHICLE NO.");

        jTextField6.setText("N/A");
        jTextField6.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField6FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField6FocusLost(evt);
            }
        });
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField6KeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("SUPPLY DATE");

        jDateChooser3.setDateFormatString("dd/MM/yyyy");

        jPanel1.setBackground(new java.awt.Color(229, 242, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "PRODUCTS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("ITEM NAME");

        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });
        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(255, 255, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("N/A");
        jLabel13.setOpaque(true);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("QTY.");

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("RATE");

        jLabel16.setBackground(new java.awt.Color(255, 255, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("0");
        jLabel16.setOpaque(true);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("DISCOUNT %");

        jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField9.setText("0");
        jTextField9.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField9FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField9FocusLost(evt);
            }
        });
        jTextField9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField9KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField9KeyReleased(evt);
            }
        });

        jLabel18.setBackground(new java.awt.Color(255, 255, 0));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("0");
        jLabel18.setOpaque(true);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("TAXABLE AMT.");

        jLabel20.setBackground(new java.awt.Color(255, 255, 0));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("0");
        jLabel20.setOpaque(true);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("CGST %");

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField10.setText("0");
        jTextField10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField10FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField10FocusLost(evt);
            }
        });
        jTextField10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField10KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField10KeyReleased(evt);
            }
        });

        jLabel22.setBackground(new java.awt.Color(255, 255, 0));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("0");
        jLabel22.setOpaque(true);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("SGST %");

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField11.setText("0");
        jTextField11.setEnabled(false);

        jLabel24.setBackground(new java.awt.Color(255, 255, 0));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("0");
        jLabel24.setOpaque(true);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("IGST %");

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField12.setText("0");
        jTextField12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField12FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField12FocusLost(evt);
            }
        });
        jTextField12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField12KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField12KeyReleased(evt);
            }
        });

        jLabel26.setBackground(new java.awt.Color(255, 255, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("0");
        jLabel26.setOpaque(true);

        jLabel27.setBackground(new java.awt.Color(0, 255, 0));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("0");
        jLabel27.setOpaque(true);

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("ADD TO LIST");
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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ADDED ITEM DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "ITEM", "QTY.", "MRP", "RATE", "AMOUNT", "DISC.%", "DISC. AMT.", "TAXABLE", "CGST%", "CGST AMT.", "SGST%", "SGST AMT.", "IGST%", "IGST AMT.", "TOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("NET QTY.");

        jLabel29.setBackground(new java.awt.Color(255, 255, 0));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("0");
        jLabel29.setOpaque(true);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("NET AMOUNT");

        jLabel31.setBackground(new java.awt.Color(255, 255, 0));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");
        jLabel31.setOpaque(true);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setText("NET TAXABLE");

        jLabel33.setBackground(new java.awt.Color(255, 255, 0));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");
        jLabel33.setOpaque(true);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("NET CGST");

        jLabel35.setBackground(new java.awt.Color(255, 255, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("0");
        jLabel35.setOpaque(true);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("NET SGST");

        jLabel37.setBackground(new java.awt.Color(255, 255, 0));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("0");
        jLabel37.setOpaque(true);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setText("NET IGST");

        jLabel39.setBackground(new java.awt.Color(255, 255, 0));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("0");
        jLabel39.setOpaque(true);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setText("NET TOTAL");

        jLabel41.setBackground(new java.awt.Color(255, 255, 0));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("0");
        jLabel41.setOpaque(true);

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel49.setText("MRP");

        jTextField16.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField16.setText("0");
        jTextField16.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField16FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField16FocusLost(evt);
            }
        });
        jTextField16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField16KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField16KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel49)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15)
                            .addComponent(jLabel17)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(jLabel25)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel40)
                                .addComponent(jLabel41))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel38)
                                .addComponent(jLabel39))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel36)
                                .addComponent(jLabel37))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel34)
                                .addComponent(jLabel35))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel32)
                                .addComponent(jLabel33))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(jLabel31))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel28)
                                .addComponent(jLabel29))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel14)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("SAVE & PROCEED");
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

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel42.setText("ROUND-OFF");

        jTextField13.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField13.setText("0");
        jTextField13.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField13FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField13FocusLost(evt);
            }
        });
        jTextField13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField13KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField13KeyReleased(evt);
            }
        });

        jLabel45.setBackground(new java.awt.Color(255, 255, 0));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("0");
        jLabel45.setOpaque(true);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel46.setText("ADVANCE");

        jTextField14.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField14.setText("0");
        jTextField14.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField14FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField14FocusLost(evt);
            }
        });
        jTextField14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField14KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField14KeyReleased(evt);
            }
        });

        jLabel47.setBackground(new java.awt.Color(0, 255, 0));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("0");
        jLabel47.setOpaque(true);

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel48.setText("REMARKS");

        jTextField15.setText("N/A");
        jTextField15.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField15FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField15FocusLost(evt);
            }
        });
        jTextField15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField15KeyPressed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("SAVE");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCheckBox1)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel7)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel11)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel46)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel47)
                        .addComponent(jLabel48)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel42)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel45)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        try
        {
            if(((String)jComboBox1.getSelectedItem()).equals("-- Select --"))
            {
                jLabel4.setText("N/A");
                currentCompid = null;
                jComboBox2.removeAllItems();
            }
            else
            {
                jLabel4.setText(compnmArray[jComboBox1.getSelectedIndex()-1]);
                currentCompid=compidArray[jComboBox1.getSelectedIndex()-1];
                populateCombo2();
            }
        }
        catch(NullPointerException ex){}
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterStockist();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        try
        {
            if(((String)jComboBox2.getSelectedItem()).equals("-- Select --"))
            {
                jLabel13.setText("N/A");
                rateformula = null;
                jTextField8.setText("0");
            }
            else
            {
                try {
                    jLabel13.setText(hsnArray[jComboBox2.getSelectedIndex()-1]);
                    // Rate Calculation
                    String itemid = "";
                    try {
                        itemid = itemidArray[jComboBox2.getSelectedIndex() - 1];
                    } catch (NullPointerException ex) {
                        return;
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        return;
                    }
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    // Number of columns in Formula: 5
                    /* formulaid, forsale, onmrp, formula, isactive */
                    // Number of columns in PSRateFormula: 6
                    /* psrfid, itemid, pformulaid, sformulaid, status, isactive */
                    String query="select onmrp, formula from PSRateFormula, Formula where pformulaid="
                            + "formulaid and itemid="+itemid;
                    System.out.println(query);
                    try {
                        Statement stm=conn.createStatement();
                        ResultSet rs=stm.executeQuery(query);
                        if ( rs.next() ) {
                            String formula = rs.getString("formula");
                            if ( !formula.equals("-") )
                                rateformula = (rs.getString("onmrp").equals("0")?"Rate":"MRP")
                                        + formula;
                            else
                                rateformula = null;
                        }
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    finally {
                        try {
                            if (conn!=null) conn.close();
                        } catch(SQLException ex){}
                    }
                    rateCalculation();
                } catch (ArrayIndexOutOfBoundsException ex) {}
            }
        }
        catch(NullPointerException ex){}
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField16.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterItem();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F3)
        {
            jTextField13.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        if ( jCheckBox1.isSelected() )
        {
            jTextField1.setText(getOpeningStockInvoiceNo());
            jTextField1.setEnabled(false);
        }
        else
        {
            jTextField1.setText("");
            jTextField1.setEnabled(true);
        }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jCheckBox1.isSelected() ) {
                jDateChooser1.requestFocusInWindow();
            } else {
                jTextField1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        String s=jTextField2.getText().trim();
        if(s.equals("N/A")) {
            jTextField2.setText("");
        }
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusLost
        String s=jTextField2.getText().trim();
        if(s.length()==0) {
            jTextField2.setText("N/A");
        }
    }//GEN-LAST:event_jTextField2FocusLost

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
        String s=jTextField3.getText().trim();
        if(s.equals("N/A")) {
            jTextField3.setText("");
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        String s=jTextField3.getText().trim();
        if(s.length()==0) {
            jTextField3.setText("N/A");
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jTextField4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusGained
        String s=jTextField4.getText().trim();
        if(s.equals("N/A")) {
            jTextField4.setText("");
        }
    }//GEN-LAST:event_jTextField4FocusGained

    private void jTextField4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField4FocusLost
        String s=jTextField4.getText().trim();
        if(s.length()==0) {
            jTextField4.setText("N/A");
        }
    }//GEN-LAST:event_jTextField4FocusLost

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusGained
        String s=jTextField5.getText().trim();
        if(s.equals("N/A")) {
            jTextField5.setText("");
        }
    }//GEN-LAST:event_jTextField5FocusGained

    private void jTextField5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField5FocusLost
        String s=jTextField5.getText().trim();
        if(s.length()==0) {
            jTextField5.setText("N/A");
        }
    }//GEN-LAST:event_jTextField5FocusLost

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField6.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jTextField6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusGained
        String s=jTextField6.getText().trim();
        if(s.equals("N/A")) {
            jTextField6.setText("");
        }
    }//GEN-LAST:event_jTextField6FocusGained

    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
        String s=jTextField6.getText().trim();
        if(s.length()==0) {
            jTextField6.setText("N/A");
        }
    }//GEN-LAST:event_jTextField6FocusLost

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        billingStart();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            billingStart();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTextField7FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusGained
        String s=jTextField7.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField7.setText("");
        } else {
            jTextField7.selectAll();
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
            jTextField8.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField8FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField8FocusGained
        String s=jTextField8.getText().trim();
        if(Double.parseDouble(s) == 0) {
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
            jTextField9.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField8KeyPressed

    private void jTextField9FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField9FocusGained
        String s=jTextField9.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField9.setText("");
        } else {
            jTextField9.selectAll();
        }
    }//GEN-LAST:event_jTextField9FocusGained

    private void jTextField9FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField9FocusLost
        String s=jTextField9.getText().trim();
        if(s.length()==0) {
            jTextField9.setText("0");
        }
    }//GEN-LAST:event_jTextField9FocusLost

    private void jTextField9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField10.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField9KeyPressed

    private void jTextField10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusGained
        String s=jTextField10.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField10.setText("");
        } else {
            jTextField10.selectAll();
        }
    }//GEN-LAST:event_jTextField10FocusGained

    private void jTextField10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusLost
        String s=jTextField10.getText().trim();
        if(s.length()==0) {
            jTextField10.setText("0");
        }
    }//GEN-LAST:event_jTextField10FocusLost

    private void jTextField10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            double val = 0.0;
            try {
                val = Double.parseDouble( jTextField10.getText().trim() );
            } catch ( NumberFormatException ex ) {
                val = 0.0;
            }
            if ( val != 0.0 ) {
                jTextField12.setEnabled(false);
                jButton2.requestFocusInWindow();
            } else {
                jTextField12.setEnabled(true);
                jTextField12.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTextField10KeyPressed

    private void jTextField12FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusGained
        String s=jTextField12.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField12.setText("");
        } else {
            jTextField12.selectAll();
        }
    }//GEN-LAST:event_jTextField12FocusGained

    private void jTextField12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusLost
        String s=jTextField12.getText().trim();
        if(s.length()==0) {
            jTextField12.setText("0");
        }
    }//GEN-LAST:event_jTextField12FocusLost

    private void jTextField12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField12KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            addToList();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField8KeyReleased

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField9KeyReleased

    private void jTextField10KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyReleased
        String x = jTextField10.getText().trim();
        if ( x.length() != 0 ) {
            jTextField11.setText(jTextField10.getText());
            jTextField12.setEnabled(false);
            computation01();
        } else {
            jTextField11.setText("0");
            jTextField12.setEnabled(true);
        }
    }//GEN-LAST:event_jTextField10KeyReleased

    private void jTextField12KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField12KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        if(evt.getSource() == jMenuItem1)
        {
            if(jTable1.getSelectedRow()!=-1 && jTable1.getSelectedColumn()!=-1)
            {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Item!",
                        "Delete Record",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0)
                {
                    int row = jTable1.getSelectedRow();
                    psAl.remove(row);
                    Fetch();
                    jComboBox2.requestFocusInWindow();
                }
                else
                    JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(null,"Select an Item and then proceed!!!","Error Found",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jTextField13FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField13FocusGained
        String s=jTextField13.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField13.setText("");
        } else {
            jTextField13.selectAll();
        }
    }//GEN-LAST:event_jTextField13FocusGained

    private void jTextField13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField13FocusLost
        String s=jTextField13.getText().trim();
        if(s.length()==0) {
            jTextField13.setText("0");
        }
    }//GEN-LAST:event_jTextField13FocusLost

    private void jTextField13KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField13KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField14.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField13KeyPressed

    private void jTextField13KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField13KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField13KeyReleased

    private void jTextField14FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField14FocusGained
        String s=jTextField14.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField14.setText("");
        } else {
            jTextField14.selectAll();
        }
    }//GEN-LAST:event_jTextField14FocusGained

    private void jTextField14FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField14FocusLost
        String s=jTextField14.getText().trim();
        if(s.length()==0) {
            jTextField14.setText("0");
        }
    }//GEN-LAST:event_jTextField14FocusLost

    private void jTextField14KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField14KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField15.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField14KeyPressed

    private void jTextField14KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField14KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField14KeyReleased

    private void jTextField15FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField15FocusGained
        String s=jTextField15.getText().trim();
        if(s.equals("N/A")) {
            jTextField15.setText("");
        }
    }//GEN-LAST:event_jTextField15FocusGained

    private void jTextField15FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField15FocusLost
        String s=jTextField15.getText().trim();
        if(s.length()==0) {
            jTextField15.setText("N/A");
        }
    }//GEN-LAST:event_jTextField15FocusLost

    private void jTextField15KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField15KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField15KeyPressed

    private void jTextField16FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField16FocusGained
        String s=jTextField16.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField16.setText("");
        } else {
            jTextField16.selectAll();
        }
    }//GEN-LAST:event_jTextField16FocusGained

    private void jTextField16FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField16FocusLost
        String s=jTextField16.getText().trim();
        if(s.length()==0) {
            jTextField16.setText("0");
        }
    }//GEN-LAST:event_jTextField16FocusLost

    private void jTextField16KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField16KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField16KeyPressed

    private void jTextField16KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField16KeyReleased
        rateCalculation();
    }//GEN-LAST:event_jTextField16KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
