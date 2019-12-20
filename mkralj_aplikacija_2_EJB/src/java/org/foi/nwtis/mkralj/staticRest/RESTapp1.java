package org.foi.nwtis.mkralj.staticRest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class RESTapp1
{

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/mkralj_aplikacija_1/webresources";

    public RESTapp1()
    {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
    }

    public String getJsonIdAvion(String id, String lozinka, String korime) throws ClientErrorException, UnsupportedEncodingException
    {
        WebTarget resource = webTarget;
        if (lozinka != null)
        {
            resource = resource.queryParam("lozinka", URLEncoder.encode(lozinka, "UTF-8"));
        }
        if (korime != null)
        {
            resource = resource.queryParam("korime", URLEncoder.encode(korime, "UTF-8"));
        }
        resource = resource.path(java.text.MessageFormat.format("{0}/avion", new Object[]
        {
            id
        }));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String postJsonIdAvion(Object requestEntity, String id) throws ClientErrorException
    {
        return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]
        {
            id
        })).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String postJson(Object requestEntity) throws ClientErrorException
    {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String deleteIdAvion(String korime, String lozinka, String id) throws ClientErrorException
    {
        return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]
        {
            id
        })).request().header("korime", korime).header("lozinka", lozinka).delete(String.class);
    }

    public String getJsonId(String id, String lozinka, String korime) throws ClientErrorException, UnsupportedEncodingException
    {
        WebTarget resource = webTarget;
        if (lozinka != null)
        {
            resource = resource.queryParam("lozinka", URLEncoder.encode(lozinka, "UTF-8"));
        }
        if (korime != null)
        {
            resource = resource.queryParam("korime", URLEncoder.encode(korime, "UTF-8"));
        }
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]
        {
            id
        }));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String deleteId(String korime, String lozinka, String id) throws ClientErrorException
    {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]
        {
            id
        })).request().header("korime", korime).header("lozinka", lozinka).delete(String.class);
    }

    public String putJsonId(Object requestEntity, String id) throws ClientErrorException
    {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]
        {
            id
        })).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String getJson(String lozinka, String korime, String dodatniParam) throws ClientErrorException, UnsupportedEncodingException
    {
        WebTarget resource = webTarget;
        if (lozinka != null)
        {
            resource = resource.queryParam("lozinka", URLEncoder.encode(lozinka, "UTF-8"));
        }
        if (korime != null)
        {
            resource = resource.queryParam("korime", URLEncoder.encode(korime, "UTF-8"));
        }
        if (dodatniParam != null)
        {
            resource = resource.queryParam("dodatniParam", URLEncoder.encode(dodatniParam, "UTF-8"));
        }

        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public void close()
    {
        client.close();
    }
}
