package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.service.ClasseService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.model.Formation;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionFormation.service.FormationService;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class FormationController {
    @Autowired
    private FormationService formationService;

    @Autowired
    private ClasseService classeService;
    @Autowired
    private UserService userService;
    @GetMapping("/Formation")
    public String voirFormations(Model model, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));
        List<Formation> formations = formationService.findAll();
        model.addAttribute("formations",formations);
        return "template_Formation";
    }

    @PostMapping("/Formation/ajouterFormation")
    public String ajouterFormation(String intitule , boolean archive){
        Formation formation = new Formation(null,intitule,null,archive);
        formationService.create(formation);
        return "redirect:/Formation";
    }

    @PostMapping("/Formation/modifierFormation")
    public String modifierFormation(Long id,String intitule){
        Formation formation = formationService.findById(id);
        if (formation != null){
            formation.setIntitule(intitule);
            formationService.update(formation);
        }
        return "redirect:/Formation";
    }

    @PostMapping("/Formation/supprimerFormation")
    public String supprimerFormation(Long id){
        Formation formation = formationService.findById(id);
        if(formation != null){
            formationService.delete(formation);
        }
        return "redirect:/Formation";
    }

    @GetMapping("/Formation/voirDetail")
    public String voirDeatil(Model model,Long idFormation,Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        Formation formation = formationService.findById(idFormation);
        if(formation != null){
            model.addAttribute("classes",formation.getClasses());
            model.addAttribute("formation",formation);
        }
        return "template_Classe";
    }
    @PostMapping("/Formation/ajouterClasse")
    public String ajouterClasse(Long idFormation, String nom,int niveau){
        Classe classe = new Classe();classe.setNiveau(niveau);
        Formation formation = formationService.findById(idFormation);
        classe.setFormation(formation);
        classe = classeService.create(classe);
        return "redirect:/Formation/voirDetail?idFormation="+idFormation;
    }

    @PostMapping("/Formation/modifierClasse")
    public String modifierClasse(Long idFormation,Long id,String nom,int niveau){
        Classe classe = classeService.findById(id);
        if(classe != null){
            classe.setNiveau(niveau);
            classeService.update(classe);
        }
        return "redirect:/Formation/voirDetail?idFormation="+idFormation;
    }

    @PostMapping("/Formation/supprimerClasse")
    public String supprimerClasse(Long idFormation,Long id){
        Classe classe = classeService.findById(id);
        if(classe != null){
            classeService.delete(classe);
        }
        return "redirect:/Formation/voirDetail?idFormation="+idFormation;
    }

    @PostMapping("/Formation/archiver")
    public String archiver(Long id){
        Formation formation = formationService.findById(id);
        if(formation != null){
            formationService.archiver(id);
        }
        return "redirect:/Formation";
    }
}
