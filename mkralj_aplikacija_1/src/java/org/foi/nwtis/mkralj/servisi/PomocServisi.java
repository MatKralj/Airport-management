package org.foi.nwtis.mkralj.servisi;

import java.util.List;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;



public class PomocServisi {

    Konfiguracija konfig;
    
    public PomocServisi(Konfiguracija konfig)
    {
        this.konfig = konfig;
    }

    /**
     * Koristi LIQKlijent servis kako bi na temelju naziva dobili lokaciju
     * @param naziv Naziv mjesta/aerodroma zua koji se provjerava lokacija
     * @return Lokacija traženog mjesta
     */
    public Lokacija dajLIQGeoLokaciju(String naziv)
    {
        try          
        {
            String token = konfig.dajPostavku("LocationIQ.token");
            if(token==null || token.isEmpty())
                return null;
            LIQKlijent liqk = new LIQKlijent(token);

            return liqk.getGeoLocation(naziv);
        }catch(Exception ex)
        {
            return null;
        }
    }
    
    /**
     * Na temelju onlitude i latitude vraća metoeo podatke tog mjesta
     * @param latitude Latitude
     * @param longitude Longitude
     * @return Meteo podaci mjesta
     */
    public MeteoPodaci dajOWMeteoPodatke(String latitude, String longitude)
    {
        try{
        
            String apikey = konfig.dajPostavku("OpenWeatherMap.apikey");
            if(apikey==null || apikey.isEmpty())
                return null;
            
            MeteoPodaci mp = null;
            OWMKlijent owmk = new OWMKlijent(apikey);
            mp = owmk.getRealTimeWeather(latitude, longitude);
            return mp;
        }catch(Exception ex)
        {
            System.out.println("Nije moguce dohvatiti meteo podatke. "+ex.getLocalizedMessage());
            return null;
        }
    }
    
    /**
     * Na temelju icao koda i intervala traži avione koji lete s traženog aerodroma
     * @param icao Icao kod aerodroma
     * @param odVremena Početak intervala
     * @param doVremena Kraj intervala
     * @return List aaviona za traženi aerodrom i interval
     */
    public List<AvionLeti> dajOSListuOdlazaka(String icao, int odVremena, int doVremena)
    {
        try{
            String username = konfig.dajPostavku("OpenSkyNetwork.korisnik");
            String password = konfig.dajPostavku("OpenSkyNetwork.lozinka");
            OSKlijent osk = new OSKlijent(username, password);

            return osk.getDepartures(icao, odVremena, doVremena);   
        }catch(Exception ex)
        {
            System.out.println("Exception kod dohvata departuresa - "+ex.getMessage());
            return null;
        }
        
    }
}
