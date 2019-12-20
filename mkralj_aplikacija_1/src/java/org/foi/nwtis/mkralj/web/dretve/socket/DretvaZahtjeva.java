package org.foi.nwtis.mkralj.web.dretve.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.foi.nwtis.mkralj.poruke.greske.ExceptionUDretvi;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika;
import org.foi.nwtis.mkralj.poruke.greske.InfoKorisniku;
import org.foi.nwtis.mkralj.poruke.greske.NeispravnaNaredbaException;
import org.foi.nwtis.mkralj.web.dretve.socket.podaci.ObradaZahtjeva;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.StatusRada;
import org.foi.nwtis.mkralj.web.dretve.threadSafe.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.web.dretve.jms.JMSQueue;



public class DretvaZahtjeva extends Thread
{

    private Socket socket;
    private boolean radi = true;
    private ThreadSafeKolekcija<ExceptionUDretvi> spremnikGresakaDretvi = null;
    private StatusRada statusRada;

    /**
     * Konstruktor dretve koja služi za usluživanje korisničkih vrata (socket).
     * @param group Grupa (ThreadGroup) kojoj ova dretva pripada
     * @param name Ime dretve
     */
    public DretvaZahtjeva(ThreadGroup group, String name) {
        super(group, name);
    }
    /**
     * Konstruktor koji služi isključivo za anonimne dretve. Kreira novu dretvu bez grupe ili imena.
     * @param socket Korisnička vrata koje treba uslužiti.
     */
    public DretvaZahtjeva (Socket socket)
    {
        this.socket = socket;
    }
    
    /**
     * Spremnik u koji sve dretve spremaju greše kod završavanja, kako bi se moglo javiti korisniku da li se je dogodila greška.
     * @param spremnikGresaka Objekt tipa ThreadSafeKolekcija u kojeg je moguće zapisati greške kod rada, te je moguće čitati 
     * greške drugih dretvi.
     */
    public void setSpremnikZaGreskeDretvi(ThreadSafeKolekcija<ExceptionUDretvi> spremnikGresaka)
    {
        this.spremnikGresakaDretvi = spremnikGresaka;
    }
    
    /**
     * Pokreće rad dretve.
     */
    @Override
    public synchronized void start()
    {
        super.start();
    }
    /**
     * Override metode iz klase Thread. U ovoj metodi se nalazi sva logika rada dretve. Pokreće se automatski nakon start()
     */
    @Override
    public void run()
    {
        while(radi){
            try{
                String komanda = procitajPoruku();
                ObradaZahtjeva oz = new ObradaZahtjeva(komanda);
                GreskaZaKorisnika.Error autentikacija = oz.autenticirajKorisnika();
                switch(autentikacija)
                {
                    case OK_10:
                        InfoKorisniku odgovor = oz.izvrsiNaredbu(statusRada, socket);
                        posaljiJms(komanda);
                        odgovoriKorisniku(odgovor);
                        break;
                    default:
                        posaljiOdgovor(autentikacija, "");
                        break;
                }
                
                socket.close();
                synchronized(this){
                    wait();
                }
            }
            catch (IOException | InterruptedException | NeispravnaNaredbaException ex){
                
                System.out.println("Zaustavljam dretvu."+ex.getMessage());
                interrupt();
            }
        }
    }
    /**
     * Override metode iz klase Thread, poziva se prilikom prekidanja dretve metodom interrupt()
     */
    @Override
    public void interrupt() {
        this.radi = false;
        try{
            socket.close();
        } catch (IOException ex)
        {
            System.out.println("Ne mogu zatvoriti socket kod zavrsetka rada.");
        }
    }
    /**
     * Setter socketa već postojećoj dretvi. Postavlja korisnička vrata (socket) dretvi.
     * @param socket Korisnička vrata (korisnički socket) koja treba uslužiti
     */
    void setSocket(Socket socket) {
        this.socket = socket;
    }
    /**
     * Čita poruku iz trenutno dodijeljenog socketa (korisničkih vrati)
     * @return Pročitana poruka sa socketa - tekst
     * @throws IOException Ukoliko je input stream zatvoren, ili ne postoji socket.
     */
    public String procitajPoruku() throws IOException
    {
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        StringBuilder sb = new StringBuilder();
        int znak;
        while((znak=is.read()) != -1)
        {
            sb.append((char)znak);
        }
        
        socket.shutdownInput();
        return sb.toString();
    }
    /**
     * Šalje odgovor korisniku preko njegovih vrata (socketa).
     * @param poruka Tekst koji želimo poslati koirsniku
     * @throws IOException Ukoliko je output stream zatvoren, ili ne postoji socket.
     */
    public void posaljiOdgovor(String poruka) throws IOException
    {
        BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

        os.write(poruka);
        os.flush();
        socket.shutdownOutput();
    }
    /**
     * Šalje odgovor korisniku preko njegovih vrata (socketa).
     * @param greska Enumeracija greške (Error) koji se šalje.
     * @param dodatnaPoruka Dodatna poruka za tekst greske
     * @throws IOException Ukoliko je output stream zatvoren, ili ne postoji socket.
     */
    public void posaljiOdgovor(GreskaZaKorisnika.Error greska, String dodatnaPoruka) throws IOException
    {
        GreskaZaKorisnika gzk = new GreskaZaKorisnika();
        String odgovor = gzk.dajTekstGreske(greska, dodatnaPoruka);
        
        this.posaljiOdgovor(odgovor);
    }
    /**
     * Šalje odgovor korisniku preko njegovih vrata (socketa).
     * @param odgovor Objekt sučelja InfoKorisnik koje sadrži informacije za slanje. 
     * @throws IOException Ukoliko je output stream zatvoren, ili ne postoji socket.
     */
    public void posaljiOdgovor(InfoKorisniku odgovor) throws IOException
    {   
        String info = odgovor.dajTekstGreske();
        
        this.posaljiOdgovor(info);
    }


    /**
     * Provjerava da li je neka dretva javila da se je kod završetka rada dogodila greška.
     * @return Objekt tipa ExceptionUDretvi ukoliko je pronađena greška, inače null.
     */
    private ExceptionUDretvi provjeriImaLiGresaka()
    {
        for(ExceptionUDretvi obj : spremnikGresakaDretvi.getObjekti())
        {
            if(obj.getExceptionHappend())
            {
                return obj;   
            }
        }
        
        return null;
    }


    /**
     * Izvršava slanje poruke korisniku.
     * @param odgovor Objekt u kojem je spremljena poruka za slanje. Ako je nullm tada se šalje ERROR_20
     * @throws IOException Ukoliko je output stream socketa zatvore, ili ukoliko je socket zatvoren
     */
    private void odgovoriKorisniku(InfoKorisniku odgovor) throws IOException
    {
        if(odgovor!=null)
            this.posaljiOdgovor(odgovor);
        else
            this.posaljiOdgovor("ERROR 20; Poslani su nepoznati podaci do servera!");
    }

    void setStatusRada(StatusRada status)
    {
        this.statusRada = status;
    }

    private void posaljiJms(String komanda)
    {
        JMSQueue myJmsQueue = new JMSQueue(komanda);
        myJmsQueue.start();
    }

}
