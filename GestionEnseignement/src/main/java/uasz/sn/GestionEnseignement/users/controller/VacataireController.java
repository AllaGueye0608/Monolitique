package uasz.sn.GestionEnseignement.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Choix;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.repository.SeanceRepository;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.ChoixService;
import uasz.sn.GestionEnseignement.authentification.model.Role;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;
import uasz.sn.GestionEnseignement.users.model.Enseignant;
import uasz.sn.GestionEnseignement.users.model.Vacataire;
import uasz.sn.GestionEnseignement.users.service.EnseignantService;
import uasz.sn.GestionEnseignement.users.service.VacataireService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class VacataireController {
    @Autowired
    private UserService userService;
    @Autowired
    private EnseignantService enseignantService;
    @Autowired
    private VacataireService vacataireService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private ChoixService choixService;

    @RequestMapping("/Vacataire/Acceuil")
    public String acceuilVacataire(Model model, Principal principal) {
        User vacataire = userService.findByUsername(principal.getName());
        if (vacataire != null) {
            model.addAttribute("nom", vacataire.getNom());
            // Vérifiez si 'prenom' est nul avant d'accéder au premier caractère
            if (vacataire.getPrenom() != null && !vacataire.getPrenom().isEmpty()) {
                model.addAttribute("prenom", vacataire.getPrenom().charAt(0));
            } else {
                model.addAttribute("prenom", "");  // Valeur par défaut si 'prenom' est nul
            }
        }
        return "template_Vacataire";
    }

    @GetMapping("/Vacataire/Emplois")
    public String emplois(Model model,Principal principal){
        Enseignant vacataire = vacataireService.findByUsername(principal.getName());
        model.addAttribute("nom",vacataire.getNom());
        model.addAttribute("prenom",vacataire.getPrenom().charAt(0));
        List<Seance> seances = seanceRepository.findByEnseignant(vacataire);
        model.addAttribute("seances",seances);
        List<String> jours = new ArrayList<>();
        jours.add("Lundi");jours.add("Mardi");jours.add("Mercredi");jours.add("Jeudi");jours.add("Vendredi");jours.add("Samedi");
        model.addAttribute("jours",jours);
        model.addAttribute("seances",seances);
        return "afficherEmploisVacataire";
    }

    @PostMapping("/ChefDepartement/ajouterVacataire")
    public String ajouterVacataire(Vacataire vacataire){
        String password = passwordEncoder.encode("Passer123");
        vacataire.setPassword(password);vacataire.setDateCreation(new Date());
        vacataire.setActive(true);
        Role role = userService.createRole(new Role("VACATAIRE"));
        if(role == null){
            role = userService.findRoleByName("VACATAIRE");
        }
        List<Role> roles = new ArrayList<>();
        roles.add(role);vacataire.setRoles(roles);
        enseignantService.create(vacataire);
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/ChefDepartement/modifierVacataire")
    public String modifierVacataire(Vacataire vacataire){
        Vacataire vacataireModif = vacataireService.findById(vacataire.getId());
        vacataireModif.setNom(vacataire.getNom());vacataireModif.setPrenom(vacataire.getPrenom());
        vacataireModif.setSpecialite(vacataire.getSpecialite());vacataireModif.setNiveau(vacataire.getNiveau());
        enseignantService.update(vacataireModif);
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/ChefDepartement/activerVacataire")
    public String activerVacataire(Long id){
        enseignantService.activer(id);
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/ChefDepartement/archiverVacataire")
    public String archierVacataire(Long id){
        enseignantService.archiver(id);
        return "redirect:/ChefDepartement/Enseignant";
    }

    @GetMapping("/Vacataire/Cours")
    public String voirCours(Model model,Principal principal){
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("prenom",user.getPrenom());
        model.addAttribute("nom",user.getNom());

        List<Choix> choixList = choixService.findAll();
        for(Choix c : choixList){
            if(c.getEnseignant() != enseignantService.findById(user.getId())){
                choixList.remove(c);
            }
        }
        model.addAttribute("choixList",choixList);
        return "template_coursVacataire";
    }
}
