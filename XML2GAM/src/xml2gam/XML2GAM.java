
package xml2gam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

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
    static ArrayList<GGrup> deps = new ArrayList<GGrup>();
    static ArrayList<GGrup> grups = new ArrayList<GGrup>();
    static ArrayList<GOrg> orgs;
    static ArrayList<GOrg> orgsAlumnes;
    
    /**
     * Col·lecció dels usuaris professors del domini GSuite.
     */
    static ArrayList<GUser> professors = new ArrayList<GUser>();
    
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
    
    // Estructures de dades per guardar info del fitxer XML del XESTIB
    public static ArrayList<Professor> profesXML = new ArrayList<Professor>();
    public static ArrayList<Alumne> alumnesXML = new ArrayList<Alumne>();
    public static ArrayList<Grup> grupsXML = new ArrayList<Grup>();
        
    
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

    
    public static void main(String[] args) {
        
        // Inicialitzar XMLReader i GAM
        XMLReader xml = new XMLReader("C:\\Users\\ToniMitjanit\\Desktop\\exportacioDadesCentre.xml");
        XML2GAM obj = new XML2GAM();
        gam = new GAM(obj);
        
        // Inicialitzar estructures de dades per guardar info del fitxer XML del XESTIB
        profesXML = new ArrayList<Professor>();
        alumnesXML = new ArrayList<Alumne>();
        grupsXML = new ArrayList<Grup>();
        
        // Inicialitzar estructures de dades per guardar info del Domini GSUITE
        deps = new ArrayList<GGrup>();
        grups = new ArrayList<GGrup>();
        professors = new ArrayList<GUser>();
        
        while(true){
            int numOpcio = printOpcionsMenuGAM();
            switch(numOpcio){
                case 0: System.exit(0); //Sortir
                case 1: profesXML = llegirProfesXML(xml); break;
                case 2: llegirAlumnesXML(xml, alumnesXML); break;
                case 3: grupsXML = llegirGrupsXML(xml); break;
                case 4: professors = llegirProfesGAM(gam); break;
                case 5: llegirAlumnesGAM(gam, alumnes); break;
                case 6: llegirUsernamesProfesGAM(gam, usernamesProfes); break;
                case 7: llegirUsernamesAlumnesGAM(gam, usernamesAlumnes); break;
                case 8: llegirOrganitGAM(gam, orgs); break;
                case 9: llegirGrupsGAM(gam); break;
                case 10: llegirDepsGAM(gam); break;
                case 11: comparaProfes(professors, profesXML); break;
                case 12: comparaAlumnes(alumnes, alumnesXML); break;
                default: break;
            }
        }
        
        
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
    
    
    public static int printOpcionsMenuGAM(){
        System.out.println("************************************");
        System.out.println("0) Sortir.");
        System.out.println("1) XML :: Llegir Professors des del fitxer XML XESTIB.");
        System.out.println("2) XML :: Llegir Alumnes des del fitxer XML XESTIB.");
        System.out.println("3) XML :: Llegir Grups/Curs des del fitxer XML XESTIB.");
        System.out.println("4) GAM :: Llegir Professors des del domini GSUITE.");
        System.out.println("5) GAM :: Llegir Alumnes des del domini GSUITE.");
        System.out.println("6) GAM :: Llegir Nom d'Usuaris Professors del domini GSUITE.");
        System.out.println("7) GAM :: Llegir Nom d'Usuaris Alumnes del domini GSUITE.");
        System.out.println("8) GAM :: Llegir Unitats Organitzatives del domini GSUITE.");
        System.out.println("9) GAM :: Llegir Grups de correu del domini GSUITE.");
        System.out.println("10) GAM :: Llegir Departaments - Grups de correu del domini GSUITE.");
        System.out.println("11) GAM :: Sincronitza Professors entre XML Xestib i GSuite IESManacor.");
        
        System.out.print(">>>>> Tria opció: ");
        Scanner teclat = new Scanner(System.in);
        return teclat.nextInt();
    }
    
    
    //************************** OPERACIONS D'ACCÉS AL FITXER XML ********************//
    
    public static ArrayList<Grup> llegirGrupsXML(XMLReader xml){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        if(DEBUG){
            System.out.println("Grups Llegits del fitxer XML.");
            System.out.println("Codi\tNomC\tNom\tDescripcio\tTutor");
            for(Grup g : grupsXML){
                System.out.println(g);
            }
        }
        return grupsXML;
    }
        
    
    public static ArrayList<Professor> llegirProfesXML(XMLReader xml){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        ArrayList<Professor> profesXML = xml.llegirProfesTutors(grupsXML);
        if(DEBUG){
            System.out.println("\tProfessors Llegits del fitxer XML.");
            System.out.println("\tCodi\tNom\tAp1\tAp2\tUsuari\tCodiDep\tTutor");
            for(Professor p : profesXML){
                System.out.println("\t"+p);
            }
        }
        return profesXML;
    }
    
    public static void llegirAlumnesXML(XMLReader xml, ArrayList<Alumne> alumnesXML){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        System.out.print(">>>>> Indica nom del grup (ex: ifc31b): ");
        Scanner teclat = new Scanner(System.in);
        String nomGrup = teclat.next();
        alumnesXML = xml.llegirAlumnesGrup(grupsXML, nomGrup);
        if(DEBUG){
            System.out.println("Alumnes llegits del fitxer XML:");
            for(Alumne a : alumnesXML){
                System.out.println(a);
            }
        }
    }
    
    
    //************************ OPERACIONS SOBRE EL DOMINI GSUITE ************//
    
    
    
    public static void comparaProfes(ArrayList<GUser> professors, ArrayList<Professor> profesXML){
        
        System.out.println("Num. Professors del GSUITE: "+professors.size());
        System.out.println("Num. Professors del XML: "+profesXML.size());
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
                //gam.afegirProfessor(p, deps);
                numNous++;
            }
        }
        System.out.println("Profes nous: "+numNous+", Profes actualitzats: "+numRepes+", Profes obsolets: "+users.size());
        
        // Profes Obsolets
        if(users.size()>0){
            for(GUser u : users){
                System.out.println("Suspenent l'usuari: "+ u);
                //gam.suspenUsuari(u.email);
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
     
    
    public static void llegirDepsGAM(GAM gam){
        // LLegir els grups Departaments del Domini GSuite.
        if(deps.isEmpty()){
            deps = gam.getDeps();
        }
        if(DEBUG){
            System.out.println("id\tname\tdescription\temail\tdep\tcodiDep");
            for(GGrup g : deps){
                System.out.println(g);
            }
        }
    }
    
    public static void llegirGrupsGAM(GAM gam){
        if(grups.isEmpty()){
            grups = gam.getGroups();
        }
        if(DEBUG){
            for(GGrup g : grups){
                System.out.println(g);
            }
        }
    }
    
    public static void llegirOrganitGAM(GAM gam, ArrayList<GOrg> orgs){
        System.out.print(">>>>> Indica nom de la unitat pare (ex: alumnes): ");
        Scanner teclat = new Scanner(System.in);
        String nomUnitat = teclat.next();
        orgs = gam.getUnits(nomUnitat);
        if(DEBUG){
            System.out.println("Unitats Organitzatives del domini GSUITE / "+nomUnitat+" ("+orgs.size()+")");
            for(GOrg g : orgs){
                System.out.println(g);
            }
        }
    }
    
    public static ArrayList<GUser> llegirProfesGAM(GAM gam){
        ArrayList<GUser> professors;
        String unitat ="professors";
        System.out.println("Membres del GSuite ("+DOMINI+") a la unitat "+unitat);
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        professors = gam.getUsers(unitat);
        for(GUser u : professors){
            System.out.println(u);
        }
        return professors;
    }
    
    public static void llegirAlumnesGAM(GAM gam, ArrayList<GUser> alumnes){
        String unitat ="alumnes";
        System.out.println("Membres del GSuite ("+DOMINI+") a la unitat "+unitat);
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        //gam.printUsers(unitat);
        alumnes = gam.getUsers(unitat);
        for(GUser u : alumnes){
            System.out.println(u);
        }
    }
    
    
    public static void llegirUsernamesProfesGAM(GAM gam, HashMap<String, Integer> usernamesProfes){
        //Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Professors)
        usernamesProfes = gam.getUserNames("professors");
        System.out.println("Users (Profes): "+Arrays.asList(usernamesProfes));
    }
    
    public static void llegirUsernamesAlumnesGAM(GAM gam, HashMap<String, Integer> usernamesAlumnes){
        //Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Professors)
        usernamesAlumnes = gam.getUserNames("alumnes");
        System.out.println("Users (Alumnes): "+Arrays.asList(usernamesAlumnes));
    }
    
    
    public static void mostraNumsXML(){
        System.out.println("**********+++++++++++*********************");
        System.out.println("***** Estadístiques Fitxer XML XESTIB ****");
        System.out.println("\tNum. Professors: "+profesXML.size());
    }
    
}



        //gam.printAlumnes();
        
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
      
        //professors = gam.getUsers("professors");
        //comparaProfes(professors, profesXML);
        
        //GOrg o = gam.getOrgUnit("alumnes/tmv22d");
        //System.out.println(o);
        /*
        
        printUnits();
        
        for(Grup g: grupsXML){
            gam.creaUnitatOrg(g.nomCanonic, g.descripcio+"-"+g.nom, "alumnes");
        }
        
        
        
        //gam.provant();
        professors = gam.getUsers("professors");
        comparaProfes(professors, profesXML);
        

        */