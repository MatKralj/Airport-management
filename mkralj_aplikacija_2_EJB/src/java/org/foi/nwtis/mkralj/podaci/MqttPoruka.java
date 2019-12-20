package org.foi.nwtis.mkralj.podaci;

import java.util.Date;


public class MqttPoruka 
{
    private Integer id;
    
    private String korisnik;
    
    private String poruka;
    
    private String stored;
    
    private int ukBrojStr;

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

    public void setUkBrojStr(int ukBrojStr)
    {
        this.ukBrojStr = ukBrojStr;
    }

    public MqttPoruka()
    {
    }

    public MqttPoruka(Integer id)
    {
        this.id = id;
    }

    public MqttPoruka(Integer id, String korisnik, String poruka, String stored)
    {
        this.id = id;
        this.korisnik = korisnik;
        this.poruka = poruka;
        this.stored = stored;
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

    public String getPoruka()
    {
        return poruka;
    }

    public void setPoruka(String poruka)
    {
        this.poruka = poruka;
    }

    public String getStored()
    {
        return stored;
    }

    public void setStored(Date stored)
    {
        TimeStamp ts = new TimeStamp();
        
        this.stored = ts.dajDatumIzEpoch(stored.getTime());
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MqttPoruka))
        {
            return false;
        }
        MqttPoruka other = (MqttPoruka) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "org.foi.nwtis.mkralj.eb.MqttPoruke[ id=" + id + " ]";
    }

}
