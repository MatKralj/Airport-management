package org.foi.nwtis.mkralj.DB;

import org.foi.nwtis.mkralj.DB.odgovor.DBodgovor;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika.Error;
import org.foi.nwtis.mkralj.web.podaci.Korisnik;

public class AutentikacijaKorisnikaDB
{

    private final String korime;
    private final String lozinka;

    public AutentikacijaKorisnikaDB(String korime, String lozinka)
    {
        this.korime = korime;
        this.lozinka = lozinka;
    }
    
    public Error autenticiraj()
    {
        VezaDB veza = VezaDB.getInstanca();
        if(veza==null || veza.getConnection()==null)
            return Error._03;
        
        DBodgovor odgovorBaze = veza.selectObjekt("KORISNICI", new Korisnik(), this.korime);
        if(odgovorBaze==null || !odgovorBaze.rezultatOk())
            return Error._11;
        
        Korisnik k = (Korisnik)odgovorBaze.dajRezultat();
        if(!k.getLozinka().equals(this.lozinka))
            return Error._11;
        
        return Error.OK_10;
    }
    
}
