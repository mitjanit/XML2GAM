

package xml2gam;

public class Professor {
    
    String codi;                //Codi Xestib
    String nom, ap1, ap2;       // Nom i llinatges (Xestib)
    String usuari;              // Usuari Xestib
    String departament;         // Codi Departament
    
    Grup  tutorGrup;
    
    // Constructor
    
    Professor(String codi, String nom, String ap1, String ap2, String usuari, String departament){
        this.codi = codi;
        this.nom = nom;
        this.ap1 = ap1;
        this.ap2 = ap2;
        this.usuari = usuari;
        this.departament = departament;
        this.tutorGrup=null;
    }
    
    Professor(String codi, String nom, String ap1, String ap2, String usuari, String departament, Grup tutor){
        this.codi = codi;
        this.nom = nom;
        this.ap1 = ap1;
        this.ap2 = ap2;
        this.usuari = usuari;
        this.departament = departament;
        this.tutorGrup=tutor;
    }
    
    
    // Setters
    
    void setCodi(String codi){
        this.codi = codi;
    }
    
    void setNomComplet(String nom, String ap1, String ap2){
        this.nom = nom;
        this.ap1 = ap1;
        this.ap2 = ap2;
    }
    
    void setDepartament(String dep){
        this.departament = dep;
    }
    
    // Getters
    
    String getCodi(){
        return this.codi;
    }
    
    String getNomComplet(){
        return nom+" "+ap1+" "+ap2;
    }
    
    Grup getGrupTutor(){
        return tutorGrup;
    }
    
    @Override
    public String toString(){
        String tutor = (tutorGrup!=null)? tutorGrup.toString() : "No Tutor";
        return codi+", "+nom+" "+ap1+" "+ap2+", "+usuari+", "+departament+" "+tutor;
    }
    
}
