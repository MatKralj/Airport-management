package org.foi.nwtis.mkralj.poruke.odgovori;

import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac.Greske;

public interface OdgZaRest
{
    Object dajRezultat();
    
    /**
     * Vraća vrsu greške
     * @return Vrsta greške koja se dogodila tijekom rada s bazom
     */
    Greske dajTipGreske();
    String dajTekstGreske();
    
    /**
     * 
     * @return True ukoliko je radnja jednostavna
     */
    boolean isJednostavnaRadnja();
    
    /**
     * 
     * @return True ukoliko je jednostavna radnja usjpela, inače false
     */
    boolean jednostavnaRadnjaUspjela();
    
    /**
     * 
     * @return True ukoliko je rezultat zadovoljavajuć. Nije null i greška ne postoji.
     * Ukoliko je jednostavna radnja, vraća true ukoliko je ona uspjela
     */
    boolean rezultatOk();
    boolean rezultatNull();
}
