/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.mkralj.slusaci;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.mkralj.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.mkralj.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.mkralj.sb.UpravljanjePorukama;

/**
 * Web application lifecycle listener.
 *
 * @author Matija
 */
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
        
        UpravljanjePorukama.setDatoteka(getDatotekaSerijalizacije());
        UpravljanjePorukama up = new UpravljanjePorukama();
        up.deserijaliziraj();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        
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


    private String getDatotekaSerijalizacije()
    {
        Konfiguracija konf = (Konfiguracija)sc.getAttribute("konfiguracija");
        String datoteka = konf.dajPostavku("datoteka.serijalizacija");
        String putanja = sc.getRealPath("/WEB-INF");
        
        putanja = putanja.substring(0, putanja.indexOf("dist"));
        putanja = putanja.replaceAll("mkralj_aplikacija_3", "mkralj_aplikacija_3_EJB");
        putanja+=datoteka;
        
        System.err.println("Naziv putanje je : "+putanja);
        return putanja;     
    }
}
