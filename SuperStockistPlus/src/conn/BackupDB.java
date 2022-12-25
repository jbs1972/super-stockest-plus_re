package conn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;

public class BackupDB
{
    public static void copyFile(String path)
    {
        try
        {
            String s=new File(".").getAbsolutePath();
            s=s.substring(0,s.lastIndexOf("."))+"Database\\SSP01.accdb";
            String d = null;
            d=path;
            if(d==null || d.length()==0)
            {
                JOptionPane.showMessageDialog(null,"Invalid Destination...","Error Found",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat formatter=
            new SimpleDateFormat("dd_MM_yyyy hh_mm_a");
            String dateNow = formatter.format(currentDate.getTime());
            d+="\\DBkSSP01"+dateNow+".bak";
            File f1 = new File(s);
            File f2 = new File(d);
            InputStream in = new FileInputStream(f1);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            JOptionPane.showMessageDialog(null,"Database Backup Complete...","Backupp Successful",JOptionPane.INFORMATION_MESSAGE);
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"File Reading Error...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"Input/Output Error...","Error Found",JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
