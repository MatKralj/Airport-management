package org.foi.nwtis.mkralj.web.podaci;

import java.util.List;



public class Relacija
{

    private List<ParRelacija> moguciParovi = null;
    private List<ParRelacija> konacniParovi = null;
    private String IcaoOdrediste;

    public Relacija(String odrediste)
    {
        IcaoOdrediste = odrediste;
    }
    
    public List<ParRelacija> getListaParova()
    {
        return konacniParovi;
    }

    public String getIcaoOdrediste()
    {
        return IcaoOdrediste;
    }

}
