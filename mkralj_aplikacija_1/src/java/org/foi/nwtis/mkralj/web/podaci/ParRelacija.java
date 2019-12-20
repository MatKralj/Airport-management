package org.foi.nwtis.mkralj.web.podaci;

import org.foi.nwtis.mkralj.DB.podaci.Avion;

public class ParRelacija
{

    private final String odrediste;

    public Avion getLet()
    {
        return let;
    }
    private final String polaziste;
    private final Avion let;

    public String getOdrediste()
    {
        return odrediste;
    }

    public String getPolaziste()
    {
        return polaziste;
    }
    
    public ParRelacija(Avion let)
    {
        this.polaziste = let.getEstDepartureAirport();
        this.odrediste = let.getEstArrivalAirport();
        this.let = let;
    }

    @Override
    public boolean equals(Object obj)
    {
        ParRelacija usporedujem = (ParRelacija)obj;
        String polazisniUsporedujem = usporedujem.getPolaziste();
        String odredisniUsporedujem = usporedujem.getOdrediste();
        
        if(this.polaziste.equals(polazisniUsporedujem) && this.odrediste.equals(odredisniUsporedujem))
            return true;
        else if(this.polaziste.equals(odredisniUsporedujem) && this.odrediste.equals(polazisniUsporedujem))
            return true;
        else if(odredisniUsporedujem.equals(this.polaziste))
            return true;
        return false;
    }

    @Override
    public String toString()
    {
        return "ParRelacija{" + "odrediste=" + odrediste + ", polaziste=" + polaziste + ", let=" + let + '}';
    }
    
    

}
