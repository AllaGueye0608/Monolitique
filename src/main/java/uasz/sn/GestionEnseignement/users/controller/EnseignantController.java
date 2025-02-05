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

        List<Enseignement> enseignements = new ArrayList<>();
        Set<String> uniqueCombination = new HashSet<>(); // Pour stocker les combinaisons uniques

        for (Enseignement enseignement : enseignementService.findAll()) {
            if (enseignement.getChoix() == null && enseignement.getEc() != null) {
                String key = enseignement.getEc().getId() + "-" + enseignement.getMaquette().getId();
                if (uniqueCombination.add(key)) { // Ajoute uniquement si la combinaison est nouvelle
                    enseignements.add(enseignement);
                }
            }
        }

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

        // Récupérer la maquette de l'enseignement
        Maquette maquette = enseignement.getMaquette();
        if (maquette == null) {
            return "redirect:/ChefDepartement/Enseignant?error=maquette_invalide";
        }

        // Récupérer tous les enseignements de la maquette
        List<Enseignement> enseignements = enseignementService.findByMaquette(maquette);

        // Date du choix
        LocalDate localDate = LocalDate.now();

        // Parcourir les types sélectionnés
        for (String type : types) {
            // Parcourir les enseignements de la maquette
            for (Enseignement enseignement1 : enseignements) {
                // Vérifier si l'enseignement correspond au type et à l'EC
                if (enseignement1.getEc().equals(enseignement.getEc())
                        && enseignement1.getType().equals(type)
                        && enseignement1.getChoix() == null) {

                    // Vérifier si un choix existe déjà pour cet enseignant et cet enseignement
                    Choix existingChoix = choixService.findByEnseignantAndEnseignement(enseignant, enseignement1);
                    if (existingChoix != null) {
                        continue; // Passer au suivant si un choix existe déjà
                    }

                    // Créer un nouveau choix
                    Choix choix = new Choix();
                    choix.setEnseignant(enseignant);
                    choix.setEnseignement(enseignement1);
                    choix.setDateChoix(localDate);

                    // Sauvegarder le choix
                    choixService.create(choix);

                    // Associer le choix à l'enseignement
                    enseignement1.setChoix(choix);
                    enseignementService.save(enseignement1);
                }
            }
        }

        return "redirect:/ChefDepartement/Enseignant?success=choix_ajoutes";
    }
}
