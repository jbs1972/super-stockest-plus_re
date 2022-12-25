package Useful;

import conn.dBConnection;
import java.sql.*;
import java.io.*;
import javax.swing.JOptionPane;

public class queryFire
{
    public static void main(String[] args) throws IOException
    {
        dBConnection db=new dBConnection();
        Connection conn=db.setConnection();
//        String query="update SalePaymentregister set isactive = 1";
//        String query="delete from logindetails";
//        String query="delete from purchasemaster";
//        String query="delete from purchasesub";
//        String query="delete from purchasepaymentregister";
//        String query="delete from distributer";
//        String query="delete from salemaster";
//        String query="delete from salesub";
        String query="delete from SalePaymentHistory";
//        String query="delete from salepaymentregister";
//        String query="delete from ItemMaster";
        System.out.println(query);
        try
        {
            Statement smt=conn.createStatement();
            smt.executeUpdate(query);
            System.out.println("Query execited successfully ...");
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"queryFire ex?: "+ex.getMessage(),
                    "SQL Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        finally {
            try {
                if (conn!=null) conn.close();
            } catch(SQLException ex){}
        }
    }
}