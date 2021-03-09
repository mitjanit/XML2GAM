
package xml2gam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
    static final boolean suspensionEnabled = false;
    static final String MEUEMAIL = "aramirez@iesmanacor.cat";
    static final boolean useHotSpot = false;
    static final String defaultPassword="iesmanacor2021";
    static final String CSVFILE = "C:\\Users\\ToniMitjanit\\Desktop\\alumnes.csv";
    
    /**
     * Conjunt de noms canònics dels grups de correu de tutoria (eso, batx, fp i fpb).
     */
    public static final String[] TUTORIES = {"tutoria.eso", "tutoria.batxillerat", "tutoria.fpb", "tutoria.fp"};
     /**
     * Noms d'usuaris dels membres de l'equip directiu i del grup directiva@iesmanacor.cat.
     */
    static final String[] DIRECTIVA = {"fsapena", "mamengual", "pcaldentey", "mfuster", "csorell", "mreyes", "jsimo", "mnicolau"};
    
    static final String[] TAGS = {"bi1", "bi2", "batx1", "batx2", "eso1", "eso2", "eso3", "eso4", "esoee", "adg21", "adg32", "com11", "com21", "com31", "ele21", "ele31", "ifc21", "ifc31", "ifc33", "tmv11", "tmv21", "tmv22", "tmv31", "paccgm"};
    
    static GAM gam;
    static ArrayList<GGrup> cursos;
    static ArrayList<GGrup> grupsAlumnes;
    static ArrayList<GGrup> deps = new ArrayList<GGrup>();
    static ArrayList<GGrup> grups = new ArrayList<GGrup>();
    static ArrayList<GOrg> orgs;
    static ArrayList<GOrg> orgsAlumnes;
    
    /**
     * Col·lecció dels usuaris professors del domini GSuite.
     */
    static ArrayList<GUser> professors = new ArrayList<GUser>();
    public static ArrayList<String> suspendedProfes;
    public static ArrayList<String> emailsProfes;
    
    /**
     * Col·lecció dels usuaris alumnes del domini GSuite.
     */
    public static ArrayList<GUser> alumnes;
    public static ArrayList<String> suspendedAlumnes;
    public static ArrayList<String> emailsAlumnes;
    
    /**
     * Mapa de Hashing dels noms d'usuaris dels professors del domini GSuite.
     */
    public static HashMap<String, Integer> usernamesProfes;
    
    /**
     * Mapa de Hashing dels noms d'usuaris dels alumnes del domini GSuite.
     */
    public static HashMap<String, Integer> usernamesAlumnes;
    
    /**
     * Mapa de Hashing dels calendaris del grups educatius.
     */
    public static HashMap<String, String> calendarsGrupsAlumnes;
    
    
    /**
     * Mapa de Hashing dels emails de grups de correus dels departaments del domini GSuite.
     */
    public static HashMap<String, String> emailsDeps;
    public static HashMap<String, String> emailsGrupsAlumnes;
    
    public static HashMap<String, String> setEmailsGrupsAlumnes() {
        
        HashMap<String, String> emailsGrups = new HashMap();

        // 1r ESO
        emailsGrups.put("471994", "eso1a@iesmanacor.cat");
        emailsGrups.put("471995", "eso1b@iesmanacor.cat");
        emailsGrups.put("471996", "eso1c@iesmanacor.cat");
        emailsGrups.put("471997", "eso1d@iesmanacor.cat");
        emailsGrups.put("471998", "eso1e@iesmanacor.cat");
        emailsGrups.put("491854", "eso1f@iesmanacor.cat");
        emailsGrups.put("491855", "eso1g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso1h@iesmanacor.cat");
        // 2n ESO
        emailsGrups.put("472004", "eso2a@iesmanacor.cat");
        emailsGrups.put("472005", "eso2b@iesmanacor.cat");
        emailsGrups.put("472006", "eso2c@iesmanacor.cat");
        emailsGrups.put("472007", "eso2d@iesmanacor.cat");
        emailsGrups.put("472008", "eso2e@iesmanacor.cat");
        emailsGrups.put("472009", "eso2f@iesmanacor.cat");
        emailsGrups.put("491856", "eso2g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso2h@iesmanacor.cat");
        // 3r ESO
        emailsGrups.put("472010", "eso3a@iesmanacor.cat");
        emailsGrups.put("472011", "eso3b@iesmanacor.cat");
        emailsGrups.put("472012", "eso3c@iesmanacor.cat");
        emailsGrups.put("472013", "eso3d@iesmanacor.cat");
        emailsGrups.put("472014", "eso3e@iesmanacor.cat");
        emailsGrups.put("472015", "eso3f@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso3g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso3h@iesmanacor.cat");
        // 4t ESO
        emailsGrups.put("472016", "eso4a@iesmanacor.cat");
        emailsGrups.put("472017", "eso4b@iesmanacor.cat");
        emailsGrups.put("472018", "eso4c@iesmanacor.cat");
        emailsGrups.put("472019", "eso4d@iesmanacor.cat");
        emailsGrups.put("472020", "eso4e@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso4f@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso4g@iesmanacor.cat");
        //emailsGrups.put("xxxx", "eso4h@iesmanacor.cat");
        // 1r Batx
        emailsGrups.put("472000", "batx1a@iesmanacor.cat");
        emailsGrups.put("472001", "batx1b@iesmanacor.cat");
        emailsGrups.put("472002", "batx1c@iesmanacor.cat");
        emailsGrups.put("472003", "batx1d@iesmanacor.cat");
        //emailsGrups.put("xxxxx", "batx1e@iesmanacor.cat");
        // 2n Batx
        emailsGrups.put("472021", "batx2a@iesmanacor.cat");
        emailsGrups.put("472022", "batx2b@iesmanacor.cat");
        emailsGrups.put("472023", "batx2c@iesmanacor.cat");
        emailsGrups.put("472024", "batx2d@iesmanacor.cat");
        //emailsGrups.put("xxxxx", "batx2e@iesmanacor.cat");
        // ADG21
        emailsGrups.put("474647", "adg21a@iesmanacor.cat");
        emailsGrups.put("474648", "adg21b@iesmanacor.cat");
        emailsGrups.put("474662", "adg21c@iesmanacor.cat");
        // ADG32
        emailsGrups.put("474669", "adg32a@iesmanacor.cat");
        emailsGrups.put("474670", "adg32b@iesmanacor.cat");
        emailsGrups.put("474671", "adg32c@iesmanacor.cat");
        // COM11
        emailsGrups.put("474687", "com11a@iesmanacor.cat");
        emailsGrups.put("474688", "com11b@iesmanacor.cat");
        emailsGrups.put("474689", "com11c@iesmanacor.cat");
        // COM21
        emailsGrups.put("471954", "com21a@iesmanacor.cat");
        emailsGrups.put("471955", "com21b@iesmanacor.cat");
        emailsGrups.put("471959", "com21c@iesmanacor.cat");
        // COM31
        emailsGrups.put("474676", "com31a@iesmanacor.cat");
        emailsGrups.put("474677", "com31b@iesmanacor.cat");
        emailsGrups.put("474678", "com31c@iesmanacor.cat");
        // TMV11
        emailsGrups.put("474683", "tmv11a@iesmanacor.cat");
        emailsGrups.put("474685", "tmv11b@iesmanacor.cat");
        emailsGrups.put("474686", "tmv11c@iesmanacor.cat");
        // TMV21
        emailsGrups.put("474628", "tmv21a@iesmanacor.cat");
        emailsGrups.put("474629", "tmv21b@iesmanacor.cat");
        emailsGrups.put("474632", "tmv21c@iesmanacor.cat");
        emailsGrups.put("474660", "tmv21d@iesmanacor.cat");
        // TMV22
        emailsGrups.put("474649", "tmv22a@iesmanacor.cat");
        emailsGrups.put("474650", "tmv22b@iesmanacor.cat");
        emailsGrups.put("474651", "tmv22c@iesmanacor.cat");
        emailsGrups.put("474652", "tmv22d@iesmanacor.cat");
        emailsGrups.put("474661", "tmv22e@iesmanacor.cat");
        // TMV31
        emailsGrups.put("474672", "tmv31a@iesmanacor.cat");
        emailsGrups.put("474673", "tmv31b@iesmanacor.cat");
        emailsGrups.put("474674", "tmv31c@iesmanacor.cat");  
        // ELE21
        emailsGrups.put("474653", "ele21a@iesmanacor.cat");
        emailsGrups.put("474654", "ele21b@iesmanacor.cat");
        emailsGrups.put("474663", "ele21c@iesmanacor.cat");
        // ELE31
        emailsGrups.put("474680", "ele31a@iesmanacor.cat");
        emailsGrups.put("474681", "ele31b@iesmanacor.cat");
        emailsGrups.put("474682", "ele31c@iesmanacor.cat");
        // IFC21
        emailsGrups.put("474655", "ifc21a@iesmanacor.cat");
        emailsGrups.put("474656", "ifc21b@iesmanacor.cat");
        emailsGrups.put("474657", "ifc21c@iesmanacor.cat");
        emailsGrups.put("474658", "ifc21d@iesmanacor.cat");
        emailsGrups.put("474664", "ifc21e@iesmanacor.cat");
        // IFC31
        emailsGrups.put("474666", "ifc31a@iesmanacor.cat");
        emailsGrups.put("474667", "ifc31b@iesmanacor.cat");
        emailsGrups.put("474668", "ifc31c@iesmanacor.cat");
        // IFC33
        emailsGrups.put("471974", "ifc33a@iesmanacor.cat");
        emailsGrups.put("471975", "ifc33b@iesmanacor.cat");
        emailsGrups.put("471976", "ifc33c@iesmanacor.cat");
        // EEBASICA             
        emailsGrups.put("475427", "esoeea@iesmanacor.cat");
        
        return emailsGrups;
    }
    
    public static HashMap<String, String> setCalendarsGrupsAlumnes() {
        
        HashMap<String, String> calendarsGrups = new HashMap();

        // 1r ESO
        calendarsGrups.put("eso1a", "c_188dcf6qc5u7qgeqmehnnclofb74e4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1b", "c_188apa4lu1l8gh61nc0ohc9hn50ns4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1c", "c_18849i6pmv8jgh52go6qvuvocsmmm4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1d", "c_188d8b7a3cv72g55m965hk5liphgi4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1e", "c_1885ct6mfh8kggnuhp53v0oqhts8g4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1f", "c_188ft83ij8ckqgp9jgqql630lfu844ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1g", "c_188e9ovqeet9ejdqgeoe2ofv8nqjk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso1h", "c_1880mf7khrf8cgs0kio18pal43e4e4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 2n ESO
        calendarsGrups.put("eso2a", "c_1882usr9lehg6jmal3be65bta8aqa4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2b", "c_188db946f8q0aikqkoo2l53kdgkd44ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2c", "c_1885pu0b8m688gi4n9sm19sjv5s024ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2d", "c_1889s4o1pj3lmhimj2s9ndhdir0ku4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2e", "c_1881ao2f58eiiim8htbfa6b9r8d8a4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2f", "c_188aa77as6f2ui5vnl7h0gu5nafk24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso2g", "c_18855tr1sdeesh7vg0vp9ls4e8op84ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 3r ESO
        calendarsGrups.put("eso3a", "c_188bi2gfq1pm0g0nhe11f0rg6qg4s4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3b", "c_1888ti2kpombkhqsnq95q3rr6usuo4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3c", "c_1889rp621rdc8j8onc1e4vcmc16924ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3d", "c_1885v7l14rn60g8jg909e833oufbq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3e", "c_188bsbd2gc91aic9nic4437nbtrv24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso3f", "c_1886et3cjs9oej43i2lspgbk0aohm4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 4t ESO
        calendarsGrups.put("eso4a", "c_188fjpr8vb4g4gf9mu51r873q8tr64ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4b", "c_188bplcv2127sjdhklpc90k0b57544ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4c", "c_188e3ppqur5oci66kt4qejnvnsfv24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4d", "c_18862oop2r6u4g6knkuobcu1il2rc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4e", "c_188577oiaa9goganinmj56ms5e0nq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("eso4f", "c_1888kp8aijiooh1lkmi4h7tkvkd5q4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 1r Batx
        calendarsGrups.put("batx1a", "c_1883r33jar55ei6fh87a0ag9orb3u4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1b", "c_1885citehufiajjugi1cbk0phlfkc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1c", "c_18889gkkg50h6jh1gmplmn4jbjmri4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1d", "c_188af98o05rsgj4om2dn85prvosma4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx1e", "c_188arfv29u0peghhg97ksm16pj1504ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // 2n Batx
        calendarsGrups.put("batx2a", "c_1889dckj25jb2g0umd6ppnufhd9no4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx2b", "c_1889vk37te9g0hstlkg5fbq8eieii4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx2c", "c_188etp7jsqqamhlek913ndihgfuc04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("batx2d", "c_188916scijs76gvfm0j0bbbo6v1c24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ADG21
        calendarsGrups.put("adg21a", "c_1889lvtsqf29qhlik4mn6tfponiio4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg21b", "c_188f36mphhe6ehbdjlh1q4vtts3g04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg21c", "c_188289g04hk9mgjoj43n4djch7g6g4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ADG32
        calendarsGrups.put("adg32a", "c_188b7b4o3n7n6i66j79v6vh4dmbim4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg32b", "c_18825qt5bsou2hidjtl864hnmpplc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("adg32c", "c_18854779j6h00hitk7bmec5nmjpt04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // COM11
        calendarsGrups.put("com11a", "c_188dd04g3dblkgk1m5bbb3jdpcso04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com11b", "c_18867mtv3pkkigeom7ieuq947oore4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com11c", "c_188auhbas4b5chq2l0grpektjm9l04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // COM21
        calendarsGrups.put("com21a", "c_18863jlabrbm0i6lijdlhk6m6ln184ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com21b", "c_1886tlpnhq6fkj5ki1qrnccrngviu4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com21c", "c_1880ek582a3n8hppmb2lf6q04dedk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // COM31
        calendarsGrups.put("com31a", "c_18878mm6r74cihgbh575m2p3f0f044ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com31b", "c_1889q9pl38seuidegi2t5jh5e9mks4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("com31c", "c_188bn6subrpq6gr4ir4gk9m9bo06s4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV11
        calendarsGrups.put("tmv11a", "c_18859deji9ptggpkg0mvr77m3o6p24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv11b", "c_188bvie2jf2q2i83m5baa5jp2rii04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv11c", "c_188cmuf71f300g9emnif2u26rishq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV21
        calendarsGrups.put("tmv21a", "c_1887q0bdt5l1qgn8n47vbifvmeau04ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv21b", "c_1888sio4pg47qjaon7harovucuauc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv21c", "c_188cotj3c4c08gqcn2djj2pllpblk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv21d", "c_1883966d4eqd8isbnlv3o4le68to64ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV22
        calendarsGrups.put("tmv22a", "c_188bluc0k9c2ci2pmnhmof1g7vglc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22b", "c_18814dqqf7etii37g886779gge4dq4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22c", "c_1884i8fuuv6g0g6rk5ja64lqtgl464ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22d", "c_1886o4rg6epcch51jraa2e95uc2n24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv22e", "c_188atnqs9jq2cg2uk7n0glfgf07qi4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // TMV31
        calendarsGrups.put("tmv31a", "c_1881uvpg8kv62ivsimjji2c9p39404ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv31b", "c_18880rbja7rfuhvtio2iqevnatroe4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("tmv31c", "c_1887el2qeimnmhm0mn8r2v6d2gudk4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");  
        // ELE21
        calendarsGrups.put("ele21a", "c_188akicvc3f68ggpk5jqr4lus8m064ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele21b", "c_1882f9mlb37okj5clmkenvavhb94i4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele21c", "c_18802mbffkp4ugpsibcmfbies5fm44ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // ELE31
        calendarsGrups.put("ele31a", "c_18811dt3rrrpojqngst2nlprl35vg4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele31b", "c_1886k33ef0u7ui2bhrf75mu0423u24ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ele31c", "c_188egas7vvj8ggi6k357gvue96ntg4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // IFC21
        calendarsGrups.put("ifc21a", "c_18848d5tmddesjpgl9opsov5kitrc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21b", "c_18849epqhonr6ikogj1sh8733o4ti4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21c", "c_1883s9vtdk8iihslgl8pjg6v3k40g4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21d", "c_188f1cq0gc58ej1rm1j7733ku759m4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc21e", "c_188d8paija0cgiitgfoammh7bdl2s4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // IFC31
        calendarsGrups.put("ifc31a", "c_1887l4ipima70gpfn7a14bj55sfbu4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc31b", "c_18813l33g0cp8jcuhr2n3okoor5fe4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc31c", "c_188cb1inrkjgoj4jigfqie7nmbcrc4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // IFC33
        calendarsGrups.put("ifc33a", "c_188bugl5859cmjacmheh9ag7mhvba4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc33b", "c_188dunoes5a2uhfmhdavqidfofbgu4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        calendarsGrups.put("ifc33c", "c_1880eg9rckn1aj58lgr3jc5r9v7404ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        // EEBASICA             
        calendarsGrups.put("esoeea", "c_188ed8a9m0go8iclg8ub31uv3ibhs4ged5in6rb1dpgm6rri5phm2t0@resource.calendar.google.com");
        
        return calendarsGrups;
    }
    
    
    /**
     * Mètode per emplenar el HashMap dels grups de correus dels Departaments amb el seu ID (XML) i EMAIL (GSUITE).
     * @return HashMap amb tuples (ID Departament_XML , EMAIL Departament GSUITE).
     */
    public static HashMap<String, String> setEmailsDeps() {
        HashMap<String, String> emailsDeps = new HashMap();

        emailsDeps.put("1130", "dept.administracio@iesmanacor.cat");
        emailsDeps.put("1134", "dept.automocio@iesmanacor.cat");
        emailsDeps.put("1136", "dept.biologia@iesmanacor.cat");
        emailsDeps.put("1124", "dept.castella@iesmanacor.cat");
        emailsDeps.put("1125", "dept.catala@iesmanacor.cat");
        emailsDeps.put("1131", "dept.comerc@iesmanacor.cat");
        emailsDeps.put("1119", "dept.educaciofisica@iesmanacor.cat");
        emailsDeps.put("1132", "dept.electricitat@iesmanacor.cat");
        emailsDeps.put("1120", "dept.filosofia@iesmanacor.cat");
        emailsDeps.put("1121", "dept.fisicaiquimica@iesmanacor.cat");
        emailsDeps.put("1122", "dept.fol@iesmanacor.cat");
        emailsDeps.put("1133", "dept.informatica@iesmanacor.cat");
        emailsDeps.put("1137", "dept.llenguesestrangeres@iesmanacor.cat");
        emailsDeps.put("1126", "dept.matematiques@iesmanacor.cat");
        emailsDeps.put("1127", "dept.musica@iesmanacor.cat");
        emailsDeps.put("1117", "dept.orientacio@iesmanacor.cat");
        emailsDeps.put("1135", "dept.plastica@iesmanacor.cat");
        emailsDeps.put("1138", "dept.socials@iesmanacor.cat");
        emailsDeps.put("1128", "dept.tecnologia@iesmanacor.cat");
        emailsDeps.put("7269", "dept.llenguesiculturaclassiques@iesmanacor.cat");

        return emailsDeps;
    }
    
    /**
     * Crea els grups d'alumnes de forma fixa per evitar llegir els grups del GAM.
     * Pot crear grups no reals (cicles amb grup E).
     */
    public void setGrupsAlumnes(){
        grupsAlumnes = new ArrayList<GGrup>();
        GGrup g;
        String[] lletres = {"a", "b", "c", "d", "e", "f", "g"};
        
        for(String tag : TAGS){
            for(String lletra : lletres){
            String nomGrup = tag+lletra;
            g = new GGrup(nomGrup,nomGrup,nomGrup+"@iesmanacor.cat");
            }
        }
    }
    
    
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
    
    // Crea els grups de GSuite corresponents als equips educatius
    public static void creaGrupsEquipsEducatius(HashMap<String, String> emailsGrups) {
        for (String s : emailsGrups.values()) {
            String email = "ee."+s;
            int at = s.indexOf("@");
            String nom = "EE " + s.substring(0, at);
            String desc = "Equip Educatiu " + s.substring(0, at).toUpperCase();
            System.out.println("Creant grup " + email + " per a " + nom + " (" + desc + ")");
            gam.creaGrup(email, nom, desc);
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
    
    public static boolean professorDinsDomini(Professor profeXML){
        String codiProfeXML = profeXML.getCodi();
        System.out.println("CODI XESTIB NOU PROFE: "+codiProfeXML);
        for(GUser u: professors){
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
        
        // Inicialitza Hash de Noms d'Usuaris dels Professors GSuite
        usernamesProfes = new HashMap<String, Integer>();
        usernamesAlumnes = new HashMap<String, Integer>();
        emailsAlumnes = new ArrayList<String>();
        emailsProfes = new ArrayList<String>();
        
        // Carrega els codis i emails dels grups de departaments (fixe - no canviar mai)..
        emailsDeps = setEmailsDeps();
        
        // Carrega els codis i emails dels grups d'alumnes (fixe - cada any canviar a principi de curs amb el 1r XML).
        emailsGrupsAlumnes = setEmailsGrupsAlumnes();
        
        // Carrega els codis dels calendars dels equips educatius
        calendarsGrupsAlumnes = setCalendarsGrupsAlumnes();
        
        // Crear grups d'alumnes
        //gam.creaCalendarisGrups();
        //gam.creaCalendariRecurs("CalFP", "Calendari FP IES Manacor", "Calendari FP IES Manacor");
        //gam.creaCalendariRecurs("CalESO", "Calendari ESO IES Manacor", "Calendari FP IES Manacor");
        //gam.creaCalendariRecurs("CalBATX", "Calendari BATXILLER IES Manacor", "Calendari Batxiller IES Manacor");
        //gam.creaCalendariRecurs("TMV11C", "Calendari Grup TMV11C", "Calendari del grup de classe TMV11C");
        
        llegirEmailsAlumnesFITXER();
        /*
        System.out.println(emailsAlumnes.size());
        for(String s : emailsAlumnes){
            //gam.afegirGrupUsuari(s, "alumnes@iesmanacor.cat");
            System.out.println("Afegint "+s+ "alumnes@iesmanacor.cat");
        }*/
        
        llegirEmailsProfesFITXER();
        /*
        System.out.println(emailsProfes.size());
        for(String s : emailsProfes){
            System.out.println(s);
        }
        */
        llegirUsernamesProfes(emailsProfes);
        /**/
        //System.out.println(usernamesProfes);
        /**/
        
        
        //printTutorsESO(xml);
        //printTutorsBATX(xml);
        //printTutorsFP(xml);
        
        // Afegir les grups als alumnes
        System.out.println("GRUPS i CALENDARS");
        for(String grup : calendarsGrupsAlumnes.keySet()){
            
            String email = "ee."+grup+"@iesmanacor.cat";
            
            //gam.afegirCalendariEquipEducatiu(email, grup);
            
            email = grup+"@iesmanacor.cat";
            System.out.println(email);
            //gam.afegirCalendariAlumne(email, grup);
        }
        
        
        //llegirUsuarisCreatsFitxer("2018-10-01");
        if(useHotSpot){
            HotSpot hs = new HotSpot();
            hs.connectHotSpot();
        }
        
        //creaGrupsEquipsEducatius(emailsGrupsAlumnes);
        
        while(true){
            int numOpcio = printOpcionsMenuGAM();
            switch(numOpcio){
                case 0: System.exit(0); //Sortir
                case 1: profesXML = llegirProfesXML(xml); break;
                case 2: alumnesXML = llegirAlumnesXML(xml); break;
                case 3: grupsXML = llegirGrupsXML(xml); break;
                case 4: professors = llegirProfesGAM(emailsProfes); break;
                case 5: alumnes = llegirAlumnesGAM(gam); break;
                case 6: llegirUsernamesProfesGAM(gam); break;
                case 7: llegirUsernamesAlumnesGAM(gam); break;
                case 8: llegirOrganitGAM(gam, orgs); break;
                case 9: llegirGrupsGAM(gam); break;
                case 10: llegirDepsGAM(gam); break;
                case 11: comparaProfes(professors, profesXML, emailsDeps); break;
                case 12: comparaAlumnes(alumnesXML); break;
                case 13: creaProfeConsola(); break;
                case 14: creaAlumneConsola(); break;
                case 15: afegirProfesGrups(gam,xml); break;
                case 16: //llegirEmailsAlumnes(gam); 
                         afegirAlumnes(gam); break;
                case 17: removeUsersFromGroup(gam);break;
                case 18: removeTAGname(gam, emailsAlumnes);break;
                //case 19: actualitzaNomAlumnes(alumnesXML); break;
                case 19: actualitzaTagAlumnes(gam, alumnesXML); break;
                case 20: suspenAlumnes(gam, emailsAlumnes, alumnesXML);
                case 21: suspenProfes(gam, professors, profesXML);
                case 22: afegirAlumnesAlseusGrups(gam, alumnesXML, emailsGrupsAlumnes); break;
                case 23: mouAlumnesAUnitatOrganitzativa(gam, alumnesXML, emailsGrupsAlumnes); break;
                case 24: llistarAlumnesSuspesosGSUITE(gam); break;
                case 25: llistarProfessorsSuspesosGSUITE(gam); break;
                case 26: llistarAlumnesSuspesosFITXER(); break;
                case 27: llistarProfessorsSuspesosFITXER(); break;
                case 28: removeSuspendedAlumnes(gam); break;
                case 29: removeSuspendedProfes(gam); break;
                case 30: printAlumnesXMLperGrup(xml, gam); break;
                case 31: afegirTutorsESO(xml, gam, professors); break;
                default: break;
            }
        }
        
     
    }
    
    // S'atasca
    public static void llistarAlumnesSuspesosGSUITE(GAM gam){
        gam.printSuspendedAlumnes();
    }
    
    // S'atasca
    public static void llistarProfessorsSuspesosGSUITE(GAM gam){
        gam.printSuspendedProfessors();
    }
    
    public static void llistarAlumnesSuspesosFITXER(){
        
        suspendedAlumnes = new ArrayList<String>();
        
        // Cal executar a la consola la comanda GAM seguent:
        // gam print users query \"orgUnitPath=/"+unitat+" isSuspended=true \""
        // Copiar els emails al fitxer suspendedAlumnes.txt a l'escriptori
        
        System.out.println("ALUMNES SUSPESOS: \n");
        
        try {
                File file = new File("C:\\Users\\ToniMitjanit\\Desktop\\suspendedAlumnes.txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		
                while ((line = bufferedReader.readLine()) != null) {
			suspendedAlumnes.add(line.trim());
                        System.out.println(line.trim());
		}
		fileReader.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
        
        System.out.println("\nNUM. ALUMNES SUSPESOS (suspendedAlumnes): "+suspendedAlumnes.size());
    }
    
    public static void llistarProfessorsSuspesosFITXER(){
        
        suspendedProfes = new ArrayList<String>();
        
        // Cal executar a la consola la comanda GAM seguent:
        // gam print users query \"orgUnitPath=/"+unitat+" isSuspended=true \""
        // Copiar els emails al fitxer suspendedAlumnes.txt a l'escriptori
        
        System.out.println("PROFESSORS SUSPESOS: \n");
        
        try {
                File file = new File("C:\\Users\\ToniMitjanit\\Desktop\\suspendedProfes.txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		
                while ((line = bufferedReader.readLine()) != null) {
			suspendedProfes.add(line.trim());
                        System.out.println(line.trim());
		}
		fileReader.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
        
        System.out.println("\nNUM. PROFESSORS SUSPESOS (suspendedProfes): "+suspendedProfes.size());
    }
    
    public static void removeSuspendedAlumnes(GAM gam){
        llistarAlumnesSuspesosFITXER();
        for(String email : suspendedAlumnes){
            gam.deleteUser(email);
        }   
    }
    
    public static void removeSuspendedProfes(GAM gam){
        llistarProfessorsSuspesosFITXER();
        for(String email : suspendedProfes){
            gam.deleteUser(email);
        }
    }
    
    public static void mouAlumnesAUnitatOrganitzativa(GAM gam, ArrayList<Alumne> alumnesXML, HashMap<String, String> emailsGrupsAlumnes){
        System.out.println(">> Afegint la Unitat Organitzativa corresponent als "+alumnesXML.size()+".");
        for(Alumne a : alumnesXML){
            String username = gam.generateUserName(a);
            String emailAlumne =username+"@alumnes.iesmanacor.cat";
            if(a.codiGrup!=null){
                String codiGrup = a.codiGrup;
                if(emailsGrupsAlumnes.containsKey(codiGrup)){
                    String emailGrup = emailsGrupsAlumnes.get(codiGrup);
                    int atpos = emailGrup.indexOf('@');
                    String u0 = emailGrup.substring(0,atpos-2);
                    String u1 = emailGrup.substring(0,atpos-1);
                    if(emailGrup.indexOf("eso")==-1 && emailGrup.indexOf("batx")==-1){
                        u0 = "fp";
                    }
                    else if(emailGrup.indexOf("esoee")!=-1){
                        u0 = "eso"; u1 = "esoee";
                    }
                    String unitat = "/alumnes/"+u0+"/"+u1;
                    if(DEBUG){
                        System.out.println("Afegint l'usuari "+emailAlumne+" a la Unitat Organitzativa "+unitat);
                    }
                    gam.setUnitatOrganitzativaUsuari(emailAlumne, unitat);
                }
            }
        }
    }
    
    
    public static void afegirAlumnesAlseusGrups(GAM gam, ArrayList<Alumne> alumnesXML, HashMap<String, String> emailsGrupsAlumnes){
        for(Alumne a : alumnesXML){
            String username = gam.generateUserName(a);
            String emailAlumne =username+"@alumnes.iesmanacor.cat";
            String codiGrup = a.codiGrup;
            String emailGrup = emailsGrupsAlumnes.get(codiGrup);
            if(DEBUG){
                System.out.println("Afegint l'usuari "+emailAlumne+" al grup "+emailGrup);
            }
            gam.afegirGrupUsuari(emailAlumne, emailGrup);
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
        System.out.println("12) GAM :: Sincronitza Alumnes entre XML Xestib i GSuite IESManacor.");
        System.out.println("13) GAM :: Crea professor/a a través de consola en el domini GSUITE IESManacor.");
        System.out.println("14) GAM :: Crea alumne a través de consola en el domini GSUITE IESManacor.");
        System.out.println("15) GAM :: Afegeix els professors als grups de classe en el domini GSUITE IESManacor.");
        System.out.println("16) GAM :: Afegeix els alumnes al domini GSUITE IESManacor.");
        System.out.println("17) GAM :: Esborra els alumnes dels grups d'alumnes.");
        System.out.println("18) GAM :: Esborra TAGs dels noms dels alumnes.");
        //System.out.println("19) GAM :: Actualitza els noms dels alumnes (corregir accents).");
        System.out.println("19) GAM :: Actualitza els llinatges dels alumnes (afegeix TAG grup).");
        System.out.println("20) GAM :: Suspen els alumnes GAM que no apareixen al fitxer XML.");
        System.out.println("21) GAM :: Suspen els professors GAM que no apareixen al fitxer XML.");
        System.out.println("22) GAM :: Afegir els alumnes GAM al seu grup de classe d'acord amb XML.");
        System.out.println("23) GAM :: Afegir els alumnes a la seva Unitat Organitzativa d'acord amb XML.");
        System.out.println("24) GAM :: Llegir els alumnes suspesos des del domini GSUITE.");
        System.out.println("25) GAM :: Llegir els professors suspesos des del domini GSUITE.");
        System.out.println("26) GAM :: Llegir els alumnes suspesos des del FITXER suspendedAlumnes.txt.");
        System.out.println("27) GAM :: Llegir els professors suspesos des del FITXER suspendedProfes.txt.");
        System.out.println("28) GAM :: Eliminar alumnes suspesos (FITXER suspendedProfes.txt).");
        System.out.println("29) GAM :: Eliminar professors suspesos (FITXER suspendedProfes.txt).");
        System.out.println("30) XML :: Imprimir alumnes XML per Grups.");
        System.out.println("31) XML & GAM:: Afegir tutors al grup de Tutors d'ESO (4).");
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
        ArrayList<Professor> profesXML = xml.llegirProfes();
        if(DEBUG){
            System.out.println("\tProfessors Llegits del fitxer XML.");
            System.out.println("\tCodi\tNom\tAp1\tAp2\tUsuari\tCodiDep\tTutor");
            for(Professor p : profesXML){
                System.out.println("\t"+p);
            }
        }
        return profesXML;
    }
    
    public static void creaProfeConsola(){
        Professor p = llegirProfeConsola();
        if(professorDinsDomini(p)){ 
            System.out.println("El professor " + p + " ja està en el Domini GSuite.");
        }
        else {
                System.out.println("El professor " + p + " no està en el Domini GSuite. Cal afegir-lo!!! \tCodi Xestib: "+p.getCodi());
                gam.afegirProfessor(p, emailsDeps);
            }
    }
    
    public static void creaAlumneConsola(){
        Alumne a = llegirAlumneConsola();
        //if(alumneDinsDomini(a)){ 
        if(false){ 
            System.out.println("L'alumne " + a + " ja està en el Domini GSuite.");
        }
        else {
                System.out.println("L'alumne " + a + " no està en el Domini GSuite. Cal afegir-lo!!! \tCodi Xestib: "+a.getCodi());
                gam.afegirAlumne(a, grups);
            }
    }
    
    public static Professor llegirProfeConsola(){
        String codi, nom, ap1, ap2, usuari, departament;
        System.out.println("****** Introduir dades del professor/a ******");
        System.out.print(">>>>> Indica codi XESTIB: ");
        Scanner teclat = new Scanner(System.in);
        codi = teclat.next();
        System.out.print(">>>>> Indica nom del professor/a: ");
        teclat = new Scanner(System.in);
        nom = teclat.next();
        System.out.print(">>>>> Indica llinatge 1 del professor/a: ");
        teclat = new Scanner(System.in);
        ap1 = teclat.next();
        System.out.print(">>>>> Indica llinatge 2 del professor/a: ");
        teclat = new Scanner(System.in);
        ap2 = teclat.next();
        System.out.print(">>>>> Indica usuari del professor/a: ");
        teclat = new Scanner(System.in);
        usuari = teclat.next();
        System.out.print(">>>>> Indica codi del Departament del professor/a: ");
        teclat = new Scanner(System.in);
        departament = teclat.next();
        Professor p = new Professor(codi, nom, ap1, ap2, usuari, departament);
        return p;
    }
    
    public static Alumne llegirAlumneConsola(){
        String codi, nom, ap1, ap2, expedient, grup;
        System.out.println("****** Introduir dades de l'alumne/a ******");
        System.out.print(">>>>> Indica codi XESTIB: ");
        Scanner teclat = new Scanner(System.in);
        codi = teclat.next();
        System.out.print(">>>>> Indica nom del professor/a: ");
        teclat = new Scanner(System.in);
        nom = teclat.next();
        System.out.print(">>>>> Indica llinatge 1 del professor/a: ");
        teclat = new Scanner(System.in);
        ap1 = teclat.next();
        System.out.print(">>>>> Indica llinatge 2 del professor/a: ");
        teclat = new Scanner(System.in);
        ap2 = teclat.next();
        System.out.print(">>>>> Indica número d'expedient de l'alumne/a: ");
        teclat = new Scanner(System.in);
        expedient = teclat.next();
        System.out.print(">>>>> Indica codi del Grup de l'alumne/a: ");
        teclat = new Scanner(System.in);
        grup = teclat.next();
        Alumne a = new Alumne(codi, nom, ap1, ap2, expedient, grup);
        return a;
    }
    
    public static ArrayList<Alumne> llegirAlumnesXML(XMLReader xml){
        ArrayList<Alumne> alumnesXML = new ArrayList<Alumne>();
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        System.out.print(">>>>> Indica nom del grup (ex: ifc31b) o (tots): ");
        Scanner teclat = new Scanner(System.in);
        String nomGrup = teclat.next();
        if(nomGrup.equalsIgnoreCase("tots")){
            alumnesXML = xml.llegirAlumnes(grupsXML);
        }
        else {
            alumnesXML = xml.llegirAlumnesGrup(grupsXML, nomGrup);
        }
        if(DEBUG){
            System.out.println("Alumnes llegits del fitxer XML:");
            for(Alumne a : alumnesXML){
                String username = gam.generateUserName(a);
                String email =username+"@alumnes.iesmanacor.cat";
            
                String emailGrup = "";
                String grup ="SENSE GRUP";
                
                if(emailsGrupsAlumnes.containsKey(a.codiGrup)){
                    emailGrup = emailsGrupsAlumnes.get(a.codiGrup);
                    int at = emailGrup.indexOf("@");
                    at = (at!=-1)?at:0;
                    grup = emailGrup.substring(0, at);
                }
                System.out.println(a+","+grup+","+email);
            }
        }
        return alumnesXML;
    }
    
    public static void printAlumnesXMLperGrup(XMLReader xml, GAM gam){
        
        ArrayList<Alumne> alumnesXML = new ArrayList<Alumne>();
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        
        System.out.println("Guardar dins fitxer CSV??? (S/N)");
        Scanner entrada = new Scanner(System.in);
        String textEntrada = entrada.next();
        
        if(textEntrada.equals("S") || textEntrada.equals("s")){
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(CSVFILE));
                xml.printAlumnesCSV(writer, grupsXML, gam);
                writer.close();
            }
            catch(IOException e){
                System.out.println("Error de fitxer CSV");
            }
        }
        
        System.out.println("Mostrar per Pantalla (S/N)");
        textEntrada = entrada.next();
        if(textEntrada.equals("S") || textEntrada.equals("s")){
                
            for(Grup g : grupsXML){
                String nomGrup = g.nomCanonic;
                //System.out.println("*****************************************");
                //System.out.println(nomGrup.toUpperCase());
                xml.printAlumnesGrup(grupsXML, nomGrup, gam);
            }
        }
        
        
    }
    
    public static void printTutorsXMLperNivell(XMLReader xml, GAM gam){
        ArrayList<Professor> tutorsXML = new ArrayList<Professor>();
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        
        tutorsXML = xml.llegirProfesTutors(grupsXML);
        System.out.println("<<<<<<<<<< TUTORSSSSS >>>>>>>>>>");
        for(Professor p : tutorsXML){
            //if(p.tutorGrup!=null){
                System.out.println(p);
            //}
        }
    }
    
    public static void printTutorsESO(XMLReader xml){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        ArrayList<Professor> tutorsXML = xml.llegirProfesTutors(grupsXML, "eso");
        System.out.println("<<<<<<<<<< TUTORS D'ESO >>>>>>>>>>");
        for(Professor p : tutorsXML){
                System.out.println(p);
        }
    }
    
    public static void afegirTutorsESO(XMLReader xml, GAM gam, ArrayList<GUser> usersProfes){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        ArrayList<Professor> tutorsXML = xml.llegirProfesTutors(grupsXML, "eso");
        System.out.println("<<<<<<<<<< TUTORS D'ESO >>>>>>>>>>");
        for(GUser user : usersProfes){
            for(Professor p : tutorsXML){
                if(user.codiXestib.equalsIgnoreCase(p.codi)){
                    String emailUser = user.email;
                    String emailGrup = "turoria.eso@iesmanacor.cat";
                    System.out.println("AFEGINT "+emailUser+" a "+ emailGrup);
                    gam.afegirGrupUsuari(emailUser, emailGrup);
                    break;
                }

            }
        }
    }
    
    public static void printTutorsBATX(XMLReader xml){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        ArrayList<Professor> tutorsXML = xml.llegirProfesTutors(grupsXML, "batx");
        System.out.println("<<<<<<<<<< TUTORS DE BATXILLERAT >>>>>>>>>>");
        for(Professor p : tutorsXML){
                System.out.println(p);
        }
    }
    
    public static void printTutorsFP(XMLReader xml){
        ArrayList<Grup> grupsXML = xml.llegirGrups();
        ArrayList<Professor> tutorsXML = xml.llegirProfesTutors(grupsXML, "fp");
        System.out.println("<<<<<<<<<< TUTORS DE FP >>>>>>>>>>");
        for(Professor p : tutorsXML){
                System.out.println(p);
        }
    }
    
    
    //************************ OPERACIONS SOBRE EL DOMINI GSUITE ************//
    
    
    public static void llegirEmailsAlumnes(GAM gam){
        emailsAlumnes = new ArrayList<String>();
        emailsAlumnes = gam.getEmailsAlumnes();
    }
    
    public static void afegirAlumnes(GAM gam){
        for(Alumne a: alumnesXML){
            System.out.println("Afegint l'alumne "+a+" al domini GSUITE.");
            gam.afegirAlumne(a,grupsAlumnes);
        }
    }
    
    
    public static void comparaProfes(ArrayList<GUser> professors, ArrayList<Professor> profesXML, HashMap<String, String> emailsDeps){
        
        System.out.println("Num. Professors del GSUITE (GUsers): "+professors.size());
        System.out.println("Num. Professors del XML: "+profesXML.size());
        System.out.println("Num. Departaments del GSuite: "+emailsDeps.size());
        System.out.println("Num. Usernames Profes del GSuite (GUsers): "+usernamesProfes.size());
        
        ArrayList<GUser> users = new ArrayList<GUser>();
        users.addAll(professors);
         
        int numNous=0, numRepes=0;
        for(Professor p : profesXML){
            
            if(professorDinsDomini(p)){
                
                if(XML2GAM.DEBUG){
                    System.out.println("El professor " + p + " ja està en el Domini GSuite.");
                }
                
                GUser u = matchingProfessor(users,p);
                /*
                //
                //Actualitzar el grup del departament? (Sí/No)
                gam.actualitzaDepartament(u, p, deps);
                
                // Actualitzar el grup de tutors
                Grup tutorGrup = p.getGrupTutor();
                gam.actualitzaTutoria(u.email, tutorGrup);
                
                // Afegir al grup de Professors
                gam.afegirGrupUsuari(u.email, "professorat@iesmanacor.cat");
                //
                */
                users.remove(u);
                numRepes++;
                /**/
            }
            else {
                if(DEBUG){
                System.out.println("El professor " + p + " no està en el Domini GSuite. Cal afegir-lo!!! \tCodi Xestib: "+p.getCodi());
                System.out.println(p.usuari+","+defaultPassword+","+p.nom+","+p.ap1+" "+p.ap2+","+p.usuari+"@iesmanacor.cat,"+p.departament);
                }
                gam.afegirProfessor(p, emailsDeps);
                numNous++;
            }
        }
        System.out.println("Profes nous: "+numNous+", Profes actualitzats: "+numRepes+", Profes obsolets: "+users.size());
        
        // Profes Obsolets que s'han de suspendre
        if(users.size()>0 && suspensionEnabled){
            for(GUser u : users){
                System.out.println("Suspenent l'usuari: "+ u);
                gam.suspenUsuari(u.email);
            }
        }
        
    }
    
    public static boolean nomUsuariDinsDomini(ArrayList<String> emailsAlumnes, Alumne a){
        String username = gam.generateUserName(a);
        String email =username+"@alumnes.iesmanacor.cat";
        return emailsAlumnes.contains(email);
    }
    
    /**
     * Actualitza el llinatge dels alumnes GSuite afegint el TAG del seu grup.
     * @param gam   Objecte GAM
     * @param alumnesXML Informació dels alumnes extreta del fitxer XML del Xestib.
     */
    public static void actualitzaTagAlumnes(GAM gam, ArrayList<Alumne> alumnesXML){
        for(Alumne a : alumnesXML){
                gam.actualitzarTAG(a);
        }
    }
    
    public static void actualitzaNomAlumnes(ArrayList<Alumne> alumnesXML){
        //llegirEmailsAlumnesFITXER();
        for(Alumne a : alumnesXML){
            if(nomUsuariDinsDomini(emailsAlumnes, a)){ 
                gam.actualitzarNomiLlinatges(a);
            }
        }
    }
     
    public static void comparaAlumnes(ArrayList<Alumne> alumnesXML){
        
        // Llegeix els emails dels alumnes del fitxer txt
        llegirEmailsAlumnesFITXER();
        ArrayList<String> emailsSuspesos = new ArrayList<String>();
        for(String s : emailsAlumnes){
            String scopy = new String(s);
            emailsSuspesos.add(scopy);
        }
        System.out.println("**Clonat l'array de emails d'alumnes: "+emailsSuspesos.size());
        
        // Llegeix els usuaris GSUITE corresponents als
        //ArrayList<GUser> users = new ArrayList<GUser>();
        //users = llegirAlumnesGAM(emailsAlumnes);
        
        
        //cursos = gam.getGroups();  // Se queda lelo en llegir la info del grup professorat@iesmanacor.cat
         
        int numNous=0, numRepes=0;
        
        for(Alumne a : alumnesXML){
            //if(alumneDinsDomini(users, a)){
            if(nomUsuariDinsDomini(emailsAlumnes, a)){
                
                // AIXÒ PER ARREGLAR ACCENTS
                //gam.actualitzarNomiLlinatges(a);
                
                //System.out.println("L'alumne " + a + " ja està en el Domini GSuite.");
                //GUser u = matchingAlumne(users,a);
                
                //Actualitzar el grup? (Sí/No)
                //gam.actualitzaGrup(u, a, grups);

                //users.remove(u);
                String emailAlumne = gam.generateUserName(a)+"@alumnes.iesmanacor.cat";
                emailsSuspesos.remove(emailAlumne);
                numRepes++;
            }
            else {
                if(XML2GAM.DEBUG){
                    System.out.println("L'alumne " + a + " no està en el Domini GSuite. Cal afegir-lo!!! \tCodi Xestib: "+a.getCodi());
                }
                // gam.afegirAlumne(a, cursos);  // HE COMENTAT AIXÔ PERQUÊ CREC QUE SOBRA ?????
                gam.afegirAlumne2(a, XML2GAM.emailsGrupsAlumnes);
                numNous++;
            }
        }
        System.out.println("Alumnes nous: "+numNous);
        System.out.println("Actualitzats: "+numRepes);
        System.out.println(", obsolets: "+emailsSuspesos.size());
        
        // Alumnes Obsolets
        /**/
        if(emailsSuspesos.size()>0 && XML2GAM.suspensionEnabled){
            for(String s : emailsSuspesos){
                System.out.println("Suspenent l'usuari: "+ s);
                gam.suspenUsuari(s);
            }
        }
        /**/
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
    
    public static ArrayList<GUser> llegirProfesGAM(ArrayList<String> emailsProfes){
        ArrayList<GUser> profes = new ArrayList<GUser>();
        for(String email : emailsProfes){
            GUser u = gam.getUserInfo(email);
            if(!u.isSuspended()){
                //System.out.println("Llegint info de "+email);
                profes.add(u);
            }
        }
        System.out.println(profes);
        return profes;
    }
    
    public static ArrayList<GUser> llegirAlumnesGAM(ArrayList<String> emailsAlumnes){
        ArrayList<GUser> alumnes = new ArrayList<GUser>();
        for(String email : emailsAlumnes){
            System.out.println("Llegint info de "+email);
            GUser u = gam.getUserInfo(email);
            alumnes.add(u);
        }
        System.out.println(alumnes);
        return alumnes;
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
    
    public static ArrayList<GUser> llegirAlumnesGAM(GAM gam){
        ArrayList<GUser> alumnes = new ArrayList<GUser>();
        String unitat ="alumnes";
        System.out.println("Membres del GSuite ("+DOMINI+") a la unitat "+unitat);
        System.out.println("Id\tName\tDescription\tEmail\tDepartament\tCodiDep");
        //gam.printUsers(unitat);
        alumnes = gam.getUsers(unitat);
        for(GUser u : alumnes){
            System.out.println(u);
        }
        return alumnes;
    }
    
    
    public static void llegirUsernamesProfesGAM(GAM gam){
        //Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Professors)
        usernamesProfes = gam.getUserNames("professors");
        System.out.println("Usernames ("+usernamesProfes.size()+"Profes): \n"+Arrays.asList(usernamesProfes));
    }
    
    public static void llegirUsernamesAlumnesGAM(GAM gam){
        //Llegir els noms d'usuaris i cardinalitat del Domini GSuite (Professors)
        usernamesAlumnes = gam.getUserNames("alumnes");
        System.out.println("Usernames ("+usernamesAlumnes.size()+" Alumnes): \n"+Arrays.asList(usernamesAlumnes));
    }
    
    // Afegeix els professors als grups de classe (batx1a) si tenen sessió docent amb ells
    // Cal llegir abans profes XML, professors GAM, grups GAM
    public static void afegirProfesGrups(GAM gam, XMLReader xml){
        ArrayList<SessioP> sessionsP = xml.llegirSessionsDocentsProfes();
        for(SessioP s : sessionsP){
            String emailProfe = emailProfe(s.codiProfessor);
            String emailGrup = emailGrup(s.codiGrup);
            System.out.println("Afegint el profe "+emailProfe+ " al grup "+emailGrup);
            gam.afegirGrupUsuari(emailProfe, emailGrup);
        }
    }
    
    // Retorna el email de professor a partir del seu codi Xestib
    public static String emailProfe(String codiXestibProfe){
        String emailProfe ="";
        for(GUser u: professors){
            if(u.codiXestib.equals(codiXestibProfe)){
                emailProfe = u.email;
                break;
            }
        }
        return emailProfe;
    }
    
    // Retorna el email del grup a partir del seu codi Xestib
    public static String emailGrup(String codiGrup){
        String emailGrup="";
        for(GGrup g : grups){
            String desc = g.description;
            // Si té el mateix codi && és un grup de classe
            if(desc.startsWith(codiGrup) && esEmailGrupClasse(g.email)){
                emailGrup=g.email;
                break;
            }
        }
        return emailGrup;
    }
    
    // Determina si un grup de correu pertany a una classe (eso1a, batx1c, ifc33a...) a partir del començament de l'email
    public static boolean esEmailGrupClasse(String email){
        return email.startsWith("eso")||
                email.startsWith("batx") || email.startsWith("bi") ||
                email.startsWith("adg") || email.startsWith("com") || email.startsWith("ifc") ||
                email.startsWith("ele") || email.startsWith("tmv");
    }
    
    
    public static void mostraNumsXML(){
        System.out.println("**********+++++++++++*********************");
        System.out.println("***** Estadístiques Fitxer XML XESTIB ****");
        System.out.println("\tNum. Professors: "+profesXML.size());
        System.out.println("\tNum. Alumnes: "+alumnesXML.size());

    }
    
    public static void llegirEmailsAlumnesFITXER(){
        emailsAlumnes = new ArrayList<String>();
        
        // Cal executar a la consola la comanda GAM seguent:
        // gam print users query orgUnitPath=/alumnes
        // Copiar els emails al fitxer emailsAlumnes.txt a l'escriptori
        
        try {
                File file = new File("C:\\Users\\ToniMitjanit\\Desktop\\emails.txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		
                while ((line = bufferedReader.readLine()) != null) {
			emailsAlumnes.add(line.trim());
		}
		fileReader.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
        
        System.out.println("NUM. ALUMNES AL DOMINI (emailsAlumnes): "+emailsAlumnes.size());
    }
    
    /** Llegeix els usernames de professors del fitxer txt d'email
     * 
     * @param emailsProfes 
     */
    public static void llegirUsernamesProfes(ArrayList<String> emailsProfes){
        usernamesProfes = new HashMap<String, Integer>();
        for (String email : emailsProfes) {

            //System.out.println("LLegint info de l'usuari: "+linies[i]);
            String[] cols = email.split("@");

            String nomUser = cols[0];
            System.out.println("NOM USUARI:" +nomUser+" ");
            int num=0;
            if (Character.isDigit(nomUser.charAt(nomUser.length() - 1))) {
                
                char c = nomUser.charAt(nomUser.length() - 1);  // Afegit
                nomUser = nomUser.substring(0, nomUser.length() - 1);
                
                
                num = Character.getNumericValue(c);      // Afegit
                System.out.println("Usuari amb numero: "+nomUser+" lletra: "+c+" que es numero: "+num);
            }
            //System.out.println(nomUser);
            if (usernamesProfes.containsKey(nomUser)) {
                int value = usernamesProfes.get(nomUser);
                //usernamesProfes.replace(nomUser, value, value + 1);
                usernamesProfes.replace(nomUser, value, Math.max(num, value)); // afegit
                System.out.println("Uspate username quantity: "+Math.max(num, value));
            } else {
                //usernamesProfes.put(nomUser, 1);
                usernamesProfes.put(nomUser, num);  // afegit
            }
        }
        
        System.out.println("NUM. USERNAMES PROFES AL DOMINI (emailsProfes): "+usernamesProfes.size());
        System.out.println(usernamesProfes);
        
    }
    
    public static void llegirEmailsProfesFITXER(){
        emailsProfes = new ArrayList<String>();
        
        // Cal executar a la consola la comanda GAM seguent:
        // gam print users query orgUnitPath=/professors
        // Copiar els emails al fitxer emailsProfes.txt a l'escriptori
        
        try {
                File file = new File("C:\\Users\\ToniMitjanit\\Desktop\\profes.txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		
                while ((line = bufferedReader.readLine()) != null) {
			emailsProfes.add(line.trim());
		}
		fileReader.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
        
        System.out.println("NUM. PROFES AL DOMINI (emailsProfes): "+emailsProfes.size());
    }
    
    public static void llegirUsuarisCreatsFitxer(String data){
        ArrayList<String> emailsDataAlumnes = new ArrayList<String>();
        
        // Cal executar a la consola la comanda GAM seguent:
        // gam print users query orgUnitPath=/alumnes
        // Copiar els emails al fitxer emailsAlumnes.txt a l'escriptori
        
        try {
                File file = new File("C:\\Users\\ToniMitjanit\\Desktop\\octubre.txt");
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		StringBuffer stringBuffer = new StringBuffer();
		String line;
		
                while ((line = bufferedReader.readLine()) != null) {
                    if(line.indexOf(data)!=-1){
                        String[] e = line.split(",");
			System.out.println(line);
                    }
		}
		fileReader.close();

	} catch (IOException e) {
		e.printStackTrace();
	}
        
        System.out.println("NUM ALUMNES AL DOMINI: "+emailsAlumnes.size());
    }
    
    public static void removeTAGname(GAM gam, ArrayList<String> emailAlumnes){
        System.out.print(">>>>> Indica email alumne (nom@alumnes.iesmanacor.cat) o tots (tots): ");
        Scanner teclat = new Scanner(System.in);
        String emailAlumne = teclat.next();
        if(emailAlumne.toLowerCase().equals("tots")){
            gam.removeAllTAGnames(emailAlumnes);
        }
        else{
            gam.removeTAGname(emailAlumne);
        }
    }
    
    public static void removeUsersFromGroup(GAM gam){
        System.out.print(">>>>> Indica email del grup (eso1a, ifc33b, ...) o tots (tots): ");
        Scanner teclat = new Scanner(System.in);
        String emailGrup = teclat.next();
        if(emailGrup.toLowerCase().equals("tots")){
            gam.removeAllStudentsFromGroups(TAGS);
        } else {
            gam.removeAllUsersFromGroup(emailGrup+"@iesmanacor.cat");
        }
    }
    
    /**
     * Per cada usuari alumne del GAM (agafat del fitxer d'emails) mira si està en el fitxer XML del XESTIB,
     * Si NO hi és, suspén a l'usuari GAM.
     * @param gam
     * @param emailsAlumnes
     * @param alumnesXML 
     */
    public static void suspenAlumnes(GAM gam, ArrayList<String> emailsAlumnes, ArrayList<Alumne> alumnesXML){
        
        for(String email : emailsAlumnes){
            // GUser user = gam.getUserInfo(email);  // No fa falta si empram expedient (5 xifres)
            int nAt = email.indexOf("@");
            if(nAt>5){
                String numExpedientGAM = email.substring(nAt-5, nAt);
                //System.out.println("Expedient extret de "+email+" es "+numExpedientGAM);
                boolean suspen = true;
                for(Alumne a : alumnesXML){
                    String numExpedientXML = a.expedient;
                    if(numExpedientXML.equals(numExpedientGAM)){
                        suspen = false;
                        break;
                    }
                }

                if(suspen){
                    //System.out.println("Suspen l'alumne "+email);
                    //gam.suspenUsuari(email);
                }
            }
            else {
                System.out.println("ERROR!! amb email "+email);
            }
        }
    }
    
    
    /**
     * Per cada usuari professor del GAM mira si està en el fitxer XML del XESTIB,
     * Si NO hi és, suspén a l'usuari del GAM.
     * @param gam   objecte GAM
     * @param usersProfes   Col·lecció de dades d'usuaris professors del GAM
     * @param profesXML     Col·lecció de dades d'usuaris professors del fitxer XML del XESTIB
     */
    public static void suspenProfes(GAM gam, ArrayList<GUser> usersProfes, ArrayList<Professor> profesXML){
        
        for(GUser user : usersProfes){
            String codiXESTIB_GAM = user.codiXestib;
            boolean suspen = true;
            for(Professor p : profesXML){
                    String codiXESTIB_XML = p.codi;
                    if(codiXESTIB_XML.equals(codiXESTIB_GAM)){
                        suspen = false;
                        break;
                    }
                }

                if(suspen){
                    System.out.println("Suspen el professor/a "+user.email);
                    gam.suspenUsuari(user.email);
                }
            }
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