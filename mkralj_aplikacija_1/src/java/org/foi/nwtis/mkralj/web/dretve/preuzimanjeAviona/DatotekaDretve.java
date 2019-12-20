package org.foi.nwtis.mkralj.web.dretve.preuzimanjeAviona;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.ServletContext;
import org.foi.nwtis.mkralj.TimeStamp;
import org.foi.nwtis.mkralj.web.slusaci.SlusacAplikacije;

public class DatotekaDretve
{

    private final String nazivDatoteke;

    private int pocetakIntervala = 0;
    private int redniBrojCiklusa = 0;

    private static DatotekaDretve instanca = null;

    private ReentrantLock lock;

    DatotekaDretve(String datoteka, String pocetakIntervala)
    {
        if (instanca == null)
        {
            lock = new ReentrantLock();
            setPocetakIntervala(pocetakIntervala);
            this.nazivDatoteke = dajPuniNazivDatoteke(datoteka);
            File f = new File(nazivDatoteke);

            if (f.exists() && f.isFile())
            {
                postaviAtribute();
            } else
            {
                kreirajDatoteku();
            }
        } else
        {
            throw new IllegalAccessError("Instanca već postoji");
        }
    }

    public static DatotekaDretve getInstance()
    {
        return instanca;
    }

    private String dajPuniNazivDatoteke(String datoteka)
    {
        ServletContext sc = SlusacAplikacije.getSc();
        String putanja = sc.getRealPath("/WEB-INF");
        String dat = putanja + File.separator + datoteka;
        return dat;
    }

    public int getCiklus()
    {
        return this.redniBrojCiklusa;
    }

    public int getZadnjiInterval()
    {
        return this.pocetakIntervala;
    }

    void spremi(int pocetakIntervala, int redniBrojCiklusa)
    {
        lock.lock();
        try
        {
            System.out.println("Spremam u datoteku. Pocetak: " + pocetakIntervala + ", redniBro: " + redniBrojCiklusa);
            this.pocetakIntervala = pocetakIntervala;
            this.redniBrojCiklusa = redniBrojCiklusa;
            JsonObject jo = dajJsonObject(pocetakIntervala, redniBrojCiklusa);
            zapisi(jo);
        } catch (IOException ex)
        {
            System.out.println("Neuspješno pisanje u datoteku!");
        } finally
        {
            lock.unlock();
        }
    }

    private void zapisi(JsonObject jo) throws IOException
    {
        File f = new File(nazivDatoteke);
        BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
        bw.write(jo.toString() + "\n");
        bw.close();
    }

    private JsonObject citaj() throws IOException
    {
        Gson gson = new Gson();
        File f = new File(nazivDatoteke);
        lock.lock();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String zadnjaLinija = "";
            String tmp = "";
            while ((tmp = br.readLine()) != null)
            {
                zadnjaLinija = tmp;
            }
            br.close();

            JsonObject jO = gson.fromJson(zadnjaLinija, JsonObject.class);

            return jO;
        }
        finally{
            lock.unlock();
        }
    }

    private void postaviAtribute()
    {
        int rBr = 0;
        try
        {
            JsonObject jO = citaj();
            if (jO != null)
            {
                JsonElement jElementInterval = jO.get("pocetakIntervala");
                JsonElement jElementRbrCiklus = jO.get("redniBrojCiklusa");

                TimeStamp ts = new TimeStamp();
                this.pocetakIntervala = ts.dajUnixDatum(jElementInterval.getAsString());
                rBr = stringToInt(jElementRbrCiklus.toString());
            }
        } catch (IOException ex)
        {
            System.out.println("Neuspjesno postavljanje atributa za upis u datoteku dretve.");
        }

        this.redniBrojCiklusa = rBr;
    }

    private void kreirajDatoteku()
    {
        File f = new File(nazivDatoteke);
        try
        {
            boolean created = f.createNewFile();
            if (created)
            {
                f.setWritable(true);
                f.setReadable(true);
            }
        } catch (IOException ex)
        {
            System.out.println("Neuspjesno stvaranje datoteke. " + ex.getMessage());
        }
    }

    private JsonObject dajJsonObject(int pocetakIntervala, int redniBrojCiklusa)
    {
        Gson gson = new Gson();
        String datumZaUpis = new TimeStamp().dajDatumIzEpoch(pocetakIntervala);
        JsonElement jElementInterval = gson.toJsonTree(datumZaUpis);
        JsonElement jElementRbrCiklus = gson.toJsonTree(redniBrojCiklusa);
        JsonObject jo = new JsonObject();
        jo.add("pocetakIntervala", jElementInterval);
        jo.add("redniBrojCiklusa", jElementRbrCiklus);

        return jo;
    }

    private int stringToInt(String asString)
    {
        try
        {
            return Integer.parseInt(asString);
        } catch (NumberFormatException ex)
        {
            return 0;
        }
    }

    public final void setPocetakIntervala(String pocetakIntervala)
    {
        this.pocetakIntervala = new TimeStamp().dajUnixDatum(pocetakIntervala);
    }

}
