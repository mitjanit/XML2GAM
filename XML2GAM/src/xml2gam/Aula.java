
package xml2gam;

/**
 * Classe per guardar la informació de les Aules extretes del fitxer XML del Xestib.
 * @author IESManacor
 */
public class Aula {
    
    public String codi;
    public String descripcio;
    
    /**
     * Constructor de la classe Aula.
     * @param codi Codi Xestib numèric de l'aula
     * @param desc Descripció de l'Aula (ex: B009)
     */
    public Aula(String codi, String desc){
        this.codi = codi;
        this.descripcio = desc;
    }
    
    /**
     * Imprimeix la informació (codi i descripció) de l'aula.
     * @return 
     */
    @Override
    public String toString(){
        return codi+"\t"+descripcio;
    }
    
}
