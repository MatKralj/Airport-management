package org.foi.nwtis.mkralj.web.podaci;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class KorisnikBase
{
    
    private String ime;
    private String prezime;
    private String korime;
    private String email;
    private boolean active;
    private int ukBrojStranica;

    public KorisnikBase()
    {
        
    }

    public void setUkBrojStranica(int ukBrojStranica)
    {
        this.ukBrojStranica = ukBrojStranica;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    public KorisnikBase(String korisnikJson)
    {
        Gson gson = new Gson();
        JsonObject jO = gson.fromJson(korisnikJson, JsonObject.class);
        this.ime = dajCistiTekst(jO, "ime");
        this.prezime = dajCistiTekst(jO, "prezime");
        this.korime = dajCistiTekst(jO, "korime");
        this.email = dajCistiTekst(jO, "email");
        this.active = dajCistiTekstBool(jO);
        this.ukBrojStranica = dajCistiBroj(jO, "ukBrojStranica");
    }
    
    public String getIme()
    {
        return ime;
    }

    public void setIme(String name)
    {
        this.ime = name;
    }

    public String getPrezime()
    {
        return prezime;
    }

    public void setPrezime(String surname)
    {
        this.prezime = surname;
    }

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    private String dajCistiTekst(JsonObject jO, String atribut)
    {
        if(jO==null)
            return "";

        return jO.get(atribut).getAsString();
    }

    private boolean dajCistiTekstBool(JsonObject jO)
    {
        String cistiTekst = dajCistiTekst(jO, "active");
        if(cistiTekst.equalsIgnoreCase("TRUE") || cistiTekst.equals("1"))
            return true;
        
        return false;
    }

    public int getUkBrojStr()
    {
        return this.ukBrojStranica;
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
}
