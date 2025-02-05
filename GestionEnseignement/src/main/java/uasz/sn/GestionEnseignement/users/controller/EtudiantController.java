package uasz.sn.GestionEnseignement.users.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uasz.sn.GestionEnseignement.authentification.model.Role;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.repository.RoleRepository;
import uasz.sn.GestionEnseignement.authentification.service.UserService;
import uasz.sn.GestionEnseignement.users.model.Etudiant;
import uasz.sn.GestionEnseignement.users.service.EtudiantService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class EtudiantController {
    @Autowired
    private EtudiantService etudiantService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/Etudiant/Acceuil")
    public String acceuilEtudiant(Model model, Principal principal){
        Etudiant etudiant = etudiantService.findByUsername(principal.getName());
        model.addAttribute("nom",etudiant.getNom());
        model.addAttribute("prenom",etudiant.getPrenom().charAt(0));
        return "template_Etudiant";
    }

    @RequestMapping("/ResponsableClasse/Acceuil")
    public String acceuilResponsableClasse(Model model,Principal principal){
        Etudiant user = etudiantService.findByUsername(principal.getName());
        model.addAttribute("nom",user.getNom());
        model.addAttribute("prenom",user.getPrenom().charAt(0));
        return "template_ResponsableClasse";
    }

    @GetMapping("/profil")
    public String profil(Model model,Principal principal){
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("utilisateur",user);
        model.addAttribute("nom",user.getNom());
        model.addAttribute("prenom",user.getPrenom().charAt(0));
        return "profil";
    }

    @PostMapping("/profil/modifier")
    public String modifier(Long id,String matricule,String nom,String prenom){
        User user = userService.findUserById(id);
        if(user != null){
            if(matricule != null){
                user.setUsername(matricule);
            }
            if(nom != null){
                user.setNom(nom);
            }
            if(prenom != null){
                user.setPrenom(prenom);
            }
            userService.updateUser(user);
        }
        return "redirect:/profil";
    }

    @GetMapping("/logout")
    public String logout(){
        return "redirect:/login";
    }
    @GetMapping("/inscription")
    public String inscription(Model model,String message){
        if(message != null){
            model.addAttribute("message",message);
        }
        return "inscription";
    }
    @PostMapping("/inscriptionUser")
    public String inscrire(String prenom,String nom,String username,String password,String confirmPassword){
        if(!password.equals(confirmPassword)){
            return "redirect:/inscription?message= veuillez confirmer le mot de passe";
        }
        String passEnc = passwordEncoder.encode(password);
        User etudiant = new Etudiant();
        Role role = roleRepository.findByRole("ETUDIANT");
        if(role == null){
            role = new Role("ETUDIANT");
            roleRepository.save(role);
        }
        List<Role> roles = new ArrayList<>(); roles.add(role);
        etudiant.setPrenom(prenom);etudiant.setNom(nom);etudiant.setRoles(roles);etudiant.setDateCreation(new Date());
        etudiant.setUsername(username);etudiant.setPassword(passEnc);
        User existingEtudiant = userService.createUser(etudiant);
        if(existingEtudiant == null){
            return "redirect:/inscription?message=cet Utilisateur existe déja";
        }
        return "redirect:/login";
    }

}
