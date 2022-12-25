package reports;

import conn.dBConnection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utilities.DateConverter;
import utilities.JTableCellColorRenderer5;
import utilities.PerfectInt;

public class StockNotification extends javax.swing.JInternalFrame {

    private DecimalFormat df3 = new DecimalFormat("###.###");
    private DecimalFormat format = new DecimalFormat("0.#");
    
    /**
     * Creates new form ProductStockReport
     */
    public StockNotification() {
        super("Stock Notification",false,true,false,true);
        initComponents();
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/notification.png")));
        
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
        
        Fetch();
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jTextField2.requestFocusInWindow();
                }
            }
        );
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
        // Calculating start and end date for last 30 days
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar sdate = Calendar.getInstance();
        sdate.add(Calendar.DATE, -30);
        Date sDt=sdate.getTime();
        String sdt = sdf.format(sDt);
//        System.out.println("Counting Start Date: "+sdt);
        Calendar edate = Calendar.getInstance();
        edate.add(Calendar.DATE, -1);
        Date eDt=edate.getTime();
        String edt = sdf.format(eDt);
//        System.out.println("Counting End Date: "+edt);
        
        String a="", b="", c="";        
        String hsn=jTextField2.getText().toUpperCase().trim();
        if(hsn.length() != 0)
        {
             a=" and hsn like '"+hsn+"%'";
        }
        String itemnm=jTextField3.getText().toUpperCase().trim();
        if(itemnm.length() != 0)
        {
             b=" and itemnm like '%"+itemnm+"%'";
        }
        String compnm=jTextField1.getText().toUpperCase().trim();
        if(compnm.length() != 0)
        {
             c=" and compnm like '"+compnm+"%'";
        }
        
        jTable1.getColumnModel().getColumn(6).setCellRenderer(new JTableCellColorRenderer5());
        
        int slno1=0;
        clearTable(jTable1);
        
        // NO. OF COLUMNS: 9
        /* SLN., HSN, ITEM, COMPANY, AVAILABLE QTY., REORDER QTY., ORDER STAT., LM. ERQ., SUPPLY CAP. (DAYS) */
        
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in CompanyMaster: 5
        /* compid, compnm, compcontact, compmail, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        // Number of columns in MeasuringUnit: 3
        /* muid, munm, isactive */
        // Number of columns in ItemStock: 3
        /* isid, itemid, qtyonhand */
        String query="select ItemMaster.itemid as itemid, itemnm, hsn, compnm, qtyonhand, munm,"
                + " reordqty from ItemStock, ItemMaster, CompanyMaster, ItemCategory, MeasuringUnit "
                + "where ItemStock.itemid=ItemMaster.itemid and ItemMaster.compid=CompanyMaster.compid"
                + " and ItemMaster.icid=ItemCategory.icid and ItemMaster.muid=MeasuringUnit.muid"
                + a+b+c+" order by compnm, hsn, itemnm";   
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
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    String currentItemid = rs.getString("itemid");
                    // itemid, itemnm, hsn, compnm, qtyonhand, munm, reordqty
                    row.addElement(rs.getString("hsn"));
                    row.addElement(rs.getString("itemnm").replaceAll("\\'", "'"));
                    row.addElement(rs.getString("compnm").replaceAll("\\'", "'"));
                    double qtyonhand = Double.parseDouble(rs.getString("qtyonhand"));
                    row.addElement(format.format(qtyonhand)+" "+rs.getString("munm"));
                    double reorderqty = Double.parseDouble(rs.getString("reordqty"));
                    row.addElement(format.format(reorderqty));
                    if(reorderqty < qtyonhand)
                        row.addElement("AVAILABLE");
                    else
                        if(reorderqty > qtyonhand)
                            row.addElement("ORDER NOW");
                        else
                            row.addElement("ON MERGINE");
                    
                    // Getting request volume for last 30 days
                    // Number of columns in SaleMaster: 26
                    /* salemid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
                    supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
                    netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
                    // Number of columns in SaleSub: 19
                    /* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
                    cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                    double reqqty = 0.0;
                    query = "select sum(qty) as reqqty from SaleSub, SaleMaster where SaleMaster.salemid="
                            + "SaleSub.salemid and saledt >=#"+DateConverter.dateConverter1(sdt)+"# and "
                            + "saledt <=#"+DateConverter.dateConverter1(edt)+"# and itemid="+currentItemid;
                    System.out.println(query);
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    if ( rs1.next() )
                    {
                        try
                        {
                            reqqty = Double.parseDouble(rs1.getString("reqqty"));
                        }
                        catch ( Exception e ) {}
                    }
                    row.addElement(format.format(reqqty));
                    if ( reqqty <= 0.0 )
                    {
                        row.addElement("INF.");
                    }
                    else
                    {
                        row.addElement(PerfectInt.getPerfectInt(Math.round(qtyonhand / (reqqty / 30.0))+"")+" Days");
                    }
                    
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
            }            
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"ItemStockNotification ex?: "+ex.getMessage(),
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
        // NO. OF COLUMNS: 9
        /* SLN., HSN, ITEM, COMPANY, AVAILABLE QTY., REORDER QTY., ORDER STAT., LM. ERQ., SUPPLY CAP. (DAYS) */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(1).setMinWidth(0);// HSN
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// ITEM
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(270);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// COMPANY
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(180);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// AVAILABLE QTY.
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// REORDER QTY.
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// ORDER STAT.
	jTable1.getColumnModel().getColumn(6).setPreferredWidth(130);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// LM. REQ.
	jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// SUPPLY CAP. (DAYS)
	jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        
        // NO. OF COLUMNS: 9
        /* SLN., HSN, ITEM, COMPANY, AVAILABLE QTY., REORDER QTY., ORDER STAT., LM. ERQ., SUPPLY CAP. (DAYS) */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("SUPPLY CAP. (DAYS)").setCellRenderer( centerRenderer );
        jTable1.getColumn("HSN").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        jTable1.getColumn("AVAILABLE QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("REORDER QTY.").setCellRenderer( rightRenderer );
        jTable1.getColumn("LM. REQ.").setCellRenderer( rightRenderer );
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
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

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

        jPanel1.setBackground(new java.awt.Color(219, 255, 237));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "PRODUCT STOCK ALERT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("COMPANY");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("HSN");

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("ITEM NAME");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(241, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "PRODUCT STOCK ALERT", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "HSN", "ITEM", "COMPANY", "AVAILABLE QTY.", "REORDER QTY.", "ORDER STAT.", "LM. REQ.", "SUPPLY CAP. (DAYS)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Fetch();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            Fetch();
        }
    }//GEN-LAST:event_jButton1KeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
