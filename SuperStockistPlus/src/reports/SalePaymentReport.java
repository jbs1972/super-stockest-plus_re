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

public class SalePaymentReport extends javax.swing.JInternalFrame implements AWTEventListener {

    private StringBuilder dtr;
    private DecimalFormat format = new DecimalFormat("0.#");
    
    private String distidArray[];
    
    public SalePaymentReport() {
        super("Sale Payment Report",false,true,false,true);
        initComponents();
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/report05.png")));
        
        this.getActionMap().put("test", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e) 
            {
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
        
        jDateChooser1.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        jDateChooser2.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() 
        {
            @Override
            public void focusGained(FocusEvent evt) 
            {
                ((JTextFieldDateEditor)evt.getSource()).selectAll();
            }
        });
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        populateCombo1();
        Fetch();
        
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
    
    public void eventDispatched(AWTEvent event) 
    {
        if(event instanceof KeyEvent)
        {
            KeyEvent key = (KeyEvent)event;
            if(key.getID()==KeyEvent.KEY_PRESSED)
            {
                if(event.getSource().equals(jDateChooser1.getDateEditor())&&key.getKeyCode()==10)
                {
                    jDateChooser2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10)
                {
                    jButton1.requestFocusInWindow();
                }
            }
        }
    }
    
    private void populateCombo1() // Distributer
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, dpin, 
        dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Number of columns in BeatMaster: 4
	/* beatid, beatnm, beatabbr, isactive */
        String query="select distid, distnm from Distributer where isactive=1 order by distnm asc";
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
                jComboBox1.addItem("-- All Distributers --");
                int i=0;
                while(rs.next())
                {
                    distidArray[i++]=rs.getString("distid");
                    jComboBox1.addItem(rs.getString("distnm"));
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SalePaymentReport ex?: "+ex.getMessage(),
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
        dtr=new StringBuilder("");
        double remaining = 0.0;
        
        String fromdt="",todt="";
        String a="", d="", e="";
        if ( jComboBox1.getSelectedIndex() > 0 )
        {
            a = " where distid="+distidArray[jComboBox1.getSelectedIndex() - 1];
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(jDateChooser1.getDate()!=null)
        {
            Date fromDt=jDateChooser1.getDate();
            try
            {
                fromdt=sdf.format(fromDt);
            }
            catch(NullPointerException ex)
            {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser1.setDate(new Date());
                jDateChooser1.requestFocusInWindow();                
                return;
            }
        }
        if(jDateChooser1.getDate()!=null)
        {
            d=" and actiondt >= #"+DateConverter.dateConverter1(fromdt)+"#";
            dtr.append("From Date: "+fromdt);
        }
        else
            return;        
        if(jDateChooser2.getDate()!=null)
        {
            Date toDt=jDateChooser2.getDate();
            try
            {
                todt=sdf.format(toDt);
            }
            catch(NullPointerException ex)
            {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser2.setDate(new Date());
                jDateChooser2.requestFocusInWindow();                
                return;
            }
        }
        if(jDateChooser2.getDate()!=null)
        {
            e=" and actiondt <= #"+DateConverter.dateConverter1(todt)+"#";
            dtr.append("  To Date: "+todt);
        }
        else
            return;
        
        // No. Of Columns: 6
        /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
        clearTable(jTable1);
        // Number of columns in SaleMaster: 27
        /* salemid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks, compid */
        // Number of columns in SalePaymentRegister: 8
        /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount */
        /*
        sprid	salemid			pknm	pkval			actiondt	refno			type	amount
        1 	SUVG/000001/17-18 	salemid SUVG/000001/17-18 	21-07-2017 	SUVG/000001/17-18 	0 	30509
        65	SUVG/000001/17-18	sphid	1                       10-08-2017	SUVG/000001/17-18	1	30509
        */
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        String query="select distnm, pkval, type, actiondt, amount from "
                + "(select sprid, salemid, pkval, actiondt, type, amount from SalePaymentRegister where isactive=1"+d+e+") x, "
                + "(select salemid, distid from SaleMaster"+a+") y, "
                + "(select distid, distnm from Distributer) z "
                + "where x.salemid=y.salemid and y.distid=z.distid "
                + "order by actiondt, sprid";
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
                Vector<String> row1 = new Vector<String>();
                // No. Of Columns: 6
                /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
                row1.addElement("SLN.");
                row1.addElement("DISTRIBUTER");
                row1.addElement("DESCRIPTION");
                row1.addElement("DATE");
                row1.addElement("DUE");
                row1.addElement("PAYMENT");
                ((DefaultTableModel)jTable1.getModel()).addRow(row1);
                
                // OPENING BALANCE
                int slno1 = 0;
                Vector<String> row2 = new Vector<String>();
                row2.addElement(++slno1+""); // SLN.
                row2.addElement("OPENING BALANCE"); // DISTRIBUTER
                row2.addElement("-");
                row2.addElement(fromdt);
                // No. Of Columns: 6
                /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
                // Number of columns in SalePaymentRegister: 8
                /* sprid, salemid, pknm, pkval, actiondt, refno, type, amount */
                /*
                sprid	salemid			pknm	pkval			actiondt	refno			type	amount
                1 	SUVG/000001/17-18 	salemid SUVG/000001/17-18 	21-07-2017 	SUVG/000001/17-18 	0 	30509
                65	SUVG/000001/17-18	sphid	1                       10-08-2017	SUVG/000001/17-18	1	30509
                */
                // Number of columns in SaleMaster: 27
                /* salemid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
                supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
                netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks, compid */
                query = "select type, amount from (select sprid, salemid, actiondt, type, amount from SalePaymentRegister"
                        + " where actiondt <= #"+DateConverter.dateConverter1(fromdt)+"#) x, "
                        + "(select salemid from SaleMaster"+a+") y "
                        + "where x.salemid=y.salemid order by actiondt, sprid";
                System.out.println(query);
                Statement smt1=conn.createStatement();
                ResultSet rs1=smt1.executeQuery(query);
                while ( rs1.next() )
                {
                    int itype = Integer.parseInt(rs1.getString("type"));
                    if ( itype == 0 ) // Sale
                    {
                        double damount = Double.parseDouble(rs1.getString("amount"));
                        remaining += damount;
                    } 
                    else // Payment
                    {
                        double damount = Double.parseDouble(rs1.getString("amount"));
                        remaining -= damount;
                    }
                }
                if ( remaining > 0.0 ) {
                    row2.addElement(MyNumberFormat.rupeeFormat(remaining)); // DUE
                    row2.addElement("-"); // PAYMENT
                } else {
                    if ( remaining < 0.0 ) {
                        row2.addElement("-"); // DUE
                        row2.addElement(MyNumberFormat.rupeeFormat(Math.abs(remaining))); // PAYMENT
                    } else {
                        row2.addElement("0"); // DUE
                        row2.addElement("0"); // PAYMENT
                    }
                }
                ((DefaultTableModel)jTable1.getModel()).addRow(row2);
                
        	while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    // No. Of Columns: 6
                    /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
                    /* distnm, pkval, type, actiondt, amount */
                    row.addElement(rs.getString("distnm").replace("\\'", "'"));
                    int itype = Integer.parseInt(rs.getString("type"));
                    if ( itype == 0 ) // Sale
                    {
                        row.addElement(rs.getString("pkval"));
                    } 
                    else // Payment
                    {
                        // Number of columns in SalePaymentHistory: 8
                        /* sphid, autorefno, distid, salemid, paydt, pmmid, payamt, paynote */
                        /*
                        sphid	autorefno	distid	salemid			paydt		pmmid	payamt	paynote
                        1	1		2	SUVG/000001/17-18	10-08-2017	4	30509	NEFT1234
                        */
                        // Number of columns in PaymentModeMaster: 5
                        /* pmmid, pmnm, isactive, remarks, field1 */
                        query = "select pmnm, paynote from "
                                + "(select pmmid, paynote from SalePaymentHistory where sphid="+rs.getString("pkval")+") x, "
                                + "(select pmmid, pmnm from PaymentModeMaster) y "
                                + "where x.pmmid=y.pmmid";
                        System.out.println(query);
                        Statement smt2=conn.createStatement();
                        ResultSet rs2=smt2.executeQuery(query);
                        if ( rs2.next() )
                        {
                            try
                            {
                                row.addElement(rs2.getString("pmnm")+" -> "+rs2.getString("paynote"));
                            }
                            catch ( Exception ex ) {}
                        }
                    }
                    row.addElement(DateConverter.dateConverter(rs.getString("actiondt")));
                    if ( itype == 0 ) // Sale
                    {
                        double damount = Double.parseDouble(rs.getString("amount"));
                        row.addElement(MyNumberFormat.rupeeFormat(damount));
                        row.addElement("-");
                        remaining += damount;
                    }
                    else // Payment
                    {
                        row.addElement("-");
                        double damount = Double.parseDouble(rs.getString("amount"));
                        row.addElement(MyNumberFormat.rupeeFormat(damount));
                        remaining -= damount;
                    }
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
                
                // No. Of Columns: 6
                /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
                Vector<String> row3 = new Vector<String>();
                row3.addElement("-"); // SLN.
                row3.addElement("BALANCE"); // DISTRIBUTER
                row3.addElement("-"); // DESCRIPTION
                row3.addElement("-"); // DATE
                if ( remaining > 0.0 ) {
                    row3.addElement(MyNumberFormat.rupeeFormat(remaining)); // DUE
                    row3.addElement("-"); // PAYMENT
                } else {
                    if ( remaining < 0.0 ) {
                        row3.addElement("-"); // DUE
                        row3.addElement(MyNumberFormat.rupeeFormat(Math.abs(remaining))); // PAYMENT
                    } else {
                        row3.addElement("0"); // DUE
                        row3.addElement("0"); // PAYMENT
                    }
                }
                row3.addElement("-"); // DUE
                row3.addElement("-"); // PAYMENT
                ((DefaultTableModel)jTable1.getModel()).addRow(row3);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"SalePaymentReport ex?: "+ex.getMessage(),
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

        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        // No. Of Columns: 6
        /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(55);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// DISTRIBUTER
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(300);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// DESCRIPTION
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(300);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// DATE
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// DUE
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// PAYMENT
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        // align funda
        // No. Of Columns: 6
        /* SLN., DISTRIBUTER, DESCRIPTION, DATE, DUE, PAYMENT */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("DISTRIBUTER").setCellRenderer( centerRenderer );
        jTable1.getColumn("DATE").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("DUE").setCellRenderer( rightRenderer );
        jTable1.getColumn("PAYMENT").setCellRenderer( rightRenderer );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("DISTRIBUTER");

        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("FROM DATE");

        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("TO DATE");

        jDateChooser2.setDateFormatString("dd/MM/yyyy");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("GET REPORT");
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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "SALE PAYMENT DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "DISTRIBUTER", "DESCRIPTION", "DATE", "DUE", "PAYMENT"
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
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jDateChooser1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Fetch();
        if ( jTable1.getRowCount() != 0 ) {
            jTable1.changeSelection(0, 0, false, false);
            jTable1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            Fetch();
            if ( jTable1.getRowCount() != 0 ) {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jButton1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
