package org.foi.nwtis.mkralj.web.slusaci;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.foi.nwtis.mkralj.sesija.Sesija;

public class SlusacSesije implements HttpSessionListener
{

    @Override
    public void sessionCreated(HttpSessionEvent se)
    {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se)
    {
        Sesija.odjaviKorisnika();
    }
}
