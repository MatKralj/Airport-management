package org.foi.nwtis.mkralj.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovor;
import org.foi.nwtis.mkralj.DB.odgovor.DBodgovorLista;
import org.foi.nwtis.mkralj.poruke.greske.ErrorLokalizac.Greske;


class Upit
{ 
    
    DBodgovorLista izvrsiSelectSvega(Connection con, DBdata data, String tablica)
    {
        DBodgovorLista lista = new DBodgovorLista();
        try(Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(data.dajSelectNaredbu(tablica));)
        {
            if(rs!=null)
                lista = dajObjekte(rs, data);
        }
        catch (SQLException ex)
        {
            System.out.println("Greska kod dohvata podataka");
            lista.setError(Greske.SQLex, "SELECT. "+ex.getLocalizedMessage());
        }
        
        return lista;
    }

    private <T extends DBdata> DBodgovorLista dajObjekte(final ResultSet rs, DBdata data) throws SQLException
    {
        DBodgovorLista lista = new DBodgovorLista();
        while(rs.next())
        {
            T obj = data.pretvoriUObjekt(rs);
            if(obj!=null)
                lista.add(obj);
            else
            {
                lista.setError(Greske.PodaciUObj, "Pretvaranje podataka u objekt");
            }    
        }
        
        return lista;
    }

    DBodgovor izvrsiSelectWhere(Connection con, DBdata data, String tablica, String id)
    {
        DBodgovor obj = new DBodgovor();
        try(Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(data.dajSelectNaredbu(tablica, id));)
        {
            if(rs!=null)
                obj = dajObjekt(rs, data);
        }
        catch (SQLException ex)
        {
            System.out.println("Greska kod dohvata podataka");
            obj.setError(Greske.SQLex, "SELECT. "+ex.getLocalizedMessage());
        }
        
        return obj;
    }
    
    DBodgovorLista izvrsiSelectWhere(Connection con, DBdata data, String tablica, WherePart where)
    {
        DBodgovorLista obj = new DBodgovorLista();
        try(Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(data.dajSelectNaredbu(tablica, where));)
        {
            if(rs!=null)
                obj = dajObjekte(rs, data);
        }
        catch (SQLException ex)
        {
            System.out.println("Greska kod dohvata podataka");
            obj.setError(Greske.SQLex, "SELECT. "+ex.getLocalizedMessage());
        }
        
        return obj;
    }

    DBodgovor izvrsiUpis(Connection con, DBdata data, String tablica)
    {
        String sqlInsert = data.dajInsertNaredbu(tablica);
        
        DBodgovor result = new DBodgovor();
        try(Statement s = con.createStatement();)
        {
           if(s.executeUpdate(sqlInsert)>0)
            {
               result.setResult(data);
               result.setJednostavnaRadnjaUspjela(Boolean.TRUE);
            }
           else
               result.setJednostavnaRadnjaUspjela(Boolean.FALSE);
        }
        catch (SQLException ex)
        {
            result.setError(Greske.SQLex, "INSERT. "+ex.getLocalizedMessage());
        }
        return result;
    }

    <T extends DBdata> DBodgovor izvrsiUpdateVremena(Connection con, DBdata data, String tablica, String id)
    {
        String sqlUpdate = data.dajUpdateVremena(tablica, id);
        DBodgovor result = new DBodgovor();
                
        try(Statement s = con.createStatement();)
        {
           if(s.executeUpdate(sqlUpdate)>0)
           {
               result.setResult(data);
               result.setJednostavnaRadnjaUspjela(Boolean.TRUE);              
           }
           else
               result.setJednostavnaRadnjaUspjela(Boolean.FALSE);
        }
        catch (SQLException ex)
        {
            result.setError(Greske.SQLex, "UPDATE. "+ex.getLocalizedMessage());
        }
        
        return result;
    }

    private <T extends DBdata> DBodgovor dajObjekt(ResultSet rs, DBdata data) throws SQLException
    {
        DBodgovor obj = new DBodgovor();
        if(rs.next())
            obj.setResult(data.pretvoriUObjekt(rs));
        
        return obj;
    }

    DBodgovor izvrsiDelete(Connection con, DBdata data, String tablica)
    {
        String sqlDelete = data.dajDeleteNaredbu(tablica);
        DBodgovor result = new DBodgovor();
        
        try(Statement s = con.createStatement();)
        {
           if(s.executeUpdate(sqlDelete)>0)
           {
               result.setResult(data);
              result.setJednostavnaRadnjaUspjela(Boolean.TRUE); 
           } 
           else
               result.setJednostavnaRadnjaUspjela(Boolean.FALSE);
        }
        catch (SQLException ex)
        {
            result.setError(Greske.SQLex, "DELETE. "+ex.getLocalizedMessage());
        }
        return result;
    }

    DBodgovor izvrsiUpdate(Connection con, DBdata data, String tablica)
    {
        String sqlUpdate = data.dajUpdateNaredbu(tablica);
        DBodgovor result = new DBodgovor();
        
        try(Statement s = con.createStatement();)
        {
           if(s.executeUpdate(sqlUpdate)>0)
           {
               result.setResult(data);
               result.setJednostavnaRadnjaUspjela(Boolean.TRUE);
           }
           else
               result.setJednostavnaRadnjaUspjela(Boolean.FALSE);
        }
        catch (SQLException ex)
        {
            result.setJednostavnaRadnjaUspjela(Boolean.FALSE);
            result.setError(Greske.SQLex, "UPDATE. "+ex.getLocalizedMessage());
        }
        return result;
    }

    int izvrsiCount(Connection con, String tablica, WherePart where, String toCount)
    {
        String sqlCount = String.format("SELECT COUNT('%s') AS count FROM %s WHERE %s", 
                toCount, tablica, where.getWhere());
        try(Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(sqlCount);)
        {
            int returnMe = 0;
            if(rs.next())
                returnMe = rs.getInt("count");
            
            return returnMe;
        }
        catch (Exception ex)
        {
            return 0;
        }
    }

    String izvrsiSelectMax(Connection con, String tablica, String column)
    {
        String sql = String.format("SELECT MAX(%s) AS 'max' FROM %s", column, tablica);
        try(Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);)
        {
            String returnMe = "";
            if(rs!=null && rs.next())
                returnMe = rs.getString("max");
            
            return returnMe;
        }
        catch (Exception ex)
        {
            System.out.println("Greska kod dohvata podataka MAX("+column+")");
            return "";
        }
    }
    
    String izvrsiSelectMax(Connection con, String tablica, String column, WherePart where)
    {
        String sql = String.format("SELECT MAX(%s) AS 'max' FROM %s WHERE %s", 
                column, tablica, where.getWhere());
        try(Statement s = con.createStatement();
            ResultSet rs = s.executeQuery(sql);)
        {
            String returnMe = "";
            if(rs!=null && rs.next())
                returnMe = rs.getString("max");
            
            return returnMe;
        }
        catch (Exception ex)
        {
            System.out.println("Greska kod dohvata podataka MAX("+column+")");
            return "";
        }
    }
    
}
