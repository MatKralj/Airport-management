package org.foi.nwtis.mkralj.web.zrna;

import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.inject.Named;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;

@Named(value = "promjenaPodataka")
@RequestScoped
public class PromjenaPodataka implements Serializable
{
    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    
    HttpSession sesija = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    
    private String korime;
    private String lozinka;
    private String email;
    private String name;
    private String surname;
    
    private String primljenaPoruka;
    
    public String getName()
    {
        this.name = (String)sesija.getAttribute("ime");
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        this.surname = (String)sesija.getAttribute("prezime");
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        this.email = (String)sesija.getAttribute("email");
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
    

    public String getKorime()
    {
        this.korime = (String)sesija.getAttribute("korime");
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getLozinka()
    {
        this.lozinka = (String)sesija.getAttribute("lozinka");
        return lozinka;
    }

    public void setLozinka(String lozinka)
    {
        this.lozinka = lozinka;
    }

    public PromjenaPodataka()
    {
    }
    
    public String azuriraj()
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        String jsonKorisnik = kreirajJsonKorisnik();
        
        if(upravljanjeKorisnicima.azuriraj(jsonKorisnik, sesija))
        {
            upravljanjeKorisnicima.autenticiraj(korime, lozinka, sesija);
            this.primljenaPoruka = "azuriranje.korisnik.uspjesno";
        }
        else
            this.primljenaPoruka = "azuriranje.korisnik.neuspjesno";
        
        return "";
    }

    private String kreirajJsonKorisnik()
    {
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        jO.addProperty("korime", this.korime);
        jO.addProperty("lozinka", this.lozinka);
        jO.addProperty("ime", this.name);
        jO.addProperty("prezime", this.surname);
        jO.addProperty("email", this.email);
        String jsonKorisnik = gson.toJson(jO);
        
        return jsonKorisnik;
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
