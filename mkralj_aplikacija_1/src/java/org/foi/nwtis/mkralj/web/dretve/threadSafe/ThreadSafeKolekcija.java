package org.foi.nwtis.mkralj.web.dretve.threadSafe;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika;
import org.foi.nwtis.mkralj.poruke.greske.InfoKorisniku;
import org.foi.nwtis.mkralj.poruke.greske.PovratniInfoKorisniku;

public class ThreadSafeKolekcija<T>
{
    private ArrayList<T> objekti = null;
    private ReentrantLock lock;
    
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
     * Thread safe metoda koja dodaje element ukoliko takav ne postoji u listi
     * @param elem Element koji dodajemo, a koji mora biti različit od ostalih u listi
     * @return Informacija o grešci ili OK
     */
    public InfoKorisniku dodajRazlicitiElement(T elem)
    {
        if(elementPostoji(elem))
            return new PovratniInfoKorisniku(GreskaZaKorisnika.Error._03);
        dodajElement(elem);
        return new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_10);
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
    
}
