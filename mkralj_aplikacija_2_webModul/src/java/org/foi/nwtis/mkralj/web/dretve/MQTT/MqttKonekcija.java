package org.foi.nwtis.mkralj.web.dretve.MQTT;

import java.net.URISyntaxException;
import org.foi.nwtis.mkralj.sb.facade.MqttPorukeFacade;
import org.foi.nwtis.mkralj.threadSafe.ThreadSafeKolekcija;
import org.foi.nwtis.mkralj.web.zrna.pomoc.PomocJson;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class MqttKonekcija extends Thread
{

    private boolean radi;
    private CallbackConnection connection;
    private ThreadSafeKolekcija<Buffer> listaPoruka = new ThreadSafeKolekcija<>();
    private String korimeJson;
    private String lozinkaJson;

    private String korimeCisti;
    private String lozinkaCista;
    private MqttPorukeFacade mqttPorukeFacade;

    public MqttKonekcija(String korisnikJson, String lozinkaJson)
    {
        this.korimeJson = korisnikJson;
        this.lozinkaJson = lozinkaJson;

        PomocJson jPomoc = new PomocJson();
        korimeCisti = jPomoc.dajCistiTekst(korimeJson, "korime").get(0);
        lozinkaCista = jPomoc.dajCistiTekst(lozinkaJson, "lozinka").get(0);
    }

    @Override
    public void interrupt()
    {
        radi = false;
        if (connection != null)
        {
            UTF8Buffer[] topics =
            {
                new UTF8Buffer("/NWTiS/mkralj".getBytes())
            };
            connection.unsubscribe(topics, new Callback<Void>()
            {
                @Override
                public void onSuccess(Void t)
                {
                    ugasiVezu();
                    System.out.println("Uspjesno unsubscribano s teme.");
                }

                @Override
                public void onFailure(Throwable thrwbl)
                {
                    System.out.println("Neuspjesno ugasen slusac. " + thrwbl.getMessage());
                }
            });
        }

        super.interrupt();
    }

    @Override
    public void run()
    {
        synchronized (Listener.class)
        {
            try
            {
                while (radi)
                {
                    Listener.class.wait();
                }
            } catch (InterruptedException ex)
            {
                System.out.println("Dretva slusac Mqtt je prekinut.");
                interrupt();
            }
        }
    }

    @Override
    public synchronized void start()
    {
        try
        {
            this.connection = createConnection();
            creteListener();
            final String destination = "/NWTiS/mkralj";
            connect(connection, destination);

            radi = true;
            super.start();
        } catch (Exception ex)
        {
            System.out.println("Dretva nije uspjesno kreirana");
        }

    }

    private CallbackConnection createConnection() throws URISyntaxException
    {
        String user = "mkralj";
        String password = "4640C1AC80";
        String host = "nwtis.foi.hr";
        int port = 61613;

        MQTT mqtt = new MQTT();
        mqtt.setHost(host, port);
        mqtt.setUserName(user);
        mqtt.setPassword(password);

        return mqtt.callbackConnection();
    }

    private CallbackConnection creteListener()
    {
        connection.listener(new org.fusesource.mqtt.client.Listener()
        {
            ThreadGroup tgObradaMqtt = new ThreadGroup("dretveObradeMqtt");
            DretvaPreuzimanjaAerodroma dpa = new DretvaPreuzimanjaAerodroma(korimeJson, lozinkaJson);
            @Override
            public void onConnected()
            {
                dpa.start();
                System.out.println("Otvorena veza na MQTT");
            }

            @Override
            public void onDisconnected()
            {
                dpa.interrupt();
                tgObradaMqtt.interrupt();
                System.out.println("Prekinuta veza na MQTT");
            }

            @Override
            public void onFailure(Throwable value)
            {
                dpa.interrupt();
                tgObradaMqtt.interrupt();
                System.out.println("Problem u vezi na MQTT. " + value.getMessage());
            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer msg, Runnable ack)
            {
                DretvaObradeMqtt dretvaObrde = new DretvaObradeMqtt(tgObradaMqtt, "d");
                dretvaObrde.setMessage(msg);
                dretvaObrde.setMqttFacade(mqttPorukeFacade);
                dretvaObrde.setKorisnija(korimeCisti);
                dretvaObrde.setListaVlastitiAerodroma(dpa.getMojiAerodromi());
                
                dretvaObrde.start();
            }
        });

        return connection;
    }

    private void connect(CallbackConnection connection, String destination)
    {
        connection.connect(new Callback<Void>()
        {
            @Override
            public void onSuccess(Void value)
            {
                Topic[] topics =
                {
                    new Topic(destination, QoS.AT_LEAST_ONCE)
                };
                connection.subscribe(topics, new Callback<byte[]>()
                {
                    @Override
                    public void onSuccess(byte[] qoses)
                    {
                        System.out.println("Pretplata na: " + destination);
                    }

                    @Override
                    public void onFailure(Throwable value)
                    {
                        System.out.println("Problem kod pretplate na: " + destination);
                    }
                });
            }

            @Override
            public void onFailure(Throwable value)
            {
                System.out.println("Neuspjela pretplata na: " + destination);
            }
        });
    }

    private void ugasiVezu()
    {
        this.connection.disconnect(new Callback<Void>()
        {
            @Override
            public void onSuccess(Void t)
            {
                System.out.println("Uspjesno ugasena veza s MQTT");
            }

            @Override
            public void onFailure(Throwable thrwbl)
            {
                System.out.println("Neuspjesno ugasena veza s MQTT");
            }
        });
    }

    public void setMqttFacade(MqttPorukeFacade mqttPorukeFacade)
    {
        this.mqttPorukeFacade = mqttPorukeFacade;
    }

}
