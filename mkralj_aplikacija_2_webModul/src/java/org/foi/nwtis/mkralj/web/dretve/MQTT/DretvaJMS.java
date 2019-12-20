package org.foi.nwtis.mkralj.web.dretve.MQTT;

import java.util.List;
import org.foi.nwtis.mkralj.web.dretve.JMSQueue;
import org.foi.nwtis.mkralj.web.podaci.Aerodrom;
import org.foi.nwtis.mkralj.web.zrna.pomoc.PomocJson;

class DretvaJMS extends Thread
{

    private final List<Aerodrom> vlastitiAerodromi;
    private final String poruka;

    public DretvaJMS(String poruka, List<Aerodrom> vlastitiAerodromi)
    {
        this.poruka = poruka;
        this.vlastitiAerodromi = vlastitiAerodromi;
    }

    @Override
    public void interrupt()
    {
        super.interrupt();
    }

    @Override
    public void run()
    {
        posaljiJmsPoruku();
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }

    private void posaljiJmsPoruku()
    {
        PomocJson jPomoc = new PomocJson();
        String aerodrom = jPomoc.dajCistiTekst(this.poruka, "aerodrom").get(0);
        for (Aerodrom a : vlastitiAerodromi)
        {
            if (a.getIcao().equals(aerodrom))
            {
                JMSQueue dzsJMS = new JMSQueue(this.poruka);
                dzsJMS.start();
                break;
            }
        }
    }
}
