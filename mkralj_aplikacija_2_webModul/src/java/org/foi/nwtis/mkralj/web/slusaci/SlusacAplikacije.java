package org.foi.nwtis.mkralj.web.slusaci;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mkralj.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mkralj.konfiguracije.NemaKonfiguracije;

public class SlusacAplikacije implements ServletContextListener
{
    
    private static ServletContext sc;

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
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        sc = sce.getServletContext();
        sc.removeAttribute("konfiguracija");
    }
    
    private String dajPuniNazivDatotekeKonf()
    {
        String putanja = sc.getRealPath("/WEB-INF");
        String nazivDatoteke = putanja + File.separator
                + sc.getInitParameter("naziv_konf");
        return nazivDatoteke;
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
}
