package org.foi.nwtis.mkralj.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class KorisnikSocketa extends Thread
{

    private boolean radi;
    private final int port;
    private final String server;
    
    private String porukaZaSlanje="";
    private String primljenaPoruka;

    public String getPrimljenaPoruka()
    {
        return primljenaPoruka;
    }
    
    public String getPorukaZaSlanje()
    {
        return porukaZaSlanje;
    }

    public void setPorukaZaSlanje(String porukaZaSlanje)
    {
        this.porukaZaSlanje = porukaZaSlanje;
    }
    
    public KorisnikSocketa(int port, String server)
    {
        this.port = port;
        this.server = server;
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
        saljiPoruku(porukaZaSlanje);
    }

    @Override
    public synchronized void start()
    {
        this.radi = true;
        super.start();
    }


    private void saljiPoruku(String poruka)
    {
        this.radi = true;
        try {
            Socket socket  = new Socket(server, port);
            pisiUSocket(socket, poruka);
            while(radi)
            {
                String porukaPrimljena = citajOdgovorSocketa(socket);
                if(interpretirajPorukuServera(porukaPrimljena))
                {
                    this.primljenaPoruka = porukaPrimljena;
                    socket.shutdownInput();
                    socket.close();
                    radi = false;
                }
            }
        }catch (IOException ex) {
            System.out.println("Greska kod "+ ex.getLocalizedMessage());
        }
    }
    
    /**
     * Piše u socket koji je stvorio prema serveru.
     * @param socket Vrata u koja će poruka biti upisana
     * @param naredba Tekst koji se šalje serveru (naredba)
     * @throws IOException Ukoliko je output stream zatvoren ili je socket zatvoren
     */
    private void pisiUSocket(Socket socket, String naredba) throws IOException
    {
        BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        
        os.write(naredba);
        os.flush();
        socket.shutdownOutput();
    }
    
     /**
     * Čita odgovor servera sa socketa
     * @param socket Vrata sa kojih se čita odgovor.
     * @return Tekst - odgovor servera prema korisniku
     * @throws IOException Ukoliko je input stream zatvoren ili je socket zatvoren
     */
    private String citajOdgovorSocketa(Socket socket) throws IOException
    {
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        
        int znak = 0; 
        while((znak=is.read()) != -1)
        {
            sb.append((char)znak);
        }
        
        return sb.toString();
    }
    
    /**
     * Provjerava što je primljeno od servera, te odlučuje o daljnjem radu
     * @param primljenaPoruka Primljena poruka sa socketa, od servera
     * @return True ukoliko je primljena konkretna ispravna poruka od servera. 
     * Ukoliko od servera nismo dobili ništa (poruka je greška), vraća se false
     */
    private boolean interpretirajPorukuServera(String primljenaPoruka)
    {
        if(!primljenaPoruka.equals(""))
            System.out.println(" primljeno: "+primljenaPoruka);
        
        if(primljenaPoruka.startsWith("OK") || primljenaPoruka.startsWith("ERROR"))
        {
            return true;
        }
        return false;
    }

    public void posaljiNaredbuServeru(String korisnik, String lozinka, String naredba)
    {
        this.porukaZaSlanje = String.format("KORISNIK %s; LOZINKA %s; %s;", korisnik, lozinka, naredba);
        start();
    }
}
