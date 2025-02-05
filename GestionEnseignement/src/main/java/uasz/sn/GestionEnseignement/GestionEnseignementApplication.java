package uasz.sn.GestionEnseignement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.service.ClasseService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.service.UEService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service.MaquetteService;
import uasz.sn.GestionEnseignement.authentification.model.Role;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;
import uasz.sn.GestionEnseignement.users.model.Permanent;
import uasz.sn.GestionEnseignement.users.model.Vacataire;
import uasz.sn.GestionEnseignement.users.service.EnseignantService;
import uasz.sn.GestionEnseignement.users.service.PermanentService;
import uasz.sn.GestionEnseignement.users.service.VacataireService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class GestionEnseignementApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(GestionEnseignementApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserService userService;
	@Autowired
	private EnseignantService enseignantService;
	@Autowired
	private PermanentService permanentService;
	@Autowired
	private VacataireService vacataireService;
	@Autowired
	private MaquetteService maquetteService;
	@Autowired
	private UEService ueService;
	@Autowired
	private ClasseService classeService;

	@Override
	public void run(String[] args) {

		// Créer ou récupérer les rôles
		Role permanent = userService.findRoleByName("PERMANENT");
		if (permanent == null) {
			permanent = userService.createRole(new Role("PERMANENT"));
		}

		Role vacataire = userService.findRoleByName("VACATAIRE");
		if (vacataire == null) {
			vacataire = userService.createRole(new Role("VACATAIRE"));
		}

		Role chefDepartement = userService.findRoleByName("CHEFDEPARTEMENT");
		if (chefDepartement == null) {
			chefDepartement = userService.createRole(new Role("CHEFDEPARTEMENT"));
		}

		// Définir le mot de passe
		String password = passwordEncoder.encode("Passer123");

		// Créer et gérer les utilisateurs
		Permanent user1 = new Permanent();
		user1.setNom("DIOP");
		user1.setPrenom("Ibrahima");
		user1.setDateCreation(new Date());
		user1.setPassword(password);
		user1.setUsername("idiop@uasz.sn");
		user1.setActive(true);
		user1.setSpecialite("Web Semantique");
		user1.setMatricule("ID2024");
		user1.setGrade("Professeur");

		User user1Exist = enseignantService.create(user1);
		if (user1Exist == null) {
			user1 = permanentService.findByUsername("idiop@uasz.sn");
		}
		if (user1.getRoles() == null) {
			user1.setRoles(new ArrayList<>());
		}
		if (!user1.getRoles().contains(permanent)) {
			userService.addUserRole(user1, permanent);
		}

		// Créer un Vacataire
		Vacataire user2 = new Vacataire();
		user2.setNom("MALACK");
		user2.setPrenom("Camir");
		user2.setDateCreation(new Date());
		user2.setPassword(password);
		user2.setUsername("cmalack@uasz.sn");
		user2.setActive(true);
		user2.setSpecialite("Ingénierie de Connaissance");
		user2.setNiveau("Doctorat");

		User user2Exist = enseignantService.create(user2);
		if (user2Exist == null) {
			user2 = vacataireService.findByUsername("cmalack@uasz.sn");
		}
		if (user2.getRoles() == null) {
			user2.setRoles(new ArrayList<>());
		}
		if (!user2.getRoles().contains(vacataire)) {
			userService.addUserRole(user2, vacataire);
		}

		// Créer un Permanent et lui attribuer le rôle Chef Département
		Permanent user3 = new Permanent();
		user3.setNom("DIAGNE");
		user3.setPrenom("Serigne");
		user3.setDateCreation(new Date());
		user3.setPassword(password);
		user3.setUsername("sdiagne@uasz.sn");
		user3.setActive(true);
		user3.setSpecialite("Base de données");
		user3.setMatricule("SD2024");
		user3.setGrade("Professeur");

		User user3Exist = enseignantService.create(user3);
		if (user3Exist == null) {
			user3 = permanentService.findByUsername("sdiagne@uasz.sn");
		}
		if (user3.getRoles() == null) {
			user3.setRoles(new ArrayList<>());
		}
		if (!user3.getRoles().contains(chefDepartement)) {
			userService.addUserRole(user3, chefDepartement);
		}
		/*
		Maquette maquette = maquetteService.create(new Maquette(null,"L2I-1er Année",1,null,null));
		if(maquette == null){
			maquette = maquetteService.findByNom("L2I-1er Année",1);
		}
		UE ue = ueService.findByIntitule("Génie Logiciel 1");
		UE ue1 = ueService.findByIntitule("Réseaux et Télécoms");
		maquetteService.addUeToMaquette(maquette,ue);
		maquetteService.addUeToMaquette(maquette,ue1);

		System.out.println("le nombre de UE:" + maquette.getUeList().size());
		int n=0;
		for(int i=0; i < maquette.getUeList().size();i++){
			UE ue2 = maquette.getUeList().get(i);
			n+= ue2.getEcList().size();
		}
		System.out.println("le nombre de EC :"+n);

/*		Classe classe = classeService.create(new Classe(null,"L2I",1,null,null));
		if(classe == null){
			classe = classeService.findClasse("L2I",1);
		}
		classeService.addMaquetteToClasse(classe,maquette);
	*/}
}
