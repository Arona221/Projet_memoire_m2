package connect.event.controller;

import connect.event.dto.LieuDTO;
import connect.event.entity.Lieu;
import connect.event.service.LieuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("lieux")
public class LieuController {

    @Autowired
    private LieuService lieuService;

    @PostMapping("/add")
    public ResponseEntity<Lieu> ajouterLieu(@Valid @RequestBody LieuDTO lieuDTO) {
        Lieu nouveauLieu = lieuService.ajouterLieu(lieuDTO);
        return ResponseEntity.ok(nouveauLieu);
    }

}
