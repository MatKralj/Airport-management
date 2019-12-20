package org.foi.nwtis.mkralj.DB.odgovor;

import org.foi.nwtis.mkralj.poruke.odgovori.OdgZaRest;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.mkralj.DB.DBdata;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac.Greske;


public class DBodgovorLista<T extends DBdata> implements OdgZaRest
{
    
    private List<T> result;
    private String error;
    private ErrorLokalizac.Greske gr;
    
    public DBodgovorLista()
    {
        result = new ArrayList<>();
        error="";
    }

    public void setResult(List<T> result)
    {
        this.result = result;
    }

    public void setError(String error)
    {
        this.error = error;
    }
    
    public void add(T e)
    {
        result.add(e);
    }

    public void setError(ErrorLokalizac.Greske greske, String localizedMessage)
    {
        this.gr = greske;
        this.error = localizedMessage;
    }

    public void setError(Greske greske)
    {
        this.gr = greske;
    }

    @Override
    public Object dajRezultat()
    {
        return this.result;
    }

    @Override
    public Greske dajTipGreske()
    {
        return this.gr;
    }

    @Override
    public String dajTekstGreske()
    {
        return this.error;
    }

    @Override
    public boolean isJednostavnaRadnja()
    {
        return false;
    }

    @Override
    public boolean jednostavnaRadnjaUspjela()
    {
        return false;
    }

    @Override
    public boolean rezultatOk()
    {
        if(gr==null && result!=null && !result.isEmpty())
            return true;
        
        return false;
    }

    @Override
    public boolean rezultatNull()
    {
        if(gr==null && (result==null || result.isEmpty()))
            return true;
        
        return false;
    }

    public void remove(T obj)
    {
        this.result.remove(obj);
    }

}
