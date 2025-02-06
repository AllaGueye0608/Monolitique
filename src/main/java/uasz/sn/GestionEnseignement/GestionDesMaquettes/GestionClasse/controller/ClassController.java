package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.EnseignementService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.service.ClasseService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.service.UEService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.model.Formation;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.service.FormationService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service.MaquetteService;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ClassController {
    @Autowired
    private EnseignementService enseignementService;
    @Autowired
    private ClasseService classeService;
    @Autowired
    private MaquetteService maquetteService;
    @Autowired
    private UserService userService;
    @Autowired
    private FormationService formationService;
    @Autowired
    private UEService ueService;

    @GetMapping("/Classe")
    public String voirClasse(Model model, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));
        model.addAttribute("formations",formationService.findAll());
        model.addAttribute("classes",classeService.findAll());
        return "template_Classe";
    }

    @PostMapping("/Classe/ajouterClasse")
    public  String ajouterClasse(Classe classe,Long idFormation){
        Formation formation = formationService.findById(idFormation);
        classe.setFormation(formation);
        Classe classe1 = classeService.create(classe);
        return "redirect:/Classe";
    }

    @PostMapping("/Classe/modifierClasse")
    public  String modifierClasse(Long id,Classe classe){
        Classe classe1 = classeService.findById(id);
        if(classe1 != null){
            classe1.setNiveau(classe.getNiveau());
            classeService.update(classe1);
        }
        return "redirect:/Classe";
    }

    @PostMapping("/Classe/supprimerClasse")
    public String supprimerClasse(Long id){
        Classe classe = classeService.findById(id);
        if(classe != null){
            classeService.delete(classe);
        }
        return "redirect:/Classe";
    }


    @PostMapping("/Classe/ajouterMaquette")
    public String ajouterMaquette(Long classe, Long[] idUEs, int semestre) {
        Maquette maquette = new Maquette();
        Classe classe1 = classeService.findById(classe);
        maquette.setActive(true);
        maquette.setArchive(false);
        maquette.setSemestre(semestre);
        maquette.setClasse(classe1);
        List<UE> ues = new ArrayList<>();
        if (idUEs != null && idUEs.length > 0) {
            for (Long id : idUEs) {
                ues.add(ueService.findById(id));
            }
        }
        maquette.setUeList(ues);
        Maquette maquette1 = maquetteService.create(maquette);

        // Ajout des enseignements pour chaque UE et EC
        List<String> types = new ArrayList<>();
        types.add("CM");
        types.add("TD");
        types.add("TP");

        if (maquette1 != null && !ues.isEmpty()) {
            for (UE ue : ues) {
                for (EC ec : ue.getEcList()) {
                    for (String type : types) {
                        Enseignement enseignement = new Enseignement();
                        enseignement.setMaquette(maquette1);
                        enseignement.setEc(ec);
                        enseignement.setType(type);
                        enseignementService.save(enseignement);
                    }
                }
            }
        }

        return "redirect:/Maquette";
    }

    @PostMapping("/Classe/modifierMaquette")
    public String modifierMaquette(Long id,String nom , int semestre, Long[] idUEs){
        Maquette maquette = maquetteService.findById(id);
        if(maquette != null){
            maquette.setNom(nom);maquette.setSemestre(semestre);
            List<UE> ues = new ArrayList<>();
            for(Long idUe : idUEs){
                UE ue = ueService.findById(idUe);
                if(ue != null){
                    ues.add(ue);
                }
            }
            // Ajouter les UEs qui ne sont pas encore associés à Maquette
            for (UE ue : ues) {
                if (!maquette.getUeList().contains(ue)) {
                    maquette.getUeList().add(ue);
                }
            }

            // Dissocier les UEs qui ne sont plus dans la liste
            Iterator<UE> iterator = maquette.getUeList().iterator();
            while (iterator.hasNext()) {
                UE ue = iterator.next();
                if (!ues.contains(ue)) {
                    iterator.remove(); // Retirer de la liste de l'UE
                }
            }
            maquetteService.update(maquette);
        }
        return "redirect:/Maquette";
    }
    @PostMapping("/Classe/supprimerMaquette")
    public String supprimerMaquette(Long idClasse,Long id){
        Maquette maquette = maquetteService.findById(id);
        if(maquette != null){
            maquetteService.delete(maquette);
        }
        return "redirect:/Classe/voirDetail?id="+idClasse;
    }
    @PostMapping("/Classe/archiver")
    public String archiver(Long id){
        Classe classe = classeService.findById(id);
        if(classe != null){
            classeService.archiver(id);
        }
        return "redirect:/Classe";
    }
}