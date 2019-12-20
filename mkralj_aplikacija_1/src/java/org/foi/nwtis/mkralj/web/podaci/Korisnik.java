package org.foi.nwtis.mkralj.web.podaci;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.foi.nwtis.mkralj.DB.DBdata;
import org.foi.nwtis.mkralj.DB.WherePart;

public class Korisnik implements DBdata
{

    private String ime;
    private String prezime;
    private String korime;
    private String email;
    private boolean active;
    private String lozinka;
    
    private int brojStrStranicenje = 1;

    public int getBrojStrStranicenje()
    {
        return brojStrStranicenje;
    }

    public void setBrojStrStranicenje(int brojStrStranicenje)
    {
        this.brojStrStranicenje = brojStrStranicenje;
    }

    public Korisnik(String name, String surname, String korime, String email, String password, boolean active)
    {
        this.ime = name;
        this.prezime = surname;
        this.korime = korime;
        this.email = email;
        this.lozinka = password;
        this.active = active;
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
    
    public Korisnik()
    {
        
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public void setLozinka(String password)
    {
        this.lozinka = password;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
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
        String whereString = String.format("WHERE korime='%s'", id);
        String returnString = String.format("%s %s",dajSelectNaredbu(tablica), whereString);
        
        return returnString;
    }

    @Override
    public String dajUpdateVremena(String tablica, String id)
    {
        String returnString = String.format("UPDATE %s SET STORED=DEFAULT WHERE korime='%s'", tablica, id);
        
        return returnString;
    }

    @Override
    public String dajInsertNaredbu(String tablica)
    {
        String returnString = String.format("INSERT INTO %s VALUES ('%s', '%s', '%s', '%s', '%s', %s, DEFAULT)", 
                tablica, this.korime, this.lozinka, this.ime, this.prezime, this.email, this.active);
        
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
        String returnString = String.format("DELETE FROM %s WHERE korime='%s'", tablica, this.korime);
        
        return returnString;
    }

    @Override
    public String dajUpdateNaredbu(String tablica)
    {
        String returnString = String.format("UPDATE %s SET name='%s', surname='%s', "
                + "password='%s', active=%s, STORED=DEFAULT WHERE korime='%s'", 
                tablica, this.ime, this.prezime, this.lozinka, this.active, this.korime);
        
        return returnString;
    }

    @Override
    public Korisnik pretvoriUObjekt(ResultSet rs)
    {
        Korisnik k = null;
        try
        {
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String korisnickoIme = rs.getString("korime");
            String eAdresa = rs.getString("email");
            String password = rs.getString("password");
            boolean aktivan = getBoolValue(rs.getString("active"));
            
            k = new Korisnik(name, surname, korisnickoIme, eAdresa, password, aktivan);

        } catch (SQLException ex){
            System.out.println("Neispravno stvaranje objekta iz podataka baze. "+ex.getMessage());
        }

        return k;
    }

    private boolean getBoolValue(String string)
    {
        if(string.equals("1"))
            return true;
        
        return false;
    }

    @Override
    public String toString()
    {
        return "Korisnik{" + "name=" + ime + ", surname=" + prezime + ", korime=" + korime + ", email=" + email + ", active=" + active + '}';
    }
    
    

}
