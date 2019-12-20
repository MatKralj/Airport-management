package org.foi.nwtis.mkralj.sb.facade.stranicenje;

import java.util.List;
import javax.persistence.Query;

public class Stranicenje
{

    private final int limit;
    private final int trenutnaStranica;
    private int ukBrojStr;

    public Stranicenje(int trenutnaStranica, int limit)
    {
        this.trenutnaStranica = trenutnaStranica;
        this.limit = limit;
    }
    
    public Query stranici(Query q)
    {
        this.ukBrojStr = countNumberOfPages(q, limit);
        
        q.setFirstResult(dajOffset(trenutnaStranica, limit));
        q.setMaxResults(limit);
        
        return q;
    }

    public int getUkBrojStr()
    {
        return ukBrojStr;
    }

    public int countNumberOfPages(Query q, int limit)
    {
        List<Object[]> rezultat = q.getResultList();
        int returnMe = (int) Math.ceil((double) rezultat.size() / limit);
        return returnMe;
    }
    
    private int dajOffset(int currentPage, int limit)
    {
        return (currentPage - 1) * limit;
    }
}
