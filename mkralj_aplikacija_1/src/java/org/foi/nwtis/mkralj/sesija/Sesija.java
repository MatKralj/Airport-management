package org.foi.nwtis.mkralj.sesija;


public class Sesija {

    private static String korime;
    private static String lozinka;

    public static String getKorime()
    {
        return korime;
    }

    public static String getLozinka()
    {
        return lozinka;
    }
    
    public static void prijaviKorisnika(String korisnik, String password)
    {
        korime = korisnik;
        lozinka = password;
    }
    public static void odjaviKorisnika()
    {
        korime = null;
        lozinka = null;
    }
    
    public static boolean korisnikPrijavljen()
    {
        if((korime==null || korime.isEmpty()) || (lozinka==null || lozinka.isEmpty()))
            return false;

        return true;
    }

}
