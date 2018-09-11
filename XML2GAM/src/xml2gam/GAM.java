
package xml2gam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

import static xml2gam.XML2GAM.getEmailDep;
import static xml2gam.XML2GAM.usernamesProfes;

/**
 * Conjunt de mètodes per interactuar amb GAM.
 * Operacions sobre usuaris del domini GSuite: imprimir, crear, eliminar suspendre.
 * 
 * @author IESManacor
 * @version 2018.6
 */
public class GAM {
    
    XML2GAM obj;
    ArrayList<GGrup> ggrups;
    
    GAM(XML2GAM obj){
        this.obj = obj;
    }
    

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    //++++++++++++++++++++++++ OPERACIONS SOBRE GRUPS DEL DOMINI GSUITE ++++++++++++++++++++//
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    
    /**
    * Imprimeix la informació dels grups de correu del domini GSuite. 
    * 
    */ 
    public void printGroups(){
        String command = "D:\\gam\\gam print groups id name description";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    
    /**
    * Retorna la informació dels grups de correu de tutors del domini GSuite. 
    * 
    * @return Col·lecció de dades dels grups corresponents als tutors (ESO, FP, Batxiller).
    */
    public ArrayList<GGrup> getTutors(){
        String command = "D:\\gam\\gam print groups";		
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        
        ArrayList<GGrup> grups = new ArrayList<GGrup>();
        for(int i=1; i<linies.length; i++){
            System.out.println("LLegint info del grup: "+linies[i]);
            if(linies[i].contains("tutoria.")){
                GGrup g = getGroupInfo(linies[i]);
                grups.add(g);
            }
        }
        return grups;
    }
    
    /**
    * Retorna la informació dels grups de correu dels departaments del domini GSuite. 
    * 
    * @return Col·lecció de dades dels grups corresponents als departaments.
    */
    public ArrayList<GGrup> getDeps(){
        String command = "D:\\gam\\gam print groups";		
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        
        ArrayList<GGrup> grups = new ArrayList<GGrup>();
        for(int i=1; i<linies.length; i++){
            System.out.println("LLegint info del grup: "+linies[i]);
            if(linies[i].contains("dept.")){
                GGrup g = getGroupInfo(linies[i]);
                grups.add(g);
            }
        }
        return grups;
    }
    
    /**
    * Retorna la informació de tots els grups de correu del domini GSuite. 
    * 
    * @return Col·lecció de dades dels grups del domini GSuite.
    */
    public ArrayList<GGrup> getGroups(){
        String command = "D:\\gam\\gam print groups";		
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        
        ArrayList<GGrup> grups = new ArrayList<GGrup>();
        for(int i=1; i<linies.length; i++){
            System.out.println("LLegint info del grup: "+linies[i]);
            GGrup g = getGroupInfo(linies[i]);
            grups.add(g);
        }
        return grups;
    }
    
    /**
    * Retorna la informació d'un grup de correu del domini GSuite. 
    * 
    * @param info Email del grup a consultar (ex: professors@iesmanacor.cat)
    * @return GGrup Informació del grup de correu del domini GSuite.
    */
    public GGrup getGroupInfo(String info){
        
        String command = "D:\\gam\\gam info group "+info;		
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        String id="", name="", description="", email="";
        
        for(int i=0; i<linies.length; i++){
            String linia = linies[i];
            String[] columnes = linia.split(":");
            if(columnes.length>1){
                String param = columnes[0].trim();
                switch (param) {
                    case "id":
                        id=columnes[1].trim(); //System.out.println("Llegit Id: "+columnes[1]);
                        break;
                    case "name":
                        name = columnes[1].trim(); //System.out.println("Llegit Nom: "+columnes[1]);
                        break;
                    case "description":
                        description = columnes[1].trim(); //System.out.println("Llegit Descripció: "+columnes[1]);
                        break;
                    case "email":
                        email = columnes[1].trim(); //System.out.println("Llegit Email: "+columnes[1]);
                        break;
                    default:
                        break;
                }
            }
           
        }
        
        boolean departament = name.contains("Departament");
        String codiDep=(departament)? description.substring(0, 4) : "";
        
        GGrup g = new GGrup(id, name, description, email, departament, codiDep);
        if(XML2GAM.DEBUG){
            System.out.println("Grup llegit: "+g);
        }
        return g;
    }
    
    // Ja no funciona quan hi ha més de 67 grups
    ArrayList<GGrup> getGroups2(){
        String command = "D:\\gam\\gam print groups id name description";		
	String output = obj.executeCommand(command);
        System.out.println(output);
        
        ArrayList<GGrup> grups = new ArrayList<>();

        String[] linies = output.split("\n");
        for(int i=1; i<linies.length; i++){
            if(XML2GAM.DEBUG){
                System.out.println("LLegint grup ("+i+"): "+linies[i]);
            }
            String linia = linies[i];
            String[] columnes = linia.split(",");
            String email = columnes[0];
            String id = columnes[1];
            String name = columnes[2];
            String description = columnes[3];
            boolean departament = name.contains("Departament");
            String codiDep=(departament)? description.substring(0, 4) : "";
            GGrup g = new GGrup(id, name, description, email, departament, codiDep);
            grups.add(g);
        }

        return grups;
    }
    
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    //+++++++++++++ OPERACIONS SOBRE UNITATS ORGANITZATIVES DEL DOMINI GSUITE ++++++++++++++//
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
    
    
    /**
    * Crea una unitat organitzativa en el domini GSuite. 
    * 
    * @param nom Nom de la unitat organitzativa (ex:professors, alumnes, eso, eso1, batx1, ...).
    * @param desc Descripción de la unitat organitzativa.
    * @param pare Nom de la unitat organitzativa pare (ex:/ o alumnes)
    */
    public void creaUnitatOrg(String nom, String desc, String pare){
        // gam create org <name> [description <Description>] [parent <Parent Org>]
        String command = "D:\\gam\\gam create org \""+nom+"\" description \""+desc+"\" parent /"+pare+" ";	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Imprimeix la informació de les unitats organitzatives del domini GSuite. 
    * 
    */ 
    public void printUnits(){
        String command = "D:\\gam\\gam print orgs";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Retorna totes les Unitats Organitzatives del domini GSuite. 
    * 
    * @return Col·lecció de les unitats organitzatives en el domini GSuite. 
    */  
    public ArrayList<GOrg> getUnits(){
        
        String command = "D:\\gam\\gam print orgs";		
	String output = obj.executeCommand(command);
        System.out.println(output);
        
        ArrayList<GOrg> orgs = new ArrayList<>();
        
        String[] linies = output.split("\n");
        for(int i=1; i<linies.length; i++){
            if(XML2GAM.DEBUG){
                System.out.println("LLegint Unitat Org. ("+i+"): "+linies[i]);
            }
            String linia = linies[i];
            String[] columnes = linia.split(",");
            String path = columnes[0];
            String id = columnes[1];
            String name = columnes[2];
            String parent = (columnes.length>3)? columnes[3]: "";
            GOrg o = new GOrg(id, name, parent, path);
            orgs.add(o);
        }
        return orgs;
    }
    
    // Retorna totes les Unitats Org. No funciona per més de 67 unitats!!!!
    public ArrayList<GOrg> getUnits2(){
        String command = "D:\\gam\\gam print orgs";		
	String output = obj.executeCommand(command);

        ArrayList<GOrg> orgs = new ArrayList<>();
        
        String[] linies = output.split("\n");
        for(int i=1; i<linies.length; i++){
            if(XML2GAM.DEBUG){
                System.out.println("LLegint Unitat Org. ("+i+"): "+linies[i]);
            }
            String linia = linies[i];
            String[] columnes = linia.split(",");
            String path = columnes[0];
            String id = columnes[1];
            String name = columnes[2];
            String parent = (columnes.length>3)? columnes[3]: "";
            GOrg o = new GOrg(id, name, parent, path);
            orgs.add(o);
        }
        return orgs;
    }
    
    /**
    * Retorna les Unitats Org. filles de la unitat /alumnes. 
    * 
    * @return Col·lecció de les unitats organitzatives en el domini GSuite. 
    */  
    public ArrayList<GOrg> getUnitsAlumnes(){
        return getUnits("alumnes");
    }  

    /**
    * Retorna la informació d'una Unitat Organitzativa del domini GSuite. 
    * 
    * @param  unitName  Nom de la unitat del domini GSuite (ex: alumnes, professors, batx2, o eso1).
    * @return GOrg  Informació de la unitat organitzativa en el domini GSuite. 
    */  
    public GOrg getOrgUnit(String unitName){
        String command = "D:\\gam\\gam info org /"+unitName;			
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        String nom="", path="", pathParent="",id="", idParent="", desc="";
        
        for(int i=0; i<linies.length; i++){
            String linia = linies[i];
            String[] columnes = linia.split(":");
            
            if(columnes.length>1){
                String param = columnes[0].trim();
                switch (param) {
                    case "name":
                        nom=columnes[1].trim(); //System.out.println("Llegit nom: "+columnes[1]);
                        break;
                    case "description":
                        desc = columnes[1].trim(); //System.out.println("Llegit descriupcio: "+columnes[1]);
                        break;
                    case "orgUnitId":
                        id = columnes[2].trim(); //System.out.println("Llegit Id: "+columnes[2]);
                        break;
                    case "parentOrgUnitId":
                        idParent = columnes[2].trim(); //System.out.println("Llegit id Pare: "+columnes[2]);
                        break;
                    case "parentOrgUnitPath":
                        pathParent = columnes[1].trim(); //System.out.println("Llegit Parent Path: "+columnes[1]);
                        break;
                    case "orgUnitPath":
                        path = columnes[1].trim(); //System.out.println("Llegit Path: "+columnes[1]);
                        break;
                    default:
                        break;
                }
            }
        }
        
        GOrg g = new GOrg(id, nom, desc, idParent, path, pathParent);
        return g;
        
    }
    

    /**
    * Retorna les Unitats Org. filles de la unitat pare corresponent. 
    * 
    * @param  parentUnit  Nom de la unitat org. pare en el domini GSuite (ex: alumnes, professors, batx2, o eso1).
    * @return Col·lecció de les unitats organitzatives en el domini GSuite. 
    */  
    public ArrayList<GOrg> getUnits(String parentUnit){
        
        String command = "D:\\gam\\gam print orgs";			
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        GOrg pare = getOrgUnit(parentUnit);
        
        ArrayList<GOrg> orgs = new ArrayList<>();
        
        String[] linies = output.split("\n");
        for(int i=1; i<linies.length; i++){
            if(XML2GAM.DEBUG){
                System.out.println("LLegint Unitat Org. ("+i+"): "+linies[i]);
            }
            String linia = linies[i];
            String[] columnes = linia.split(",");
            String path = columnes[0];
            String id = (columnes[1].split(":"))[1];
            String name = columnes[2];
            String parentId = (columnes.length>3)? (columnes[3].split(":"))[1]: "";
            
            GOrg o = new GOrg(id, name, parentId, path);
            if(o.idPare.equals(pare.id)){
                orgs.add(o);
            }
        }
        
        return orgs;
    }
    
    
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // +++++++++++++++++++++ OPERACIONS SOBRE USUARIS DEL DOMINI GSUITE ++++++++++++++++++++ //
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    
    /**
    * Imprimeix la informació d'un usuari del domini GSuite. 
    * 
    * @param  email  Email de l'usuari (ex: nomprofe@iesmanacor.cat o nomalumne@alumne.iesmanacor.cat ).
    */ 
    public void printUserInfo(String email){
        String command = "D:\\gam\\gam info user "+email;		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Imprimeix la informació d'un usuari del domini GSuite. 
    * 
    * @param  codiXestib  Codi Xestib de l'usuari (ex: 944BA537A676E45FE040D70A59055935 ).
    */  
    public void printUserXestib(String codiXestib){
        String command = "D:\\gam\\gam info user  query \"value="+codiXestib+"\"";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Obté la informació de l'usuari del domini GSuite. 
    * 
    * @param  info  Email de l'usuari del domini GSuite (ex: nomprofe@iesmanacor.cat o nomalumne@alumne.iesmanacor.cat).
    * @return GUser  Informació de l'usuari del domini GSuite. 
    */  
    public GUser getUserInfo(String info){
        
        String command = "D:\\gam\\gam info user "+info;		
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        String email="", firstName="", lastName="", codiXestib="", orgUnitPath="";
        ArrayList<String> grups = new ArrayList();
        boolean admin = false;
        boolean suspended = false;
        
        boolean grupsOn = false;
        for(int i=0; i<linies.length; i++){
            String linia = linies[i];
            String[] columnes = linia.split(":");
            if(columnes.length>1){
                String param = columnes[0].trim();
                switch (param) {
                    case "User":
                        email=columnes[1].trim(); //System.out.println("Llegit Email: "+columnes[1]);
                        break;
                    case "First Name":
                        firstName = columnes[1].trim(); //System.out.println("Llegit Nom: "+columnes[1]);
                        break;
                    case "Last Name":
                        lastName = columnes[1].trim(); //System.out.println("Llegit Llinatges: "+columnes[1]);
                        break;
                    case "value":
                        codiXestib = columnes[1].trim(); //System.out.println("Llegit codi Xestib: "+columnes[1]);
                        break;
                    case "Google Org Unit Path":
                        orgUnitPath = columnes[1].trim(); //System.out.println("Llegit Org.Unit: "+columnes[1]);
                        break;
                    case "Is a Super Admin":
                        admin = columnes[1].contains("True"); //System.out.println("Llegit Administrador: "+columnes[1]);
                        break;
                    case "Groups":
                        grupsOn = true; //System.out.println("Llegint Grups!");
                        break;
                    case "Account Suspended":
                        suspended = columnes[1].contains("True"); //System.out.println("Llegit Suspended: "+columnes[1]);
                        break;
                    default:
                        break;
                }
            }
            
            if(grupsOn){
                String l = linies[i];
                String[] cols = l.split("<");
                if(cols.length>1){
                    int n = cols[1].length();
                    String sg = cols[1].substring(0, n-1);
                    grups.add(sg); //System.out.println("Llegit Grup: "+sg);
                }
            }
        }
        
        GUser u = new GUser(email, firstName, lastName, codiXestib, orgUnitPath, grups, admin, suspended);
        if(XML2GAM.DEBUG){
            System.out.println("Usuari llegit: "+u);
        }
        return u;
    }
    
    /**
    * Imprimeix la informació del usuaris d'una unitat en el domini GSuite. 
    * 
    * @param  unitat  Nom de la unitat del domini GSuite (ex: /, alumnes, professors, batx2, o eso1).
    */  
    public void printUsers(String unitat){
        String command = "D:\\gam\\gam print users custom all query \"orgUnitPath=/"+unitat+"\"";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Imprimeix la informació dels professors en el domini GSuite. 
    * 
    */  
    public void printProfessors(){
        System.out.println("Membres del GSuite a la unitat /professors");
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        ArrayList<GUser> professors = getUsers("professors");
        for(GUser u : professors){
            System.out.println(u);
        }
        System.out.println(">>>>>> Total professors: "+professors.size());
    }
    
    /**
    * Imprimeix la informació dels alumnes en el domini GSuite. 
    * 
    */  
    public void printAlumnes(){
        System.out.println("Membres del GSuite a la unitat /alumnes");
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        ArrayList<GUser> alumnes = getUsers("alumnes");
        for(GUser u : alumnes){
            System.out.println(u);
        }
        System.out.println(">>>>>> Total alumnes: "+alumnes.size());
    }
    
    /**
    * Imprimeix la informació del usuaris suspesos d'una unitat en el domini GSuite. 
    * 
    * @param  unitat  Nom de la unitat del domini GSuite (ex: alumnes, professors, batx2, o eso1).
    */  
    public void printSuspendedUsers(String unitat){
        String command = "D:\\gam\\gam print users query \"orgUnitPath=/"+unitat+" isSuspended=true \"";
        System.out.println(command);	
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Elimina els usuaris suspesos d'una unitat en el domini GSuite. 
    * 
    * @param  unitat  Nom de la unitat del domini GSuite (ex: alumnes, professors, batx2, o eso1).
    */  
    public void deleteSuspendedUsers(String unitat){
        String command = "D:\\gam\\gam print users query \"orgUnitPath=/"+unitat+" isSuspended=true\"";
	String output = obj.executeCommand(command);
        System.out.println(output);
        String[] linies = output.split("\n");
        for(int i=1; i<linies.length; i++){
            String email = linies[i].trim();
            System.out.println("Eliminant l'usuari suspés "+email);
            deleteUser(email);
        }
    }
    
    /**
    * Elimina un usuari del domini GSuite. 
    * 
    * @param  email  Email de l'usuari del domini GSuite (ex: nomprofessor@iesmanacor.cat o nomalumne@alumne.iesmanacor.cat).
    */  
    public void deleteUser(String email){
        String command = "D:\\gam\\gam delete user "+email;
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Genera una col·lecció amb la informació del usuaris suspesos (o no suspesos) d'una unitat en el domini GSuite. 
    * 
    * @param  unitat  Nom de la unitat del domini GSuite (ex: alumnes, professors, batx2, o eso1).
    * @param  suspended  Booleà indicant si filtram per usuaris suspesos o no suspesos (true o false).
    * @return Col·lecció dels usuaris suspesos o no suspesos de la unitat en el domini GSuite. 
    */  
    public ArrayList<GUser> getUsers(String unitat, boolean suspended){
        //String command = "D:\\gam\\gam print users query \"orgUnitPath=/"+unitat+"\"";	
        String sus = (suspended)?"true":"false";
        String command = "D:\\gam\\gam print users query \"orgUnitPath=/"+unitat+" isSuspended="+sus+"\"";
	String output = obj.executeCommand(command);
        //System.out.println(output);
        String[] linies = output.split("\n");
        
        ArrayList<GUser> users = new ArrayList<GUser>();
        for(int i=1; i<linies.length; i++){
            if(isOneEmail(linies[i])){
                System.out.println("LLegint info de l'usuari: "+linies[i]);
                GUser u = getUserInfo(linies[i]);
                users.add(u);
            }
        }
        return users;
    }
    
    public boolean isOneEmail(String s){
        return s.contains("@") && s.indexOf("@")==s.lastIndexOf("@");
    }
    
    /**
    * Genera una col·lecció amb la informació del usuaris d'una unitat en el domini GSuite. 
    * 
    * @param  unitat  Nom de la unitat del domini GSuite (ex: alumnes, professors, batx2, o eso1).
    * @return Col·lecció dels usuaris de la unitat en el domini GSuite. 
    */   
    public ArrayList<GUser> getUsers(String unitat){
        //String command = "D:\\gam\\gam print users query \"orgUnitPath=/"+unitat+"\"";	
        String command = "D:\\gam\\gam print users query orgUnitPath=/"+unitat;	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
        String[] linies = output.split("\n");
        
        
        ArrayList<GUser> users = new ArrayList<GUser>();
        for(int i=1; i<linies.length; i++){
            if(isOneEmail(linies[i])){
                System.out.println("LLegint info de l'usuari: "+linies[i]);
                GUser u = getUserInfo(linies[i]);
                users.add(u);
            }
        }
        System.out.println(">>>>>> Total usuaris de la unitat "+unitat+": "+users.size());
        return users;
    }
    
    /**
    * Elimina els accents dels noms i llinatges per a generar noms d'usuari del domini GSuite. 
    * 
    * @param  input  Nom o llinatges dels que volem eliminar els accents.
    * @return String  Nom o llinatges sense accents.
    */   
    public String remove_accents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String accentRemoved = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return accentRemoved;
    }
    
    /**
    * Genera el HasMap per als noms d'usuari d'una unitat en el domini GSuite. 
    * 
    * @param  unitat  Nom de la unitat del domini GSuite (ex: alumnes o professors).
    * @return HasMap dels noms d'usuaris i número d'ocurrències.
    */    
    public HashMap<String, Integer> getUserNames(String unitat){
        HashMap<String, Integer> nomUsuaris = new HashMap();
        
        String command = "D:\\gam\\gam print users query \"orgUnitPath=/"+unitat+"\"";	
        //String command = "D:\\gam\\gam print users custom all";	
	String output = obj.executeCommand(command);
        //System.out.println(output);
        
        String[] linies = output.split("\n");
        
        for(int i=1; i<linies.length; i++){
            
            //System.out.println("LLegint info de l'usuari: "+linies[i]);
            String[] cols = linies[i].split("@");
            
            String nomUser = cols[0];
            //System.out.print(nomUser+" ");
            if(Character.isDigit(nomUser.charAt(nomUser.length()-1))){
                nomUser = nomUser.substring(0, nomUser.length()-1);
            }
            //System.out.println(nomUser);
            if(nomUsuaris.containsKey(nomUser)){
                int value = nomUsuaris.get(nomUser);
                nomUsuaris.replace(nomUser, value, value+1);
            }
            else {
                nomUsuaris.put(nomUser, 1);
            }
        }
        return nomUsuaris;
    }
    
    /**
    * Retorna el nom d'usuari per al professor/a en el domini GSuite. 
    * Cal fer ús d'una versió actualitzada del HashMap dels noms d'usuaris dels professors.
    * @param  p  Professor/a del domini GSuite.
    * @return String  Nom d'usuari generat per al professor/a.
    */   
    public String generateUserName(Professor p){
        
        String nom = remove_accents(p.nom);
        String ap1 = remove_accents(p.ap1);
        String username = nom.substring(0, 1) + ap1.replaceAll("\\s", "");
        username = username.toLowerCase();
        System.out.println("Ckecking username: "+username);
        System.out.println("Num. Usernames Profes del GSuite: "+usernamesProfes.size());
        if(XML2GAM.usernamesProfes.containsKey(username)){
            System.out.println("USER NAME REPEATED!!!! "+ username);
            int n = XML2GAM.usernamesProfes.get(username);
            XML2GAM.usernamesProfes.put(username, n + 1);
            username = username.concat(Integer.toString(n));
        }
        else {
            System.out.println("NEW USER NAME.");
            XML2GAM.usernamesProfes.put(username, 1);
        }
        System.out.println("Username per al professor/a "+nom+" "+ap1+" "+p.ap2+" es: "+username);
        return username;
    }
    
    /**
    * Retorna el nom d'usuari per a l'alumne en el domini GSuite. 
    * Cal fer ús d'una versió actualitzada del HashMap dels noms d'usuaris dels alumnes.
    * @param  a  Alumne del domini GSuite.
    * @return String  Nom d'usuari generat per a l'alumne.
    */   
    public String generateUserName(Alumne a){
        
        String nom = remove_accents(a.nom);
        String ap1 = remove_accents(a.ap1);
        String username = nom.substring(0, 1) + ap1.replaceAll("\\s", "");
        username = username.toLowerCase();
        
        if(XML2GAM.usernamesAlumnes.containsKey(username)){
            int n = XML2GAM.usernamesAlumnes.get(username);
            XML2GAM.usernamesAlumnes.put(username, n + 1);
            username = username.concat(Integer.toString(n));
        }
        else {
            XML2GAM.usernamesAlumnes.put(username, 1);
        }
        
        return username;
    }
    
    /**
    * Afegeix un usuari professor/a al domini GSuite en la unitat org. /iesmanacor/professors. 
    * Afegeix als grups de correu corresponents: professors, dep., tutoria, ...
    * @param  p  Informació del professor/a.
    * @param deps Collecció dels grups de correu del domini corresponents als departaments.
    */  
    public void afegirProfessor(Professor p, ArrayList<GGrup> deps){
        
        // Generar el nom d'usuari per al professor/a
        String username = generateUserName(p);
        String email =username+"@iesmanacor.cat";
        String nom = p.nom;
        String llinatges = p.ap1+" "+p.ap2;
        
        // Crea usuari
        String password = "iesmanacor2018";
        String command = "D:\\gam\\gam create user "+email+" firstname \""+nom+"\" lastname \""+llinatges+"\" password "+password+" changepassword on org professors";	
	String output = obj.executeCommand(command);
        System.out.println(output);
        
        // Associar Codi XESTIB
        String command2 = "D:\\gam\\gam update user "+email+" externalid organization "+p.codi+" ";	
	String output2 = obj.executeCommand(command2);
        System.out.println(output2);
        
        // Afegeix usuari al grup del departament
        String codiDep = p.departament;
        String emailDep = getEmailDep(deps, codiDep);
        if(emailDep!=null){
            System.out.println("Afegint "+email+" a "+emailDep);
            afegirGrupUsuari(email, emailDep);
        }
        else {
            System.out.println("ALERTA!! Professor sense departament. "+p);
        }
        
        // Afegir el professor/a al grup de tutors (si pertoca)
        Grup tutorGrup = p.getGrupTutor();
        if(tutorGrup!=null){
            if(tutorGrup.nomCanonic.contains("eso")){
                afegirGrupUsuari(email, "tutoria.eso@iesmanacor.cat");
            }
            else if(tutorGrup.nomCanonic.contains("batx")){
                afegirGrupUsuari(email, "tutoria.batxillerat@iesmanacor.cat");
            }
            else if(tutorGrup.nomCanonic.contains("11")){
                afegirGrupUsuari(email, "tutoria.fpb@iesmanacor.cat");
            }
            else {
                afegirGrupUsuari(email, "tutoria.fp@iesmanacor.cat");
            }
        }
        else {
            System.out.println("El professor no és tutor/a. No cal afegir a grup de tutors.");
        }
        
        // Afegir al grup de Professors
        afegirGrupUsuari(email, "professors@iesmanacor.cat");
        
        // Afegir al Calendari Escolar IES Manacor
        afegirUserCalendarACL(email, "iesmanacor.cat_43616c4945534d616e61636f72@resource.calendar.google.com", "read");
        
        // Afegir al Calendari Extraescolar IES Manacor
        afegirUserCalendarACL(email, "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com", "read");
        
        
    }
    
    // NO ACABAT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    /**
    * Afegeix un usuari alumne al domini GSuite en la unitat org. /iesmanacor/alumnes/eso/eso1... 
    * Afegeix als grups de correu corresponents: alumnes, eso1a, ...
    * @param  a  Informació de l'alumne.
    * @param cursos Collecció dels grups de correu del domini corresponents als cursos.
    */  
    public void afegirAlumne(Alumne a, ArrayList<GGrup> cursos){
        
        // Generar el nom d'usuari per al professor/a
        String username = generateUserName(a);
        String email =username+"@alumnes.iesmanacor.cat";
        String nom = a.nom;
        String llinatges = a.ap1+" "+a.ap2;
        
        Grup g = a.grup;
        
        String pathOrg = getUnitPath(g);
        //if(g.nomCanonic)
        //XML2GAM.getPathUnitatAlumne(XML2GAM.orgs, a);
        //eso/eso1a falta obtenir el nom canonic de la unitat organitzativa corresponent al grup de l'alumne";
        
        // Crea usuari
        String password = "iesmanacor2018";
        String command = "D:\\gam\\gam create user "+email+" firstname \""+nom+"\" lastname \""+llinatges+"\" password \""+password+"\" changepassword on org "+pathOrg;	
	System.out.println(command);
        //String output = obj.executeCommand(command);
        //System.out.println(output);
        
        // Associar Codi XESTIB
        String command2 = "D:\\gam\\gam update user "+email+" externalid organization "+a.codi+" ";	
        System.out.println(command2);
	//String output2 = obj.executeCommand(command2);
        //System.out.println(output2);
        
        // Afegeix usuari al grup del curs
        String emailGrup = g.nomCanonic+"@iesmanacor.cat";
        if(g.nomCanonic.length()>0){
            System.out.println("Afegint "+email+" a "+emailGrup);
            //afegirGrupUsuari(email, emailGrup);
        }
        else {
            System.out.println("ALERTA!! Alumne sense curs/grup. "+a);
        }
        
        // Afegir al grup de Alumnes
        System.out.println("Afegint "+email+" a alumnes@iesmanacor.cat");
        //afegirGrupUsuari(email, "alumnes@iesmanacor.cat");
    }
    
    /**
    * Retorna la ruta de la Unitat Organitzativa corresponent a un grup/curs d'alumnes. 
    * 
    * @param g Informació del grup d'alumnes (ex: eso1a).
    * @return  String Ruta de la unitat organitzativa corresponent al grup (ex: alumnes/eso/eso1).
    */ 
    public String getUnitPath(Grup g){
        String rutaUnitat = "alumnes/";
        if(g.nomCanonic.contains("eso")){
            rutaUnitat +="eso/"+g.nomCanonic.substring(0, g.nomCanonic.length()-1);
        }
        else if(g.nomCanonic.contains("batx")){
            rutaUnitat +="batx/"+g.nomCanonic.substring(0, g.nomCanonic.length()-1);
        }
        else if(g.nomCanonic.contains("ct")){
            rutaUnitat +="ct";    // no està bé
        }
        else if(g.nomCanonic.contains("ebasica")){
            rutaUnitat +="ebasica";    // no està bé
        }
        else {
            rutaUnitat +="fp/"+g.nomCanonic.substring(0, g.nomCanonic.length()-1);
        }
        return rutaUnitat;
    }
    
    public void actualitzaGrup(GUser u, Alumne a, ArrayList<GGrup> grups){
        String codiGrup = a.codiGrup;
        if(codiGrup!=null){
            String emailGrup = XML2GAM.getEmailGrup(grups, codiGrup);
            if(emailGrup!=null){
                System.out.println("Afegint "+u.email+" a "+emailGrup);
                afegirGrupUsuari(u.email, emailGrup);
            }
        }
        else {
            System.out.println("ALERTA! Alumne sense grup! "+a);
        }
    }
     
    public void actualitzaDepartament(GUser u, Professor p, ArrayList<GGrup> deps){
        String codiDep = p.departament;
        if(codiDep!=null){
            String emailDep = XML2GAM.getEmailDep(deps, codiDep);
            if(emailDep!=null){
                System.out.println("Afegint "+u.email+" a "+emailDep);
                afegirGrupUsuari(u.email, emailDep);
            }
        }
        else {
            System.out.println("ALERTA! Professor sense departament! "+p);
        }
    }
    
    public void actualitzaTutoria(String emailProfe, Grup tutorGrup){
        
        // Llevar dels grups de tutories del curs anterior.
        llevarTutories(emailProfe);
        
        // Afegir el professor/a al grup de tutors (si pertoca)
        if(tutorGrup!=null){
            if(tutorGrup.nomCanonic.contains("eso")){
                afegirGrupUsuari(emailProfe, "tutoria.eso@iesmanacor.cat");
            }
            else if(tutorGrup.nomCanonic.contains("batx")){
                afegirGrupUsuari(emailProfe, "tutoria.batxillerat@iesmanacor.cat");
            }
            else if(tutorGrup.nomCanonic.contains("11")){
                afegirGrupUsuari(emailProfe, "tutoria.fpb@iesmanacor.cat");
            }
            else {
                afegirGrupUsuari(emailProfe, "tutoria.fp@iesmanacor.cat");
            }
        }
        else {
            System.out.println("El professor no és tutor/a. No cal afegir a grup de tutors.");
        }
    }
    
    /**
    * Lleva a tots els usuaris GSuite de tots els grups de tutories (eso, batx, fp, i fpb). 
    * 
    */    
    public void llevarTutories(){
        for (String tutoria : XML2GAM.TUTORIES) {
            String command = "D:\\gam\\gam update group "+tutoria+"@iesmanacor.cat clear ";	
            String output = obj.executeCommand(command);
            System.out.println(output);
        }
    }
    
    /**
    * Lleva a l'usuari professor/a GSuite de tots els grups de tutories (eso, batx, fp, i fpb). 
    * 
    * @param  emailProfe  Email de l'usuari (ex: nomprofessor@iesmanacor.cat)
    */    
    public void llevarTutories(String emailProfe){
        for (String tutoria : XML2GAM.TUTORIES) {
            String command = "D:\\gam\\gam update group "+tutoria+"@iesmanacor.cat remove member "+emailProfe;	
            String output = obj.executeCommand(command);
            System.out.println(output);
        }
    }
    

    public void AfegirAlumne(Alumne a, ArrayList<GGrup> grups){
        
    }
    
    /**
    * Suspén a l'usuari de GSuite. 
    * No borra l'usuari de cap dels grups que en sigui membre.
    * @param  email  Email de l'usuari (ex: nomprofessor@iesmanacor.cat  o nomalumne@alumne.iesmanacor.cat)
    */
    public void suspenUsuari(String email){
        String command = "D:\\gam\\gam update user "+email+" suspended on";	
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    public void provant(){
        String command = "D:\\gam\\gam print orgs ";	
        String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    
    //++++++++++ Operacions sobre Grups ++++++++++++++++++++++//
    
    /**
    * Crea un grup de correu GSuite. 
    * 
    * @param  email  Email del grup (ex: batx2a@iesmanacor.cat)
    * @param  nom  Nom del grup (ex: batx2a)
    * @param  desc  Descripció del grup (ex: 369456 - 2n batx.-A)
    */
    public void creaGrup(String email, String nom, String desc){
        String command = "D:\\gam\\gam create group \""+email+"\" name \""+nom+"\" description \""+desc+"\" ";	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    
    /**
    * Lleva els usuaris actuals del grup. 
    * Un cop executat el grup no tendrà membres.
    *
    * @param  emailGrup  Email del grup (ex: batx1a@iesmanacor.cat)
    */
    public void buidarGrupUsuaris(String emailGrup){
        String command = "D:\\gam\\gam update group "+emailGrup+" clear ";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
    * Afegeix un usuari al Grup GSuite. 
    *
    * @param  emailUser Email de l'usuari (ex: nomprofessor@iesmanacor.cat  o nomalumne@alumne.iesmanacor.cat)
    * @param  emailGrup Email del grup (ex: batx1a@iesmanacor.cat)
    */
    public void afegirGrupUsuari(String emailUser, String emailGrup){
        String command = "D:\\gam\\gam update group "+emailGrup+" add member "+emailUser;		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // +++++++++++++++++++++ OPERACIONS SOBRE CALENDARIS DEL DOMINI GSUITE +++++++++++++++++ //
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    
    /**
     * Imprimeix la informació de tots els calendaris de recursos del domini GSuite.
     */
    public void printCalendars(){
        String command = "D:\\gam\\gam print resources ";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Imprimeix la informació dels calendaris de l'usuari del domini GSuite.
     * @param emailUser Email de l'usuari del domini GSuite. 
     */
    public void printCalendarsUser(String emailUser){
        String command = "D:\\gam\\gam user "+emailUser+" show calendars";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Imprimeix els usuaris subscrits a un calendari del domini GSuite.
     * @param emailCalendar 
     */
    public void printUsersCalendar(String emailCalendar){
        String command = "D:\\gam\\gam calendar "+emailCalendar+" showacl";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Crea un calendari de recurs en el domini GSuite.
     * @param id Identificador del calendari de recurs.
     * @param desc Descripció del calendari de recurs.
     */
    public void creaCalendariRecurs(String id, String nom, String desc){
        String command = "D:\\gam\\gam create resource "+id+" \""+nom+"\""+" description \""+desc+"\" ";	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Crea un calendari de recurs per a una Aula.
     * @param codiAula Codi de l'Aula.
     * @param desc Descripció de l'Aula.
     * @param tipus Tipus de l'Aula (Aula Teòrica, Aula d'Exàmens, Aula Informàtica, ...)
     */
    public void creaCalendariRecursAula(String codiAula, String desc, String tipus){
        String command = "D:\\gam\\gam create resource "+codiAula+" description \""+desc+"\" type \""+tipus+"\"";		
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Afegeix un usuari del domini GSuite a la llista de subscriptors d'un Calendari.
     * @param emailUser Email de l'usuari de domini GSuite.
     * @param emailCalendar Email del calendari de domini GSuite.
     * @param rol  Rol de l'usuari (freebusy|red|editor|owner).
     */
    public void afegirUserCalendarACL(String emailUser, String emailCalendar, String rol){
        String command = "D:\\gam\\gam calendar "+emailCalendar+" add "+rol+" "+emailUser;	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    
    public void afegirProfesCalendariEscolar(ArrayList<GUser> profes){
        // Afegir al Calendari Escolar IES Manacor
        for(GUser u: profes){
            String emailUser=u.email;
            afegirUserCalendarACL(emailUser, "iesmanacor.cat_43616c4945534d616e61636f72@resource.calendar.google.com", "read");
        }
        
    }
    public void afegirGrupProfesCalendariEscolar(){
        // Afegir al grup de professors al Calendari Escolar IES Manacor
        afegirUserCalendarACL("professors@iesmanacor.cat", "iesmanacor.cat_43616c4945534d616e61636f72@resource.calendar.google.com", "read");
    }
    
    public void afegirProfesCalendariExtraescolar(ArrayList<GUser> profes){
        // Afegir els professors al Calendari Escolar IES Manacor
        for(GUser u: profes){
            String emailUser=u.email;
            afegirUserCalendarACL(emailUser, "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com", "read");
        }
        
    }
    
    public void afegirGrupProfesCalendariExtraescolar(){
        // Afegir al grup de professors al Calendari Extraescolar IES Manacor
        afegirUserCalendarACL("professors@iesmanacor.cat", "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com", "read");
        
    }
    
    
    
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
    // ++++++++++++++++++ OPERACIONS SOBRE UNITATD DEQUIP DEL DOMINI GSUITE ++++++++++++++++ //
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ //
        
    
    /**
     * Imprimeix les Unitats d'Equip d'un usuari del Domini GSuite.
     * @param emailUser Email de l'usuari.
     */
    public void printUnitTeamsUser(String emailUser){
        // gam user <email> print|show teamdrives [todrive] [asadmin]
        String command = "D:\\gam\\gam user "+emailUser+" print teamdrives";	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Crea una Unitat d'Equip (Drive)
     * @param unitName Nom de la Unitat d'Equip (Carpeta Drive)
     * @param emailUser Email de l'usuari creadro de la unitat d'equip.
     */
    public void createUnitTeam(String unitName, String emailUser){
        // gam user <email> add teamdrive <name>
        String command = "D:\\gam\\gam user "+emailUser+" add teamdrive \""+unitName+"\"";	
        System.out.println(command);
	String output = obj.executeCommand(command);
        System.out.println(output);
    }
    
    /**
     * Crea les Unitats d'Equip (Drive) per a cada departament amb l'usuari indicat.
     * @param deps Col·lecció del grups del domini GSuite (filtra per Departaments).
     * @param emailUser Email de l'usuari creador de la Unitat d'Equip.
     */
    public void createUnitTeamsDepartments(ArrayList<GGrup> deps, String emailUser){
        for(GGrup dep : deps){
            String unitName = dep.name;
            if(dep.departament){
                createUnitTeam(unitName, emailUser);
            }
        }
    }
    
    /**
     * Crea les Unitats d'Equip (Drive) per a cada coordinació amb l'usuari indicat.
     * @param coords Col·lecció del grups del domini GSuite (filtra per Coordinació).
     * @param emailUser Email de l'usuari creador de la Unitat d'Equip.
     */
    public void createUnitTeamsCoords(ArrayList<GGrup> coords, String emailUser){
        for(GGrup c : coords){
            String unitName = c.name;
            if(unitName.contains("Coord.")){
                createUnitTeam(unitName, emailUser);
            }
        }
    }
    
    
    /**
     * Crea les Unitat d'Equip (Drive) per als grups de Tutors
     * @param tuts Col·lecció del grups del domini GSuite (filtra per Tutors).
     * @param emailUser Email de l'usuari cresador de la unitat d'equip.
     */
    public void createUnitTeamsTutors(ArrayList<GGrup> tuts, String emailUser){
        for(GGrup t : tuts){
            String unitName = t.name;
            if(unitName.contains("Tutors")){
                createUnitTeam(unitName, emailUser);
            }
        }
    }
}
