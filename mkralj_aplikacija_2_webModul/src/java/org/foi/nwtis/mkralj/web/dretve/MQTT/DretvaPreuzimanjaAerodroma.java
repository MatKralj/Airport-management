package org.foi.nwtis.mkralj.web.dretve.MQTT;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.mkralj.staticRest.RESTapp1;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.foi.nwtis.mkralj.web.zrna.pomoc.PomocJson;

class DretvaPreuzimanjaAerodroma extends Thread
{

    private final String lozinkaJson;
    private final String korimeJson;
    private List<Aerodrom> mojiAerodromi;
    private boolean radi;

    public DretvaPreuzimanjaAerodroma(String korimeJson, String lozinkaJson)
    {
        this.korimeJson = korimeJson;
        this.lozinkaJson = lozinkaJson;
    }

    @Override
    public void interrupt()
    {
        radi = false;
        super.interrupt();
    }

    @Override
    public void run()
    {
        while(radi)
        {
            this.mojiAerodromi = dajAerodrome();
            
            try{
                Thread.sleep(2000);
            }catch(Exception ex)
            {
                interrupt();
            }
        }
    }

    @Override
    public synchronized void start()
    {
        radi = true;
        super.start();
    }

    public List<Aerodrom> getMojiAerodromi()
    {
        return mojiAerodromi;
    }

    private List<Aerodrom> dajAerodrome()
    {
        RESTapp1 client = new RESTapp1();
        String dodatniParam = kreirajDodatnePodatke(korimeJson);
        try
        {
            String jsonAerodromi = client.getJson(lozinkaJson, korimeJson, dodatniParam);

            return dajAerodromeIzJson(jsonAerodromi);
        } catch (Exception ex)
        {
            System.out.println("Nije moguce dobiti aerodrome od servisa.");
            return null;
        }
    }

    private String kreirajDodatnePodatke(String korimeJson)
    {
        Gson gson = new Gson();
        JsonObject jO = gson.fromJson(korimeJson, JsonObject.class);
        String cistiKorime = jO.get("korime").getAsString();
        JsonObject jObjReturn = new JsonObject();
        jObjReturn.addProperty("dodao", cistiKorime);
        jObjReturn.addProperty("trenutnaStranica", -1);

        return gson.toJson(jObjReturn);
    }

    private List<Aerodrom> dajAerodromeIzJson(String jsonAerodromi)
    {
        List<Aerodrom> returnList = new ArrayList<>();
        try
        {
            PomocJson jPomoc = new PomocJson(jsonAerodromi);
            List<String> listaAerodromaString = jPomoc.dajCistiTekst("odgovor");

            for (String aerodromJson : listaAerodromaString)
            {
                Aerodrom a = new Aerodrom(aerodromJson);
                if (a != null)
                {
                    returnList.add(a);
                }
            }
        } catch (Exception ex)
        {
            System.out.println("GRESKICAAA " + ex.getMessage());
        }

        return returnList;
    }
}
