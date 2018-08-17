package xml2gam;

/** Classe per a guardar la informació referent de les sessions dels alumnes.
 * Informació extreta del fitxer XML del Xestib.
 * @author IESManacor
 */
public class SessioA {
    
    String codiAlumne; //"K3UA4146DXSNEG0X040XR7BB5J37AZ6J"
    
    String dia; //="2"
    String hora; //="09:50"
    String durada="55";
    
    String codiAula; //="19893"
    String codiSubmateria; //="1317842"

    
    SessioA(String codiAl, String dia, String h, String dura, String codiA, String codiMat){
        this.codiAlumne = codiAl;
        this.dia = dia;
        this.hora = h;
        this.durada = dura;
        this.codiAula = codiA;
        this.codiSubmateria = codiMat;
    }
    
    
    @Override
    public String toString(){
        return codiAlumne+"\t"+dia+"\t"+hora+"\t"+durada+"\t"+codiAula+"\t"+codiSubmateria;
    }
    
}
