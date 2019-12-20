package org.foi.nwtis.mkralj.poruke.odgovori;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac.Greske;


public class OdgovorRest
{
    private JsonObject jObject;

    public OdgovorRest(OdgZaRest dbOdg)
    {
        jObject = new JsonObject();
        Gson gson = new Gson();
        if(dbOdg!=null)
        {
            if(dbOdg.rezultatOk())
            {
                Object result = dbOdg.dajRezultat();
                jObject.add("odgovor", gson.toJsonTree(result));
                jObject.addProperty("status", "OK");
            }
            else if(dbOdg.rezultatNull())
            {
                jObject.addProperty("status", "ERR");
                jObject.add("poruka", gson.toJsonTree(Greske.NullObj));
            }
            else
            {
                jObject.addProperty("status", "ERR");
                jObject.add("poruka", gson.toJsonTree(dbOdg.dajTipGreske()));
            }
        }
        else
        {
            jObject.addProperty("status", "ERR");
            jObject.add("poruka", gson.toJsonTree(Greske.NullObj));
        }
    }
    
    public OdgovorRest(Object data)
    {
        jObject = new JsonObject();
        Gson gson = new Gson();
        if(data!=null)
        {
            jObject.add("odgovor", gson.toJsonTree(data));
            jObject.addProperty("status", "OK");
        }
        else
        {
            jObject.addProperty("status", "ERR");
            jObject.add("poruka", gson.toJsonTree(Greske.NullObj));
        }
    }
    


    
    public JsonObject getJsonObj()
    {
        return jObject;
    }
    
    private String dajJsonObjString()
    {
        Gson gson = new Gson();
        
        return gson.toJson(jObject);
    }

    @Override
    public String toString()
    {
        return dajJsonObjString();
    }
    
    
}
