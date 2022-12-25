package ssp;

import com.toedter.calendar.JTextFieldDateEditor;
import conn.dBConnection;
import dto.Enterprise;
import dto.SaleMaster;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import print.printSaleBill;
import query.Query;
import utilities.DateConverter;
import utilities.MyNumberFormat;
import utilities.Settings;

/**
 *
 * @author Jayanta B. Sen
 */
public class ReprintSaleBill extends javax.swing.JInternalFrame implements AWTEventListener{

    private Settings settings=new Settings();
    private DecimalFormat df2 = new DecimalFormat("###.##");
    private DecimalFormat df3 = new DecimalFormat("###.###");
    private Query q=new Query();
    private JDesktopPane jDesktopPane;
    private Enterprise e;
    
    private String salemidArray[];
    private String currentSalemid;
    private SaleMaster sm;
    
    /**
     * Creates new form ReprintSaleBill
     */
    public ReprintSaleBill(JDesktopPane jDesktopPane, Enterprise e) {
        super("Reprint Sale Bill",false,true,false,true);
        initComponents();
        this.jDesktopPane=jDesktopPane;
        this.e = e;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/PRINT.PNG")));
        
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
        
        Fetch();
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jDateChooser1.requestFocusInWindow();
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
                    jDateChooser2.requestFocusInWindow();
                }
                if(event.getSource().equals(jDateChooser2.getDateEditor())&&key.getKeyCode()==10)
                {
                    jTextField1.requestFocusInWindow();
                }
            }
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
        String fromdt = "", todt = "", x = "", y = "", b = "", c = "", d="", e="";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(jDateChooser1.getDate()!=null)
        {
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
                    JOptionPane.showMessageDialog(null,"Invalid From Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
                    jDateChooser1.setDate(null);
                    jDateChooser1.requestFocusInWindow();                
                    return;
                }
            }
            if(jDateChooser1.getDate()!=null)
            {
                x=" and saledt >= #"+DateConverter.dateConverter1(fromdt)+"#";
            }
            else
                return;
        }
        if(jDateChooser2.getDate()!=null)
        {
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
                    JOptionPane.showMessageDialog(null,"Invalid From Date.","Invalid Date",JOptionPane.ERROR_MESSAGE);
                    jDateChooser2.setDate(null);
                    jDateChooser1.requestFocusInWindow();      
                    return;
                }
            }
            if(jDateChooser2.getDate()!=null)
            {
                y=" and saledt <= #"+DateConverter.dateConverter1(todt)+"#";
            }
            else
                return;
        }
        String salemid=jTextField1.getText().trim().toUpperCase();
        if(salemid.length() != 0)
        {
             b=" and salemid like '%"+salemid+"%'";
        }
        String beatabbr=jTextField2.getText().trim().toUpperCase();
        if(beatabbr.length() != 0)
        {
             c=" and beatabbr like '"+beatabbr+"%'";
        }
        String distnm=jTextField3.getText().trim().toUpperCase();
        if(distnm.length() != 0)
        {
             d=" and distnm like '%"+distnm+"%'";
        }
        if ( x.length()==0 && y.length()==0 && b.length()==0 && c.length()==0 && 
                d.length()==0 && jCheckBox1.isSelected()==false ) {
            e = " top 22";
        }

        // NO. OF COLUMNS: 7
        /* SLN., BILL NO., BILL DATE, BEAT, LINES, RETAILER, BILL VALUE */
        
        clearTable(jTable1);
        
        // Number of columns in SaleMaster: 27
	/* salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
	supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
	netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
        // Number of columns in BeatMaster: 4
	/* beatid, beatnm, beatabbr, isactive */
        // Number of columns in Distributer: 18
	/* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
	dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String query="select x.salemid, saledt, beatabbr, totlines, distnm, netamt02, mrpval from "
                + "(select"+e+" salemid, distid, saledt, netamt02 from SaleMaster  where isactive=1"+x+y+b
                + " order by saledt desc) x, (select distid, distnm, beatid from Distributer where isactive=1"
                + c+d+") y, (select beatid, beatabbr from BeatMaster where isactive=1"+c+") z, (select salemid,"
                + " count(salesid) as totlines, sum(mrp*qty) as mrpval from SaleSub group by salemid) a "
                + "where x.distid=y.distid and y.beatid=z.beatid and x.salemid=a.salemid order by "
                + "saledt desc, x.salemid desc";
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
                double totbillval = 0.0;
                double totmrpval = 0.0;
                // NO. OF COLUMNS: 7
        /* SLN., BILL NO., BILL DATE, BEAT, LINES, RETAILER, BILL VALUE */
                Vector<String> rowperv = new Vector<String>();
                rowperv.addElement("SLN.");
                rowperv.addElement("BILL NO.");
                rowperv.addElement("BILL DATE");
                rowperv.addElement("BEAT");
                rowperv.addElement("LINES");
                rowperv.addElement("RETAILER");
                rowperv.addElement("BILL VALUE");
                rowperv.addElement("MRP VALUE");
                ((DefaultTableModel)jTable1.getModel()).addRow(rowperv);
                salemidArray = new String[total];
                int slno1=0;
                int i = 0;
                while(rs.next())
                {
                    /* salemid, saledt, beatabbr, totlines, retnm */
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    String salemid1 = rs.getString("salemid");
                    salemidArray[i++] = salemid1;
                    row.addElement(salemid1);
                    row.addElement(DateConverter.dateConverter(rs.getString("saledt")));
                    row.addElement(rs.getString("beatabbr"));
                    row.addElement(rs.getString("totlines"));
                    row.addElement(rs.getString("distnm"));
                    double netamt02 = Double.parseDouble(rs.getString("netamt02"));
                    totbillval += netamt02;
                    row.addElement(MyNumberFormat.rupeeFormat(netamt02));
                    double mrpval = Double.parseDouble(rs.getString("mrpval"));
                    totmrpval += mrpval;
                    row.addElement(MyNumberFormat.rupeeFormat(mrpval));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                }
                Vector<String> rownext = new Vector<String>();
                rownext.addElement("");
                rownext.addElement("TOTAL");
                rownext.addElement("");
                rownext.addElement("");
                rownext.addElement("");
                rownext.addElement("");
                rownext.addElement(MyNumberFormat.rupeeFormat(totbillval));
                rownext.addElement(MyNumberFormat.rupeeFormat(totmrpval));
                ((DefaultTableModel)jTable1.getModel()).addRow(rownext);
            }
        }     
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ReprintSaleBill ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally
        {
            try 
            {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        
        jTable1.setDragEnabled(false);
        // Disable auto resizing
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = jTable1.getTableHeader();
        header.setBackground(Color.cyan);
        // NO. OF COLUMNS: 7
        /* SLN., BILL NO., BILL DATE, BEAT, LINES, RETAILER, BILL VALUE */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// BILL NO.
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(238);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// BILL DATE
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// BEAT
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(100); 
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// LINES
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(80); 
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// RETAILER
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// BILL VALUE
	jTable1.getColumnModel().getColumn(6).setPreferredWidth(100); 
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// MRP VALUE
	jTable1.getColumnModel().getColumn(7).setPreferredWidth(100); 
                
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("BILL NO.").setCellRenderer( centerRenderer );
        jTable1.getColumn("BILL DATE").setCellRenderer( centerRenderer );
        jTable1.getColumn("BEAT").setCellRenderer( centerRenderer );
        jTable1.getColumn("LINES").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("BILL VALUE").setCellRenderer( rightRenderer );
        jTable1.getColumn("MRP VALUE").setCellRenderer( rightRenderer );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();

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
        jLabel1.setText("FROM DT.");

        jDateChooser1.setBackground(new java.awt.Color(0, 255, 0));
        jDateChooser1.setDateFormatString("dd/MM/yyyy");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("BILL NO.");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 2, true), "BILLING DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "BILL NO.", "BILL DATE", "BEAT", "LINES", "RETAILER", "BILL VALUE", "MRP VALUE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("BEAT");

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("DIST.");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("NO LIMIT");
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SEARCH.PNG"))); // NOI18N
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

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("TO DT.");

        jDateChooser2.setBackground(new java.awt.Color(0, 255, 0));
        jDateChooser2.setDateFormatString("dd/MM/yyyy");

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
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jCheckBox1))
                    .addComponent(jButton1))
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
//            SaleMaster sm = q.getSaleMaster(salemidArray[jTable1.getSelectedRow()]);
//            new printSaleBill(sm, e, q.getDistributer(sm.getDistid()));
//            evt.consume();
            int selectedrow = jTable1.getSelectedRow();
            if ( selectedrow == 0 || selectedrow == jTable1.getRowCount()-1 )
                return;
            SaleMaster sm = q.getSaleMaster(salemidArray[selectedrow-1]);
            // Printing Selection
            try
            {
                ReprintSaleBillSub ref=new ReprintSaleBillSub(sm, e, q.getDistributer(sm.getDistid()));
                ref.setVisible(true);
                jDesktopPane.add(ref);
                ref.show();
                ref.setIcon(false);
                ref.setSelected(true);
            }
            catch(PropertyVetoException e){}
            
            evt.consume();
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2)
        {
//            SaleMaster sm = q.getSaleMaster(salemidArray[jTable1.getSelectedRow()]);
//            new printSaleBill(sm, e, q.getDistributer(sm.getDistid()));
            
            int selectedrow = jTable1.getSelectedRow();
            if ( selectedrow == 0 || selectedrow == jTable1.getRowCount()-1 )
                return;
            SaleMaster sm = q.getSaleMaster(salemidArray[selectedrow-1]);
            // Printing Selection
            try
            {
                ReprintSaleBillSub ref=new ReprintSaleBillSub(sm, e, q.getDistributer(sm.getDistid()));
                ref.setVisible(true);
                jDesktopPane.add(ref);
                ref.show();
                ref.setIcon(false);
                ref.setSelected(true);
            }
            catch(PropertyVetoException e){}
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jCheckBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Fetch();
        if ( jTable1.getRowCount() != 0 )
        {
            jTable1.changeSelection(0, 0, false, false);
            jTable1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            Fetch();
            if ( jTable1.getRowCount() != 0 )
            {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jButton1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
