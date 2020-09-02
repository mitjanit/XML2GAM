
package xml2gam;

/**
 * Classe Alumne per a guardar la informació dels alumnes extreta del fitxer XML del Xestib.
 * @author IESManacor
 * @version 2018.6
 */

public class Alumne {
    
    /** 
     * Codi XESTIB de l'alumne.
     * <p> Exemple: 676716273672613712 </p>
     * */
    public String codi;
    
    /**
     * Nom de l'alumne.
     */
    public String nom;
    
    /**
     * Primer llinatge de l'alumne.
     */
    public String ap1;
    
    /**
     * Segon llinatge de l'alumne.
     */
    public String ap2;
    
    /**
     * Número d'Expedient de l'alumne.
     */
    public String expedient;
    
    /**
     * Codi del Grup al que pertany l'alumne.
     */
    public String codiGrup;
    Grup grup;
    
    /**
     * Constructor de la classe Alumne.
     * @param codi Codi XESTIB de l'alumne
     * @param nom   Nom de l'alumne.
     * @param ap1   Primer llinatge de l'alumne.
     * @param ap2   Segon llinatge de l'alumne.
     * @param exp   Número d'Expedient de l'alumne.
     * @param grup  Codi del Grup al que pertany l'alumne.
     */
    public Alumne(String codi, String nom, String ap1, String ap2, String exp, String grup){
        this.codi = codi;
        this.nom = nom;
        this.ap1 = ap1;
        this.ap2 = ap2;
        this.expedient = exp;
        this.codiGrup = grup;
    }
    
    /**
     * Estableix el grup Grup al que pertany l'alumne.
     * @param g Grup de l'alumne.
     */
    public void setGrup(Grup g){
        this.grup = g;
    }
    
    /**
     * Retorna el codi XESTIB de l'alumne
     * @return Codi de l'alumne.
     */
    public String getCodi(){
        return this.codi;
    }
    
    /**
     * Retorna un String amb tota la informació de l'alumne.
     * @return Informació de l'alumne.
     */
    @Override
    public String toString(){
        //return codi+"\t"+nom+" "+ap1+" "+ap2+"\t"+expedient+"\t"+codiGrup;
        return codi+","+nom+","+ap1+","+ap2+","+expedient+","+codiGrup;
    }
}
