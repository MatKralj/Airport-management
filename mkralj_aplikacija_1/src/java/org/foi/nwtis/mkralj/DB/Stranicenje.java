package org.foi.nwtis.mkralj.DB;

import javax.servlet.ServletContext;
import org.foi.nwtis.mkralj.konfiguracije.Konfiguracija;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;

public class Stranicenje
{

    private String tablica;
    private int brRedaka;
    private int ukBbrojPodataka;
    private int brojStranica;
    private final String toCount;

    public Stranicenje(String tablica, String toCount)
    {
        this.tablica = tablica;
        this.toCount = toCount;
    }

    public int getBrojStranica()
    {
        return brojStranica;
    }

    public WherePart dajWhereZaStranicenje(String whereString, int trenutnaStranica)
    {
        WherePart where;
        if(trenutnaStranica!=-1)
        {
            String wherePart;
            brojStranica = dajBrojStranicaStranicenja(whereString);
            wherePart = String.format("%s LIMIT %s, %s",
                whereString, dajBrojRedakaOffset(trenutnaStranica), brRedaka);

            where = new WherePart(wherePart);
        }else
            where = new WherePart(whereString);

        return where;
    }

    private int dajBrojStranicaStranicenja(String whereDio)
    {
        this.brRedaka = Integer.parseInt(tablicaBrojRedaka());

        VezaDB veza = VezaDB.getInstanca();
        WherePart where = new WherePart(whereDio);
        this.ukBbrojPodataka = veza.selectCount(this.tablica, toCount, where);
        int returnMe = (int) Math.ceil((double) ukBbrojPodataka / this.brRedaka);

        return returnMe;
    }

    private int dajBrojRedakaLimit(int trenutnaStranica)
    {
        try
        {
            int returnMe = (trenutnaStranica - 1) * brRedaka + brRedaka;

            return returnMe;
        } catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    private int dajBrojRedakaOffset(int trenutnaStranica)
    {
        return (trenutnaStranica - 1) * this.brRedaka;
    }

    private String tablicaBrojRedaka()
    {
        ServletContext sc = SlusacAplikacije.getSc();
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("konfiguracija");

        String returnStr = konf.dajPostavku("tablica.brojRedaka");
        if (returnStr != null && !returnStr.isEmpty())
        {
            return returnStr;
        }

        return "Greška! Broj redaka tablice nije pronađen";
    }
}
