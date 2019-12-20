package org.foi.nwtis.mkralj.poruke.greske;

public class NedozvoljeniPort extends ZabranjenoStanje
{
    /**
     * Konstruktor nove greške koji je specijalizacija ZabranjenogStanja
     * @param msg Poruka koja će biti prikazana kada se dogodi ova greška
     */
    public NedozvoljeniPort(String msg) {
        super(msg);
    }
}
