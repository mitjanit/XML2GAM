
package xml2gam;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLReader {
    
    Document doc;
           
    // Crea l'object Doc per a parsear el fitxer XML
    XMLReader(String filePath) {
        File f = new File(filePath);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            this.doc = db.parse(f);
        } catch (ParserConfigurationException |SAXException | IOException ex) {
            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.doc.getDocumentElement().normalize();
            
        Attr aCodi = doc.getDocumentElement().getAttributeNode("codi");
        String codiCentre = aCodi.getValue();
        System.out.println("LLegint el XML del Centre: " + codiCentre);
    }
    
    // Retorna els professors del fitxer XML
    ArrayList<Professor> llegirProfes(){
        
        ArrayList<Professor> profes = new ArrayList<Professor>();
        NodeList nodes = doc.getElementsByTagName("PROFESSOR");
        
        int numProfes = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació dels " + numProfes + " professors.");
            System.out.println("\n\tCodi\tNom\tAp1\tAp2");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eProfe = (Element) node;
                String codi = eProfe.getAttribute("codi");
                String nom = eProfe.getAttribute("nom");
                String ap1 = eProfe.getAttribute("ap1");
                String ap2 = eProfe.getAttribute("ap2");
                String usuari = eProfe.getAttribute("username");
                String departament = eProfe.getAttribute("departament");
                
                if(XML2GAM.DEBUG && departament.length()==0){
                    System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + usuari+ "\t" + departament);
                    System.out.println("ALERTA! El professor/a no té departament!!!");
                }
                
                Professor p = new Professor(codi, nom, ap1, ap2, usuari, departament);
                profes.add(p);
                
                
            }
        }
        
    return profes;   
}
    
    /**
     * Retorna una col·lecció de dades amb les sessions dels professors del fitxerXML.
     * @return Col·lecció de dades de les sessions del professors.
     */ 
    ArrayList<SessioP> llegirSessionsProfes(){
        
        ArrayList<SessioP> sessions = new ArrayList<SessioP>();
        
        NodeList parent = doc.getElementsByTagName("HORARIP");
        
        Node n = parent.item(0);
        
        NodeList nodes = n.getChildNodes();
        
        int numSessionsP = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació de les " + numSessionsP + " sessions de professors.");
            System.out.println("\tcodiProfe\tcodiGrup\tcodiCurs\tDia\tHora\tDuradatAula\tSubmateria\tActivitat\tPlaca");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("SESSIO")) {
                Element eSessio = (Element) node;
                String codiProfe = eSessio.getAttribute("professor");
                String codiGrup = eSessio.getAttribute("grup");
                String codiCurs = eSessio.getAttribute("curs");
                String dia = eSessio.getAttribute("dia");
                String hora = eSessio.getAttribute("hora");
                String durada = eSessio.getAttribute("durada");
                String aula = eSessio.getAttribute("aula");
                String submateria = eSessio.getAttribute("submateria");
                String activitat = eSessio.getAttribute("activitat");
                String placa = eSessio.getAttribute("placa");
                
                
                if(XML2GAM.DEBUG){
                    System.out.println("\t" + codiProfe + "\t" + codiGrup + "\t" + codiCurs + "\t" + dia + "\t" + hora+ "\t" + durada + "\t" + aula +"\t" + submateria + "\t" + activitat + "\t" + placa);
                }
                
                SessioP s = new SessioP(codiProfe, codiCurs, codiGrup, dia, hora, durada, aula, submateria, activitat, placa);

                sessions.add(s);
                
                
            }
        }

        return sessions;
    }
    
    ArrayList<SessioP> llegirSessionsDocentsProfes(){
        
        ArrayList<SessioP> sessions = new ArrayList<SessioP>();
        
        NodeList parent = doc.getElementsByTagName("HORARIP");
        
        Node n = parent.item(0);
        
        NodeList nodes = n.getChildNodes();
        
        int numSessionsP = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació de les " + numSessionsP + " sessions de professors.");
            System.out.println("\tcodiProfe\tcodiGrup\tcodiCurs\tDia\tHora\tDuradatAula\tSubmateria");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("SESSIO")) {
                Element eSessio = (Element) node;
                String codiProfe = eSessio.getAttribute("professor");
                String codiGrup = eSessio.getAttribute("grup");
                String codiCurs = eSessio.getAttribute("curs");
                String dia = eSessio.getAttribute("dia");
                String hora = eSessio.getAttribute("hora");
                String durada = eSessio.getAttribute("durada");
                String aula = eSessio.getAttribute("aula");
                String submateria = eSessio.getAttribute("submateria");
                String activitat = eSessio.getAttribute("activitat");
                String placa = eSessio.getAttribute("placa");
                
                SessioP s = new SessioP(codiProfe, codiCurs, codiGrup, dia, hora, durada, aula, submateria, activitat, placa);
                
                if(codiGrup.length()>0 && codiCurs.length()>0 && submateria.length()>0){
                    sessions.add(s);
                    
                    if(XML2GAM.DEBUG){
                        System.out.println("\t" + codiProfe + "\t" + codiGrup + "\t" + codiCurs + "\t" + dia + "\t" + hora+ "\t" + durada + "\t" + aula +"\t" + submateria);
                    }
                }
               
            }
        }

        return sessions;
    }
    
    ArrayList<SessioP> llegirSessionsNoDocentsProfes(){
        
        ArrayList<SessioP> sessions = new ArrayList<SessioP>();
        
        NodeList parent = doc.getElementsByTagName("HORARIP");
        
        Node n = parent.item(0);
        
        NodeList nodes = n.getChildNodes();
        
        int numSessionsP = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació de les " + numSessionsP + " sessions de professors no docents.");
            System.out.println("\tcodiProfe\tDia\tHora\tDurada\tActivitat");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("SESSIO")) {
                Element eSessio = (Element) node;
                String codiProfe = eSessio.getAttribute("professor");
                String codiGrup = eSessio.getAttribute("grup");
                String codiCurs = eSessio.getAttribute("curs");
                String dia = eSessio.getAttribute("dia");
                String hora = eSessio.getAttribute("hora");
                String durada = eSessio.getAttribute("durada");
                String aula = eSessio.getAttribute("aula");
                String submateria = eSessio.getAttribute("submateria");
                String activitat = eSessio.getAttribute("activitat");
                String placa = eSessio.getAttribute("placa");
                
                SessioP s = new SessioP(codiProfe, codiCurs, codiGrup, dia, hora, durada, aula, submateria, activitat, placa);
                
                if(activitat.length()>0){
                    sessions.add(s);
                    
                    if(XML2GAM.DEBUG){
                        System.out.println("\t" + codiProfe + "\t" + dia + "\t" + hora+ "\t" + durada + "\t" + activitat);
                    }
                }
               
            }
        }

        return sessions;
    }
    
    ArrayList<SessioA> llegirSessionsAlumnes(){
        
        ArrayList<SessioA> sessions = new ArrayList<SessioA>();
        
        NodeList parent = doc.getElementsByTagName("HORARIA");
        
        Node n = parent.item(0);
        
        NodeList nodes = n.getChildNodes();
        
        int numSessionsA = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació de les " + numSessionsA + " sessions d'alumnes.");
            System.out.println("\tcodiAlumne\tDia\tHora\tDurada\tAula\tSubmateria");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("SESSIO")) {
                Element eSessio = (Element) node;
                String codiAlu = eSessio.getAttribute("alumne");
                String dia = eSessio.getAttribute("dia");
                String hora = eSessio.getAttribute("hora");
                String durada = eSessio.getAttribute("durada");
                String aula = eSessio.getAttribute("aula");
                String submateria = eSessio.getAttribute("submateria");
                
                SessioA s = new SessioA(codiAlu, dia, hora, durada, aula, submateria);
                sessions.add(s);
                    
                if(XML2GAM.DEBUG){
                    System.out.println("\t" + codiAlu + "\t" + dia + "\t" + hora+ "\t" + durada + "\t" + aula +"\t" + submateria);
                }
            }
        }

        return sessions;
    }
    
    // Retrona els tutors del fitxer XML
    ArrayList<Professor> llegirProfesTutors(ArrayList<Grup> grups){
        
        ArrayList<Professor> profes = new ArrayList<Professor>();
        NodeList nodes = doc.getElementsByTagName("PROFESSOR");
        
        int numProfes = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació dels " + numProfes + " professors.");
            System.out.println("\n\tCodi\tNom\tAp1\tAp2");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eProfe = (Element) node;
                String codi = eProfe.getAttribute("codi");
                String nom = eProfe.getAttribute("nom");
                String ap1 = eProfe.getAttribute("ap1");
                String ap2 = eProfe.getAttribute("ap2");
                String usuari = eProfe.getAttribute("username");
                String departament = eProfe.getAttribute("departament");
                
                if(XML2GAM.DEBUG && departament.length()==0){
                    System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + usuari+ "\t" + departament);
                    System.out.println("ALERTA! El professor/a no té departament!!!");
                }
                
                Grup tutorGrup = null;
                for(Grup g: grups){
                    if(g.tutor.equalsIgnoreCase(codi)){
                        tutorGrup = g;
                        break;
                    }
                }
                
                Professor p = new Professor(codi, nom, ap1, ap2, usuari, departament, tutorGrup);
                profes.add(p);
            }
        }
        
        return profes;   
    }
    
    // Retorna els alumnes del fitxer XML
    ArrayList<Alumne> llegirAlumnes(ArrayList<Grup> grups){
        
        ArrayList<Alumne> alus = new ArrayList<Alumne>();
        NodeList nodes = doc.getElementsByTagName("ALUMNE");
        
        int numAlumnes = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació dels " + numAlumnes + " alumnes.");
            System.out.println("\n\tCodi\tNom\tAp1\tAp2\tExpedient\tGrup");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eAlumne = (Element) node;
                String codi = eAlumne.getAttribute("codi");
                String nom = eAlumne.getAttribute("nom");
                String ap1 = eAlumne.getAttribute("ap1");
                String ap2 = eAlumne.getAttribute("ap2");
                String exp = eAlumne.getAttribute("expedient");
                String grup = eAlumne.getAttribute("grup");
                
                Grup g = getGrupAlumne(grups, grup);
                
                if(XML2GAM.DEBUG){
                    System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + exp+ "\t" + grup);
                    
                }
                if(g==null){
                    System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + exp+ "\t" + grup);
                    System.out.println("ALERTA! l'alumne/a no té grup!!!");
                }
                
                Alumne a = new Alumne(codi, nom, ap1, ap2, exp, grup);
                a.setGrup(g);
                alus.add(a);
            }
        }
        
        return alus;   
    }

    // Retorna els alumnes del grup indicat del fitxer XML (Emprar nomCanonic)
    /**
     * Retorna una col·lecció amb la informació dels alumnes que pertanyen a un grup llegides del fitxer XML del Xestib.
     * @param grups Col·lecció de dades dels grups/cursos.
     * @return Col·lecció de dades d'alumnes.
     */
    public ArrayList<Alumne> llegirAlumnesGrup(ArrayList<Grup> grups, String nomCanonicGrup){
        
        ArrayList<Alumne> alus = new ArrayList<Alumne>();
        NodeList nodes = doc.getElementsByTagName("ALUMNE");
        
        String codiGrup = XML2GAM.getCodiGrup(grups, nomCanonicGrup);

        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació dels alumnes del curs: "+nomCanonicGrup+".");
            System.out.println("\n\tCodi\tNom\tAp1\tAp2\tExpedient\tGrup");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eAlumne = (Element) node;
                String codi = eAlumne.getAttribute("codi");
                String nom = eAlumne.getAttribute("nom");
                String ap1 = eAlumne.getAttribute("ap1");
                String ap2 = eAlumne.getAttribute("ap2");
                String exp = eAlumne.getAttribute("expedient");
                String grup = eAlumne.getAttribute("grup");
                
                if(grup.equals(codiGrup)){
                
                    Grup g = getGrupAlumne(grups, grup);

                    if(XML2GAM.DEBUG){
                        System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + exp+ "\t" + grup);
                    }
                    if(g==null){
                        System.out.println("\t" + codi + "\t" + nom + "\t" + ap1 + "\t" + ap2 + "\t" + exp+ "\t" + grup);
                        System.out.println("ALERTA! l'alumne/a no té grup!!!");
                    }

                    Alumne a = new Alumne(codi, nom, ap1, ap2, exp, grup);
                    a.setGrup(g);
                    alus.add(a);
                }
            }
        }
        
        return alus;   
    }

    /**
     * Retorna una col·lecció amb la informació dels grups llegits del fitxer XML del Xestib.
     * @return Col·lecció de dades de grups.
     */
    public ArrayList<Grup> llegirGrups(){
        
        ArrayList<Grup> grups = new ArrayList<Grup>();
        NodeList nodes = doc.getElementsByTagName("CURS");
         
        int numCurs = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació dels " + numCurs + " Cursos.");
            System.out.println("\tCodi\tDescripcio");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eCurs = (Element) node;
                String codiCurs = eCurs.getAttribute("codi");
                String descCurs = eCurs.getAttribute("descripcio");

                Curs c = new Curs(codiCurs,descCurs);

                // Grup
                NodeList nodesGrup = eCurs.getElementsByTagName("GRUP");
                int numNodesGrup = nodesGrup.getLength();
                
                if(XML2GAM.DEBUG){
                    System.out.println("\t\tGrups del Curs: " + numNodesGrup);
                    System.out.println("\t\tCodi\tNom\tTutor");
                }
                
                for (int j = 0; j < nodesGrup.getLength(); j++) {
                    Node node2 = nodesGrup.item(j);
                    if (node2.getNodeName().equals("GRUP")) {
                        Element eGrup = (Element) node2;
                        String codiGrup = eGrup.getAttribute("codi");
                        String nomGrup = eGrup.getAttribute("nom");
                        String nomC = (grupCase(descCurs) + nomGrup).toLowerCase();
                        String tutorGrup = eGrup.getAttribute("tutor");
                        if (tutorGrup.equals("")) {
                            tutorGrup = "NULL";
                        }
                        if(XML2GAM.DEBUG){
                            System.out.println("\t\t" + codiGrup + "\t" + nomGrup + "\t" + tutorGrup);
                        }
                        
                        Grup g = new Grup(codiGrup, nomC, nomGrup, tutorGrup, codiCurs, descCurs);
                        grups.add(g);
                    }
                }

            }
        }
        return grups;
    }
    

    /**
     * Retorna una col·lecció amb la informació dels departaments llegits del fitxer XML del Xestib.
     * @return Col·lecció de dades de departaments.
     */
    ArrayList<Departament> llegirDepartaments(){
        
        ArrayList<Departament> deps = new ArrayList<Departament>();
        NodeList nodes = doc.getElementsByTagName("DEPARTAMENT");
        
        int numDeps = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació dels " + numDeps + " departaments.");
            System.out.println("\n\tCodi\tDescripcio");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eDep = (Element) node;
                String codi = eDep.getAttribute("codi");
                String descripcio = eDep.getAttribute("descripcio");
                if(XML2GAM.DEBUG){
                    System.out.println("\t" + codi + "\t" + descripcio);
                }
                
                Departament d = new Departament(codi, descripcio);
                deps.add(d);
            }
        }
        
    return deps;   
}
    

    /**
     * Retorna tota la informació d'un grup a partir del seu codi.
     * @param grups Col·lecció de dades amb la informació de tots els grups.
     * @param codiGrup Codi Xestib del grup.
     * @return Informació del grup.
     */
    Grup getGrupAlumne(ArrayList<Grup> grups, String codiGrup){
        for(Grup g : grups){
            if(g.codi.equalsIgnoreCase(codiGrup)){
                return g;
            }
        }
        return null;
    }
    

    /**
     * Retorna una col·lecció amb la informació de les aules llegides del fitxer XML del Xestib.
     * @return Col·lecció de dades d'aules.
     */
    ArrayList<Aula> llegirAules(){
        
        ArrayList<Aula> aules = new ArrayList<Aula>();
        NodeList nodes = doc.getElementsByTagName("AULA");
        
        int numAules = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació de les " + numAules + " aules.");
            System.out.println("\n\tCodi\tDescripcio");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eDep = (Element) node;
                String codi = eDep.getAttribute("codi");
                String descripcio = eDep.getAttribute("descripcio");
                if(XML2GAM.DEBUG){
                    System.out.println("\t" + codi + "\t" + descripcio);
                }
                
                Aula a = new Aula(codi, descripcio);
                aules.add(a);
            }
        }
        
    return aules;   
}
    

    /**
     * Retorna una col·lecció amb la informació de les matèries llegides del fitxer XML del Xestib.
     * @return Col·lecció de dades de matèries.
     */
    ArrayList<Submateria> llegirMateries(){
        
        ArrayList<Submateria> materies = new ArrayList<Submateria>();
        NodeList nodes = doc.getElementsByTagName("SUBMATERIA");
        
        int numMateries = nodes.getLength();
        if(XML2GAM.DEBUG){
            System.out.println("\n\tInformació de les " + numMateries + " matèries.");
            System.out.println("\n\tCodi\tCurs\tDescripcio\tCurta");
        }

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eDep = (Element) node;
                String codi = eDep.getAttribute("codi");
                String curs = eDep.getAttribute("curs");
                String descripcio = eDep.getAttribute("descripcio");
                String curta = eDep.getAttribute("curta");
                if(XML2GAM.DEBUG){
                    System.out.println("\t" + codi + "\t" + curs + "\t" + descripcio + "\t" + curta);
                }
                
                Submateria s = new Submateria(codi, curs, descripcio, curta);
                materies.add(s);
            }
        }
        
    return materies;   
}
    
    
    
   //++++++++++++++++++ Funcions de Format +++++++++++++++++++++++++//
    
    
    String grupCase(String descCurs) {
        switch (descCurs) {
            case "Ed. Especial: Bàsica":
                return "esoee";
            case "1r ESO":
                return "eso1";
            case "2n ESO":
                return "eso2";
            case "3r ESO":
                return "eso3";
            case "4t ESO":
                return "eso4";
            case "1r Batx.":
                return "batx1";
            case "2n batx.":
                return "batx2";
            default:
                return descCurs;
        }
    }
    
    String remove_accents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String accentRemoved = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return accentRemoved;
    }
    
        
}
