package org.foi.nwtis.mkralj.web.zrna;

import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.servisi.soap.Avion;
import org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service;
import org.foi.nwtis.mkralj.web.zrna.pomoc.DateToString;
import org.foi.nwtis.mkralj.web.zrna.pomoc.TimeStamp;

@Named(value = "letoviAerodrom")
@SessionScoped
public class RadsLetovimaAerodroma implements Serializable
{

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkralj_aplikacija_1/SOAPwsApp1.wsdl")
    private SOAPwsApp1_Service service;
    
    private String icao;
    private String odgovor;
    private DateToString dateToStr = new DateToString();
    
    private String odVremena;
    private String doVremena;
    private String icao24;
    
    private List<Avion> listaAviona;
     
    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    private int trenutnaStranica = 1;
    private int ukBrojStr = 1;

    public int getTrenutnaStranica()
    {
        return trenutnaStranica;
    }

    public void setTrenutnaStranica(int trenutnaStranica)
    {
        this.trenutnaStranica = trenutnaStranica;
    }

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

    public void setUkBrojStr(int ukBrojStr)
    {
        this.ukBrojStr = ukBrojStr;
    }

    public List<Avion> getListaAviona()
    {
        return listaAviona;
    }

    public void setListaAviona(List<Avion> listaAviona)
    {
        this.listaAviona = listaAviona;
    }

    public String getOdVremena()
    {
        return odVremena;
    }

    public void setOdVremena(String odVremena)
    {
        this.odVremena = dajDatumIspravnogFotmata(odVremena);
    }

    public String getDoVremena()
    {
        return doVremena;
    }

    public void setDoVremena(String doVremena)
    {
        this.doVremena = dajDatumIspravnogFotmata(doVremena);
    }
    

    public DateToString getDateToStr()
    {
        return dateToStr;
    }

    public void setDateToStr(DateToString dateToStr)
    {
        this.dateToStr = dateToStr;
    }

    public String getOdgovor()
    {
        return odgovor;
    }

    public void setOdgovor(String odgovor)
    {
        this.odgovor = odgovor;
    }

    public String getIcao()
    {
        return icao;
    }

    public void setIcao(String icao)
    {
        this.icao = icao;
    }
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    
    
    public String preuzmiAvione(String icao)
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        this.icao = icao;
        provjeriStranice();
        
        this.listaAviona = dajPodatkeOLetovimaPoletjelim(icao, this.odVremena, this.doVremena, korime, lozinka, this.trenutnaStranica);
        if(listaAviona!=null && !listaAviona.isEmpty())
            this.ukBrojStr = listaAviona.get(0).getUkBrojStranica();
        else 
            this.ukBrojStr = 1;
        
        return "";
    }

    public String getIcao24()
    {
        return icao24;
    }

    public void setIcao24(String icao24)
    {
        this.icao24 = icao24;
    }
    
    public void nextPage()
    {
        if (trenutnaStranica < ukBrojStr)
        {
            trenutnaStranica++;
            preuzmiAvione(this.icao);
        }
    }

    public void prevPage()
    {
        if (trenutnaStranica > 1)
        {
            trenutnaStranica--;
            preuzmiAvione(this.icao);
        }
    }

    private String dajDatumIspravnogFotmata(String vrijeme)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try{
            Date date = sdf.parse(vrijeme);
            TimeStamp ts = new TimeStamp();
            
            return ts.dajDatumIzEpoch((int)(date.getTime()/1000));
        }catch(Exception ex)
        {
            return "";
        }
    }
    
    public String dajDatumIzEpoch(int epoch)
    {
        TimeStamp ts = new TimeStamp();
        return ts.dajDatumIzEpoch(epoch);
    }

    private java.util.List<org.foi.nwtis.mkralj.servisi.soap.Avion> dajPodatkeOLetovimaPoletjelim(java.lang.String icao, java.lang.String odVremena, java.lang.String doVremena, java.lang.String korime, java.lang.String lozinka, int arg5)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dajPodatkeOLetovimaPoletjelim(icao, odVremena, doVremena, korime, lozinka, arg5);
    }

    private void provjeriStranice()
    {
        if(this.trenutnaStranica>ukBrojStr)
                this.trenutnaStranica=1;
    } 
}
