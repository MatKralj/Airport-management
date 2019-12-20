package org.foi.nwtis.mkralj.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.poruke.odgovori.OdgovorRest;
import org.foi.nwtis.mkralj.rest.podaci.KorisnikBase;
import org.foi.nwtis.mkralj.servisi.soap.Korisnik;
import org.foi.nwtis.mkralj.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkralj.socket.KorisnikSocketa;

@Path("korisnik")
public class RESTkorisnikApp3
{
    public RESTkorisnikApp3()
    {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("korisnikJson") String korisnikJson, @QueryParam("trenutnaStranica") int trenutnaStranica)
    {
        String korime = dajCistiTekst(korisnikJson, "korime");
        String lozinka = dajCistiTekst(korisnikJson, "lozinka");
        List<Korisnik> korisnici = dajPodatkeSvihKorisnika(korime, lozinka, trenutnaStranica);
        List<KorisnikBase> korisniciBezPw = new ArrayList<>();
        for (Korisnik k : korisnici)
        {
            korisniciBezPw.add(new KorisnikBase(k));
        }
        OdgovorRest odg = new OdgovorRest(korisniciBezPw);
        return odg.toString();
    }

    /*GET  metoda  -  na  bazi  putanje  "{id}"  -  preuzimanje  jednog  korisnika  -  vraća  odgovor  u application/json formatu. 
    Struktura odgovora je sljedeća: {"odgovor": [{...}], "status": "OK"} | {"status": "ERR", "poruka": "tekst poruke"}. Ne vraća se lozinka!*/
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getKorisnika(@PathParam("id") String korisnik, @QueryParam("auth") String auth,
            @QueryParam("korimeJson") String korimeJson)
    {
        OdgovorRest odg;
        if (korimeJson == null || korimeJson.isEmpty())
        {
            String odgovor = autenticiraj(korisnik, auth);
            odg = new OdgovorRest(odgovor);
        } else
        {
            String lozinka = dajCistiTekst(auth, "lozinka");
            String korime = dajCistiTekst(korimeJson, "korime");
            String odgovor = dajKorisnika(korisnik, korime, lozinka);
            odg = new OdgovorRest(odgovor);
        }

        return odg.toString();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String autenticiraj(@PathParam("id") String korime, @QueryParam("auth") String auth)
    {
        String strToSend = "";

        if (auth != null && !auth.isEmpty())
        {
            strToSend = dajRezultatAutentikacije(auth, korime);
        }

        return new OdgovorRest(strToSend).toString();
    }

    private String dajRezultatAutentikacije(String auth, String korime)
    {
        String lozinka = dajVrijednostJsona(auth, "lozinka");
        KorisnikSocketa ks = new KorisnikSocketa(getPort(), "localhost");
        String naredba = String.format("KORISNIK %s; LOZINKA %s;", korime, lozinka);
        ks.setPorukaZaSlanje(naredba);
        ks.start();
        try
        {
            ks.join();
            return ks.getPrimljenaPoruka();
        } catch (InterruptedException ex)
        {
            System.out.println("Interrupted");

            return "";
        }
    }

    /*POST   metoda   -   osnovna   adresa   -   dodavanje   jednog   korisnika   -   šalju   se   podaci   u application/json  formatu.
    Vraća  odgovor  u  application/json  formatu.  Struktura  odgovora  je sljedeća: 
    {"odgovor": [], "status": "OK"}  |  {"status": "ERR", "poruka": "tekst poruke"}. */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String registriraj(String korisnikJson)
    {
        Korisnik k = kreirajKorisnikaIzJson(korisnikJson);

        OdgovorRest odg;
        if (dodajKorisnika(k))
        {
            odg = new OdgovorRest(k);
        } else
        {
            odg = new OdgovorRest("ERROR : Korisnik nije uspješno registriran");
        }
        return odg.toString();
    }

    /*PUT  metoda  -  na  bazi  putanje  {id}  -  ažuriranje  jednog  korisnika  -  šalju  se  podaci  u application/json  formatu.
    Vraća  odgovor  u  application/json  formatu.  Struktura  odgovora  je sljedeća: 
    {"odgovor": [], "status": "OK"}  |  {"status": "ERR", "poruka": "tekst poruke"}.*/
    /**
     *
     * @param korime
     * @param korisnikJson
     * @return
     */
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String azuriraj(@PathParam("id") String korime, String korisnikJson)
    {
        Korisnik k = kreirajKorisnikaIzJson(korisnikJson);
        String staraLozinka = dajCistiTekst(korisnikJson, "staraLozinka");
        OdgovorRest odg;
        if (azurirajKorisnika(k, korime, staraLozinka))
        {
            odg = new OdgovorRest(k);
        } else
        {
            odg = new OdgovorRest("ERROR : Korisnik nije uspješno ažuriran");
        }
        return odg.toString();
    }

    private String dajCistiTekst(String jsonText, String attributeName)
    {
        try
        {
            Gson gson = new Gson();
            JsonObject jO = gson.fromJson(jsonText, JsonObject.class);
            JsonElement jE = jO.get(attributeName);

            return jE.getAsString();
        } catch (Exception ec)
        {
            return "";
        }
    }

    private String dajVrijednostJsona(String jsonText, String attributeName)
    {
        Gson gson = new Gson();
        JsonObject jO = gson.fromJson(jsonText, JsonObject.class);
        JsonElement jElem = jO.get(attributeName);

        return jElem.getAsString();
    }

    private Korisnik kreirajKorisnikaIzJson(String korisnikJson)
    {
        String korime = dajVrijednostJsona(korisnikJson, "korime");
        String lozinka = dajVrijednostJsona(korisnikJson, "lozinka");
        String ime = dajVrijednostJsona(korisnikJson, "ime");
        String prezime = dajVrijednostJsona(korisnikJson, "prezime");
        String email = dajVrijednostJsona(korisnikJson, "email");

        Korisnik k = new Korisnik();
        k.setActive(true);
        k.setEmail(email);
        k.setKorime(korime);
        k.setLozinka(lozinka);
        k.setIme(ime);
        k.setPrezime(prezime);

        return k;
    }

    private int getPort()
    {
        try
        {
            ServletContext sc = SlusacAplikacije.getSc();

            Konfiguracija konf = (Konfiguracija) sc.getAttribute("konfiguracija");
            String port = konf.dajPostavku("socket.port");

            return Integer.parseInt(port);
        } catch (Exception ex)
        {
            return 0;
        }
    } 

    private String dajKorisnika(String id, String korime, String lozinka)
    {
        OdgovorRest odg = new OdgovorRest("");
        List<Korisnik> listaKorisnika = dajPodatkeSvihKorisnika(korime, lozinka, -1);

        if (listaKorisnika == null || listaKorisnika.isEmpty())
        {
            return odg.toString();
        }

        for (Korisnik k : listaKorisnika)
        {
            if (k.getKorime().equals(id))
            {
                odg = new OdgovorRest(k);
                break;
            }
        }
        return odg.toString();
    }

    private static boolean azurirajKorisnika(org.foi.nwtis.mkralj.servisi.soap.Korisnik korisnik, java.lang.String korime, java.lang.String lozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service service = new org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service();
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.azurirajKorisnika(korisnik, korime, lozinka);
    }

    private static boolean dodajKorisnika(org.foi.nwtis.mkralj.servisi.soap.Korisnik korisnik)
    {
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service service = new org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service();
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dodajKorisnika(korisnik);
    }

    private static java.util.List<org.foi.nwtis.mkralj.servisi.soap.Korisnik> dajPodatkeSvihKorisnika(java.lang.String korime, java.lang.String lozinka, int arg2)
    {
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service service = new org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service();
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dajPodatkeSvihKorisnika(korime, lozinka, arg2);
    }

}
