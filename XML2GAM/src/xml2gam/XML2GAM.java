
package xml2gam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <h3>XML2GAM és una eina per a sincronitzar les dades del XESTIB amb les dades del domini GSuite.</h3>
 * <ul>
 * <li>Llegeix les dades del fitxer XML del Xestib mitjançant els mètodes de la classe XMLReader.</li>
 * <li>Opera sobre usuaris, grups i unitats organitzatives del domini GSuite.</li>
 * <li>Utilitza GAM (gestió per línia de comandes de Google G Suite. <a href="https://github.com/jay0lee/GAM/wiki">https://github.com/jay0lee/GAM/wiki</a></li>
 * </ul>

 * @author IESManacor
 * @version 2018.6
 */

public class XML2GAM {

    /**
     * Domini GSuite.
     */
    public static final String DOMINI = "IESMANACOR.CAT";
    static final boolean DEBUG = true;
    static final String MEUEMAIL = "aramirez@iesmanacor.cat";
    
    /**
     * Conjunt de noms canònics dels grups de correu de tutoria (eso, batx, fp i fpb).
     */
    public static final String[] TUTORIES = {"tutoria.eso", "tutoria.batxillerat", "tutoria.fpb", "tutoria.fp"};
     /**
     * Noms d'usuaris dels membres de l'equip directiu i del grup directiva@iesmanacor.cat.
     */
    static final String[] DIRECTIVA = {"fsapena", "mamengual", "mcubells", "mfuster", "mnicolau", "mreyes", "priera", "ssureda"};
    
    static GAM gam;
    static ArrayList<GGrup> cursos;
    static ArrayList<GGrup> deps;
    static ArrayList<GOrg> orgs;
    static ArrayList<GOrg> orgsAlumnes;
    
    /**
     * Col·lecció dels usuaris professors del domini GSuite.
     */
    static ArrayList<GUser> professors;
    
    /**
     * Col·lecció dels usuaris alumnes del domini GSuite.
     */
    public static ArrayList<GUser> alumnes;
    
    /**
     * Mapa de Hashing dels noms d'usuaris dels professors del domini GSuite.
     */
    public static HashMap<String, Integer> usernamesProfes;
    
    /**
     * Mapa de Hashing dels noms d'usuaris dels alumnes del domini GSuite.
     */
    public static HashMap<String, Integer> usernamesAlumnes;
    
    // Execució de comanda en terminal, retorn el resultat de la comanda.
    public static String executeCommand(String command) {

		StringBuilder output = new StringBuilder();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line).append("\n");
			}

		} catch (IOException | InterruptedException e) {
                    System.out.println("exception happened - here's what I know: ");
                    e.printStackTrace();
                    System.exit(-1);
                }

		return output.toString();

    }
  
    // Carrega usuaris GSuite de la unitat /professors i els imprimeix
    public static void printProfessors(){
        // Membres de GSuite (OK) de la unitat /professors.
        String unitat ="professors";
        System.out.println("Membres del GSuite ("+DOMINI+") a la unitat "+unitat);
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        professors = gam.getUsers(unitat);
        for(GUser u : professors){
            System.out.println(u);
        }
    }
    
    // Carrega els grups GSuite del domini i els imprimeix
    public static void printGrups(){
        // Grups de GSuite (OK) del domini IESMANACOR:CAT
        System.out.println("Grups de GSuite ("+DOMINI+").");
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        ArrayList<GGrup> grups = gam.getGroups();
        for(GGrup g : grups){
            System.out.println(g);
        }
    }
    
    
    // Crea els grups de GSuite corresponents amb els diferents curs-grup del fitxer XML
    public static void creaGrupsCursos(ArrayList<Grup> grupsXML){
        for(Grup g: grupsXML){
            String email = g.nomCanonic+"@iesmanacor.cat";
            String nom = g.nomCanonic;
            String desc = g.codi+" - "+g.descripcio+"-"+g.nom;
            System.out.println("Creant grup "+email+" per a "+nom+" ("+desc+")");
            gam.creaGrup(email, nom, desc);
        }
    }
    
    // Carrega les unitats org. GSuite de domini i les imprimeix
    public static void printUnits(){
        // Unitats Organitzatives de GSuite (OK) del domini IESMANACOR:CAT
        System.out.println("Unitats del GSuite ("+DOMINI+").");
        System.out.println("Path\tId\tName\tparentId");
        orgs = gam.getUnits();
        for(GOrg o : orgs){
            System.out.println(o);
        }
    }
    
    // Obté la informació d'un usuari de GSuite a partir del seu email i la imprimeix
    public static void printInfoUser(String email){
        // Info de Membre de GSuite (OK) a partir de l'email
        gam.printUserInfo(email);
        GUser u = gam.getUserInfo(email);
        System.out.println("Email\tNom\tLlinatges\tCodiXestib\tUnitat Org.\tAdmin\tGrups");
        System.out.println(u);
    }
    
    public static boolean professorDinsDomini(ArrayList<GUser> users, Professor profeXML){
        for(GUser u: users){
            String codiProfeXML = profeXML.getCodi();
            if(u.codiXestib.equalsIgnoreCase(codiProfeXML.trim())){
                return true;
            }
        }
        return false;
    }
    
    public static boolean alumneDinsDomini(ArrayList<GUser> users, Alumne alumneXML){
        for(GUser u: users){
            String codiAlumneXML = alumneXML.getCodi();
            if(u.codiXestib.equalsIgnoreCase(codiAlumneXML.trim())){
                return true;
            }
        }
        return false;
    }
    
    public static GUser matchingProfessor(ArrayList<GUser> users, Professor profeXML){
        for(GUser u: users){
            String codiProfeXML = profeXML.getCodi();
            if(u.codiXestib.equalsIgnoreCase(codiProfeXML.trim())){
                return u;
            }
        }
        return null;
    }
    
    public static GUser matchingAlumne(ArrayList<GUser> users, Alumne alumneXML){
        for(GUser u: users){
            String codiAlumneXML = alumneXML.getCodi();
            if(u.codiXestib.equalsIgnoreCase(codiAlumneXML.trim())){
                return u;
            }
        }
        return null;
    }
    
    
    public static String getEmailDep(ArrayList<GGrup> grups, String codiDep){
        for(GGrup g: grups){
            if(g.codiDep.equalsIgnoreCase(codiDep)){
                return g.email;
            }
        }
        return null;
    }
    
    public static String getEmailGrup(ArrayList<GGrup> grups, String codiGrup){
        for(GGrup g: grups){
            if(g.codiDep.equalsIgnoreCase(codiGrup)){
                return g.email;
            }
        }
        return null;
    }
    
    public static void comparaProfes(ArrayList<GUser> professors, ArrayList<Professor> profesXML){
        
        ArrayList<GUser> users = new ArrayList<GUser>();
        users.addAll(professors);
         
        int numNous=0, numRepes=0;
        for(Professor p : profesXML){
            if(professorDinsDomini(users, p)){
                
                System.out.println("El professor " + p + " ja està en el Domini GSuite.");
                
                GUser u = matchingProfessor(users,p);
                /*
                //Actualitzar el grup del departament? (Sí/No)
                gam.actualitzaDepartament(u, p, deps);
                
                // Actualitzar el grup de tutors
                Grup tutorGrup = p.getGrupTutor();
                gam.actualitzaTutoria(u.email, tutorGrup);
                
                // Afegir al grup de Professors
                gam.afegirGrupUsuari(u.email, "professors@iesmanacor.cat");
                */
                users.remove(u);
                numRepes++;
            }
            else {
                System.out.println("El professor " + p + " no està en el Domini GSuite. Cal afegir-lo!!! \tCodi Xestib: "+p.getCodi());
                gam.afegirProfessor(p, deps);
                numNous++;
            }
        }
        System.out.println("Profes nous: "+numNous+", Profes actualitzats: "+numRepes+", Profes obsolets: "+users.size());
        
        // Profes Obsolets
        if(users.size()>0){
            for(GUser u : users){
                System.out.println("Suspenent l'usuari: "+ u);
                gam.suspenUsuari(u.email);
            }
        }
        
    }
     
    public static void comparaAlumnes(ArrayList<GUser> alumnes, ArrayList<Alumne> alumnesXML){
        
        ArrayList<GUser> users = new ArrayList<GUser>();
        users.addAll(alumnes);
         
        int numNous=0, numRepes=0;
        for(Alumne a : alumnesXML){
            if(alumneDinsDomini(users, a)){
                
                System.out.println("L'alumne " + a + " ja està en el Domini GSuite.");
                GUser u = matchingAlumne(users,a);
                
                //Actualitzar el grup? (Sí/No)
                //gam.actualitzaGrup(u, a, grups);

                users.remove(u);
                numRepes++;
            }
            else {
                System.out.println("L'alumne " + a + " no està en el Domini GSuite. Cal afegir-lo!!! \tCodi Xestib: "+a.getCodi());
                gam.afegirAlumne(a, cursos);
                numNous++;
            }
        }
        System.out.println("Alumnes nous: "+numNous+", actualitzats: "+numRepes+", obsolets: "+users.size());
        
        // Profes Obsolets
        if(users.size()>0){
            for(GUser u : users){
                System.out.println("Suspenent l'usuari: "+ u);
                gam.suspenUsuari(u.email);
            }
        }
        
    }
     
    
    public static void main(String[] args) {
        
        // Inicialitzar XMLReader i GAM
        XMLReader xml = new XMLReader("C:\\Users\\ToniMitjanit\\Desktop\\exportacioDadesCentre.xml");
        XML2GAM obj = new XML2GAM();
        gam = new GAM(obj);
        
        //ArrayList<SessioA> sa = xml.llegirSessionsAlumnes();
        
        //ArrayList<GGrup> tuts = gam.getTutors();
        //gam.createUnitTeamsTutors(tuts, "aramirez@iesmanacor.cat");
        
        
        
        //gam.createUnitTeam("Dept. Castellà", "aramirez@iesmanacor.cat");
        
        //gam.printUnitTeamsUser("aramirez@iesmanacor.cat");
        //gam.afegirUserCalendarACL("cbarcelo@iesmanacor.cat", "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com", "editor");
        //gam.afegirUserCalendarACL("coord.extraescolars@iesmanacor.cat@iesmanacor.cat", "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com", "editor");

        /*
        ArrayList<Submateria> mats = xml.llegirMateries();
        for(Submateria a : mats){
            System.out.println(a);
        }
        */
        
        //ArrayList<GUser> gprofes = gam.getUsers("professors");
        //gam.afegirGrupProfesCalendariExtraescolar();
        //gam.afegirGrupProfesCalendariEscolar();
        
        //gam.afegirUserCalendarACL("aramirez@iesmanacor.cat", "iesmanacor.cat_43616c45787472614945534d616e61636f72@resource.calendar.google.com", "editor");
        
        //gam.creaCalendariRecurs("CalExtraIESManacor", "Calendari Extraescolars IESManacor", "Calendari Extraescolar IES Manacor.");
        
        //gam //gam.printCalendars();
        
        //gam.printCalendarsUser("aramirez@iesmanacor.cat");
        
        //gam.printUserInfo("aramirez@iesmanacor.cat");
        
        //gam.printSuspendedUsers("alumnes");
        
        //gam.deleteSuspendedUsers("alumnes");
        
        //gam.buidarGrupUsuaris("batx2a@iesmanacor.cat");
        /*
        ArrayList<GUser> suspesos = gam.getUsers("alumnes", true);
        System.out.println("Usuaris suspesos:");
        for(GUser u : suspesos){
            System.out.println(u);
        }
        */
        
        
        //printProfessors();
        //gam.provant();
        
        //orgs = gam.getUnits();
        
        //Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Professors)
        //usernamesProfes = gam.getUserNames("professors");
        //System.out.println("Users (Profes): "+Arrays.asList(usernamesProfes));
        
        // Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Alumnes)
        //usernamesAlumnes = gam.getUserNames("alumnes");
        //System.out.println("Users (Alumnes):"+Arrays.asList(usernamesAlumnes));
        
        // LLegir els grups Departaments del Domini GSuite.
        //deps = gam.getDeps();
        
        // LLegir les Unitats Organitzatives d'Alumnes del Domini GSuite.
        //orgsAlumnes = gam.getUnits("alumnes");
        
        // Llegir els usuaris del Domini GSuite (Professors).
        //professors = gam.getUsers("professors");
        
        // Llegir els usuaris del Domini GSuite (Alumnes).
        //alumnes = gam.getUsers("alumnes");
        
        // Llegir els grups de correu del Domini GSuite ().
        //ArrayList<GGrup> grupsGS = gam.getGroups();
        
        //ArrayList<Grup> grupsXML = xml.llegirGrups();
        //ArrayList<Professor> profesXML = xml.llegirProfesTutors(grupsXML);
        //ArrayList<Alumne> alumnesXML = xml.llegirAlumnesGrup(grupsXML, "ifc31b");
        
        // Alumnes llegits 
        /*
        System.out.println("Alumnes llegits del fitxer XML:");
        for(Alumne a : alumnesXML){
            System.out.println(a);
        }
        */
        // Compara professors del domini GSuite i professors del fitxer XML. 
        //comparaProfes(professors, profesXML);
        
        // Compara alumnes del domini GSuite i alumnes del fitxer XML. 
        //comparaAlumnes(alumnes, alumnesXML);

        // Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Professors)
        //usernamesAlumnes = gam.getUserNames("alumnes/eso1a");
        //System.out.println(Arrays.asList(usernamesAlumnes));
        
        //gam.provant();
        // Info de Membre de GSuite. (OK)
        /*
        String email = "mtimoner@iesmanacor.cat";
        // XML: 944BA534D00BE45FE040D70A59055935, GSuite:944BA534D00BE45FE040D70A59055935
        gam.printUserInfo(email);
        GUser u = gam.getUserInfo(email);
        System.out.println("Email\tNom\tLlinatges\tCodiXestib\tUnitat Org.\tAdmin\tGrups");
        System.out.println(u);
        */
        //gam.printUserXestib("918D3D4739FACB36E040D70A59056FF3");
        // Membres de GSuite (OK)
        /*
        String unitat ="professors";
        System.out.println("Membres del GSuite ("+DOMINI+") a la unitat "+unitat);
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        //gam.printUsers(unitat);
        ArrayList<GUser> professors = gam.getUsers(unitat);
        for(GUser u : professors){
            System.out.println(u);
        }
        */
        
        //gam.provant();
        /*
        XMLReader xml = new XMLReader("C:\\Users\\ToniMitjanit\\Desktop\\exportacioDadesCentre.xml");
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        System.out.println("Grups Llegits del fitxer XML.");
        System.out.println("Codi\tNomC\tNom\tDescripcio\tTutor");
        for(Grup g : grupsXML){
            System.out.println(g);
        }
        ArrayList<Professor> profesXML = xml.llegirProfesTutors(grupsXML);
        System.out.println("Professors Llegits del fitxer XML.");
        System.out.println("Codi\tNom\tAp1\tAp2\tUsuari\tCodiDep");
        for(Professor p : profesXML){
            System.out.println(p);
        }
        */
        //professors = gam.getUsers("professors");
        //comparaProfes(professors, profesXML);
        /*
        ArrayList<GOrg> orgs = gam.getUnits("alumnes");
        for(GOrg g : orgs){
            System.out.println(g);
        }
        */
        
        //GOrg o = gam.getOrgUnit("alumnes/tmv22d");
        //System.out.println(o);
        /*
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        System.out.println("Grups Llegits del fitxer XML.");
        System.out.println("Codi\tNomC\tNom\tDescripcio\tTutor");
        for(Grup g : grupsXML){
            System.out.println(g);
        }
        
        printUnits();
        
        for(Grup g: grupsXML){
            gam.creaUnitatOrg(g.nomCanonic, g.descripcio+"-"+g.nom, "alumnes");
        }
        
        ArrayList<Professor> profesXML = xml.llegirProfes();
        
        System.out.println("Professors Llegits del fitxer XML.");
        System.out.println("Codi\tNom\tAp1\tAp2\tUsuari\tCodiDep");
        for(Professor p : profesXML){
            System.out.println(p);
        }
        
        grups = gam.getGroups();
        for(GGrup g : grups){
            System.out.println(g);
        }
        
        //gam.provant();
        professors = gam.getUsers("professors");
        comparaProfes(professors, profesXML);
        

        */
    }
        
        
    // Retorn el codi del grup a partir del Nom Canònic del Grup (eso1a, 
    public static String getCodiGrup(ArrayList<Grup> grups, String nomGrup){
        String codiGrup="";
        for(Grup g : grups){
            if(g.nomCanonic.equalsIgnoreCase(nomGrup)){
                return g.codi;
            }
        }
        return codiGrup;
    }
    
    // Retorn el nom de la Unitat Organitzativa d'un Alumne (eso1a,...) 
    public static String getPathUnitatAlumne(ArrayList<GOrg> unitats, Alumne a){
        Grup g = a.grup;
        String codiGrup = a.getCodi();
        for(GOrg u : unitats){
            if(u.name.equalsIgnoreCase(g.nomCanonic)){
                return u.path;
            }
        }
        return codiGrup;
    }
        
      
    
}
