/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import conn.dBConnection;
import dto.Distributer;
import dto.Enterprise;
import dto.PurchaseMaster;
import dto.PurchaseSub;
import dto.SaleMaster;
import dto.SaleSub;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import utilities.DateConverter;

/**
 *
 * @author Jayanta B. Sen
 */
public class Query {
    
    public String passwordEncript(String pwd)
    {
        StringBuffer result=new StringBuffer("");
        for(int i=0;i<pwd.length();i++)
        {
            char ch=pwd.charAt(i);
            int ich=(int)ch;
            int alteredIch=ich+(i%2==0?(i+1):(i+1)*(-1));
            char alteredCh=(char)alteredIch;
            result.append(alteredCh);
        }
        return result.toString();
    }
    
    public String passwordDecript(String pwd)
    {
        StringBuffer result=new StringBuffer("");
        for(int i=0;i<pwd.length();i++)
        {
            char ch=pwd.charAt(i);
            int ich=(int)ch;
            int alteredIch=ich+(i%2==0?(i+1)*(-1):(i+1));
            char alteredCh=(char)alteredIch;
            result.append(alteredCh);
        }
        return result.toString();
    }

    public int getMaxId(String tableNm,String fieldNm)
    {
        int total=0;
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        String query="select max("+fieldNm+") as x from "+tableNm;
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                String stotal = rs.getString("x");
                if ( stotal != null )
                {
                    total = Integer.parseInt(stotal);
                }
            }
        }
        catch(NumberFormatException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query:getNextId ex1: "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query:getNextId ex2: "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        return total;
    }

    public Enterprise getEnterprise()
    {
        // Number of columns in Enterprise: 16
        /* ename, estreet, ecity, edist, estate, estatecode, epin, ecountry, 
        eabbr, econtact, email, efax, egstno, egstregntype epanno, eaadhaarno */
        Enterprise result=null;
        String query="select ename, estreet, ecity, edist, estate, estatecode, "
                + "epin, ecountry, eabbr, econtact, email, efax, egstno, "
                + "egstregntype epanno, eaadhaarno from Enterprise";
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement stm=conn.createStatement();
            ResultSet rs=stm.executeQuery(query);
            if(rs.next())
            {
                result=new Enterprise();
                result.setEname(rs.getString("ename"));
                result.setEstreet(rs.getString("estreet"));
                result.setEcity(rs.getString("ecity"));
                result.setEdist(rs.getString("edist"));
                result.setEstate(rs.getString("estate"));
                result.setEstatecode(rs.getString("estatecode"));
                result.setEpin(rs.getString("epin"));
                result.setEcountry(rs.getString("ecountry"));
                result.setEabbr(rs.getString("eabbr"));
                result.setEcontact(rs.getString("econtact"));
                result.setEmail(rs.getString("email"));
                result.setEfax(rs.getString("efax"));
                result.setEgstno(rs.getString("egstno"));
                result.setEgstregntype(rs.getString("egstregntype"));
                result.setEpanno(rs.getString("epanno"));
                result.setEaadhaarno(rs.getString("eaadhaarno"));
            }
        }
        catch(SQLException ex)// ex1
        {
            JOptionPane.showMessageDialog(null,"Query:getEnterprise ex1: "+ex.getMessage(),"Error Found",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException ex){}
        }
        return result;
    }
    
    public Date parseDate(String date, String format)
    {
        Date dt=null;
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            dt=formatter.parse(date);
        }
        catch(ParseException ex) {}
        return dt;
    }
    
    public Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    
    public boolean isNumeric(String str)
    {  
        try  
        {  
            double d = Double.parseDouble(str);  
        }  
        catch(NumberFormatException nfe)  
        {  
            return false;  
        }  
        return true;  
    }
    
    public int insertToPurchaseMaster(PurchaseMaster pm)
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int pmid = getMaxId("PurchaseMaster", "pmid");
        int psid = getMaxId("PurchaseSub", "psid");

        // Number of columns in PurchaseMaster: 27
        /* pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        String insertTableSQL1 = "insert into PurchaseMaster (pmid, csid, compid, invno, invdt, deliverynote, "
                + "payterm, ordno, orddt, transporter, vehicleno, supplydt, netqty, netamt, nettaxableamt,"
                + " netcgst, netsgst, netigst, nettotal, roundoff, netamt01, advance, netamt02, isopening,"
                + " amtpaid, isactive, remarks) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        String insertTableSQL2 = "insert into PurchaseSub (psid, pmid, itemid, qty, mrp, rate, amt, discper, "
                + "discamt, taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, "
                + "qtysold, retqty) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in PurchaseMaster: 27
            /* pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setInt(1, ++pmid);
            preparedStatementInsert1.setInt(2, Integer.parseInt(pm.getCsid()));
            preparedStatementInsert1.setInt(3, Integer.parseInt(pm.getCompid()));
            preparedStatementInsert1.setString(4, pm.getInvno());
            preparedStatementInsert1.setDate(5, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            preparedStatementInsert1.setString(6, pm.getDeliverynote());
            preparedStatementInsert1.setString(7, pm.getPayterm());
            preparedStatementInsert1.setString(8, pm.getOrdno());
            preparedStatementInsert1.setDate(9, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getOrddt())));
            preparedStatementInsert1.setString(10, pm.getTransporter());
            preparedStatementInsert1.setString(11, pm.getVehicleno());
            preparedStatementInsert1.setDate(12, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getSupplydt())));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(pm.getNetqty()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(pm.getNetamt()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(pm.getNettaxableamt()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(pm.getNetcgst()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(pm.getNetsgst()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(pm.getNetigst()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(pm.getNettotal()));
            preparedStatementInsert1.setDouble(20, Double.parseDouble(pm.getRoundoff()));
            preparedStatementInsert1.setDouble(21, Double.parseDouble(pm.getNetamt01()));
            preparedStatementInsert1.setDouble(22, Double.parseDouble(pm.getAdvance()));
            preparedStatementInsert1.setDouble(23, Double.parseDouble(pm.getNetamt02()));
            preparedStatementInsert1.setInt(24, Integer.parseInt(pm.getIsopening()));
            preparedStatementInsert1.setDouble(25, Double.parseDouble(pm.getAmtpaid()));
            preparedStatementInsert1.setInt(26, Integer.parseInt(pm.getIsactive()));
            preparedStatementInsert1.setString(27, pm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(PurchaseSub ps : pm.getPsAl())
            {
                // Number of columns in PurchaseSub: 19
                /* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
                sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                preparedStatementInsert2.setInt(1, ++psid);
                preparedStatementInsert2.setInt(2, pmid);
                preparedStatementInsert2.setInt(3, Integer.parseInt(ps.getItemid()));
                preparedStatementInsert2.setDouble(4, Double.parseDouble(ps.getQty()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ps.getMrp()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ps.getRate()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ps.getAmt()));
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ps.getDiscper()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ps.getDiscamt()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ps.getTaxableamt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ps.getCgstper()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ps.getCgstamt()));
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ps.getSgstper()));
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ps.getSgstamt()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ps.getIgstper()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ps.getIgstamt()));
                preparedStatementInsert2.setDouble(17, Double.parseDouble(ps.getTotal()));
                preparedStatementInsert2.setDouble(18, Double.parseDouble(ps.getQtysold()));
                preparedStatementInsert2.setDouble(19, Double.parseDouble(ps.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToPurchaseMaster ex1: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return 0;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return pmid;
    }
    
    public PurchaseMaster getPurchaseMaster( String pmid )
    {
        PurchaseMaster pm=new PurchaseMaster();
        // Number of columns in PurchaseMaster: 27
        /* pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        String query="select pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, "
                + "transporter, vehicleno, supplydt, netqty, netamt, nettaxableamt, netcgst, "
                + "netsgst, netigst, nettotal, roundoff, netamt01, advance, netamt02, isopening,"
                + " amtpaid, isactive, remarks from PurchaseMaster where pmid="+pmid; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            while(rs.next())
            {
                // Number of columns in PurchaseMaster: 27
            /* pmid, csid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
                pm.setPmid(pmid);
                pm.setCsid(rs.getString("csid"));
                pm.setCompid(rs.getString("compid"));
                pm.setInvno(rs.getString("invno"));
                pm.setInvdt(DateConverter.dateConverter(rs.getString("invdt")));
                pm.setDeliverynote(rs.getString("deliverynote"));
                pm.setPayterm(rs.getString("payterm"));
                pm.setOrdno(rs.getString("ordno"));
                pm.setOrddt(DateConverter.dateConverter(rs.getString("orddt")));
                pm.setTransporter(rs.getString("transporter"));
                pm.setVehicleno(rs.getString("vehicleno"));
                pm.setSupplydt(DateConverter.dateConverter(rs.getString("supplydt")));
                pm.setNetqty(rs.getString("netqty"));
                pm.setNetamt(rs.getString("netamt"));
                pm.setNettaxableamt(rs.getString("nettaxableamt"));
                pm.setNetcgst(rs.getString("netcgst"));
                pm.setNetsgst(rs.getString("netsgst"));
                pm.setNetigst(rs.getString("netigst"));
                pm.setNettotal(rs.getString("nettotal"));
                pm.setRoundoff(rs.getString("roundoff"));
                pm.setNetamt01(rs.getString("netamt01"));
                pm.setAdvance(rs.getString("advance"));
                pm.setNetamt02(rs.getString("netamt02"));
                pm.setIsopening(rs.getString("isopening"));
                pm.setAmtpaid(rs.getString("amtpaid"));
                pm.setIsactive(rs.getString("isactive"));
                pm.setRemarks(rs.getString("remarks"));
                
                // Getting Formula Phase
                ArrayList<PurchaseSub> psAl=new ArrayList<PurchaseSub>();
                // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                query="select psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt,"
                        + " cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, qtysold,"
                        + " retqty from PurchaseSub where pmid="+pmid; 
                try
                {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next())
                    {    
                        PurchaseSub ps = new PurchaseSub();
                        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                        ps.setPsid(rs1.getString("psid"));
                        ps.setPmid(rs1.getString("pmid"));
                        ps.setItemid(rs1.getString("itemid"));
                        ps.setQty(rs1.getString("qty"));
                        ps.setMrp(rs1.getString("mrp"));
                        ps.setRate(rs1.getString("rate"));
                        ps.setAmt(rs1.getString("amt"));
                        ps.setDiscper(rs1.getString("discper"));
                        ps.setDiscamt(rs1.getString("discamt"));
                        ps.setTaxableamt(rs1.getString("taxableamt"));
                        ps.setCgstper(rs1.getString("cgstper"));
                        ps.setCgstamt(rs1.getString("cgstamt"));
                        ps.setSgstper(rs1.getString("sgstper"));
                        ps.setSgstamt(rs1.getString("sgstamt"));
                        ps.setIgstper(rs1.getString("igstper"));
                        ps.setIgstamt(rs1.getString("igstamt"));
                        ps.setTotal(rs1.getString("total"));
                        ps.setQtysold(rs1.getString("qtysold"));
                        ps.setRetqty(rs1.getString("retqty"));
                        psAl.add(ps);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getPurchaseMaster ex2: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                pm.setPsAl(psAl);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getPurchaseMaster ex3: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return pm;
    }
    
    public void adjustQty()
    {
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        ArrayList<String> dataAl = new ArrayList<String>();
        String query="select itemid, sum(qty-(qtysold+retqty)) as avlqty from PurchaseSub group by itemid";
        System.out.println(query);
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement();
            ResultSet rs=smt.executeQuery(query);
            while (rs.next())
            {
                dataAl.add(rs.getString("itemid")+"~"+rs.getString("avlqty"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: adjustQty ex1: "+ex.getMessage(),
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
        
        conn=db.setConnection();
        try
        {
            conn.setAutoCommit(false);
            
            if ( dataAl.size() != 0)
            {
                // Number of columns in ItemStock: 3
                /* isid, itemid, qtyonhand */
                String sql1 = "update ItemStock set qtyonhand=? where itemid=?";
                PreparedStatement psmt1 = conn.prepareStatement(sql1);
                for ( String s : dataAl )
                {
                    String x[] = s.split("~");
                    psmt1.setDouble(1, Double.parseDouble(x[1]));
                    psmt1.setInt(2, Integer.parseInt(x[0]));
                    psmt1.addBatch();
                }
                psmt1.executeBatch();
            }
            
            conn.commit();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: adjustQty ex1: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        }
        finally
        {
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public boolean insertToSaleMaster(SaleMaster sm)
    {
        System.out.println("step 1 insertToSaleMaster call");
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int salesid = getMaxId("SaleSub", "salesid");

        // Number of columns in SaleMaster: 27
        /* salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
        String insertTableSQL1 = "insert into SaleMaster (salemid, compid, distid, saledt, ordno, orddt, deliverynote,"
                + " paymentterm, transporter, vehicleno, supplydt, netqty, netamt, nettaxableamt, netcgst,"
                + " netsgst, netigst, nettotal, roundoff, netamt01, displaynote, displayamt, advance, "
                + "netamt02, amtpaid, isactive, remarks) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String insertTableSQL2 = "insert into SaleSub (salesid, salemid, psid, itemid, qty, "
                + "mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, sgstper, "
                + "sgstamt, igstper, igstamt, total, retqty) values (?, ?, ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        System.gc();
        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in SaleMaster: 27
            /* salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setString(1, sm.getSalemid());
            preparedStatementInsert1.setInt(2, Integer.parseInt(sm.getCompid()));
            preparedStatementInsert1.setInt(3, Integer.parseInt(sm.getDistid()));
            preparedStatementInsert1.setDate(4, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
            preparedStatementInsert1.setString(5, sm.getOrdno());
            preparedStatementInsert1.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getOrddt())));
            preparedStatementInsert1.setString(7, sm.getDeliverynote());
            preparedStatementInsert1.setString(8, sm.getPaymentterm());
            preparedStatementInsert1.setString(9, sm.getTransporter());
            preparedStatementInsert1.setString(10, sm.getVehicleno());
            preparedStatementInsert1.setDate(11, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSupplydt())));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(sm.getNetqty()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(sm.getNetamt()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(sm.getNettaxableamt()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(sm.getNetcgst()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(sm.getNetsgst()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(sm.getNetigst()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(sm.getNettotal()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(sm.getRoundoff()));
            preparedStatementInsert1.setDouble(20, Double.parseDouble(sm.getNetamt01()));
            preparedStatementInsert1.setString(21, sm.getDisplaynote());
            preparedStatementInsert1.setDouble(22, Double.parseDouble(sm.getDisplayamt()));
            preparedStatementInsert1.setDouble(23, Double.parseDouble(sm.getAdvance()));
            preparedStatementInsert1.setDouble(24, Double.parseDouble(sm.getNetamt02()));
            preparedStatementInsert1.setDouble(25, Double.parseDouble(sm.getAmtpaid()));
            preparedStatementInsert1.setInt(26, Integer.parseInt(sm.getIsactive()));
            preparedStatementInsert1.setString(27, sm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            System.out.println("step 2 insertToSaleMaster call");
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            String sale_m_id = sm.getSalemid();
            ArrayList<SaleSub> ssAl = sm.getSsAl();
            for(SaleSub ss : ssAl)
            {
                System.out.println("in salesub loop insertToSaleMaster call");
                // Number of columns in SaleSub: 19
                /* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
                cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                preparedStatementInsert2.setInt(1, ++salesid);
                preparedStatementInsert2.setString(2, sale_m_id);
                System.out.println("1");
                preparedStatementInsert2.setInt(3, Integer.parseInt(ss.getPsid()));
                System.out.println("2");
                preparedStatementInsert2.setInt(4, Integer.parseInt(ss.getItemid()));
                System.out.println("3");
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ss.getQty()));
                System.out.println("4");
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ss.getMrp()));
                System.out.println("5");
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ss.getRate()));
                System.out.println("6");
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ss.getAmt()));
                System.out.println("7");
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ss.getDiscper()));
                System.out.println("8");
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ss.getDiscamt()));
                System.out.println("9");
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ss.getTaxableamt()));
                System.out.println("10");
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ss.getCgstper()));
                System.out.println("11");
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ss.getCgstamt()));
                System.out.println("12");
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ss.getSgstper()));
                System.out.println("13");
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ss.getSgstamt()));
                System.out.println("14");
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ss.getIgstper()));
                System.out.println("15");
                preparedStatementInsert2.setDouble(17, Double.parseDouble(ss.getIgstamt()));
                System.out.println("16");
                preparedStatementInsert2.setDouble(18, Double.parseDouble(ss.getTotal()));
                System.out.println("17");
                preparedStatementInsert2.setDouble(19, Double.parseDouble(ss.getRetqty()));
                System.out.println("18");
                System.out.println("before addbatch insertToSaleMaster call");
                preparedStatementInsert2.addBatch(); 
                System.out.println("after addbatch insertToSaleMaster call");
            }
            System.out.println("before executeBatch insertToSaleMaster call");
            preparedStatementInsert2.executeBatch();
            System.out.println("after executeBatch insertToSaleMaster call");

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToSaleMaster ex1: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public SaleMaster getSaleMaster( String salemid )
    {
        SaleMaster sm=new SaleMaster();
        // Number of columns in SaleMaster: 27
	/* salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
	supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
	netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
        String query="select salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, "
                + "transporter, vehicleno, supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst,"
                + " netigst, nettotal, roundoff, netamt01, displaynote, displayamt, advance, netamt02,"
                + " amtpaid, isactive, remarks from SaleMaster where salemid='"+salemid+"'"; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            while(rs.next())
            {
                // Number of columns in SaleMaster: 26
	/* salemid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
	supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
	netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
                sm.setSalemid(salemid);
                sm.setCompid(rs.getString("compid"));
                sm.setDistid(rs.getString("distid"));
                sm.setSaledt(DateConverter.dateConverter(rs.getString("saledt")));
                sm.setOrdno(rs.getString("ordno"));
                sm.setOrddt(DateConverter.dateConverter(rs.getString("orddt")));
                sm.setDeliverynote(rs.getString("deliverynote"));
                sm.setPaymentterm(rs.getString("paymentterm"));
                sm.setTransporter(rs.getString("transporter"));
                sm.setVehicleno(rs.getString("vehicleno"));
                sm.setSupplydt(DateConverter.dateConverter(rs.getString("supplydt")));
                sm.setNetqty(rs.getString("netqty"));
                sm.setNetamt(rs.getString("netamt"));
                sm.setNettaxableamt(rs.getString("nettaxableamt"));
                sm.setNetcgst(rs.getString("netcgst"));
                sm.setNetsgst(rs.getString("netsgst"));
                sm.setNetigst(rs.getString("netigst"));
                sm.setNettotal(rs.getString("nettotal"));
                sm.setRoundoff(rs.getString("roundoff"));
                sm.setNetamt01(rs.getString("netamt01"));
                sm.setDisplaynote(rs.getString("displaynote"));
                sm.setDisplayamt(rs.getString("displayamt"));
                sm.setAdvance(rs.getString("advance"));
                sm.setNetamt02(rs.getString("netamt02"));
                sm.setAmtpaid(rs.getString("amtpaid"));
                sm.setIsactive(rs.getString("isactive"));
                sm.setRemarks(rs.getString("remarks"));
                
                // Getting Formula Phase
                ArrayList<SaleSub> ssAl=new ArrayList<SaleSub>();
                // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                query="select salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, "
                        + "discamt, taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, "
                        + "igstamt, total, retqty from SaleSub where salemid='"+salemid+"'"; 
                try
                {
                    Statement smt1=conn.createStatement();
                    ResultSet rs1=smt1.executeQuery(query);
                    while(rs1.next())
                    {    
                        SaleSub ss = new SaleSub();
                        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                        ss.setSalesid(rs1.getString("salesid"));
                        ss.setSalemid(rs1.getString("salemid"));
                        ss.setPsid(rs1.getString("psid"));
                        ss.setItemid(rs1.getString("itemid"));
                        ss.setQty(rs1.getString("qty"));
                        ss.setMrp(rs1.getString("mrp"));
                        ss.setRate(rs1.getString("rate"));
                        ss.setAmt(rs1.getString("amt"));
                        ss.setDiscper(rs1.getString("discper"));
                        ss.setDiscamt(rs1.getString("discamt"));
                        ss.setTaxableamt(rs1.getString("taxableamt"));
                        ss.setCgstper(rs1.getString("cgstper"));
                        ss.setCgstamt(rs1.getString("cgstamt"));
                        ss.setSgstper(rs1.getString("sgstper"));
                        ss.setSgstamt(rs1.getString("sgstamt"));
                        ss.setIgstper(rs1.getString("igstper"));
                        ss.setIgstamt(rs1.getString("igstamt"));
                        ss.setTotal(rs1.getString("total"));
                        ss.setRetqty(rs1.getString("retqty"));
                        ssAl.add(ss);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Query: getSaleMaster ex2: "+ex.getMessage(),
                            "SQL Error Found",JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                sm.setSsAl(ssAl);
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getSaleMaster ex3: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return sm;
    }
    
    public Distributer getDistributer(String distid) {
        Distributer d = new Distributer();
        // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
        String query="select distid, beatid, distnm, contactperson, dstreet, dcity, ddist, "
                + "dstate, dstatecode, dpin, dcountry, dcontact, dmail, dgstno, dgstregntype,"
                + " dpanno, daadhaarno, isactive from Distributer where distid="+distid; 
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
        try
        {
            Statement smt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet rs=smt.executeQuery(query);
            if(rs.next())
            {
                // Number of columns in Distributer: 18
        /* distid, beatid, distnm, contactperson, dstreet, dcity, ddist, dstate, dstatecode, 
        dpin, dcountry, dcontact, dmail, dgstno, dgstregntype, dpanno, daadhaarno, isactive */
                d.setDistid(distid);
                d.setBeatid(rs.getString("beatid"));
                d.setDistnm(rs.getString("distnm"));
                d.setContactperson(rs.getString("contactperson"));
                d.setDstreet(rs.getString("dstreet"));
                d.setDcity(rs.getString("dcity"));
                d.setDdist(rs.getString("ddist"));
                d.setDstate(rs.getString("dstate"));
                d.setDstatecode(rs.getString("dstatecode"));
                d.setDpin(rs.getString("dpin"));
                d.setDcountry(rs.getString("dcountry"));
                d.setDcontact(rs.getString("dcontact"));
                d.setDmail(rs.getString("dmail"));
                d.setDgstno(rs.getString("dgstno"));
                d.setDgstregntype(rs.getString("dgstregntype"));
                d.setDpanno(rs.getString("dpanno"));
                d.setDaadhaarno(rs.getString("daadhaarno"));
                d.setIsactive(rs.getString("isactive"));
            }
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: getDistributer ex1: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return null;
        }
        finally
        {
            try {
                if (conn!=null) conn.close();
            }
            catch(SQLException e){}
        }
        return d;
    }
    
    public boolean updateToSaleMaster(SaleMaster sm)
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int salesid = getMaxId("SaleSub", "salesid");

        // Number of columns in SaleMaster: 27
        /* salemid, compid, distid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
        String insertTableSQL1 = "update SaleMaster set distid=?, saledt=?, ordno=?, orddt=?, deliverynote=?,"
                + " paymentterm=?, transporter=?, vehicleno=?, supplydt=?, netqty=?, netamt=?, nettaxableamt=?,"
                + " netcgst=?, netsgst=?, netigst=?, nettotal=?, roundoff=?, netamt01=?, displaynote=?,"
                + " displayamt=?, advance=?, netamt02=?, amtpaid=?, isactive=?, remarks=? where salemid='"
                + sm.getSalemid()+"'";
        
        // Number of columns in SaleSub: 19
	/* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
	cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
        String insertTableSQL2 = "insert into SaleSub (salesid, salemid, psid, itemid, qty, "
                + "mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, sgstper, "
                + "sgstamt, igstper, igstamt, total, retqty) values (?, ?, ?, ?, ?, ?, ?, ?,"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in SaleMaster: 27
            /* salemid, compid, retid, saledt, ordno, orddt, deliverynote, paymentterm, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, displaynote, displayamt, advance, netamt02, amtpaid, isactive, remarks */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setInt(1, Integer.parseInt(sm.getDistid()));
            preparedStatementInsert1.setDate(2, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSaledt())));
            preparedStatementInsert1.setString(3, sm.getOrdno());
            preparedStatementInsert1.setDate(4, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getOrddt())));
            preparedStatementInsert1.setString(5, sm.getDeliverynote());
            preparedStatementInsert1.setString(6, sm.getPaymentterm());
            preparedStatementInsert1.setString(7, sm.getTransporter());
            preparedStatementInsert1.setString(8, sm.getVehicleno());
            preparedStatementInsert1.setDate(9, java.sql.Date.valueOf(DateConverter.dateConverter1(sm.getSupplydt())));
            preparedStatementInsert1.setDouble(10, Double.parseDouble(sm.getNetqty()));
            preparedStatementInsert1.setDouble(11, Double.parseDouble(sm.getNetamt()));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(sm.getNettaxableamt()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(sm.getNetcgst()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(sm.getNetsgst()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(sm.getNetigst()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(sm.getNettotal()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(sm.getRoundoff()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(sm.getNetamt01()));
            preparedStatementInsert1.setString(19, sm.getDisplaynote());
            preparedStatementInsert1.setDouble(20, Double.parseDouble(sm.getDisplayamt()));
            preparedStatementInsert1.setDouble(21, Double.parseDouble(sm.getAdvance()));
            preparedStatementInsert1.setDouble(22, Double.parseDouble(sm.getNetamt02()));
            preparedStatementInsert1.setDouble(23, Double.parseDouble(sm.getAmtpaid()));
            preparedStatementInsert1.setInt(24, Integer.parseInt(sm.getIsactive()));
            preparedStatementInsert1.setString(25, sm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(SaleSub ss : sm.getSsAl())
            {
                // Number of columns in SaleSub: 19
                /* salesid, salemid, psid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, 
                cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, retqty */
                preparedStatementInsert2.setInt(1, ++salesid);
                preparedStatementInsert2.setString(2, sm.getSalemid());
                preparedStatementInsert2.setInt(3, Integer.parseInt(ss.getPsid()));
                preparedStatementInsert2.setInt(4, Integer.parseInt(ss.getItemid()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ss.getQty()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ss.getMrp()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ss.getRate()));
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ss.getAmt()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ss.getDiscper()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ss.getDiscamt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ss.getTaxableamt()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ss.getCgstper()));
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ss.getCgstamt()));
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ss.getSgstper()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ss.getSgstamt()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ss.getIgstper()));
                preparedStatementInsert2.setDouble(17, Double.parseDouble(ss.getIgstamt()));
                preparedStatementInsert2.setDouble(18, Double.parseDouble(ss.getTotal()));
                preparedStatementInsert2.setDouble(19, Double.parseDouble(ss.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: insertToSaleMaster ex1: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
    
    public boolean updateToPurchaseMaster(PurchaseMaster pm)
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        PreparedStatement preparedStatementInsert2 = null;
        
        int psid = getMaxId("PurchaseSub", "psid");

        // Number of columns in PurchaseMaster: 27
        /* pmid, ssid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
        supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
        netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
        String insertTableSQL1 = "update PurchaseMaster set invno=?, invdt=?, deliverynote=?, payterm=?,"
                + " ordno=?, orddt=?, transporter=?, vehicleno=?, supplydt=?, netqty=?, netamt=?, nettaxableamt=?,"
                + " netcgst=?, netsgst=?, netigst=?, nettotal=?, roundoff=?, netamt01=?, advance=?, "
                + "netamt02=?, isopening=?, amtpaid=0, remarks=? where pmid="+pm.getPmid();
        
        // Number of columns in PurchaseSub: 19
	/* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
	sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
        String insertTableSQL2 = "insert into PurchaseSub (psid, pmid, itemid, qty, mrp, rate, amt, discper, "
                + "discamt, taxableamt, cgstper, cgstamt, sgstper, sgstamt, igstper, igstamt, total, "
                + "qtysold, retqty) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try 
        {
            db=new dBConnection();
            conn=db.setConnection();

            conn.setAutoCommit(false);
            
            // Number of columns in PurchaseMaster: 27
            /* pmid, ssid, compid, invno, invdt, deliverynote, payterm, ordno, orddt, transporter, vehicleno, 
            supplydt, netqty, netamt, nettaxableamt, netcgst, netsgst, netigst, nettotal, roundoff, 
            netamt01, advance, netamt02, isopening, amtpaid, isactive, remarks */
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            preparedStatementInsert1.setString(1, pm.getInvno());
            preparedStatementInsert1.setDate(2, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getInvdt())));
            preparedStatementInsert1.setString(3, pm.getDeliverynote());
            preparedStatementInsert1.setString(4, pm.getPayterm());
            preparedStatementInsert1.setString(5, pm.getOrdno());
            preparedStatementInsert1.setDate(6, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getOrddt())));
            preparedStatementInsert1.setString(7, pm.getTransporter());
            preparedStatementInsert1.setString(8, pm.getVehicleno());
            preparedStatementInsert1.setDate(9, java.sql.Date.valueOf(DateConverter.dateConverter1(pm.getSupplydt())));
            preparedStatementInsert1.setDouble(10, Double.parseDouble(pm.getNetqty()));
            preparedStatementInsert1.setDouble(11, Double.parseDouble(pm.getNetamt()));
            preparedStatementInsert1.setDouble(12, Double.parseDouble(pm.getNettaxableamt()));
            preparedStatementInsert1.setDouble(13, Double.parseDouble(pm.getNetcgst()));
            preparedStatementInsert1.setDouble(14, Double.parseDouble(pm.getNetsgst()));
            preparedStatementInsert1.setDouble(15, Double.parseDouble(pm.getNetigst()));
            preparedStatementInsert1.setDouble(16, Double.parseDouble(pm.getNettotal()));
            preparedStatementInsert1.setDouble(17, Double.parseDouble(pm.getRoundoff()));
            preparedStatementInsert1.setDouble(18, Double.parseDouble(pm.getNetamt01()));
            preparedStatementInsert1.setDouble(19, Double.parseDouble(pm.getAdvance()));
            preparedStatementInsert1.setDouble(20, Double.parseDouble(pm.getNetamt02()));
            preparedStatementInsert1.setInt(21, Integer.parseInt(pm.getIsopening()));
            preparedStatementInsert1.setString(22, pm.getRemarks());
            preparedStatementInsert1.executeUpdate();
            
            preparedStatementInsert2 = conn.prepareStatement(insertTableSQL2);
            for(PurchaseSub ps : pm.getPsAl())
            {
                double qtysold = Double.parseDouble(ps.getQtysold());
                double retqty = Double.parseDouble(ps.getRetqty());
                if ( qtysold != 0.0 || retqty != 0.0 )
                {
                    continue;
                }
                // Number of columns in PurchaseSub: 19
                /* psid, pmid, itemid, qty, mrp, rate, amt, discper, discamt, taxableamt, cgstper, cgstamt, 
                sgstper, sgstamt, igstper, igstamt, total, qtysold, retqty */
                preparedStatementInsert2.setInt(1, ++psid);
                preparedStatementInsert2.setInt(2, Integer.parseInt(pm.getPmid()));
                preparedStatementInsert2.setInt(3, Integer.parseInt(ps.getItemid()));
                preparedStatementInsert2.setDouble(4, Double.parseDouble(ps.getQty()));
                preparedStatementInsert2.setDouble(5, Double.parseDouble(ps.getMrp()));
                preparedStatementInsert2.setDouble(6, Double.parseDouble(ps.getRate()));
                preparedStatementInsert2.setDouble(7, Double.parseDouble(ps.getAmt()));
                preparedStatementInsert2.setDouble(8, Double.parseDouble(ps.getDiscper()));
                preparedStatementInsert2.setDouble(9, Double.parseDouble(ps.getDiscamt()));
                preparedStatementInsert2.setDouble(10, Double.parseDouble(ps.getTaxableamt()));
                preparedStatementInsert2.setDouble(11, Double.parseDouble(ps.getCgstper()));
                preparedStatementInsert2.setDouble(12, Double.parseDouble(ps.getCgstamt()));
                preparedStatementInsert2.setDouble(13, Double.parseDouble(ps.getSgstper()));
                preparedStatementInsert2.setDouble(14, Double.parseDouble(ps.getSgstamt()));
                preparedStatementInsert2.setDouble(15, Double.parseDouble(ps.getIgstper()));
                preparedStatementInsert2.setDouble(16, Double.parseDouble(ps.getIgstamt()));
                preparedStatementInsert2.setDouble(17, Double.parseDouble(ps.getTotal()));
                preparedStatementInsert2.setDouble(18, Double.parseDouble(ps.getQtysold()));
                preparedStatementInsert2.setDouble(19, Double.parseDouble(ps.getRetqty()));
                preparedStatementInsert2.addBatch(); 
            }
            preparedStatementInsert2.executeBatch();

            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Query: updateToPurchaseMaster ex1: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex1) {
                ex1.printStackTrace();
            }
        } 
        finally 
        {
            if (preparedStatementInsert1 != null) 
            {
                try {
                    preparedStatementInsert1.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (preparedStatementInsert2 != null) 
            {
                try {
                    preparedStatementInsert2.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) 
            {
                db=null;
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }
}
