package uasz.sn.GestionEnseignement.GestionDesSalles.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Batiment;
import uasz.sn.GestionEnseignement.GestionDesSalles.model.Salle;
import uasz.sn.GestionEnseignement.GestionDesSalles.service.BatimentService;
import uasz.sn.GestionEnseignement.GestionDesSalles.service.SalleService;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class SalleController {
    @Autowired
    private SalleService salleService;
    @Autowired
    private BatimentService batimentService;
    @Autowired
    private UserService userService;

    @GetMapping("/Salle")
    public String afficherSalle(Model model, Long idBatiment, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        if(idBatiment != null){
            return "redirect:/Batiment/detail?idBatiment="+idBatiment;
        }
        List<Batiment> batiments = batimentService.getAll();
        List<Salle> salles = salleService.findAll();
        model.addAttribute("salles",salles);
        model.addAttribute("batiments",batiments);
        return "template_salle";
    }

    @PostMapping("/Salle/ajouterSalle")
    public String ajouterSalle(Long batiment,Long idBatiment,int numero){
        if(batiment != null){
            Batiment batiment1 = batimentService.findById(batiment);
            Salle salle = new Salle();salle.setNumero(numero);salle.setBatiment(batiment1);
            salleService.create(salle);
            return "redirect:/Salle?idBatiment="+batiment;
        }
        Batiment batiment1 = batimentService.findById(idBatiment);
        if(batiment1 != null){
            Salle salle = new Salle();salle.setNumero(numero);
            salle.setBatiment(batiment1);
            salleService.create(salle);
        }
        return "redirect:/Salle";
    }

    @PostMapping("/Salle/modifierSalle")
    public String modifierSalle(Long batiment,int numero,Long id){

        Salle salle = salleService.findById(id);
        if(salle != null && salle.getNumero() != numero){
            salle.setNumero(numero);
            salleService.update(salle);
        }
        if(batiment != null){
            return "redirect:/Salle?idBatiment="+batiment;
        }
        return "redirect:/Salle";
    }

    @PostMapping("/Salle/supprimerSalle")
    public String supprimerSalle(Long id,Long batiment){
        Salle salle = salleService.findById(id);
        if(salle != null){
            salleService.delete(salle);
        }
        if(batiment != null){
            return "redirect:/Salle?idBatiment="+batiment;
        }
        return "redirect:/Salle";
    }
}
