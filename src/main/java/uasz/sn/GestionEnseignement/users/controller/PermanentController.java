package uasz.sn.GestionEnseignement.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uasz.sn.GestionEnseignement.authentification.model.Role;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;
import uasz.sn.GestionEnseignement.users.model.Permanent;
import uasz.sn.GestionEnseignement.users.service.EnseignantService;
import uasz.sn.GestionEnseignement.users.service.PermanentService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class PermanentController {
    @Autowired
    private UserService userService;
    @Autowired
    private EnseignantService enseignantService;

    @Autowired
    private PermanentService permanentService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/Permanent/Acceuil")
    public String acceuilPermanent(Model model, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));
        return "template_Permanent";
    }

    @RequestMapping("/ChefDepartement/Acceuil")
    public String acceuilChefDepartement(Model model,Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));
        return "template_ChefDepartement";
    }

    @PostMapping("/ChefDepartement/ajouterPermanent")
    public String ajouterPermanent(Permanent permanent){
        String password = passwordEncoder.encode("Passer123");
        permanent.setPassword(password);permanent.setDateCreation(new Date());permanent.setActive(true);
        Role role = userService.createRole(new Role("PERMANENT"));
        if(role == null){
            role = userService.findRoleByName("PERMANENT");
        }
        List<Role> roles = new ArrayList<>();
        roles.add(role);permanent.setRoles(roles);
        enseignantService.create(permanent);
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/ChefDepartement/modifierPermanent")
    public String modifierPermanent(Permanent permanent){
        Permanent permanentModif = permanentService.findById(permanent.getId());
        permanentModif.setMatricule(permanent.getMatricule());
        permanentModif.setNom(permanent.getNom());
        permanentModif.setPrenom(permanent.getPrenom());
        permanentModif.setSpecialite(permanent.getSpecialite());
        permanentModif.setGrade(permanent.getGrade());
        enseignantService.update(permanentModif);
        return "redirect:/ChefDepartement/Enseignant";
    }

    @PostMapping("/ChefDepartement/activerPermanent")
    public String activerPermanent(Long id){
        enseignantService.activer(id);
        return "redirect:/ChefDepartement/Enseignant";
    }
    @PostMapping("/ChefDepartement/archiverPermanent")
    public String archiverPermanent(Long id){
        enseignantService.archiver(id);
        return  "redirect:/ChefDepartement/Enseignant";
    }
}
