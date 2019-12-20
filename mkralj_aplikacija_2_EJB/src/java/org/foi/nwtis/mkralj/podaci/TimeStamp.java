package org.foi.nwtis.mkralj.podaci;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeStamp {

    /**
     * 
     * @return Trenutno vrijeme u formatu yyy-MM-dd HH:mm:ss.SSS
     * Tj. format za upis u bazu
     */
    public String dajTrenutnoVrijemeBaza()
    {
        return dajTrenutnoVrijeme("yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    public String dajTrenutnoVrijeme(String format)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        
        return dateFormat.format(new Date());
    }
    
    public long dajTrenutniUnixMills()
    {
        return new Date().getTime();
    }
    
    public long dajUnixMills(String datum)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        try
        {
            Date date = dateFormat.parse(datum);
            
            return date.getTime();
        } catch (ParseException ex)
        {
            System.out.println("Neispravno vrijeme. "+ex.getMessage());
            return 0;
        }
    }
    
    public long dajUnixMills(String datum, String format)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try
        {
            Date date = dateFormat.parse(datum);
            
            return date.getTime();
        } catch (ParseException ex)
        {
            System.out.println("Neispravno vrijeme. "+ex.getMessage());
            return 0;
        }
    }
    
    private int dajUnix(String datum, String format)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try
        {
            Date date = dateFormat.parse(datum);
            
            return (int)(date.getTime()/1000);
        } catch (ParseException ex)
        {
            System.out.println("Neispravno vrijeme. "+ex.getMessage());
            return 0;
        }
    }
    
    public int dajUnixDatum(String datum){
        return dajUnix(datum, "dd.MM.yyyy HH:mm:ss");
    }
    
    public int dajUnixIzBaze(String datum)
    {
        return dajUnix(datum, "yyyy-MM-dd HH:mm:ss.S");
    }
    
    private String dajDatumIzEpochDefault(int pocetakIntervala, String format)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        
        Date d = new Date(((long)(pocetakIntervala))*1000);
        return dateFormat.format(d);
    }
    
    private String dajDatumIzEpochDefault(long pocetakIntervala, String format)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        
        Date d = new Date((pocetakIntervala));
        return dateFormat.format(d);
    }

    public String dajDatumIzEpoch(int pocetakIntervala)
    {
        return dajDatumIzEpochDefault(pocetakIntervala, "dd.MM.yyyy HH:mm:ss");
    }
    
    public String dajDatumIzEpochZaBazu(int pocetakIntervala)
    {
        return dajDatumIzEpochDefault(pocetakIntervala, "yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    public String dajDatumIzEpochZaBazu(long pocetakIntervala)
    {
        return dajDatumIzEpochDefault(pocetakIntervala, "yyyy-MM-dd HH:mm:ss.SSSS");
    }
    
    public String dajDatumIzEpoch(long pocetakIntervala)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        
        Date d = new Date(pocetakIntervala);
        return dateFormat.format(d);
    }
   
}
