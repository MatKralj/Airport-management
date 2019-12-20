package org.foi.nwtis.mkralj.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovor;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.konfiguracije.bp.BP_Konfiguracija;

public class VezaDB {

    private BP_Konfiguracija konfig;
    private static VezaDB instanca = null;
    private ReentrantLock lock;
    private Connection connection;

    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Singleton klasa
     * @param bpKonfig Konfiguracija u kojoj su podaci za povezivanje na bazu
     * @throws IllegalAccessException Ukoliko se pokuša kreirati još jedna instance
     */
    public VezaDB(BP_Konfiguracija bpKonfig) throws IllegalAccessException
    {
        if(instanca == null)
        {
            instanca = this;
            instanca.konfig = bpKonfig;
            instanca.lock = new ReentrantLock();
        }
        else
            throw new IllegalAccessException("Instanca vec postoji");
    }

    public static VezaDB getInstanca()
    {
        return instanca;
    }
    
    /**
     * Zatvara Connection s bazom
     */
    public void zatvoriVezu()
    {
        try
        {
            if(connection!=null)
                connection.close();
        } catch (SQLException ex)
        {
            Logger.getLogger(VezaDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Kreira konekciju s bazom - Connection.Na temelju podataka iz konfiguracije
     * @return Konekcija
     */
    public Connection kreirajVezu() throws NullPointerException
    {
        if(konfig == null)
            throw new NullPointerException("Konfiguracija je null");
        
        Connection con = null;
        try{
            String server = konfig.getServerDatabase();
            String korisnik = konfig.getUserUsername(); 
            String lozinka = konfig.getUserPassword();
            String baza = konfig.getUserDatabase();
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(server+baza, korisnik, lozinka);
            this.connection = con;
        }catch (SQLException ex)
        {
            System.out.println("Greska kod kreiranja SQL veze : "+ex.getMessage());
        } catch (ClassNotFoundException ex)
        {
            Logger.getLogger(VezaDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return con;
    }
    
    /**
     * Iz tablice daje listu svih objekata i eventualno grešku koja se je dogodila pri radu s bazom
     * @param <T> Klasa koja implementira sučelje DBdata
     * @param tablica Tablica nad kojom se vrši radnja
     * @param data Sučelje DBdata
     * @return Objekt u kojem se nalazi lista rezultata tipa T i eventualna greška koja se je dogodila
     */
    public <T extends DBdata> DBodgovorLista selectObjekte(String tablica, DBdata data)
    {
        DBodgovorLista lista = null;
        Upit u = new Upit();
        if(this.connection==null)
            return null;
        
        lock.lock();
        try{
            lista = u.izvrsiSelectSvega(connection, data, tablica);
        }catch(Exception ex)
        {
            return null;
        }
        finally{
            lock.unlock();
        }
        
        return lista;
    }
    
    /**
     * Iz tablice daje objekt i eventualno grešku koja se je dogodila pri radu s bazom
     * @param <T> Klasa koja implementira sučelje DBdata
     * @param tablica Tablica nad kojom se vrši radnja
     * @param data Sučelje DBdata - može biti prazi objekt - npr new Aerodrom()
     * @param id ID prema kojem se radi select Where operacija
     * @return Objekt u kojem se nalazi rezultat tipa T i eventualna greška koja se je dogodila
     */
    public <T extends DBdata> DBodgovor selectObjekt(String tablica, DBdata data, String id)
    {
        DBodgovor obj = null;
        Upit u = new Upit();
        
        if(this.connection==null)
            return null;
        
        lock.lock();
        try{
            obj = u.izvrsiSelectWhere(connection, data, tablica, id);
        }finally{
            lock.unlock();
        }
        
        return obj;
    }
    
    /**
     * Iz tablice daje objekt i eventualno grešku koja se je dogodila pri radu s bazom
     * @param <T> Klasa koja implementira sučelje DBdata
     * @param tablica Tablica nad kojom se vrši radnja
     * @param data Sučelje DBdata - može biti prazni objekt - npr new Aerodrom()
     * @param where Objekt tipa WherePart koji sadrži string koji dolazi nakon WHERE dijela upita. Bez ključne riječi 'WHERE'
     * @return Objekt u kojem se nalazi rezultat tipa T i eventualna greška koja se je dogodila
     */
    public <T extends DBdata> DBodgovorLista selectObjekt(String tablica, DBdata data, WherePart where)
    {
        if(this.connection==null)
            return null;
        
        DBodgovorLista obj = null;
        Upit u = new Upit();
        
        lock.lock();
        try{
            obj = u.izvrsiSelectWhere(connection, data, tablica, where);
        }finally{
            lock.unlock();
        }
        
        return obj;
    }
    
    /**
     * Iz tablice daje max vrijednost unesenog stupca i eventualno grešku koja se je dogodila pri radu s bazom
     * @param tablica Tablica u kojoj se vrijednost traži
     * @param column Stupac za koji se traži max vrijednost
     * @param where Where dio uvjeta
     * @return Objekt u kojem se nalazi rezultat tipa T i eventualna greška koja se je dogodila
     */
    public String selectMaxVal(String tablica, String column, WherePart where)
    {
        if(this.connection==null)
            return null;
        
        String obj = null;
        Upit u = new Upit();
        
        lock.lock();
        try{
            obj = u.izvrsiSelectMax(connection, tablica, column, where);
        }finally{
            lock.unlock();
        }
        
        return obj;
    }
    
    /**
     * Iz tablice daje max vrijednost unesenog stupca i eventualno grešku koja se je dogodila pri radu s bazom
     * @param tablica Tablica u kojoj se vrijednost traži
     * @param column Stupac za koji se traži max vrijednost
     * @return Objekt u kojem se nalazi rezultat tipa T i eventualna greška koja se je dogodila
     */
    public String selectMaxVal(String tablica, String column)
    {
        if(this.connection==null)
            return null;
        
        String obj = null;
        Upit u = new Upit();
        
        lock.lock();
        try{
            obj = u.izvrsiSelectMax(connection, tablica, column);
        }finally{
            lock.unlock();
        }
        
        return obj;
    }

    /**
     * Zapisuje objekt u tablicu
     * @param tablica Tablica u koju se upisuje
     * @param data Objekt koji se upisuje u tablicu
     * @return Rezultat rada s bazom i eventualna greška koja se je dogodila pri radu
     */
    public DBodgovor zapisiUTablicu(String tablica, DBdata data)
    {
        if(this.connection==null)
            return null;
        
        Upit u = new Upit();
        DBodgovor rezultat = null;
        
        lock.lock();
        try{
            rezultat = u.izvrsiUpis(connection, data, tablica);
            return rezultat;
        }finally{
            lock.unlock();
        }
    }
    
    /**
     * Zapisuje u tablicu, odnosno ažurira u tablici vrijeme kada je podatak spremljen
     * @param tablica Tablica nad kojom se vrši naredba
     * @param data Objekt sučelja DBdata, može se dati prazni objekt - npr. new Aerodrom()
     * @param id ID s kojim se retkom radi ažuriranje
     * @return Rezultat rada s bazom i eventualna greška koja se je dogodila pri radu
     */
    public DBodgovor zapisiUTablicu(String tablica, DBdata data, String id)
    {
        if(this.connection==null)
            return null;
        
        Upit u = new Upit();
        DBodgovor rezultat = null;
        
        lock.lock();
        try{
            rezultat = u.izvrsiUpdateVremena(connection, data, tablica, id);
            return rezultat;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Briše iz baze redak koji odgovara atributu objekta klase DBdata
     * @param tablica Tablica nad kojom se vrši brisanje
     * @param data Objekt iz kojeg se generira delete upit, tj. u ovom objektu su pohranjeni podaci koji služe za brisanje retka
     * @return Rezultat rada s bazom i eventualna greška koja se je dogodila pri radu
     */
    public DBodgovor deleteObjekt(String tablica, DBdata data)
    {
        if(this.connection==null)
            return null;
        
        Upit u = new Upit();
        DBodgovor rezultat = null;
        
        lock.lock();
        try{
            rezultat = u.izvrsiDelete(connection, data, tablica);
            return rezultat;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Ažurira redak koji odgovara podacima atributa objekta klase DBdata
     * @param tablica Tablica u kojoj se podaci ažuriraju
     * @param data Objekt sučelja čiji podaci odgovaraju podacima tablice. Ovi podaci će se zapisati umjesto odgovarajućeg retka tablice
     * @return Rezultat rada s bazom i eventualno greška koja se je pojavila
     */
    public DBodgovor updateObjekt(String tablica, DBdata data)
    {
        if(this.connection==null)
            return null;
        
        Upit u = new Upit();
        DBodgovor rezultat = null;
        
        lock.lock();
        try{
            rezultat = u.izvrsiUpdate(connection, data, tablica);
            return rezultat;
        }finally{
            lock.unlock();
        }
    }

    /**
     * Radi select count nekog stupca s where dijelom
     * @param tablica Tablica u kojoj će se zbrajati broj redaka
     * @param toCount Stupac koji se gleda u vrhu zbrajanja (Najčešće ID tablice)
     * @param where Where dio upita
     * @return Broj redaka koji zadovoljavaju where
     */
    public int selectCount(String tablica, String toCount, WherePart where)
    {
        Upit u = new Upit();
        if(this.connection==null)
            return 0;
        
        lock.lock();
        try{
            int brojRedaka = u.izvrsiCount(connection, tablica, where, toCount);
            return brojRedaka;
        }finally{
            lock.unlock();
        }
    }
}
