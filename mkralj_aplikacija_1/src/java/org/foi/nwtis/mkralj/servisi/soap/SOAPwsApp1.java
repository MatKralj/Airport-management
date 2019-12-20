package org.foi.nwtis.mkralj.servisi.soap;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkralj.DB.AutentikacijaKorisnikaDB;
import org.foi.nwtis.mkralj.DB.Stranicenje;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovor;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.DB.podaci.Avion;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.DioAplikacije;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.Dnevnik;
import org.foi.nwtis.mkralj.TimeStamp;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika;
import org.foi.nwtis.mkralj.servisi.PomocServisi;
import org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromStatus;
import org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service;
import org.foi.nwtis.mkralj.web.dretve.provjeraPresjedanja.ProvjeraPresjedanja;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.foi.nwtis.mkralj.web.podaci.Korisnik;
import org.foi.nwtis.mkralj.web.podaci.ParRelacija;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

@WebService(serviceName = "SOAPwsApp1")
public class SOAPwsApp1
{

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/nwtis.foi.hr_8080/NWTiS_2019/AerodromiWS.wsdl")
    private AerodromiWS_Service service;
 
    //zadnji preuzeti podaci o avionima za izabrani aerodrom. Vraća AvionLeti
    @WebMethod(operationName = "dajZadnjePreuzetePodatkeAviona")
    public List<AvionLeti> dajZadnjePreuzetePodatkeAviona(@WebParam(name = "icao") String icao, 
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        return (List<AvionLeti>)dajZadnjihNPreuzetePodatkeAviona(icao, 0, korime, lozinka);
    }
    
    //posljednjih n (n se unosi) podataka o avionima za izabrani aerodrom. Vraća List<AvionLeti>.
    @WebMethod(operationName = "dajZadnjihNPreuzetePodatkeAviona")
    public List<AvionLeti> dajZadnjihNPreuzetePodatkeAviona(@WebParam(name = "icao") String icao, 
            @WebParam(name = "zadnjihN") int zadnjihN, @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        
        VezaDB vezaDB = VezaDB.getInstanca();
        if(vezaDB == null)
            return null;
        if(zadnjihN==0)
            pisiuDnevnik(korime, "daj zadnje preuzete podatke");
        else
            pisiuDnevnik(korime, "daj zadnjih N preuzetih podataka");
        
        String whereMax = String.format("estDepartureAirport='%s'", icao);
        String zadnjiStored = vezaDB.selectMaxVal("AIRPLANES", "stored", new WherePart(whereMax));
        
        String maxOd = dajPrviDatum(zadnjiStored);
        String maxDo = dajDrugiDatum(zadnjiStored);
        
        String limit = dajLimit(zadnjihN);
        String where = String.format("estDepartureAirport='%s' AND "
                + "stored BETWEEN '%s' AND '%s'%s", icao, maxOd, maxDo, limit);
        DBodgovorLista listaZadnjihN = vezaDB.selectObjekt("AIRPLANES", new Avion(), new WherePart(where));
        
        return (List<AvionLeti>)listaZadnjihN.dajRezultat();
    }
    
    private String dajPrviDatum(String zadnjiStored)
    {
        TimeStamp ts = new TimeStamp();
        int zadnji = ts.dajUnixIzBaze(zadnjiStored);
        int konacniDatum = Math.abs(zadnji - 5);
        return ts.dajDatumIzEpochZaBazu(konacniDatum);
    }

    private String dajDrugiDatum(String zadnjiStored)
    {
        TimeStamp ts = new TimeStamp();
        int zadnji = ts.dajUnixIzBaze(zadnjiStored);
        int konacniDatum = zadnji + 5;
        return ts.dajDatumIzEpochZaBazu(konacniDatum);
    }

    private String dajLimit(int zadnjihN)
    {
        String limit = "";
        if(zadnjihN <= 0)
            limit = "";
        else
            limit = " Limit "+zadnjihN;
        
        return limit;
    }
    
    //podaci o letovima avionima koji su poletjeli s izabranog aerodroma u nekom vremenskom intervalu  
    //(od  datuma,  do  datuma  kao  timestamp)  u  vremenskom  slijedu  kako  su  avioni prolazili (firstSeen). Vraća List<AvionLeti>.
    @WebMethod(operationName = "dajPodatkeOLetovimaPoletjelim")
    public List<Avion> dajPodatkeOLetovimaPoletjelim(@WebParam(name = "icao") String icao, 
            @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka, int trenutnaStranica)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        VezaDB vezaDB = VezaDB.getInstanca();
        if(vezaDB==null)
            return null;
        pisiuDnevnik(korime, "daj podatke o letovima poletjelim s aerodroma");
        
        Stranicenje stranicenje = new Stranicenje("AIRPLANES", "id");
        
        int odVremenaUnix = new TimeStamp().dajUnixDatum(odVremena);
        int doVremenaUnix = new TimeStamp().dajUnixDatum(doVremena);
        String where = String.format("estDepartureAirport='%s' AND firstSeen between %s AND %s ORDER BY firstSeen ASC", 
                icao, odVremenaUnix, doVremenaUnix);
        WherePart wherePart = stranicenje.dajWhereZaStranicenje(where, trenutnaStranica);
        
        DBodgovorLista listaOdgovora = vezaDB.selectObjekt("AIRPLANES", new Avion(), wherePart);
        if(listaOdgovora==null || !listaOdgovora.rezultatOk())
            return null;
        
        List<Avion> returnList = dajListiUkBrStranica(listaOdgovora, stranicenje.getBrojStranica());
        return returnList;
    }
    
    //podaci o letovima izabranog aviona u nekom vremenskom intervalu (od datuma, do datuma kao   timestamp)   
    //u   vremenskom   slijedu   kako   je   avion   prolazio   (firstSeen).   Vraća List<AvionLeti>
    @WebMethod(operationName = "dajPodatkeOLetovimaAviona")
    public List<Avion> dajPodatkeOLetovimaAviona(@WebParam(name = "icao24") String icao24, 
            @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka, int trenutnaStranica)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        VezaDB vezaDB = VezaDB.getInstanca();
        if(vezaDB==null)
            return null;
        pisiuDnevnik(korime, "daj podatke o letovima aviona");
        
        int odVremenaUnix = new TimeStamp().dajUnixDatum(odVremena);
        int doVremenaUnix = new TimeStamp().dajUnixDatum(doVremena);
        Stranicenje stranicenje = new Stranicenje("AIRPLANES", "id");
        
        String where = String.format("icao24='%s' AND firstSeen between %s AND %s ORDER BY firstSeen ASC", 
                icao24, odVremenaUnix, doVremenaUnix);
        WherePart wherePart = stranicenje.dajWhereZaStranicenje(where, trenutnaStranica);
        
        DBodgovorLista listaOdgovora = vezaDB.selectObjekt("AIRPLANES", new Avion(), wherePart);
        if(listaOdgovora==null || !listaOdgovora.rezultatOk())
            return null;
        
        return dajListusUkBrojemStr(listaOdgovora, stranicenje.getBrojStranica());
    }
    
    //podaci o letovima izabranog aviona u nekom vremenskom intervalu (od datuma, do datuma kao timestamp). 
    //Vraća List<String> naziva aerodroma u vremenskom slijedu kako je avion prolazio (firstSeen).
    @WebMethod(operationName = "dajPodatkeOLetovimaAvionaAerodromi")
    public List<String> dajPodatkeOLetovimaAvionaAerodromi(@WebParam(name = "icao24") String icao24, 
            @WebParam(name = "odVremena") String odVremena, @WebParam(name = "doVremena") String doVremena,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        List<Avion> listaLetova = dajPodatkeOLetovimaAviona(icao24, odVremena, doVremena, korime, lozinka, -1);
        if(listaLetova==null)
            return null;
        pisiuDnevnik(korime, "daj aerodrome (List<String>) aviona poletjelih u intervalu");
        
        List<String> listaNazivaAerodroma = new ArrayList<>();
        for(AvionLeti a : listaLetova)
        {
            listaNazivaAerodroma.add(a.getEstDepartureAirport());
        }
        return listaNazivaAerodroma;
    }
    
    //važeći meteorološki podaci za izabrani aerodrom (putem openweathermap.org web servisa). Vraća MeteoPodaci.
    @WebMethod(operationName = "dajMeteoPodatkeAerodroma")
    public MeteoPodaci dajMeteoPodatkeAerodroma(@WebParam(name = "icao") String icao,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        VezaDB vezaDB = VezaDB.getInstanca();
        if(vezaDB==null)
            return null;
        pisiuDnevnik(korime, "daj meteo podatke za aerodrom");
        
        DBodgovor odgovor = vezaDB.selectObjekt("MYAIRPORTS", new Aerodrom(), icao);
        if(odgovor==null || !odgovor.rezultatOk())
            return null;
        
        Konfiguracija konfig = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        PomocServisi pomocServis = new PomocServisi(konfig);
        
        Lokacija lokacija = ((Aerodrom)odgovor.dajRezultat()).getLokacija();
        return pomocServis.dajOWMeteoPodatke(lokacija.getLatitude(), lokacija.getLongitude());
    }
    
    //dodaj korisnika. Prima Korisnik. Vraća true ako je uspješno ili false.
    @WebMethod(operationName = "dodajKorisnika")
    public boolean dodajKorisnika(@WebParam(name = "korisnik") Korisnik korisnik)
    {
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return false;
        pisiuDnevnik(korisnik.getKorime(), "registracija korisnika");
        
        DBodgovor odg = veza.zapisiUTablicu("Korisnici", korisnik);
        return odg.jednostavnaRadnjaUspjela();
    }
    
    //ažuriraj korisnika. Prima Korisnik. Vraća true ako je uspješno ili false.
    @WebMethod(operationName = "azurirajKorisnika")
    public boolean azurirajKorisnika(@WebParam(name = "korisnik") Korisnik korisnik,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return false;
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return false;
        pisiuDnevnik(korime, "azuriranje korisnika");
        
        DBodgovor odg = veza.updateObjekt("Korisnici", korisnik);
        return odg.jednostavnaRadnjaUspjela();
    }
    
    //podaci o korisnicima. Vraća List<Korisnik>.
    //ako je -1, traži podatke svih korisnika. Ako je ==-1, daj korisnika korime
    @WebMethod(operationName = "dajPodatkeSvihKorisnika")
    public List<Korisnik> dajPodatkeSvihKorisnika(@WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka,
            int trenutnaStranica)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return null;
        pisiuDnevnik(korime, "daj podatke svih korisnika uz stranicenje");

        List<Korisnik> listaKor;
        if(trenutnaStranica!=-1)
        {
            listaKor = dajListuStranicenjeKorisnik(trenutnaStranica);
        }else{
            DBodgovor odg = veza.selectObjekt("Korisnici", new Korisnik(), korime);
            
            listaKor = new ArrayList<>();
            if(odg.rezultatOk())
                listaKor.add((Korisnik) odg.dajRezultat());
        }

        return listaKor;
    }
    
    private List<Korisnik> dajListuStranicenjeKorisnik(int trenutnaStranica)
    {
        VezaDB veza = VezaDB.getInstanca();
        int brojStranica = 0;
        
        Stranicenje stranicenje = new Stranicenje("korisnici", "korime");
        WherePart where = stranicenje.dajWhereZaStranicenje("1", trenutnaStranica);
        DBodgovorLista odg = veza.selectObjekt("Korisnici", new Korisnik(), where);
        brojStranica = stranicenje.getBrojStranica();
        
        List<Korisnik> listaKor = (List<Korisnik>)odg.dajRezultat();
        
        if(listaKor!=null)
        {
            for(Korisnik k : listaKor)
            {
                k.setBrojStrStranicenje(brojStranica);
            }
        }
        
        return listaKor;
    }
    
    
    /*podaci o potrebnim letovima od izabranog polazišnog aerodroma do izabranog odredišnog aerodroma u nekom vremenskom intervalu 
    (od  datuma, do datuma kao timestamp). Vraća List<AvionLeti>  za  letove  avione  u  vremenskom  slijedu  kako  je  avion  
    prolazio  između pojedinihi aerodroma. Ovo bi se mogao koristiti kao planer putovanja od jednog aerodroma do drugog aerodroma 
    pri čemu nije unaprijed poznato kroz koje je ostale aerodrome potrebno prolaziti.  Postoji  mogućnost  direktne  veze/leta  
    između  ta  dva  aerodroma  no  ovdje  se prvenstveno očekuje da treba obaviti barem jedno presjedanje.  Potrebno je voditi 
    brigu da vrijeme  lastSeen  kod  leta  aviona  u  jedan  odredišni aerodrom  i  vrijeme  firstSeen  kod  leta aviona s njega 
    kao novog polazišnog aerodroma u sljedeći odredišni aerodrom bude veća od minimalno potrebno vremena za presjedanj (postavnom je određeno) */
    @WebMethod(operationName = "dajPotrebneLetove")
    public List<ParRelacija> dajPotrebneLetove(@WebParam(name = "IcaoOdAerodroma") String IcaoOdAerodroma,
            @WebParam(name = "IcaoDoAerodroma") String IcaoDoAerodroma, @WebParam(name = "odVremena") String odVremena,
            @WebParam(name = "doVremena") String doVremena, @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        //todo:  nije jos dovrseno
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return null;
        pisiuDnevnik(korime, "daj podatke o potrebnim letoovima s presjedanjima");
        
        int odVremenaUnix = new TimeStamp().dajUnixDatum(odVremena);
        int doVremenaUnix = new TimeStamp().dajUnixDatum(doVremena);
        String wherePolaz = String.format("estDepartureAirport='%s' AND firstSeen>=%s AND lastSeen<=%s", 
                IcaoOdAerodroma, odVremenaUnix, doVremenaUnix);
        DBodgovorLista odgovorPolazisni =  veza.selectObjekt("airplanes", new Avion(), new WherePart(wherePolaz));
        
        if(odgovorPolazisni==null || !odgovorPolazisni.rezultatOk())
            return null;
        Konfiguracija konf = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        int minimalnoVrijemePrejedanja = dajMinimalnoVrijeme(konf);
        List<ProvjeraPresjedanja> ppLista = new ArrayList<>();
        
        ThreadSafeKolekcija<ParRelacija> listaMogucihRelacija = new ThreadSafeKolekcija<>();
        for(Avion a : (List<Avion>)odgovorPolazisni.dajRezultat())
        {
            ProvjeraPresjedanja pp = new ProvjeraPresjedanja(listaMogucihRelacija, a, minimalnoVrijemePrejedanja, IcaoDoAerodroma);
            ppLista.add(pp);
            pp.start();
        }
        for(ProvjeraPresjedanja pp : ppLista)
        {
            for(ParRelacija par : pp.getRjesenje())
            {
                listaMogucihRelacija.dodajRazlicitiElement(par);
            }
        }
        return listaMogucihRelacija.getObjekti();
    }
    
    
    
    //udaljenost u km između dva izabrana aerodroma
    @WebMethod(operationName = "dajUdaljenostKm")
    public double dajUdaljenostKm(@WebParam(name = "IcaoOdAerodroma") String IcaoOdAerodroma,
            @WebParam(name = "IcaoDoAerodroma") String IcaoDoAerodroma,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {
        if(!autenticirajZahtjev(korime, lozinka))
            return 0;
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return 0f;
        pisiuDnevnik(korime, "daj udaljenost 2 aerodroma");
        
        int earthRadiusKm = 6367;
        DBodgovor odgovorOd =  veza.selectObjekt("myairports", new Aerodrom(), IcaoOdAerodroma);
        DBodgovor odgovorDo =  veza.selectObjekt("myairports", new Aerodrom(), IcaoDoAerodroma);
        
        Lokacija lokacijaOd = ((Aerodrom)odgovorOd.dajRezultat()).getLokacija();
        Lokacija lokacijaDo = ((Aerodrom)odgovorDo.dajRezultat()).getLokacija();
        
        double dLat = degreesToRadians(izracunajLat(lokacijaDo, lokacijaOd));
        double dLong = degreesToRadians(izracunajLong(lokacijaDo, lokacijaOd));
        
        double lat1Rad = degreesToRadians(stringToDouble(lokacijaOd.getLatitude()));
        double lat2Rad = degreesToRadians(stringToDouble(lokacijaDo.getLatitude()));

        double a = pow(sin(dLat/2.0), 2) + cos(lat1Rad) * cos(lat2Rad) * pow(sin(dLong/2.0), 2);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a)); 
        return earthRadiusKm * c;
    }
    
    
    private double degreesToRadians(double degrees)
    {
        return (degrees * Math.PI) / 180.0;
    }

    private double izracunajLat(Lokacija lokacijaDo, Lokacija lokacijaOd)
    {
        double latDo = stringToDouble(lokacijaDo.getLatitude());
        double latOd = stringToDouble(lokacijaOd.getLatitude());

        return latDo-latOd;
    }

    private double izracunajLong(Lokacija lokacijaDo, Lokacija lokacijaOd)
    {
        double lonDo = stringToDouble(lokacijaDo.getLongitude());
        double lonOd = stringToDouble(lokacijaOd.getLongitude());

        return lonDo-lonOd;
    }

    //aerodromi koji su udaljeneni od izabranog aerodroma unutar određenih granica u km (npr. 200 i 2000).
    @WebMethod(operationName = "dajAerodromeUGranicama")
    public List<Aerodrom> dajAerodromeUGranicama(@WebParam(name = "icao") String icao,
            @WebParam(name = "donjaGranica") int donjaGranica, @WebParam(name = "gornjaGranica") int gornjaGranica,
            @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {   
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return null;
        pisiuDnevnik(korime, "daj aerodrome koji su udaljeni od aerodroma unutar odredenih granica");
        
        DBodgovorLista listaSvihOdgovora =  veza.selectObjekte("myairports", new Aerodrom());
        if(listaSvihOdgovora==null || !listaSvihOdgovora.rezultatOk())
            return null;
        
        List<Aerodrom> sviAerodromi = (List<Aerodrom>)listaSvihOdgovora.dajRezultat();
        List<Aerodrom> konacniAerodromi = new ArrayList<>();
        for(Aerodrom a : sviAerodromi)
        {
            double udaljenost = dajUdaljenostKm(icao, a.getIcao(), korime, lozinka);
            if(udaljenost>=donjaGranica && udaljenost<=gornjaGranica)
                konacniAerodromi.add(a);
        }
        
        return konacniAerodromi;
    }
    
    @WebMethod(operationName = "aktivirajAerodromGrupi")
    public boolean aktivirajAerodromGrupi(@WebParam(name = "icao") String icao,
           @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {   
        if(!autenticirajZahtjev(korime, lozinka))
            return false;
        
        pisiuDnevnik(korime, "aktivira aerodrom "+icao+" grupi");

        boolean rezultat = aktivirajAerodromGrupe(icao);
        
        return rezultat;
    }
    
    @WebMethod(operationName = "blokirajAerodromGrupi")
    public boolean blokirajAerodromGrupi(@WebParam(name = "icao") String icao,
           @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {   
        if(!autenticirajZahtjev(korime, lozinka))
            return false;
        
        pisiuDnevnik(korime, "blokira aerodrom "+icao+" grupi");
        
        boolean rezultat = blokirajAerodromGrupe(icao);
        
        return rezultat;
    }

    @WebMethod(operationName = "dajStatusAerodromaGr")
    public AerodromStatus dajStatusAerodromaGr(@WebParam(name = "icao") String icao,
           @WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {   
        if(!autenticirajZahtjev(korime, lozinka))
            return null;
        
        pisiuDnevnik(korime, "status aerodroma "+icao+" grupe");
        
        AerodromStatus rezultat= dajStatusAerodromaGrupe(icao);
        
        return rezultat;
    }
    
    @WebMethod(operationName = "dajBrojPorukaOdMqtt")
    public int dajBrojPorukaOdMqtt(@WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {   
        if(!autenticirajZahtjev(korime, lozinka))
            return -1;
        
        pisiuDnevnik(korime, "broj poruke od mqtt");
        
        int rezultat = dajBrojPoruka();
        
        return rezultat;
    }
    
    @WebMethod(operationName = "dajIntervalMqtt")
    public int dajIntervalMqtt(@WebParam(name = "korime") String korime, @WebParam(name = "lozinka") String lozinka)
    {   
        if(!autenticirajZahtjev(korime, lozinka))
            return -1;
        
        pisiuDnevnik(korime, "interval od mqtt");
        
        int rezultat = dajTrajanjeCiklusa();
        
        return rezultat;
    }
    
    private double stringToDouble(String s)
    {
         try{
            return Double.parseDouble(s);
        }catch(NumberFormatException ex)
        {
            return 0;
        }
    }

    private int dajMinimalnoVrijeme(Konfiguracija konf)
    {
        try{
            String minPresStr = konf.dajPostavku("min.presjedanje");
            
            return Integer.parseInt(minPresStr)*60;
        }catch(NumberFormatException ex)
        {
            return 0;
        }
    }
    
    private boolean autenticirajZahtjev(String korime, String lozinka)
    {
        if(lozinka==null || korime==null || lozinka.isEmpty() || korime.isEmpty())
            return false;
        
        AutentikacijaKorisnikaDB auth = new AutentikacijaKorisnikaDB(korime, lozinka);
        
        GreskaZaKorisnika.Error err = auth.autenticiraj();
        if(err.equals(GreskaZaKorisnika.Error.OK_10))
            return true;
        
        return false;
    }
    
    private void pisiuDnevnik(String korisnik, String zahtjev)
    {
        DioAplikacije dioAplikacije = new DioAplikacije(DioAplikacije.Dio.SOAP_APP1, zahtjev);
        Dnevnik dnevnik = new Dnevnik(korisnik, dioAplikacije);
        dnevnik.setOpis("Zapisivanje SOAP zahtjeva aplikacije 1.");
        dnevnik.setRadnja("Upis");
        
        dnevnik.zapisi();
    }

    private boolean aktivirajAerodromGrupe(java.lang.String idAerodrom)
    {
        Konfiguracija konfig = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");
        
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajAerodromGrupe(korimeSVN, lozinkaSVN, idAerodrom);
    }

    private boolean blokirajAerodromGrupe(java.lang.String idAerodrom)
    {
        Konfiguracija konfig = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");
        
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajAerodromGrupe(korimeSVN, lozinkaSVN, idAerodrom);
    }

    private AerodromStatus dajStatusAerodromaGrupe(java.lang.String idAerodrom)
    {
        Konfiguracija konfig = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");
        
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusAerodromaGrupe(korimeSVN, lozinkaSVN, idAerodrom);
    }

    private List<Avion> dajListiUkBrStranica(DBodgovorLista listaOdgovora, int brojStranica)
    {
        List<Avion> listaAviona = (List<Avion>)listaOdgovora.dajRezultat();
        for(Avion a : listaAviona)
        {
            a.setUkBrojStranica(brojStranica);
        }
        
        return listaAviona;
    }

    private List<Avion> dajListusUkBrojemStr(DBodgovorLista listaOdgovora, int ukBrojStr)
    {
        List<Avion> returnList = (List<Avion>) listaOdgovora.dajRezultat();
        
        for(Avion a : returnList)
        {
            a.setUkBrojStranica(ukBrojStr);
        }
        
        return returnList;
    }

    private int dajBrojPoruka()
    {
        Konfiguracija konfig = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");
        
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajBrojPoruka(korimeSVN, lozinkaSVN);
    }

    private int dajTrajanjeCiklusa()
    {
        Konfiguracija konfig = (Konfiguracija)SlusacAplikacije.getSc().getAttribute("konfiguracija");
        String korimeSVN = konfig.dajPostavku("aerodromi_ws.korime");
        String lozinkaSVN = konfig.dajPostavku("aerodromi_ws.lozinka");
        
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajTrajanjeCiklusa(korimeSVN, lozinkaSVN);
    }
    
}
