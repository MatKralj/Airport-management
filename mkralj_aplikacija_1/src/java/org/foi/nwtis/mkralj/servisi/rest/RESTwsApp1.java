package org.foi.nwtis.mkralj.servisi.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.mkralj.DB.AutentikacijaKorisnikaDB;
import org.foi.nwtis.mkralj.DB.Stranicenje;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovor;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.DB.podaci.Avion;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.DioAplikacije;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.Dnevnik;
import org.foi.nwtis.mkralj.PomocJson;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac.Greske;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika.Error;
import org.foi.nwtis.mkralj.poruke.odgovori.OdgovorRest;
import org.foi.nwtis.mkralj.servisi.PomocServisi;
import org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromStatus;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;

@Path("aerodromi")
public class RESTwsApp1
{

    /*GET metoda - osnovna adresa - vraća popis svih aerodroma, 
    a za svaki aerodrom icao, naziv, državu  i  geo  lokaciju  u  application/json  formatu.  
    Struktura  odgovora  je  u  sljedeća: {"odgovor": [{...},{...}...], "status": "OK"}  |  
    {"status": "ERR", "poruka": "tekst poruke"}. */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("lozinka") String lozinka, @QueryParam("korime") String korime,
            @QueryParam("dodatniParam") String dodatniParam)
    {
        if (!autenticirajZahtjev(korime, lozinka))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }

        String korisnik = new PomocJson().dajCistiTekst(korime, "korime").get(0);
        DBodgovorLista odgovorBaze;
        if (dodatniParam != null && !dodatniParam.isEmpty())
        {
            try
            {
                int trenutnaStranica = dajCistiBroj(dodatniParam);
                String dodao = new PomocJson().dajCistiTekst(dodatniParam, "dodao").get(0);
                pisiuDnevnik(korisnik, "dohvaca podatke aerodroma za korisnika: " + dodao);
                odgovorBaze = odradiStranicenjeAerodroma(dodao, trenutnaStranica);
            } catch (Exception ex)
            {
                odgovorBaze = new DBodgovorLista();
                odgovorBaze.setError(Greske.SQLex);
            }
        } else
        {
            pisiuDnevnik(korisnik, "dohvaca sve aerodrome");
            odgovorBaze = veza.selectObjekte("myairports", new Aerodrom());
        }

        OdgovorRest returnMe = new OdgovorRest(odgovorBaze);

        return returnMe.toString();
    }

    private DBodgovorLista odradiStranicenjeAerodroma(String dodao, int trenutnaStranica)
    {
        VezaDB veza = VezaDB.getInstanca();
        DBodgovorLista odgovorBaze;

        Stranicenje stranicenje = new Stranicenje("myairports", "icao");
        String whereDio = String.format("korisnik = '%s'", dodao);
        WherePart where = stranicenje.dajWhereZaStranicenje(whereDio, trenutnaStranica);
        odgovorBaze = veza.selectObjekt("myairports", new Aerodrom(), where);
        odgovorBaze = zapisiBrojStranicaZaAerodrome(odgovorBaze, stranicenje.getBrojStranica());

        return odgovorBaze;
    }

    /*GET metoda - na bazi putanje "{id}" - za izabrani aerodrom vraća podatke. Vraća podatke u application/json  formatu.  
    Struktura  odgovora  je  u  sljedeća:  {"odgovor":  [{...}],  "status": "OK"} | {"status": "ERR", "poruka": "tekst poruke"}.*/
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getJsonId(@PathParam("id") String id, @QueryParam("lozinka") String lozinka, @QueryParam("korime") String korime)
    {
        if (!autenticirajZahtjev(korime, lozinka))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }
        String korisnik = new PomocJson().dajCistiTekst(korime, "korime").get(0);
        pisiuDnevnik(korisnik, "dohvaca podatke izabranog aerodroma");

        DBodgovor odgovorBaze = veza.selectObjekt("myairports", new Aerodrom(), id);
        OdgovorRest returnMe = new OdgovorRest(odgovorBaze);

        return returnMe.toString();
    }

    /*GET  metoda  -  na  bazi  putanje  "{id}/avion"  -  za  izabrani  aerodrom  vraća  podatke  njegove avine. 
    Vraća   podatke   u   application/json   formatu.   Struktura   odgovora   je   u   
    sljedeća: {"odgovor": [{...}], "status": "OK"} | {"status": "ERR", "poruka": "tekst poruke"}. */
    @GET
    @Path("{id}/avion")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonIdAvion(@PathParam("id") String id, @QueryParam("lozinka") String lozinka, @QueryParam("korime") String korime)
    {
        if (!autenticirajZahtjev(korime, lozinka))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }
        String korisnik = new PomocJson().dajCistiTekst(korime, "korime").get(0);
        pisiuDnevnik(korisnik, "dohvaca avione izabranog aerodroma");

        String where = String.format("estDepartureAirport='%s'", id);
        DBodgovorLista odgovorBaze = veza.selectObjekt("AIRPLANES", new Avion(), new WherePart(where));
        OdgovorRest returnMe = new OdgovorRest(odgovorBaze);

        return returnMe.toString();
    }

    /*POST  metoda  -  na  bazi  putanje  "{id}/avion"  -  dodaje  avion(e)  (šalje  se  icao24  kod) aerodromu koji ima icao = id. 
    Šalju se podaci u application/json formatu [{"icao24":  "icao24 kod"},  ...].  Vraća  odgovor  u  application/json  formatu.  
    Struktura  odgovora  je  u  sljedeća: {"odgovor": [], "status": "OK"}  |  {"status": "ERR", "poruka": "tekst poruke"}. */
    @POST
    @Path("{id}/avion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJsonIdAvion(@PathParam("id") String icao, String podaciJson)
    {
        if (!autenticirajZahtjevKaoLista(podaciJson))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }

        PomocJson jPomoc = new PomocJson(podaciJson);
        List<String> cistiIcao24Lista = jPomoc.dajCistiTekstLista("icao24");
        String korisnik = jPomoc.dajCistiTekstLista("korime").get(0);
        
        pisiuDnevnik(korisnik, "dodaje avion(e)");

        List<Avion> noviAvioni = new ArrayList<>();

        for (String cistiIcao24 : cistiIcao24Lista)
        {
            String where = String.format("icao24='%s'", cistiIcao24);
            WherePart wPart = new WherePart(where);
            DBodgovorLista listaOdg = veza.selectObjekt("AIRPLANES", new Avion(), wPart);
            if (listaOdg.rezultatOk())
            {
                noviAvioni.addAll((List<Avion>) listaOdg.dajRezultat());
            }
        }
        postaviAvioneGrupe(noviAvioni);

        return null;
    }

    /*POST  metoda  -  osnovna  adresa  -  dodaje  aerodrom  (šalje  se  icao  kod).  
    Šalju  se  podaci  u application/json  formatu  {"icao":    "icao  kod"}.  Vraća  odgovor  u  application/json  formatu. 
    Struktura  odgovora  je  u  sljedeća:  {"odgovor":  [],  "status":  "OK"}    |    {"status":  "ERR", "poruka": "tekst poruke"}.*/
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postJson(String podaciJson)
    {
        if (!autenticirajZahtjev(podaciJson))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }

        PomocJson jPomoc = new PomocJson(podaciJson);
        List<String> cistiIcaoKodvi = jPomoc.dajCistiTekst("icao");
        String korime = jPomoc.dajCistiTekst("korime").get(0);
        
        pisiuDnevnik(korime, "dodaje aerodrom");
        List<OdgovorRest> listaOdgovora = new ArrayList<>();

        for (String cistiIcao : cistiIcaoKodvi)
        {
            DBodgovor odgovorAirports = veza.selectObjekt("AIRPORTS", new Aerodrom(), cistiIcao);

            listaOdgovora.add(provjeriDodavanjeAerodroma(odgovorAirports, korime));
        }

        return listaOdgovora.toString();
    }

    private OdgovorRest provjeriDodavanjeAerodroma(DBodgovor odgovorAirports, String korime)
    {
        OdgovorRest odgZaRest;
        VezaDB veza = VezaDB.getInstanca();
        if (odgovorAirports == null || !odgovorAirports.rezultatOk())
        {
            odgZaRest = new OdgovorRest(odgovorAirports);
        } else
        {
            odgZaRest = dodajAerodrom(odgovorAirports, veza, korime);

            Aerodrom noviAerodrom = (Aerodrom) odgovorAirports.dajRezultat();
            dodajAerodromGrupi(noviAerodrom);
        }

        return odgZaRest;
    }

    private OdgovorRest dodajAerodrom(DBodgovor odgovorAirports, VezaDB veza, String korisnik)
    {
        Aerodrom zaDodati = (Aerodrom) odgovorAirports.dajRezultat();
        zaDodati.setKorime(korisnik);
        DBodgovor odgovorUpisa = veza.zapisiUTablicu("MYAIRPORTS", zaDodati);
        OdgovorRest odgZaRest = new OdgovorRest(odgovorUpisa);
        return odgZaRest;
    }

    /*PUT metoda - na bazi putanje "{id}" - za izabrani aerodrom ažurira podatke (šalju se naziv i adresa, 
    koja služi za preuzimanje geo lokacije). Šalju se podaci u application/json formatu. Vraća podatke u application/json formatu. 
    Struktura odgovora je u sljedeća: {"odgovor": [], "status": "OK"}  |  {"status": "ERR", "poruka": "tekst poruke"}.*/
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJsonId(@PathParam("id") String id, String jsonPodaci)
    {
        if (!autenticirajZahtjev(jsonPodaci))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }

        String korime = new PomocJson().dajCistiTekst(jsonPodaci, "korime").get(0);
        pisiuDnevnik(korime, "za izabrani aerodrom azurira podatke lokacije");

        DBodgovor odg = veza.selectObjekt("MYAIRPORTS", new Aerodrom(), id);
        if (odg == null || !odg.rezultatOk())
        {
            OdgovorRest odgZaRest = new OdgovorRest(odg);
            return odgZaRest.toString();
        }

        Aerodrom a = (Aerodrom) odg.dajRezultat();
        a = dajAerodromsNovomLokacijom(jsonPodaci, a);

        odg = veza.updateObjekt("MYAIRPORTS", a);
        if (odg.rezultatOk())
        {
            azurirajAerodromGrupi(a);
        }

        OdgovorRest odgRest = new OdgovorRest(odg);
        return odgRest.toString();
    }

    private Aerodrom dajAerodromsNovomLokacijom(String jsonPodaci, Aerodrom a)
    {
        PomocServisi servisi = new PomocServisi((Konfiguracija) SlusacAplikacije.getSc().getAttribute("konfiguracija"));
        String naziv = new PomocJson().dajCistiTekst(jsonPodaci, "naziv").get(0);
        a.setLokacija(servisi.dajLIQGeoLokaciju(naziv));

        return a;
    }

    /*DELETE  metoda  -  na  bazi  putanje  "{id}"  -  briše  izabrani  aerodrom.  Vraća  podatke  u application/json formatu. 
    Struktura odgovora je u sljedeća: {"odgovor": [], "status": "OK"}  |  {"status": "ERR", "poruka": "tekst poruke"}. */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteId(@PathParam("id") String id, @HeaderParam("lozinka") String lozinka, @HeaderParam("korime") String korime)
    {
        if (!autenticirajZahtjev(korime, lozinka))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }
        String korisnik = new PomocJson().dajCistiTekst(korime, "korime").get(0);
        pisiuDnevnik(korisnik, "brisanje aerodroma prema ID");

        DBodgovor odg = veza.selectObjekt("MYAIRPORTS", new Aerodrom(), id);
        if (odg == null || !odg.rezultatOk())
        {
            OdgovorRest odgZaRest = new OdgovorRest(odg);
            return odgZaRest.toString();
        }

        odg = veza.deleteObjekt("MYAIRPORTS", (Aerodrom) odg.dajRezultat());
        if (odg.rezultatOk())
        {
            obrisiAerodromGrupe(id);
        }

        OdgovorRest odgRest = new OdgovorRest(odg);
        return odgRest.toString();
    }

    /*DELETE metoda - na bazi putanje ""{id}/avion/" - briše sve avione za aerodroma koji ima icao  =  id.  
    Vraća  podatke  u  application/json  formatu.  Struktura  odgovora  je  u  sljedeća: 
    {"odgovor": [], "status": "OK"}  |  {"status": "ERR", "poruka": "tekst poruke"}. */
    @DELETE
    @Path("{id}/avion")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deleteIdAvion(@PathParam("id") String icao24, @HeaderParam("lozinka") String lozinka, @HeaderParam("korime") String korime)
    {
        if (!autenticirajZahtjev(korime, lozinka))
        {
            return dajGreskuAutentikacije();
        }
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return dajGreskuVeze();
        }
        String korisnik = new PomocJson().dajCistiTekst(korime, "korime").get(0);
        pisiuDnevnik(korisnik, "brisanje aviona prema ID");

        DBodgovor odg = veza.selectObjekt("AIRPLANES", new Avion(), icao24);
        if (odg == null || !odg.rezultatOk())
        {
            OdgovorRest odgZaRest = new OdgovorRest(odg);
            return odgZaRest.toString();
        }

        odg = veza.deleteObjekt("AIRPLANES", (Avion) odg.dajRezultat());
        if (odg.rezultatOk())
        {
            brisiAvionGrupi();
        }

        OdgovorRest odgRest = new OdgovorRest(odg);
        return odgRest.toString();
    }

    private String dajGreskuVeze()
    {
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        JsonElement jE = gson.toJsonTree(Greske.VezaNull);
        jO.addProperty("status", "ERR");
        jO.add("poruka", jE);
        return gson.toJson(jO);
    }

    private String dajGreskuAutentikacije()
    {
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        JsonElement jE = gson.toJsonTree(Greske.SesijaNePostoji);
        jO.addProperty("status", "ERR");
        jO.add("poruka", jE);
        return gson.toJson(jO);
    }

    private boolean autenticirajZahtjev(String jsonKorime, String jsonLozinka)
    {
        List<String> lozinke = new PomocJson().dajCistiTekst(jsonLozinka, "lozinka");
        List<String> korime = new PomocJson().dajCistiTekst(jsonKorime, "korime");

        if (lozinke == null || korime == null || lozinke.isEmpty() || korime.isEmpty())
        {
            return false;
        }

        AutentikacijaKorisnikaDB auth = new AutentikacijaKorisnikaDB(korime.get(0), lozinke.get(0));

        Error err = auth.autenticiraj();
        if (err.equals(Error.OK_10))
        {
            return true;
        }

        return false;
    }

    private boolean autenticirajZahtjev(String podaciJson)
    {
        PomocJson jPomoc = new PomocJson(podaciJson);
        List<String> lozinke = jPomoc.dajCistiTekst("lozinka");
        List<String> korime = jPomoc.dajCistiTekst("korime");

        if (lozinke == null || korime == null || lozinke.isEmpty() || korime.isEmpty())
        {
            return false;
        }

        AutentikacijaKorisnikaDB auth = new AutentikacijaKorisnikaDB(korime.get(0), lozinke.get(0));

        Error err = auth.autenticiraj();
        if (err.equals(Error.OK_10))
        {
            return true;
        }

        return false;
    }

    private void pisiuDnevnik(String korisnik, String zahtjev)
    {
        DioAplikacije dioAplikacije = new DioAplikacije(DioAplikacije.Dio.REST_APP1, zahtjev);
        Dnevnik dnevnik = new Dnevnik(korisnik, dioAplikacije);
        dnevnik.setOpis("Zapisivanje REST zahtjeva aplikacije 1.");
        dnevnik.setRadnja("Upis");

        dnevnik.zapisi();
    }

    private static boolean postaviAvioneGrupe(List<Avion> avioniNovi)
    {
        Konfiguracija konfig = (Konfiguracija) SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");

        List<org.foi.nwtis.mkralj.servisi.soap.klijent.Avion> listaNovih = new ArrayList<>();
        for (Avion a : avioniNovi)
        {
            org.foi.nwtis.mkralj.servisi.soap.klijent.Avion av = new org.foi.nwtis.mkralj.servisi.soap.klijent.Avion();
            av.setCallsign(a.getCallsign());
            av.setEstarrivalairport(a.getEstArrivalAirport());
            av.setEstdepartureairport(a.getEstDepartureAirport());
            av.setIcao24(a.getIcao24());
            av.setId(a.getId());

            listaNovih.add(av);
        }

        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.postaviAvioneGrupe(korimeSVN, lozinkaSVN, listaNovih);
    }

    private static Boolean dodajAerodromGrupi(Aerodrom novi)
    {
        Konfiguracija konfig = (Konfiguracija) SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");

        org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom aerodrom = new org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom();
        aerodrom.setDrzava(novi.getDrzava());
        aerodrom.setIcao(novi.getIcao());

        org.foi.nwtis.mkralj.servisi.soap.klijent.Lokacija novaLok = new org.foi.nwtis.mkralj.servisi.soap.klijent.Lokacija();
        novaLok.setLatitude(novi.getLokacija().getLatitude());
        novaLok.setLongitude(novi.getLokacija().getLongitude());

        aerodrom.setLokacija(novaLok);
        aerodrom.setNaziv(novi.getNaziv());
        aerodrom.setStatus(AerodromStatus.AKTIVAN);

        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajAerodromGrupi(korimeSVN, lozinkaSVN, aerodrom);
    }

    private static boolean obrisiAerodromGrupe(java.lang.String idAerodrom)
    {
        Konfiguracija konfig = (Konfiguracija) SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");

        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.obrisiAerodromGrupe(korimeSVN, lozinkaSVN, idAerodrom);
    }

    private void azurirajAerodromGrupi(Aerodrom noviAerodrom)
    {
        if (obrisiAerodromGrupe(noviAerodrom.getIcao()))
        {
            dodajAerodromGrupi(noviAerodrom);
        }
    }

    private void brisiAvionGrupi()
    {
        //brisanje aerodroma pa se izbrisu svi avioni
        //TODO
    }

    private DBodgovorLista zapisiBrojStranicaZaAerodrome(DBodgovorLista odgovorBaze, int brojStranica)
    {
        if (odgovorBaze.rezultatOk())
        {
            List<Aerodrom> listaAerodroma = (List<Aerodrom>) odgovorBaze.dajRezultat();
            for (Aerodrom a : listaAerodroma)
            {
                a.setUkBrojStranica(brojStranica);
            }

            odgovorBaze.setResult(listaAerodroma);
        }

        return odgovorBaze;
    }

    private int dajCistiBroj(String dodatniParam)
    {
        PomocJson jPomoc = new PomocJson(dodatniParam);
        try
        {
            return Integer.parseInt(jPomoc.dajCistiTekst("trenutnaStranica").get(0));
        } catch (Exception ex)
        {
            return 1;
        }
    }

    private boolean autenticirajZahtjevKaoLista(String podaciJson)
    {
        PomocJson jPomocc = new PomocJson(podaciJson);
        List<String> korimeLista = jPomocc.dajCistiTekstLista("korime");
        List<String> lozinkeLista = jPomocc.dajCistiTekstLista("lozinka");
        
        if (lozinkeLista == null || korimeLista == null || lozinkeLista.isEmpty() || korimeLista.isEmpty())
        {
            return false;
        }

        AutentikacijaKorisnikaDB auth = new AutentikacijaKorisnikaDB(korimeLista.get(0), lozinkeLista.get(0));

        Error err = auth.autenticiraj();
        if (err.equals(Error.OK_10))
        {
            return true;
        }

        return false;
    }

}
