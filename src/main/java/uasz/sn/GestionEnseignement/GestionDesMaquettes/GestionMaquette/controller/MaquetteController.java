package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.controller;


import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.EnseignementService;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.SeanceService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.model.Classe;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionClasse.service.ClasseService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.model.EC;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesEC.service.ECService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.model.UE;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.service.UEService;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.model.Maquette;
import uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionMaquette.service.MaquetteService;
import uasz.sn.GestionEnseignement.authentification.model.User;
import uasz.sn.GestionEnseignement.authentification.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class MaquetteController {
    @Autowired
    private MaquetteService maquetteService;
    @Autowired
    private UEService ueService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClasseService classeService;

    @Autowired
    private SeanceService seanceService;
    @Autowired
    private EnseignementService enseignementService;
    @Autowired
    private ECService ecService;

    @GetMapping("/Maquette")
    public ModelAndView afficherMaquette( Principal principal) {

        List<UE> ues = ueService.findAll();
        List<Classe> classes = classeService.findAll();

        List<Maquette> maquettes = maquetteService.findAll();

        ModelAndView modelAndView = new ModelAndView("template_Maquette"); // Nom de votre vue
        User permanent = userService.findByUsername(principal.getName());
        modelAndView.addObject("nom",permanent.getNom());
        modelAndView.addObject("prenom",permanent.getPrenom().charAt(0));

        modelAndView.addObject("nom",permanent.getNom());
        modelAndView.addObject("prenom",permanent.getPrenom().charAt(0));
        modelAndView.addObject("ues", ues);
        modelAndView.addObject("classes", classes);
        modelAndView.addObject("maquettes", maquettes);
        return modelAndView;
    }



    @PostMapping("/Maquette/ajouterMaquette")
    public String ajouterMaquette(Long classe, Long[] idUEs, int semestre) {
        Maquette maquette = new Maquette();
        Classe classe1 = classeService.findById(classe);
        maquette.setNom(classe1.getFormation().getIntitule() + '-'+classe1.getNiveau());
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
                        enseignement.setUe(ue);
                        enseignement.setEc(ec);
                        enseignement.setType(type);
                        enseignementService.save(enseignement);
                    }
                }
            }
        }

        return "redirect:/Maquette";
    }



    @PostMapping("/Maquette/modifierMaquette")
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
            Maquette maquette1 = maquetteService.update(maquette);
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
                            enseignement.setUe(ue);
                            enseignement.setEc(ec);
                            enseignement.setType(type);
                            enseignementService.save(enseignement);
                        }
                    }
                }
            }

        }
        return "redirect:/Maquette";
    }
    @PostMapping("/Maquette/supprimerMaquette")
    public String supprimerMaquette(@RequestParam Long id, Model model) {
        Maquette maquette = maquetteService.findById(id);

        if (maquette != null) {
            // La suppression de la maquette va automatiquement supprimer les UE associées grâce à la cascade
            maquetteService.delete(maquette);
        } else {
            // Si la maquette n'existe pas, on peut ajouter un message d'erreur
            model.addAttribute("error", "Maquette non trouvée.");
        }

        // Rediriger vers la page des maquettes
        return "redirect:/Maquette";
    }


    @PostMapping("/Maquette/ajouterUE")
    public String ajouterUE(Long idMaquette, String code, String intitule, int credit, int coefficient,Long[] idECs) {
        // Vérifier si la Maquette existe
        Maquette maquette = maquetteService.findById(idMaquette);
        if (maquette == null) {
            return "redirect:/Maquette";  // Ou rediriger vers une page d'erreur si la maquette n'est pas trouvée
        }

        // Créer une nouvelle UE
        UE ue = new UE();
        ue.setCode(code);
        ue.setIntitule(intitule);
        ue.setCredit(credit);
        ue.setCoefficient(coefficient);
        if(idECs != null){
            for(Long id : idECs){

                EC ec = ecService.findById(id);
                if(ec.getUe() == null){
                    ue.getEcList().add(ec);
                }
            }
        }
        // Sauvegarder l'UE
        UE savedUe = ueService.create(ue);

        // Ajouter l'UE à la maquette
        maquette.getUeList().add(savedUe);
        maquetteService.update(maquette);

        // Ajouter les enseignements pour chaque EC de cette UE dans la maquette
        if (savedUe.getEcList() != null && !savedUe.getEcList().isEmpty()) {
            List<String> types = List.of("CM", "TD", "TP");  // Types d'enseignements
            for (EC ec : savedUe.getEcList()) {
                for (String type : types) {
                    // Vérifier si l'enseignement existe déjà pour cette combinaison
                    if (!enseignementService.exists(maquette, savedUe, ec, type)) {
                        Enseignement enseignement = new Enseignement();
                        enseignement.setMaquette(maquette);
                        enseignement.setUe(savedUe);
                        enseignement.setEc(ec);
                        enseignement.setType(type);
                        enseignementService.save(enseignement);  // Sauvegarder l'enseignement
                    }
                }
            }
        }

        // Rediriger vers la page de détail de la maquette
        return "redirect:/Maquette/detail?id=" + idMaquette;
    }


    public String modifierUE(Long idMaquette, Long id, String code, String intitule, int credit, int coefficient, Long[] idECs) {

        UE ue1 = ueService.findById(id);
        if (ue1 == null) {
            return "redirect:/UE?error=UE_non_trouvee";
        }

        if (idECs != null) {
            List<EC> ecs = new ArrayList<>();
            for (Long idEC : idECs) {
                EC ec = ecService.findById(idEC);
                if (ec != null) {
                    ecs.add(ec);
                }
            }
            // Ajouter les ECs qui ne sont pas encore associés à UE
            for (EC ec : ecs) {
                if (!ue1.getEcList().contains(ec)) {
                    ue1.getEcList().add(ec);
                    ec.setUe(ue1);
                    ecService.update(ec);
                }
            }
            // Dissocier les ECs qui ne sont plus dans la liste
            Iterator<EC> iterator = ue1.getEcList().iterator();
            while (iterator.hasNext()) {
                EC ec = iterator.next();
                if (!ecs.contains(ec)) {
                    iterator.remove(); // Retirer de la liste de l'UE
                    ec.setUe(null); // Dissocier sans supprimer
                    ecService.update(ec); // Mettre à jour l'EC pour que l'UE soit null
                }
            }
            ue1.setEcList(ecs);
        }

        // Mise à jour des autres propriétés de l'UE
        ue1.setCode(code);
        ue1.setIntitule(intitule);
        ue1.setCredit(credit);
        ue1.setCoefficient(coefficient);

        UE ue = ueService.update(ue1);

        if (ue != null) {
            if (ue.getEcList() == null || ue.getEcList().isEmpty()) {
                return "redirect:/UE?error=aucun_ec_pour_ue";
            }

            if (ue.getMaquettes() == null || ue.getMaquettes().isEmpty()) {
                return "redirect:/UE?error=aucune_maquette_pour_ue";
            }

            List<String> types = new ArrayList<>();
            types.add("CM");
            types.add("TD");
            types.add("TP");

            for (Maquette maquette : ue.getMaquettes()) {
                for (EC ec : ue.getEcList()) {
                    for (String type : types) {
                        if (!enseignementService.exists(maquette, ue, ec, type)) {
                            Enseignement enseignement = new Enseignement();
                            enseignement.setMaquette(maquette);
                            enseignement.setUe(ue);
                            enseignement.setEc(ec);
                            enseignement.setType(type);
                            enseignementService.save(enseignement);
                        }
                    }
                }
            }
        }

        if (idMaquette != null) {
            return "redirect:/UE?idMaquette=" + idMaquette;
        }
        return "redirect:/UE";
    }

    @PostMapping("/Maquette/supprimerUE")
    public String supprimerUE(Long idMaquette, Long id) {
        // Récupérer la maquette et l'UE
        Maquette maquette = maquetteService.findById(idMaquette);
        UE ue = ueService.findById(id);

        // Vérifier si la maquette et l'UE existent
        if (maquette == null || ue == null) {
            return "redirect:/Maquette";  // Rediriger si l'une des entités n'existe pas
        }

        // Supprimer les enseignements associés à cette UE et cette maquette
        List<Enseignement> enseignements = enseignementService.findByMaquetteAndUe(maquette, ue);
        for (Enseignement enseignement : enseignements) {
            enseignementService.delete(enseignement);  // Supprimer chaque enseignement
        }

        // Retirer l'UE de la liste des maquettes
        maquette.getUeList().remove(ue);
        maquetteService.update(maquette);  // Enregistrer la maquette mise à jour

        // Supprimer l'UE
        ueService.delete(ue.getId());

        // Rediriger vers la page de détail de la maquette
        return "redirect:/Maquette/detail?id=" + idMaquette;
    }


    @GetMapping("/Maquette/detail")
    public String detailMaquette(Model model,Long id,Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        Maquette maquette = maquetteService.findById(id);
        List<UE> listeUE = maquette.getUeList();
        List<EC> listeEC = new ArrayList<>();
        int creditsUE = 0;
        int coeffUE = 0;
        int CMT = 0;
        int TDT =0;
        int TPT = 0;
        int VHTT= 0;
        int TPET = 0;
        int coeffs = 0;
        for(UE ue : listeUE){
            creditsUE += ue.getCredit();
            coeffUE += ue.getCoefficient();
            for( EC ec : ue.getEcList()){
                CMT += ec.getCM();
                TDT += ec.getTD();
                TPT += ec.getTP();
                VHTT += (ec.getCM() + ec.getTD() + ec.getTP());
                TPET += (ec.getCoefficient() * (ec.getCM() + ec.getTD() + ec.getTP()));
                coeffs += ec.getCoefficient();
            }
        }
        model.addAttribute("cm",CMT);
        model.addAttribute("tp",TPT);
        model.addAttribute("td",TDT);
        model.addAttribute("vht",VHTT);
        model.addAttribute("tpe",TPET);
        model.addAttribute("coefficient",coeffs);
        model.addAttribute("credit",creditsUE);
        model.addAttribute("coeffUE",coeffUE);
        model.addAttribute("maquette",maquette);
        model.addAttribute("listeUE",listeUE);
        System.out.print(maquette.getUeList().size());
        return "template_UE";
    }

    @PostMapping("/Maquette/archiver")
    public String archiver(Long id){
        Maquette maquette = maquetteService.findById(id);
        if(maquette != null){
            maquetteService.archiver(id);
        }
        return "redirect:/Maquette";
    }

    @PostMapping("/Maquette/activer")
    public String activer(Long id){
        Maquette maquette = maquetteService.findById(id);
        if(maquette != null){
            maquetteService.activer(id);
        }

        return "redirect:/Maquette";
    }

    @PostMapping("/Maquette/afficher")
    public String afficherMaquette(Long id, Model model,Principal principal) {
        Maquette maquette = maquetteService.findById(id);
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));

        List<UE> listeUE = maquette.getUeList();
        int creditsUE = 0;
        int coeffUE = 0;
        int CMT = 0;
        int TDT =0;
        int TPT = 0;
        int coeffs = 0;
        for(UE ue : listeUE){
            creditsUE += ue.getCredit();
            coeffUE += ue.getCoefficient();
            for( EC ec : ue.getEcList()){
                CMT += ec.getCM();
                TDT += ec.getTD();
                TPT += ec.getTP();
                coeffs += ec.getCoefficient();
            }
        }
        model.addAttribute("cm",CMT);
        model.addAttribute("tp",TPT);
        model.addAttribute("td",TDT);
        model.addAttribute("coefficient",coeffs);
        model.addAttribute("credit",creditsUE);
        model.addAttribute("coeffUE",coeffUE);
        model.addAttribute("maquette", maquette);
        model.addAttribute("listeUE", maquette.getUeList());
        return "AfficherMaquette";
    }

    /*@PostMapping("/Maquette/voirSeance")
    public String voirSeance(Model model,Long id){
        Maquette maquette = maquetteService.findById(id);
        List<Seance> seances = seanceService.findAll();
        model.addAttribute("seances",seances);
        model.addAttribute("maquette",maquette);
        return "template_Seance";
    }*/
}