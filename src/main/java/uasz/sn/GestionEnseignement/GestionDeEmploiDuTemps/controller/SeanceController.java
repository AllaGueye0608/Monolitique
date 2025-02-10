package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.controller;

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
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.SeanceService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.service.ECService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service.MaquetteService;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;
import uasz.sn.GestionEnseignement.GestionDesSalles.service.SalleService;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;
import uasz.sn.GestionEnseignement.users.model.Enseignant;
import uasz.sn.GestionEnseignement.users.service.EnseignantService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Controller
public class SeanceController {
    @Autowired
    private EnseignementService enseignementService;
    @Autowired
    private EnseignantService enseignantService;
    @Autowired
    private SeanceService seanceService;
    @Autowired
    private ECService ecService;
    @Autowired
    private MaquetteService maquetteService;
    @Autowired
    private SalleService salleService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChoixService choixService;

    @GetMapping("/Choix")
    public String seance(Model model, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));
        List<Seance> seances = seanceService.findAll();
        List<Enseignant> enseignants = enseignantService.findAll();
        List<Enseignement> listeEnseignements = enseignementService.findAll();
        List<Enseignement> enseignements = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();

        for (Enseignement enseignement : listeEnseignements) {
            String key = enseignement.getEc().getId() + "-" + enseignement.getMaquette().getId();

            if (!uniqueKeys.contains(key)) {
                uniqueKeys.add(key);
                enseignements.add(enseignement);
            }
        }

        List<Salle> salles = salleService.findAll();
        List<String> jours = new ArrayList<>();jours.add("Lundi");jours.add("Mardi");jours.add("Mercredi");jours.add("Jeudi");jours.add("Vendredi");jours.add("Samedi");
        List<Maquette> maquettes = maquetteService.findAll();
        model.addAttribute("salles",salles);
        model.addAttribute("maquettes",maquettes);
        model.addAttribute("jours",jours);
        model.addAttribute("enseignants",enseignants);
        model.addAttribute("enseignements",enseignements);
        model.addAttribute("seances",seances);
        model.addAttribute("choixList",choixService.findAll());
        return "template_choixListe";
    }
    @PostMapping("/Choix/ajouterSeance")
    public String ajouterSeance(
            String jour, String type, LocalTime heureDebut, LocalTime heureFin,
            Long salle, Long idEnseignant, Long idEC, Long idMaquette
    ) {
        // Récupérer les entités
        EC ec = ecService.findById(idEC);
        Maquette maquette = maquetteService.findById(idMaquette);
        Enseignement enseignement = enseignementService.findByMaquetteAndEc(maquette, ec);
        Enseignant enseignant = enseignantService.findById(idEnseignant);
        Salle salle1 = salleService.findById(salle);

        // Validation des entités
        if (ec == null || maquette == null || enseignement == null || enseignant == null || salle1 == null) {
            return "redirect:/Choix?error=donnees_invalides";
        }

        // Validation des heures
        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
            return "redirect:/Choix?error=horaire_invalide";
        }

        // Vérifier les conflits de salle (existe-t-il déjà une séance à cet horaire dans cette salle)
        if (seanceService.verifierSeance(jour, heureDebut, heureFin, salle1)) {
            return "redirect:/Choix?error=conflit_salle";
        }

        // Créer ou récupérer le choix pour l'enseignant et l'enseignement
        Choix choix = choixService.findByEnseignantAndEnseignementAndType(enseignant, enseignement, type);

        // Vérification si un enseignant est déjà affecté à ce CM (Cours Magistral)
        if (type.equals("CM")) {
            Choix choix1 = choixService.findByEnseignementAndType(enseignement, "CM");
            if (choix1 != null) {
                return "redirect:/Choix?error=Un_professeur_est_deja_affecte_a_ce_CM";
            }
        }

        // Si le choix n'existe pas, en créer un nouveau
        if (choix == null) {
            choix = new Choix();
            choix.setDateChoix(LocalDate.now());
            choix.setEnseignant(enseignant);
            choix.setEnseignement(enseignement);
            choix.setType(type);
            choixService.create(choix);
            enseignementService.save(enseignement);
        }

        // Vérifier s'il existe déjà une séance pour ces horaires et cet enseignement/type
        List<Seance> existingSeances = seanceService.findByJourAndHeureDebutAndHeureFinAndEnseignementAndType(jour, heureDebut, heureFin, enseignement, type);
        Seance existingSeance = existingSeances.isEmpty() ? null : existingSeances.get(0);

        if (existingSeance != null) {
            // Si une séance du même type et enseignement existe déjà, autoriser l'ajout d'une autre
            // Aucune action supplémentaire ici pour le cas où la séance existe déjà
        } else {
            // Si aucune séance similaire n'existe, vérifier les conflits d'enseignement pour ces horaires
            List<Seance> conflictingSeances = seanceService.findByJourAndHeureDebutAndHeureFin(jour, heureDebut, heureFin);
            if (!conflictingSeances.isEmpty()) {
                Seance conflictingSeance = conflictingSeances.get(0);
                // Si une autre séance existe avec des horaires similaires mais d'un autre enseignement, retour d'erreur
                if (!conflictingSeance.getEnseignement().equals(enseignement) || !conflictingSeance.getType().equals(type)) {
                    return "redirect:/Choix?error=conflit_enseignement";
                }
            }
        }

        // Créer la nouvelle séance
        Seance seance = new Seance();
        seance.setJour(jour);
        seance.setSalle(salle1);
        seance.setType(type);
        seance.setHeureDebut(heureDebut);
        seance.setHeureFin(heureFin);
        seance.setEnseignant(enseignant);
        seance.setEnseignement(enseignement);

        // Sauvegarder la nouvelle séance
        Seance seance2 = seanceService.create(seance);
        if (seance2 != null) {
            return "redirect:/Choix?success=seance_ajoutee";
        }

        return "redirect:/Choix?error=seance_nonAjoutee";
    }






    @PostMapping("/Choix/modifierSeance")
    public String modifierSeance(Long idSeance ,String jour, LocalTime heureDebut,LocalTime heureFin,Long salle){
        Seance seance = seanceService.findById(idSeance);
        Enseignement enseignement = seance.getEnseignement();
        Salle salle1 = salleService.findById(salle);
        if(!seance.getJour().equals(jour)){
            if( (heureDebut.getHour()*60 + heureDebut.getMinute()) >= (heureFin.getHour()*60 + heureFin.getMinute()) || seanceService.verifierSeance(jour,heureDebut,heureFin,salle1) == true){
                return "redirect:/Choix";
            }
        }



        // Vérifier les conflits de salle (existe-t-il déjà une séance à cet horaire dans cette salle)
        if (seanceService.verifierSeance(jour, heureDebut, heureFin, salle1)) {
            return "redirect:/Choix?error=conflit_salle";
        }

        // Vérifier s'il existe déjà une séance pour ces horaires et cet enseignement/type
        List<Seance> existingSeances = seanceService.findByJourAndHeureDebutAndHeureFinAndEnseignementAndType(jour, heureDebut, heureFin, enseignement, seance.getType());
        Seance existingSeance = existingSeances.isEmpty() ? null : existingSeances.get(0);

        if (existingSeance != null) {
            // Si une séance du même type et enseignement existe déjà, autoriser l'ajout d'une autre
            // Aucune action supplémentaire ici pour le cas où la séance existe déjà
        } else {
            // Si aucune séance similaire n'existe, vérifier les conflits d'enseignement pour ces horaires
            List<Seance> conflictingSeances = seanceService.findByJourAndHeureDebutAndHeureFin(jour, heureDebut, heureFin);
            if (!conflictingSeances.isEmpty()) {
                Seance conflictingSeance = conflictingSeances.get(0);
                // Si une autre séance existe avec des horaires similaires mais d'un autre enseignement, retour d'erreur
                if (!conflictingSeance.getEnseignement().equals(enseignement) || !conflictingSeance.getType().equals(seance.getType())) {
                    return "redirect:/Choix?error=conflit_enseignement";
                }
            }
        }


        seance.setJour(jour);
        seance.setSalle(salle1);seance.setHeureDebut(heureDebut);seance.setHeureFin(heureFin);
        seanceService.update(seance);
        return "redirect:/Choix";
    }

    @PostMapping("/Choix/supprimerSeance")
    public String supprimerSeance(@RequestParam("id") Long id) {
        Seance seance = seanceService.findById(id);
        Enseignement enseignement = seance.getEnseignement();
        enseignement.getSeances().remove(seance);
        Enseignant enseignant = seance.getEnseignant();
        enseignementService.save(enseignement);
        seanceService.delete(seance);
        return "redirect:/Choix";
    }

    @PostMapping("/Choix/ajouterChoix")
    public String ajouter(
            Long idEnseignant, // ID de l'enseignant
            Long idEnseignement, // ID de l'enseignement
            String[] types // Types sélectionnés (CM, TD, TP)
    ) {
        if (idEnseignant == null) {
            System.out.println("enseignant null");
            return "redirect:/Choix?error=enseignant_manquant";
        }

        if (idEnseignement == null) {
            System.out.println("enseignement null");
            return "redirect:/Choix?error=enseignement_manquant";
        }

        if (types == null || types.length == 0) {
            System.out.println("types null");

            return "redirect:/Choix?error=types_manquants";
        }

        // Récupérer l'enseignant et l'enseignement
        Enseignant enseignant = enseignantService.findById(idEnseignant);
        Enseignement enseignement = enseignementService.findById(idEnseignement);

        // Validation des entrées
        if (enseignant == null || enseignement == null || types == null || types.length == 0) {
            return "redirect:/ChefDepartement/Enseignant?error=donnees_invalides";
        }

        for(String type : types){
            Choix choix = choixService.findByEnseignantAndEnseignementAndType(enseignant,enseignement,type);
            if(choix.getType().equals("CM")){
                continue;
            }
            if(choix == null){
                choix.setEnseignement(enseignement);
                choix.setEnseignant(enseignant);
                choix.setType(type);
                choix.setDateChoix(LocalDate.now());
                choixService.create(choix);
            }
        }

        return "redirect:/Choix?success=choix_ajoutes";
    }


    @PostMapping("/Choix/modifierChoix")
    public String modifierChoix(
            @RequestParam Long idChoix,
            @RequestParam Long idEnseignant,
            @RequestParam Long idEnseignement,
            @RequestParam String type
    ) {
        // Récupérer les entités depuis la base de données
        Choix choix = choixService.findById(idChoix);
        Enseignant enseignant = enseignantService.findById(idEnseignant);
        Enseignement enseignement = enseignementService.findById(idEnseignement);

        // Valider les entités
        if (choix == null || enseignant == null || enseignement == null) {
            return "redirect:/Choix?error=donnees_invalides"; // Rediriger si une entité est introuvable
        }
        if(enseignant != choix.getEnseignant()){
            choix.setEnseignant(enseignant);
        }
        if(enseignement != choix.getEnseignement()){
            choix.setEnseignement(enseignement);
        }
        if(!choix.getType().equals(type)){
            choix.setType(type);
            choix.setDateChoix(LocalDate.now());
        }

        choixService.update(choix);

        // Rediriger avec un message de succès
        return "redirect:/Choix?success=choix_modifie";
    }
    @PostMapping("/Choix/supprimerChoix")
    public String supprimerChoix(Long id){
        Choix choix = choixService.findById(id);
        choixService.delete(choix);
        Seance seance = seanceService.findByEnseignementAndEnseignant(choix.getEnseignement(),choix.getEnseignant());
        if(seance != null){
            Enseignement enseignement = seance.getEnseignement();
            enseignement.getSeances().remove(seance);
            Enseignant enseignant = seance.getEnseignant();
            enseignementService.save(enseignement);
            seanceService.delete(seance);
        }
        choixService.delete(choix);
        return "redirect:/Choix";
    }

}
