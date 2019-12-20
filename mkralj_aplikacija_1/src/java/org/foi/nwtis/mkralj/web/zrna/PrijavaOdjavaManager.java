package org.foi.nwtis.mkralj.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import org.foi.nwtis.mkralj.sesija.Sesija;

@Named(value = "prijavaOdjava")
@SessionScoped
public class PrijavaOdjavaManager implements Serializable
{

    private String radnja = "";
    private String urlRadnje = "";

    public String getRadnja()
    {
        if(Sesija.korisnikPrijavljen())
            radnja = "Odjavi se";
        else
            radnja = "Prijava";
        
        return radnja;
    }
    
    public String getUrlRadnje()
    {
        if(Sesija.korisnikPrijavljen())
            urlRadnje = "/faces/odjava.xhtml";
        else
            urlRadnje = "/faces/prijava.xhtml";
        
        return urlRadnje;
    }

    public void setUrlRadnje(String urlRadnje)
    {
        this.urlRadnje = urlRadnje;
    }

    public void setRadnja(String radnja)
    {
        this.radnja = radnja;
    }
    
    public PrijavaOdjavaManager()
    {
    }
    
}
