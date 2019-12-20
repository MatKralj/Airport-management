package org.foi.nwtis.mkralj.web.zrna;

import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import javax.inject.Named;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ClientErrorException;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.staticRest.RESTapp3;
import org.foi.nwtis.mkralj.web.zrna.pomoc.PomocJson;
import org.foi.nwtis.mkralj.web.podaci.KorisnikBase;

@Named(value = "korisnici")
@SessionScoped
public class RadsKorisnicima implements Serializable
{

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    private int trenutnaStranica = 1;
    private int ukBrojStr = 1;

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

    public void setUkBrojStr(int ukBrojStr)
    {
        this.ukBrojStr = ukBrojStr;
    }

    public int getTrenutnaStranica()
    {
        return trenutnaStranica;
    }

    public void setTrenutnaStranica(int trenutnaStranica)
    {
        this.trenutnaStranica = trenutnaStranica;
    }

    private List<KorisnikBase> listaKorisnika = new ArrayList<>();

    public List<KorisnikBase> getListaKorisnika()
    {
        return listaKorisnika;
    }

    public void setListaKorisnika(List<KorisnikBase> listaKorisnika)
    {
        this.listaKorisnika = listaKorisnika;
    }

    public String preuzmi()
    {
        if (!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");

        RESTapp3 client = new RESTapp3();
        try
        {
            
            String loginPodaciJson = upravljanjeKorisnicima.dajPodatkeZaAutentikacijuJson(sesija);
            
            provjeriStranice();
            String jsonKorisnici = client.getJson(loginPodaciJson, this.trenutnaStranica);
            this.listaKorisnika = dajListuIzJson(jsonKorisnici);
            
            if (this.listaKorisnika != null && !this.listaKorisnika.isEmpty())
                this.ukBrojStr = this.listaKorisnika.get(0).getUkBrojStr();
            else
                this.ukBrojStr = 1;
            
        } catch (ClientErrorException | UnsupportedEncodingException ex)
        {
            System.out.println("Exceptiooon: " + ex.getMessage());
        }

        return "";
    }

    private List<KorisnikBase> dajListuIzJson(String jsonKorisnici)
    {
        List<KorisnikBase> returnList = new ArrayList<>();
        try
        {
            PomocJson jPomoc = new PomocJson(jsonKorisnici);
            List<String> listaAerodromaString = jPomoc.dajCistiTekst("odgovor");

            for (String jsonKorisnik : listaAerodromaString)
            {
                KorisnikBase kor = new KorisnikBase(jsonKorisnik);
                if (kor != null)
                {
                    returnList.add(kor);
                }
            }
        } catch (Exception ex)
        {
            System.out.println("GRESKICAAA " + ex.getMessage());
        }

        return returnList;
    }

    public void nextPage()
    {
        if (trenutnaStranica < ukBrojStr)
        {
            trenutnaStranica++;
            preuzmi();
        }
    }

    public void prevPage()
    {
        if (trenutnaStranica > 1)
        {
            trenutnaStranica--;
            preuzmi();
        }
    }

    private void provjeriStranice()
    {
        if(this.trenutnaStranica>ukBrojStr)
                this.trenutnaStranica=1;
    }

}
