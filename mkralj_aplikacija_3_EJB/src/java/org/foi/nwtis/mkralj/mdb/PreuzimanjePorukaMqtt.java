package org.foi.nwtis.mkralj.mdb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.foi.nwtis.mkralj.InformatorJms;
import org.foi.nwtis.mkralj.sb.UpravljanjePorukama;

@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_mkralj_2"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class PreuzimanjePorukaMqtt implements MessageListener
{

    @EJB
    private UpravljanjePorukama upravljanjePorukama;

    public PreuzimanjePorukaMqtt()
    {
    }

    @Override
    public void onMessage(Message message)
    {
        upravljanjePorukama.addMqttMessage(message);
        Gson gson = new Gson();
        JsonObject jO = new JsonObject();
        jO.addProperty("vrsta", "JMS2");
        InformatorJms.saljiPoruku(gson.toJson(jO));
    }

}
