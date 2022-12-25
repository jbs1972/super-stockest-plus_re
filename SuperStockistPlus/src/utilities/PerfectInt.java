package utilities;

public class PerfectInt {
    public static String getPerfectInt(String no)
    {
        if(no.indexOf(".")==-1)
            return no;
        if(no.indexOf(".")==0)
            return "0";
        return no.substring(0, no.indexOf("."));
    }
}
