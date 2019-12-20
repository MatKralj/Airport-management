package org.foi.nwtis.mkralj.poruke.greske;


public class NeispravnaNaredbaException extends Exception
{
    
    /**
     * Konstruktor nove greške koji je specijalizacija ZabranjenogStanja
     * @param msg Poruka koja će biti prikazana kada se dogodi ova greška
     */
    public NeispravnaNaredbaException(String msg) {
        super(msg);
    }

}
