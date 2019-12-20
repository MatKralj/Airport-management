package org.foi.nwtis.mkralj.sb.facade;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.mkralj.eb.MqttPoruke;
import org.foi.nwtis.mkralj.eb.MqttPoruke_;
import org.foi.nwtis.mkralj.podaci.MqttPoruka;
import org.foi.nwtis.mkralj.sb.facade.stranicenje.Stranicenje;

@Stateless
public class MqttPorukeFacade extends AbstractFacade<MqttPoruke> {

    @PersistenceContext(unitName = "mkralj_aplikacija_2_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public MqttPorukeFacade()
    {
        super(MqttPoruke.class);
    }

    public List<MqttPoruka> findAllStranicenje(int trenutnaStranica, int limit, String korime)
    {
        Query q = upitSelectLetovi(korime);
        Stranicenje stranicenje = new Stranicenje(trenutnaStranica, limit);
        q = stranicenje.stranici(q);
        int ukBrojStr = stranicenje.getUkBrojStr();
        
        List<MqttPoruke> mqttPoruke = q.getResultList();

        List<MqttPoruka> returnList = new ArrayList<>();
        for (MqttPoruke poruka : mqttPoruke)
        {
            MqttPoruka p = new MqttPoruka();
            p.setKorisnik(korime);
            p.setPoruka(poruka.getPoruka());
            p.setStored(poruka.getStored());
            p.setUkBrojStr(ukBrojStr);
            
            returnList.add(p);
        }

        return returnList;
    }

    public void removeAll(String korime)
    {
        Query q = upitSelectLetovi(korime);
        List<MqttPoruke> mqttPoruke = q.getResultList();
        
        for(MqttPoruke poruka : mqttPoruke)
        {
            remove(poruka);
        }
    }

    private Query upitSelectLetovi(String korisnik)
    {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MqttPoruke> sveMqttPoruke = cq.from(MqttPoruke.class);
        cq.select(sveMqttPoruke);
        List<Predicate> uvjeti = new ArrayList<>();
        
        uvjeti.add(cb.equal(sveMqttPoruke.get(MqttPoruke_.korisnik), korisnik));
        
        cq.where(uvjeti.toArray(new Predicate[]{}));
        Query q = em.createQuery(cq);
        return q;
    }
   
}
