package org.foi.nwtis.mkralj.web.dretve;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.mkralj.web.zrna.pomoc.TimeStamp;


public class JMSQueue extends Thread
{ 
    
    private int idPoruke;
    private final String poruka;

    @Override
    public void interrupt()
    {
        System.out.println("Dretva za slanje JMS je prekinuta.");
        super.interrupt();
    }

    @Override
    public void run()
    {
        try
        {
            sendJMSMessageToNWTiS_mkralj_2();
            System.out.println("Dretva salje JMS poruku od mqtta");
        } catch (JMSException | NamingException ex)
        {
            interrupt();
        }
    }

    @Override
    public synchronized void start()
    {
        super.start();
    }
   
    
    public JMSQueue(String poruka)
    {
        this.poruka = poruka;
    }

    private String dajVrijeme()
    {
        TimeStamp ts = new TimeStamp();
        return ts.dajTrenutnoVrijeme("dd.MM.yyyy HH.mm.ss.SSS");
    }

    private String dajPorukuJson(String msgId)
    {
        //{"id": broj, "poruka":  sadrzajMQTTPoruke, "vrijeme": "dd.MM.yyyy hh:mm:ss.SSS"}
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        jO.addProperty("id", msgId);
        jO.addProperty("poruka", this.poruka);
        jO.addProperty("vrijeme", dajVrijeme());
        
        return gson.toJson(jO);
    }

    private Message createJMSMessageForjmsNWTiS_mkralj_2(Session session) throws JMSException
    {
        TextMessage tm = session.createTextMessage();
        String msgId = tm.getJMSMessageID();
        String msgToSend = dajPorukuJson(msgId);
        tm.setText(msgToSend);
        return tm;
    }

    private void sendJMSMessageToNWTiS_mkralj_2() throws JMSException, NamingException
    {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("java:comp/env/jms/NWTiS_QF_mkralj_2");
        Connection conn = null;
        Session s = null;
        try
        {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) c.lookup("java:comp/env/jms/NWTiS_mkralj_2");
            MessageProducer mp = s.createProducer(destination);
            mp.send(createJMSMessageForjmsNWTiS_mkralj_2(s));
        } finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                } catch (JMSException e)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null)
            {
                conn.close();
            }
        }
    }
}
