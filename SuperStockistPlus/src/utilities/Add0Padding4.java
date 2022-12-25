package utilities;

public class Add0Padding4 {
    public static String add0Padding(int no)
    {
        String alteredNo="";
        String sno=no+"";
        switch(sno.length())
        {
            case 1:alteredNo="000"+sno;
                break;
            case 2:alteredNo="00"+sno;
                break;
            case 3:alteredNo="0"+sno;
                break;
            case 4:alteredNo=sno;
                break;
        }
        return alteredNo;
    }
}
