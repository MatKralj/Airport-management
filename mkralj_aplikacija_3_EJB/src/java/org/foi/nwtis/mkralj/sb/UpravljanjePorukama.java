package org.foi.nwtis.mkralj.sb;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.foi.nwtis.mkralj.pomocneKlase.SerijalizacijaObjekta;
import org.foi.nwtis.mkralj.pomocneKlase.ThreadSafeKolekcija;

@Singleton
@LocalBean
public class UpravljanjePorukama implements Serializable
{

    private static final long serialVersionUID = 6529685098267757690L;

    private ThreadSafeKolekcija<String> listaJmsPoruka = new ThreadSafeKolekcija<>();
    private ThreadSafeKolekcija<String> listaMqttPoruka = new ThreadSafeKolekcija<>();
    private transient SerijalizacijaObjekta serijalizacijaObj;
    private transient static String datoteka;

    public ThreadSafeKolekcija<String> getListaJmsPoruka()
    {
        return listaJmsPoruka;
    }

    public ThreadSafeKolekcija<String> getListaMqttPoruka()
    {
        return listaMqttPoruka;
    }

    public static void setDatoteka(String datoteka)
    {
        UpravljanjePorukama.datoteka = datoteka;
    }

    public boolean addMessage(Message msg)
    {
        try
        {
            TextMessage tm = (TextMessage) msg;
            if (listaJmsPoruka != null)
            {
                this.listaJmsPoruka.dodajElement(tm.getText());
                return true;
            }
        } catch (Exception ex)
        {
            return false;
        }

        return false;
    }

    @PreDestroy
    void serijaliziraj()
    {
        try
        {
            this.serijalizacijaObj.serijalizirajObjekt(this);
        } catch (IOException ex)
        {
            System.err.println("Serijalizacija objekata nije uspjela. " + ex.getMessage());
        }
    }

    @PostConstruct
    public void deserijaliziraj()
    {
        this.serijalizacijaObj = new SerijalizacijaObjekta(datoteka);
        try
        {
            UpravljanjePorukama deserijaliziraniObjekt = (UpravljanjePorukama) serijalizacijaObj.deserijalizirajObjekt();
            this.listaJmsPoruka = deserijaliziraniObjekt.listaJmsPoruka;
            this.listaMqttPoruka = deserijaliziraniObjekt.listaMqttPoruka;
            if (this.listaJmsPoruka != null)
            {
                listaJmsPoruka.pripremiLock();
            }

            if (this.listaMqttPoruka != null)
            {
                listaMqttPoruka.pripremiLock();
            }

        } catch (IOException | ClassNotFoundException ex)
        {
            System.out.println("Neuspješno deserijaliziranje podataka" + ex.getMessage());
        }
    }

    public boolean addMqttMessage(Message message)
    {
        try
        {
            TextMessage tm = (TextMessage) message;
            if (listaMqttPoruka != null)
            {
                this.listaMqttPoruka.dodajElement(tm.getText());
                return true;
            }
        } catch (Exception ex)
        {
            return false;
        }

        return false;
    }

    public void brisiJms(String poruka)
    {
        for(String s : this.listaJmsPoruka.getObjekti())
        {
            if(s.equals(poruka))
            {
                this.listaJmsPoruka.getObjekti().remove(s);
                break;
            }
                
        }
    }
    
    public void brisiMqtt(String poruka)
    {
        for(String s : this.listaMqttPoruka.getObjekti())
        {
            if(s.equals(poruka))
            {
                this.listaMqttPoruka.getObjekti().remove(s);
                break;
            }
                
        }
    }
}
