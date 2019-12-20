package org.foi.nwtis.mkralj.web.slusaci;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mkralj.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mkralj.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.mkralj.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.mkralj.web.dretve.preuzimanjeAviona.PreuzimanjeAviona;
import org.foi.nwtis.mkralj.web.dretve.socket.ServerSocketUpravljanje;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.StatusRada;

public class SlusacAplikacije implements ServletContextListener
{

    private static ServletContext sc;
    private VezaDB veza;
    private PreuzimanjeAviona preuzimanjeAviona = null;
    private ServerSocketUpravljanje ssu = null;
    private volatile StatusRada statusRada;
    
    public static ServletContext getSc()
    {
        return sc;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        sc = sce.getServletContext();
        
        String nazivDatoteke = dajPuniNazivDatotekeKonf();
        
        postaviKonfigKontekst(nazivDatoteke);
        postaviKonfigBPKontekst(nazivDatoteke);
        
        kreirajDBVezu();
        pokreniDretve();
    }

    private void pokreniDretve()
    {
        statusRada = new StatusRada();
        try{
            preuzimanjeAviona = PreuzimanjeAviona.Kreiraj(statusRada);
            preuzimanjeAviona.start();
        
            ssu = ServerSocketUpravljanje.kreirajInstancu(statusRada);
            ssu.start();
        }catch(IllegalAccessException ex)
        {
            System.out.println("Dretva vec postoji. "+ex.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        if(preuzimanjeAviona != null)
            preuzimanjeAviona.interrupt();
        if(ssu != null)
            ssu.interrupt();
        sc = sce.getServletContext();
        if(veza!=null)
            veza.zatvoriVezu();
        
        sc.removeAttribute("konfiguracija_baza");
        sc.removeAttribute("konfiguracija");
        sc = null;
    }
    
    
    
    private void postaviKonfigBPKontekst(String nazivDatoteke)
    {
        try {
            BP_Konfiguracija konfiguracija = new BP_Konfiguracija(nazivDatoteke);
            sc.setAttribute("konfiguracija_baza", konfiguracija);
            
            System.out.println("Učitana BP_konfiguracija!");
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.out.println("Greska kod ucitavanja konfiguracije BP "+ex.getMessage());
        }
    }
    
    private void postaviKonfigKontekst(String nazivDatoteke)
    {
        Konfiguracija konfiguracija;
        try
        {
            konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
            sc.setAttribute("konfiguracija", konfiguracija);
            
            System.out.println("Učitana konfiguracija!");
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex)
        {
            System.out.println("Greska kod ucitavanja konfiguracije "+ex.getMessage());
        }
    }
    
    private void kreirajDBVezu()
    {
        try
        {
            BP_Konfiguracija bpKonf = (BP_Konfiguracija)sc.getAttribute("konfiguracija_baza");

            veza = new VezaDB(bpKonf);
            veza.kreirajVezu();
        } catch (IllegalAccessException | NullPointerException ex)
        {
            veza = null;
            System.out.println("Greska kod kreiranja veze! "+ex.getMessage());
        }
    }

    private String dajPuniNazivDatotekeKonf()
    {
        String putanja = sc.getRealPath("/WEB-INF");
        String nazivDatoteke = putanja + File.separator
                + sc.getInitParameter("naziv_konf");
        return nazivDatoteke;
    }
}
