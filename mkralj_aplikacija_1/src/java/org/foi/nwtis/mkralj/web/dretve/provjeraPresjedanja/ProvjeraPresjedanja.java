package org.foi.nwtis.mkralj.web.dretve.provjeraPresjedanja;

import java.util.List;
import org.foi.nwtis.mkralj.DB.VezaDB;
import org.foi.nwtis.mkralj.DB.WherePart;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.DB.podaci.Avion;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.web.podaci.ParRelacija;

public class ProvjeraPresjedanja extends Thread
{

    private ThreadSafeKolekcija<ParRelacija> listaMogucihRelacija;
    private Avion polaziste;
    private int minVrijemePresjedanja;
    private String doAerodroma;

    public ProvjeraPresjedanja(ThreadSafeKolekcija<ParRelacija> listaMogucihRelacija,
            Avion polaziste, int minVrijemePresjedanja, String doAerodroma)
    {
        this.polaziste = polaziste;
        this.minVrijemePresjedanja = minVrijemePresjedanja;
        this.doAerodroma = doAerodroma;
        this.listaMogucihRelacija = listaMogucihRelacija;
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
    }

    @Override
    public void run()
    {
        VezaDB veza = VezaDB.getInstanca();
        provjeriOdredisne(polaziste, veza, minVrijemePresjedanja, doAerodroma);
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }

    private void provjeriOdredisne(Avion polaziste, VezaDB veza, int minVrijemePresjedanja, String doAerodroma)
    {
        //todo:  nije jos dovrseno -- mozda cak i koristiti dretve jer bude sporo
        String where = String.format("estDepartureAirport='%s'", polaziste.getEstArrivalAirport());
        DBodgovorLista listaOdredisnih = veza.selectObjekt("airplanes", polaziste, new WherePart(where));
        if (listaOdredisnih == null || !listaOdredisnih.rezultatOk())
        {
            return;
        }
        for (Avion odrediste : (List<Avion>) listaOdredisnih.dajRezultat())
        {
            ParRelacija novaRelacija = null;
            if (doAerodroma.equals(odrediste.getEstDepartureAirport()))
            {
                novaRelacija = new ParRelacija(odrediste);
                this.listaMogucihRelacija.dodajRazlicitiElement(novaRelacija);
            } else if (polaziste.getLastSeen() + minVrijemePresjedanja < odrediste.getLastSeen())
            {
                novaRelacija = new ParRelacija(odrediste);
                this.listaMogucihRelacija.dodajRazlicitiElement(novaRelacija);
                provjeriOdredisne(odrediste, veza, minVrijemePresjedanja, doAerodroma);
            }
        }
    }

    public List<ParRelacija> getRjesenje()
    {
        return this.listaMogucihRelacija.getObjekti();
    }

}
