package org.foi.nwtis.mkralj.zrna.pomoc;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;


public class DateToString implements Converter
{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string)
    {
        try{
            if(string==null)
                return "";
            return string;
        }catch(Exception e)
        {
            return "";
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o)
    {
        
        try{
            if(o==null)
                return "";

            return o.toString();
        }catch(Exception e)
        {
            return "";
        }
    }

}
