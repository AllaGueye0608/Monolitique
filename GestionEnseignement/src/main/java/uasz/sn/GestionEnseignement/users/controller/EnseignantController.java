package uasz.sn.GestionEnseignement.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Choix;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.ChoixService;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.EnseignementService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;
import uasz.sn.GestionEnseignement.users.model.Enseignant;
import uasz.sn.GestionEnseignement.users.model.Permanent;
import uasz.sn.GestionEnseignement.users.model.Vacataire;
import uasz.sn.GestionEnseignement.users.service.EnseignantService;
import uasz.sn.GestionEnseignement.users.service.PermanentService;
import uasz.sn.GestionEnseignement.users.service.VacataireService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class EnseignantController {
    @Autowired
    private UserService userService;
    @Autowired
    private ChoixService choixService;
    @Autowired
    private VacataireService vacataireService;
    @Autowired
    private PermanentService permanentService;
    @Autowired
    private EnseignantService enseignantService;

    @Autowired
    private EnseignementService enseignementService;


    @GetMapping("/ChefDepartement/Enseignant")
    public String chefDepartement_Enseignant(Model model, Principal principal) {
        List<Permanent> permanents = permanentService.findAll();
        model.addAttribute("permanents", permanents);

        List<Vacataire> vacataires = vacataireService.findAll();
        model.addAttribute("vacataires", vacataires);

        List<Enseignement> enseignements = enseignementService.findAll();
        model.addAttribute("enseignements", enseignements);

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("nom", user.getNom());
        model.addAttribute("prenom", user.getPrenom().charAt(0));

        return "chefDepartement_Enseignant";
    }


    @PostMapping("/Enseignant/archiver")
    public String archiverUnEnseignant(Long id){
        Enseignant enseignant = enseignantService.findById(id);
        if(enseignant != null){
            enseignantService.archiver(id);
        }
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/Enseignant/desarchiver")
    public String desarchiverEnseignant(Long id){
        Enseignant enseignant = enseignantService.findById(id);
        if(enseignant != null){
            enseignantService.archiver(id);
        }
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/Choix/ajouterPermanentChoix")
    public String ajouter(
            @RequestParam Long id, // ID de l'enseignant
            @RequestParam Long idEnseignement, // ID de l'enseignement
            @RequestParam String[] types // Types sélectionnés (CM, TD, TP)
    ) {
        // Récupérer l'enseignant et l'enseignement
        Enseignant enseignant = enseignantService.findById(id);
        Enseignement enseignement = enseignementService.findById(idEnseignement);

        // Validation des entrées
        if (enseignant == null || enseignement == null || types == null || types.length == 0) {
            return "redirect:/ChefDepartement/Enseignant?error=donnees_invalides";
        }

        for(String type : types){
            if(choixService.findByEnseignantAndEnseignementAndType(enseignant,enseignement,type) == null){
                if(choixService.findByEnseignementAndType(enseignement,type) != null){
                    if(type.equals("CM")){
                        return "redirect:/ChefDepartement/Enseignant?erreur=un enseignant est déja affecté pour faire ce CM";
                    }
                }
                Choix choix = new Choix();
                choix.setEnseignement(enseignement);choix.setEnseignant(enseignant);choix.setType(type);
                choix.setDateChoix(LocalDate.now());
                choixService.create(choix);
            }
        }

        return "redirect:/ChefDepartement/Enseignant?success=choix_ajoutes";
    }
}
