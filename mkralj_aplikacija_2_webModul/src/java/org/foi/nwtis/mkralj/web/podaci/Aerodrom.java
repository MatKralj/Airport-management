package org.foi.nwtis.mkralj.web.podaci;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class Aerodrom
{
    private String icao;
    private String naziv;
    private String drzava;
    private String longitude;
    private String latitude;
    
    private int ukBrojStranica;

    public int getUkBrojStranica()
    {
        return ukBrojStranica;
    }

    public void setUkBrojStranica(int ukBrojStranica)
    {
        this.ukBrojStranica = ukBrojStranica;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }
    private String korime;

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    @Override
    public boolean equals(Object obj)
    {
        try{
            Aerodrom objekt = (Aerodrom)obj;
            if(objekt.getIcao().equals(this.getIcao()))
                return true;
        }catch(Exception ex)
        {
            return false;
        }
        
        return false;
    }

    public Aerodrom() {
    }

    public Aerodrom(String jsonAerodrom) {
        Gson gson = new Gson();
        JsonObject jO = gson.fromJson(jsonAerodrom, JsonObject.class);
        this.drzava = dajCistiTekst(jO, "drzava");
        this.icao = dajCistiTekst(jO, "icao");
        this.korime = dajCistiTekst(jO, "korime");
        this.latitude = dajCistiTekstLokacija(jO, "latitude");
        this.longitude = dajCistiTekstLokacija(jO, "longitude");
        this.naziv = dajCistiTekst(jO, "naziv");
        this.ukBrojStranica = dajCistiBroj(jO, "ukBrojStranica");
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }
    
    private String dajCistiTekst(JsonObject jO, String atribut)
    {
        if(jO==null)
            return "";

        return jO.get(atribut).getAsString();
    }
    
    private int dajCistiBroj(JsonObject jO, String atribut)
    {
        try{
            String cistiTekst = dajCistiTekst(jO, atribut);
            return Integer.parseInt(cistiTekst);
        }catch(Exception ex)
        {
            return 1;
        }
    }

    private String dajCistiTekstLokacija(JsonObject jO, String key)
    {
        try{
            Gson gson = new Gson();
            JsonElement jElemLokacija = jO.get("lokacija");
            String jsonLokacija = gson.toJson(jElemLokacija);
            JsonObject jObjLokacija = gson.fromJson(jsonLokacija, JsonObject.class);
            return jObjLokacija.get(key).getAsString().trim();
        }catch(Exception ex)
        {
            return "";
        }
    }

}
