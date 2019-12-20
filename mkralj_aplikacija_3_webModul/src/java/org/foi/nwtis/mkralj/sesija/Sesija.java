package org.foi.nwtis.mkralj.sesija;

public class Sesija
{
    private static String korime;
    private static String lozinka;

    public String getKorime()
    {
        return korime;
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public void zapisiKorisnika(String korisnik, String password)
    {
        korime = korisnik;
        lozinka = password;
    }

    public void odjaviKorisnika()
    {
        korime = null;
        lozinka = null;
    }

}
