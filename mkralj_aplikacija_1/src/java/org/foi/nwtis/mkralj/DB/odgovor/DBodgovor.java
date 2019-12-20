package org.foi.nwtis.mkralj.DB.odgovor;

import org.foi.nwtis.mkralj.poruke.odgovori.OdgZaRest;
import org.foi.nwtis.mkralj.DB.DBdata;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac.Greske;


public class DBodgovor<T extends DBdata> implements OdgZaRest
{
    private T result = null;
    private String error="";
    private Greske gr;
    private Boolean jednostavnaRadnja = null;
    
    public DBodgovor()
    {
        error = "";
    }

    public void setResult(T result)
    {
        this.result = result;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public void setError(ErrorLokalizac.Greske greske, String localizedMessage)
    {
        gr = greske;
        this.error = localizedMessage;
    }

    public void setJednostavnaRadnjaUspjela(Boolean b)
    {
        jednostavnaRadnja = b;
    }

    @Override
    public DBdata dajRezultat()
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
        return this.jednostavnaRadnja!=null;
    }

    @Override
    public boolean jednostavnaRadnjaUspjela()
    {
        return this.jednostavnaRadnja;
    }
    
    @Override
    public boolean rezultatOk()
    {
        if(gr==null && result!=null)
            return true;
        else if(isJednostavnaRadnja() && jednostavnaRadnjaUspjela())
            return true;
        
        return false;
    }

    @Override
    public boolean rezultatNull()
    {
        if(this.gr==null && this.result==null)
            return true;
        
        return false;
    }
}
