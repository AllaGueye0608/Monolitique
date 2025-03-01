package uasz.sn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uasz.sn.model.Repartition;
import uasz.sn.service.RepartitionService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/choix")
public class RepartitionController {
    @Autowired
    private RepartitionService repartitionService;

    @GetMapping("")
    public ResponseEntity<List<Repartition>> findAll() {
        return ResponseEntity.ok(repartitionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Repartition repartition = repartitionService.findById(id);
        if (repartition == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Le choix avec cet id("+id+") n'existe pas");
        } else {
            return ResponseEntity.ok(repartition);
        }
    }

    @PutMapping("/{id}/valider")
    public ResponseEntity<?> valider(@PathVariable Long id) {
        Repartition repartition = repartitionService.valider(id);
        if (repartition == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("vous ne pouvez pas valider le choix car il est null");
        }
        return ResponseEntity.ok(repartition);
    }

    @PutMapping("/{id}/modifier")
    public ResponseEntity<?> modifierChoix(
            @PathVariable Long id,
            @RequestBody Repartition repartitionUpdate
    ) {
        Repartition repartitionExisting = repartitionService.findByEnseignantAndEnseignementAndType(
                repartitionUpdate.getEnseignant(),
                repartitionUpdate.getEnseignement(),
                repartitionUpdate.getType()
        );

        // Si la repartition existe déjà, renvoyer un message avec un code HTTP 409 (Conflict)
        if(repartitionExisting != null){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("L'enseignant a déjà fait le choix");  // Message d'erreur
        }

        Repartition repartition = repartitionService.findById(id);
        if (repartition != null) {
            if (repartition.getType() != null && !repartition.getType().equals(repartitionUpdate.getType())) {
                repartition.setType(repartitionUpdate.getType());
            }
            repartition.setDateChoix(LocalDate.now());
            if ((repartition.getEnseignant() != repartitionUpdate.getEnseignant()) && (repartitionUpdate.getEnseignant() != null)) {
                repartition.setEnseignant(repartitionUpdate.getEnseignant());
            }
            if ((repartition.getEnseignement() != repartitionUpdate.getEnseignement()) && (repartitionUpdate.getEnseignement() != null)) {
                repartition.setEnseignement(repartitionUpdate.getEnseignement());
            }
            repartition.setValide(repartitionUpdate.isValide());
            Repartition repartition1 = repartitionService.update(repartition);
            return ResponseEntity.ok(repartition1);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> supprimer(@PathVariable Long id){
        Repartition repartition = repartitionService.findById(id);
        if(repartition != null){
            repartitionService.delete(repartition);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Le choix a été supprimé");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouter(@RequestBody Repartition repartition){

        // Vérifier si une repartition avec les mêmes enseignant, enseignement et type existe déjà
        Repartition repartitionExisting = repartitionService.findByEnseignantAndEnseignementAndType(
                repartition.getEnseignant(),
                repartition.getEnseignement(),
                repartition.getType()
        );

        // Si la repartition existe déjà, renvoyer un message avec un code HTTP 409 (Conflict)
        if(repartitionExisting != null){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("L'enseignant a déjà fait le choix");  // Message d'erreur
        }

        // Si la repartition n'existe pas, on définit la date du choix et on crée la repartition
        repartition.setDateChoix(LocalDate.now());
        Repartition repartition1 = repartitionService.create(repartition);

        // Retourner la repartition créée avec un code HTTP 200 (OK)
        return ResponseEntity.ok(repartition1);
    }

    @GetMapping("/{id}/infos")
    public ResponseEntity<?> getInfo(@PathVariable Long id){
        Repartition repartition = repartitionService.findById(id);
        if(repartition == null){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("repartition null");
        }
        String enseignement = repartition.getEnseignement().getNom() +'('+repartition.getEnseignement().getNiveau()+") semestre(" + repartition.getEnseignement().getSemestre()+')';
        String enseignant = repartition.getEnseignant().getPrenom() +' '+repartition.getEnseignant().getNom();
        String type = repartition.getType();
        List<String> infos = new ArrayList<>();
        infos.add(enseignement);infos.add(enseignant);infos.add(type);
        return ResponseEntity.ok(infos);
    }
}
