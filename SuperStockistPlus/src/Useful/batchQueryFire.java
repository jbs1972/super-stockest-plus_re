package Useful;

import conn.dBConnection;
import java.sql.*;
import java.io.*;
import javax.swing.JOptionPane;
import query.Query;

public class batchQueryFire
{
    private static Query q = new Query();
    
    public static void main(String[] args) throws IOException
    {
        dBConnection db=null;
        Connection conn = null;
        
        PreparedStatement preparedStatementInsert1 = null;
        
        int psrfid = q.getMaxId("PSRateFormula", "psrfid");

        // Number of columns in PSRateFormula: 6
	/* psrfid, itemid, pformulaid, sformulaid, status, isactive */
        String insertTableSQL1 = "insert into PSRateFormula (psrfid, itemid, pformulaid, sformulaid,"
                + " status, isactive) values (?, ?, 0, 0, 0, 1)";

        try 
        {
            db=new dBConnection();
            conn=db.setConnection();
            
            String query="select itemid from ItemMaster";
            Statement stm=conn.createStatement();
            ResultSet rs=stm.executeQuery(query);
            
            conn.setAutoCommit(false);
            preparedStatementInsert1 = conn.prepareStatement(insertTableSQL1);
            
            while ( rs.next() ) {
                // Number of columns in ItemStock: 3
                /* isid, itemid, qtyonhand */
                preparedStatementInsert1.setInt(1, ++psrfid);
                preparedStatementInsert1.setInt(2, Integer.parseInt(rs.getString("itemid")));
                preparedStatementInsert1.addBatch(); 
            }
            preparedStatementInsert1.executeBatch();
            
            conn.commit();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"batchQueryFire ex1: "+ex.getMessage(),
                    "Error Found",JOptionPane.ERROR_MESSAGE);
            try {
                conn.rollback();
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
}