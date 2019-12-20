package org.foi.nwtis.mkralj.web.dretve.socket.podaci;

import javax.servlet.ServletContext;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika.Error;
import org.foi.nwtis.mkralj.poruke.greske.InfoKorisniku;
import org.foi.nwtis.mkralj.poruke.greske.PovratniInfoKorisniku;
import org.foi.nwtis.mkralj.servisi.soap.klijent.StatusKorisnika;
import static org.foi.nwtis.mkralj.servisi.soap.klijent.StatusKorisnika.AKTIVAN;
import static org.foi.nwtis.mkralj.servisi.soap.klijent.StatusKorisnika.BLOKIRAN;
import static org.foi.nwtis.mkralj.servisi.soap.klijent.StatusKorisnika.DEREGISTRIRAN;
import static org.foi.nwtis.mkralj.servisi.soap.klijent.StatusKorisnika.NEPOSTOJI;
import org.foi.nwtis.mkralj.web.dretve.socket.DretvaPripremanjaGrupe;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;


public class NaredbeZaGrupu
{

    private String korisnik;
    private String lozinka;

    private final String naredba;
    
    public NaredbeZaGrupu(String naredbaZaGrupu)
    {
        ServletContext sc = SlusacAplikacije.getSc();
        Konfiguracija konfig = (Konfiguracija)sc.getAttribute("konfiguracija");

        this.korisnik = konfig.dajPostavku("aerodromi_ws.korime");
        this.lozinka = konfig.dajPostavku("aerodromi_ws.lozinka");
        this.naredba = dajCistuNaredbu(naredbaZaGrupu);
    }
    
    private String dajCistuNaredbu(String naredbaZaGrupu)
    {
        naredbaZaGrupu = naredbaZaGrupu.replaceAll("GRUPA", "");
        
        return naredbaZaGrupu.trim();
    }
   
    public InfoKorisniku izvrsiNaredbu()
    {
        InfoKorisniku returnMe = null;
        switch(naredba)
        {
            case "DODAJ":
                returnMe = izvrsiNaredbuDodaj();
                break;
            case "PREKID":
                returnMe = izvrsiNaredbuPrekid();
                break;
            case "KRENI":
                returnMe = izvrsiNaredbuKreni();
                break;
            case "PAUZA":
                returnMe = izvrsiNaredbuPauza();
                break;
            case "STANJE":
                returnMe = izvrsiNaredbuStanje();
                break; 
        }
        
        return returnMe;
    }

    private InfoKorisniku izvrsiNaredbuDodaj()
    {
        StatusKorisnika status = dajStatusGrupe(korisnik, lozinka);
        if(status.equals(NEPOSTOJI) || status.equals(DEREGISTRIRAN))
        {
            registrirajGrupu(korisnik, lozinka);
            DretvaPripremanjaGrupe dpg = new DretvaPripremanjaGrupe(this.korisnik, this.lozinka);
            dpg.start();
            
            return new PovratniInfoKorisniku(Error.OK_20);
        }
        return new PovratniInfoKorisniku(Error._20); 
    }

    private InfoKorisniku izvrsiNaredbuPrekid()
    {
        StatusKorisnika status = dajStatusGrupe(korisnik, lozinka);
        if(status.equals(DEREGISTRIRAN) || status.equals(NEPOSTOJI))
            return new PovratniInfoKorisniku(Error._21, "nije bila registrirana"); 
        
        boolean rez = deregistrirajGrupu(korisnik, lozinka);
        return new PovratniInfoKorisniku(Error.OK_20);
    }

    private InfoKorisniku izvrsiNaredbuKreni()
    {  
        StatusKorisnika status = dajStatusGrupe(korisnik, lozinka);
        if(status.equals(NEPOSTOJI) || status.equals(DEREGISTRIRAN))
            return new PovratniInfoKorisniku(Error._21, "ne postoji");
        else if(status.equals(AKTIVAN))
            return new PovratniInfoKorisniku(Error._22);
        
        aktivirajGrupu(korisnik, lozinka);
        return new PovratniInfoKorisniku(Error.OK_20); 
    }

    private InfoKorisniku izvrsiNaredbuPauza()
    {
        StatusKorisnika status = dajStatusGrupe(korisnik, lozinka);
        if(status.equals(NEPOSTOJI) || status.equals(DEREGISTRIRAN))
            return new PovratniInfoKorisniku(Error._21, "ne postoji");
        else if(status.equals(AKTIVAN))
        {
            blokirajGrupu(korisnik, lozinka);
            return new PovratniInfoKorisniku(Error.OK_20);
        }
        return new PovratniInfoKorisniku(Error._23);
    }

    private InfoKorisniku izvrsiNaredbuStanje()
    {
        StatusKorisnika status = dajStatusGrupe(korisnik, lozinka);
        switch (status)
        {
            case AKTIVAN:
                return new PovratniInfoKorisniku(Error.OK_21);
            case BLOKIRAN:
                return new PovratniInfoKorisniku(Error.OK_22);
            case NEPOSTOJI:
                return new PovratniInfoKorisniku(Error._21, "ne postoji");
            case DEREGISTRIRAN:
                return new PovratniInfoKorisniku(Error._21, "ne postoji");
            default:
                return new PovratniInfoKorisniku(Error._201);
        }
    }

    private static StatusKorisnika dajStatusGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
    }

    private static Boolean registrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    private static Boolean deregistrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    private static Boolean aktivirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    private static Boolean blokirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka)
    {
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service service = new org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS_Service();
        org.foi.nwtis.mkralj.servisi.soap.klijent.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
    }

}
