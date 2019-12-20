package org.foi.nwtis.mkralj.sb.facade;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.mkralj.eb.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.foi.nwtis.mkralj.podaci.DnevnikStr;
import org.foi.nwtis.mkralj.sb.facade.stranicenje.Stranicenje;


@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> {

    @PersistenceContext(unitName = "mkralj_aplikacija_2_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public DnevnikFacade()
    {
        super(Dnevnik.class);
    }
    
    public List<DnevnikStr> findAll(int trenutnaStranica, int limit)
    {
        Query q = upitSelectSveDnevnik();
        
        Stranicenje stranicenje = new Stranicenje(trenutnaStranica, limit);
        q = stranicenje.stranici(q);
        int ukBrojStr = stranicenje.getUkBrojStr();
        
        List<Dnevnik> listaDnevnik = q.getResultList();

        List<DnevnikStr> returnList = new ArrayList<>();
        for (Dnevnik d : listaDnevnik)
        {
            DnevnikStr dStr = new DnevnikStr();
            dStr.setId(d.getId());
            dStr.setIpAdresa(d.getIpAdresa());
            dStr.setKorisnik(d.getKorisnik());
            dStr.setTrajanjeobrade(d.getTrajanjeobrade());
            dStr.setUrl(d.getUrl());
            dStr.setVrijemePrijema(d.getVrijemeprijema());
            dStr.setUkBrojStr(ukBrojStr);
            
            returnList.add(dStr);
        }
        
        return returnList;
    }
    
    private Query upitSelectSveDnevnik()
    {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Dnevnik> sviZapisi = cq.from(Dnevnik.class);
        cq.select(sviZapisi);

        Query q = em.createQuery(cq);
        return q;
    }

}
