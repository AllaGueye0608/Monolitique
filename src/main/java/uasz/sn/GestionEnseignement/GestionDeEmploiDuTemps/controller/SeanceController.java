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
        Enseignement enseignement = enseignementService.findByMaquetteAndEcAndType(maquette, ec, type);
        Enseignant enseignant = enseignantService.findById(idEnseignant);
        Salle salle1 = salleService.findById(salle);

        // Validation des entrées
        if (ec == null || maquette == null || enseignement == null || enseignant == null || salle1 == null) {
            return "redirect:/Choix?error=donnees_invalides";
        }

        // Validation des heures
        if (heureDebut.isAfter(heureFin) || heureDebut.equals(heureFin)) {
            return "redirect:/Choix?error=horaire_invalide";
        }

        // Vérifier les conflits de salle
        if (seanceService.verifierSeance(jour, heureDebut, heureFin, salle1)) {
            return "redirect:/Choix?error=conflit_salle";
        }

        // Vérifier les conflits d'enseignant
        if (seanceService.findByEnseignementAndEnseignant(enseignement, enseignant) != null) {
            return "redirect:/Choix?error=conflit_enseignant";
        }

        // Vérifier les conflits d'enseignement
        if (seanceService.verifierSeanceEnseignement(jour, heureDebut, heureFin, enseignement)) {
            return "redirect:/Choix?error=conflit_enseignement";
        }

        // Vérifier si l'enseignement est déjà choisi par un autre enseignant
        Choix existingChoix = choixService.findByEnseignement(enseignement);
        if (existingChoix != null && !existingChoix.getEnseignant().equals(enseignant)) {
            return "redirect:/Choix?error=enseignement_deja_choisi";
        }

        // Créer ou récupérer le choix
        Choix choix = choixService.findByEnseignantAndEnseignement(enseignant, enseignement);
        if (choix == null) {
            choix = new Choix();
            choix.setDateChoix(LocalDate.now());
            choix.setEnseignant(enseignant);
            choix.setEnseignement(enseignement);
            choixService.create(choix);
            enseignement.setChoix(choix);
            enseignementService.save(enseignement);
        }

        // Créer la séance
        Seance seance = new Seance();
        seance.setJour(jour);
        seance.setSalle(salle1);
        seance.setType(type);
        seance.setHeureDebut(heureDebut);
        seance.setHeureFin(heureFin);
        seance.setEnseignant(enseignant);
        seance.setEnseignement(enseignement);
        seanceService.create(seance);

        return "redirect:/Choix?success=seance_ajoutee";
    }




    @PostMapping("/Choix/modifierSeance")
    public String modifierSeance(Long idSeance ,String jour, LocalTime heureDebut,LocalTime heureFin,Long salle){
        Seance seance = seanceService.findById(idSeance);
        Salle salle1 = salleService.findById(salle);
        if(!seance.getJour().equals(jour)){
            if( (heureDebut.getHour()*60 + heureDebut.getMinute()) >= (heureFin.getHour()*60 + heureFin.getMinute()) || seanceService.verifierSeance(jour,heureDebut,heureFin,salle1) == true){
                return "redirect:/Choix";
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
        enseignement.setSeance(null);
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

        // Récupérer la maquette de l'enseignement
        Maquette maquette = enseignement.getMaquette();
        if (maquette == null) {
            return "redirect:/Choix?error=maquette_invalide";
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
                    Choix choix1 = choixService.create(choix);
                    if(choix1 != null){
                        enseignement1.setChoix(choix);
                        enseignementService.save(enseignement1);
                        enseignant.getChoixList().add(choix1);
                        enseignantService.update(enseignant);
                    }
                    // Associer le choix à l'enseignement
                }
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
        Choix choix = choixService.findById(idChoix);
        Enseignant enseignant = enseignantService.findById(idEnseignant);
        Enseignement enseignement = enseignementService.findById(idEnseignement);

        if (choix == null || enseignant == null || enseignement == null || type == null) {
            return "redirect:/Choix?error=donnees_invalides";
        }

        for (Enseignement enseignement1 : enseignementService.findAll()) {
            if (enseignement1.getEc().equals(enseignement.getEc()) &&
                    enseignement1.getMaquette().equals(enseignement.getMaquette()) &&
                    enseignement1.getType().equals(type)) {

                // Vérifier si cet enseignement est déjà choisi
                if (!choixService.estChoisi(enseignement1)) {
                    choix.setEnseignant(enseignant);
                    choix.setEnseignement(enseignement1);
                    choix.setDateChoix(LocalDate.now());

                    choixService.update(choix);
                    enseignementService.save(enseignement1);
                    break;
                } else {
                    return "redirect:/Choix?error=enseignement_deja_choisi";
                }
            }
        }

        return "redirect:/Choix?success=choix_modifie";
    }

    @PostMapping("/Choix/supprimerChoix")
    public String supprimerChoix(Long id){
        Choix choix = choixService.findById(id);
        for(Enseignement enseignement : enseignementService.findAll()){
            if(enseignement.getChoix() == choix){
                enseignement.setChoix(null);
                enseignementService.save(enseignement);
            }
        }
        for(Enseignant enseignant : enseignantService.findAll()){
            if(enseignant.getChoixList().contains(choix)){
                enseignant.getChoixList().remove(choix);
                enseignantService.update(enseignant);
            }
        }
        Seance seance = seanceService.findByEnseignementAndEnseignant(choix.getEnseignement(),choix.getEnseignant());
        if(seance != null){
            Enseignement enseignement = seance.getEnseignement();
            enseignement.setSeance(null);
            Enseignant enseignant = seance.getEnseignant();
            enseignementService.save(enseignement);
            seanceService.delete(seance);
        }
        choixService.delete(choix);
        return "redirect:/Choix";
    }

}
