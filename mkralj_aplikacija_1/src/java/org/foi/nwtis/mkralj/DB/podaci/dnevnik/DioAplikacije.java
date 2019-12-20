package org.foi.nwtis.mkralj.DB.podaci.dnevnik;


public class DioAplikacije
{

    private Dio dioApp;
    private String dodatnaPoruka;

    public enum Dio{
        autentikacija,
        SOAP_APP1, 
        REST_APP1;
    };
    
    /**
     * Konstruktor koji se koristi kako bi se dobila konacna poruka za upis u dnevnik rada
     * @param d Dio aplikacije koji je upisao
     * @param dodatnaPoruka Ako dio d predstavlja npr dretvu, onda ovaj dio mora jasno definirati o kojoj se dretvi radi
     */
    public DioAplikacije(Dio d, String dodatnaPoruka)
    {
        this.dioApp = d;
        this.dodatnaPoruka = dodatnaPoruka;
    }
    
    /**
     * Daje tekst dijela aplikacije koji se upisuje u dnevnik rada.
     * @return tekst dijela aplikacije koji se upisuje u dnevnik rada.
     */
    public String dajTekstDijela()
    {
        String returnMe="";
        switch(dioApp)
        {
            case autentikacija:
                returnMe = "Autentikacija korisnika-";
                break;
            case SOAP_APP1:
                returnMe = "SOAP zahtjev app1-";
                break;
            case REST_APP1:
                returnMe = "REST zahtjev app1-";
                break;
        }
        
        return returnMe+" "+dodatnaPoruka;
    }
}
