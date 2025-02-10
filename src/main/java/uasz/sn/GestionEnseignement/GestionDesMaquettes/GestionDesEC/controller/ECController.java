package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.service.ECService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class ECController {
    @Autowired
    private ECService ecService;
    @Autowired
    private UserService userService;
    @GetMapping("/EC")
    public String voirListe(Model model, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        List<EC> ecList= ecService.findAll();
        model.addAttribute("ecList",ecList);
        return "template_EC";
    }

    @PostMapping("/EC/ajouterEC")
    public String ajouterEC(EC ec){
        EC ec1 = ecService.create(ec);
        return "redirect:/EC";
    }
    @PostMapping("/EC/modifierEC")
    public String modifierEc( Long id,String code,String intitule,int CM,int TD,int TP,int coefficient){
        EC ec = ecService.findById(id);
        ec.setCode(code);ec.setIntitule(intitule);ec.setCM(CM);ec.setTP(TP);ec.setTD(TD);ec.setCoefficient(coefficient);
        EC ec1 = ecService.update(ec);
        return "redirect:/EC";
    }

    @PostMapping("/EC/supprimerEC")
    public String supprimerEC(Long id){
        EC ec = ecService.findById(id);
        if(ec.getUe() != null){
            ec.setUe(null);
            ecService.update(ec);
        }
        ecService.delete(ec);
        return "redirect:/EC";
    }

    @PostMapping("/EC/archiver")
    public String archiver(Long id){
        EC ec = ecService.findById(id);
        if(ec != null){
            ecService.archiver(id);
        }
        return "redirect:/EC";
    }

    @PostMapping("/EC/activer")
    public String activer(Long id){
       EC ec = ecService.findById(id);
        if(ec != null){
            ecService.activer(id);
        }

        return "redirect:/EC";
    }

}
