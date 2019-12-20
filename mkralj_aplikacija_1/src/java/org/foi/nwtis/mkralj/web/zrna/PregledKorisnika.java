package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1;
import org.foi.nwtis.mkralj.sesija.Sesija;
import org.foi.nwtis.mkralj.web.podaci.Korisnik;

@Named(value = "pregledKorisnika")
@SessionScoped
public class PregledKorisnika implements Serializable
{

    private int trenutnaStranica = 1;
    private int ukBrojStr = 1;

    public int getTrenutnaStranica()
    {
        return trenutnaStranica;
    }

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

    public void setTrenutnaStranica(int trenutnaStranica)
    {
        this.trenutnaStranica = trenutnaStranica;
    }

    public void setUkBrojStr(int ukBrojStr)
    {
        this.ukBrojStr = ukBrojStr;
    }
    
    public PregledKorisnika()
    {
    }
    
    private List<Korisnik> listaKorisnika = new ArrayList<>();

    public List<Korisnik> getListaKorisnika()
    {
        if(!Sesija.korisnikPrijavljen())
            listaKorisnika = null;
        return listaKorisnika;
    }

    public void setListaKorisnika(List<Korisnik> listaKorisnika)
    {
        this.listaKorisnika = listaKorisnika;
    }
    
    public String preuzmiKorisnike()
    {
        if(!Sesija.korisnikPrijavljen())
        {
            listaKorisnika = null;
            return "";
        }
        SOAPwsApp1 soap = new SOAPwsApp1();
        
        provjeriStranice();
        listaKorisnika = soap.dajPodatkeSvihKorisnika(Sesija.getKorime(), Sesija.getLozinka(), trenutnaStranica);
        
        if(listaKorisnika!=null && !listaKorisnika.isEmpty())
            ukBrojStr = listaKorisnika.get(0).getBrojStrStranicenje();
        else
            this.ukBrojStr = 1;
        return "";
    }
    
    public void nextPage()
    {
        if(trenutnaStranica<ukBrojStr) 
        {
            trenutnaStranica++;
            preuzmiKorisnike();
        }
    }
    
    public void prevPage()
    {
        if(trenutnaStranica>1)
        {
            trenutnaStranica--;
            preuzmiKorisnike();
        }
    }

    private void provjeriStranice()
    {
        if(this.trenutnaStranica>ukBrojStr)
                this.trenutnaStranica=1;
    }

}
