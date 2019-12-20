package org.foi.nwtis.mkralj.pomocneKlase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeKolekcija<T> implements Serializable
{
    private ArrayList<T> objekti = null;
    private transient ReentrantLock lock;
    
    /**
     * Konstruktor koji kreira novi objekt klase ThreadSafeKolekcija u koji se može spremati objekt tipa T
     */
    public ThreadSafeKolekcija()
    {
        objekti = new ArrayList<>();
        lock = new ReentrantLock(true);
    }
    /**
     * Thread safe metoda. Postavlja listu iz argumenta kao trenutna važeća lista ovog objekta. 
     * @param lista Lista koju želimo pohraniti u ovaj objekt
     */
    public void postaviListu(ArrayList<T> lista)
    {
        lock.lock();
        try
        {
            this.objekti = lista;
        } finally
        {
            lock.unlock();
        }
    }

    /**
     * Thread safe metoda koja dodaje element u listu
     * @param elem Element koji se dodaje u listu
     */
    public void dodajElement(T elem)
    {
        lock.lock();
        try
        {
            objekti.add(elem);
        } finally
        {
            lock.unlock();
        }
    }
    /**
     * Thread safe getter koji vraća cijelu listu objekata koja se nalazi u ovom objektu
     * @return Lista objekata koja se nalazi u ovom objekut
     */
    public ArrayList<T> getObjekti()
    {
        lock.lock();
        try
        {
            return this.objekti;
        } finally
        {
            lock.unlock();
        }
    }
    /**
     * Thread safe metoda koja provjerava postoji li već element u listi
     * @param elem Element kojeg se provjerava
     * @return True ukoliko element postoji, inače false.
     */
    public boolean elementPostoji(T elem)
    {
        lock.lock();
        try
        {
            for(T obj : this.objekti)
            {
                if(obj.equals(elem))
                    return true;
            }
            return false;
        } finally{
            lock.unlock();
        }
    }

    public void pripremiLock()
    {
        this.lock = new ReentrantLock();
    }
    
}
