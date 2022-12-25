package utilities;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AgeCalculator {
    
    // dob and currentdt in dd/MM/yyyy format
    public static String calculateAge(String dob, String currentdt)
    {
        String sdob[]=dob.split("/");
        String scurrentdt[]=currentdt.split("/");
        GregorianCalendar d1 = new GregorianCalendar(Integer.parseInt(sdob[2]), Integer.parseInt(sdob[1]) - 1, Integer.parseInt(sdob[0]));
        GregorianCalendar d2 = new GregorianCalendar(Integer.parseInt(scurrentdt[2]), Integer.parseInt(scurrentdt[1]) - 1, Integer.parseInt(scurrentdt[0]));

        d2.add(Calendar.YEAR, -d1.get(Calendar.YEAR));
        d2.add(Calendar.MONTH, -d1.get(Calendar.MONTH));
        d2.add(Calendar.DAY_OF_MONTH, -d1.get(Calendar.DAY_OF_MONTH) + 1);

        int y = sdob[2].equals(scurrentdt[2])?0:d2.get(Calendar.YEAR);
        int m = d2.get(Calendar.MONTH);
        int d = d2.get(Calendar.DAY_OF_MONTH) - 1;
        String result=y+"y "+m+"m "+d+"d.";
        System.out.println(result);
        return result;
    }

    public static void main(String args[])
    {
        calculateAge("19/01/2014","27/11/2014");
        calculateAge("01/12/1972","27/11/2014");
        calculateAge("27/11/2013","27/11/2014");
    }
}
