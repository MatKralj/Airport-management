package org.foi.nwtis.mkralj.pomocneKlase;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SerijalizacijaObjekta<T extends Serializable>
{
    private String imeDatoteke="";
    private ReentrantLock lock = null;
    
    /**
     * Konstruktor objekta serijalizacije objekta. 
     * Stvara novi generalizirani objekt koji može vršiti serijalizaciju ili deserijalizaciju svih objekata koji naslijeđuju sučelje Serializable
     * @param imeDatoteke Putanja do datoeke nad kojom se vrši serijalizacija ili deserijalizacija
     */
    public SerijalizacijaObjekta(String imeDatoteke)
    {
        if(imeDatoteke!=null)
            this.imeDatoteke = imeDatoteke;
        
        lock = new ReentrantLock();
    }
    /**
     * Thread safe metoda koja serijalizira objekt. Nakon serijalizacije jednog objekta, 
     * stream se zatvara, pozivanjem ponovno ove metode, stream se ponovno kreira.
     * @param objektZaSerijalizaciju Objekt koji se serijalizira
     * @throws IOException Ukoliko datoteka ne postoji ili se u nju ne može zapisivati
     */
    public void serijalizirajObjekt(Serializable objektZaSerijalizaciju) throws IOException
    {
        lock.lock();
        try
        {
            FileOutputStream out = new FileOutputStream(imeDatoteke);
            ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(objektZaSerijalizaciju);
            s.close();
        } finally
        {
            lock.unlock();
        }
    }
    /**
     * Thread safe metoda koja serijalizira listu objekata. Serijalizira se objekt po objekt, 
     * metoda je optimirizanija za opis više objekata od metode serijalizirajObjekt
     * @param objektiZaSerijalizaciju Lista objekata koja se serijalizira
     * @throws IOException Ukoliko datoteka ne postoji ili se u nju ne može zapisivati
     */
    public void serijalizirajObjekte(List<Serializable> objektiZaSerijalizaciju) throws IOException
    {
        lock.lock();
        try
        {
            FileOutputStream out = new FileOutputStream(imeDatoteke);
            ObjectOutputStream s = new ObjectOutputStream(out);
            for(Serializable obj : objektiZaSerijalizaciju)
            {
                s.writeObject(obj);
            }
            s.close();
        } finally
        {
            lock.unlock();
        }
    }
   
    /**
     * Thread safe metoda koja deserijalizira objekt iz liste.
     * @return Deserijalizirani generalizirani objekt koji implementira sučelje Serializable
     * @throws IOException Ukoliko datoteka ne postoji ili se u nju ne može zapisivati
     * @throws ClassNotFoundException Ukoliko nije moguće prepoznati generaliziranu klasu
     */
    public T deserijalizirajObjekt() throws IOException, ClassNotFoundException
    {
        lock.lock();
        try
        {
            FileInputStream in = new FileInputStream(imeDatoteke);
            ObjectInputStream s = new ObjectInputStream(in);
            T returnObj = (T) s.readObject();
            in.close();
            s.close();
            return returnObj;
        } finally
        {
            lock.unlock();
        }
    }
    
    /**
     * Thread safe metoda koja deserijalizira objekt iz liste.
     * @return Deserijalizirana lista generaliziranih objekata koji implementira sučelje Serializable
     * @throws IOException Ukoliko datoteka ne postoji ili se u nju ne može zapisivati
     * @throws ClassNotFoundException Ukoliko nije moguće prepoznati generaliziranu klasu
     */
    public ArrayList<T> deserijalizirajObjekte() throws IOException, ClassNotFoundException
    {
        lock.lock();
        try{
            ArrayList<T> returnList = new ArrayList<>();
            FileInputStream in = new FileInputStream(imeDatoteke);
            ObjectInputStream s = new ObjectInputStream(in);
            
            while(true){
                try{
                    T procitaniObjekt = (T) s.readObject();
                    returnList.add(procitaniObjekt);
                }
                catch(EOFException ex){
                    in.close();
                    s.close();
                    break;
                }
            }
            
            return returnList;
        } finally{
            lock.unlock();
        }
    }
    /**
     * Thread safe metoda. Provjerava postoji li datoteka
     * @return True ukoliko postoji, inače false
     */
    public boolean datotekaPostoji()
    {
        try{
            lock.lock();
            FileInputStream in = new FileInputStream(imeDatoteke);
            in.close();
            return true;
        }
        catch(IOException ex){
            return false;
        }
        finally{
            lock.unlock();
        }
    }
}