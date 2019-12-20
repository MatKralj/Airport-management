package org.foi.nwtis.mkralj.web.zrna.pomoc;

import java.io.IOException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;


public class Redirecter
{
    
    public void redirectTo(String to)
    {
        try{
            ExternalContext cont = FacesContext.getCurrentInstance().getExternalContext();
            cont.redirect(to);
        }catch(IOException ex)
        {
            System.out.println("Cannot redirect");
        }
    }

}
