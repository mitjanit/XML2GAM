package xml2gam;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotSpot {
    
    public static void connectHotSpot(){
        try {
            Connection conn = null;
            
            conn = DriverManager.getConnection("jdbc:mysql://10.216.189.241/radius?zeroDateTimeBehavior=convertToNull&serverTimezone=UTC" +
                            "&user=radius&password=radpass");
            
            System.out.println("CONNECTAT CORRECTAMENT A LA BBDD MySQL DEL HOTSPOT.");
            Statement stmt = null;
            stmt = conn.createStatement();
        
        } catch (SQLException ex) {
            Logger.getLogger(HotSpot.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
    }
    
    }

   /**
     * @param dept si es un alumne posam el curs (eso1a, batx1a, etc); si es un professor posam professor
     * @param grup indica el grup de radius al que s'ha d'afegir: ALU o PROFE
     */
public static void addUser(String usuari,String pwd,String firstname,String lastname,String dept, String grup){
    
    try {
            Connection conn = null;
            
            conn = DriverManager.getConnection("jdbc:mysql://10.216.189.241/radius?zeroDateTimeBehavior=convertToNull&serverTimezone=UTC" +
                            "&user=radius&password=radpass");
            
            Statement stmt = null;
            stmt = conn.createStatement();
            
            String query="INSERT INTO userinfo (username, firstname, lastname, department,creationdate, creationby)  VALUES ('"+usuari+"','"+ firstname+"','"+ lastname+"','"+ dept+"', now(), 'administrator')";
            stmt.executeUpdate(query);

            query ="INSERT INTO radcheck (id,Username,Attribute,op,Value)  VALUES (0,'"+ usuari+"','Cleartext-Password', ':=',creapass())";
            stmt.executeUpdate(query);
            
            
            query="insert into radusergroup (username, groupname,priority) values('"+usuari+"','"+grup+"',1)";
            stmt.executeUpdate(query);
            
            conn.close();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(HotSpot.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
    }
    
}   

}