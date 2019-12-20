package org.foi.nwtis.mkralj.staticRest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class RESTapp3
{

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/mkralj_aplikacija_3_webModul/webresources";

    public RESTapp3()
    {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("korisnik");
    }

    public String azuriraj(Object requestEntity, String id) throws ClientErrorException
    {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]
        {
            id
        })).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String registriraj(Object korisnik) throws ClientErrorException
    {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(korisnik, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String getKorisnika(String id, String auth, String korimeJson) throws ClientErrorException, UnsupportedEncodingException
    {
        WebTarget resource = webTarget;
        if (auth != null)
        {
            resource = resource.queryParam("auth", URLEncoder.encode(auth, "UTF-8"));
        }
        if (korimeJson != null)
        {
            resource = resource.queryParam("korimeJson", URLEncoder.encode(korimeJson, "UTF-8"));
        }
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]
        {
            id
        }));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String autenticiraj(String id, String auth) throws ClientErrorException, UnsupportedEncodingException
    {
        WebTarget resource = webTarget;
        if (auth != null)
        {
            resource = resource.queryParam("auth", URLEncoder.encode(auth, "UTF-8"));
        }
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]
        {
            id
        }));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String getJson(String korisnikJson, int trenutnaStranica) throws ClientErrorException, UnsupportedEncodingException
    {
        WebTarget resource = webTarget;
        if (korisnikJson != null)
        {
            resource = resource.queryParam("korisnikJson", URLEncoder.encode(korisnikJson, "UTF-8"));
        }
        resource = resource.queryParam("trenutnaStranica", trenutnaStranica);
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public void close()
    {
        client.close();
    }
}
