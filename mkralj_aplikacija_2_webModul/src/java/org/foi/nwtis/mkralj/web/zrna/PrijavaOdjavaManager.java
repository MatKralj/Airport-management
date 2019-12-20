package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;

@Named(value = "prijavaOdjava")
@SessionScoped
public class PrijavaOdjavaManager implements Serializable
{

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;

    private HttpSession sesija = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);;
    private String radnjaPrijave = "";
    private String urlRadnjePrijave = "";

    private String radnjaRegistracije = "";
    private String urlRadnjeRegistracije = "";

    public String getRadnjaRegistracije()
    {
        if(upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            radnjaRegistracije = "stranice.promijeniPodatke";
        else
            radnjaRegistracije = "stranice.registracija";

        return radnjaRegistracije;
    }

    public void setRadnjaRegistracije(String radnjaRegistracije)
    {
        this.radnjaRegistracije = radnjaRegistracije;
    }

    public String getUrlRadnjeRegistracije()
    {
        if(upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            urlRadnjeRegistracije = "/faces/promijeniPodatke.xhtml";
        else
            urlRadnjeRegistracije = "/faces/registracija.xhtml";
        
        return urlRadnjeRegistracije;
    }

    public void setUrlRadnjeRegistracije(String urlRadnjeRegistracije)
    {
        this.urlRadnjeRegistracije = urlRadnjeRegistracije;
    }
    
    public String getRadnjaPrijave()
    {
        if(upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            radnjaPrijave = "stranice.odjava";
        else
            radnjaPrijave = "stranice.prijava";
        
        return radnjaPrijave;
    }
    
    public String getUrlRadnjePrijave()
    {
        if(upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            urlRadnjePrijave = "/faces/odjava.xhtml";
        else
            urlRadnjePrijave = "/faces/prijava.xhtml";
        
        return urlRadnjePrijave;
    }

    public void setUrlRadnjePrijave(String urlRadnje)
    {
        this.urlRadnjePrijave = urlRadnje;
    }

    public void setRadnjaPrijava(String radnja)
    {
        this.radnjaPrijave = radnja;
    }
    
    public PrijavaOdjavaManager()
    {
    }
}
