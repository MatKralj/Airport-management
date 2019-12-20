package org.foi.nwtis.mkralj.poruke.greske;

public class ZabranjenoStanje extends Exception
{
    /**
     * Konstruktor koji stvara objekt tipa ZabranjenoStanje koje je specijalizacija klase Exception.
     * Ukoliko se dogodi ova iznimka ili neka njezina specijalizacija, aplikacija završava rad.
     * @param msg Dodatna poruka za ispis uz grešku
     */
    public ZabranjenoStanje(String msg)
    {
        super(msg+" Aplikacija se zaustavlja.");
        System.exit(0);
    }
}


