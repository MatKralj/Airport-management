package org.foi.nwtis.mkralj.web.podaci;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mkralj.DB.DBdata;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.rest.podaci.Lokacija;


public class Aerodrom implements DBdata
{
    private String icao;
    private String naziv;
    private String drzava;
    private Lokacija lokacija;
    private String korime;

    private int ukBrojStranica;

    public int getUkBrojStranica()
    {
        return ukBrojStranica;
    }

    public void setUkBrojStranica(int ukBrojStranica)
    {
        this.ukBrojStranica = ukBrojStranica;
    }
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

    public Aerodrom(String icao, String naziv, String drzava, Lokacija lokacija, String korime) {
        this.icao = icao;
        this.naziv = naziv;
        this.drzava = drzava;
        this.lokacija = lokacija;
        this.korime = korime;
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

    public Lokacija getLokacija() {
        return lokacija;
    }

    public void setLokacija(Lokacija lokacija) {
        this.lokacija = lokacija;
    }

    @Override
    public Aerodrom pretvoriUObjekt(ResultSet rs)
    {
        Aerodrom a = null;
        try
        {   
            String id = rs.getString("ident");
            String imeAerodroma = rs.getString("name");
            String drz = rs.getString("iso_country");
            String coordinates = rs.getString("coordinates");
            Lokacija lok = getLokacijaFromCoordinates(coordinates);
            if(rs.getMetaData().getTableName(1).equalsIgnoreCase("MYAIRPORTS"))
            {
                String korisnik = rs.getString("korisnik");
            
                a = new Aerodrom(id, imeAerodroma, drz, lok, korisnik);
            }
            else
                a = new Aerodrom(id, imeAerodroma, drz, lok, "");
            
        } catch (SQLException ex)
        {
            Logger.getLogger(Aerodrom.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return a;
    }

    @Override
    public String dajSelectNaredbu(String tablica)
    {
        String returnString = String.format("SELECT * FROM %s", tablica);
        
        return returnString;
    }
    
    @Override
    public String dajSelectNaredbu(String tablica, String id)
    {
        String whereString = String.format("WHERE ident='%s'", id);
        String returnString = String.format("%s %s",dajSelectNaredbu(tablica), whereString);
        
        return returnString;
    }

    private Lokacija getLokacijaFromCoordinates(String coordinates)
    {
        coordinates = coordinates.trim();
        String longit = coordinates.substring(0, coordinates.indexOf(','));
        String lat = coordinates.substring(coordinates.indexOf(',')+1);
        
        return new Lokacija(lat.trim(), longit.trim());
    }

    @Override
    public String dajUpdateVremena(String tablica, String id)
    {
        String returnString = String.format("UPDATE %s SET STORED=DEFAULT WHERE IDENT='%s'", tablica, id);
        
        return returnString;
    }

    @Override
    public String dajInsertNaredbu(String tablica)
    {
        String coordinades = dajCoordinatesIzLok(this.lokacija);
        String returnString = String.format("INSERT INTO %s VALUES ('%s', '%s', '%s', '%s', DEFAULT, '%s')", 
                tablica, this.icao, this.naziv, this.drzava, coordinades, this.korime);
        
        return returnString;
    }

    @Override
    public String dajSelectNaredbu(String tablica, WherePart where)
    {
        String whereString = String.format("WHERE %s", where.getWhere());
        String returnString = String.format("%s %s",dajSelectNaredbu(tablica), whereString);
        
        return returnString;
    }

    @Override
    public String dajDeleteNaredbu(String tablica)
    {
        String returnString = String.format("DELETE FROM %s WHERE ident='%s'",
                tablica, this.getIcao());
        
        return returnString;
    }

    @Override
    public String dajUpdateNaredbu(String tablica)
    {
        String coordinates = dajCoordinatesIzLok(this.lokacija);
        String returnString = String.format("UPDATE %s SET NAME='%s', ISO_COUNTRY='%s', "
                + "COORDINATES='%s', STORED=DEFAULT, korisnik='%s' WHERE IDENT='%s'", 
                tablica, this.naziv, this.drzava, coordinates, this.korime, this.icao);
        
        return returnString;
    }
    
    private String dajCoordinatesIzLok(Lokacija lok)
    {
        String longitude = lok.getLongitude();
        String latitude = lok.getLatitude();
        
        return longitude+","+latitude;
    }
}
