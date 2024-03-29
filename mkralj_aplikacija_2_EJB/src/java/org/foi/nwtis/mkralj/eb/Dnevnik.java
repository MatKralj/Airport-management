package org.foi.nwtis.mkralj.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d")
})
public class Dnevnik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String korisnik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String url;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijemeprijema;
    @Basic(optional = false)
    @NotNull
    private int trajanjeobrade;
    @Size(max = 25)
    @Column(name = "IP_ADRESA")
    private String ipAdresa;

    public Dnevnik()
    {
    }

    public Dnevnik(Integer id)
    {
        this.id = id;
    }

    public Dnevnik(Integer id, String korisnik, String url, Date vrijemeprijema, int trajanjeobrade)
    {
        this.id = id;
        this.korisnik = korisnik;
        this.url = url;
        this.vrijemeprijema = vrijemeprijema;
        this.trajanjeobrade = trajanjeobrade;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getKorisnik()
    {
        return korisnik;
    }

    public void setKorisnik(String korisnik)
    {
        this.korisnik = korisnik;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Date getVrijemeprijema()
    {
        return vrijemeprijema;
    }

    public void setVrijemeprijema(Date vrijemeprijema)
    {
        this.vrijemeprijema = vrijemeprijema;
    }

    public int getTrajanjeobrade()
    {
        return trajanjeobrade;
    }

    public void setTrajanjeobrade(int trajanjeobrade)
    {
        this.trajanjeobrade = trajanjeobrade;
    }

    public String getIpAdresa()
    {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa)
    {
        this.ipAdresa = ipAdresa;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dnevnik))
        {
            return false;
        }
        Dnevnik other = (Dnevnik) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "org.foi.nwtis.mkralj.eb.Dnevnik[ id=" + id + " ]";
    }

}
