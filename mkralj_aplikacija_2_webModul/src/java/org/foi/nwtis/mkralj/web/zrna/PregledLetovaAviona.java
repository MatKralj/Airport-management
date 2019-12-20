package org.foi.nwtis.mkralj.web.zrna;

import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.servisi.soap.Avion;
import org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service;

@Named(value = "pregledLetova")
@SessionScoped
public class PregledLetovaAviona implements Serializable
{

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkralj_aplikacija_1/SOAPwsApp1.wsdl")
    private SOAPwsApp1_Service service;
    
    private String icao24;
    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    private int trenutnaStranica = 1;
    private int ukBrojStr = 1;
    private String odVremena;
    private String doVremena;
    private List<Avion> listaLetova;

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

    public String getIcao24()
    {
        return icao24;
    }

    public void setIcao(String icao24)
    {
        this.icao24 = icao24;
    }
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    public String preuzmi(String icao24, String odVremena, String doVremena)
    {
        this.icao24 = icao24;
        this.odVremena = odVremena;
        this.doVremena = doVremena;
        
        
        Redirecter redirecter = new Redirecter();
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            redirecter.redirectTo("index.xhtml");
        else
            redirecter.redirectTo("pregledLetovaAviona.xhtml");
        
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        
        provjeriStranice();
        this.listaLetova = dajPodatkeOLetovimaAviona(icao24, odVremena, doVremena, korime, lozinka, this.trenutnaStranica);
        
        if(listaLetova!=null && !listaLetova.isEmpty())
            this.ukBrojStr = listaLetova.get(0).getUkBrojStranica();
        else 
            this.ukBrojStr = 1;
            
        return "";
    }

    public List<Avion> getListaLetova()
    {
        return listaLetova;
    }

    public void setListaLetova(List<Avion> listaLetova)
    {
        this.listaLetova = listaLetova;
    }
    
        public void nextPage()
    {
        if (trenutnaStranica < ukBrojStr)
        {
            trenutnaStranica++;
            preuzmi(this.icao24, this.odVremena, this.doVremena);
        }
    }

    public void prevPage()
    {
        if (trenutnaStranica > 1)
        {
            trenutnaStranica--;
            preuzmi(this.icao24, this.odVremena, this.doVremena);
        }
    }
    
    private void provjeriStranice()
    {
        if(this.trenutnaStranica>ukBrojStr)
                this.trenutnaStranica=1;
    }

    private java.util.List<org.foi.nwtis.mkralj.servisi.soap.Avion> dajPodatkeOLetovimaAviona(java.lang.String icao24, java.lang.String odVremena, java.lang.String doVremena, java.lang.String korime, java.lang.String lozinka, int arg5)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dajPodatkeOLetovimaAviona(icao24, odVremena, doVremena, korime, lozinka, arg5);
    }

}
