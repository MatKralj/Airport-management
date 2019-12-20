package org.foi.nwtis.mkralj.web.dretve.socket.podaci;

import java.net.Socket;
import org.foi.nwtis.mkralj.DB.AutentikacijaKorisnikaDB;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.StatusRada;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.DioAplikacije;
import org.foi.nwtis.mkralj.DB.podaci.dnevnik.Dnevnik;
import org.foi.nwtis.mkralj.poruke.greske.InfoKorisniku;
import org.foi.nwtis.mkralj.poruke.greske.NeispravnaNaredbaException;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika.Error;
import org.foi.nwtis.mkralj.poruke.greske.PovratniInfoKorisniku;

public class ObradaZahtjeva
{

    private final String komanda;
    private String korisnik;
    private String lozinka;
    private String naredba;
    
    public ObradaZahtjeva(String komanda) throws NeispravnaNaredbaException
    {
        this.komanda = komanda;
        zapisiParametreKomande();
    }
    
    private void zapisiParametreKomande() throws NeispravnaNaredbaException
    {
        String[] split = komanda.split(";");
        if(split.length<2)
            throw new NeispravnaNaredbaException("Naredba nije ispravna.");
        
        split[0] = split[0].replaceAll("KORISNIK", "");
        split[1] = split[1].replaceAll("LOZINKA", "");
        for(int i = 0; i<split.length; i++)
        {
            split[i] = split[i].trim();
            if(i==2)
                this.naredba = split[i];
        }
        this.korisnik = split[0];
        this.lozinka = split[1];
    }

    public Error autenticirajKorisnika()
    {
        Error err;
        
        err = autenticirajBazom();
  
        if(err==Error.OK_10)
            pisiuDnevnik();
        return err;
    }

    public InfoKorisniku izvrsiNaredbu(StatusRada statusRada, Socket socket)
    {
        InfoKorisniku returnMe;
        if(this.naredba!=null && this.naredba.startsWith("GRUPA") && !statusRada.isPauza())
        {
            NaredbeZaGrupu nzg = new NaredbeZaGrupu(naredba);
            returnMe = nzg.izvrsiNaredbu();
        }
        else if(this.naredba!=null && this.naredba.length()>1)
        {   
            NaredbeZaPosluzitelja nzp = new NaredbeZaPosluzitelja(naredba, statusRada);
            returnMe = nzp.izvrsiNaredbu(socket);
        }
        else
            returnMe = new PovratniInfoKorisniku(Error.OK_10);
        
            
        return returnMe;
    }

    private void pisiuDnevnik()
    {
        DioAplikacije dioAplikacije = new DioAplikacije(DioAplikacije.Dio.autentikacija, "Dretva obrade zahtjeva socketa");
        Dnevnik dnevnik = new Dnevnik(korisnik, dioAplikacije);
        dnevnik.setOpis("Zapisivanje usjpesne autentikacije korisnika u bazu.");
        dnevnik.setRadnja("Upis");
        
        dnevnik.zapisi();
    }

    private Error autenticirajBazom()
    {
        AutentikacijaKorisnikaDB auth = new AutentikacijaKorisnikaDB(korisnik, lozinka);
        
        return auth.autenticiraj();
    }


}
