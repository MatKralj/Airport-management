package org.foi.nwtis.mkralj.web.dretve.threadSafe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RadSDatotekom
{
    private String datoteka = "";
    
    /**
     * Konstruktor koji stvara objekt rada s datotekom
     * @param datoteka Datoteka nad kojom se vrše radovi
     */
    public RadSDatotekom(String datoteka)
    {
        this.datoteka = datoteka;
    }
    /**
     * Provjerava postoji li datoteka
     * @return True ukoliko postoji, inače false
     */
    public boolean datotekaPostoji()
    {
        File f = new File(datoteka);
        
        return (f.isFile() && f.exists());
    }
    /**
     * Dodaje polje stringova na kraj datoteke
     * @param formatedStrings Polje stringova
     * @throws IOException Ukoliko datotena ne postoji ili nije moguće pisati u nju
     */
    public void dodajNaKraj(ArrayList<String> formatedStrings) throws IOException
    {
        for(String str : formatedStrings)
        {
            dodajNaKraj(str);
        }
    }
    /**
     * Dodaje string na kraj datoteke
     * @param formatedString String koji se dodaje na kraj
     * @throws IOException Ukoliko datotena ne postoji ili nije moguće pisati u nju
     */
    public void dodajNaKraj(String formatedString) throws IOException
    {
        try(FileWriter fw = new FileWriter(datoteka, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(formatedString);   
        }
    }
    /**
     * Briše sav sadržaj koji je zapisan u datoteci
     * @throws IOException Ukoliko dretva ne postoji ili se u nju ne može pisati
     */
    public void obrisiSadrzajDatoteke() throws IOException
    { 
        try (PrintWriter writer = new PrintWriter(datoteka))
        {
            writer.print("");
        }
    }
    /**
     * Kreira novu datoteku naziva datoteke iz ovog objekta
     * @throws IOException Ukoliko nije moguće kreirati datoteku
     */
    public void kreirajDatoteku() throws IOException
    {
        File f = new File(datoteka);
        f.createNewFile();
    }    
}
