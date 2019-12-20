package org.foi.nwtis.mkralj.web.zrna;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;


@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable
{

    private HttpSession sesija = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    private String jezik;

    public Lokalizacija() {
    }

    public String getJezik()
    {
        return (String)sesija.getAttribute("jezik");
    }

    public void setJezik(String jezik){
        this.jezik = jezik;
        sesija.setAttribute("jezik", jezik);
    }
}
