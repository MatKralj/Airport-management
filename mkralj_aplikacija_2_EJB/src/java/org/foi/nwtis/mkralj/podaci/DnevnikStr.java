package org.foi.nwtis.mkralj.podaci;

import java.util.Date;

public class DnevnikStr
{
    private Integer id;

    private String korisnik;

    private String url;

    private String vrijemePrijema;

    private int trajanjeobrade;

    private String ipAdresa;
    private int ukBrojStr;

    public DnevnikStr()
    {
    }

    public DnevnikStr(Integer id)
    {
        this.id = id;
    }

    public DnevnikStr(Integer id, String korisnik, String url, String vrijemeprijema, int trajanjeobrade)
    {
        this.id = id;
        this.korisnik = korisnik;
        this.url = url;
        this.vrijemePrijema = vrijemeprijema;
        this.trajanjeobrade = trajanjeobrade;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getKorisnik()
    {
        return korisnik;
    }

    public void setKorisnik(String korisnik)
    {
        this.korisnik = korisnik;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getVrijemePrijema()
    {
        return vrijemePrijema;
    }

    public void setVrijemePrijema(Date stored)
    {
        TimeStamp ts = new TimeStamp();
        
        this.vrijemePrijema = ts.dajDatumIzEpoch(stored.getTime());
    }

    public int getTrajanjeobrade()
    {
        return trajanjeobrade;
    }

    public void setTrajanjeobrade(int trajanjeobrade)
    {
        this.trajanjeobrade = trajanjeobrade;
    }

    public String getIpAdresa()
    {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa)
    {
        this.ipAdresa = ipAdresa;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString()
    {
        return "org.foi.nwtis.mkralj.eb.Dnevnik[ id=" + id + " ]";
    }

    public void setUkBrojStr(int ukBrojStr)
    {
        this.ukBrojStr = ukBrojStr;
    }

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

}
