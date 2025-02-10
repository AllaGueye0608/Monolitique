package uasz.sn.GestionEnseignement.authentification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Page de connexion (GET)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Accueil après authentification (GET)
    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        System.out.println("Entrée dans la méthode home");

        if (principal == null) {
            System.out.println("Principal est nul, redirection vers login");
            return "redirect:/login"; // Rediriger vers la page de connexion si l'utilisateur n'est pas authentifié
        }

        // Obtenir le nom d'utilisateur à partir de Principal
        String username = principal.getName();
        System.out.println("Nom d'utilisateur principal: " + username);

        User user = userService.findByUsername(username);
        if (user == null) {
            System.out.println("Utilisateur non trouvé.");
            model.addAttribute("error", "Utilisateur non trouvé.");
            return "login"; // Retourner à la page de connexion si l'utilisateur n'existe pas
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            System.out.println("Rôle non attribué à l'utilisateur.");
            model.addAttribute("error", "Rôle non attribué à l'utilisateur.");
            return "login"; // Retourner à la page de connexion si l'utilisateur n'a pas de rôle
        }

        // Rediriger en fonction du rôle de l'utilisateur
        String role = user.getRoles().get(0).getRole(); // Assurez-vous que l'utilisateur a un rôle
        System.out.println("Rôle de l'utilisateur: " + role);

        switch (role) {
            case "PERMANENT":
                return "redirect:/Permanent/Acceuil";
            case "VACATAIRE":
                return "redirect:/Vacataire/Acceuil";
            case "CHEFDEPARTEMENT":
                return "redirect:/ChefDepartement/Acceuil";
            case "ETUDIANT":
                return "redirect:/Etudiant/Acceuil";
            case "RESPONSABLECLASSE":
                return "redirect:/ResponsableClasse/Acceuil";
            default:
                System.out.println("Rôle inconnu ou non attribué.");
                model.addAttribute("error", "Rôle inconnu ou non attribué.");
                return "login"; // Retourner à la page de connexion si le rôle est inconnu
        }
    }

}
