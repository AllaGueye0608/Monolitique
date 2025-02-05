package uasz.sn.GestionEnseignement.GestionDesSalles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Batiment;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;
import uasz.sn.GestionEnseignement.GestionDesSalles.service.BatimentService;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class BatimentController {
    @Autowired
    private BatimentService batimentService;
    @Autowired
    private UserService userService;
    @GetMapping("/Batiment")
    public String afficherBatiment(Model model,Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        List<Batiment> batiments = batimentService.getAll();
        model.addAttribute("batiments",batiments);
        return "template_batiment";
    }

    @PostMapping("/Batiment/ajouterBatiment")
    public String ajouterBatiment(String nom){
        Batiment batiment = new Batiment();
        batiment.setNom(nom);
        Batiment batiment1 = batimentService.create(batiment);
        return "redirect:/Batiment";
    }
    @PostMapping("/Batiment/modifierBatiment")
    public String modifierBatiment(Long id,String nom){
        Batiment batiment = batimentService.findById(id);
        if(batiment != null){
            batiment.setNom(nom);
            Batiment batiment1 = batimentService.update(batiment);
        }
        return "redirect:/Batiment";
    }

    @PostMapping("/Batiment/supprimerBatiment")
    public String supprimerBatiment(@RequestParam("id") Long id) {
        Batiment batiment = batimentService.findById(id);
        if (batiment != null) {
            batimentService.delete(batiment);
        }
        return "redirect:/Batiment";
    }

    @GetMapping("/Batiment/detail")
    public String detail(Model model, Long idBatiment, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        Batiment batiment = batimentService.findById(idBatiment);
        List<Salle> salles = batiment.getSalles();
        model.addAttribute("salles",salles);
        model.addAttribute("batiment",batiment);
        return "template_salle";
    }

}
