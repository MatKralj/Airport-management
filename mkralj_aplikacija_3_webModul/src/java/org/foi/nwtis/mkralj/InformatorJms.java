package org.foi.nwtis.mkralj;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/infoJms")
public class InformatorJms
{
    
    private static Set<Session> listaSesija = Collections.synchronizedSet(new HashSet<Session>());

    /**
     * Poziva se kod spajanja na server end point, nova sesija se dodaje u listu
     * @param session Sesija koja se je otvorila, te će biti dodana u listu
     * @param conf EndPoint konfiguracija
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig conf)
    {
        System.out.println("Otvorena veza s: "+session.getId());
        listaSesija.add(session);
    }
    
    /**
     * Poziva se zatvaranjem veze prema server end pointu, sesija se briše iz liste
     * @param session Sesija koja se zatvara
     * @param conf EndPoint konfiguracija
     */
    @OnClose
    public void onClose(Session session, EndpointConfig conf)
    {
        System.out.println("Zatvorena veza: "+session.getId());
        listaSesija.remove(session);
    }
    
    /**
     * Šalje poruku svim sesijama
     * @param poruka Poruka koja se šalje
     */
    public static void saljiPoruku(String poruka) {
        for(Session s: listaSesija) {
            if(s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(poruka);
                } catch (IOException ex) {
                    System.out.println("Slanje poruke nije uspjelo");
                }
            }
        }
    }

    /**
     * Poziva se primitkom poruke
     * @param msg Poruka koja je primljena
     */
    @OnMessage
    public void onMessage(String msg)
    {
        for(Session s: listaSesija) {
            if(s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(msg);
                } catch (IOException ex) {
                    System.out.println("Slanje poruke nije uspjelo");
                }
            }
        }
    }
}
