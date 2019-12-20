package org.foi.nwtis.mkralj.web.dretve.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.servlet.ServletContext;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika;
import org.foi.nwtis.mkralj.poruke.greske.InfoKorisniku;
import org.foi.nwtis.mkralj.poruke.greske.NedozvoljeniPortException;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.StatusRada;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.ProvjeraKonfiguracije;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;

public class ServerSocketUpravljanje extends Thread
{

    private ThreadSafeKolekcija<DretvaZahtjeva> dretveZahtjeva = new ThreadSafeKolekcija<>();
    private ThreadGroup tgKorisnickeDretve;
    private boolean radi;
    private ServerSocket ss;
    private static ServerSocketUpravljanje instanca=null;
    private static volatile StatusRada status;

    public static ServerSocketUpravljanje getInstanca()
    {
        return instanca;
    }

    public static ServerSocketUpravljanje kreirajInstancu(StatusRada s) throws IllegalAccessException
    {
        if (instanca != null)
        {
            throw new IllegalAccessException("Instanca server socketa već postoji.");
        }

        instanca = new ServerSocketUpravljanje();
        status = s;
        
        return instanca;
    }
    
    public static ServerSocketUpravljanje kreirajInstancu() throws IllegalAccessException
    {
        if (instanca != null)
        {
            throw new IllegalAccessException("Instanca server socketa već postoji.");
        }

        instanca = new ServerSocketUpravljanje();
        status = new StatusRada();
        
        return instanca;
    }

    private ServerSocketUpravljanje()
    {
        
    }

    @Override
    public void interrupt()
    {
        try
        {
            this.radi = false;
            super.interrupt();
        } finally
        {
            instanca = null;
        }
    }

    @Override
    public void run()
    {
        while (this.radi)
        {
            Socket socket;
            try
            {
                socket = ss.accept();
                upravljajCekacima(socket);
            } catch (IOException ex)
            {
                System.out.println("Greska kod prihvačanja socketa: " + ex.getMessage());
            }
        }
    }

    @Override
    public void start()
    {
        this.radi = true;
        ServletContext sc = SlusacAplikacije.getSc();
        try
        {
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("konfiguracija");
            pripremiKorisnickeDretve(konf);
            ss = kreirajServerSocket(konf);
            super.start();
        } catch (NedozvoljeniPortException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Kreira server socket koji sluša na zahtjeve korisnika za uspostavom komunikacije
     *
     * @param konf Konfiguracija učitana metodom ucitajKonfiguraciju
     * @return ServerSocket ukoliko je sve u redu.
     * @throws NedozvoljeniPortException Ukoliko je iz konfiguracije učitan nedozvoljeni port
     */
    private ServerSocket kreirajServerSocket(Konfiguracija konf) throws NedozvoljeniPortException
    {
        ProvjeraKonfiguracije pk = new ProvjeraKonfiguracije();
        int port = pk.dajBrojIzStringa(konf.dajPostavku("socket.port"));
        int brojCekaca = pk.dajBrojIzStringa(konf.dajPostavku("socket.maks.cekaca"));

        pk.provjeriPort(port);

        ServerSocket ss = null;
        try
        {
            ss = new ServerSocket(port, brojCekaca);
        } catch (IOException ex)
        {
            throw new NedozvoljeniPortException(String.format("Port: " + port + " je zauzet."));
        }

        return ss;
    }

    /**
     * Provjerava ima li slobodnih dretvi, ukoliko ima, njoj dodjeljuje socket i pokreće ju. Inače, pokreće se postupak slanja
     * informacije o nedostpnosti dretve
     *
     * @param socket Vrata na koja se piše poruka (korisnički zahtjev)
     * @throws IOException Ukoliko je output stream zatvore ili je socket zatvoren
     */
    public void upravljajCekacima(Socket socket) throws IOException
    {
        DretvaZahtjeva dretvaZahtjeva = dajSlobodnuDretvu();
        if (dretvaZahtjeva != null)
        {
            dretvaZahtjeva.setSocket(socket);
            pokreniDretvu(dretvaZahtjeva);
        } else
        {
            posaljiInfoNedostupnosti(socket);
        }
    }

    /**
     * Pronalazi slobodnu dretvu ukoliko je ima
     *
     * @return Slobodna dretva DretvaZahtjeva ukoliko je ima, ili null ukoliko nema raspoložive dretve
     */
    private DretvaZahtjeva dajSlobodnuDretvu()
    {
        for (DretvaZahtjeva dz : dretveZahtjeva.getObjekti())
        {
            if (dz.getState().equals(Thread.State.WAITING) || !dz.isAlive())
            {
                return dz;
            }
        }
        return null;
    }

    /**
     * Pokreće dretvu zadanu argumentom.
     *
     * @param dretvaZahtjeva DretvaZahtjeva koju želimo pokrenuti
     */
    private void pokreniDretvu(DretvaZahtjeva dretvaZahtjeva)
    {
        if (dretvaZahtjeva.getState().equals(Thread.State.WAITING))
        {
            synchronized (dretvaZahtjeva)
            {
                dretvaZahtjeva.notify();
            }
        } else
        {
            dretvaZahtjeva.start();
        }
    }

    /**
     * Priprema, tj. kreira određeni broj korisničkih dretvi koje će usluživati korisnike.
     *
     * @param konf Konfiguracija učitana metodom ucitajKonfiguraciju
     */
    private void pripremiKorisnickeDretve(Konfiguracija konf)
    {
        this.tgKorisnickeDretve = new ThreadGroup("mkralj_KD");
        int maxBrojDretvi = 0;

        ProvjeraKonfiguracije pk = new ProvjeraKonfiguracije();
        maxBrojDretvi = pk.dajBrojIzStringa(konf.dajPostavku("maks.dretvi"));

        for (int i = 1; i <= maxBrojDretvi; i++)
        {
            pripremiKorisnickuDretvu(i);
        }
    }

    /**
     * Priprema korisničku dretvu, postavlja joj početne vrijednosti
     *
     * @param redniBroj Redni broj dretve, služi za dodjelu imena
     */
    private void pripremiKorisnickuDretvu(int redniBroj)
    {
        DretvaZahtjeva dz = new DretvaZahtjeva(this.tgKorisnickeDretve, this.tgKorisnickeDretve.getName() + "_" + redniBroj);
        dz.setStatusRada(status);
        this.dretveZahtjeva.dodajElement(dz);
    }

    /**
     * Kreira se nova anonimna dretva tipa DretvaZahtjeva kako bi se korisniku, bez ikakve obrade njegove poruke, poslala poruka da
     * za njega trenutno nema dostupih dretvi
     *
     * @param socket Korisnička vrata na koja je potrebno poslati poruku
     */
    public void posaljiInfoNedostupnosti(Socket socket)
    {
        DretvaZahtjeva dz = new DretvaZahtjeva(socket)
        {
            @Override
            public void run()
            {
                try
                {
                    try
                    {
                        this.posaljiOdgovor(GreskaZaKorisnika.Error._01, "");
                        this.procitajPoruku();
                    } finally
                    {
                        socket.close();
                    }
                } catch (IOException ex)
                {
                    System.out.println("Informacija o nedostupnosti dretve nije poslana. " + ex.getLocalizedMessage());
                }
            }
        };
        dz.start();
    }

    /**
     * Sluzi za zaustavljanje cijelog servere, no prije toga na socket šalje odgovor
     *
     * @param info Odgovor korisniku
     * @param socket Socket na koji se šalje odgovor
     */
    public void interrupt(InfoKorisniku info, Socket socket)
    {
        DretvaZahtjeva dz = new DretvaZahtjeva(socket)
        {
            private InfoKorisniku odg;

            @Override
            public void posaljiOdgovor(InfoKorisniku odgovor) throws IOException
            {
                odg = odgovor;
                super.start();
            }

            @Override
            public void run()
            {
                try
                {
                    super.posaljiOdgovor(odg);
                } catch (Exception ex)
                {
                    System.out.println("Exception: " + ex.getMessage());
                    super.interrupt();
                }
            }
        };

        try
        {
            dz.posaljiOdgovor(info);
            dz.join();
            this.tgKorisnickeDretve.interrupt();
            this.dretveZahtjeva.getObjekti().clear();
        } catch (Exception ex)
        {
            dz.interrupt();
        }

        this.interrupt();
    }
}
