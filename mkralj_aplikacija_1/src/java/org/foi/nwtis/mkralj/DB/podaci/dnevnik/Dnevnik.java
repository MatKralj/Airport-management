package org.foi.nwtis.mkralj.DB.podaci.dnevnik;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.foi.nwtis.mkralj.DB.DBdata;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovor;


public class Dnevnik implements DBdata
{
    
    private String id;
    private String stored;
    private String opis;
    private String dioAplikacije;
    private String korime;
    private String radnja;

    private Dnevnik(String id, String stored, String opis, String dioAplikacije, String korime, String radnja)
    {
        this.id = id;
        this.stored = stored;
        this.opis = opis;
        this.dioAplikacije = dioAplikacije;
        this.korime = korime;
        this.radnja = radnja;
    }
    
    public Dnevnik(String korime, DioAplikacije dioAplikacije)
    {
        this.dioAplikacije = dioAplikacije.dajTekstDijela();
        this.korime = korime;
    }
    
    public Dnevnik()
    {
        
    }
    
    public boolean zapisi()
    {
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null)
            return false;
        
        DBodgovor dbOdg = veza.zapisiUTablicu("dnevnik", this);
        
        return dbOdg.rezultatOk();
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
        String whereString = String.format("WHERE id='%s'", id);
        String returnString = String.format("%s %s",dajSelectNaredbu(tablica), whereString);
        
        return returnString;
    }

    @Override
    public String dajUpdateVremena(String tablica, String id)
    {
        String returnString = String.format("UPDATE %s SET STORED=DEFAULT WHERE id='%s'", tablica, id);
        
        return returnString;
    }

    @Override
    public String dajInsertNaredbu(String tablica)
    {
        String returnString = String.format("INSERT INTO %s VALUES (DEFAULT, '%s', DEFAULT, '%s', '%s', '%s')", 
                tablica, this.opis, this.korime, this.radnja, this.dioAplikacije);
        
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
        String returnString = String.format("DELETE FROM %s WHERE korime='%s'", tablica, this.id);
        
        return returnString;
    }

    @Override
    public String dajUpdateNaredbu(String tablica)
    {
        String returnString = String.format("UPDATE %s SET opis='%s', stored=DEFAULT, "
                + "korisnik='%s', radnja=%s, dio_aplikacije='%s' WHERE id='%s'", 
                tablica, this.opis, this.korime, this.radnja, this.dioAplikacije, this.id);
        
        return returnString;
    }

    @Override
    public Dnevnik pretvoriUObjekt(ResultSet rs)
    {
        Dnevnik d = null;
        try
        {
            String ident = rs.getString("id");
            String opisRadnje = rs.getString("opis");
            String storedOn = rs.getString("stored");
            String korisnik = rs.getString("korisnik");
            String radnjaApp = rs.getString("radnja");
            String dioApp = rs.getString("dio_aplikacije");
            
            d = new Dnevnik(ident, storedOn, opisRadnje, dioApp, korisnik, radnjaApp);

        } catch (SQLException ex){
            System.out.println("Neispravno stvaranje objekta iz podataka baze. "+ex.getMessage());
        }

        return d;
    }

    public String getOpis()
    {
        return opis;
    }

    public void setOpis(String opis)
    {
        this.opis = opis;
    }

    public String getDioAplikacije()
    {
        return dioAplikacije;
    }

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getRadnja()
    {
        return radnja;
    }

    public void setRadnja(String radnja)
    {
        this.radnja = radnja;
    }
    
        public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStored()
    {
        return stored;
    }

    public void setStored(String stored)
    {
        this.stored = stored;
    }

}
