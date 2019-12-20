package org.foi.nwtis.mkralj.DB;

public interface DBdata extends DBpretvarac
{
    /**
     * Daje naredbu select svega što je potrebno za pretvoriti podatke u objekt
     * @param tablica Tablica nad kojom se vrši select
     * @return Slect naredba
     */
    String dajSelectNaredbu(String tablica);
    
    /**
     * Daje naredbu select svega što je potrebno za pretvoriti podatke u objekt
     * @param tablica Tablica nad kojom se vrši select
     * @param id ID objekta u bazi koji će se pretvoriti u objekt. WHERE id='id'
     * @return Select where naredba
     */
    String dajSelectNaredbu(String tablica, String id);
    
    /**
     * Daje naredbu update koja ažurira samo vrijeme kada je podatak pospremljen
     * @param tablica Tablica nad kojom se vrši radnja
     * @param id Id na kojem se temelji WHERE dio. Podatak koji se mijenja
     * @return Update stored where naredba
     */
    String dajUpdateVremena(String tablica, String id);
    
    /**
     * Daje insert naredbu za trenutni objekt
     * @param tablica Tablica u koju se upisuje podatak
     * @return Insert naredba
     */
    String dajInsertNaredbu(String tablica);

    /**
     * Daje naredbu select svega što je potrebno za pretvoriti podatke u objekt
     * @param tablica Tablica nad kojom se vrši select
     * @param where Objekt klase WherePart koji sadrži string where dijela naredbe, ali bez WHERE ključne riječi
     * @return Select where naredba
     */
    String dajSelectNaredbu(String tablica, WherePart where);
    
    /**
     * Daje naredbu brisanja iz baze na temelju id
     * @param tablica Tablica nad kojom se vrši naredba brisanja
     * @return Delete naredba
     */
    String dajDeleteNaredbu(String tablica);

    /**
     * Daje update naredbu koja ažurira specifični podataka u tablici
     * @param tablica Tablica nad kojom se vrši ažuriranje
     * @return Update naredba
     */
    String dajUpdateNaredbu(String tablica);
}
