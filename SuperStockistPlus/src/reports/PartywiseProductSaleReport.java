package reports;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utilities.DateConverter;
import utilities.MyNumberFormat;

public class PartywiseProductSaleReport extends javax.swing.JInternalFrame implements AWTEventListener {

    private StringBuilder dtr;
    private DecimalFormat format = new DecimalFormat("0.#");
    
    private String compidArray[];
    private String distidArray[];
    private String itemidArray[];
    
    public PartywiseProductSaleReport() {
        super("Partywise Product Sale Report",false,true,false,true);
        initComponents();
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/report04.png")));
        
        this.getActionMap().put("test", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        map.put(stroke,"test");
        
        Calendar date = Calendar.getInstance();
        jDateChooser2.setDate(date.getTime());
        date.set(Calendar.DAY_OF_MONTH, 1);
        jDateChooser1.setDate(date.getTime());
        
        jDateChooser1.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        jDateChooser2.getToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);
        
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        jDateChooser2.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox3.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        populateCombo1();
        populateCombo2();
        Fetch();
        
        SwingUtilities.invokeLater (() -> {
            jComboBox1.requestFocusInWindow();
        });
    }
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent) {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED) {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10) {
                    jDateChooser2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10) {
                    jButton1.requestFocusInWindow();
                }
            }
        }
    }
    
    private void populateCombo1() {// Company
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in CompanyMaster: 6
        /* compid, compnm, compabbr, compcontact, compmail, isactive */
        String query="select compid, compnm from CompanyMaster where isactive=1 order by compnm asc";
        System.out.println(query);
        try {
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
            if(total != 0) {
                compidArray=new String[total];
                jComboBox1.addItem("-- All Companies --");
                int i=0;
                while(rs.next()) {
                    compidArray[i++]=rs.getString("compid");
                    jComboBox1.addItem(rs.getString("compnm"));
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PartywiseProductSaleReport ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e) {}
        }
    }
    
    private void populateCombo2() {// Distributer
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, dpin, 
        dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Number of columns in BeatMaster: 4
	/* beatid, beatnm, beatabbr, isactive */
        String query="select distid, distnm from Distributer where isactive=1 order by distnm asc";
        System.out.println(query);
        try {
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
            if(total != 0) {
                distidArray=new String[total];
                jComboBox2.addItem("-- All Distributers --");
                int i=0;
                while(rs.next()) {
                    distidArray[i++]=rs.getString("distid");
                    jComboBox2.addItem(rs.getString("distnm"));
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PartywiseProductSaleReport ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e) {}
        }
    }
    
    private void populateCombo3() {// Item HSN
        if (jComboBox1.getSelectedIndex()<=0) {
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
                + "where qtysold>0) and compid="+compidArray[jComboBox1.getSelectedIndex()-1]
                + ") x, (select icid, hsn from ItemCategory where isactive=1) y where x.icid=y.icid"
                + " order by itemnm";
        System.out.println(query);
        try {
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
            if(total != 0) {
                itemidArray=new String[total];
                jComboBox3.addItem("-- All Products --");
                int i=0;
                while(rs.next()) {
                    itemidArray[i++]=rs.getString("itemid");
                    jComboBox3.addItem(rs.getString("itemnm")+" - "+rs.getString("hsn"));
                }
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PartywiseProductSaleReport ex: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException e) {}
        }
    }
    
    private void clearTable(JTable table) {
        for(int i=table.getRowCount()-1; i>=0; i--) {
            ((DefaultTableModel)table.getModel()).removeRow(i);
        }
    }
    
    private void Fetch() {
        dtr=new StringBuilder("");
        double totqty = 0.0;
        double totmrpamt = 0.0;
        double nettaxableamt = 0.0;
        double net_each_gstamt = 0.0;
        double net_landing_price = 0.0;
        
        String fromdt="",todt="";
        String a="", b="", c="", d="", e="", f="";
        if ( jComboBox1.getSelectedIndex() > 0 ) {
            a = " and compid="+compidArray[jComboBox1.getSelectedIndex() - 1];
        }
        if ( jComboBox2.getSelectedIndex() > 0 ) {
            b = " and distid="+distidArray[jComboBox2.getSelectedIndex() - 1];
        }
        if ( jComboBox3.getSelectedIndex() > 0 ) {
            c = " and itemid="+itemidArray[jComboBox3.getSelectedIndex() - 1];
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(jDateChooser1.getDate()!=null) {
            Date fromDt=jDateChooser1.getDate();
            try {
                fromdt=sdf.format(fromDt);
            } catch(NullPointerException ex) {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser1.setDate(new Date());
                jDateChooser1.requestFocusInWindow();                
                return;
            }
        }
        if(jDateChooser1.getDate()!=null) {
            d=" and saledt >= #"+DateConverter.dateConverter1(fromdt)+"#";
            dtr.append("From Date: "+fromdt);
        } else {
            return;  
        }
        if(jDateChooser2.getDate()!=null) {
            Date toDt=jDateChooser2.getDate();
            try {
                todt=sdf.format(toDt);
            } catch(NullPointerException ex) {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser2.setDate(new Date());
                jDateChooser2.requestFocusInWindow();                
                return;
            }
        }
        if(jDateChooser2.getDate()!=null) {
            e=" and saledt <= #"+DateConverter.dateConverter1(todt)+"#";
            dtr.append("  To Date: "+todt);
        } else {
            return;
        }
        String hsn = jTextField1.getText();
        if(!hsn.isEmpty()) {
            f = " and hsn like '%"+jTextField1.getText().trim()+"%'";
        }
        clearTable(jTable1);
        // Partywise Product Sale Report - 15 Cloumns
        /*
        SLN., RETAILER, INVOICE NO., INVOICE DATE, COMPANY, PRODUCT, HSN, MRP., QTY., 
        NET MRP., TAXABLE AMT., GST %, CGST AMT., SGST AMT., LANDING PRICE
        */
        // Number of columns in SaleMaster: 27
        /* salemid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks, compid */
        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, dpin, 
        dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in CompanyMaster: 6
        /* compid, compnm, compabbr, compcontact, compmail, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        String query="select distnm, x.salemid as salemid, saledt, compnm, itemnm, hsn, mrp, "
                + "qty, taxableamt, cgstper, cgstamt, sgstamt "
                + "from "
                + "(select salemid, saledt, distid from SaleMaster where isactive=1) x, "
                + "(select distid, distnm from Distributer where isactive=1"+b+") y, "
                + "(select salemid, itemid, qty, mrp, taxableamt, cgstper, cgstamt, sgstamt from SaleSub) z, "
                + "(select itemid, itemnm, compid, icid from ItemMaster where isactive=1"+c+") w, "
                + "(select compid, compnm from CompanyMaster where isactive=1"+a+") a, "
                + "(select icid, hsn from ItemCategory where isactive=1"+f+") b "
                + "where x.distid=y.distid "
                + "and x.salemid=z.salemid "
                + "and z.itemid=w.itemid "
                + "and w.compid=a.compid "
                + "and w.icid=b.icid"
                + d+e+" order by salemid, saledt";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        int total=0;
        try {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            //Move to the last record
            rs.afterLast();
            //Get the current record position
            if(rs.previous())
                total = rs.getRow();
            //Move back to the first record;
            rs.beforeFirst();
            if(total != 0) {
                Vector<String> row1 = new Vector<String>();
                // Partywise Product Sale Report - 15 Cloumns
                /*
                SLN., RETAILER, INVOICE NO., INVOICE DATE, COMPANY, PRODUCT, HSN, MRP., QTY., 
                NET MRP., TAXABLE AMT., GST %, CGST AMT., SGST AMT., LANDING PRICE
                */
                row1.addElement("SLN.");
                row1.addElement("DISTRIBUTER");
                row1.addElement("INVOICE NO.");
                row1.addElement("INVOICE DATE");
                row1.addElement("COMPANY");
                row1.addElement("PRODUCT");
                row1.addElement("HSN");
                row1.addElement("MRP.");
                row1.addElement("QTY.");
                row1.addElement("NET MRP.");
                row1.addElement("TAXABLE AMT.");
                row1.addElement("GST %");
                row1.addElement("CGST AMT.");
                row1.addElement("SGST AMT.");
                row1.addElement("LANDING PRICE");
                ((DefaultTableModel)jTable1.getModel()).addRow(row1);
                
                int slno1=0;
                int i=0;
		while(rs.next()) {
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    // Partywise Product Sale Report - 15 Cloumns
                    /*
                    SLN., RETAILER, INVOICE NO., INVOICE DATE, COMPANY, PRODUCT, HSN, MRP., QTY., 
                    NET MRP., TAXABLE AMT., GST %, CGST AMT., SGST AMT., LANDING PRICE
                    */
                    /* distnm, x.salemid as salemid, saledt, compnm, itemnm, hsn, mrp, qty, taxableamt, cgstper, cgstamt, sgstamt, igstper */
                    row.addElement(rs.getString("distnm").replace("\\'", "'"));
                    row.addElement(rs.getString("salemid"));
                    row.addElement(DateConverter.dateConverter(rs.getString("saledt")));
                    row.addElement(rs.getString("compnm").replace("\\'", "'"));
                    row.addElement(rs.getString("itemnm").replace("\\'", "'"));
                    row.addElement(rs.getString("hsn"));
                    double dmrp = Double.parseDouble(rs.getString("mrp"));
                    row.addElement(MyNumberFormat.rupeeFormat(dmrp));
                    double dqty = Double.parseDouble(rs.getString("qty"));
                    totqty += dqty;
                    row.addElement(format.format(dqty));
                    double netmrp = dmrp * dqty;
                    totmrpamt += netmrp;
                    row.addElement(MyNumberFormat.rupeeFormat(netmrp));
                    double taxableamt = Double.parseDouble(rs.getString("taxableamt"));
                    nettaxableamt += taxableamt;
                    row.addElement(MyNumberFormat.rupeeFormat(taxableamt));
                    row.addElement(format.format(Double.parseDouble(rs.getString("cgstper"))*2.0));
                    double cgstamt = Double.parseDouble(rs.getString("cgstamt"));
                    net_each_gstamt += cgstamt;
                    row.addElement(MyNumberFormat.rupeeFormat(cgstamt));
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("sgstamt"))));
                    double landing_price = taxableamt+(cgstamt*2.0);
                    net_landing_price += landing_price;
                    row.addElement(MyNumberFormat.rupeeFormat(landing_price));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
                // Partywise Product Sale Report - 15 Cloumns
                /*
                SLN., RETAILER, INVOICE NO., INVOICE DATE, COMPANY, PRODUCT, HSN, MRP., QTY., 
                NET MRP., TAXABLE AMT., GST %, CGST AMT., SGST AMT., LANDING PRICE
                */
                Vector<String> row2 = new Vector<String>();
                row2.addElement("-");
                row2.addElement("TOTAL");
                row2.addElement("-"); // INVOICE NO.
                row2.addElement("-"); // INVOICE DATE
                row2.addElement("-"); // COMPANY
                row2.addElement("-"); // PRODUCT
                row2.addElement("-"); // HSN
                row2.addElement("-"); // MRP.
                row2.addElement(format.format(totqty)); // QTY.
                row2.addElement(MyNumberFormat.rupeeFormat(totmrpamt)); // NET MRP.
                row2.addElement(MyNumberFormat.rupeeFormat(nettaxableamt)); // NET TAXABLE AMT.
                row2.addElement("-"); // GST %
                row2.addElement(MyNumberFormat.rupeeFormat(net_each_gstamt)); // NET CGST AMT.
                row2.addElement(MyNumberFormat.rupeeFormat(net_each_gstamt)); // NET SGST AMT.
                row2.addElement(MyNumberFormat.rupeeFormat(net_landing_price)); // NET LANDING PRICE
                ((DefaultTableModel)jTable1.getModel()).addRow(row2);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PartywiseProductSaleReport ex: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        // Partywise Product Sale Report - 15 Cloumns
        /*
        SLN., RETAILER, INVOICE NO., INVOICE DATE, COMPANY, PRODUCT, HSN, MRP., QTY., 
        NET MRP., TAXABLE AMT., GST %, CGST AMT., SGST AMT., LANDING PRICE
        */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// RETAILER
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// INVOICE NO.
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// INVOICE DATE
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// COMPANY
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// PRODUCT
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// HSN
	jTable1.getColumnModel().getColumn(6).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// MRP.
	jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
	jTable1.getColumnModel().getColumn(8).setMinWidth(0);// QTY.
	jTable1.getColumnModel().getColumn(8).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// NET MRP.
	jTable1.getColumnModel().getColumn(9).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(10).setMinWidth(0);// TAXABLE AMT.
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(11).setMinWidth(0);// GST %
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// CGST AMT.
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(13).setMinWidth(0);// SGST AMT.
        jTable1.getColumnModel().getColumn(13).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(14).setMinWidth(0);// LANDING PRICE
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(120);
        
        // align funda
        // Partywise Product Sale Report - 15 Cloumns
        /*
        SLN., RETAILER, INVOICE NO., INVOICE DATE, COMPANY, PRODUCT, HSN, MRP., QTY., 
        NET MRP., TAXABLE AMT., GST %, CGST AMT., SGST AMT., LANDING PRICE
        */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("INVOICE NO.").setCellRenderer( centerRenderer );
        jTable1.getColumn("INVOICE DATE").setCellRenderer( centerRenderer );
        jTable1.getColumn("HSN").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("MRP.").setCellRenderer( rightRenderer );
        jTable1.getColumn("QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("NET MRP.").setCellRenderer( rightRenderer );
        jTable1.getColumn("TAXABLE AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("GST %").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("LANDING PRICE").setCellRenderer( rightRenderer );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("COMPANY");

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

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("RETAILER");

        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("PRODUCT");

        jComboBox3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox3KeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("FROM DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("TO DATE");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
        jButton1.setText("SEARCH");
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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "PRODUCT DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "RETAILER", "INVOICE NO.", "INVOICE DATE", "COMPANY", "PRODUCT", "HSN", "MRP.", "QTY.", "NET MRP.", "TAXABLE AMT.", "GST %", "CGST AMT.", "SGST AMT.", "LANDING PRICE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("HSN");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBox2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jComboBox3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jComboBox3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox3KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Fetch();
        if ( jTable1.getRowCount() != 0 ) {
            jTable1.changeSelection(0, 0, false, false);
            jTable1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Fetch();
            if ( jTable1.getRowCount() != 0 ) {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if ( jComboBox1.getSelectedIndex() != 0 ) {
            populateCombo3();
        } else {
            jComboBox3.removeAllItems();
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
