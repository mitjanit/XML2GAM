
package xml2gam;


public class GOrg {
    
    String id, name, idPare, path, pathPare, descripcio;
    
    
    GOrg(String id, String name, String idPare, String path){
        
        this.id = id;
        this.name = name;
        this.idPare = idPare;
        this.path = path;
    }
    
    //new GOrg(orgId, nom, parentId, orgPath, parentPath);
    GOrg(String id, String name, String desc, String idPare, String path, String parePath){
        
        this.id = id;
        this.name = name;
        this.descripcio = desc;
        this.idPare = idPare;
        this.path = path;
        this.pathPare = parePath;
    }
    
    @Override
    public String toString(){
        return path+"\t"+id+"\t"+name+"\t"+idPare;
    }
    
}
