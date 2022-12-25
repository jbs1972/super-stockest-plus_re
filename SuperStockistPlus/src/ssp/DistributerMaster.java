package ssp;

import conn.dBConnection;
import dto.UserProfile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import query.Query;
import utilities.EmailValidator;
import utilities.JTextFieldLimit;
import utilities.Settings;

public class DistributerMaster extends javax.swing.JInternalFrame {

    private JDesktopPane jDesktopPane1;
    private boolean isFromOtherWindow;
    private UserProfile up;
    private Query q=new Query();
    
    private Settings settings=new Settings();
    
    private String distidArray[];
    private String currentDistid;
    private String beatidArray[];
    private String newBeatabbr;
    private String newDistnm;

    public DistributerMaster(JDesktopPane jDesktopPane1, boolean isFromOtherWindow,  UserProfile up) {
        super("Distributer Master",false,true,false,true);
        initComponents();
        this.jDesktopPane1 = jDesktopPane1;
        this.isFromOtherWindow = isFromOtherWindow;
        this.up = up;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/customer.png")));
        
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
        
        jTextField9.setDocument(new JTextFieldLimit(2));
        jTextField24.setDocument(new JTextFieldLimit(2));
        
        settings.numvalidatorFloat(jTextField9);
        settings.numvalidatorFloat(jTextField10);
        settings.numvalidatorFloat(jTextField24);
        settings.numvalidatorFloat(jTextField25);
        
        ((DefaultTableCellRenderer)jTable1.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        ((JLabel)jComboBox1.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((JLabel)jComboBox2.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        
        Fetch();
        populateCombo1n2();
        
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                {
                    currentDistid = null;
                    newBeatabbr = null;
                    Fetch();
                    jComboBox2.setSelectedIndex(0);
                    jTextField19.setText("");
                    jTextField33.setText("");
                    jTextField20.setText("");
                    jTextField21.setText("");
                    jTextField22.setText("");
                    jTextField23.setText("");
                    jTextField24.setText("");
                    jTextField25.setText("");
                    jTextField26.setText("");
                    jTextField27.setText("");
                    jTextField28.setText("");
                    jTextField29.setText("");
                    jTextField30.setText("");
                    jTextField31.setText("");
                    jTextField32.setText("");
                    jCheckBox1.setSelected(false);
                }
            }
        });
        
        jTabbedPane1.setMnemonicAt(0, KeyEvent.VK_A);
        jTabbedPane1.setMnemonicAt(1, KeyEvent.VK_L);
        jTabbedPane1.setMnemonicAt(2, KeyEvent.VK_D);
   
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
    
    public String getNewDistnm()
    {
        return newDistnm;
    }
    
    private void populateCombo1n2() // Beat
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in BeatMaster: 4
	/* beatid, beatnm, beatabbr, isactive */
        String query="select beatid, beatabbr from BeatMaster where isactive=1 order by beatabbr asc";
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
            jComboBox2.removeAllItems();
            if(total != 0)
            {
                beatidArray=new String[total];
                jComboBox1.addItem("-- Select --");
                jComboBox2.addItem("-- Select --");
                int i=0;
                while(rs.next())
                {
                    beatidArray[i++]=rs.getString("beatid");
                    String beatabbr=rs.getString("beatabbr");
                    jComboBox1.addItem(beatabbr);
                    jComboBox2.addItem(beatabbr);
                }
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
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
        String a="", b="", c="";
        String distnm=jTextField1.getText().trim().toUpperCase().replace("'", "\\'");
        if(distnm.length()!=0)
        {
            a=" and distnm like '%"+distnm+"%'";
        }
        String beatabbr=jTextField2.getText().trim().toUpperCase();
        if(beatabbr.length()!=0)
        {
            b=" and beatabbr like '"+beatabbr+"%'";
        }
        if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ADD"))
        {
            distnm=jTextField4.getText().trim().toUpperCase().replace("'", "\\'");
            if(distnm.length() != 0)
            {
                 c=" and distnm like '%"+distnm+"%'";
            }
        }
        
        clearTable(jTable1);
        // No. Of Columns: 18
        /* SLN., BEAT, DISTRIBUTER, CONTACT PER., STREET, CITY, DIST., STATE, STATE CODE, PIN, COUNTRY,
        CONTACT NO., EMAIL, GSTIN, GST REGN. TYP, PAN, AADHAAR NO., IS ACTIVE? */
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, dpin, 
        dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Number of columns in BeatMaster: 4
	/* beatid, beatnm, beatabbr, isactive */
        String query="select distid, beatabbr, distnm, contactperson, dstreet, dcity, ddist, dstate,"
                + " dstatecode, dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, "
                + "daadhaarno, isactive from Distributer, (select beatid, beatabbr from BeatMaster"
                + " where isactive=1) x where Distributer.beatid=x.beatid and Distributer.isactive=1"
                + a+b+c+" order by distnm";
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
                distidArray=new String[total];
                int slno1=0;
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    distidArray[i++]=rs.getString("distid");
                    /* distid, beatabbr, distnm, contactperson, dstreet, dcity, ddist, dstate, 
                    dstatecode, dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, 
                    daadhaarno, isactive */
                    row.addElement(rs.getString("beatabbr"));
                    row.addElement(rs.getString("distnm").replace("\\'", "'"));
                    row.addElement(rs.getString("contactperson"));
                    row.addElement(rs.getString("dstreet").replace("\\'", "'"));
                    row.addElement(rs.getString("dcity"));
                    row.addElement(rs.getString("ddist"));
                    row.addElement(rs.getString("dstate"));
                    row.addElement(rs.getString("dstatecode"));
                    row.addElement(rs.getString("dpin"));
                    row.addElement(rs.getString("dcountry"));
                    row.addElement(rs.getString("dcontact"));
                    row.addElement(rs.getString("dmail"));
                    row.addElement(rs.getString("dgstno"));
                    row.addElement(rs.getString("dgstregntype"));
                    row.addElement(rs.getString("dpanno"));
                    row.addElement(rs.getString("daadhaarno"));
                    String isactive=rs.getString("isactive");
                    row.addElement(isactive.equals("0")?"In-Active":"Active");
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
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
        // No. Of Columns: 18
        /* SLN., BEAT, DISTRIBUTER, CONTACT PER., STREET, CITY, DIST., STATE, STATE CODE, PIN, COUNTRY,
        CONTACT NO., EMAIL, GSTIN, GST REGN. TYP, PAN, AADHAAR NO., IS ACTIVE? */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(30);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// BEAT
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// DISTRIBUTER
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// CONTACT PER.
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// STREET
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(5).setMinWidth(0);// CITY
	jTable1.getColumnModel().getColumn(5).setPreferredWidth(60);
	jTable1.getColumnModel().getColumn(6).setMinWidth(0);// DIST.
	jTable1.getColumnModel().getColumn(6).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(7).setMinWidth(0);// STATE
	jTable1.getColumnModel().getColumn(7).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(8).setMinWidth(0);// STATE CODE
	jTable1.getColumnModel().getColumn(8).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(9).setMinWidth(0);// PIN
	jTable1.getColumnModel().getColumn(9).setPreferredWidth(60);
	jTable1.getColumnModel().getColumn(10).setMinWidth(0);// COUNTRY
	jTable1.getColumnModel().getColumn(10).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(11).setMinWidth(0);// CONTACT NO.
	jTable1.getColumnModel().getColumn(11).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(12).setMinWidth(0);// EMAIL
	jTable1.getColumnModel().getColumn(12).setPreferredWidth(60);
        jTable1.getColumnModel().getColumn(13).setMinWidth(0);// GSTIN
	jTable1.getColumnModel().getColumn(13).setPreferredWidth(100);
	jTable1.getColumnModel().getColumn(14).setMinWidth(0);// GST REGN. TYPE
	jTable1.getColumnModel().getColumn(14).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(15).setMinWidth(0);// PAN
	jTable1.getColumnModel().getColumn(15).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(16).setMinWidth(0);// AADHAAR NO.
	jTable1.getColumnModel().getColumn(16).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(17).setMinWidth(0);// IS ACTIVE?
	jTable1.getColumnModel().getColumn(17).setPreferredWidth(50);
        
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
    }
    
    private void insertToDatabase()
    {
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        if ( jComboBox1.getSelectedIndex() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select proper Beat of the Retailer.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox1.requestFocusInWindow();
            return;
        }
        String beatid=beatidArray[jComboBox1.getSelectedIndex()-1];
        String distnm=jTextField4.getText().toUpperCase();
        if(distnm.length()==0) {
            JOptionPane.showMessageDialog(null,"Distributer Name field is mandetory.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField4.requestFocusInWindow();
            return;
        }
        String contactperson = jTextField18.getText().trim().toUpperCase();
        String dstreet=jTextField5.getText().toUpperCase();
        String dcity=jTextField6.getText().toUpperCase();
        String ddist=jTextField7.getText().toUpperCase();
        String dstate=jTextField8.getText().toUpperCase();
        if(dstate.length()==0) {
            JOptionPane.showMessageDialog(null,"State field is mandetory.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField8.requestFocusInWindow();
            return;
        }
        String dstatecode=jTextField9.getText().toUpperCase();
        if(dstatecode.length()==0) {
            JOptionPane.showMessageDialog(null,"State Code field is mandetory.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField9.requestFocusInWindow();
            return;
        }
        String dpin=jTextField10.getText();
        String dcountry=jTextField11.getText().toUpperCase();
        String dcontact=jTextField12.getText();
        String dmail=jTextField13.getText();
        if(!dmail.equals("N/A")&&!EmailValidator.validate(dmail))
        {
            JOptionPane.showMessageDialog(null,"Enter valid email id of the Distributer!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField13.requestFocusInWindow();
            return;
        }
        String dgstno=jTextField14.getText().toUpperCase();
//        if(!dgstno.equals("N/A")&&dgstno.length()!=15) {
//            JOptionPane.showMessageDialog(null,"Provide valid GSTIN is mandetory.",
//                    "Error Found",JOptionPane.ERROR_MESSAGE);
//            jTextField14.requestFocusInWindow();
//            return;
//        }
        String dgstregntype=jTextField15.getText();
        String dpanno=jTextField16.getText().toUpperCase();
        String daadhaarno=jTextField17.getText().toUpperCase();
//        if(dgstno.equals("N/A") && dpanno.equals("N/A") && daadhaarno.equals("N/A")) {
//            JOptionPane.showMessageDialog(null,"Provide valid GSTIN or PAN+AADHAAR.",
//                    "Error Found",JOptionPane.ERROR_MESSAGE);
//            jTextField14.requestFocusInWindow();
//            return;
//        } else {
//            if(dgstno.equals("N/A") && (dpanno.equals("N/A") || daadhaarno.equals("N/A"))) {
//                JOptionPane.showMessageDialog(null,"Provide valid PAN+AADHAAR instead of GSTIN.",
//                        "Error Found",JOptionPane.ERROR_MESSAGE);
//                jTextField14.requestFocusInWindow();
//                return;
//            }
//        }
        String isactive = "1";
        
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Duplicate Testing
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select * from Distributer where distnm='"+distnm+"' and beatid="+beatid;
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
            if(total != 0)
            {
                JOptionPane.showMessageDialog(null,"Duplicate Entry! Action Denied.",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                jComboBox1.requestFocusInWindow();
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
                return;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        int distid=q.getMaxId("Distributer", "distid");
        distid++;
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        query="insert into Distributer (distid, beatid, distnm, contactperson, dstreet, dcity,"
                + " ddist, dstate, dstatecode, dpin, dcountry, dcontact, dmail, dgstno, "
                + "dgstregntype, dpanno, daadhaarno, isactive) values ("+distid+", '"+beatid
                + "', '"+distnm+"', '"+contactperson+"', '"+dstreet+"', '"+dcity+"', '"+ddist
                + "', '"+dstate+"', '"+dstatecode+"', '"+dpin+"', '"+dcountry+"', '"+dcontact
                + "', '"+dmail+"', '"+dgstno+"', '"+dgstregntype+"', '"+dpanno+"', '"+daadhaarno
                + "', "+isactive+")";
        conn = db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        if (isFromOtherWindow)
        {
            newDistnm = distnm;
            setVisible(true);
            dispose();
        }
        else
        {
            newBeatabbr = null;
            jComboBox1.setSelectedIndex(0);
            jTextField4.setText("");
            jTextField18.setText("N/A");
            jTextField5.setText("N/A");
            jTextField6.setText("N/A");
            jTextField7.setText("N/A");
            jTextField8.setText("");
            jTextField9.setText("");
            jTextField10.setText("N/A");
            jTextField11.setText("N/A");
            jTextField12.setText("N/A");
            jTextField13.setText("N/A");
            jTextField14.setText("");
            jTextField15.setText("N/A");
            jTextField16.setText("N/A");
            jTextField17.setText("N/A");
            Fetch();
            jComboBox1.requestFocusInWindow();
        }
    }
    
    private void keyReleasedTable1()
    {
        if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
        {
            int row=jTable1.getSelectedRow();
            currentDistid=distidArray[row];
            jComboBox2.setSelectedItem((String)jTable1.getModel().getValueAt(row, 1));
            jTextField19.setText((String)jTable1.getModel().getValueAt(row, 2));
            jTextField33.setText((String)jTable1.getModel().getValueAt(row, 3));
            jTextField20.setText((String)jTable1.getModel().getValueAt(row, 4));
            jTextField21.setText((String)jTable1.getModel().getValueAt(row, 5));
            jTextField22.setText((String)jTable1.getModel().getValueAt(row, 6));
            jTextField23.setText((String)jTable1.getModel().getValueAt(row, 7));
            jTextField24.setText((String)jTable1.getModel().getValueAt(row, 8));
            jTextField25.setText((String)jTable1.getModel().getValueAt(row, 9));
            jTextField26.setText((String)jTable1.getModel().getValueAt(row, 10));
            jTextField27.setText((String)jTable1.getModel().getValueAt(row, 11));
            jTextField28.setText((String)jTable1.getModel().getValueAt(row, 12));
            jTextField29.setText((String)jTable1.getModel().getValueAt(row, 13));
            jTextField30.setText((String)jTable1.getModel().getValueAt(row, 14));
            jTextField31.setText((String)jTable1.getModel().getValueAt(row, 15));
            jTextField32.setText((String)jTable1.getModel().getValueAt(row, 16));
            boolean checkBoxFlag=((String)jTable1.getModel().getValueAt(row, 17)).equals("Active");
            jCheckBox1.setSelected(checkBoxFlag);
        }
    }
    
    private void updateToDatabase()
    {
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        if ( jComboBox2.getSelectedIndex() == 0 )
        {
            JOptionPane.showMessageDialog(null,"Select proper Beat of the Distributer.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jComboBox2.requestFocusInWindow();
            return;
        }
        String beatid=beatidArray[jComboBox2.getSelectedIndex()-1];
        String distnm=jTextField19.getText().toUpperCase();
        if(distnm.length()==0) {
            JOptionPane.showMessageDialog(null,"Distributer Name field is mandetory.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField19.requestFocusInWindow();
            return;
        }
        String contactperson=jTextField33.getText().trim().toUpperCase();
        String dstreet=jTextField20.getText().toUpperCase();
        String dcity=jTextField21.getText().toUpperCase();
        String ddist=jTextField22.getText().toUpperCase();
        String dstate=jTextField23.getText().toUpperCase();
        if(dstate.length()==0) {
            JOptionPane.showMessageDialog(null,"State field is mandetory.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField23.requestFocusInWindow();
            return;
        }
        String dstatecode=jTextField24.getText().toUpperCase();
        if(dstatecode.length()==0) {
            JOptionPane.showMessageDialog(null,"State Code field is mandetory.",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField24.requestFocusInWindow();
            return;
        }
        String dpin=jTextField25.getText();
        String dcountry=jTextField26.getText().toUpperCase();
        String dcontact=jTextField27.getText();
        String dmail=jTextField28.getText();
        if(!dmail.equals("N/A")&&!EmailValidator.validate(dmail))
        {
            JOptionPane.showMessageDialog(null,"Enter valid email id of the Distributer!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField28.requestFocusInWindow();
            return;
        }
        String dgstno=jTextField29.getText().toUpperCase();
//        if(!dgstno.equals("N/A")&&dgstno.length()!=15) {
//            JOptionPane.showMessageDialog(null,"Provide valid GSTIN is mandetory.",
//                    "Error Found",JOptionPane.ERROR_MESSAGE);
//            jTextField29.requestFocusInWindow();
//            return;
//        }
        String dgstregntype=jTextField30.getText();
        String dpanno=jTextField31.getText().toUpperCase();
        String daadhaarno=jTextField32.getText().toUpperCase();
//        if(dgstno.equals("N/A") && dpanno.equals("N/A") && daadhaarno.equals("N/A")) {
//            JOptionPane.showMessageDialog(null,"Provide valid GSTIN or PAN+AADHAAR.",
//                    "Error Found",JOptionPane.ERROR_MESSAGE);
//            jTextField29.requestFocusInWindow();
//            return;
//        } else {
//            if(dgstno.equals("N/A") && (dpanno.equals("N/A") || daadhaarno.equals("N/A"))) {
//                JOptionPane.showMessageDialog(null,"Provide valid PAN+AADHAAR instead of GSTIN.",
//                        "Error Found",JOptionPane.ERROR_MESSAGE);
//                jTextField29.requestFocusInWindow();
//                return;
//            }
//        }
        String isactive=jCheckBox1.isSelected()?"1":"0";
        
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        // Duplicate Testing
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select * from Distributer where distnm='"+distnm+"' and beatid="
                + beatid+" and distid<>"+currentDistid;
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
            if(total != 0)
            {
                JOptionPane.showMessageDialog(null,"Duplicate Entry! Action Denied.",
                        "Error Found",JOptionPane.ERROR_MESSAGE);
                jComboBox2.requestFocusInWindow();
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
                return;
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, dpin,
        dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        query="update Distributer set beatid="+beatid+", distnm='"+distnm+"', contactperson='"+contactperson+"',"
                + " dstreet='"+dstreet+"', "+"dcity='"+dcity+"', ddist='"+ddist+"', dstate='"+dstate
                + "', dstatecode='"+dstatecode+"', dpin='"+dpin+"', dcountry='"+dcountry+"', dcontact='"
                + dcontact+"', dmail='"+dmail+"', dgstno='"+dgstno+"', dgstregntype='"+dgstregntype
                + "', dpanno='"+dpanno+"', daadhaarno='"+daadhaarno+"', isactive="+isactive
                + " where distid="+currentDistid;
        conn = db.setConnection();
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }

        if (isFromOtherWindow)
        {
            newDistnm = distnm;
            setVisible(true);
            dispose();
        }
        else
        {
            newBeatabbr = null;
            currentDistid = null;
            jComboBox2.setSelectedIndex(0);
            jTextField19.setText("");
            jTextField33.setText("");
            jTextField20.setText("");
            jTextField21.setText("");
            jTextField22.setText("");
            jTextField23.setText("");
            jTextField24.setText("");
            jTextField25.setText("");
            jTextField26.setText("");
            jTextField27.setText("");
            jTextField28.setText("");
            jTextField29.setText("");
            jTextField30.setText("");
            jTextField31.setText("");
            jTextField32.setText("");
            jCheckBox1.setSelected(false);
            Fetch();
        }
    }
    
    private void deleteToDatabase()
    {
        if(jTable1.getSelectedRow()!=-1&&jTable1.getSelectedColumn()!=-1) {
            String distid=distidArray[jTable1.getSelectedRow()];
            String ObjButtons[] = {"Yes","Cancel"};
            int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Distributer information!",
                    "Delete Record", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
            if(PromptResult==0) {
                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();
                String query="delete from Distributer where distid="+distid;
                try {
                    Statement smt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    smt.executeUpdate(query);
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(null,"DistributerMaster ex?: "+ex.getMessage(),
                            "Error Found",JOptionPane.ERROR_MESSAGE);
                }
                finally {
                    try {
                        if (conn!=null) conn.close();
                    } catch(SQLException e){}
                    Fetch();
                }
            } else
                JOptionPane.showMessageDialog(null,"Action Discarded","Discard Information",JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,"Select a Record and then try to Delete","Error Found",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addAlterBeat() {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    setVisible(false);
                    final BeatMaster ref=new BeatMaster(true, up);
                    ref.addInternalFrameListener(new InternalFrameAdapter()
                    {
                        @Override
                        public void internalFrameDeactivated(InternalFrameEvent e)
                        {
                            newBeatabbr=ref.getNewBeatabbr();
                        }
                        @Override
                        public void internalFrameClosed(InternalFrameEvent e)
                        {
                            DistributerMaster.this.setVisible(true);
                            if(newBeatabbr!=null)
                            {
                                populateCombo1n2();
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ADD"))
                                {
                                    jComboBox1.setSelectedItem(newBeatabbr);
                                }
                                if(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                                {
                                    jComboBox2.setSelectedItem(newBeatabbr);
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
        jTextField2 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jTextField26 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jTextField29 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTextField30 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

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

        jPanel1.setBackground(new java.awt.Color(226, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "SEARCH", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("DISTRIBUTER");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("BEAT");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(128, 128, 128)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "COMPANY DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "BEAT", "DISTRIBUTER", "CONTACT PER.", "STREET", "CITY", "DIST.", "STATE", "STATE CODE", "PIN", "COUNTRY", "CONTACT NO.", "EMAIL", "GSTIN", "GST REGN. TYPE", "PAN", "AADHAAR NO.", "IS ACTIVE?"
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

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("BEAT");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("DISTRIBUTER");

        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("STREET");

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

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("CITY");

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

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("DISTRICT");

        jTextField7.setText("N/A");
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
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("STATE");

        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField8KeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("STATE CODE");

        jTextField9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField9KeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("PIN");

        jTextField10.setText("N/A");
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
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("COUNTRY");

        jTextField11.setText("N/A");
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
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("CONTACT NO.");

        jTextField12.setText("N/A");
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
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("EMAIL");

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

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("GSTIN");

        jTextField14.setText("N/A");
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
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("GST REGN. TYPE");

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

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("PAN NO.");

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

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("AADHAAR NO");

        jTextField17.setText("N/A");
        jTextField17.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField17FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField17FocusLost(evt);
            }
        });
        jTextField17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField17KeyPressed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("ADD");
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

        jComboBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox1KeyPressed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("CONTACT PERSON");

        jTextField18.setText("N/A");
        jTextField18.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField18FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField18FocusLost(evt);
            }
        });
        jTextField18.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField18KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ADD", jPanel2);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("COMPANY");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("DISTRIBUTER");

        jTextField19.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField19FocusGained(evt);
            }
        });
        jTextField19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField19KeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("STREET");

        jTextField20.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField20FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField20FocusLost(evt);
            }
        });
        jTextField20.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField20KeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("CITY");

        jTextField21.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField21FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField21FocusLost(evt);
            }
        });
        jTextField21.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField21KeyPressed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("DISTRICT");

        jTextField22.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField22FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField22FocusLost(evt);
            }
        });
        jTextField22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField22KeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("STATE");

        jTextField23.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField23FocusGained(evt);
            }
        });
        jTextField23.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField23KeyPressed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("STATE CODE");

        jTextField24.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField24FocusGained(evt);
            }
        });
        jTextField24.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField24KeyPressed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("PIN");

        jTextField25.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField25FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField25FocusLost(evt);
            }
        });
        jTextField25.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField25KeyPressed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("COUNTRY");

        jTextField26.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField26FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField26FocusLost(evt);
            }
        });
        jTextField26.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField26KeyPressed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel27.setText("CONTACT NO.");

        jTextField27.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField27FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField27FocusLost(evt);
            }
        });
        jTextField27.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField27KeyPressed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("EMAIL");

        jTextField28.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField28FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField28FocusLost(evt);
            }
        });
        jTextField28.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField28KeyPressed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("GSTIN");

        jTextField29.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField29FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField29FocusLost(evt);
            }
        });
        jTextField29.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField29KeyPressed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("GST REGN. TYPE");

        jTextField30.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField30FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField30FocusLost(evt);
            }
        });
        jTextField30.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField30KeyPressed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("PAN NO.");

        jTextField31.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField31FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField31FocusLost(evt);
            }
        });
        jTextField31.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField31KeyPressed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setText("AADHAAR NO");

        jTextField32.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField32FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField32FocusLost(evt);
            }
        });
        jTextField32.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField32KeyPressed(evt);
            }
        });

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("IS ACTIVE ?");
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("UPDATE");
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

        jComboBox2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jComboBox2KeyPressed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel35.setText("CONTACT PERSON");

        jTextField33.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField33FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField33FocusLost(evt);
            }
        });
        jTextField33.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField33KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(jTextField30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ALTER", jPanel3);

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 0, 0));
        jLabel33.setText("SELECT A ROW, THEN PROCEED TO DELETE ...");

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton3.setText("DELETE");
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(166, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 428, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        jTabbedPane1.addTab("DELETE", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField18.requestFocusInWindow();
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
            jTextField7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField6KeyPressed

    private void jTextField7FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusGained
        String s=jTextField7.getText().trim();
        if(s.equals("N/A")) {
            jTextField7.setText("");
        }
    }//GEN-LAST:event_jTextField7FocusGained

    private void jTextField7FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField7FocusLost
        String s=jTextField7.getText().trim();
        if(s.length()==0) {
            jTextField7.setText("N/A");
        }
    }//GEN-LAST:event_jTextField7FocusLost

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField8.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField9.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField8KeyPressed

    private void jTextField9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField9KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField10.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField9KeyPressed

    private void jTextField10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusGained
        String s=jTextField10.getText().trim();
        if(s.equals("N/A")) {
            jTextField10.setText("");
        }
    }//GEN-LAST:event_jTextField10FocusGained

    private void jTextField10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField10FocusLost
        String s=jTextField10.getText().trim();
        if(s.length()==0) {
            jTextField10.setText("N/A");
        }
    }//GEN-LAST:event_jTextField10FocusLost

    private void jTextField10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField10KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField11.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField10KeyPressed

    private void jTextField11FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusGained
        String s=jTextField11.getText().trim();
        if(s.equals("N/A")) {
            jTextField11.setText("");
        }
    }//GEN-LAST:event_jTextField11FocusGained

    private void jTextField11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusLost
        String s=jTextField11.getText().trim();
        if(s.length()==0) {
            jTextField11.setText("N/A");
        }
    }//GEN-LAST:event_jTextField11FocusLost

    private void jTextField11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField11KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField12.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField11KeyPressed

    private void jTextField12FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusGained
        String s=jTextField12.getText().trim();
        if(s.equals("N/A")) {
            jTextField12.setText("");
        }
    }//GEN-LAST:event_jTextField12FocusGained

    private void jTextField12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusLost
        String s=jTextField12.getText().trim();
        if(s.length()==0) {
            jTextField12.setText("N/A");
        }
    }//GEN-LAST:event_jTextField12FocusLost

    private void jTextField12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField12KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField13.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField12KeyPressed

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

    private void jTextField14KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField14KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField15.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField14KeyPressed

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
            jTextField16.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField15KeyPressed

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
            jTextField17.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField16KeyPressed

    private void jTextField17FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField17FocusGained
        String s=jTextField17.getText().trim();
        if(s.equals("N/A")) {
            jTextField17.setText("");
        }
    }//GEN-LAST:event_jTextField17FocusGained

    private void jTextField17FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField17FocusLost
        String s=jTextField17.getText().trim();
        if(s.length()==0) {
            jTextField17.setText("N/A");
        }
    }//GEN-LAST:event_jTextField17FocusLost

    private void jTextField17KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField17KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField17KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
            {
                jComboBox2.requestFocusInWindow();
                evt.consume();
            }
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("DELETE"))
            {
                jButton3.requestFocusInWindow();
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        keyReleasedTable1();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        keyReleasedTable1();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2)
        {
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
            {
                jComboBox2.requestFocusInWindow();
                evt.consume();
            }
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("DELETE"))
            {
                jButton3.requestFocusInWindow();
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTextField19FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField19FocusGained
        jTextField19.selectAll();
    }//GEN-LAST:event_jTextField19FocusGained

    private void jTextField19KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField19KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField33.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField19KeyPressed

    private void jTextField20FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField20FocusGained
        String s=jTextField20.getText().trim();
        if(s.equals("N/A")) {
            jTextField20.setText("");
        }
        else {
            jTextField20.selectAll();
        }
    }//GEN-LAST:event_jTextField20FocusGained

    private void jTextField20FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField20FocusLost
        String s=jTextField20.getText().trim();
        if(s.length()==0) {
            jTextField20.setText("N/A");
        }
    }//GEN-LAST:event_jTextField20FocusLost

    private void jTextField20KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField20KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField21.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField20KeyPressed

    private void jTextField21FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField21FocusGained
        String s=jTextField21.getText().trim();
        if(s.equals("N/A")) {
            jTextField21.setText("");
        }
        else {
            jTextField21.selectAll();
        }
    }//GEN-LAST:event_jTextField21FocusGained

    private void jTextField21FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField21FocusLost
        String s=jTextField21.getText().trim();
        if(s.length()==0) {
            jTextField21.setText("N/A");
        }
    }//GEN-LAST:event_jTextField21FocusLost

    private void jTextField21KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField21KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField22.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField21KeyPressed

    private void jTextField22FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField22FocusGained
        String s=jTextField22.getText().trim();
        if(s.equals("N/A")) {
            jTextField22.setText("");
        }
        else {
            jTextField22.selectAll();
        }
    }//GEN-LAST:event_jTextField22FocusGained

    private void jTextField22FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField22FocusLost
        String s=jTextField22.getText().trim();
        if(s.length()==0) {
            jTextField22.setText("N/A");
        }
    }//GEN-LAST:event_jTextField22FocusLost

    private void jTextField22KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField22KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField23.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField22KeyPressed

    private void jTextField23FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField23FocusGained
        jTextField23.selectAll();
    }//GEN-LAST:event_jTextField23FocusGained

    private void jTextField23KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField23KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField24.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField23KeyPressed

    private void jTextField24FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField24FocusGained
        jTextField24.selectAll();
    }//GEN-LAST:event_jTextField24FocusGained

    private void jTextField24KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField24KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField25.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField24KeyPressed

    private void jTextField25FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField25FocusGained
        String s=jTextField25.getText().trim();
        if(s.equals("N/A")) {
            jTextField25.setText("");
        }
        else {
            jTextField25.selectAll();
        }
    }//GEN-LAST:event_jTextField25FocusGained

    private void jTextField25FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField25FocusLost
        String s=jTextField25.getText().trim();
        if(s.length()==0) {
            jTextField25.setText("N/A");
        }
    }//GEN-LAST:event_jTextField25FocusLost

    private void jTextField25KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField25KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField26.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField25KeyPressed

    private void jTextField26FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField26FocusGained
        String s=jTextField26.getText().trim();
        if(s.equals("N/A")) {
            jTextField26.setText("");
        }
        else {
            jTextField26.selectAll();
        }
    }//GEN-LAST:event_jTextField26FocusGained

    private void jTextField26FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField26FocusLost
        String s=jTextField26.getText().trim();
        if(s.length()==0) {
            jTextField26.setText("N/A");
        }
    }//GEN-LAST:event_jTextField26FocusLost

    private void jTextField26KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField26KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField27.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField26KeyPressed

    private void jTextField27FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField27FocusGained
        String s=jTextField27.getText().trim();
        if(s.equals("N/A")) {
            jTextField27.setText("");
        }
        else {
            jTextField27.selectAll();
        }
    }//GEN-LAST:event_jTextField27FocusGained

    private void jTextField27FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField27FocusLost
        String s=jTextField27.getText().trim();
        if(s.length()==0) {
            jTextField27.setText("N/A");
        }
    }//GEN-LAST:event_jTextField27FocusLost

    private void jTextField27KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField27KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField28.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField27KeyPressed

    private void jTextField28FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField28FocusGained
        String s=jTextField28.getText().trim();
        if(s.equals("N/A")) {
            jTextField28.setText("");
        }
        else {
            jTextField28.selectAll();
        }
    }//GEN-LAST:event_jTextField28FocusGained

    private void jTextField28FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField28FocusLost
        String s=jTextField28.getText().trim();
        if(s.length()==0) {
            jTextField28.setText("N/A");
        }
    }//GEN-LAST:event_jTextField28FocusLost

    private void jTextField28KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField28KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField29.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField28KeyPressed

    private void jTextField29FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField29FocusGained
        String s=jTextField29.getText().trim();
        if(s.equals("N/A")) {
            jTextField29.setText("");
        }
        else {
            jTextField29.selectAll();
        }
    }//GEN-LAST:event_jTextField29FocusGained

    private void jTextField29KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField29KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField30.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField29KeyPressed

    private void jTextField30FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField30FocusGained
        String s=jTextField30.getText().trim();
        if(s.equals("N/A")) {
            jTextField30.setText("");
        }
        else {
            jTextField30.selectAll();
        }
    }//GEN-LAST:event_jTextField30FocusGained

    private void jTextField30FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField30FocusLost
        String s=jTextField30.getText().trim();
        if(s.length()==0) {
            jTextField30.setText("N/A");
        }
    }//GEN-LAST:event_jTextField30FocusLost

    private void jTextField30KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField30KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField31.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField30KeyPressed

    private void jTextField31FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField31FocusGained
        String s=jTextField31.getText().trim();
        if(s.equals("N/A")) {
            jTextField31.setText("");
        }
        else {
            jTextField31.selectAll();
        }
    }//GEN-LAST:event_jTextField31FocusGained

    private void jTextField31FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField31FocusLost
        String s=jTextField31.getText().trim();
        if(s.length()==0) {
            jTextField31.setText("N/A");
        }
    }//GEN-LAST:event_jTextField31FocusLost

    private void jTextField31KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField31KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField32.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField31KeyPressed

    private void jTextField32FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField32FocusGained
        String s=jTextField32.getText().trim();
        if(s.equals("N/A")) {
            jTextField32.setText("");
        }
        else {
            jTextField32.selectAll();
        }
    }//GEN-LAST:event_jTextField32FocusGained

    private void jTextField32FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField32FocusLost
        String s=jTextField32.getText().trim();
        if(s.length()==0) {
            jTextField32.setText("N/A");
        }
    }//GEN-LAST:event_jTextField32FocusLost

    private void jTextField32KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField32KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jCheckBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField32KeyPressed

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        updateToDatabase();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            updateToDatabase();
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        deleteToDatabase();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            deleteToDatabase();
        }
    }//GEN-LAST:event_jButton3KeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        Fetch();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable1.getRowCount() != 0 )
            {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
            else
            jComboBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jComboBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField4.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterBeat();
        }
    }//GEN-LAST:event_jComboBox1KeyPressed

    private void jComboBox2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jComboBox2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField19.requestFocusInWindow();
        }
        if (evt.getKeyCode() == KeyEvent.VK_F2)
        {
            addAlterBeat();
        }
    }//GEN-LAST:event_jComboBox2KeyPressed

    private void jTextField18KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField18KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField18KeyPressed

    private void jTextField18FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField18FocusGained
        String s=jTextField18.getText().trim();
        if(s.equals("N/A")) {
            jTextField18.setText("");
        }
    }//GEN-LAST:event_jTextField18FocusGained

    private void jTextField18FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField18FocusLost
        String s=jTextField18.getText().trim();
        if(s.length()==0) {
            jTextField18.setText("N/A");
        }
    }//GEN-LAST:event_jTextField18FocusLost

    private void jTextField33FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField33FocusGained
        String s=jTextField33.getText().trim();
        if(s.equals("N/A")) {
            jTextField33.setText("");
        }
        else {
            jTextField33.selectAll();
        }
    }//GEN-LAST:event_jTextField33FocusGained

    private void jTextField33KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField33KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField20.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField33KeyPressed

    private void jTextField33FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField33FocusLost
        String s=jTextField33.getText().trim();
        if(s.length()==0) {
            jTextField33.setText("N/A");
        }
    }//GEN-LAST:event_jTextField33FocusLost

    private void jTextField14FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField14FocusGained
        String s=jTextField14.getText().trim();
        if(s.equals("N/A")) {
            jTextField14.setText("");
        }
    }//GEN-LAST:event_jTextField14FocusGained

    private void jTextField14FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField14FocusLost
        String s=jTextField14.getText().trim();
        if(s.length()==0) {
            jTextField14.setText("N/A");
        }
    }//GEN-LAST:event_jTextField14FocusLost

    private void jTextField29FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField29FocusLost
        String s=jTextField29.getText().trim();
        if(s.length()==0) {
            jTextField29.setText("N/A");
        }
    }//GEN-LAST:event_jTextField29FocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField30;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
