package ssp;

import conn.BackupDB;
import conn.RestoreDB;
import conn.dBConnection;
import dto.UserProfile;
import java.awt.Dimension;
import java.awt.Toolkit;
import dto.Enterprise;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import query.Query;
import reports.PartywiseProductSaleReport;
import reports.SalePaymentReport;
import reports.Sale_GSTReport;
import reports.StockNotification;
import reports.StockValueReport;
import utilities.MyCustomFilterDBBackup;
import utilities.clsTimer;

public class MainWindow extends javax.swing.JFrame {

    private UserProfile up=new UserProfile();
    private String loginTime="";
    private Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    private Enterprise e;
    private Query q=new Query();
    private boolean enterpriseSetupFlag;
    public boolean openReservationPanelFlag;

    public MainWindow(UserProfile up, String loginTime) 
    {
        super("SUPER-STOCKIST PRO ++");
        initComponents();
        this.up=up;
        this.loginTime=loginTime;

        jMenu6.setMnemonic('F');// File
        jMenu2.setMnemonic('Y');// System Setup
        jMenu1.setMnemonic('B');// Business Setup
        jMenu9.setMnemonic('U');// Purchase
        jMenu11.setMnemonic('S');// Sale
        jMenu13.setMnemonic('N');// Print
        jMenu15.setMnemonic('M');// Miscellaneous Reports

        this.setIconImage(new ImageIcon(getClass().getResource("/images/icon.png")).getImage());
        this.setSize(screen);
        new clsTimer(jLabel3, 1);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                String ObjButtons[] = {"Yes","Cancel"};
                int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Leave from Application","Bye...",
                        JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
                if(PromptResult==0)
                {
                    // Mandatory Database Backup
                    JFileChooser fc= new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showSaveDialog(MainWindow.this);
                    String path="";
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        path = fc.getSelectedFile().getAbsolutePath();
                        BackupDB.copyFile(path);
                    }
                    
                    Calendar cal=Calendar.getInstance();
                    Date date = cal.getTime();
                    DateFormat dateFormatter = DateFormat.getTimeInstance();
                    String logoutTime=DateFormat.getDateInstance().format(date) + " [ " + dateFormatter.format(date) + " ]";
                    String query="update LoginDetails set logout='"+logoutTime+"' where uid="+MainWindow.this.up.getUid()+" and login='"+MainWindow.this.loginTime+"'";
                    dBConnection db=new dBConnection();
                    Connection conn=db.setConnection();
                    try
                    {
                        Statement stm=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                        stm.executeUpdate(query);
                    }
                    catch(SQLException ex)
                    {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,"MainWindow Ex1: "+ex,"Error Found",JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    finally
                    {
                        try {
                            if (conn!=null) conn.close();
                        }
                        catch(SQLException ex){}
                    }
                    setVisible(false);
                    dispose();
                    System.exit(0);
                }
            }
        });

        e=q.getEnterprise();
        if(e==null)
            enterpriseSetupFlag=false;
        else
        {
            jLabel7.setText("CURRENT USER: "+up.getUnm());
            enterpriseSetupFlag=true;
        }
        if(!enterpriseSetupFlag)
        {
            JOptionPane.showMessageDialog(null,"Your Enterprise Setup Incomplete, Open System Settings->Enterprise Add to provide it.",
                    "Enterprise Setup Message",JOptionPane.WARNING_MESSAGE);
        }
        
        SwingUtilities.invokeLater(
            new Runnable() 
            {
                @Override
                public void run() 
                {
                    jLabel8.setIcon(new ImageIcon (Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/bgimg.jpg")).
                        getScaledInstance(jLabel8.getWidth(), jLabel8.getHeight(), Image.SCALE_SMOOTH)));
                }
            }
        );
    }

    private void runComponents(String sComponents)
    {
	Runtime rt = Runtime.getRuntime();
	try{rt.exec(sComponents);}
	catch(IOException evt)
        {
            JOptionPane.showMessageDialog(null,evt.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jLabel8 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem62 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu16 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenu17 = new javax.swing.JMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenu9 = new javax.swing.JMenu();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenu12 = new javax.swing.JMenu();
        jMenu13 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenu14 = new javax.swing.JMenu();
        jMenu15 = new javax.swing.JMenu();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();

        jLabel5.setBackground(new java.awt.Color(204, 255, 204));
        jLabel5.setOpaque(true);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        jToolBar1.setBackground(new java.awt.Color(205, 222, 238));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel4.setText("          ");
        jToolBar1.add(jLabel4);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 204));
        jLabel3.setText("10:10");
        jToolBar1.add(jLabel3);
        jToolBar1.add(filler1);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 0, 0));
        jLabel7.setText("CURRENT USER: N/A");
        jToolBar1.add(jLabel7);

        jLabel6.setText("          ");
        jToolBar1.add(jLabel6);

        jLabel8.setBackground(new java.awt.Color(204, 255, 204));
        jLabel8.setOpaque(true);

        jDesktopPane1.setLayer(jLabel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
        );

        jMenu4.setText("    ");
        jMenu4.setEnabled(false);
        jMenuBar1.add(jMenu4);

        jMenu6.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Log Details.png"))); // NOI18N
        jMenuItem1.setText("Login Details");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem1);
        jMenu6.add(jSeparator2);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calc.png"))); // NOI18N
        jMenuItem2.setText("Calculator");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem2);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notepad.png"))); // NOI18N
        jMenuItem3.setText("Notepad");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem3);
        jMenu6.add(jSeparator3);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/EXIT.PNG"))); // NOI18N
        jMenuItem4.setText("Exit");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem4);

        jMenuBar1.add(jMenu6);

        jMenu3.setText("    ");
        jMenu3.setEnabled(false);
        jMenuBar1.add(jMenu3);

        jMenu2.setText("System Setup");

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Branches.png"))); // NOI18N
        jMenuItem5.setText("Edit Enterprise Data");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SalesRep.png"))); // NOI18N
        jMenuItem6.setText("User Profiles");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem62.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem62.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar_year.png"))); // NOI18N
        jMenuItem62.setText("Financial Year");
        jMenuItem62.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem62ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem62);
        jMenu2.add(jSeparator1);

        jMenuItem34.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/db_backup.png"))); // NOI18N
        jMenuItem34.setText("Database Backup");
        jMenuItem34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem34ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem34);

        jMenuItem13.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/db_restore.png"))); // NOI18N
        jMenuItem13.setText("Database Restore");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuBar1.add(jMenu2);

        jMenu7.setText("    ");
        jMenu7.setEnabled(false);
        jMenuBar1.add(jMenu7);

        jMenu1.setText("Business Setup");

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paymentmode.png"))); // NOI18N
        jMenuItem10.setText("Payment Mode Master");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);

        jMenu8.setText("Item");

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/company.png"))); // NOI18N
        jMenuItem9.setText("Company Master");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem9);

        jMenuItem12.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/SUPPLIER.PNG"))); // NOI18N
        jMenuItem12.setText("Company Stockist");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem12);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/categories.png"))); // NOI18N
        jMenuItem8.setText("Item Category");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem8);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/measuringunit.png"))); // NOI18N
        jMenuItem11.setText("Measuring Unit");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem11);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/item.png"))); // NOI18N
        jMenuItem7.setText("Item Master");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem7);

        jMenu1.add(jMenu8);

        jMenu16.setText("Distributer Master");

        jMenuItem17.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/beat.png"))); // NOI18N
        jMenuItem17.setText("Beat Master");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu16.add(jMenuItem17);

        jMenuItem18.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/customer.png"))); // NOI18N
        jMenuItem18.setText("Distributer Master");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu16.add(jMenuItem18);

        jMenu1.add(jMenu16);

        jMenu17.setText("Purchase-Sale Rate");

        jMenuItem20.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/formula.png"))); // NOI18N
        jMenuItem20.setText("Purchase-Sale Formula");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu17.add(jMenuItem20);

        jMenuItem21.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/formula_mapping.png"))); // NOI18N
        jMenuItem21.setText("Purchase-Sale Formula Mapping");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu17.add(jMenuItem21);

        jMenu1.add(jMenu17);

        jMenuBar1.add(jMenu1);

        jMenu5.setText("    ");
        jMenu5.setEnabled(false);
        jMenuBar1.add(jMenu5);

        jMenu9.setText("Purchase");

        jMenuItem14.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/purchase01.png"))); // NOI18N
        jMenuItem14.setText("Purchase");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem14);

        jMenuItem24.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/MODIFY.PNG"))); // NOI18N
        jMenuItem24.setText("Edit Purchase");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem24);

        jMenuBar1.add(jMenu9);

        jMenu10.setText("    ");
        jMenu10.setEnabled(false);
        jMenuBar1.add(jMenu10);

        jMenu11.setText("Sale");

        jMenuItem16.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/prod_sale_01.png"))); // NOI18N
        jMenuItem16.setText("Sale");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem16);

        jMenuItem22.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/MODIFY.PNG"))); // NOI18N
        jMenuItem22.setText("Edit Sale");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem22);

        jMenuItem23.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report01.png"))); // NOI18N
        jMenuItem23.setText("Sale-GST Report");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem23);

        jMenuItem25.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/payments.png"))); // NOI18N
        jMenuItem25.setText("Sale Due Payment");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem25);

        jMenuItem26.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report04.png"))); // NOI18N
        jMenuItem26.setText("Partywise Product Sale Report");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem26);

        jMenuItem27.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/report05.png"))); // NOI18N
        jMenuItem27.setText("Sale Payment Report");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem27);

        jMenuBar1.add(jMenu11);

        jMenu12.setText("    ");
        jMenu12.setEnabled(false);
        jMenuBar1.add(jMenu12);

        jMenu13.setText("Print");

        jMenuItem19.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/PRINT.PNG"))); // NOI18N
        jMenuItem19.setText("Re-print Sale Bill");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu13.add(jMenuItem19);

        jMenuBar1.add(jMenu13);

        jMenu14.setText("    ");
        jMenu14.setEnabled(false);
        jMenuBar1.add(jMenu14);

        jMenu15.setText("Miscellaneous Reports");

        jMenuItem15.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/notification.png"))); // NOI18N
        jMenuItem15.setText("Stock Notification");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu15.add(jMenuItem15);

        jMenuItem28.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stockval01.png"))); // NOI18N
        jMenuItem28.setText("Stock Value Report");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu15.add(jMenuItem28);

        jMenuBar1.add(jMenu15);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 861, Short.MAX_VALUE)
            .addComponent(jDesktopPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Login Details - 
        try {
            LoginDetails ref=new LoginDetails();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
}//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // Calculator
        runComponents("Calc.exe");
}//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // Notepad
        runComponents("Notepad.exe");
}//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // Exit Menu Item
        String ObjButtons[] = {"Yes","Cancel"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure to Leave from Application",
                "Bye...",JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult==0) 
        {
            // Mandatory Database Backup
            JFileChooser fc= new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(MainWindow.this);
            String path="";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fc.getSelectedFile().getAbsolutePath();
                BackupDB.copyFile(path);
            }
            
            Calendar cal=Calendar.getInstance();
            Date date = cal.getTime();
            DateFormat dateFormatter = DateFormat.getTimeInstance();
            String logoutTime=DateFormat.getDateInstance().format(date) + " [ " + dateFormatter.format(date) + " ]";
            String query="update LoginDetails set logout='"+logoutTime+"' where uid='"+MainWindow.this.up.getUid()+"' and login='"+MainWindow.this.loginTime+"'";
            dBConnection db=new dBConnection();
            Connection conn=db.setConnection();
            try {
                Statement stm=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                stm.executeUpdate(query);
            } catch(SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,"MainWindow Ex2: "+ex,"Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                try {
                    if (conn!=null) conn.close();
                } catch(SQLException ex){}
            }
            this.setVisible(false);
            this.dispose();
            System.exit(0);
        }
}//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // Enterprise Edit
        try {
            EnterpriseEdit ref=new EnterpriseEdit();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
}//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // User Profile
        try {
            UserProfiles ref=new UserProfiles(up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
}//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem62ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem62ActionPerformed
        // Finalcial Year - calendar_year.png
        try {
            FinancialYear ref=new FinancialYear(up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem62ActionPerformed

    private void jMenuItem34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem34ActionPerformed
        // Database Backup
        JFileChooser fc= new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showSaveDialog(MainWindow.this);
        String path="";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            path = fc.getSelectedFile().getAbsolutePath();
        }
        BackupDB.copyFile(path);
    }//GEN-LAST:event_jMenuItem34ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // Database Restore - MODIFY.PNG
        JFileChooser fc= new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new MyCustomFilterDBBackup());
        int returnVal = fc.showSaveDialog(MainWindow.this);
        String path="";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            path = fc.getSelectedFile().getAbsolutePath();
        }
        System.out.println("Backup File Path= "+path);
        RestoreDB.copyFile(path);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // Item Master - item.png
        try {
            ItemMaster ref=new ItemMaster(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // Item Category Master - categories.png
        try {
            ItemCategoryMaster ref=new ItemCategoryMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        } catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // Payment Mode Master - paymentmode.png
        try
        {
            PaymentModeMaster ref=new PaymentModeMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // Company Master - company.png
        try
        {
            CompanyMaster ref=new CompanyMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // Measuring Unit - measuringunit.png
        try
        {
            MeasuringUnitMaster ref=new MeasuringUnitMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // Super Stockist - SUPPLIER.PNG
        try
        {
            CompanyStockist ref=new CompanyStockist(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        // Purchase - purchase01.png
        try
        {
            Purchase ref=new Purchase(jDesktopPane1, up, false);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // Stock Notification - notification.png
        try
        {
            StockNotification ref=new StockNotification();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        // Sale - prod_sale_01.png
        try
        {
            Sale ref=new Sale(jDesktopPane1, up, e);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        // Beat Master - beat.png
        try
        {
            BeatMaster ref=new BeatMaster(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // Distributor Master - customer.png
        try
        {
            DistributerMaster ref=new DistributerMaster(jDesktopPane1, false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        // Reprint Sale Bill - PRINT.PNG
        try
        {
            ReprintSaleBill ref=new ReprintSaleBill(jDesktopPane1, e);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        // Purchase-Sale Formula - formula.png
        try
        {
            Formula ref=new Formula(false, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // Purchase-Sale Formula Mapping - formula_mapping.png
        try
        {
            PurchaseSaleFormulaMapping ref=new PurchaseSaleFormulaMapping(false, up, jDesktopPane1);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        // Edit Sale 01 - MODIFY.PNG
        try
        {
            EditSale01 ref=new EditSale01(jDesktopPane1, e, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        // Sale-GST Report - report01.png
        try
        {
            Sale_GSTReport ref=new Sale_GSTReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        // Edit Sale 01 - MODIFY.PNG
        try
        {
            EditPurchase01 ref=new EditPurchase01(jDesktopPane1, e, up);
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        // Sale Due Payment - payments.png
        try
        {
            SaleDuePayment ref=new SaleDuePayment();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        // Partywise Product Sale Report - report04.png
        try
        {
            PartywiseProductSaleReport ref=new PartywiseProductSaleReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem26ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        // Sale Payment Report - report05.png
        try
        {
            SalePaymentReport ref=new SalePaymentReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        // Stock Value Report - stockval01.png
        try
        {
            StockValueReport ref=new StockValueReport();
            ref.setVisible(true);
            jDesktopPane1.add(ref);
            ref.show();
            ref.setIcon(false);
            ref.setSelected(true);
        }
        catch(PropertyVetoException e){}
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu14;
    private javax.swing.JMenu jMenu15;
    private javax.swing.JMenu jMenu16;
    private javax.swing.JMenu jMenu17;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem62;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

}
