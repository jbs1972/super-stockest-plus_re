package ssp;

import conn.dBConnection;
import dto.UserProfile;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import query.Query;
import utilities.GroupButtonUtils;
import utilities.Settings;

public class Formula extends javax.swing.JInternalFrame {

    private boolean isFromOtherWindow;
    private UserProfile up;
    private Query q=new Query();
    private Settings settings=new Settings();
    
    private String formulaidArray[];
    private String currentFormulaid;
    private String newFormulaid;
    
    public Formula(boolean isFromOtherWindow,  UserProfile up) {
        super("Sale-Purchase Formula",false,true,false,true);
        initComponents();
        this.isFromOtherWindow = isFromOtherWindow;
        this.up = up;
        Dimension d=getSize();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(((int) dim.getWidth() - (int)d.getWidth())/2,((int) dim.getHeight() - (int)d.getHeight())/2-43);
	this.setResizable(false);
        this.setFrameIcon(new ImageIcon(getClass().getResource("/images/formula.png")));
        
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
        
        settings.numvalidatorSimplification(jTextField1);
        settings.numvalidatorSimplification(jTextField2);
        
        Fetch();
        
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(!jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
                {
                    currentFormulaid=null;
                    buttonGroup4.clearSelection();
                    buttonGroup5.clearSelection();
                    jTextField2.setText("");
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
                    jRadioButton4.requestFocusInWindow();
                }
            }
        );
    }
    
    public String getNewFormulaid()
    {
        return newFormulaid;
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
        String a="";
        String rButtonText = GroupButtonUtils.getSelectedButtonText(buttonGroup1);
        try {
            if ( !rButtonText.equals("ALL FORMULA") ) {
                if ( rButtonText.equals("PURCHASE FORMULA") ) {
                    a=" and forsale=0";
                } else {
                    a=" and forsale=1";
                }
            }
        } catch ( NullPointerException ex ) {
            return;
        }
        
        // No. Of Columns: 5
        /* SLN., SALE/PURCHASE, MRP/RATE, FORMULA, IS ACTIVE? */
        int slno1=0;
        clearTable(jTable1);
        // Number of columns in Formula: 5
        /* formulaid, forsale, onmrp, formula, isactive */
        String query="select formulaid, forsale, onmrp, formula, isactive from Formula "
                + "where formulaid<>0"+a+" order by formulaid";
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
                formulaidArray=new String[total];
                int i=0;
		while(rs.next())
		{
                    Vector<String> row = new Vector<String>();
                    row.addElement(++slno1+"");
                    /* formulaid, forsale, onmrp, formula, isactive */
                    formulaidArray[i++]=rs.getString("formulaid");
                    String forsale=rs.getString("forsale");
                    row.addElement(forsale.equals("0")?"Purchase":"Sale");
                    String onmrp=rs.getString("onmrp");
                    row.addElement(onmrp.equals("0")?"Rate":"MRP");
                    row.addElement(rs.getString("formula"));
                    String isactive=rs.getString("isactive");
                    row.addElement(isactive.equals("0")?"In-Active":"Active");
                    ((DefaultTableModel)jTable1.getModel()).addRow(row);
		}
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Formula ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
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
        // No. Of Columns: 5
        /* SLN., SALE/PURCHASE, MRP/RATE, FORMULA, IS ACTIVE? */
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);// SLN.
	jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
	jTable1.getColumnModel().getColumn(1).setMinWidth(0);// SALE/PURCHASE
	jTable1.getColumnModel().getColumn(1).setPreferredWidth(130);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);// MRP/RATE
	jTable1.getColumnModel().getColumn(2).setPreferredWidth(130);
        jTable1.getColumnModel().getColumn(3).setMinWidth(0);// FORMULA
	jTable1.getColumnModel().getColumn(3).setPreferredWidth(240);
        jTable1.getColumnModel().getColumn(4).setMinWidth(0);// IS ACTIVE?
	jTable1.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        // align funda
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        jTable1.getColumn("SLN.").setCellRenderer( centerRenderer );
        jTable1.getColumn("SALE/PURCHASE").setCellRenderer( centerRenderer );
        jTable1.getColumn("MRP/RATE").setCellRenderer( centerRenderer );
        jTable1.getColumn("FORMULA").setCellRenderer( centerRenderer );
        jTable1.getColumn("IS ACTIVE?").setCellRenderer( centerRenderer );
    }
    
    private void keyReleasedTable1()
    {
        if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
        {
            int row=jTable1.getSelectedRow();
            currentFormulaid=formulaidArray[row];
            buttonGroup4.clearSelection();
            String sp=(String)jTable1.getModel().getValueAt(row, 1);
            if(sp.equals("Purchase"))
            {
                jRadioButton8.setSelected(true);// Purchase
                jRadioButton9.setSelected(false);// Sale
            }
            else
            {
                jRadioButton8.setSelected(false);// Purchase
                jRadioButton9.setSelected(true);// Sale
            }
            buttonGroup5.clearSelection();
            String mr=(String)jTable1.getModel().getValueAt(row, 2);
            if(mr.equals("MRP"))
            {
                jRadioButton10.setSelected(true);// MRP
                jRadioButton11.setSelected(false);// Rate
            }
            else
            {
                jRadioButton10.setSelected(false);// MRP
                jRadioButton11.setSelected(true);// Rate
            }
            jTextField2.setText((String)jTable1.getModel().getValueAt(row, 3));
            boolean checkBoxFlag=((String)jTable1.getModel().getValueAt(row, 4)).equals("Active");
            jCheckBox1.setSelected(checkBoxFlag);
        }
    }
    
    private void insertToDatabase()
    {
        // Number of columns in Formula: 5
        /* formulaid, forsale, onmrp, formula, isactive */
        String forsale = "";
        String rButtonText = GroupButtonUtils.getSelectedButtonText(buttonGroup2);
        if ( rButtonText.equals("PURCHASE") ) {
            forsale = "0";
        } else {
            forsale = "1";
        }
        String onmrp = "";
        rButtonText = GroupButtonUtils.getSelectedButtonText(buttonGroup3);
        if ( rButtonText.equals("MRP") ) {
            onmrp = "1";
        } else {
            onmrp = "0";
        }
        String formula=jTextField1.getText().trim().toUpperCase();
        if(formula.length()==0)
        {
            JOptionPane.showMessageDialog(null,"Formula is mandatory!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField1.requestFocusInWindow();
            return;
        } else {
            char firstChar = formula.charAt(0);
            if ( firstChar != '+' && firstChar != '-' && firstChar != '*' && firstChar != '/' ) {
                JOptionPane.showMessageDialog(null,"Formula must begin with OPEATOR: +, -, *, /",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField1.requestFocusInWindow();
                return;
            }
        }
        String isactive="1";
        
        // Duplicate Testing not required
        
        int formulaid=q.getMaxId("Formula", "formulaid");
        formulaid++;
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Formula: 5
        /* formulaid, forsale, onmrp, formula, isactive */
        String query="insert into Formula (formulaid, forsale, onmrp, formula, isactive) values (" 
                + formulaid+", "+forsale+", "+onmrp+", '"+formula+"', "+isactive+")";
        System.out.println(query);
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Formula ex?: "+ex.getMessage(),
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
            newFormulaid = formulaid+"";
            setVisible(true);
            dispose();
        }
        else
        {
            buttonGroup2.clearSelection();
            jRadioButton4.setSelected(true);
            buttonGroup3.clearSelection();
            jRadioButton6.setSelected(true);
            jTextField1.setText("");
            Fetch();
        }
    }
    
    private void updateToDatabase()
    {
        // Number of columns in Formula: 5
        /* formulaid, forsale, onmrp, formula, isactive */
        String forsale = "";
        String rButtonText = GroupButtonUtils.getSelectedButtonText(buttonGroup4);
        if ( rButtonText.equals("PURCHASE") ) {
            forsale = "0";
        } else {
            forsale = "1";
        }
        String onmrp = "";
        rButtonText = GroupButtonUtils.getSelectedButtonText(buttonGroup5);
        if ( rButtonText.equals("MRP") ) {
            onmrp = "1";
        } else {
            onmrp = "0";
        }
        String formula=jTextField2.getText().trim().toUpperCase();
        if(formula.length()==0)
        {
            JOptionPane.showMessageDialog(null,"Formula is mandatory!",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            jTextField2.requestFocusInWindow();
            return;
        } else {
            char firstChar = formula.charAt(0);
            if ( firstChar != '+' && firstChar != '-' && firstChar != '*' && firstChar != '/' ) {
                JOptionPane.showMessageDialog(null,"Formula must begin with OPEATOR: +, -, *, /",
                    "Error Found",JOptionPane.ERROR_MESSAGE);
                jTextField2.requestFocusInWindow();
                return;
            }
        }
        String isactive=jCheckBox1.isSelected()?"1":"0";
        
        // Duplicate Testing not required
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        // Number of columns in Formula: 5
        /* formulaid, forsale, onmrp, formula, isactive */
        String query="update Formula set forsale="+forsale+", onmrp="+onmrp+", formula='"+formula
                + "', isactive="+isactive+" where formulaid="+currentFormulaid;
        System.out.println(query);
        try {
            Statement stm=conn.createStatement();
            stm.executeUpdate(query);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Formula ex?: "+ex.getMessage(),
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
            newFormulaid = currentFormulaid;
            setVisible(true);
            dispose();
        }
        else
        {
            currentFormulaid = null;
            buttonGroup4.clearSelection();
            buttonGroup5.clearSelection();
            jTextField2.setText("");
            jCheckBox1.setSelected(false);
            Fetch();
        }
    }
    
    private void deleteToDatabase()
    {
        if(jTable1.getSelectedRow()!=-1&&jTable1.getSelectedColumn()!=-1) {
            String formulaid=formulaidArray[jTable1.getSelectedRow()];
            String ObjButtons[] = {"Yes","Cancel"};
            int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Delete the Category Information!","Delete Record",
                    JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
            if(PromptResult==0) {
                dBConnection db=new dBConnection();
                Connection conn=db.setConnection();
                String query="delete from Formula where formulaid="+formulaid;
                try {
                    Statement smt=(Statement) conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                    smt.executeUpdate(query);
                } catch(SQLException ex) {
                    JOptionPane.showMessageDialog(null,"Formula ex?: "+ex.getMessage(),
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
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

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "FORMULA DETAILS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SLN.", "SALE/PURCHASE", "MRP/RATE", "FORMULA", "IS ACTIVE?"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
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

        jPanel1.setBackground(new java.awt.Color(226, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 255), 2, true), "FORMULA SEARCH", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("ALL FORMULA");
        jRadioButton1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton1ItemStateChanged(evt);
            }
        });
        jRadioButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton1KeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton2.setText("PURCHASE FORMULA");
        jRadioButton2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton2ItemStateChanged(evt);
            }
        });
        jRadioButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton2KeyPressed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton3.setText("SALE FORMULA");
        jRadioButton3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButton3ItemStateChanged(evt);
            }
        });
        jRadioButton3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton3KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton3)
                .addContainerGap(156, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("PURCHASE");
        jRadioButton4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton4KeyPressed(evt);
            }
        });

        buttonGroup2.add(jRadioButton5);
        jRadioButton5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton5.setText("SALE");
        jRadioButton5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton5KeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("FORMULA FOR");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("CALCULATE ON");

        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton6.setSelected(true);
        jRadioButton6.setText("MRP");
        jRadioButton6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton6KeyPressed(evt);
            }
        });

        buttonGroup3.add(jRadioButton7);
        jRadioButton7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton7.setText("RATE");
        jRadioButton7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton7KeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("FORMULA");

        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("SAVE");
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton7))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton5))
                            .addComponent(jLabel3)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton5)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButton6)
                    .addComponent(jRadioButton7)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ADD", jPanel2);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("FORMULA FOR");

        buttonGroup4.add(jRadioButton8);
        jRadioButton8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton8.setText("PURCHASE");
        jRadioButton8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton8KeyPressed(evt);
            }
        });

        buttonGroup4.add(jRadioButton9);
        jRadioButton9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton9.setText("SALE");
        jRadioButton9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton9KeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("CALCULATE ON");

        buttonGroup5.add(jRadioButton10);
        jRadioButton10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton10.setText("MRP");
        jRadioButton10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton10KeyPressed(evt);
            }
        });

        buttonGroup5.add(jRadioButton11);
        jRadioButton11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRadioButton11.setText("RATE");
        jRadioButton11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jRadioButton11KeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("FORMULA");

        jTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField2FocusGained(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
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

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jCheckBox1.setText("IS ACTIVE ?");
        jCheckBox1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jCheckBox1KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton11))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton9))
                            .addComponent(jLabel6)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButton8)
                    .addComponent(jRadioButton9)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jRadioButton10)
                    .addComponent(jRadioButton11)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 237, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        jTabbedPane1.addTab("ALTER", jPanel3);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("SELECT A RECORD FROM THE TABLE, THEN DELETE ...");

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
                    .addComponent(jLabel7)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 382, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
        moveToFront();
    }//GEN-LAST:event_formInternalFrameIconified

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton1ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton1ItemStateChanged

    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton2ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton2ItemStateChanged

    private void jRadioButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton1KeyPressed

    private void jRadioButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton3.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton2KeyPressed

    private void jRadioButton3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButton3ItemStateChanged
        Fetch();
    }//GEN-LAST:event_jRadioButton3ItemStateChanged

    private void jRadioButton3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton3KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if ( jTable1.getRowCount() != 0 ) {
                jTable1.changeSelection(0, 0, false, false);
                jTable1.requestFocusInWindow();
            }
        }
    }//GEN-LAST:event_jRadioButton3KeyPressed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
            {
                jRadioButton8.requestFocusInWindow();
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

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if (evt.getClickCount() == 2)
        {
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("ALTER"))
            {
                jRadioButton8.requestFocusInWindow();
                evt.consume();
            }
            if (jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()).equals("DELETE"))
            {
                jButton3.requestFocusInWindow();
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        keyReleasedTable1();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jRadioButton4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton4KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton5.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton4KeyPressed

    private void jRadioButton5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton5KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton6.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton5KeyPressed

    private void jRadioButton6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton6KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton7.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton6KeyPressed

    private void jRadioButton7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton7KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton7KeyPressed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jRadioButton8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton8KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton9.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton8KeyPressed

    private void jRadioButton9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton9KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton10.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton9KeyPressed

    private void jRadioButton10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton10KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jRadioButton11.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton10KeyPressed

    private void jRadioButton11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jRadioButton11KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jTextField2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jRadioButton11KeyPressed

    private void jTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField2FocusGained
        jTextField2.selectAll();
    }//GEN-LAST:event_jTextField2FocusGained

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jCheckBox1.requestFocusInWindow();
        }
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jCheckBox1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jCheckBox1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            jButton2.requestFocusInWindow();
        }
    }//GEN-LAST:event_jCheckBox1KeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        insertToDatabase();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            insertToDatabase();
        }
    }//GEN-LAST:event_jButton1KeyPressed

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

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        String x = jTextField1.getText().trim();
        if ( x.length() != 0 ) {
            jTextField1.selectAll();
        }
    }//GEN-LAST:event_jTextField1FocusGained


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
