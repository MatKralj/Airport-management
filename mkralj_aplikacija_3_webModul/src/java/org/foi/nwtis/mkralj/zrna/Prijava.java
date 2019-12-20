package org.foi.nwtis.mkralj.zrna;

import javax.inject.Named;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.zrna.pomoc.Redirecter;

@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable
{

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    
    private String korime;
    private String lozinka;
    private String primljenaPoruka;
    
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

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
        boolean rezultat = upravljanjeKorisnicima.autenticiraj(korime, lozinka, sesija);
        if(!rezultat)
            primljenaPoruka="Prijava nije uspjela";   
        else
            new Redirecter().redirectTo("index.xhtml");

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
 
}
