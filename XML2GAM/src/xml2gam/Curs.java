
package xml2gam;

import java.util.ArrayList;

public class Curs {
    
    String codi;
    String descripcio;
    
    ArrayList<Grup> grups;
    
    Curs(String codi, String descripcio){
        this.codi = codi;
        this.descripcio = descripcio;
        this.grups = new ArrayList<Grup>();
    }
    
    void afegirGrup(String codi, String nomC, String nom, String tutor){
        Grup g = new Grup(codi, nomC, nom, tutor, this.codi, this.descripcio);
        this.grups.add(g);
    }
}
