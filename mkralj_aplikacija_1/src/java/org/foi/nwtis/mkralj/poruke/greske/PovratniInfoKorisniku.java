package org.foi.nwtis.mkralj.poruke.greske;

public class PovratniInfoKorisniku implements InfoKorisniku
{    

    private String dodatniArgument = "";
    private GreskaZaKorisnika.Error error;
    
    /**
     * Konstruktor koji kreira objekt koji sadrži grešku i dodatnu poruku
     * @param err Enumeracija predefinirane greške
     * @param dodatniArg Dodatna poruka za ispis uz predodređen opis greške
     */
    public PovratniInfoKorisniku(GreskaZaKorisnika.Error err, String dodatniArg)
    {
        this.error = err;
        this.dodatniArgument = dodatniArg;
    }
    
    /**
     * Konstruktor koji kreira objekt koji sadrži grešku
     * @param err Enumeracija predefinirane greške
     */
    public PovratniInfoKorisniku(GreskaZaKorisnika.Error err)
    {
        this.error = err;
    }
    
    /**
     * Override metode dajTekstGreske sučelja InfoKorisniku koji spaja predodređeni opis greške na temelju 
     * predefinirane greške iz enumeracije uz dodatnu poruku ako je definirana.
     * @return predodređeni opis greške na temelju predefinirane greške iz enumeracije uz dodatnu poruku ako je definirana.
     */
    @Override
    public String dajTekstGreske()
    {
        GreskaZaKorisnika gzk = new GreskaZaKorisnika();
        String returnMe = gzk.dajTekstGreske(error, dodatniArgument);
        return returnMe;
    }


}
