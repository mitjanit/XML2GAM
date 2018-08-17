
package xml2gam;

/**
 * Classe Departament per a guardar la informació dels departaments extrets del fitxer XML del Xestib.
 * @author IESManacor
 * @version 2018.6
 */

public class Departament {
    
    /**
     * Codi de Xestib del Departament.
     * <p>Exemple: 1130.</p>
     */
    public String codi;
    /**
     * Nom del Departament.
     * <p>Exemple: Administració (F.P.)</p>
     */
    public String descripcio;
    
    /**
     * Constructor de la classe Departament.
     * @param codi Codi del departament.
     * @param Descripcio Descripció del departament.
     */
    public Departament(String codi, String Descripcio){
        this.codi = codi;
        this.descripcio = descripcio;
    }
    
    /** Impressió de la informació del Departament.
     * Concatena en un String el codi i la descripció del departament.
     * @return Informació del Departament.
     */
    @Override
    public String toString(){
        return codi+"\t"+descripcio;
    }
}
