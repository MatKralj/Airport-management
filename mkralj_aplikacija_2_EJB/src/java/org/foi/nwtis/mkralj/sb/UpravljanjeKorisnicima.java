package org.foi.nwtis.mkralj.sb;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.UnsupportedEncodingException;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ClientErrorException;
import org.foi.nwtis.mkralj.staticRest.RESTapp3;

@Stateful
@LocalBean
public class UpravljanjeKorisnicima
{

    private String korime = "";
    private String lozinka = "";
    private String email = "";
    private String ime = "";
    private String prezime = "";

    public boolean autenticiraj(String korime, String lozinka, HttpSession sesija)
    {
        String lozinkaJson = kreirajJson(lozinka, "lozinka");

        String odgovor = dajOdgovorAutentikacijeKlijent(korime, lozinkaJson);
        if (odgovor.contains("OK"))
        {
            zapisiKorisnika(korime, lozinka, sesija);
            return true;
        }

        odjaviKorisnika(sesija);
        return false;
    }

    private String dajOdgovorAutentikacijeKlijent(String korime, String lozinkaJson)
    {
        RESTapp3 client = new RESTapp3();
        String odgovor = "";
        try
        {
            odgovor = client.autenticiraj(korime, lozinkaJson);
        } catch (Exception ex)
        {
            odgovor = "";
        }
        return odgovor;
    }

    public boolean registriraj(String jsonKorisnik)
    {
        RESTapp3 client = new RESTapp3();

        String odgovor = client.registriraj(jsonKorisnik);
        if (odgovor.contains("OK"))
        {
            return true;
        }

        return false;
    }

    public boolean azuriraj(String noviKorisnik, HttpSession sesija)
    {
        RESTapp3 client = new RESTapp3();
        postaviKorisnickePodatke(sesija);

        String stariKorime = this.korime;

        noviKorisnik = dodajStaruLozinku(noviKorisnik);

        String odgovor = client.azuriraj( noviKorisnik, stariKorime);

        if (odgovor.contains("OK"))
        {
            String noviKorime = dajCistiTekst(noviKorisnik, "korime");
            String novaLozinka = dajCistiTekst(noviKorisnik, "lozinka");
            zapisiKorisnika(noviKorime, novaLozinka, sesija);
            return true;
        }

        return false;
    }

    public String dajJsonPodatkeKorisnika(String korime)
    {
        RESTapp3 client = new RESTapp3();

        String lozinkaJson = kreirajJson(this.lozinka, "lozinka");
        String korisnik = kreirajJson(this.korime, "korime");
        try
        {
            return client.getKorisnika(korime, lozinkaJson, korisnik);
        } catch (ClientErrorException | UnsupportedEncodingException ex)
        {
            return "";
        }
    }

    private String kreirajJson(String s, String key)
    {
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();

        jO.addProperty(key, s);

        return gson.toJson(jO);
    }

    private String dajCistiTekst(String json, String key)
    {
        try
        {
            Gson gson = new Gson();
            JsonObject jO = gson.fromJson(json, JsonObject.class);
            JsonElement jElemOdgovor = jO.get("odgovor");
            JsonObject jObjOdgovor = gson.fromJson(jElemOdgovor, JsonObject.class);
            
            JsonElement jElemKonacno = jObjOdgovor.get(key);

            return jElemKonacno.getAsString();
        }
        catch(Exception ex)
        {
            return "";
        }
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public String getEmail()
    {
        return email;
    }

    public String getIme()
    {
        return ime;
    }

    public String getPrezime()
    {
        return prezime;
    }

    public String getKorime()
    {
        return korime;
    }

    public void zapisiKorisnika(String korime, String lozinka, HttpSession sesija)
    {
        this.korime = korime;
        this.lozinka = lozinka;
        String jsonOstaliPodaci = dajJsonPodatkeKorisnika(korime);

        zapisiOstaleAtribute(jsonOstaliPodaci);
        
        postaviSesiju(sesija);
    }
    
    private void postaviSesiju(HttpSession sesija)
    {
        sesija.setAttribute("korime", this.korime);
        sesija.setAttribute("lozinka", this.lozinka);
        sesija.setAttribute("ime", this.ime);
        sesija.setAttribute("prezime", this.prezime);
        sesija.setAttribute("email", this.email);
    }
   

    public void odjaviKorisnika(HttpSession sesija)
    {
        try{
            sesija.invalidate();
        }catch(Exception ex)
        {
            System.out.println("Sesija je već invalidirana");
        }
        
    }

    public boolean korisnikJePrijavljen(HttpSession sesija)
    {
        postaviKorisnickePodatke(sesija);
        if ((this.korime == null || this.korime.isEmpty()) || (this.lozinka == null || this.lozinka.isEmpty()))
        {
            return false;
        }

        return true;
    }

    private void zapisiOstaleAtribute(String jsonOstaliPodaci)
    {
        this.email = dajCistiTekst(jsonOstaliPodaci, "email");
        this.ime = dajCistiTekst(jsonOstaliPodaci, "ime");
        this.prezime = dajCistiTekst(jsonOstaliPodaci, "prezime");
    }

    private String dodajStaruLozinku(String noviKorisnik)
    {
        try
        {
            Gson gson = new Gson();
            JsonObject jO = gson.fromJson(noviKorisnik, JsonObject.class);
            jO.addProperty("staraLozinka", this.lozinka);

            return gson.toJson(jO);
        } catch (Exception ex)
        {
            return noviKorisnik;
        }
    }

    public String dajPodatkeZaAutentikacijuJson(HttpSession sesija)
    {
        try
        {
            postaviKorisnickePodatke(sesija);
            Gson gson = new Gson();
            JsonObject jO = new JsonObject();
            jO.addProperty("korime", this.korime);
            jO.addProperty("lozinka", this.lozinka);

            return gson.toJson(jO);
        } catch (Exception ex)
        {
            return "";
        }
    }

    private void postaviKorisnickePodatke(HttpSession sesija)
    {
        this.korime = (String)sesija.getAttribute("korime");
        this.lozinka = (String)sesija.getAttribute("lozinka");
    }

    public String dajKorimeJson(HttpSession sesija)
    {
        Gson gson = new Gson();
        try{
            String korime = (String)sesija.getAttribute("korime");
            JsonObject jO = new JsonObject();
            jO.addProperty("korime", korime);
            return gson.toJson(jO);
        }catch(Exception ex)
        {
            return "";
        }
    }

    public String dajLozinkuJson(HttpSession sesija)
    {
        Gson gson = new Gson();
        try{
            String lozinka = (String)sesija.getAttribute("lozinka");
            JsonObject jO = new JsonObject();
            jO.addProperty("lozinka", lozinka);
            return gson.toJson(jO);
        }catch(Exception ex)
        {
            return "";
        }
    }
    
    public String dajKorime(HttpSession sesija)
    {

        return (String)sesija.getAttribute("korime");

    }

    public String dajLozinku(HttpSession sesija)
    {
        return (String)sesija.getAttribute("lozinka");
    }
}
