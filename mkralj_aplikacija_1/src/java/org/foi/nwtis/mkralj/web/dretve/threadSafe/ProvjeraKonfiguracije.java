package org.foi.nwtis.mkralj.web.dretve.threadSafe;

import org.foi.nwtis.mkralj.poruke.greske.NedozvoljeniPortException;

public class ProvjeraKonfiguracije
{
    /**
     * Provjerava port na serverskoj strani iz konfiguracije
     * @param port Vrijednost porta
     * @throws NedozvoljeniPortException Ukoliko je uneseni port u nedozvoljenom rasponu
     */
    public void provjeriPort(int port) throws NedozvoljeniPortException
    {
        int minPort = 8000;
        int maxPort = 9999;
        if (port < minPort || port > maxPort) {
            throw new NedozvoljeniPortException(String.format("Port nije u dopustenom intervalu: %d - %d", minPort, maxPort));
        }
    }
    
    /**
     * Metoda koja iz teksta stvara broj ukoliko je to moguće. Ukoliko nije, postavlja se vrijednost na 0
     * @param vrijednost Vrijednost koju želimo pretvoriti u broj
     * @return Vrijednost pretvorena u broj ili 0 ukoliko nije moguće napraviti pretvorbu.
     * @throws NumberFormatException 
     */
    public int dajBrojIzStringa(String vrijednost)
    {
        Integer returnMe = 0;
        try{
            returnMe = Integer.parseInt(vrijednost);
        }
        catch(NumberFormatException ex)
        {
            System.out.println("Ne mogu pretvoriti vrijednost "+vrijednost+ " u broj. Pretvaram u broj: 0");
            return 0;
        }
         
        return returnMe;
    }
}
