package uasz.sn.GestionEnseignement.GestionDesMaquettes.GestionDesUE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.model.Enseignement;
import uasz.sn.GestionEnseignement.GestionDeEmploiDuTemps.service.EnseignementService;
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
public class UEController {
    @Autowired
    private UEService ueService;
    @Autowired
    private ECService ecService;
    @Autowired
    private MaquetteService maquetteService;
    @Autowired
    private UserService userService;
    @Autowired
    private EnseignementService enseignementService;

    @GetMapping("/UE")
    public String afficherUE(Model model, Long idMaquette, Principal principal){
        User permanent = userService.findByUsername(principal.getName());
        model.addAttribute("nom",permanent.getNom());
        model.addAttribute("prenom",permanent.getPrenom().charAt(0));
        model.addAttribute("listeEC",ecService.findAll());
        List<UE> ueList = ueService.findAll();

        if(idMaquette != null){
            Maquette maquette = maquetteService.findById(idMaquette);
            if(maquette != null){
                ueList = maquette.getUeList();
                model.addAttribute("maquette",maquette);
            }
        }
        model.addAttribute("listeUE",ueList);
        return "template_UE";
    }
    @PostMapping("/UE/ajouterUE")
    public String ajouterUE(Long idMaquette,String code,String intitule,int credit,int coefficient,Long[] idECs ){
        UE ue = new UE();
        ue.setCredit(credit);
        ue.setCode(code);ue.setIntitule(intitule);ue.setCoefficient(coefficient);
        ue.setActive(true);
        ue.setArchive(false);
        UE ue1 = ueService.create(ue);
        if(idECs != null){
            for(Long idEc : idECs){
                EC ec = ecService.findById(idEc);
                if(ec.getUe() == null){
                    ue1.getEcList().add(ec);
                    ec.setUe(ue1);
                    ecService.update(ec);
                }

            }
            ueService.update(ue1);
        }


        if(idMaquette != null){

            Maquette maquette = maquetteService.findById(idMaquette);
            if(maquette != null && ue1 != null){
                 maquette.getUeList().add(ue1);
            }

            return "redirect:/UE?idMaquette="+idMaquette;

        }else{
            return "redirect:/UE";
        }
    }
    @PostMapping("/UE/modifierUE")
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
            ue1.getEcList().clear();
            ue1.getEcList().addAll(ecs);
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

    @PostMapping("/UE/supprimerUE")
    public String supprimerUE( Long idMaquette,  Long id) {
        boolean deleted = ueService.delete(id);

        if (!deleted) {
            return "redirect:/UE?error=UE non trouvée";
        }

        return idMaquette != null ? "redirect:/UE?idMaquette=" + idMaquette : "redirect:/UE";
    }

    @GetMapping("/UE/detail")
    public String voirDetail(Model model,Long id){
        UE ue = ueService.findById(id);
        model.addAttribute("ue",ue);
        model.addAttribute("ecList",ue.getEcList());
        return "template_UEDetail";
    }

    @PostMapping("/UE/ajouterEC")
    public String ajouterEC(Long id, String code, String intitule, int CM, int TD, int TP, int coefficient) {
        UE ue = ueService.findById(id);
        if(ue != null){
            EC ec = new EC();
            ec.setActive(true);
            ec.setCode(code);
            ec.setIntitule(intitule);
            ec.setCM(CM);
            ec.setTD(TD);
            ec.setTP(TP);
            ec.setArchive(false);
            ec.setCoefficient(coefficient);
            ec.setUe(ue);;
            EC ec1 = ecService.create(ec);
            if(ec1 != null && ec1.getUe() != null &&  ec1.getUe().getMaquettes() != null) {
                List<String> types = new ArrayList<>();
                types.add("TP");types.add("TD");types.add("CM");
                for(Maquette maquette : ec1.getUe().getMaquettes()){
                    for(String type : types){
                        if (!enseignementService.exists(maquette, ec1.getUe(), ec1, type)) {
                            Enseignement enseignement = new Enseignement();
                            enseignement.setMaquette(maquette);
                            enseignement.setUe(ec1.getUe());
                            enseignement.setEc(ec);
                            enseignement.setType(type);
                            enseignementService.save(enseignement);
                        }
                    }
                }
            }
        }
        return "redirect:/UE/detail?id=" + id;
    }


    @PostMapping("/UE/modifierEC")
    public String modifierEC(Long idUE,Long id,EC ec){
        EC existing = ecService.findById(id);
        if(existing != null){
            existing.setCode(ec.getCode());existing.setIntitule(ec.getIntitule());
            existing.setCM(ec.getCM());existing.setTD(ec.getTD());existing.setTP(ec.getTP());
            existing.setCoefficient(ec.getCoefficient());
            ecService.update(existing);
        }
        return "redirect:/UE/detail?id="+idUE;
    }

    @PostMapping("UE/supprimerEC")
    public String supprimerEC(Long idUE,Long id){
        EC existing = ecService.findById(id);
        if(existing != null){
            existing.setUe(null);
            ecService.update(existing);
            ecService.delete(existing);
        }
        return "redirect:/UE/detail?id="+idUE;
    }

    @PostMapping("/UE/archiver")
    public String archiver(Long id){
        UE ue = ueService.findById(id);
        if(ue != null){
            ueService.archiver(id);
        }
        return "redirect:/UE";
    }

    @PostMapping("/UE/activer")
    public String activer(Long id){
        UE ue = ueService.findById(id);
        if(ue != null){
            ueService.activer(id);
        }

        return "redirect:/UE";
    }

}
