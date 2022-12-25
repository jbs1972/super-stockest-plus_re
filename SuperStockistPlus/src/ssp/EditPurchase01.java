package ssp;

import conn.dBConnection;
import dto.Enterprise;
import dto.PurchaseMaster;
import dto.UserProfile;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
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
import query.Query;
import utilities.DateConverter;
import utilities.Settings;

public class EditPurchase01 extends javax.swing.JInternalFrame implements AWTEventListener{

    private Settings settings=new Settings();
    private DecimalFormat df2 = new DecimalFormat("###.##");
    private DecimalFormat df3 = new DecimalFormat("###.###");
    private Query q=new Query();
    private JDesktopPane jDesktopPane;
    private Enterprise e;
    private UserProfile up;
    
    private String pmidArray[];
    private String currentPmid;
    private PurchaseMaster pm;
    
    public EditPurchase01(JDesktopPane jDesktopPane, Enterprise e, UserProfile up) {
        super("Edit Purchase 01",false,true,false,true);
        initComponents();
        this.jDesktopPane=jDesktopPane;
        this.e = e;
        this.up = up;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-40);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/MODIFY.PNG")));
        
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
        String specificdt="", a = "", b = "", c = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(jDateChooser1.getDate()!=null)
        {
            Date specificDt=jDateChooser1.getDate();
            try
            {
                specificdt=sdf.format(specificDt);
                a=" and invdt = #"+DateConverter.dateConverter1(specificdt)+"#";
            }
            catch(NullPointerException ex)
            {            
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"Invalid From Date.",
                        "Invalid Date",JOptionPane.ERROR_MESSAGE);
                jDateChooser1.setDate(null);
                jDateChooser1.requestFocusInWindow();                
                return;
            }
        }
        String invno=jTextField1.getText().trim().toUpperCase();
        if(invno.length() != 0)
        {
            b=" and invno like '%"+invno+"%'";
        }
        if ( a.length()==0 && b.length()==0 && jCheckBox1.isSelected()==false ) {
            c = " top 22";
        }

        // NO. OF COLUMNS: 4
        /* SLN., INV. NO., INV. DATE, LINES */
        clearTable(jTable1);
        // Number of columns in PurchaseMaster: 27
        /* pmid, ssid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, 
	cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        String query="select pmid, invno, invdt, totlines from (select"+c+" pmid, invno, "
                + "invdt from PurchaseMaster where isactive=1"+a+b+" order by invdt desc)"
                + " x, (select pmid, count(psid) as totlines from PurchaseSub group by pmid)"
                + " y where x.pmid=y.pmid order by invdt desc, pmid desc";
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
                pmidArray = new String[total];
                int slno1=0;
                int i = 0;
                while(rs.next())
                {
                    /* salemid, saledt, beatabbr, totlines, retnm */
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    pmidArray[i++] = rs.getString("pmid");
                    row.addElement(rs.getString("invno"));
                    row.addElement(DateConverter.dateConverter(rs.getString("invdt")));
                    row.addElement(rs.getString("totlines"));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                }
            }
        }     
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditPurchase01 ex?: "+ex.getMessage(),
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
        // NO. OF COLUMNS: 4
        /* SLN., INV. NO., INV. DATE, LINES */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// INV. NO.
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(400);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// INV. DATE
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// LINES
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(200); 
                
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("INV. NO.").setCellRenderer( centerRenderer );
        jTable1.getColumn("INV. DATE").setCellRenderer( centerRenderer );
        jTable1.getColumn("LINES").setCellRenderer( centerRenderer );
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
        jCheckBox1 = new javax.swing.JCheckBox();

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
        jLabel1.setText("INVOICE DATE");

        jDateChooser1.setBackground(new java.awt.Color(0, 255, 0));
        jDateChooser1.setDateFormatString("dd/MM/yyyy");
        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("INVOICE NO.");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 255, 0), 2, true), "BILLING DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "INV. NO.", "INV. DATE", "LINES"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("NO LIMIT");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDateChooser1PropertyChange
        Fetch();
    }//GEN-LAST:event_jDateChooser1PropertyChange

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jCheckBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            // Bill Existance Testing
            boolean notExistsFlag = false;
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            String query="select * from PurchaseMaster where pmid="+pmidArray[jTable1.getSelectedRow()];
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
                if(total == 0)
                {
                    JOptionPane.showMessageDialog(null,"Bill does not exist !!!.",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    notExistsFlag = true;
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"EditPurchase01 ex?: "+ex.getMessage(),
                        "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            finally {
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
            }
            if ( notExistsFlag ) {
                Fetch();
                if ( jTable1.getRowCount() != 0 ) {
                    jTable1.changeSelection(0, 0, false, false);
                    jTable1.requestFocusInWindow();
                }
                evt.consume();
                return;
            }
            
            PurchaseMaster pm = q.getPurchaseMaster(pmidArray[jTable1.getSelectedRow()]);
            // Editing functionality
            try
            {
                EditPurchase02 ref=new EditPurchase02(jDesktopPane, up, pm);
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
            // Bill Existance Testing
            boolean notExistsFlag = false;
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            String query="select * from PurchaseMaster where pmid="+pmidArray[jTable1.getSelectedRow()];
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
                if(total == 0)
                {
                    JOptionPane.showMessageDialog(null,"Bill does not exist !!!.",
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                    notExistsFlag = true;
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"EditPurchase01 ex?: "+ex.getMessage(),
                        "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            finally {
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
            }
            if ( notExistsFlag ) {
                Fetch();
                if ( jTable1.getRowCount() != 0 ) {
                    jTable1.changeSelection(0, 0, false, false);
                    jTable1.requestFocusInWindow();
                }
                return;
            }
            
            PurchaseMaster pm = q.getPurchaseMaster(pmidArray[jTable1.getSelectedRow()]);
            // Editing functionality
            try
            {
                EditPurchase02 ref=new EditPurchase02(jDesktopPane, up, pm);
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

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable1.getRowCount() != 0 )
            {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jCheckBox1ItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
