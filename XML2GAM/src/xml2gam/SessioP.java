
package xml2gam;

/** Classe per a guardar la informació referent de les sessions del professorat.
 * Informació extreta del fitxer XML del Xestib.
 * @author IESManacor
 */
public class SessioP {
    
    String codiProfessor; //"K3UA4146DXSNEG0X040XR7BB5J37AZ6J"
    String codiCurs; //="94"
    String codiGrup; //"370703"
    
    String dia; //="2"
    String hora; //="09:50"
    String durada="55";
    
    String codiAula; //="19893"
    String codiSubmateria; //="1317842"
    String codiActivitat; //=""          
     
    String placa; //="10260"
    
    SessioP(String codiP, String codiC, String codiG, String dia, String h, String dura, String codiA, String codiMat, String codiAct, String p){
        this.codiProfessor = codiP;
        this.codiCurs = codiC;
        this.codiGrup = codiG;
        this.dia = dia;
        this.hora = h;
        this.durada = dura;
        this.codiAula = codiA;
        this.codiSubmateria = codiMat;
        this.codiActivitat = codiAct;
        this.placa = p;
    }
    
    
    @Override
    public String toString(){
        return codiProfessor+"\t"+codiCurs+"\t"+codiGrup;
    }
    
}
