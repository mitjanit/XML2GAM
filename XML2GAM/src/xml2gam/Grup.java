
package xml2gam;


public class Grup {
    
    String nomCanonic;
    String codi;
    String nom;
    String tutor;
    
    String curs;
    String descripcio;
    
    Grup(String codi, String nomc, String nom, String tutor, String curs, String cursDescripcio){
        this.codi = codi;
        this.nomCanonic = nomc;
        this.nom = nom;
        this.tutor = tutor;
        this.curs = curs;
        this.descripcio = cursDescripcio;
    }
    
    @Override
    public String toString(){
        return codi+"\t"+nomCanonic+"\t"+nom+"\t"+descripcio+"\t"+tutor;
    }
    
}
