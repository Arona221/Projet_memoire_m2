package connect.event.service;

import connect.event.dto.LieuDTO;
import connect.event.entity.Lieu;
import connect.event.repository.LieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LieuService {

    @Autowired
    private LieuRepository lieuRepository;

    public Lieu ajouterLieu(LieuDTO lieuDTO) {
        // Convertir DTO en entité
        Lieu lieu = new Lieu();
        lieu.setNom(lieuDTO.getNom());
        lieu.setAdresse(lieuDTO.getAdresse());
        lieu.setCapacite(lieuDTO.getCapacite());
        lieu.setDisponible(lieuDTO.isDisponible());
        lieu.setDepartement(lieuDTO.getDepartement());

        return lieuRepository.save(lieu);
    }
}
