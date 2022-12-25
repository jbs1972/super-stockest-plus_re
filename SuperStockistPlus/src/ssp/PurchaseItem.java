package ssp;

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
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import static javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utilities.DateConverter;
import utilities.MyNumberFormat;

public class PurchaseItem extends javax.swing.JInternalFrame {

    private String itemid;
    private DecimalFormat format = new DecimalFormat("0.#");
    
    private String psidArray[];
    private String selectedPsid;
    
    public PurchaseItem(String itemid) {
        super("Purchased Items",false,true,false,true);
        initComponents();
        this.itemid = itemid;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/prod_sale_01.png")));
        
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
        
        // Getting Item Info.
        // Number of columns in ItemMaster: 7
        /* itemid, itemnm, compid, icid, muid, reordqty, isactive */
        // Number of columns in ItemCategory: 4
        /* icid, icnm, hsn, isactive */
        String query="select itemnm, hsn from (select itemnm, icid from ItemMaster where isactive=1"
                + " and itemid="+this.itemid+") x, (select icid, hsn from ItemCategory where isactive=1)"
                + " y where x.icid=y.icid";
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
                    jLabel2.setText(rs.getString("itemnm"));
                    jLabel4.setText(rs.getString("hsn"));
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseItem ex?: "+ex.getMessage(),
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
        
        Fetch();
        
        SwingUtilities.invokeLater
        (
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    if ( jTable1.getRowCount() != 0 ) {
                        jTable1.changeSelection(0, 0, false, false);
                        jTable1.requestFocusInWindow();
                    }
                }
            }
        );
    }
    
    public String getSelectedPsid()
    {
        return selectedPsid;
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
        double totavlqty = 0.0;
        clearTable(jTable1);
        // No. Of Columns: 10
        /* SLN., INV. NO., INV. DATE, MRP, PUR. RATE, DISC.%, CGST%, SGST%, IGST%, AVL. QTY. */
        // Number of columns in PurchaseMaster: 27
        /* pmid, ssid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
	cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        String query="select psid, invno, invdt, mrp, rate, discper, cgstper, sgstper, igstper,"
                + " avlqty from (select pmid, invno, invdt from PurchaseMaster where isactive=1) x,"
                + " (select psid, pmid, mrp, rate, discper, cgstper, sgstper, igstper, qty-(qtysold+retqty)"
                + " as avlqty from PurchaseSub where qty-(qtysold+retqty)>0 and itemid="+itemid+")"
                + " y where x.pmid=y.pmid order by invdt";
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
                psidArray=new String[total];
                int slno1=0;
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    psidArray[i++]=rs.getString("psid");
                    /* psid, invno, invdt, mrp, rate, discper, cgstper, sgstper, igstper, avlqty */
                    row.addElement(rs.getString("invno"));
                    row.addElement(DateConverter.dateConverter(rs.getString("invdt")));
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("mrp"))));
                    row.addElement(MyNumberFormat.rupeeFormat(Double.parseDouble(rs.getString("rate"))));
                    row.addElement(format.format(Double.parseDouble(rs.getString("discper"))));
                    row.addElement(format.format(Double.parseDouble(rs.getString("cgstper"))));
                    row.addElement(format.format(Double.parseDouble(rs.getString("sgstper"))));
                    row.addElement(format.format(Double.parseDouble(rs.getString("igstper"))));
                    double avlqty = Double.parseDouble(rs.getString("avlqty"));
                    totavlqty += avlqty;
                    row.addElement(format.format(avlqty));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"PurchaseItem ex?: "+ex.getMessage(),
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
        // No. Of Columns: 10
        /* SLN., INV. NO., INV. DATE, MRP, PUR. RATE, DISC.%, CGST%, SGST%, IGST%, AVL. QTY. */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(60);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// INV. NO.
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(140);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// INV. DATE
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// MRP
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// PUR. RATE
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// DISC.%
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(0);// CGST%
	jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
	jTable1.getColumnModel().getColumn(7).setMinWidth(0);// SGST%
	jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// IGST%
	jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// AVL. QTY.
	jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        // No. Of Columns: 11
        /* SLN., INV. NO., INV. DATE, MRP, PUR. RATE, DISC.%, CGST%, SGST%, IGST%, AVL. QTY., SALE RATE */
        jTable1.getColumn("MRP").setCellRenderer( rightRenderer );
        jTable1.getColumn("PUR. RATE").setCellRenderer( rightRenderer );
        jTable1.getColumn("DISC.%").setCellRenderer( rightRenderer );
        jTable1.getColumn("CGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("SGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("IGST%").setCellRenderer( rightRenderer );
        jTable1.getColumn("AVL. QTY.").setCellRenderer( rightRenderer );
        
        jLabel6.setText(format.format(totavlqty));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(226, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "SEARCH CRITERIA", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("ITEM");

        jLabel2.setBackground(new java.awt.Color(255, 255, 51));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("N/A");
        jLabel2.setOpaque(true);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("HSN");

        jLabel4.setBackground(new java.awt.Color(0, 255, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("N/A");
        jLabel4.setOpaque(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(197, 197, 197)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(225, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "PURCHASED ITEM DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "INV. NO.", "INV. DATE", "MRP", "PUR. RATE", "DISC.%", "CGST%", "SGST%", "IGST%", "AVL. QTY."
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("TOTAL AVAILABLE STOCK");

        jLabel6.setBackground(new java.awt.Color(255, 255, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("0");
        jLabel6.setOpaque(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            int row=jTable1.getSelectedRow();
            selectedPsid=psidArray[row];
            setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2)
        {
            int row=jTable1.getSelectedRow();
            selectedPsid=psidArray[row];
            setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_jTable1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
