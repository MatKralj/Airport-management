package org.foi.nwtis.mkralj.poruke.greske;

public class ExceptionUDretvi
{
    private boolean dogodioSeException = false;
    private Class klasa = null;
    
    /**
     * Konstruktor koji kreira novi objekt klase ExceptionUDretvi
     * @param klasa Klasa u kojoj se je dogodila greška
     */
    public ExceptionUDretvi(Class klasa)
    {
        this.dogodioSeException = true;
        this.klasa = klasa;
    }
    /**
     * Getter za informacije o tome da li se je greška dogodila ili ne.
     * @return True ukoliko se je dogodila greška, inače false.
     */
    public boolean getExceptionHappend()
    {
        return this.dogodioSeException;
    }
    /**
     * Getter klase u kojoj se je dogodila greška.
     * @return Klasa u kojoj se je dogodila greška
     */
    public Class getClassOfException()
    {
        return this.klasa;
    }
}
