
package xml2gam;

/**
 * Classe per guardar la informació de les matèries del fitxer XML del Xestib.
 * @author IESManacor
 */
public class Submateria {
    
    String codi; //="1315054"
    String codiCurs; //="62"
    String descripcio; //="Biologia i geologia-A"
    String curta; //="BG-A"
    
    public Submateria(String codi, String codiCurs, String desc, String curta){
        this.codi = codi;
        this.codiCurs = codiCurs;
        this.descripcio = desc;
        this.curta = curta;
    }
    
    @Override
    public String toString(){
        return codi+"\t"+codiCurs+"\t"+descripcio+"\t"+curta;
    }
    
}
