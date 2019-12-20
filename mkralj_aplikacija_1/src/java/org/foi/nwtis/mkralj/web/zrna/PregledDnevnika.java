package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import org.foi.nwtis.mkralj.DB.Stranicenje;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.Dnevnik;
import org.foi.nwtis.mkralj.sesija.Sesija;

@Named(value = "pregledDnevnika")
@SessionScoped
public class PregledDnevnika implements Serializable
{
    private List<Dnevnik> listaDnevnika;
    
    private int trenutnaStranica = 1;
    private int ukBrojStr = 1;
    private String dioApp = "";
    private String odVremena="";
    private String doVremena="";
    
    private DateToString dateToStr = new DateToString();

    public DateToString getDateToStr()
    {
        return dateToStr;
    }

    public String getDioApp()
    {
        return dioApp;
    }

    public void setDioApp(String dioApp)
    {
        this.dioApp = dioApp;
    }

    public String getOdVremena()
    {
        return odVremena;
    }

    public void setOdVremena(String odVremena)
    {
        if(odVremena==null)
            this.odVremena = "";
        else
            this.odVremena = odVremena;
    }

    public String getDoVremena()
    {
        return doVremena;
    }

    public void setDoVremena(String doVremena)
    {
        if(doVremena==null)
            this.doVremena = "";
        else
            this.doVremena = doVremena;
    }

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

    public List<Dnevnik> getListaDnevnika()
    {
        if(!Sesija.korisnikPrijavljen())
            listaDnevnika = null;
        
        return listaDnevnika;
    }

    public void setListaDnevnika(List<Dnevnik> listaDnevnika)
    {
        this.listaDnevnika = listaDnevnika;
    }
    
    public PregledDnevnika()
    {
    }
    
    public String preuzmiDnevnikRada()
    {
        if(!Sesija.korisnikPrijavljen())
        {
            listaDnevnika = null;
            return "";
        }
        
        Stranicenje stranicenje = new Stranicenje("dnevnik", "id");
        
        String upit = dajUpit();
        
        provjeriStranice();
        WherePart where = stranicenje.dajWhereZaStranicenje(upit, trenutnaStranica);
        
        listaDnevnika = dohvatiPodatkeIzBaze(where);
        this.ukBrojStr = stranicenje.getBrojStranica();

        return "";
    }
    
    public void nextPage()
    {
        if(trenutnaStranica<ukBrojStr) 
        {
            trenutnaStranica++;
            preuzmiDnevnikRada();
        }
    }
    
    public void prevPage()
    {
        if(trenutnaStranica>1)
        {
            trenutnaStranica--;
            preuzmiDnevnikRada();
        }
    }

    private String dajUpit()
    {
        String like = "%"+dioApp+"%";
        String upit = "";
        if((this.odVremena.isEmpty() || this.doVremena.isEmpty()) && this.dioApp.isEmpty())
            upit = "1";
        else if(this.odVremena.isEmpty() || this.doVremena.isEmpty())
            upit = String.format("dio_aplikacije LIKE '%s'", like);
        else if(this.dioApp.isEmpty())
            upit = String.format("stored BETWEEN '%s' AND '%s'", odVremena, doVremena);
        else
            upit = String.format("dio_aplikacije LIKE '%s' AND stored BETWEEN '%s' AND '%s'", like, odVremena, doVremena);
        
        return upit;
    }

    private List<Dnevnik> dohvatiPodatkeIzBaze(WherePart where)
    {
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null || veza.getConnection()==null)
            return null;
        DBodgovorLista odg = veza.selectObjekt("Dnevnik", new Dnevnik(), where);
        try{
            return (List<Dnevnik>)odg.dajRezultat();
        }catch(Exception ex)
        {
            return null;
        }
    }

    private void provjeriStranice()
    {
        if(this.trenutnaStranica>ukBrojStr)
                this.trenutnaStranica=1;
    }
}
