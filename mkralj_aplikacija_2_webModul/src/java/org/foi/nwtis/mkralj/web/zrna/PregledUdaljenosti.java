package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.mkralj.sb.UpravljanjeKorisnicima;
import org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1_Service;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.foi.nwtis.mkralj.web.zrna.pomoc.Redirecter;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@Named(value = "udaljenost")
@RequestScoped
public class PregledUdaljenosti implements Serializable
{

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/mkralj_aplikacija_1/SOAPwsApp1.wsdl")
    private SOAPwsApp1_Service service;

    private HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);

    @EJB
    private UpravljanjeKorisnicima upravljanjeKorisnicima;
    private String primljenaPoruka;

    private List<Aerodrom> odabraniAerodromi = new ArrayList<>();

    public List<Aerodrom> getOdabraniAerodromi()
    {
        return odabraniAerodromi;
    }

    public void setOdabraniAerodromi(List<Aerodrom> odabraniAerodromi)
    {
        this.odabraniAerodromi = odabraniAerodromi;
    }

    public String getPrimljenaPoruka()
    {
        return primljenaPoruka;
    }

    public void setPrimljenaPoruka(String primljenaPoruka)
    {
        this.primljenaPoruka = primljenaPoruka;
    }

    public String preuzmiUdaljenost()
    {
        if (!upravljanjeKorisnicima.korisnikJePrijavljen(sesija))
            new Redirecter().redirectTo("index.xhtml");
        if (this.odabraniAerodromi.size() < 2)
            return "";

        Aerodrom odAerodroma = this.odabraniAerodromi.get(0);
        Aerodrom doAerodroma = this.odabraniAerodromi.get(1);
        String korime = upravljanjeKorisnicima.dajKorime(sesija);
        String lozinka = upravljanjeKorisnicima.dajLozinku(sesija);

        Double udaljenost = dajUdaljenostKm(odAerodroma.getIcao(), doAerodroma.getIcao(), korime, lozinka);

        primljenaPoruka = udaljenost + " km";
        return "";
    }

    public void onRowSelect(SelectEvent event)
    {
        FacesMessage msg = new FacesMessage("Aerodrom Selected", ((Aerodrom) event.getObject()).getIcao());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowUnselect(UnselectEvent event)
    {
        FacesMessage msg = new FacesMessage("Aerodrom Unselected", ((Aerodrom) event.getObject()).getIcao());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    private double dajUdaljenostKm(java.lang.String icaoOdAerodroma, java.lang.String icaoDoAerodroma, java.lang.String korime, java.lang.String lozinka)
    {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.mkralj.servisi.soap.SOAPwsApp1 port = service.getSOAPwsApp1Port();
        return port.dajUdaljenostKm(icaoOdAerodroma, icaoDoAerodroma, korime, lozinka);
    }

}
