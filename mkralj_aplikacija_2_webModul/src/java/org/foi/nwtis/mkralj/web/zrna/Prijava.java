package org.foi.nwtis.mkralj.web.zrna;

import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import javax.inject.Named;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.sb.facade.MqttPorukeFacade;
import org.foi.nwtis.mkralj.web.dretve.MQTT.MqttKonekcija;

@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable
{
    @EJB
    private MqttPorukeFacade mqttPorukeFacade;

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    
    private String korime;
    private String lozinka;
    private String primljenaPoruka;
    
    private HttpSession sesija = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public void setLozinka(String lozinka)
    {
        this.lozinka = lozinka;
    }

    public Prijava()
    {
    }
    
    public String prijava()
    {
        boolean prijava = upravljanjeKorisnicima.autenticiraj(korime, lozinka, sesija);
        
        if(prijava)
        {
            pokreniSlusacaMqtt(sesija);
            Redirecter redirect = new Redirecter();
            redirect.redirectTo("index.xhtml");
        }
        else{
            primljenaPoruka = "label.neuspjesno.autentikacija";
        }
        
        return "";
    }

    public String getPrimljenaPoruka()
    {
        return primljenaPoruka;
    }

    public void setPrimljenaPoruka(String primljenaPoruka)
    {
        this.primljenaPoruka = primljenaPoruka;
    }
    
    public String odjavi()
    {
        upravljanjeKorisnicima.odjaviKorisnika(sesija);
        
        Redirecter redirect = new Redirecter();
        redirect.redirectTo("index.xhtml");
        
        return "";
    }

    private void pokreniSlusacaMqtt(HttpSession sesija)
    {
        try{
            sesija = postaviMqttSlusacKorisniku(sesija);
            MqttKonekcija kon = (MqttKonekcija)sesija.getAttribute("MqttKonekcija");
            kon.setMqttFacade(mqttPorukeFacade);
            kon.start();
            System.out.println("Pokrenut slusac mqtt");
        }catch(Exception ex)
        {
            System.out.println("Slusac nije uspjesno pokrenut!");
        }
    }
    private HttpSession postaviMqttSlusacKorisniku(HttpSession sesija)
    {
        try
        {
            String korimeJson = upravljanjeKorisnicima.dajKorimeJson(sesija);
            String lozinkaJson = upravljanjeKorisnicima.dajLozinkuJson(sesija);
            MqttKonekcija slusacMqtt = new MqttKonekcija(korimeJson, lozinkaJson);
            sesija.setAttribute("MqttKonekcija", slusacMqtt);

        } catch (Exception ex)
        {
            System.out.println("Neuspješno dodavanje slusaca sesiji");
        }
        return sesija;
    }
}
