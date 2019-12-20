package org.foi.nwtis.mkralj.web.dretve.socket.podaci;

import java.net.Socket;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.StatusRada;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika;
import org.foi.nwtis.mkralj.poruke.greske.InfoKorisniku;
import org.foi.nwtis.mkralj.poruke.greske.PovratniInfoKorisniku;
import org.foi.nwtis.mkralj.web.dretve.preuzimanjeAviona.PreuzimanjeAviona;
import org.foi.nwtis.mkralj.web.dretve.socket.ServerSocketUpravljanje;


public class NaredbeZaPosluzitelja
{
    
    private final String naredba;
    private final StatusRada statusRada;
    private Socket socket;
    
    public NaredbeZaPosluzitelja(String naredbaZaPosluzitelja, StatusRada statusRada)
    {
        this.naredba = naredbaZaPosluzitelja;
        this.statusRada = statusRada;
    }

    InfoKorisniku izvrsiNaredbu(Socket socket)
    {
        this.socket = socket;
        InfoKorisniku returnMe = null;
        switch(naredba)
        {
            case "AKTIVNO":
                returnMe = izvrsiNaredbuAktivno();
                break;
            case "PASIVNO":
                returnMe = izvrsiNaredbuPasivno();
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
            case "STANI":
                returnMe = izvrsiNaredbuStani();
                break;
        }
        
        return returnMe;
    }

    private InfoKorisniku izvrsiNaredbuAktivno()
    {
        InfoKorisniku info;
        if(statusRada.isPasivno())
        {
            statusRada.setPasivno(false); 
            PreuzimanjeAviona pa = PreuzimanjeAviona.getInstanca();
            synchronized(pa){
                pa.notify(); 
            }
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_10);
        }
        else
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error._141);
        
        return info;
    }

    private InfoKorisniku izvrsiNaredbuPasivno()
    {
        InfoKorisniku info;
        if(!statusRada.isPasivno())
        {
            statusRada.setPasivno(true); 
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_10);
        }
        else
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error._142);
        
        return info;
    }

    private InfoKorisniku izvrsiNaredbuKreni()
    {
        InfoKorisniku info;
        if(statusRada.isPauza())
        {
           statusRada.setPauza(false);
           info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_10);
        }
        else
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error._13);
        
        return info;
    }

    private InfoKorisniku izvrsiNaredbuPauza()
    {
        InfoKorisniku info;
        if(!statusRada.isPauza())
        {
           statusRada.setPauza(true);
           info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_10);
        }
        else
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error._12);
        
        return info;
    }

    /*STANJE; – vraća trenutno stanje poslužitelja. 
    Vraća OK dd; gdje dd znači: 11 – preuzima sve komanda i preuzima podatke za aerodrome, 
    12 - preuzima sve komanda i ne preuzima podatke za aerodrome, 13 – preuzima samo poslužiteljske komanda i 
    preuzima podatke za aerodrome, 14 – preuzima samo poslužiteljske komanda i ne preuzima podatke za aerodrome. */
    private InfoKorisniku izvrsiNaredbuStanje()
    {
        InfoKorisniku info;
        if(!statusRada.isPasivno() && !statusRada.isPauza())
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_11);
        else if(statusRada.isPasivno() && !statusRada.isPauza())
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_12);
        else if(!statusRada.isPasivno() && statusRada.isPauza())
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_13);
        else if(statusRada.isPasivno() && statusRada.isPauza())
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_14);
        else
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error._03);
        
        return info;
    }

    /*STANI; –  potpuno  prekida  preuzimanje  podataka  za  aerodrome  i  preuzimanje  komandi.  I završava  rad.
    Vraća  OK  10;  ako  nije  bio  u  postupku  prekida,  odnosno  ERR  16;  ako  je  bio  u postupku prekida*/
    private InfoKorisniku izvrsiNaredbuStani()
    {
        InfoKorisniku info;
        if(statusRada.isuPrekidu())
            return new PovratniInfoKorisniku(GreskaZaKorisnika.Error._16);
        statusRada.setuPrekidu(true);
        try{
            izvrsiNaredbuPauza();
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error.OK_10);
            PreuzimanjeAviona.getInstanca().interrupt();
            
            ServerSocketUpravljanje.getInstanca().interrupt(info, socket);
            statusRada.setuPrekidu(false);
            
        }catch(Exception ex)
        {
            info = new PovratniInfoKorisniku(GreskaZaKorisnika.Error._16);
        }

        return info;
    }

}
