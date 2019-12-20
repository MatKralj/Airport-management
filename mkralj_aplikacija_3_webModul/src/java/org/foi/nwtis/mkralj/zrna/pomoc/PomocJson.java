package org.foi.nwtis.mkralj.zrna.pomoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;


public class PomocJson {
    
    private String jsonText;
    
    public PomocJson(String jsonText)
    {
        this.jsonText = jsonText;
    }
    
    public PomocJson()
    {
    }

    public List<String> dajCistiTekst(String jsonText, String attributeName)
    {
        Gson gson = new Gson();
        JsonArray jArray;
        JsonObject jO = gson.fromJson(jsonText, JsonObject.class);
        try{
            jArray = gson.fromJson(jO.get(attributeName), JsonArray.class);
        }catch(Exception ex)
        {
            List<String> returnMe = new ArrayList<>();
            returnMe.add(jO.get(attributeName).getAsString());
            return returnMe;
        }
        List<String> returnList =  new ArrayList<>();
        for(JsonElement jElement : jArray)
        {
            returnList.add(jElement.toString());
        }
        return returnList;
    }
    
    public List<String> dajCistiTekst(String attributeName)
    {
        return dajCistiTekst(this.jsonText, attributeName);
    }
}
