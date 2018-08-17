
package xml2gam;


public class GGrup {
    
    String id, name, description, email;
    
    boolean departament;
    String codiDep;
    
    public GGrup(String id, String name, String description, String email, boolean departament, String codiDep){
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.email = email;
        
        this.departament = departament;
        this.codiDep = codiDep;
    }
    
    @Override
    public String toString(){
        String dep = (departament)? "Sí": "No";
        return id+"\t"+name+"\t"+description+"\t"+email+"\t"+dep+"\t"+codiDep;
    }
    
}
