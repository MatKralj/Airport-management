package org.foi.nwtis.mkralj.poruke.greske;

public class GreskaZaKorisnika
{
    /**
     * Enumeracije predefiniranih mogućih grešaka u programu
     */
    public enum Error{
        _01,
        _02,
        _10,
        _11,
        _12,
        _13,
        _14, 
        OK;

    }
    
    /**
     * Daje tekst greške ovisno o enumeraciji greške.
     * @param e Enumeracija greške
     * @param dodatnaPoruka Poruka koju želimo nadodati na predefiniranu povratnu vrijednost ove metode.
     * @return Tekst koji opisuje grešku ovisno o parametru e, te dodatna poruka.
     */
    public String dajTekstGreske(Error e, String dodatnaPoruka)
    {
        String returnString = "";
        switch(e)
        {
            case _01:
                returnString = "ERROR 01; Molimo pricekajte, trenutno nema raspolozivih dretvi. ";
                break;
            case _02:
                returnString = "ERROR 02; Poslana naredba nije ispravnog formata, molimo pokusajte ponovno. ";
                break;
            case _10:
                returnString = "ERROR 10; Lozinka ili korisnicko ime su neispravni, ili nemate ovlasti za danu akciju. ";
                break;
            case _11:
                returnString = "ERROR 11; Dogodila se pogreska kod izvršavanja vaseg zahtjeva. ";
                break;
            case _12:
                returnString = "ERROR 12; Podaci aerodroma ne odgovaraju. ";
                break;
            case _13:
                returnString = "ERROR 13; Cekanje nije uspjeSno odrađeno. ";
                break;
            case _14:
                returnString = "ERROR 14; ICAO ili IATA vec postoji u kolekciji aerodroma. ";
                break;
            case OK:
                returnString = "OK; ";
                break;
        }
        
        return returnString + dodatnaPoruka;
    }
}
