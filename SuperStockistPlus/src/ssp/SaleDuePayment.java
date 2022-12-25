package ssp;

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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import query.Query;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

public class SaleDuePayment extends javax.swing.JInternalFrame implements AWTEventListener
{

    private Settings settings=new Settings();
    private Query q=new Query();
    private DecimalFormat df2 = new DecimalFormat("###.##");
    
    private String distidArray[];
    private String selectedDistid;
    private String previnfo[];
    private double dnetpruchaseamt;
    private double dnetpaid;
    private double dnetdue;
    private String pmmidAr[];
    private boolean billwise=true;
    private boolean cyclic=false;
    private ArrayList<String> salemidAl;
    private String currentSalemid;

    /** Creates new form SaleDueRayment */
    public SaleDuePayment() {
        super("Sale Due Payment",false,true,false,true);
        initComponents();
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-45);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/payments.png")));
        
        /******************When <ESC> is pressed in this window****************/
        this.getActionMap().put("test", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        InputMap map = this.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        map.put(stroke,"test");  

        populateCombo1();
        populateCombo2();

        fillBlank(false);

        settings.numvalidatorFloat(jTextField1);

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
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((DefaultTableCellRenderer)jTable2.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jRadioButton1.requestFocusInWindow();
                }
            }
        );
    }
    
    @Override
    public void eventDispatched(AWTEvent event) {
        if(event instanceof KeyEvent){
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED)
            {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10)
                {
                    jComboBox2.requestFocusInWindow();
                }                          
            }
        }
    }

    private void fillBlank(boolean flag)
    {
        selectedDistid=null;
        clearTable(jTable1);
        jLabel5.setText("0");
        jLabel6.setText("0");
        jLabel8.setText("0");
        clearTable(jTable2);
        jLabel11.setText("0");
        jTextField1.setText("0");
        jTextField1.setEnabled(flag);
        jLabel14.setText("0");
        jDateChooser1.setDate(new Date());
        populateCombo2();
        jComboBox2.setSelectedIndex(0);
        jComboBox2.setEnabled(flag);
        jTextField3.setText("N/A");
        jTextField3.setEnabled(flag);
        jButton3.setEnabled(flag);
    }

    private void populateCombo1()// Distributer
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
                jComboBox1.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    distidArray[i]=rs.getString("distid");
                    jComboBox1.addItem(rs.getString("distnm")+" ["+rs.getString("beatabbr")+"]");
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
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

    private void populateCombo2() // Payment Modes
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in PaymentModeMaster: 4
        /* pmmid, pmnm, isactive, remarks */
        String query="select pmmid, pmnm from PaymentModeMaster order by pmnm asc";
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
            pmmidAr=new String[total];
            if(total != 0)
            {
                int i=0;
                jComboBox2.addItem("-- Select --");
                while(rs.next())
                {
                    if(rs.getString("pmnm").equals("N/A"))
                        continue;
                    pmmidAr[i]=rs.getString("pmmid");
                    jComboBox2.addItem(rs.getString("pmnm"));
                    i++;
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
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

    private void Fetch1()
    {
        if ( selectedDistid == null ) {
            return;
        }
        dnetpruchaseamt=0.0;
        dnetpaid=0.0;
        dnetdue=0.0;
        
        // PURCHASE HISTORY OF CUSTOMER WITH DUW :: ~
        // NO. OF COLUMNS: 6
        /* SLN., INVOICE NO., INVOICE DATE, TOTAL AMT., AMT. PAID, AMT. DUE */
        
        int slno1=0;
        clearTable(jTable1);
        
        // Number of columns in SaleMaster: 27
        /* salemid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno,
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks, compid */
        String query="select salemid, saledt, netamt02, amtpaid from SaleMaster where "
                + "amtpaid < netamt02 and isactive=1 and distid='"+selectedDistid
                + "' order by saledt";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=null;
        salemidAl=new ArrayList<String>();
        int total=0;
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
                previnfo=new String[total];
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    // salemid, saledt, netamt02, amtpaid
                    String salemid=rs.getString("salemid");
                    salemidAl.add(salemid);
                    previnfo[i]=salemid;
                    row.addElement(salemid);
                    row.addElement(DateConverter.dateConverter(rs.getString("saledt")));
                    String netamt02=rs.getString("netamt02");
                    previnfo[i]+="@"+netamt02;
                    dnetpruchaseamt+=Double.parseDouble(netamt02);
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(netamt02)));
                    String amtpaid=rs.getString("amtpaid");
                    previnfo[i]+="@"+amtpaid;
                    dnetpaid+=Double.parseDouble(amtpaid);
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(amtpaid)));
                    double damtdue = Double.parseDouble(netamt02) - Double.parseDouble(amtpaid);
                    String amtdue = df2.format(damtdue);
                    previnfo[i]+="@"+amtdue;
                    dnetdue+=damtdue;
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(amtdue)));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                    i++;
                    // previnfo=salemid@netamt02@amtpaid@amtdue
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
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
        // NO. OF COLUMNS: 6
        /* SLN., INVOICE NO., INVOICE DATE, TOTAL AMT., AMT. PAID, AMT. DUE */
	jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// Invoice No.
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// Invoice Dt.
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(130);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// Net Billing Amount
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// Amount Paid
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// Amount Due
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(180);

        jLabel5.setText(MyNumberFormat.rupeeFormat(dnetpruchaseamt));
        jLabel6.setText(MyNumberFormat.rupeeFormat(dnetpaid));
        jLabel8.setText(MyNumberFormat.rupeeFormat(dnetdue));
        if(billwise)
        {
            jLabel11.setText("0");
            jLabel14.setText("0");
        }
        else
        {
            jLabel11.setText(MyNumberFormat.rupeeFormat(dnetdue));
            jLabel14.setText(MyNumberFormat.rupeeFormat(dnetdue));
        }

        // align funda
        // NO. OF COLUMNS: 6
        /* SLN., INVOICE NO., INVOICE DATE, TOTAL AMT., AMT. PAID, AMT. DUE */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("INVOICE DATE").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("TOTAL AMT.").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMT. PAID").setCellRenderer( rightRenderer );
        jTable1.getColumn("AMT. DUE").setCellRenderer( rightRenderer );
    }

    private void Fetch2()
    {
        int slno1=0;
        double netPayment=0.0;
        clearTable(jTable2);
        
        // Number of columns in SalePaymentHistory: 8
        /* sphid, autorefno, distid, salemid, paydt, pmmid, payamt, paynote */
        // Number of columns in PaymentModeMaster: 5
        /* pmmid, pmnm, isactive, remarks, field1 */
        // Number of columns in SaleMaster: 27
	/* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
	supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
	netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
        String query="select SalePaymentHistory.paydt as paydt, salemid, PaymentModeMaster.pmnm as mode, "
                + "payamt, SalePaymentHistory.paynote as paynote from "
                + "SalePaymentHistory, SaleMaster, PaymentModeMaster where SalePaymentHistory"
                + ".pmmid=PaymentModeMaster.pmmid and SalePaymentHistory.salemid=SaleMaster.salemid and "
                + "SalePaymentHistory.distid="+selectedDistid+" order by SalePaymentHistory.paydt asc,"
                + "SalePaymentHistory.sphid asc";
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
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    row.addElement(DateConverter.dateConverter(rs.getString("paydt")));
                    row.addElement(rs.getString("salemid"));
                    row.addElement(rs.getString("mode"));
                    String spayamt=rs.getString("payamt");
                    netPayment+=Double.parseDouble(spayamt);
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(spayamt)));
                    row.addElement(rs.getString("paynote"));
                    ((DefaultTableModel)jTable2.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
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
        // NO. OF COLUMNS: 6
        /* SLN., PAYMENT DATE, INVOICE NO., PAYMENT MODE, PAYMENT AMT., REMARKS */
	jTable2.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable2.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(1).setMinWidth(0);// PAYMENT DATE
	jTable2.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(2).setMinWidth(0);// INVOICE NO.
	jTable2.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(3).setMinWidth(0);// PAYMENT MODE
	jTable2.getColumnModel().getColumn(3).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(4).setMinWidth(0);// PAYMENT AMT.
	jTable2.getColumnModel().getColumn(4).setPreferredWidth(170);
        jTable2.getColumnModel().getColumn(5).setMinWidth(0);// REMARKS
	jTable2.getColumnModel().getColumn(5).setPreferredWidth(340);

        jLabel19.setText(MyNumberFormat.rupeeFormat(netPayment));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable2.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable2.getColumn("PAYMENT DATE").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable2.getColumn("PAYMENT AMT.").setCellRenderer( rightRenderer );
    }
    
    private void insertToDatabase()
    {
        if(selectedDistid==null)
        {
            JOptionPane.showMessageDialog(null,"Invalid Distributer Selection !!!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String snowpaying=jTextField1.getText().trim();
        if(snowpaying.equals("0"))
        {
            JOptionPane.showMessageDialog(null,"Give Valid Payment Amount!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField1.requestFocusInWindow();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date payDate=jDateChooser1.getDate();
        String paydt=null;
        try
        {
            paydt=sdf.format(payDate);
        }
        catch(NullPointerException ex)
        {
            jDateChooser1.setDate(new Date());
        }
        if(jComboBox2.getSelectedIndex()==0)
        {
            JOptionPane.showMessageDialog(null,"Select Proper Payment Mode!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        String pmmid=pmmidAr[jComboBox2.getSelectedIndex()-1];
        String remarks=jTextField3.getText().trim().toUpperCase().replace("'", "\\'");
        BigDecimal dnowpaying=new BigDecimal(snowpaying);
        
        int sphid=q.getMaxId("SalePaymentHistory", "sphid");
        int autorefno=q.getMaxId("SalePaymentHistory", "autorefno");
        autorefno++;
            
        if(cyclic)
        {
            int i=0;
            /*
            * @return -1, 0, or 1 as this {@code BigDecimal} is numerically
 *          less than, equal to, or greater than {@code val}.
            */
            while(dnowpaying.compareTo(BigDecimal.valueOf(0)) == 1)
            {
                System.out.println("Remaining Amt. : "+dnowpaying);
                // previnfo=salemid@netamt02@amtpaid@amtdue
                String x[]=previnfo[i++].split("@");
                String salemid=x[0];
                String snetamt02=x[1];
                double dnetamt02=Double.parseDouble(snetamt02);
                String samtpaid=x[2];
                double damtpaid=Double.parseDouble(samtpaid);
                String samtdue=x[3];
                BigDecimal damtdue=new BigDecimal(samtdue);
                if(damtdue.compareTo(BigDecimal.valueOf(0)) ==  0)
                {
                    continue;
                }
                BigDecimal damt4thisinv=new BigDecimal("0");
                if(dnowpaying.compareTo(damtdue) <= 0)
                {
                    damt4thisinv=dnowpaying;
                    dnowpaying=BigDecimal.valueOf(0);
                }
                else
                {
                    damt4thisinv=damtdue;
                    dnowpaying=dnowpaying.subtract(damtdue);
                }

                // DATABASE UPDATES & INSERTS
                // Step 01: Updating SaleMaster (Multitimes)
                
                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();
                // Number of columns in SaleMaster: 27
                /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
                supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
                netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
                String query="update SaleMaster set amtpaid=amtpaid+"+damt4thisinv.toPlainString()
                        + " where salemid='"+salemid+"'";
                try 
                {
                    Statement smt=(Statement) conn.createStatement();
                    smt.executeUpdate(query);
                } 
                catch(SQLException ex)// ex1
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Number of columns in SalePaymentHistory: 8
                /* sphid, autorefno, distid, salemid, paydt, pmmid, payamt, paynote */
                sphid++;
                query="insert into SalePaymentHistory ( sphid, autorefno, distid, salemid, paydt, "
                        + "pmmid, payamt, paynote ) values ("+sphid+","
                        + autorefno+","+selectedDistid+",'"+salemid+"',#"+DateConverter.dateConverter1(paydt)
                        + "#,"+pmmid+","+damt4thisinv.toPlainString()+",'"+remarks+"')";
                System.out.println(query);
                try {

                    Statement smt=conn.createStatement();
                    smt.executeUpdate(query);
                } 
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Step 04: Inserting SalePaymentRegister
                
                // Number of columns in SalePaymentRegister: 9
                /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount, isactive */
                // Number of columns in SaleMaster: 27
                /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
                supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
                netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
                // We have already salemid. So, database query is not required.
                
                int sprid=q.getMaxId("SalePaymentRegister", "sprid");
                sprid++;
                // Number of columns in SalePaymentRegister: 9
                /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount, isactive */
                query="insert into SalePaymentRegister ( sprid, salemid, pknm, pkval, actiondt, "
                        + "refno, type, amount, isactive ) values ("+sprid+",'"+salemid+"','sphid','"
                        + sphid+"',#"+DateConverter.dateConverter1(paydt)+"#,'"+salemid+"',1,"
                        + damt4thisinv.toPlainString()+",1)";
                System.out.println(query);
                try {

                    Statement smt=conn.createStatement();
                    smt.executeUpdate(query);
                } 
                catch(SQLException ex)// ex1
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
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
            }
        }
        else
        {
            // Number of columns in SaleMaster: 27
            /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            String query="update SaleMaster set amtpaid=amtpaid+"+dnowpaying+" where salemid='"+currentSalemid+"'";
            try {

                Statement smt=(Statement) conn.createStatement();
                smt.executeUpdate(query);
            } 
            catch(SQLException ex)// ex1
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"PurchaseDuePayment ex?: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Step 03: Inserting SalePaymentHistory (Only Once)
            // Number of columns in SalePaymentHistory: 8
            /* sphid, autorefno, distid, salemid, paydt, pmmid, payamt, paynote */
            sphid++;
            query="insert into SalePaymentHistory ( sphid, autorefno, distid, salemid, paydt,"
                    + " pmmid, payamt, paynote) values ("+sphid+","+autorefno+","
                    + selectedDistid+",'"+currentSalemid+"',#"+DateConverter.dateConverter1(paydt)
                    + "#,"+pmmid+","+dnowpaying+",'"+remarks+"')";
            System.out.println(query);
            try {

                Statement smt=conn.createStatement();
                smt.executeUpdate(query);
            } 
            catch(SQLException ex)// ex1
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Step 04: Inserting SalePaymentRegister
                
            // Number of columns in SalePaymentRegister: 9
            /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount, isactive */
            // Number of columns in SaleMaster: 27
            /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
            // We have already "salemid" (SaleMaster ID). So, no more need to use database query.
            
            int sprid=q.getMaxId("SalePaymentRegister", "sprid");
            sprid++;
            // Number of columns in SalePaymentRegister: 9
            /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount, isactive */
            query="insert into SalePaymentRegister ( sprid, salemid, pknm, pkval, actiondt, "
                    + "refno, type, amount, isactive ) values ("+sprid+",'"+currentSalemid+"','sphid','"
                    + sphid+"',#"+DateConverter.dateConverter1(paydt)+"#,'"+currentSalemid+"',1,"+dnowpaying+",1)";
            System.out.println(query);
            try {

                Statement smt=conn.createStatement();
                smt.executeUpdate(query);
            } 
            catch(SQLException ex)// ex1
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"SaleDuePayment ex?: "+ex.getMessage(),
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
        }
        
        fillBlank(false);
        jComboBox1.setSelectedIndex(0);
        jComboBox1.requestFocusInWindow();
        billwise=true;
        cyclic=false;
        buttonGroup1.clearSelection();
        jRadioButton1.setSelected(true);
        jLabel22.setText("N/A");
        jLabel24.setText("N/A");
        currentSalemid=null;
        
        jRadioButton1.requestFocusInWindow();
    }
    
    private void keyRelease()
    {
        int row=jTable1.getSelectedRow();
        currentSalemid=salemidAl.get(row);
        jLabel24.setText(jComboBox1.getSelectedItem().toString());
        jLabel22.setText((String)jTable1.getModel().getValueAt(row, 1));
        jLabel11.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(jTable1.getModel().getValueAt(row, 5).toString().replaceAll(",", ""))));
        jLabel14.setText(MyNumberFormat.rupeeFormat(Double.parseDouble(jTable1.getModel().getValueAt(row, 5).toString().replaceAll(",", ""))));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();

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
        jLabel1.setText("SELECT DISTRIBUTER");

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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "~ :: PURCHASE HISTORY OF SUPPLIER / VENDOR WITH DUE :: ~", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "INVOICE NO.", "INVOICE DATE", "TOTAL AMT.", "AMT. PAID", "AMT. DUE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "~ :: PAYMENT HISTORY OF PAYMENT TO VENDORWISE AGAINST INDIVIDUAL PURCHASE :: ~", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "PAYMENT DATE", "INVOICE NO.", "PAYMENT MODE", "PAYMENT AMT.", "REMARKS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("AMOUNT DUE AS YET");

        jLabel11.setBackground(new java.awt.Color(255, 255, 0));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("0");
        jLabel11.setOpaque(true);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("NOW PAYING");

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField1.setText("0");
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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("AMOUNT DUE AFTER PAYMENT");

        jLabel14.setBackground(new java.awt.Color(255, 255, 0));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("0");
        jLabel14.setOpaque(true);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("PAYMENT DATE");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("PAYMENT MODE");

        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("PAYMENT NOTE (IF ANY)");

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

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("SUBMIT");
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

        jDateChooser1.setBackground(new java.awt.Color(255, 0, 51));
        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField3)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel11)
                        .addComponent(jLabel12)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(jLabel14)
                        .addComponent(jLabel15))
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("NET PURCHASE AMOUNT");

        jLabel5.setBackground(new java.awt.Color(255, 255, 0));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("0");
        jLabel5.setOpaque(true);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("NET AMOUNT PAID");

        jLabel6.setBackground(new java.awt.Color(255, 255, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("0");
        jLabel6.setOpaque(true);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("NET AMOUNT DUE");

        jLabel8.setBackground(new java.awt.Color(255, 255, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("0");
        jLabel8.setOpaque(true);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("TOTAL PAYMENT AMOUNT");

        jLabel19.setBackground(new java.awt.Color(0, 255, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("0");
        jLabel19.setOpaque(true);

        jLabel20.setBackground(new java.awt.Color(153, 255, 255));
        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("SELECT PAYMENT MODE");
        jLabel20.setOpaque(true);

        jRadioButton1.setBackground(new java.awt.Color(153, 255, 255));
        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("BILLWISE PAYMENT");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });
        jRadioButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton1KeyPressed(evt);
            }
        });

        jRadioButton2.setBackground(new java.awt.Color(153, 255, 255));
        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton2.setText("CYCLIC PAYMENT");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });
        jRadioButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton2KeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("INVOICE NO.");

        jLabel22.setBackground(new java.awt.Color(0, 255, 0));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("N/A");
        jLabel22.setOpaque(true);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("SUPPLIER NAME");

        jLabel24.setBackground(new java.awt.Color(0, 255, 0));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("N/A");
        jLabel24.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton2)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(jLabel19)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        // When pressing F2 on jComboBox1
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if ( jRadioButton1.isSelected() ) {
                if (jTable1.getRowCount() !=0)
                {
                    jTable1.changeSelection(0, 0, false, false);
                    jTable1.requestFocusInWindow();
                }
            } else {
                jTextField1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        // Now Paying Focus Gain
        String s=jTextField1.getText().trim();
        if(s.equals("0"))
        {
            jTextField1.setText("");
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        // Now Paying Focus Lost
        String s=jTextField1.getText().trim();
        if(s.length()==0) {
            jTextField1.setText("0");
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        // When Now Paying value is being typed
        String sdue=jLabel11.getText().replaceAll(",", "");
        double ddue=Double.parseDouble(sdue);
        String spaying=jTextField1.getText().trim();
        double dpaying=0.0;
        try
        {
            dpaying=Double.parseDouble(spaying);
        }
        catch(NumberFormatException ex){ return; }
        if(dpaying>ddue)
        {
            JOptionPane.showMessageDialog(null,"Current Payment can't be > Existing Due!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField1.setText(spaying.substring(0,spaying.length()-1));
            jTextField1.requestFocusInWindow();
            return;
        }
        double ddueremain=ddue-dpaying;
        jLabel14.setText(MyNumberFormat.rupeeFormat(ddueremain));
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // SUBMIT Button
        insertToDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusGained
        // Payment Note Focus Gain
        String s=jTextField3.getText().trim();
        if(s.equals("N/A"))
        {
            jTextField3.setText("");
        }
    }//GEN-LAST:event_jTextField3FocusGained

    private void jTextField3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField3FocusLost
        // Payment Note Focus Lost
        String s=jTextField3.getText().trim();
        if(s.length()==0) {
            jTextField3.setText("N/A");
        }
    }//GEN-LAST:event_jTextField3FocusLost

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        // When pressing F2 on jComboBox2
        if (evt.getKeyCode() == KeyEvent.VK_F2) {
            jComboBox2.showPopup();
        }
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if(jComboBox1.getSelectedIndex()!=0)
        {
            fillBlank(true);
            selectedDistid=distidArray[jComboBox1.getSelectedIndex()-1];
            Fetch1();
            Fetch2();           
        }
        else
        {
            jComboBox1.requestFocusInWindow();
            clearTable(jTable1);
            clearTable(jTable2);
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            jButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insertToDatabase();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        if(jRadioButton1.isSelected())
        {
            billwise=true;
            cyclic=false;
            Fetch1();
        }        
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        if(jRadioButton2.isSelected())
        {
            billwise=false;
            cyclic=true;
            jLabel22.setText("N/A");
            jLabel24.setText("N/A");
            currentSalemid=null;
            Fetch1();
        }  
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        if(billwise)
            keyRelease();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        if(billwise)
            keyRelease();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) 
        {
            jTextField1.requestFocusInWindow();
            evt.consume();
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2) 
        {
            jTextField1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jRadioButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) 
        {
            jRadioButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton1KeyPressed

    private void jRadioButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) 
        {
            jComboBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton2KeyPressed

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

}
