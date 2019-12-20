package org.foi.nwtis.mkralj.web.zrna;

import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.servisi.soap.MeteoPodaci;
import org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service;
import org.foi.nwtis.mkralj.staticRest.RESTapp1;
import org.foi.nwtis.mkralj.web.zrna.pomoc.PomocJson;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Overlay;

@Named(value = "aerodromi")
@SessionScoped
public class RadsVlastitimAerodromima implements Serializable
{

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkralj_aplikacija_1/SOAPwsApp1.wsdl")
    private SOAPwsApp1_Service service;
    
    private MapModel mapModel = new DefaultMapModel();

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    private int trenutnaStranica = 1;
    private int ukBrojStr = 1;
    
    private String aerodromIcaoZaDodavanje;
    private String primljenaPoruka;
    
    private String temperatura;
    private String vlaga;
    private String tlak;
    private String ikonaVrijeme;

    private String odabraniIcao;
    private String odabraniLongitude;
    private String odabraniLatitude;
    private Marker odabraniMarker;

    public MapModel getMapModel()
    {
        LatLng latitudeLongitude = new LatLng(dajDoubleLokacije(odabraniLatitude), dajDoubleLokacije(odabraniLongitude));
        Overlay overlayMarker = new Marker(latitudeLongitude, "Podaci za: "+this.odabraniIcao);
        overlayMarker.setData(formatirajPodatkeOverlay());
        
        this.mapModel.addOverlay(overlayMarker);
        
        return mapModel;
    }

    public void setMapModel(MapModel mapModel)
    {
        this.mapModel = mapModel;
    }

    public String getOdabraniIcao()
    {
        if(odabraniIcao!=null)
            return odabraniIcao;
        
        return "";
    }

    public void setOdabraniIcao(String odabraniIcao)
    {
        this.odabraniIcao = odabraniIcao;
    }
    public String getTemperatura()
    {
        if(temperatura!=null)
            return temperatura;
        
        return "";
    }

    public void setTemperatura(String temperatura)
    {
        this.temperatura = temperatura;
    }

    public String getVlaga()
    {
        if(vlaga!=null)
            return vlaga;
        
        return "";
    }

    public void setVlaga(String vlaga)
    {
        this.vlaga = vlaga;
    }

    public String getTlak()
    {
        if(tlak!=null)
            return tlak;
        
        return "";
    }

    public void setTlak(String tlak)
    {
        this.tlak = tlak;
    }

    public String getPrimljenaPoruka()
    {
        return primljenaPoruka;
    }

    public void setPrimljenaPoruka(String primljenaPoruka)
    {
        this.primljenaPoruka = primljenaPoruka;
    }

    public String getAerodromIcaoZaDodavanje()
    {
        return aerodromIcaoZaDodavanje;
    }

    public void setAerodromIcaoZaDodavanje(String aerodromIcaoZaDodavanje)
    {
        this.aerodromIcaoZaDodavanje = aerodromIcaoZaDodavanje;
    }

    private List<Aerodrom> listaAerodroma = new ArrayList<>();

    public List<Aerodrom> getListaAerodroma()
    {
        return listaAerodroma;
    }

    public void setListaAerodroma(List<Aerodrom> listaAerodroma)
    {
        this.listaAerodroma = listaAerodroma;
    }

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

    public void setUkBrojStr(int ukBrojStr)
    {
        this.ukBrojStr = ukBrojStr;
    }

    public int getTrenutnaStranica()
    {
        return trenutnaStranica;
    }

    public void setTrenutnaStranica(int trenutnaStranica)
    {
        this.trenutnaStranica = trenutnaStranica;
    }

    public String preuzmi()
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        String korimeJson = upravljanjeKorisnicima.dajKorimeJson(sesija);
        String lozinkaJson = upravljanjeKorisnicima.dajLozinkuJson(sesija);
        
        provjeriStranice();
        String dodatniPodaciJson = kreirajDodatnePodatke(korimeJson);

        RESTapp1 client = new RESTapp1();
        try
        {
            String jsonAerodromi = client.getJson(lozinkaJson, korimeJson, dodatniPodaciJson);

            this.listaAerodroma = dajAerodromeIzJson(jsonAerodromi);
            if (!listaAerodroma.isEmpty())
                this.ukBrojStr = listaAerodroma.get(0).getUkBrojStranica();
            else
                this.ukBrojStr = 1;
        }catch(Exception ex)
        {
            System.out.println("Exceptiooon: "+ex.getMessage());
        }
        primljenaPoruka="";

        return "";
    }
    
    public String dodaj()
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        String korimeLozinkaJson = upravljanjeKorisnicima.dajPodatkeZaAutentikacijuJson(sesija);
        String konacniPodaciZaSlanjeJson = dodajIcaoArgument(korimeLozinkaJson);
        
        RESTapp1 client = new RESTapp1();
        String odgovor = client.postJson(konacniPodaciZaSlanjeJson);
        if(odgovor.contains("OK"))
            primljenaPoruka="dodavanje.aerodrom.uspjesno";
        else
            primljenaPoruka="dodavanje.aerodrom.neuspjesno";
        
        return "";
    }
    
    public String upravljaj(String icao)
    {
        this.odabraniIcao = icao;
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        else
            new Redirecter().redirectTo("upravljanjeAerodromom.xhtml");
        return "";
    }
    
    public String preuzmiMeteo(String icao, String longitude, String latitude)
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
     
        this.mapModel = new DefaultMapModel();
        this.odabraniLongitude = longitude;
        this.odabraniLatitude = latitude;
        
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        MeteoPodaci meteoPodaci = dajMeteoPodatkeAerodroma(icao, korime, lozinka);
        if(meteoPodaci!=null)
            postaviMeteoPodatke(meteoPodaci);

        return "";
    }

    public String getOdabraniLongitude()
    {
        if(odabraniLongitude!=null)
            return odabraniLongitude;
        
        return "";
    }

    public void setOdabraniLongitude(String odabraniLongitude)
    {
        this.odabraniLongitude = odabraniLongitude;
    }

    public String getOdabraniLatitude()
    {
        if(odabraniLatitude!=null)
            return odabraniLatitude;
        
        return "";
    }

    public void setOdabraniLatitude(String odabraniLatitude)
    {
        this.odabraniLatitude = odabraniLatitude;
    }

    public void nextPage()
    {
        if (trenutnaStranica < ukBrojStr)
        {
            trenutnaStranica++;
            preuzmi();
        }
    }

    public void prevPage()
    {
        if (trenutnaStranica > 1)
        {
            trenutnaStranica--;
            preuzmi();
        }
    }

    private List<Aerodrom> dajAerodromeIzJson(String jsonAerodromi)
    {
        List<Aerodrom> returnList = new ArrayList<>();
        try
        {
            PomocJson jPomoc = new PomocJson(jsonAerodromi);
            List<String> listaAerodromaString = jPomoc.dajCistiTekst("odgovor");

            for (String aerodromJson : listaAerodromaString)
            {
                Aerodrom a = new Aerodrom(aerodromJson);
                if (a != null)
                {
                    returnList.add(a);
                }
            }
        } catch (Exception ex)
        {
            System.out.println("GRESKICAAA " + ex.getMessage());
        }

        return returnList;
    }

    private String kreirajDodatnePodatke(String korimeJson)
    {
        Gson gson = new Gson();
        JsonObject korimeJsonObj = gson.fromJson(korimeJson, JsonObject.class);
        String korime = korimeJsonObj.get("korime").getAsString();
        JsonObject jO = new JsonObject();
        jO.addProperty("dodao", korime);
        jO.addProperty("trenutnaStranica", this.trenutnaStranica);

        return gson.toJson(jO);
    }

    private String dodajIcaoArgument(String korimeLozinkaJson)
    {
        Gson gson = new Gson();
        JsonObject jO = gson.fromJson(korimeLozinkaJson, JsonObject.class);
        jO.addProperty("icao", this.aerodromIcaoZaDodavanje);
        
        return gson.toJson(jO);
    }

    private MeteoPodaci dajMeteoPodatkeAerodroma(java.lang.String icao, java.lang.String korime, java.lang.String lozinka)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dajMeteoPodatkeAerodroma(icao, korime, lozinka);
    }

    private void postaviMeteoPodatke(MeteoPodaci meteoPodaci)
    {
        this.temperatura = meteoPodaci.getTemperatureValue()+" "+meteoPodaci.getTemperatureUnit();
        this.vlaga = meteoPodaci.getHumidityValue()+" "+meteoPodaci.getHumidityUnit();
        this.tlak = meteoPodaci.getPressureValue()+" "+meteoPodaci.getPressureUnit();
        this.ikonaVrijeme = meteoPodaci.getWeatherIcon()+".png";
    }
    
    private void provjeriStranice()
    {
        if(this.trenutnaStranica>ukBrojStr)
                this.trenutnaStranica=1;
    }
    
    private double dajDoubleLokacije(String lok)
    {
        try{
            return Double.parseDouble(lok);
        }catch(Exception ex)
        {
            return 0;
        }
    }
    
    public void onMarkerSelect(OverlaySelectEvent event)
    {
        this.odabraniMarker = (Marker) event.getOverlay();
    }

    public Marker getOdabraniMarker()
    {
        return odabraniMarker;
    }

    public void setOdabraniMarker(Marker odabraniMarker)
    {
        this.odabraniMarker = odabraniMarker;
    }

    private String formatirajPodatkeOverlay()
    {
        String returnMe = String.format("Airport: %s\r\n Temperatura: %s\r\nTlak: %s\r\nVlaga: %s\r\nLat: %s\r\nLong: %s",
                this.odabraniIcao, this.temperatura, this.tlak, this.vlaga, this.odabraniLatitude, this.odabraniLongitude);
        
        return returnMe;
    }

    public String getIkonaVrijeme()
    {
        return ikonaVrijeme;
    }

    public void setIkonaVrijeme(String urlIkonaVrijeme)
    {
        this.ikonaVrijeme = urlIkonaVrijeme;
    }
    
    
}
