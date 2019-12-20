package org.foi.nwtis.mkralj.DB;

import java.sql.ResultSet;

interface DBpretvarac
{
    
    /**
     * Pretvara podatke dobivene iz baze u objekt
     * @param <T> Tip objekta u koji će se podaci pretvoriti
     * @param rs ResultSet iz baze podataka
     * @return Objekt nastao iz podataka baze 
     */
    <T> T pretvoriUObjekt(ResultSet rs);
}
