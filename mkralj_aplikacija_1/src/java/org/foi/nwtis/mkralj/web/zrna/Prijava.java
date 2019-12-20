package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.foi.nwtis.mkralj.DB.AutentikacijaKorisnikaDB;
import org.foi.nwtis.mkralj.poruke.greske.GreskaZaKorisnika.Error;
import org.foi.nwtis.mkralj.sesija.Sesija;

@Named(value = "prijava")
@RequestScoped
public class Prijava implements Serializable
{
    
    private String korime;
    private String lozinka;
    private String primljenaPoruka;

    public String getKorime()
    {
        return korime;
    }

    public void setKorime(String korime)
    {
        this.korime = korime;
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public void setLozinka(String lozinka)
    {
        this.lozinka = lozinka;
    }

    public Prijava()
    {
    }
    
    public String prijava()
    {
        AutentikacijaKorisnikaDB auth = new AutentikacijaKorisnikaDB(korime, lozinka);
        
        Error rezultat = auth.autenticiraj();
        if(!rezultat.equals(Error.OK_10))
        {
            Sesija.odjaviKorisnika();
            primljenaPoruka="Prijava nije uspjela";   
        }   
        else
        {
            primljenaPoruka = "Prijava je uspješno provedena";
            Sesija.prijaviKorisnika(korime, lozinka);
            
            Redirecter redirect = new Redirecter();
            redirect.redirectTo("index.xhtml");
        }

        return "";
    }

    public String getPrimljenaPoruka()
    {
        return primljenaPoruka;
    }

    public void setPrimljenaPoruka(String primljenaPoruka)
    {
        this.primljenaPoruka = primljenaPoruka;
    }
    
    public String odjavi()
    {
        Sesija.odjaviKorisnika();
        Redirecter redirect = new Redirecter();
        redirect.redirectTo("index.xhtml");
        
        return "";
    }

    static class RESTwsApp1_JerseyClient
    {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/mkralj_aplikacija_1/webresources";

        public RESTwsApp1_JerseyClient()
        {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("aerodromi");
        }

        public String getJsonIdAvion(String id, String lozinka, String korime) throws ClientErrorException
        {
            WebTarget resource = webTarget;
            if (lozinka != null)
            {
                resource = resource.queryParam("lozinka", lozinka);
            }
            if (korime != null)
            {
                resource = resource.queryParam("korime", korime);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String postJsonIdAvion(Object requestEntity, String id) throws ClientErrorException
        {
            return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity) throws ClientErrorException
        {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String deleteIdAvion(String korime, String lozinka, String id) throws ClientErrorException
        {
            return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id})).request().header("korime", korime).header("lozinka", lozinka).delete(String.class);
        }

        public String getJsonId(String id, String lozinka, String korime) throws ClientErrorException
        {
            WebTarget resource = webTarget;
            if (lozinka != null)
            {
                resource = resource.queryParam("lozinka", lozinka);
            }
            if (korime != null)
            {
                resource = resource.queryParam("korime", korime);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String deleteId(String korime, String lozinka, String id) throws ClientErrorException
        {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().header("korime", korime).header("lozinka", lozinka).delete(String.class);
        }

        public String putJsonId(Object requestEntity, String id) throws ClientErrorException
        {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJson(String lozinka, String korime) throws ClientErrorException
        {
            WebTarget resource = webTarget;
            if (lozinka != null)
            {
                resource = resource.queryParam("lozinka", lozinka);
            }
            if (korime != null)
            {
                resource = resource.queryParam("korime", korime);
            }
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close()
        {
            client.close();
        }
    }
    
    
    
}
