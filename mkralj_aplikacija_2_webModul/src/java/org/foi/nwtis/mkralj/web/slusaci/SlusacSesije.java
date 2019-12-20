package org.foi.nwtis.mkralj.web.slusaci;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.foi.nwtis.mkralj.threadSafe.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.web.dretve.MQTT.MqttKonekcija;

public class SlusacSesije implements HttpSessionListener
{

    private static ThreadSafeKolekcija<HttpSession> aktivneSesije = new ThreadSafeKolekcija<>();

    public static ThreadSafeKolekcija<HttpSession> getAktivneSesije()
    {
        return aktivneSesije;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se)
    {
        HttpSession sesija = se.getSession();

        if (sesija != null && sesija.isNew())
            aktivneSesije.dodajElement(sesija);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se)
    {
        try
        {
            HttpSession sesija = se.getSession();

            if (sesija != null)
            {
                gasiMqttSlusacKorisnika(sesija);

                System.out.println("Gasim sesiju: " + sesija.getAttribute("korime"));
                for (HttpSession s : aktivneSesije.getObjekti())
                {
                    if (s.getAttribute("korime").equals(sesija.getAttribute("korime")))
                    {
                        aktivneSesije.brisi(s);
                        break;
                    }
                }
            }
        } catch (Exception ex)
        {
            System.out.println("Exception :" + ex.getMessage());
        }

    }

    private void gasiMqttSlusacKorisnika(HttpSession sesija)
    {
        try
        {
            MqttKonekcija mqttKonekcija = (MqttKonekcija) sesija.getAttribute("MqttKonekcija");
            mqttKonekcija.interrupt();
        } catch (Exception ex)
        {
            System.out.println("Exception kod interrupta mqtt slusaca: " + ex.getMessage());
        }
    }
}
