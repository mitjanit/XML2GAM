
package xml2gam;

import java.util.ArrayList;

public class GUser {
    
    String email;
    String firstName, lastName;
    String codiXestib;
    String orgUnitPath;
    ArrayList<String> grups;
    boolean administrador;
    boolean suspended;
    
    GUser(String email, String fname, String lname, String xestib, String org, ArrayList<String> grups, boolean admin, boolean sus){
        this.email = email;
        this.firstName = fname;
        this.lastName = lname;
        this.codiXestib = xestib;
        this.orgUnitPath = org;
        this.administrador = admin;
        this.grups = grups;
        this.suspended = sus;
    }
    
    
    ArrayList<String> getGrups(){
        return grups;
    }
    
    @Override
    public String toString(){
        String admin = (administrador)? "Sí" : "No";
        String gs ="";
        for(int i=0; i<grups.size(); i++){
            gs += grups.get(i) + ((i<grups.size()-1)? ",":"");
        }
        return email+"\t"+firstName+"\t"+lastName+"\t"+codiXestib+"\t"+orgUnitPath+"\t"+admin+"\t"+suspended+"\t"+gs;
    }
    
}
