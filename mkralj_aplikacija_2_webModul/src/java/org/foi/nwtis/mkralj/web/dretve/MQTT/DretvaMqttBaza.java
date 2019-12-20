package org.foi.nwtis.mkralj.web.dretve.MQTT;

import java.util.Date;
import org.foi.nwtis.mkralj.eb.MqttPoruke;
import org.foi.nwtis.mkralj.sb.facade.MqttPorukeFacade;
import org.foi.nwtis.mkralj.web.zrna.pomoc.TimeStamp;


class DretvaMqttBaza extends Thread
{

    private final String jsonPoruka;
    private final String korisnik;
    private final MqttPorukeFacade mqttPorukeFacade;
    
    public DretvaMqttBaza(String jsonPoruka, String korisnik, MqttPorukeFacade mqttPorukeFacade)
    {
        this.mqttPorukeFacade = mqttPorukeFacade;
        this.korisnik = korisnik;
        this.jsonPoruka = jsonPoruka;
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
    }

    @Override
    public void run()
    {
        zapisiuBazu(jsonPoruka);
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }
    
    private void zapisiuBazu(String porukaJson)
    {
        MqttPoruke poruke = new MqttPoruke();
        poruke.setKorisnik(this.korisnik);
        poruke.setPoruka(porukaJson);
        poruke.setStored(dajVrijemeZaBazu());
        
        System.out.println("Zapisujem u bazu mqtt");

        mqttPorukeFacade.create(poruke);
    }
    
    private Date dajVrijemeZaBazu()
    {
        TimeStamp ts = new TimeStamp();
        String bazaDatum = ts.dajTrenutnoVrijemeBaza();
        long unix = ts.dajUnixMills(bazaDatum, "yyy-MM-dd HH:mm:ss.SSS");
        return new Date(unix);
    }

}
