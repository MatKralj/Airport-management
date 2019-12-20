package org.foi.nwtis.mkralj.web.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import javax.inject.Named;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.dkermek.ws.serveri.AerodromStatus;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service;
import org.foi.nwtis.mkralj.staticRest.RESTapp1;

@Named(value = "upraljanjeAerodromom")
@SessionScoped
public class UpravljanjeAerodromom implements Serializable
{

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkralj_aplikacija_1/SOAPwsApp1.wsdl")
    private SOAPwsApp1_Service service;

    private String icao;
    private String odgovor;

    private String naziv;
    private String adresa;

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        this.naziv = naziv;
    }

    public String getAdresa()
    {
        return adresa;
    }

    public void setAdresa(String adresa)
    {
        this.adresa = adresa;
    }

    public String getOdgovor()
    {
        return odgovor;
    }

    public void setOdgovor(String odgovor)
    {
        this.odgovor = odgovor;
    }

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;

    public String getIcao()
    {
        return icao;
    }

    public void setIcao(String icao)
    {
        this.icao = icao;
    }
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    public String obrisi(String icao)
    {
        if (!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
        {
            new Redirecter().redirectTo("index.xhtml");
        }

        String korimeJson = upravljanjeKorisnicima.dajKorimeJson(sesija);
        String lozinkaJson = upravljanjeKorisnicima.dajLozinkuJson(sesija);
        RESTapp1 client = new RESTapp1();
        odgovor = client.deleteId(korimeJson, lozinkaJson, icao);
        return "";
    }

    public String aktiviraj(String icao)
    {
        if (!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
        {
            new Redirecter().redirectTo("index.xhtml");
        }

        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        if (aktivirajAerodromGrupi(icao, korime, lozinka))
        {
            this.odgovor = "aerodrom.aktiviran.uspjesno";
        } else
        {
            this.odgovor = "aerodrom.aktiviran.neuspjesno";
        }
        return "";
    }

    public String blokiraj(String icao)
    {
        if (!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
        {
            new Redirecter().redirectTo("index.xhtml");
        }

        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        if (blokirajAerodromGrupi(icao, korime, lozinka))
        {
            this.odgovor = "aerodrom.blokiran.uspjesno";
        } else
        {
            this.odgovor = "aerodrom.blokiran.neuspjesno";
        }
        return "";
    }

    public String status(String icao)
    {
        if (!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
        {
            new Redirecter().redirectTo("index.xhtml");
        }

        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        AerodromStatus status = dajStatusAerodromaGr(icao, korime, lozinka);

        this.odgovor = status.name();
        return "";
    }

    public String azuriraj(String icao)
    {
        RESTapp1 client = new RESTapp1();

        String dodatniPodaciJson = dajDodatnePodatkeAzuiranje();
        String odgovorRest = client.putJsonId(dodatniPodaciJson, icao);
        if(odgovorRest.contains("OK"))
            this.odgovor = "aerodrom.azuriran.uspjesno";
        else
            this.odgovor = "aerodrom.azuriran.neuspjesno";

        return "";
    }

    private boolean aktivirajAerodromGrupi(java.lang.String icao, java.lang.String korime, java.lang.String lozinka)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.aktivirajAerodromGrupi(icao, korime, lozinka);
    }

    private boolean blokirajAerodromGrupi(java.lang.String icao, java.lang.String korime, java.lang.String lozinka)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.blokirajAerodromGrupi(icao, korime, lozinka);
    }

    private AerodromStatus dajStatusAerodromaGr(java.lang.String icao, java.lang.String korime, java.lang.String lozinka)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dajStatusAerodromaGr(icao, korime, lozinka);
    }

    private String dajDodatnePodatkeAzuiranje()
    {
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        jO.addProperty("lozinka", lozinka);
        jO.addProperty("korime", korime);
        jO.addProperty("adresa", this.adresa);
        jO.addProperty("naziv", this.naziv);
        
        return gson.toJson(jO);
    }

}
