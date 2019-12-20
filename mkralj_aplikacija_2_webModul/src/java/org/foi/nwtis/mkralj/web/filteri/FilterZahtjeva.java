package org.foi.nwtis.mkralj.web.filteri;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.mkralj.eb.Dnevnik;
import org.foi.nwtis.mkralj.sb.facade.DnevnikFacade;

@WebFilter(filterName = "FilterZahtjeva", urlPatterns =
{
    "/*"
})
public class FilterZahtjeva implements Filter, Serializable
{

    private static final boolean debug = true;

    private FilterConfig filterConfig = null;

    public FilterZahtjeva()
    {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException
    {
        if (debug)
        {
            log("FilterZahtjeva:DoBeforeProcessing");
        }

        request.setAttribute("vrijemePocetka", new Date());

        // Write code here to process the request and/or response before
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log items on the request object,
        // such as the parameters.
        /*
	for (Enumeration en = request.getParameterNames(); en.hasMoreElements(); ) {
	    String name = (String)en.nextElement();
	    String values[] = request.getParameterValues(name);
	    int n = values.length;
	    StringBuffer buf = new StringBuffer();
	    buf.append(name);
	    buf.append("=");
	    for(int i=0; i < n; i++) {
	        buf.append(values[i]);
	        if (i < n-1)
	            buf.append(",");
	    }
	    log(buf.toString());
	}
         */
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException
    {
        if (debug)
        {
            log("FilterZahtjeva:DoAfterProcessing");
        }
        long vrijemeZavrsetkaObrade = new Date().getTime();
        Date vrijemePocetkaObrade = (Date) request.getAttribute("vrijemePocetka");
        long trajanjeObradeMS = vrijemeZavrsetkaObrade - vrijemePocetkaObrade.getTime();

        String IPadresa = request.getRemoteAddr();

        try
        {
            HttpSession sesija = ((HttpServletRequest) request).getSession();
            String korime = dajKorime(sesija);
            ServletContext sc = request.getServletContext();
            String contextUrl = sc.getContextPath();

            String path = dajPath(((HttpServletRequest) request));

            String realUrl = contextUrl + path;

            DnevnikFacade dnevnikFacade = lookupDnevnikFacadeBean();
            if (dnevnikFacade != null)
            {
                zapisiUBazu(korime, trajanjeObradeMS, realUrl, vrijemePocetkaObrade, IPadresa, dnevnikFacade);
            }
        }
        catch(Exception ex)
        {
            System.out.println("Neuspjesni upis u bazu.");
        }

    }

    private void zapisiUBazu(String korime, long trajanjeObradeMS, String realUrl, Date vrijemePocetkaObrade, String IPadresa, DnevnikFacade dnevnikFacade)
    {
        Dnevnik d = new Dnevnik();
        d.setKorisnik(korime);
        d.setTrajanjeobrade((int) trajanjeObradeMS);
        d.setUrl(realUrl);
        d.setVrijemeprijema(vrijemePocetkaObrade);
        d.setIpAdresa(IPadresa);

        dnevnikFacade.create(d);
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException
    {

        if (debug)
        {
            log("FilterZahtjeva:doFilter()");
        }

        doBeforeProcessing(request, response);

        Throwable problem = null;
        try
        {
            chain.doFilter(request, response);
        } catch (Throwable t)
        {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }

        doAfterProcessing(request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null)
        {
            if (problem instanceof ServletException)
            {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException)
            {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig()
    {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy()
    {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig)
    {
        this.filterConfig = filterConfig;
        if (filterConfig != null)
        {
            if (debug)
            {
                log("FilterZahtjeva:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString()
    {
        if (filterConfig == null)
        {
            return ("FilterZahtjeva()");
        }
        StringBuffer sb = new StringBuffer("FilterZahtjeva(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response)
    {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals(""))
        {
            try
            {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex)
            {
            }
        } else
        {
            try
            {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex)
            {
            }
        }
    }

    public static String getStackTrace(Throwable t)
    {
        String stackTrace = null;
        try
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex)
        {
        }
        return stackTrace;
    }

    public void log(String msg)
    {
        filterConfig.getServletContext().log(msg);
    }

    private String dajKorime(HttpSession sesija)
    {
        String korime = (String) sesija.getAttribute("korime");

        if (korime == null || korime.isEmpty())
        {
            return "/";
        }

        return korime;
    }

    private DnevnikFacade lookupDnevnikFacadeBean()
    {
        try
        {
            Context c = new InitialContext();
            return (DnevnikFacade) c.lookup("java:global/mkralj_aplikacija_2/mkralj_aplikacija_2_EJB/DnevnikFacade!org.foi.nwtis.mkralj.sb.facade.DnevnikFacade");
        } catch (NamingException ne)
        {
            System.out.println("Nisam pronasao dnevnik facade");
        }
        return null;
    }

    private String dajPath(HttpServletRequest httpServletRequest)
    {
        String path = httpServletRequest.getPathInfo();
        if (path == null || !path.endsWith(".xhtml"))
        {
            return "";
        }
        if (!path.contains("/"))
        {
            path = "/" + path;
        }

        return path;
    }
}
