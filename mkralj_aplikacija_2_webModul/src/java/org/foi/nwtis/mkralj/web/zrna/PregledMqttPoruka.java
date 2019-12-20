package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.podaci.MqttPoruka;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.sb.facade.MqttPorukeFacade;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;

@Named(value = "pregledMqtt")
@SessionScoped
public class PregledMqttPoruka implements Serializable
{

    @EJB
    private MqttPorukeFacade mqttPorukeFacade;
    private HttpSession sesija = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    
    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    
    private String primljenaPoruka;
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
    
    private List<MqttPoruka> listaMqttPoruka = new ArrayList<>();

    public List<MqttPoruka> getListaMqttPoruka()
    {
        return listaMqttPoruka;
    }

    public void setListaMqttPoruka(List<MqttPoruka> listaMqttPoruka)
    {
        this.listaMqttPoruka = listaMqttPoruka;
    }

    public String getPrimljenaPoruka()
    {
        return primljenaPoruka;
    }

    public void setPrimljenaPoruka(String primljenaPoruka)
    {
        this.primljenaPoruka = primljenaPoruka;
    }
    
    public String preuzmi()
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        
        this.listaMqttPoruka = mqttPorukeFacade.findAllStranicenje(this.trenutnaStranica, dajBrojStranicaStranicenja(), korime);
        if(listaMqttPoruka!=null && !listaMqttPoruka.isEmpty())
            this.ukBrojStr = listaMqttPoruka.get(0).getUkBrojStr();
        else
            this.ukBrojStr = 1;
        return "";
    }
    
    public String obrisiSve()
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        mqttPorukeFacade.removeAll(korime);
        
        preuzmi();
        return "";
    }

    private int dajBrojStranicaStranicenja()
    {
        ServletContext sc = SlusacAplikacije.getSc();
        Konfiguracija konfig = (Konfiguracija)sc.getAttribute("konfiguracija");
        
        String brStranicenje = konfig.dajPostavku("stranicenje.mqtt.redovi");
        try{
            return Integer.parseInt(brStranicenje);
        }catch(Exception ex){
            return -1;
        }
    }
    
    public void nextPage()
    {
        if (this.trenutnaStranica < this.ukBrojStr)
        {
            trenutnaStranica++;
            preuzmi();
        }
        else
            trenutnaStranica=1;
    }

    public void prevPage()
    {
        if (this.trenutnaStranica > 1 && this.trenutnaStranica<=ukBrojStr)
        {
            this.trenutnaStranica--;
            preuzmi();
        }
        else
            this.trenutnaStranica=1;
    }
    
}
