package org.foi.nwtis.mkralj.zrna;

import javax.inject.Named;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.slusaci.SlusacAplikacije;
import org.foi.nwtis.mkralj.socket.KorisnikSocketa;
import org.foi.nwtis.mkralj.zrna.pomoc.Redirecter;

@Named(value = "upravljanjeUticnicom")
@SessionScoped
public class UpravljanjeUticnicom implements Serializable
{

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    
    private String primljenaPorukaServer;
    
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    private String primljenaPorukaGrupa;

    public UpravljanjeUticnicom()
    {
    }
    
    public String trenutniStatusPosluzitelja()
    {
        this.primljenaPorukaServer = posaljiPoruku("STANJE");

        return "";
    }

    public String pauziraj()
    {
        this.primljenaPorukaServer = posaljiPoruku("PAUZA"); 
        
        return "";
    }
    
    public String kreni()
    {
        this.primljenaPorukaServer = posaljiPoruku("KRENI");

        return "";
    }
    
    public String pasiviraj()
    {
        this.primljenaPorukaServer = posaljiPoruku("PASIVNO");
        
        return "";
    }
    
    public String aktiviraj()
    {
        this.primljenaPorukaServer = posaljiPoruku("AKTIVNO");
        
        return "";
    }
    
    public String zavrsiRad()
    {
        this.primljenaPorukaServer = posaljiPoruku("STANI");
        
        return "";
    }
    
    public String provjeriGrupu()
    {
        this.primljenaPorukaGrupa = posaljiPoruku("GRUPA STANJE");
        
        return "";
    }
    
    public String dodajGrupu()
    {
        this.primljenaPorukaGrupa = posaljiPoruku("GRUPA DODAJ");
        
        return "";
    }
    
    public String odjaviGrupu()
    {
        this.primljenaPorukaGrupa = posaljiPoruku("GRUPA PREKID");
        
        return "";
    }
    
    public String aktivirajGrupu()
    {
        this.primljenaPorukaGrupa = posaljiPoruku("GRUPA KRENI");
        
        return "";
    }
    
    public String blokirajGrupu()
    {
        this.primljenaPorukaGrupa = posaljiPoruku("GRUPA PAUZA");
        
        return "";
    }

    public String getPrimljenaPorukaGrupa()
    {
        return primljenaPorukaGrupa;
    }

    public void setPrimljenaPorukaGrupa(String primljenaPorukaGrupa)
    {
        this.primljenaPorukaGrupa = primljenaPorukaGrupa;
    }
    
    private String cekajPoruku(KorisnikSocketa ks)
    {
        try{
            ks.join();
            return ks.getPrimljenaPoruka();
        }catch(Exception ex)
        {
            return "Dogodila se je greška.";
        }
    }

    public String getPrimljenaPorukaServer()
    {
        return primljenaPorukaServer;
    }

    public void setPrimljenaPorukaServer(String primljenaPoruka)
    {
        this.primljenaPorukaServer = primljenaPoruka;
    }

    private int dajPort()
    {
        ServletContext sc = SlusacAplikacije.getSc();
        
        Konfiguracija konfig = (Konfiguracija)sc.getAttribute("konfiguracija");
        
        String strPort = konfig.dajPostavku("socket.port");
        try{
            return Integer.parseInt(strPort);
        }catch(Exception ex)
        {
            return 0;
        }
    }

    private String posaljiPoruku(String naredba)
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);
        
        KorisnikSocketa ks = new KorisnikSocketa(dajPort(), "localhost");
        ks.posaljiNaredbuServeru(korime, lozinka, naredba);
        return cekajPoruku(ks);
    }
 
}
