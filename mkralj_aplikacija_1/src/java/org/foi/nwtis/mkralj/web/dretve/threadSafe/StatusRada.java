package org.foi.nwtis.mkralj.web.dretve.threadSafe;


public class StatusRada
{

    private volatile boolean uPrekidu = false; 
    private volatile boolean pasivno = false;
    private volatile boolean pauza = false;

    public boolean isuPrekidu()
    {
        return uPrekidu;
    }

    public void setuPrekidu(boolean uPrekidu)
    {
        this.uPrekidu = uPrekidu;
    }

    public boolean isPasivno()
    {
        return pasivno;
    }

    public void setPasivno(boolean pasivno)
    {
        this.pasivno = pasivno;
    }

    public boolean isPauza()
    {
        return pauza;
    }

    public void setPauza(boolean pauza)
    {
        this.pauza = pauza;
    }
    
}
