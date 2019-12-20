package org.foi.nwtis.mkralj.rest.podaci;

import org.foi.nwtis.mkralj.servisi.soap.Korisnik;


public class KorisnikBase
{
    
    private String ime;
    private String prezime;
    private String korime;
    private String email;
    private boolean active;
    private int ukBrojStranica;

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    public KorisnikBase(Korisnik k)
    {
        this.ime = k.getIme();
        this.prezime = k.getPrezime();
        this.korime = k.getKorime();
        this.email = k.getEmail();
        this.active = k.isActive();
        this.ukBrojStranica = k.getBrojStrStranicenje();
    }
    
    public String getIme()
    {
        return ime;
    }

    public void setIme(String name)
    {
        this.ime = name;
    }

    public String getPrezime()
    {
        return prezime;
    }

    public void setPrezime(String surname)
    {
        this.prezime = surname;
    }

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public int getUkBrojStr()
    {
        return this.ukBrojStranica;
    }
}
