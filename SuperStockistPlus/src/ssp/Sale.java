package ssp;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.Enterprise;
import dto.OperatorOperand;
import dto.SaleMaster;
import dto.SaleSub;
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
import utilities.Add0Padding6;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.ProperCase;
import utilities.Settings;
import utilities.SimplificationSpliter;

public class Sale extends javax.swing.JInternalFrame  implements AWTEventListener {

    private Settings settings=new Settings();
    private Query q=new Query();
    private JDesktopPane jDesktopPane1;
    private UserProfile up;
    private Enterprise e;
    private DecimalFormat format = new DecimalFormat("0.#");
    private DecimalFormat format2afterDecimal = new DecimalFormat("#.##");
    
    private String distidArray[];
    private String newDistnm;
    private String beatabbrArray[];
    private String itemidArray[];
    private String selectedItemid;
    private String selectedPsid;
    private String hsnArray[];
    private double avlqty;
    private ArrayList<SaleSub> ssAl = new ArrayList<SaleSub>();
    private String compidArray[];
    private SaleMaster sm;
    private String probablyNewItem;
    private String rateformula;
    private boolean onmrpFlag;
    private double purRate;
    
    public Sale(JDesktopPane jDesktopPane, UserProfile up, Enterprise e) 
    {
        super("Sale",false,true,false,true);
        initComponents(); 
        this.jDesktopPane1 = jDesktopPane;
        this.up = up;
        this.e = e;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/prod_sale_01.png")));
        
        this.getActionMap().put("test", new AbstractAction(){  //ESCAPE
            @Override
            public void actionPerformed(ActionEvent e) {                
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
        ((JLabel)jComboBox3.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        settings.numvalidatorFloat(jTextField6);
        settings.numvalidatorFloat(jTextField7);
        settings.numvalidatorFloat(jTextField8);
        settings.numvalidatorFloat(jTextField9);
        settings.numvalidatorFloat(jTextField10);
        settings.numvalidatorFloat(jTextField11);
        settings.numvalidatorFloatWithSign(jTextField12);
        settings.numvalidatorFloat(jTextField14);
        settings.numvalidatorFloat(jTextField15);
        
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
        
        populateCombo1();
        populateCombo3();
        
        jTable1.setComponentPopupMenu(jPopupMenu1);
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jComboBox3.requestFocusInWindow();
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
                    jTextField1.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10)
                {
                    jComboBox1.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser3.getDateEditor())&&key.getKeyCode()==10)
                {
                    jButton1.requestFocusInWindow();
                }
            }
        }
    }
    
    private String getNextSaleInvoiceNo()
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
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String compabbr = jComboBox3.getSelectedItem().toString();
        query="select IFNULL(max(salemid),'') as x from SaleMaster where salemid like '"
                    + e.getEabbr()+compabbr+"/______/"+financialcode+"'";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                String lastTotalID=rs.getString("x");
                if(lastTotalID.length()!=0)
                {
                    String lastID=lastTotalID.substring(lastTotalID.indexOf("/")+1,lastTotalID.lastIndexOf("/"));
                    total=Integer.parseInt(lastID);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
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
        return e.getEabbr()+compabbr+"/"+Add0Padding6.add0Padding(total)+"/"+financialcode;
    }

    private void populateCombo1() // Distributer
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Number of columns in BeatMaster: 4
	/* beatid, beatnm, beatabbr, isactive */
        String query="select distid, distnm, beatabbr from (select distid, beatid, distnm from "
                + "Distributer where isactive=1) x, (select beatid, beatabbr from BeatMaster"
                + " where isactive=1) y where x.beatid=y.beatid order by distnm asc";
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
                distidArray=new String[total];
                beatabbrArray = new String[total];
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    distidArray[i]=rs.getString("distid");
                    String beatabbr = rs.getString("beatabbr");
                    beatabbrArray[i] = beatabbr;
                    jComboBox1.addItem(rs.getString("distnm")+" ["+beatabbr+"]");
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
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
    
    private void populateCombo2() // Item HSN
    {
        if (jComboBox3.getSelectedIndex()==0) {
            return;
        }
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
	cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        String query="select itemid, hsn, itemnm from (select itemid, itemnm, icid from ItemMaster"
                + " where isactive=1 and itemid in (select distinct itemid from PurchaseSub "
                + "where qty-(qtysold+retqty)>0) and compid="+compidArray[jComboBox3.getSelectedIndex()-1]
                + ") x, (select icid, hsn from ItemCategory where isactive=1) y where x.icid=y.icid"
                + " order by itemnm";
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
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
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
    
    private void populateCombo3() // Company
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyMaster: 6
        /* compid, compnm, compabbr, compcontact, compmail, isactive */
        String query="select compid, compabbr from CompanyMaster order by compabbr asc";
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
            if(total != 0)
            {
                compidArray=new String[total];
                jComboBox3.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    compidArray[i++]=rs.getString("compid");
                    jComboBox3.addItem(rs.getString("compabbr"));
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
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
    
    private void addAlterDistributer() {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final DistributerMaster ref=new DistributerMaster(jDesktopPane1, true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newDistnm=ref.getNewDistnm();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Sale.this.setVisible(true);
                            if(newDistnm!=null)
                            {
                                populateCombo1();
                                jComboBox1.setSelectedItem(newDistnm);
                                try
                                {
                                    if(((String)jComboBox1.getSelectedItem()).equals("-- Select --"))
                                    {
                                        jLabel51.setText("N/A");
                                    }
                                    else
                                    {
                                        jLabel51.setText(beatabbrArray[jComboBox1.getSelectedIndex()-1]);
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
    
    private void getItemFromPurchase() {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final PurchaseItem ref=new PurchaseItem(selectedItemid);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            selectedPsid=ref.getSelectedPsid();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Sale.this.setVisible(true);
                            if(selectedPsid!=null)
                            {
                                // Populating Item Data
                                // Number of columns in PurchaseSub: 19
                                /* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
                                cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                                String query="select mrp, 0 as salerate, discper, cgstper, sgstper, igstper, "
                                        + "qty-(qtysold+retqty) as qtyavl from PurchaseSub where psid="+selectedPsid;
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
                                        if(rs.next())
                                        {
                                            jLabel15.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("mrp"))));
                                            jTextField6.setText(format.format(Double.parseDouble(rs.getString("qtyavl"))));
                                            jTextField7.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("salerate"))));
                                            jTextField8.setText(format.format(Double.parseDouble(rs.getString("discper"))));
                                            double igstper = Double.parseDouble(rs.getString("igstper"));
                                            if ( igstper != 0.0 ) {
                                                jTextField9.setText(format.format(igstper / 2.0));
                                                jTextField10.setText(format.format(igstper / 2.0));
                                            } else {
                                                jTextField9.setText(format.format(Double.parseDouble(rs.getString("cgstper"))));
                                                jTextField10.setText(format.format(Double.parseDouble(rs.getString("sgstper"))));
                                            }
                                            jTextField11.setText("0");
                                            avlqty = Double.parseDouble(rs.getString("qtyavl"));
                                            rateCalculation();
                                            computation01();
                                            jTextField6.requestFocusInWindow();
                                        }
                                    }
                                }
                                catch(SQLException ex)
                                {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                                            "Error Found",JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                                finally
                                {
                                    try {
                                        if (conn!=null) conn.close();
                                    }
                                    catch(SQLException ex){}
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
            qty = Double.parseDouble(jTextField6.getText().trim());
            rate = Double.parseDouble(jTextField7.getText().trim());
            discper = Double.parseDouble(jTextField8.getText().trim());
            cgstper = Double.parseDouble(jTextField9.getText().trim());
            sgstper = Double.parseDouble(jTextField10.getText().trim());
            igstper = Double.parseDouble(jTextField11.getText().trim());
        }
        catch (NumberFormatException ex)
        {
            return;
        }
        if ( qty > avlqty ) {
            JOptionPane.showMessageDialog(null,"Quantity for Sale can't be > available quantity in Purchase !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField6.selectAll();
            jTextField6.requestFocusInWindow();
            return;
        }
        double amt = qty * rate;
        jLabel18.setText(MyNumberFormat.rupeeFormat(amt));
        double discamt = amt * (discper / 100.0);
        jLabel20.setText(MyNumberFormat.rupeeFormat(discamt));
        double taxableamt = amt - discamt;
        jLabel22.setText(MyNumberFormat.rupeeFormat(taxableamt));
        double cgstamt = taxableamt * (cgstper / 100.0);
        jLabel24.setText(MyNumberFormat.rupeeFormat(cgstamt));
        double sgstamt = taxableamt * (sgstper / 100.0);
        jLabel26.setText(MyNumberFormat.rupeeFormat(sgstamt));
        double igstamt = taxableamt * (igstper / 100.0);
        jLabel28.setText(MyNumberFormat.rupeeFormat(igstamt));
        double total = taxableamt + (cgstamt + sgstamt + igstamt);
        jLabel29.setText(MyNumberFormat.rupeeFormat(total));
    }
    
    private void computation02()
    {
        double nettotal = Double.parseDouble(jLabel43.getText().replaceAll(",", "").trim());
        double nettotalwithoutdecimal = Math.round(nettotal);
        double roundoff = nettotalwithoutdecimal - nettotal;
        jTextField12.setText(MyNumberFormat.rupeeFormat(roundoff));
        double netamt01 = nettotal + roundoff;
        jLabel45.setText(MyNumberFormat.rupeeFormat(netamt01));
        double advance = 0.0;
        double displayamt = 0.0;
        try
        {
            displayamt = Double.parseDouble(jTextField14.getText().trim());
            advance = Double.parseDouble(jTextField15.getText().trim());
        }
        catch (NumberFormatException ex)
        {
            return;
        }
        double netamt02 = netamt01 - ( displayamt + advance );
        jLabel49.setText(MyNumberFormat.rupeeFormat(netamt02));
    }
    
    private void addToList() {
        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        if (selectedPsid == null || selectedItemid == null) {
            JOptionPane.showMessageDialog(null,"Item should be selected from Purchase using <Enter> !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        // Checking duplicate for same psid
        if ( ssAl.size() != 0 ) {
            for ( SaleSub ss : ssAl ) {
                if ( ss.getPsid().equals(selectedPsid)) {
                    JOptionPane.showMessageDialog(null,"Duplicate Purchase-Item Selection !!!",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    jComboBox2.requestFocusInWindow();
                    return;
                }
            }
        }
        if ( jComboBox2.getSelectedIndex() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select proper item !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        // Already have "itemid" as currentItemid
        String mrp = jLabel15.getText().trim().replaceAll(",", "");
        String qty=jTextField6.getText().trim();
        if(Double.parseDouble(qty) == 0)
        {
            JOptionPane.showMessageDialog(null,"Quantity is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField6.requestFocusInWindow();
            return;
        }
        if ( Double.parseDouble(qty) > avlqty ) {
            JOptionPane.showMessageDialog(null,qty+"Pcs item not available in selected purchase !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField6.requestFocusInWindow();
            return;
        }
        String rate=jTextField7.getText().trim();
        if(Double.parseDouble(rate) == 0)
        {
            JOptionPane.showMessageDialog(null,"Rate is mandatory !!!","Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField7.requestFocusInWindow();
            return;
        }
        String amt = jLabel18.getText().trim().replaceAll(",", "");
        String discper = jTextField8.getText().trim();
        String discamt = jLabel20.getText().trim().replaceAll(",", "");
        String taxableamt = jLabel22.getText().trim().replaceAll(",", "");
        String cgstper = jTextField9.getText().trim();
        String cgstamt = jLabel24.getText().trim().replaceAll(",", "");
        String sgstper = jTextField10.getText().trim();
        String sgstamt = jLabel26.getText().trim().replaceAll(",", "");
        String igstper = jTextField11.getText().trim();
        String igstamt = jLabel28.getText().trim().replaceAll(",", "");
        if ( Double.parseDouble(cgstper) != 0 && Double.parseDouble(sgstper) != 0 
                && Double.parseDouble(igstper) != 0 ) {
            JOptionPane.showMessageDialog(null,"Either CGST+SGST or IGST !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField9.selectAll();
            jTextField9.requestFocusInWindow();
            return;
        } else {
            if ( (Double.parseDouble(cgstper) != 0 && Double.parseDouble(igstper) != 0) || 
                    (Double.parseDouble(sgstper) != 0 && Double.parseDouble(igstper) != 0) ) {
                JOptionPane.showMessageDialog(null,"Combination CGST+SGST or IGST !!!",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField9.selectAll();
                jTextField9.requestFocusInWindow();
                return;
            } else {
                if ( (Double.parseDouble(cgstper) != 0 && Double.parseDouble(sgstper) == 0) || 
                        (Double.parseDouble(cgstper) == 0 && Double.parseDouble(sgstper) != 0) ) {
                    JOptionPane.showMessageDialog(null,"Combination must be CGST+SGST !!!",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    jTextField9.selectAll();
                    jTextField9.requestFocusInWindow();
                    return;
                } else {
                    if ( Double.parseDouble(cgstper) != Double.parseDouble(sgstper) ) {
                        JOptionPane.showMessageDialog(null,"CGST must be equals to SGST !!!",
                                "Error Found",JOptionPane.ERROR_MESSAGE);
                        jTextField9.selectAll();
                        jTextField9.requestFocusInWindow();
                        return;
                    }
                }
            }
        }
        String total = jLabel29.getText().trim().replaceAll(",", "");
        String retqty = "0";
        
        SaleSub ss = new SaleSub();
        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        ss.setSalesid(""); // At Insert
        ss.setSalemid(""); // At Insert
        ss.setPsid(selectedPsid);
        ss.setItemid(selectedItemid);
        ss.setQty(qty);
        ss.setMrp(mrp);
        ss.setRate(rate);
        ss.setAmt(amt);
        ss.setDiscper(discper);
        ss.setDiscamt(discamt);
        ss.setTaxableamt(taxableamt);
        ss.setCgstper(cgstper);
        ss.setCgstamt(cgstamt);
        ss.setSgstper(sgstper);
        ss.setSgstamt(sgstamt);
        ss.setIgstper(igstper);
        ss.setIgstamt(igstamt);
        ss.setTotal(total);
        ss.setRetqty(retqty);
        ssAl.add(ss);
        
        Fetch();
        
        selectedItemid = null;
        selectedPsid = null;
        avlqty = 0.0;
        onmrpFlag = false;
        purRate = 0.0;
        
        jComboBox2.setSelectedIndex(0);
        jLabel13.setText("N/A");
        jLabel15.setText("0");
        jTextField6.setText("0");
        jTextField7.setText("0");
        jLabel18.setText("0");
        jTextField8.setText("0");
        jLabel20.setText("0");
        jLabel22.setText("0");
        jTextField9.setText("0");
        jLabel24.setText("0");
        jTextField10.setText("0");
        jLabel26.setText("0");
        jTextField11.setEnabled(true);
        jTextField11.setText("0");
        jLabel28.setText("0");
        jLabel29.setText("0");
        
        jComboBox2.requestFocusInWindow();
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
        
        // Number of columns in SaleSub: 19
        /* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
        cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        for (SaleSub ss :  ssAl)
        {
            Vector<String> row = new Vector<String>();
            row.addElement(++slno1+"");
            // No. Of Columns: 18
            /* SLN., PUR. NO., ITEM, QTY., UOM, MRP, RATE, AMOUNT, DISC.%, DISC. AMT., TAXABLE, CGST%, CGST AMT., 
            SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
            // Getting Pur. Inv. No.
            // Number of columns in PurchaseMaster: 27
            /* pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
            // Number of columns in PurchaseSub: 19
            /* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
            cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
            String query="select invno from PurchaseMaster, PurchaseSub where PurchaseMaster.pmid="
                    + "PurchaseSub.pmid and psid="+ss.getPsid();
            System.out.println(query);
            try
            {
                Statement smt=conn.createStatement();
                ResultSet rs=smt.executeQuery(query);
                if (rs.next())
                {
                    row.addElement(rs.getString("invno"));
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Number of columns in ItemMaster: 7
            /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
            // Number of columns in MeasuringUnit: 3
            /* muid, munm, isactive */
            // Number of columns in ItemCategory: 4
            /* icid, icnm, hsn, isactive */
            query="select itemnm, munm, hsn from ItemMaster, MeasuringUnit, ItemCategory where ItemMaster.muid="
                    + "MeasuringUnit.muid and ItemMaster.icid=ItemCategory.icid and itemid="+ss.getItemid();
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
                JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            netqty += Double.parseDouble(ss.getQty());
            row.addElement(format.format(Double.parseDouble(ss.getQty())));
            row.addElement(munm);
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getMrp())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getRate())));
            netamt += Double.parseDouble(ss.getAmt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getAmt())));
            row.addElement(format.format(Double.parseDouble(ss.getDiscper())));
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getDiscamt())));
            nettaxableamt += Double.parseDouble(ss.getTaxableamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getTaxableamt())));
            row.addElement(format.format(Double.parseDouble(ss.getCgstper())));
            netcgst += Double.parseDouble(ss.getCgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getCgstamt())));
            row.addElement(format.format(Double.parseDouble(ss.getSgstper())));
            netsgst += Double.parseDouble(ss.getSgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getSgstamt())));
            row.addElement(format.format(Double.parseDouble(ss.getIgstper())));
            netigst += Double.parseDouble(ss.getIgstamt());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getIgstamt())));
            nettotal += Double.parseDouble(ss.getTotal());
            row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(ss.getTotal())));
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
        // No. Of Columns: 18
        /* SLN., PUR. NO., ITEM, QTY., UOM, MRP, RATE, AMOUNT, DISC.%, DISC. AMT., TAXABLE, CGST%, CGST AMT., 
        SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// PUR. NO.
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
	jTable1.getColumnModel().getColumn(2).setMinWidth(0);// ITEM
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(220);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// QTY.
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// UMO
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// MRP
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// RATE
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// AMOUNT
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// DISC.%
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// DISC. AMT.
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// TAXABLE
	jTable1.getColumnModel().getColumn(10).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(11).setMinWidth(0);// CGST%
	jTable1.getColumnModel().getColumn(11).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// CGST AMT.
	jTable1.getColumnModel().getColumn(12).setPreferredWidth(80);
	jTable1.getColumnModel().getColumn(13).setMinWidth(0);// SGST%
	jTable1.getColumnModel().getColumn(13).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(14).setMinWidth(0);// SGST AMT.
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(15).setMinWidth(0);// IGST%
        jTable1.getColumnModel().getColumn(15).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(16).setMinWidth(0);// IGST AMT.
        jTable1.getColumnModel().getColumn(16).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(17).setMinWidth(0);// TOTAL
        jTable1.getColumnModel().getColumn(17).setPreferredWidth(100);
        
        // No. Of Columns: 18
        /* SLN., PUR. NO., ITEM, QTY., UMO, MRP, RATE, AMOUNT, DISC.%, DISC. AMT., TAXABLE, CGST%, CGST AMT., 
        SGST%, SGST AMT., IGST%, IGST AMT., TOTAL */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("UOM").setCellRenderer( centerRenderer );
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
        
        jLabel31.setText(format.format(netqty));
        jLabel33.setText(MyNumberFormat.rupeeFormat(netamt));
        jLabel35.setText(MyNumberFormat.rupeeFormat(nettaxableamt));
        jLabel37.setText(MyNumberFormat.rupeeFormat(netcgst));
        jLabel39.setText(MyNumberFormat.rupeeFormat(netsgst));
        jLabel41.setText(MyNumberFormat.rupeeFormat(netigst));
        jLabel43.setText(MyNumberFormat.rupeeFormat(nettotal));
        computation02();
    }
    
    private void insertToDatabase()
    {
        System.out.println(e.getEname()+"-------------"+"RAJIV ENTERPRISE");
        System.out.println(e.getEname().equals("RAJIV ENTERPRISE"));
        System.out.println(!e.getEname().equals("RAJIV ENTERPRISE"));
        if(!(e.getEname().equals("RAJIV ENTERPRISE"))) {
            JOptionPane.showMessageDialog(null,"Unauthorized User","You are not AUTHORIZED to use this software",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (ssAl.size() == 0)
        {
            JOptionPane.showMessageDialog(null,"Incomplete Data !!!","Incomplete Data",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        if(((String)jComboBox3.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Company!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return;
        }
        String compid="";
        try
        {
            compid=compidArray[jComboBox3.getSelectedIndex()-1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return;
        }
        String salemid=jLabel2.getText().trim();
        if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Retailer!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String distid="";
        try
        {
            distid=distidArray[jComboBox1.getSelectedIndex()-1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            return;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date saleDt =jDateChooser1.getDate();
        String saledt=null;
        try
        {
            saledt=sdf.format(saleDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Sale Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        String ordno = jTextField1.getText().trim().toUpperCase();
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
        String deliverynote = jTextField2.getText().trim().toUpperCase();
        String paymentterm = jTextField3.getText().trim().toUpperCase();
        String transporter = jTextField4.getText().trim().toUpperCase();
        String vehicleno = jTextField5.getText().trim().toUpperCase();
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
        String netqty = jLabel31.getText().trim().replaceAll(",", "");
        String netamt = jLabel33.getText().trim().replaceAll(",", "");
        String nettaxableamt = jLabel35.getText().trim().replaceAll(",", "");
        String netcgst = jLabel37.getText().trim().replaceAll(",", "");
        String netsgst = jLabel39.getText().trim().replaceAll(",", "");
        String netigst = jLabel41.getText().trim().replaceAll(",", "");
        String nettotal = jLabel43.getText().trim().replaceAll(",", "");
        String roundoff = jTextField12.getText().trim();
        String netamt01 = jLabel45.getText().trim().replaceAll(",", "");
        String displaynote = ProperCase.toDisplayCase(jTextField13.getText().trim());
        String displayamt = jTextField14.getText().trim();
        String advance = jTextField15.getText().trim();
        String netamt02 = jLabel49.getText().trim().replaceAll(",", "");
        String amtpaid = "0";
        String isactive = "1";
        String remarks = jTextField16.getText().trim().toUpperCase().replace("'", "\\'");
        
        // Number of columns in SaleMaster: 27
        /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
        sm = new SaleMaster();
        sm.setSalemid(salemid);
        sm.setCompid(compid);
        sm.setDistid(distid);
        sm.setSaledt(saledt);
        sm.setOrdno(ordno);
        sm.setOrddt(orddt);
        sm.setDeliverynote(deliverynote);
        sm.setPaymentterm(paymentterm);
        sm.setTransporter(transporter);
        sm.setVehicleno(vehicleno);
        sm.setSupplydt(supplydt);
        sm.setNetqty(netqty);
        sm.setNetamt(netamt);
        sm.setNettaxableamt(nettaxableamt);
        sm.setNetcgst(netcgst);
        sm.setNetsgst(netsgst);
        sm.setNetigst(netigst);
        sm.setNettotal(nettotal);
        sm.setRoundoff(roundoff);
        sm.setNetamt01(netamt01);
        sm.setDisplaynote(displaynote);
        sm.setDisplayamt(displayamt);
        sm.setAdvance(advance);
        sm.setNetamt02(netamt02);
        sm.setAmtpaid(amtpaid);
        sm.setIsactive(isactive);
        sm.setRemarks(remarks);
        sm.setSsAl(ssAl);
        
        System.out.println("before insertToSaleMaster call");
        boolean success = q.insertToSaleMaster(sm);
        System.out.println("after insertToSaleMaster call");
        if ( success )
        {
            sm = q.getSaleMaster(salemid);
            
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            
            try {
                String sql = "select ename from Enterprise";
                Statement stm=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet rs=stm.executeQuery(sql);
                boolean chitting_flag = true;
                while(rs.next())
                {
                    String enterprise_name = rs.getString("ename");
                    if (enterprise_name.equalsIgnoreCase("SHREE KRISHNA AGENCY")) {
                        chitting_flag = false;
                    }
                }
                if (chitting_flag) {
                    JOptionPane.showMessageDialog(null,"You are not authorized user!",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Sale ex: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                if (conn != null) 
                {
                    db=null;
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        ex.printStackTrace();
                    }
                }
            }
            
            // Number of columns in ItemRegister: 17
            /* irid, compid, itemid, pknm, pkval, actiondt, refno, description, type, qty, 
            mrp, rate, discper, cgstper, sgstper, igstper, total */
            int irid=q.getMaxId("ItemRegister", "irid");
            // Number of columns in SalePaymentRegister: 8
            /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount */
            int sprid=q.getMaxId("SalePaymentRegister", "sprid");
            
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
                
                // Number of columns in PurchaseSub: 19
                /* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
                cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                String sql3 = "update PurchaseSub set qtysold=qtysold+? where psid=?";
                PreparedStatement psmt3 = conn.prepareStatement(sql3);
                
                for( SaleSub ref : sm.getSsAl() )
                {
                    // Number of columns in ItemRegister: 17
                /* irid, compid, itemid, pknm, pkval, actiondt, refno, description, type, qty, 
                rate, discper, cgstper, sgstper, igstper, total */
                    psmt1.setInt(1, ++irid);
                    psmt1.setInt(2, Integer.parseInt(sm.getCompid()));
                    psmt1.setInt(3, Integer.parseInt(ref.getItemid()));
                    psmt1.setString(4, "salesid");
                    psmt1.setString(5, ref.getSalesid());
                    psmt1.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(saledt)));
                    psmt1.setString(7, salemid);
                    psmt1.setString(8, jComboBox1.getSelectedItem().toString());
                    psmt1.setString(9, "SQ");
                    psmt1.setDouble(10, Double.parseDouble(ref.getQty()));
                    psmt1.setDouble(11, Double.parseDouble(ref.getMrp()));
                    psmt1.setDouble(12, Double.parseDouble(ref.getRate()));
                    psmt1.setDouble(13, Double.parseDouble(ref.getDiscper()));
                    psmt1.setDouble(14, Double.parseDouble(ref.getCgstper()));
                    psmt1.setDouble(15, Double.parseDouble(ref.getSgstper()));
                    psmt1.setDouble(16, Double.parseDouble(ref.getIgstper()));
                    psmt1.setDouble(17, Double.parseDouble(ref.getTotal()));
                    psmt1.addBatch();
                    
                    psmt3.setDouble(1, Double.parseDouble(ref.getQty()));
                    psmt3.setInt(2, Integer.parseInt(ref.getPsid()));
                    psmt3.addBatch();
                }
                psmt1.executeBatch();
                psmt3.executeBatch();
                
                // Number of columns in SalePaymentRegister: 9
                /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount, isactive */
                String sql2 = "insert into SalePaymentRegister (sprid, salemid, pknm, pkval, actiondt,"
                        + " refno, type, amount, isactive) values (?, ?, ?, ?, ?, ?, ?, ?, 1)";
                PreparedStatement psmt2 = conn.prepareStatement(sql2);
                psmt2.setInt(1, ++sprid);
                psmt2.setString(2, salemid);
                psmt2.setString(3, "salemid");
                psmt2.setString(4, salemid);
                psmt2.setDate(5, java.sql.Date.valueOf(DateConverter.dateConverter1(saledt)));
                psmt2.setString(6, salemid);
                psmt2.setInt(7, 0);
                psmt2.setDouble(8, Double.parseDouble(sm.getNetamt02()));
                psmt2.executeUpdate();
                
                conn.commit();
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
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
                    db=null;
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                q.adjustQty();
                formFlush();
            }
        }
    }
    
    private void formFlush() {
        newDistnm = null;
        populateCombo2();
        selectedItemid = null;
        selectedPsid = null;
        avlqty = 0.0;
        ssAl = new ArrayList<SaleSub>();
        onmrpFlag = false;
        purRate = 0.0;
        
        jComboBox3.setEnabled(true);
        jDateChooser1.setEnabled(true);
        jTextField1.setEnabled(true);
        jDateChooser2.setEnabled(true);
        jComboBox1.setEnabled(true);
        jTextField2.setEnabled(true);
        jTextField3.setEnabled(true);
        jTextField4.setEnabled(true);
        jTextField5.setEnabled(true);
        jDateChooser3.setEnabled(true);
        jButton1.setEnabled(true);
    
        jComboBox3.setSelectedIndex(0);
        jDateChooser1.setDate(new Date());
        jTextField1.setText("N/A");
        String sDate1="01/01/2000";  
        Date date1=null;
        try {
            date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        }
        catch (ParseException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Sale ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        jDateChooser2.setDate(date1);
        jComboBox1.setSelectedIndex(0);
        jLabel51.setText("N/A");
        jTextField2.setText("N/A");
        jTextField3.setText("N/A");
        jTextField4.setText("N/A");
        jTextField5.setText("N/A");
        jDateChooser3.setDate(date1);
                
        jComboBox2.removeAllItems();
        jLabel13.setText("N/A");
        jLabel15.setText("0");
        jTextField6.setText("0");
        jTextField7.setText("0");
        jLabel18.setText("0");
        jTextField8.setText("0");
        jLabel20.setText("0");
        jLabel22.setText("0");
        jTextField9.setText("0");
        jLabel24.setText("0");
        jTextField10.setText("0");
        jLabel26.setText("0");
        jTextField11.setEnabled(true);
        jTextField11.setText("0");
        jLabel28.setText("0");
        jLabel29.setText("0");
        clearTable(jTable1);
        
        jLabel31.setText("0");
        jLabel33.setText("0");
        jLabel35.setText("0");
        jLabel37.setText("0");
        jLabel39.setText("0");
        jLabel41.setText("0");
        jLabel43.setText("0");
        
        jTextField12.setText("0");
        jLabel45.setText("0");
        jTextField13.setText("N/A");
        jTextField14.setText("0");
        jTextField15.setText("0");
        jLabel49.setText("0");
        jTextField16.setText("N/A");
        
        jComboBox3.requestFocusInWindow();
    }
    
    private void startBilling() {
        // Validity testing
        if(((String)jComboBox3.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Company!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox3.requestFocusInWindow();
            return;
        }
        if(((String)jComboBox1.getSelectedItem()).equals("-- Select --")) 
        {
            JOptionPane.showMessageDialog(null,"Select The Retailer!","Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Date saleDt =jDateChooser1.getDate();
        String saledt=null;
        try
        {
            saledt=sdf.format(saleDt);
        }
        catch(NullPointerException ex)
        {
            JOptionPane.showMessageDialog(null,"Invalid Sale Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
            jDateChooser1.requestFocusInWindow();
            return;
        }
        String ordno = jTextField1.getText().trim().toUpperCase();
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
        
        // Setting enability to false
        jComboBox3.setEnabled(false);
        jDateChooser1.setEnabled(false);
        jTextField1.setEnabled(false);
        jDateChooser2.setEnabled(false);
        jComboBox1.setEnabled(false);
        jTextField2.setEnabled(false);
        jTextField3.setEnabled(false);
        jTextField4.setEnabled(false);
        jTextField5.setEnabled(false);
        jDateChooser3.setEnabled(false);
        jButton1.setEnabled(false);
        jComboBox2.requestFocusInWindow();
    }
    
    private void addNewPurchase() {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final Purchase ref=new Purchase(jDesktopPane1, up, true);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            probablyNewItem = ref.getProbablyNewItem();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            Sale.this.setVisible(true);
                            populateCombo2();
                            if(probablyNewItem!=null)
                            {
                                jComboBox2.setSelectedItem(probablyNewItem);
                                jComboBox2.requestFocusInWindow();
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
    
    private void rateCalculation() {
        if ( rateformula == null && rateformula.length() == 0 ) {
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
        if ( onmrpFlag ) {
            String smrp = "0";
            try
            {
                smrp = jLabel15.getText().trim().replaceAll(",", "");
                double dmrp = Double.parseDouble(smrp);
            }
            catch (NumberFormatException ex)
            {
                return;
            }
            rateformula = smrp+rateformula.substring(firstOperatorIndex);
        } else {
            // We have already purchase rate as purRate(double)
            rateformula = purRate+rateformula.substring(firstOperatorIndex);
        }
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
        jTextField7.setText(format2afterDecimal.format(temp));
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
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
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
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        jMenuItem1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jMenuItem1.setForeground(new java.awt.Color(255, 0, 0));
        jMenuItem1.setText("## DELETE THIS ITEM ##");
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
        jLabel1.setText("COMPANY");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("INV. DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("ORDER NO.");

        jTextField1.setText("N/A");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("ORD. DATE");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("DISTRIBUTER");

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

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("DELIVERY NOTE");

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

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("PAYMENT NOTE");

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

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("TRASPORTER");

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

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("VEHICLE NO.");

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

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("SUPPLY DT.");

        jDateChooser3.setDateFormatString("dd/MM/yyyy");

        jPanel1.setBackground(new java.awt.Color(229, 242, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ITEM DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("ITEM + <Enter>");

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
        jLabel14.setText("MRP");

        jLabel15.setBackground(new java.awt.Color(255, 255, 0));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("0");
        jLabel15.setOpaque(true);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("QTY.");

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField6.setText("0");
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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("RATE");

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

        jLabel18.setBackground(new java.awt.Color(255, 255, 0));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("0");
        jLabel18.setOpaque(true);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("DISCOUNT %");

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

        jLabel20.setBackground(new java.awt.Color(255, 255, 0));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("0");
        jLabel20.setOpaque(true);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("TAXABLE AMT.");

        jLabel22.setBackground(new java.awt.Color(255, 255, 0));
        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("0");
        jLabel22.setOpaque(true);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("CGST %");

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

        jLabel24.setBackground(new java.awt.Color(255, 255, 51));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("0");
        jLabel24.setOpaque(true);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("SGST %");

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField10.setText("0");
        jTextField10.setEnabled(false);

        jLabel26.setBackground(new java.awt.Color(255, 255, 51));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("0");
        jLabel26.setOpaque(true);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("IGST %");

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField11.setText("0");
        jTextField11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField11FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField11FocusLost(evt);
            }
        });
        jTextField11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField11KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField11KeyReleased(evt);
            }
        });

        jLabel28.setBackground(new java.awt.Color(255, 255, 51));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("0");
        jLabel28.setOpaque(true);

        jLabel29.setBackground(new java.awt.Color(0, 255, 0));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("0");
        jLabel29.setOpaque(true);

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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "ADDED ITEMS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "PUR. NO.", "ITEM", "QTY.", "UOM", "MRP", "RATE", "AMOUNT", "DISC.%", "DISC. AMT.", "TAXABLE", "CGST%", "CGST AMT.", "SGST%", "SGST AMT.", "IGST%", "IGST AMT.", "TOTAL"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("NET QTY.");

        jLabel31.setBackground(new java.awt.Color(255, 255, 0));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("0");
        jLabel31.setOpaque(true);

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setText("NET AMOUNT");

        jLabel33.setBackground(new java.awt.Color(255, 255, 0));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("0");
        jLabel33.setOpaque(true);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("NET TAXABLE");

        jLabel35.setBackground(new java.awt.Color(255, 255, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("0");
        jLabel35.setOpaque(true);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("NET CGST");

        jLabel37.setBackground(new java.awt.Color(255, 255, 0));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("0");
        jLabel37.setOpaque(true);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setText("NET SGST");

        jLabel39.setBackground(new java.awt.Color(255, 255, 0));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("0");
        jLabel39.setOpaque(true);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setText("NET IGST");

        jLabel41.setBackground(new java.awt.Color(255, 255, 0));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("0");
        jLabel41.setOpaque(true);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel42.setText("NET TOTAL");

        jLabel43.setBackground(new java.awt.Color(255, 255, 0));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("0");
        jLabel43.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel42)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 31, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel42)
                        .addComponent(jLabel43))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel30)
                        .addComponent(jLabel31)
                        .addComponent(jLabel32)
                        .addComponent(jLabel33)
                        .addComponent(jLabel34)
                        .addComponent(jLabel35)
                        .addComponent(jLabel36)
                        .addComponent(jLabel37)
                        .addComponent(jLabel38)
                        .addComponent(jLabel39)
                        .addComponent(jLabel40)
                        .addComponent(jLabel41)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel44.setText("ROUND-OFF");

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

        jLabel45.setBackground(new java.awt.Color(255, 255, 0));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("0");
        jLabel45.setOpaque(true);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel46.setText("DISPLAY NOTE");

        jTextField13.setText("N/A");
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
        });

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel47.setText("DISPLAY AMT.");

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

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel48.setText("ADVANCE");

        jTextField15.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField15.setText("0");
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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField15KeyReleased(evt);
            }
        });

        jLabel49.setBackground(new java.awt.Color(0, 255, 0));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("0");
        jLabel49.setOpaque(true);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel50.setText("REMARKS");

        jTextField16.setText("N/A");
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
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("SAVE & PRINT");
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

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton4.setText("ONLY SAVE");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jButton4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton4KeyPressed(evt);
            }
        });

        jLabel51.setBackground(new java.awt.Color(255, 255, 0));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel51.setText("N/A");
        jLabel51.setOpaque(true);

        jComboBox3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox3ItemStateChanged(evt);
            }
        });
        jComboBox3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox3KeyPressed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(0, 255, 0));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("N/A");
        jLabel2.setOpaque(true);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/start01.png"))); // NOI18N
        jButton1.setToolTipText("Start Billing");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel47)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel51)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45)
                    .addComponent(jLabel46)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        String s=jTextField1.getText().trim();
        if(s.equals("N/A")) {
            jTextField1.setText("");
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        String s=jTextField1.getText().trim();
        if(s.length()==0) {
            jTextField1.setText("N/A");
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
        if(evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterDistributer();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

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
            jTextField5.requestFocusInWindow();
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
            jDateChooser3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        try
        {
            if(((String)jComboBox1.getSelectedItem()).equals("-- Select --"))
            {
                jLabel51.setText("N/A");
            }
            else
            {
                jLabel51.setText(beatabbrArray[jComboBox1.getSelectedIndex()-1]);
            }
        }
        catch(NullPointerException ex){}
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        try
        {
            if(((String)jComboBox2.getSelectedItem()).equals("-- Select --"))
            {
                System.out.println("No item selected");
                jLabel13.setText("N/A");
                rateformula = null;
                jTextField7.setText("0");
            }
            else
            {
                System.out.println("Item selected");
                try {
                    jLabel13.setText(hsnArray[jComboBox2.getSelectedIndex()-1]);
                    // Rate Calculation
                    String itemid = "";
                    try {
                        itemid = itemidArray[jComboBox2.getSelectedIndex() - 1];
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        return;
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    // Number of columns in Formula: 5
                    /* formulaid, forsale, onmrp, formula, isactive */
                    // Number of columns in PSRateFormula: 6
                    /* psrfid, itemid, pformulaid, sformulaid, status, isactive */
                    String query="select onmrp, formula from PSRateFormula, Formula where sformulaid="
                            + "formulaid and itemid="+itemid;
                    System.out.println(query);
                    onmrpFlag = false;
                    try {
                        Statement stm=conn.createStatement();
                        ResultSet rs=stm.executeQuery(query);
                        if ( rs.next() ) {
                            onmrpFlag = rs.getString("onmrp").equals("0")?false:true;
                            rateformula = (onmrpFlag?"Rate":"MRP")+rs.getString("formula");
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
                } catch (ArrayIndexOutOfBoundsException ex) {}
            }
        }
        catch(NullPointerException ex){}
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jComboBox2.getSelectedIndex() == 0 ) {
                JOptionPane.showMessageDialog(null,"Select proper Item",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jComboBox2.requestFocusInWindow();
                return;
            }
            selectedItemid = itemidArray[jComboBox2.getSelectedIndex()-1];
            getItemFromPurchase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addNewPurchase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F3)
        {
            jTextField12.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jTextField6FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusGained
        String s=jTextField6.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField6.setText("");
        } else {
            jTextField6.selectAll();
        }
    }//GEN-LAST:event_jTextField6FocusGained

    private void jTextField6FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField6FocusLost
        String s=jTextField6.getText().trim();
        if(s.length()==0) {
            jTextField6.setText("0");
        }
    }//GEN-LAST:event_jTextField6FocusLost

    private void jTextField6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField6KeyReleased

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

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField7KeyReleased

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

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField8KeyReleased

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
            jTextField10.setText("0");
        }
    }//GEN-LAST:event_jTextField9FocusLost

    private void jTextField9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            double val = 0.0;
            try {
                val = Double.parseDouble( jTextField9.getText().trim() );
            } catch ( NumberFormatException ex ) {
                val = 0.0;
            }
            if ( val != 0.0 ) {
                jTextField11.setEnabled(false);
                jButton2.requestFocusInWindow();
            } else {
                jTextField11.setEnabled(true);
                jTextField11.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jTextField9KeyPressed

    private void jTextField9KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyReleased
        jTextField10.setText(jTextField9.getText());
        computation01();
    }//GEN-LAST:event_jTextField9KeyReleased

    private void jTextField11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusGained
        String s=jTextField11.getText().trim();
        if(Double.parseDouble(s) == 0) {
            jTextField11.setText("");
        } else {
            jTextField11.selectAll();
        }
    }//GEN-LAST:event_jTextField11FocusGained

    private void jTextField11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusLost
        String s=jTextField11.getText().trim();
        if(s.length()==0) {
            jTextField11.setText("0");
        }
    }//GEN-LAST:event_jTextField11FocusLost

    private void jTextField11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField11KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField11KeyPressed

    private void jTextField11KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField11KeyReleased
        computation01();
    }//GEN-LAST:event_jTextField11KeyReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        addToList();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            addToList();
        }
    }//GEN-LAST:event_jButton2KeyPressed

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
            jTextField13.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField12KeyPressed

    private void jTextField12KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField12KeyReleased

    private void jTextField13FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField13FocusGained
        String s=jTextField13.getText().trim();
        if(s.equals("N/A")) {
            jTextField13.setText("");
        }
    }//GEN-LAST:event_jTextField13FocusGained

    private void jTextField13FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField13FocusLost
        String s=jTextField13.getText().trim();
        if(s.length()==0) {
            jTextField13.setText("N/A");
        }
    }//GEN-LAST:event_jTextField13FocusLost

    private void jTextField13KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField13KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField14.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField13KeyPressed

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
        if(Double.parseDouble(s) == 0) {
            jTextField15.setText("");
        } else {
            jTextField15.selectAll();
        }
    }//GEN-LAST:event_jTextField15FocusGained

    private void jTextField15FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField15FocusLost
        String s=jTextField15.getText().trim();
        if(s.length()==0) {
            jTextField15.setText("0");
        }
    }//GEN-LAST:event_jTextField15FocusLost

    private void jTextField15KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField15KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField16.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField15KeyPressed

    private void jTextField15KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField15KeyReleased
        computation02();
    }//GEN-LAST:event_jTextField15KeyReleased

    private void jTextField16FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField16FocusGained
        String s=jTextField16.getText().trim();
        if(s.equals("N/A")) {
            jTextField16.setText("");
        }
    }//GEN-LAST:event_jTextField16FocusGained

    private void jTextField16FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField16FocusLost
        String s=jTextField16.getText().trim();
        if(s.length()==0) {
            jTextField16.setText("N/A");
        }
    }//GEN-LAST:event_jTextField16FocusLost

    private void jTextField16KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField16KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField16KeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        insertToDatabase();
//        new printSaleBill(sm, e, q.getDistributer(sm.getDistid()));
        // Printing Selection
        try
        {
            ReprintSaleBillSub ref=new ReprintSaleBillSub(sm, e, q.getDistributer(sm.getDistid()));
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
        sm = null;
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
//            new printSaleBill(sm, e, q.getDistributer(sm.getDistid()));
            // Printing Selection
            try
            {
                ReprintSaleBillSub ref=new ReprintSaleBillSub(sm, e, q.getDistributer(sm.getDistid()));
                ref.setVisible(true);
                jDesktopPane1.add(ref);
                ref.show();
                ref.setIcon(false);
                ref.setSelected(true);
            }
            catch(PropertyVetoException e){}
            sm = null;
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            jButton4.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
        }
        if (evt.getKeyCode() == KeyEvent.VK_LEFT)
        {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton4KeyPressed

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
                    ssAl.remove(row);
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

    private void jComboBox3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox3KeyPressed

    private void jComboBox3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox3ItemStateChanged
        if ( jComboBox3.getSelectedIndex() != 0 ) {
            jLabel2.setText(getNextSaleInvoiceNo());
            populateCombo2();
        } else {
            jLabel2.setText("N/A");
            jComboBox2.removeAllItems();
        }
    }//GEN-LAST:event_jComboBox3ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        startBilling();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            startBilling();
        }
    }//GEN-LAST:event_jButton1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
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
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
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
