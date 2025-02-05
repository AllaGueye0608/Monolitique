package uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Choix;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Seance;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.ChoixService;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.EnseignementService;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.SeanceService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.service.ClasseService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.service.ECService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service.MaquetteService;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EmploiDuTempsController {
    @Autowired
    private SeanceService seanceService;
    @Autowired
    private ChoixService choixService;
    @Autowired
    private ClasseService classeService;
    @Autowired
    private MaquetteService maquetteService;
    @Autowired
    private EnseignementService enseignementService;
    @Autowired
    private ECService ecService;

    @GetMapping("/Emplois")
    public String afficher(Model model){
        List<Seance> seances = seanceService.findAll();
        List<Maquette> maquettes = new ArrayList<>();
        List<Choix> choixList = choixService.findAll();
        if(choixList != null){
            for(Choix seance : choixList){
                Maquette maquette = seance.getEnseignement().getMaquette();
                if(!maquettes.contains(maquette)){
                    maquettes.add(maquette);
                }
            }
        }
        model.addAttribute("maquettes",maquettes);

        return "template_emploiDuTemps";
    }

    @GetMapping("/Emplois/voirEmplois")
    public String voirEmploi(Model model,Long idMaquette){
        Maquette maquette = maquetteService.findById(idMaquette);
        List<Seance> seances = new ArrayList<>();
        for(Seance seance : seanceService.findAll()){
            if(seance.getEnseignement().getMaquette() == maquette){
                seances.add(seance);
            }
        }
        List<LocalTime> heures = new ArrayList<>();

        // Ajouter les heures de 08:00 à 20:00
        LocalTime heure = LocalTime.of(8, 0);
        while (!heure.isAfter(LocalTime.of(20, 0))) {
            heures.add(heure);
            heure = heure.plusHours(1);
        }

        List<String> jours = new ArrayList<>();
        jours.add("Lundi");jours.add("Mardi");jours.add("Mercredi");jours.add("Jeudi");jours.add("Vendredi");jours.add("Samedi");
        model.addAttribute("maquette",maquette);
        model.addAttribute("jours",jours);
        model.addAttribute("heures",heures);
        model.addAttribute("seances",seances);
        return "afficherEmploiDuTemps";
    }

    @GetMapping("/Emplois/voirRepartition")
    public String voirRepartition(Model model,Long idMaquette){
        Maquette maquette = maquetteService.findById(idMaquette);
        Classe classe = maquette.getClasse();
        List<EC> ecs = new ArrayList<>();
        List<Enseignement> enseignements = new ArrayList<>();
        for(Enseignement enseignement : enseignementService.findAll()){
            if(enseignement.getMaquette() == maquette){
                enseignements.add(enseignement);
                if(!ecs.contains(enseignement.getEc())){
                    ecs.add(enseignement.getEc());
                }
            }
        }
        System.out.println(ecs.size());
        model.addAttribute("choixList",choixService.findAll());
        model.addAttribute("ecs",ecs);
        model.addAttribute("classe",classe);
        model.addAttribute("enseignements",enseignements);
        model.addAttribute("maquette",maquette);
        return "afficherRepartition";
    }
}
