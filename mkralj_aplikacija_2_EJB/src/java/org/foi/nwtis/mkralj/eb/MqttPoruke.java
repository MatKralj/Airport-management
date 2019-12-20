package org.foi.nwtis.mkralj.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "MQTT_PORUKE")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "MqttPoruke.findAll", query = "SELECT m FROM MqttPoruke m")
})
public class MqttPoruke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    private String korisnik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String poruka;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date stored;

    public MqttPoruke()
    {
    }

    public MqttPoruke(Integer id)
    {
        this.id = id;
    }

    public MqttPoruke(Integer id, String korisnik, String poruka, Date stored)
    {
        this.id = id;
        this.korisnik = korisnik;
        this.poruka = poruka;
        this.stored = stored;
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

    public String getPoruka()
    {
        return poruka;
    }

    public void setPoruka(String poruka)
    {
        this.poruka = poruka;
    }

    public Date getStored()
    {
        return stored;
    }

    public void setStored(Date stored)
    {
        this.stored = stored;
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
        if (!(object instanceof MqttPoruke))
        {
            return false;
        }
        MqttPoruke other = (MqttPoruke) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "org.foi.nwtis.mkralj.eb.MqttPoruke[ id=" + id + " ]";
    }

}
