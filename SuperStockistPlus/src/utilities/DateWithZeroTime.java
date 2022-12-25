package utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateWithZeroTime{
    
    public static Date zeroTime( final Date date )
    {
        return setTime( date, 0, 0, 0, 0 );
    }
    
    public static Date setTime( final Date date, final int hourOfDay, final int minute, final int second, final int ms )
    {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime( date );
        gc.set( Calendar.HOUR_OF_DAY, hourOfDay );
        gc.set( Calendar.MINUTE, minute );
        gc.set( Calendar.SECOND, second );
        gc.set( Calendar.MILLISECOND, ms );
        return gc.getTime();
    }
    
}
