package org.foi.nwtis.mkralj.sb;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;

@Singleton
@LocalBean
public class EvidencijaKorisnika
{

   //lista svih prijavljenih korisnika
    
    
    private String dajCistiTekst(String json, String key)
    {
        Gson gson = new Gson();
        JsonObject jO = gson.fromJson(json, JsonObject.class);
        JsonElement jE = jO.get(key);
        
        return jE.getAsString();
    }
}
