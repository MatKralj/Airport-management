package org.foi.nwtis.mkralj.DB.podaci;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.foi.nwtis.mkralj.DB.DBdata;
import org.foi.nwtis.mkralj.TimeStamp;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.rest.podaci.AvionLeti;


public class Avion extends AvionLeti implements DBdata
{

    private int id;
    private int ukBrojStranica;

    public int getUkBrojStranica()
    {
        return ukBrojStranica;
    }

    public void setUkBrojStranica(int ukBrojStranica)
    {
        this.ukBrojStranica = ukBrojStranica;
    }
    
    @Override
    public Avion pretvoriUObjekt(ResultSet rs)
    {
        Avion a = null;
        try
        {
            if(rs!=null)
                a = dajObjekt(rs);
        } 
        catch (SQLException | NumberFormatException ex)
        {
            System.out.println("Greska kod SQL upita ili pretvaranje Stringa u int. "+ex.getLocalizedMessage());
        }
        
        return a;
    }

    @Override
    public String dajSelectNaredbu(String tablica)
    {
        String returnString = String.format("SELECT * FROM %s", tablica);
        
        return returnString;
    }

    @Override
    public String dajSelectNaredbu(String tablica, String icao24)
    {
        String whereDio = String.format("WHERE ICAO24='%s'", icao24);
        String returnString = String.format("%s %s", dajSelectNaredbu(tablica), whereDio);
        
        return returnString;
    }

    private Avion dajObjekt(ResultSet rs) throws SQLException
    {
        Avion a = new Avion();
        a.setId(rs.getString("id"));
        a.setIcao24(rs.getString("ICAO24"));
        a.setFirstSeen(dajBrojIzStringa(rs.getString("FIRSTSEEN")));
        a.setLastSeen(dajBrojIzStringa(rs.getString("LASTSEEN")));
        a.setEstDepartureAirport(rs.getString("ESTDEPARTUREAIRPORT"));
        a.setEstArrivalAirport(rs.getString("ESTARRIVALAIRPORT"));
        a.setCallsign(rs.getString("CALLSIGN"));
        a.setEstDepartureAirportHorizDistance(dajBrojIzStringa(rs.getString("ESTDEPARTUREAIRPORTHORIZDISTANCE")));
        a.setEstDepartureAirportVertDistance(dajBrojIzStringa(rs.getString("ESTDEPARTUREAIRPORTVERTDISTANCE")));
        a.setEstArrivalAirportVertDistance(dajBrojIzStringa(rs.getString("ESTARRIVALAIRPORTVERTDISTANCE")));
        a.setEstArrivalAirportHorizDistance(dajBrojIzStringa(rs.getString("ESTARRIVALAIRPORTHORIZDISTANCE")));
        a.setDepartureAirportCandidatesCount(dajBrojIzStringa(rs.getString("DEPARTUREAIRPORTCANDIDATESCOUNT")));
        a.setArrivalAirportCandidatesCount(dajBrojIzStringa(rs.getString("ARRIVALAIRPORTCANDIDATESCOUNT")));
        
        return a;
    }
    
    private int dajBrojIzStringa(String broj)
    {
        try{
            return Integer.parseInt(broj);
        }
        catch(NumberFormatException ex)
        {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        AvionLeti aL = (AvionLeti) obj;
        if(aL.getIcao24().equals(this.getIcao24()))
            return true;
        
        return false;
    }

    public Avion copyFrom(AvionLeti aL)
    {
        this.setIcao24(aL.getIcao24());
        this.setFirstSeen(aL.getFirstSeen());
        this.setLastSeen(aL.getLastSeen());
        this.setEstDepartureAirport(aL.getEstDepartureAirport());
        this.setEstArrivalAirport(aL.getEstArrivalAirport());
        this.setCallsign(aL.getCallsign());
        this.setEstDepartureAirportHorizDistance(aL.getEstDepartureAirportHorizDistance());
        this.setEstDepartureAirportVertDistance(aL.getEstDepartureAirportVertDistance());
        this.setEstArrivalAirportVertDistance(aL.getEstArrivalAirportVertDistance());
        this.setEstArrivalAirportHorizDistance(aL.getEstArrivalAirportHorizDistance());
        this.setDepartureAirportCandidatesCount(aL.getDepartureAirportCandidatesCount());
        this.setArrivalAirportCandidatesCount(aL.getArrivalAirportCandidatesCount());
        
        return this;
    }

    @Override
    public String dajUpdateVremena(String tablica, String id)
    {
        String returnString = String.format("UPDATE %s SET STORED=DEFAULT WHERE ICAO24='%s'", tablica, id);
        
        return returnString;
    }

    @Override
    public String dajInsertNaredbu(String tablica)
    {
        String currentTime = new TimeStamp().dajTrenutnoVrijemeBaza();
        String returnString = String.format("INSERT INTO %s VALUES (DEFAULT,"
                + "'%s', %s, '%s', %s, '%s', '%s', %s, %s, %s, %s, %s, %s, '%s')", 
                tablica, this.getIcao24(), this.getFirstSeen(), this.getEstDepartureAirport(), this.getLastSeen(), 
                this.getEstArrivalAirport(), this.getCallsign(), this.getEstDepartureAirportHorizDistance(),
                this.getEstDepartureAirportVertDistance(), this.getEstArrivalAirportHorizDistance(),
                this.getEstArrivalAirportVertDistance(), this.getDepartureAirportCandidatesCount(),
                this.getArrivalAirportCandidatesCount(), currentTime);
        
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
        String returnString = String.format("DELETE FROM %s WHERE ICAO24='%s'",
                tablica, this.getIcao24());
        
        return returnString;
    }

    @Override
    public String dajUpdateNaredbu(String tablica)
    {
        String returnString = String.format("UPDATE %s SET FIRSTSEEN=%s, ESTDEPARTUREAIRPORT='%s',"
                + "LASTSEEN=%s, ESTARRIVALAIRPORT='%s', CALLSIGN='%s', STORED=DEFAULT WHERE ICAO24='%s')", 
                tablica, this.getFirstSeen(), this.getEstDepartureAirport(), this.getLastSeen(), 
                this.getEstArrivalAirport(), this.getCallsign(), this.getIcao24());
        
        return returnString;
    }

    private void setId(String id)
    {
        try{
            this.id = Integer.parseInt(id);
        }catch(Exception ex)
        {
            this.id = 0;
        }
    }

    public int getId()
    {
        return id;
    }

}
