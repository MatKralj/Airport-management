package org.foi.nwtis.mkralj.poruke.greske;

public class GreskaZaKorisnika
{
    /**
     * Enumeracije predefiniranih mogućih grešaka u programu
     */
    public enum Error{
        _01,
        _02,
        _03,
        _10,
        _11,
        _12,
        _13,
        _141, 
        _142,
        _15,
        _16,
        _20,
        _21,
        _22,
        _23,
        _201,
        OK_10,
        OK_11,
        OK_12,
        OK_13,
        OK_14, 
        OK_20, 
        OK_21, 
        OK_22, 
        OK_23;

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
            case _03:
                returnString = "ERROR 03; Dogodila se pogreska kod izvršavanja vaseg zahtjeva. ";
                break;
            case _11:
                returnString = "ERROR 11; Lozinka ili korisnicko ime su neispravni, ili nemate ovlasti za danu akciju. ";
                break;
            case _12:
                returnString = "ERROR 12; Već je bio u pauzi. ";
                break;
            case _13:
                returnString = "ERROR 13; Nije bio u pauzi. ";
                break;
            case _141:
                returnString = "ERROR 14; Već je bio u aktivnom radu. ";
                break;
            case _142:
                returnString = "ERROR 14; Već je bio u pasivnom radu. ";
                break;
            case _15:
                returnString = "ERROR 15; ICAO ili IATA vec postoji u kolekciji aerodroma. ";
                break;
            case _16:
                returnString = "ERROR 16; Vec je bio u postupku prekida. ";
                break;
            case _20:
                returnString = "ERROR 20; Grupa je vec registrirana. ";
                break;
            case _21:
                returnString = "ERROR 21; Grupa ";
                break;
            case _22:
                returnString = "ERROR 22; Grupa je bila aktivna. ";
                break;
            case _23:
                returnString = "ERROR 23; Grupa nije bila aktivna ";
                break;
            case _201:
                returnString = "ERROR 201; Grupa nije ni aktivna, ni blokirana, ali postoji ";
                break;
            case OK_10:
                returnString = "OK 10; ";
                break;
            case OK_11:
                returnString = "OK 11; Preuzima sve komanda i preuzima podatke za aerodrom. ";
                break;
            case OK_12:
                returnString = "OK 12; Preuzima sve komanda i ne preuzima podatke za aerodrome. ";
                break;
            case OK_13:
                returnString = "OK 13; Preuzima samo poslužiteljske komanda i preuzima podatke za aerodrome. ";
                break;
            case OK_14:
                returnString = "OK 14; Preuzima samo poslužiteljske komanda i ne preuzima podatke za aerodrome. ";
                break;
            case OK_20:
                returnString = "OK 20; ";
                break;
            case OK_21:
                returnString = "OK 21; grupa je aktivna";
                break;
            case OK_22:
                returnString = "OK 22; grupa je blokirana";
                break;
            case OK_23:
                returnString = "OK 23; ";
                break;
        }
        
        return returnString + dodatnaPoruka;
    }
}
