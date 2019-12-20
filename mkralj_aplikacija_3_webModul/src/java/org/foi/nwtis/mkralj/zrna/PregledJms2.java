package org.foi.nwtis.mkralj.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.InformatorJms;
import org.foi.nwtis.mkralj.pomocneKlase.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.sb.UpravljanjePorukama;
import org.foi.nwtis.mkralj.zrna.pomoc.Redirecter;

@Named(value = "pregledJms2")
@SessionScoped
public class PregledJms2 implements Serializable
{

    @EJB
    private UpravljanjePorukama upravljanjePorukama;

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;

    private List<String> listaJms;

    public List<String> getListaJms()
    {
        return listaJms;
    }
    
    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    public PregledJms2()
    {
    }
    
    public String preuzmi()
    {
        if(!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
        {
            new Redirecter().redirectTo("index.xhtml");
            return "";
        }
        
        ThreadSafeKolekcija tsKolekcija = upravljanjePorukama.getListaMqttPoruka();
        this.listaJms = tsKolekcija.getObjekti();

        return "";
    }

    public String brisi(String poruka)
    {
        upravljanjePorukama.brisiMqtt(poruka);
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        jO.addProperty("vrsta", "JMS2");
        InformatorJms.saljiPoruku(gson.toJson(jO));
        
        return "";
    }
 
}
