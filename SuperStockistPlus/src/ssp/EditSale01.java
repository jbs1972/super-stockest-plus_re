package ssp;

import conn.dBConnection;
import dto.Enterprise;
import dto.SaleMaster;
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

public class EditSale01 extends javax.swing.JInternalFrame implements AWTEventListener{

    private Settings settings=new Settings();
    private DecimalFormat df2 = new DecimalFormat("###.##");
    private DecimalFormat df3 = new DecimalFormat("###.###");
    private Query q=new Query();
    private JDesktopPane jDesktopPane;
    private Enterprise e;
    private UserProfile up;
    
    private String salemidArray[];
    private String currentSalemid;
    private SaleMaster sm;
    
    public EditSale01(JDesktopPane jDesktopPane, Enterprise e, UserProfile up) {
        super("Edit Sale 01",false,true,false,true);
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
        String specificdt="", a = "", b = "", c = "", d="", e="";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if(jDateChooser1.getDate()!=null)
        {
            Date specificDt=jDateChooser1.getDate();
            try
            {
                specificdt=sdf.format(specificDt);
                a=" and billdt = #"+DateConverter.dateConverter1(specificdt)+"#";
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
        if ( a.length()==0 && b.length()==0 && c.length()==0 && d.length()==0 && jCheckBox1.isSelected()==false ) {
            e = " top 22";
        }

        // NO. OF COLUMNS: 6
        /* SLN., BILL NO., BILL DATE, BEAT, LINES, RETAILER */
        
        clearTable(jTable1);
        
        // Number of columns in SaleMaster: 26
	/* salemid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
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
        String query="select salemid, saledt, beatabbr, totlines, distnm from (select"+e+" salemid,"
                + " distid, saledt from SaleMaster  where isactive=1"+a+b+" order by saledt"
                + " desc) x, (select distid, distnm, beatid from Distributer where isactive=1"+c+d+") y,"
                + " (select beatid, beatabbr from BeatMaster where isactive=1"+c+") z, (select salemid,"
                + " count(salesid) as totlines from SaleSub group by salemid) a where x.distid=y.distid and"
                + " y.beatid=z.beatid and x.salemid=a.salemid order by saledt desc, salemid desc";
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
                salemidArray = new String[total];
                int slno1=0;
                int i = 0;
                while(rs.next())
                {
                    /* salemid, saledt, beatabbr, totlines, distnm */
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    String salemid1 = rs.getString("salemid");
                    salemidArray[i++] = salemid1;
                    row.addElement(salemid1);
                    row.addElement(DateConverter.dateConverter(rs.getString("saledt")));
                    row.addElement(rs.getString("beatabbr"));
                    row.addElement(rs.getString("totlines"));
                    row.addElement(rs.getString("distnm"));
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
                }
            }
        }     
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"EditSale01 ex?: "+ex.getMessage(),
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
        // NO. OF COLUMNS: 6
        /* SLN., BILL NO., BILL DATE, BEAT, LINES, RETAILER */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// BILL NO.
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(250);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// BILL DATE
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// BEAT
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(100); 
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// LINES
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(80); 
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// RETAILER
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(350); 
                
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("BILL NO.").setCellRenderer( centerRenderer );
        jTable1.getColumn("BILL DATE").setCellRenderer( centerRenderer );
        jTable1.getColumn("BEAT").setCellRenderer( centerRenderer );
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
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
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
        jLabel1.setText("BILLING DATE");

        jDateChooser1.setBackground(new java.awt.Color(0, 255, 0));
        jDateChooser1.setDateFormatString("dd/MM/yyyy");
        jDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jDateChooser1PropertyChange(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("BILL NO.");

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
                "SLN.", "BILL NO.", "BILL DATE", "BEAT", "LINES", "DISTRIBUTER"
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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("RETAILER");

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField3KeyReleased(evt);
            }
        });

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
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCheckBox1)
                        .addGap(0, 14, Short.MAX_VALUE)))
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
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jCheckBox1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
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

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            // Bill Existance Testing
            boolean notExistsFlag = false;
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            String query="select * from SaleMaster where salemid='"+salemidArray[jTable1.getSelectedRow()]+"'";
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
                JOptionPane.showMessageDialog(null,"EditSale01 ex?: "+ex.getMessage(),
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
            
            SaleMaster sm = q.getSaleMaster(salemidArray[jTable1.getSelectedRow()]);
            // Editing functionality
            try
            {
                EditSale02 ref=new EditSale02(jDesktopPane, up, e, sm);
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
            String query="select * from SaleMaster where salemid='"+salemidArray[jTable1.getSelectedRow()]+"'";
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
                JOptionPane.showMessageDialog(null,"EditSale01 ex?: "+ex.getMessage(),
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
            
            SaleMaster sm = q.getSaleMaster(salemidArray[jTable1.getSelectedRow()]);
            // Editing functionality
            try
            {
                EditSale02 ref=new EditSale02(jDesktopPane, up, e, sm);
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

    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField3KeyReleased

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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
