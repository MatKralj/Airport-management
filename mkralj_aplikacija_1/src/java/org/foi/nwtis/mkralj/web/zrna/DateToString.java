package org.foi.nwtis.mkralj.web.zrna;

import java.text.SimpleDateFormat;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;


class DateToString implements Converter
{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
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
