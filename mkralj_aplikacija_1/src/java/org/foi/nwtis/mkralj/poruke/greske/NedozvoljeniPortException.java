package org.foi.nwtis.mkralj.poruke.greske;

public class NedozvoljeniPortException extends ZabranjenoStanjeException
{
    /**
     * Konstruktor nove greške koji je specijalizacija ZabranjenogStanja
     * @param msg Poruka koja će biti prikazana kada se dogodi ova greška
     */
    public NedozvoljeniPortException(String msg) {
        super(msg);
    }
}
