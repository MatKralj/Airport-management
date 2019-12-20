package org.foi.nwtis.mkralj.web.dretve.preuzimanjeAviona;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContext;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.DB.podaci.Avion;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.servisi.PomocServisi;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.StatusRada;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.rest.podaci.AvionLeti;



public class PreuzimanjeAviona extends Thread
{
    private static PreuzimanjeAviona instanca = null;

    public static PreuzimanjeAviona getInstanca()
    {
        return instanca;
    }
    
    private boolean radi;
    private long intervalMills;
    private int pocetakIntervala;
    private int krajIntervala;
    private Konfiguracija konfiguracija;
    private int redniBrojCiklusa = 0;
    private DatotekaDretve datotekaDretve;
    private int trajanje;
    private StatusRada statusRada;
    
    private PreuzimanjeAviona()
    {
        
    }
    
    public static PreuzimanjeAviona Kreiraj(StatusRada statusRada) throws IllegalAccessException
    {
        if(instanca == null)
            instanca = new PreuzimanjeAviona(statusRada);
        else
            throw new IllegalAccessException("Dretva vec postoji. ");
        
        return instanca;
    }

    private PreuzimanjeAviona(StatusRada statusRada)
    {
        this.statusRada = statusRada;
    }
    

    public void setInterval(String interval)
    {
        try{
            int i = Integer.parseInt(interval);
            this.intervalMills = (long)i*60*1000;
        }catch(NumberFormatException ex)
        {
            this.intervalMills = 0;
        }
    }
    
    @Override
    public void interrupt()
    {
        this.radi = false;
        super.interrupt();
    }

    @Override
    public void run()
    {
        VezaDB vezaDB = VezaDB.getInstanca();
        PomocServisi ps = new PomocServisi(konfiguracija);
        DBodgovorLista<Aerodrom> aerodromi;
        List<AvionLeti> departures = null;
        long pocetak=0;
        long ukTrajanje = 0;
        while (radi) {
            try {
                pocetak = System.currentTimeMillis();
                postaviIntervaleICiklus(krajIntervala);
                
                aerodromi = vezaDB.selectObjekte("MYAIRPORTS", new Aerodrom());
                if(aerodromi!=null && aerodromi.rezultatOk())
                {
                    departures = getDepartures(aerodromi, ps);
                    spremiAerodrome(departures, vezaDB); 
                }
                
                datotekaDretve.spremi(krajIntervala, redniBrojCiklusa);
                
                ukTrajanje = System.currentTimeMillis() - pocetak;
                Thread.sleep(dajVrijemeSpavanja(ukTrajanje));
                
                pasivnoCekaj();
            } catch (InterruptedException ex) {
                System.out.println("InterruptException. "+ex.getMessage());
            }

        }
    }

    private long dajVrijemeSpavanja(long ukTrajanje)
    {
        long intervalReturn = intervalMills - ukTrajanje;
        if(intervalReturn>0)
            return intervalReturn;
        
        return 0;
    }

    @Override
    public synchronized void start()
    {
        inicijalizirajDretvu();
        super.start();
    }
    
    /**
     * Sprema aerodrome u bazu ukoliko takav isti ne postoji u bazi, te nema null stupaca
     * @param departures Lista odlazaka, dobivena iz servisa OSKlijent
     * @param vezaDB Veza prema bazi podataka
     */
    private void spremiAerodrome(List<AvionLeti> departures, VezaDB vezaDB)
    {
        if(departures != null && !departures.isEmpty())
        {
            for(AvionLeti aL : departures)
            {
                if(provjeriAvion(aL))
                    zapisiUBazu(vezaDB, aL);
            }
        }
    }

    /**
     * Odavlja samu radnju pisanja u bazu podataka, u tablicu AIRPLANES
     * @param vezaDB Veza prema bazi podataka
     * @param aL Objekt tipa AvionLeti
     */
    private void zapisiUBazu(VezaDB vezaDB, AvionLeti aL)
    {
        String wherePart = String.format("icao24='%s' AND firstseen=%s", 
                aL.getIcao24(), aL.getFirstSeen());
        WherePart where = new WherePart(wherePart);
        DBodgovorLista avioni = vezaDB.selectObjekt("AIRPLANES", new Avion(), where);
        
        if(avioni == null || !avioni.rezultatOk())
            upisiNoviAvion(aL, vezaDB);
        else
            odradiUpdateAviona(avioni, vezaDB);
    }

    /**
     * Ukoliko avion postoji u tablici, vrši se samo update vremena kada je zadnje za njega provjerenp
     * @param avioni Lista aviona koji postoje u bazu, u svrhu da se njih ažurira
     * @param vezaDB Veza prema bazi podataka
     */
    private void odradiUpdateAviona(DBodgovorLista<Avion> avioni, VezaDB vezaDB)
    {
        for(Avion a : (List<Avion>)avioni.dajRezultat())
        {
            if(a.getLastSeen()!=0)
                vezaDB.zapisiUTablicu("AIRPLANES", a, a.getIcao24());
        }
    }

    /**
     * Ukoliko avion trenutno ne postoji u bazi, tada se njega upisuje u bazu kao novu instancu (redak)
     * @param aL Avion koji se upisuje, objekt tipa AvionLeti
     * @param vezaDB Veza prema bazi podataka
     */
    private void upisiNoviAvion(AvionLeti aL, VezaDB vezaDB)
    {
        Avion avion = new Avion();
        avion = avion.copyFrom(aL);
        vezaDB.zapisiUTablicu("AIRPLANES", avion);
    }

    /**
     * Dohvaća sve odlaske od servisa OSKlijent na temelju icao koda koji se traži
     * @param aerodromi Lista aerodroma za koje se dohvaćaju avioni na temelju njihovih icao kodova
     * @param osk Objekt OSKlijent servisa, proslijeđuje se kao argument kako se ne bi morao kreirati u svakoj iteraciji dretve, te kako nebi usporavao radnju
     * @return Lista aviona koji su trenutno, ili su bili na aerodromima u zadanom intervalu
     */
    private List<AvionLeti> getDepartures(DBodgovorLista<Aerodrom> aerodromi, PomocServisi ps)
    {
        List<AvionLeti> departures = new ArrayList<>();
        for(Aerodrom ar : (List<Aerodrom>)aerodromi.dajRezultat())
        {
            List<AvionLeti> trenutni = ps.dajOSListuOdlazaka(ar.getIcao(), pocetakIntervala, krajIntervala);
            if(trenutni!=null && !trenutni.isEmpty())
                departures.addAll(trenutni);
        }
        if(!departures.isEmpty())
            departures.sort(Comparator.comparingInt(t -> t.getFirstSeen()));
        
        return departures;
    }
    
    private void inicijalizirajDretvu()
    {
        this.radi = true;
        
        ServletContext sc = SlusacAplikacije.getSc();
        this.konfiguracija = (Konfiguracija) sc.getAttribute("konfiguracija");
        
        if(konfiguracija!=null)
        {
           setInterval(konfiguracija.dajPostavku("dretva.interval"));
           setTrajanje(konfiguracija.dajPostavku("dretva.trajanje"));
           pripremiDatoteku(konfiguracija);
        }   
    }
    
    private boolean provjeriAvion(AvionLeti aL)
    {
        for(Method m : aL.getClass().getMethods())
        {
            if(m.getName().startsWith("get") && m.getParameterCount() == 0)
            {
                if(m.getReturnType()==String.class)
                {
                    if(!provjeriStringMetodu(m, aL))
                        return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Veže se za metodu koja refleksijom provjerava ispravnos aviona. Provjerava String get metode
     * @param m Metoda koju se trenutno provjerava, a vraća String
     * @param aL Avion koji se provjerava
     * @return True ukoliko metoda zadovoljava, inače false
     */
    private boolean provjeriStringMetodu(Method m, AvionLeti aL)
    {
        String obj="";
        try{
            obj = (String)m.invoke(aL);
        }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
        {
            return false;
        }
        return (obj!=null && !obj.toLowerCase().equals("null") && !obj.equals(""));
    }

    private void pripremiDatoteku(Konfiguracija konfiguracija)
    {
        String nazivDatoteke = konfiguracija.dajPostavku("datoteka.dretva");
        String inicijalniPocetak = konfiguracija.dajPostavku("dretva.inicijalniPocetak");
        this.datotekaDretve = new DatotekaDretve(nazivDatoteke, inicijalniPocetak);
        this.redniBrojCiklusa = datotekaDretve.getCiklus();
        
        this.krajIntervala = datotekaDretve.getZadnjiInterval();
    }

    private void postaviIntervaleICiklus(int zadnjiInterval)
    {
        this.pocetakIntervala = zadnjiInterval;
        this.krajIntervala = pocetakIntervala + trajanje;
        if(krajIntervala>=(int)(new Date().getTime()/1000))
        {
            datotekaDretve.setPocetakIntervala(konfiguracija.dajPostavku("dretva.inicijalniPocetak"));
            postaviIntervaleICiklus(datotekaDretve.getZadnjiInterval());
            this.redniBrojCiklusa = datotekaDretve.getCiklus();
        }
        this.redniBrojCiklusa++;
    }

    private void setTrajanje(String trajanje)
    {
        try{
            this.trajanje = Integer.parseInt(trajanje) * 60;
        }catch(NumberFormatException ex)
        {
            this.trajanje = 60;
        }   
    }

    private void pasivnoCekaj() throws InterruptedException
    {
        if(statusRada.isPasivno())
        {
            System.out.println("Pasivno cekam");
            synchronized(this){
                wait();
            }
        }
        System.out.println("Ne cekam vise");
    }

}
