package org.foi.nwtis.mkralj.web.dretve.MQTT;

import java.util.List;
import org.foi.nwtis.mkralj.sb.facade.MqttPorukeFacade;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.fusesource.hawtbuf.Buffer;

class DretvaObradeMqtt extends Thread
{

    ThreadGroup tgDretve = new ThreadGroup("dretve");
    private Buffer message;
    private List<Aerodrom> vlastitiAerodromi;
    private String korisnik;
    private MqttPorukeFacade mqttPorukeFacade;

    DretvaObradeMqtt(ThreadGroup tgDretveObrade, String ime)
    {
        super(tgDretveObrade, ime);
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
    }

    @Override
    public void run()
    {
        String poruka = message.utf8().toString();
        DretvaMqttBaza dMqttBaza = new DretvaMqttBaza(poruka, this.korisnik, mqttPorukeFacade);
        dMqttBaza.start();

        DretvaJMS dJMS = new DretvaJMS(poruka, vlastitiAerodromi);
        dJMS.start();
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }

    void setMessage(Buffer msg)
    {
        this.message = msg;
    }

    void setListaVlastitiAerodroma(List<Aerodrom> mojiAerodromi)
    {
        this.vlastitiAerodromi = mojiAerodromi;
    }

    void setKorisnija(String korimeCisti)
    {
        this.korisnik = korimeCisti;
    }

    void setMqttFacade(MqttPorukeFacade mqttPorukeFacade)
    {
        this.mqttPorukeFacade = mqttPorukeFacade;
    }

}
