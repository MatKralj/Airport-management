package org.foi.nwtis.mkralj.web.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.inject.Named;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;

@Named(value = "registracija")
@RequestScoped
public class Registracija implements Serializable
{

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    
    private String korime;
    private String lozinka;
    private String email;
    private String name;
    private String surname;
    private String lozinkaRepeat;

    public String getLozinkaRepeat()
    {
        return lozinkaRepeat;
    }

    public void setLozinkaRepeat(String lozinkaRepeat)
    {
        this.lozinkaRepeat = lozinkaRepeat;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
    private String primljenaPoruka;

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

    public Registracija()
    {
    }
    
    public String registriraj()
    {
        if(!this.lozinkaRepeat.equals(this.lozinka))
        {
            this.primljenaPoruka = "label.lozinka.neodgovaraju";
            return "";
        }
        String jsonKorisnik = kreirajJsonKorisnika();
        
        if(upravljanjeKorisnicima.registriraj(jsonKorisnik))
            this.primljenaPoruka = "label.uspjesno";
        else
            this.primljenaPoruka = "label.neuspjesno.autentikacija";
        
        return "";
    }

    private String kreirajJsonKorisnika()
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
