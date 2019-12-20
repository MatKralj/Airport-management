package org.foi.nwtis.mkralj.poruke.greske;

public class ErrorLokalizac
{
    public enum Greske{
        GreskaAerodrom,
        GreskaLokacija,
        SQLex,
        PodaciUObj, 
        NeispravnaLokacija, 
        NeispravanAerodrom,
        NullObj,
        VezaNull
    }
    private Greske gr;
    private String dodatnaPoruka="";

    public ErrorLokalizac(Greske gr, String dodatnaPoruka)
    {
        this.gr = gr;
        this.dodatnaPoruka= dodatnaPoruka;
    }
    
    public ErrorLokalizac(Greske gr)
    {
        this.gr = gr;
        this.dodatnaPoruka= "";
    }

    public String getMessage(String jezik)
    {
        if(jezik==null)
            return dajGreskuEN();
        
        switch (jezik){
            case "hr":
                return dajGreskuHR();
            case "en":
                return dajGreskuEN();
            case "de":
                return dajGreskuDE();
            default:
                return dajGreskuEN();
        }
       
    } 
    
    private String dajGreskuHR()
    {
        String returnMe = "";
        switch(this.gr)
        {
            case GreskaAerodrom:
                returnMe = "Greška kod traženja aerodroma. Provjerite ICAO.";
                break;
            case GreskaLokacija:
                returnMe = "Neuspješno preuzimanje GPS lokacije. Provjerite ispravnost naziva aerodroma.";
                break;
            case SQLex:
                returnMe = "Greška kod izvršavanja SQL upita.";
                break;
            case PodaciUObj:
                returnMe = "Ne mogu pretvoriti podatke iz baze u objekt.";
                break;
            case NeispravanAerodrom:
                returnMe = "Nije moguće spremiti aerodrom. Provjerite ispravnost aerodroma.";
                break;
            case NeispravnaLokacija:
                returnMe = "Nije moguće dohvatiti meteo podatke. Provjerite lokaciju.";
                break;
            case NullObj:
                returnMe = "Dohvaćen je null objekt - takav objekt ne postoji.";
                break;
            case VezaNull:
                returnMe = "Veza prema bazi podataka ne postoji.";
                break;
        }
        
        return returnMe+" "+dodatnaPoruka;
    }

    private String dajGreskuEN()
    {
        String returnMe = "";
        switch(this.gr)
        {
            case GreskaAerodrom:
                returnMe = "Error while searching the airport. Check ICAO.";
                break;
            case GreskaLokacija:
                returnMe = "Failed to download GPS location. Make sure the name of the airport is correct.";
                break;
            case SQLex:
                returnMe = "Error while executing SQL queries.";
                break;
            case PodaciUObj:
                returnMe = "Can not convert data from database to object.";
                break;
            case NeispravanAerodrom:
                returnMe = "Can not save the airport. Check the airworthiness of the airport.";
                break;
            case NeispravnaLokacija:
                returnMe = "Unable to retrieve weather data. Check the location.";
                break;
            case NullObj:
                returnMe = "Object was null - there is no such object.";
                break;
            case VezaNull:
                returnMe = "Database connection does not exist";
                break;
        }
        
        return returnMe+" "+dodatnaPoruka;
    }

    private String dajGreskuDE()
    {
        String returnMe = "";
        switch(this.gr)
        {
            case GreskaAerodrom:
                returnMe = "Fehler beim Durchsuchen des Flughafens. Überprüfen Sie die ICAO.";
                break;
            case GreskaLokacija:
                returnMe = "GPS-Standort konnte nicht heruntergeladen werden. "
                        + "Stellen Sie sicher, dass der Name des Flughafens korrekt ist.";
                break;
            case SQLex:
                returnMe = "Fehler beim Ausführen von SQL-Abfragen.";
                break;
            case PodaciUObj:
                returnMe = "Nicht in der Lage, die Daten aus der Datenbank in der Anlage zu konvertieren.";
                break;
            case NeispravanAerodrom:
                returnMe = "Kann den Flughafen nicht retten. Überprüfen Sie die Lufttüchtigkeit des Flughafens.";
                break;
            case NeispravnaLokacija:
                returnMe = "Wetterdaten können nicht abgerufen werden. Überprüfen Sie den Standort.";
                break;
            case NullObj:
                returnMe = "Objekt ist null- es gibt kein Objekt.";
                break;
            case VezaNull:
                returnMe = "Datenbankverbindung existiert nicht.";
                break;
        }
        
        return returnMe+" "+dodatnaPoruka;
    }
}
