package org.foi.nwtis.mkralj.web.dretve.socket;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.DB.podaci.Avion;
import org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromStatus;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;

public class DretvaPripremanjaGrupe extends Thread
{

    private final String lozinka;
    private final String korime;

    public DretvaPripremanjaGrupe(String korisnik, String lozinka)
    {
        this.korime = korisnik;
        this.lozinka = lozinka;
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
    }

    @Override
    public void run()
    {
        List<Aerodrom> aerodromiBaze = dajAerodromeBaze();
        List<org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom> aerodromiGrupe = dajSveAerodromeGrupe(korime, lozinka);
        if (aerodromiBaze != null)
        {
            zapisiAerodromeGrupi(aerodromiBaze, aerodromiGrupe);
        }

        aerodromiBaze = null;
        aerodromiGrupe = null;

        List<Avion> avioniBaze = dajAvioneBaze();
        System.out.println("Krecem postavljati avione");
        postaviAvioneGrupe(this.korime, this.lozinka, avioniBaze);

        System.out.println("Zavrsavam s pripremom grupe");
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }

    private List<Aerodrom> dajAerodromeBaze()
    {
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return null;
        }

        DBodgovorLista odgovor = veza.selectObjekte("MYAIRPORTS", new Aerodrom());
        if (odgovor.rezultatOk())
        {
            return (List<Aerodrom>) odgovor.dajRezultat();
        }

        return null;
    }

    private void zapisiAerodromeGrupi(List<Aerodrom> aerodromiBaze, List<org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom> aerodromiGrupe)
    {
        System.out.println("Zapisujem aerodrome grupi");
        boolean postoji = false;
        for (Aerodrom a : aerodromiBaze)
        {
            postoji = false;
            for (org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom aG : aerodromiGrupe)
            {
                if (a.getIcao().equals(aG.getIcao()))
                {
                    postoji = true;
                    break;
                }
            }
            if (!postoji)
            {
                dodajAerodromGrupi(this.korime, this.lozinka, a);
            }
        }
    }

    private static Boolean dodajAerodromGrupi(String korime, String lozinka, Aerodrom novi)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom aerodrom = new org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom();
        aerodrom.setDrzava(novi.getDrzava());
        aerodrom.setIcao(novi.getIcao());

        org.foi.nwtis.mkralj.servisi.soap.klijent.Lokacija novaLok = new org.foi.nwtis.mkralj.servisi.soap.klijent.Lokacija();
        novaLok.setLatitude(novi.getLokacija().getLatitude());
        novaLok.setLongitude(novi.getLokacija().getLongitude());

        aerodrom.setLokacija(novaLok);
        aerodrom.setNaziv(novi.getNaziv());
        aerodrom.setStatus(AerodromStatus.BLOKIRAN);

        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajAerodromGrupi(korime, lozinka, aerodrom);
    }

    private List<Avion> dajAvioneBaze()
    {
        VezaDB veza = VezaDB.getInstanca();
        if (veza == null)
        {
            return null;
        }

        DBodgovorLista odgovor = veza.selectObjekte("AIRPLANES", new Avion());
        if (odgovor.rezultatOk())
        {
            return (List<Avion>) odgovor.dajRezultat();
        }

        return null;
    }

    private static boolean postaviAvioneGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, List<Avion> avioni)
    {
        List<org.foi.nwtis.mkralj.servisi.soap.klijent.Avion> avioniNovi = new ArrayList<>();
        for (Avion avion : avioni)
        {
            org.foi.nwtis.mkralj.servisi.soap.klijent.Avion avionNovi = new org.foi.nwtis.mkralj.servisi.soap.klijent.Avion();

            avionNovi.setCallsign(avion.getCallsign());
            avionNovi.setEstarrivalairport(avion.getEstArrivalAirport());
            avionNovi.setEstdepartureairport(avion.getEstDepartureAirport());
            avionNovi.setIcao24(avion.getIcao24());
            avionNovi.setId(avion.getId());

            avioniNovi.add(avionNovi);
        }

        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.postaviAvioneGrupe(korisnickoIme, korisnickaLozinka, avioniNovi);
    }

    private static java.util.List<org.foi.nwtis.mkralj.servisi.soap.klijent.Aerodrom> dajSveAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
    }

}
