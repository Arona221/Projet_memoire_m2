package connect.event.admin.service;

import connect.event.admin.dto.LieuDTO;
import connect.event.admin.dto.RessourceDTO;
import connect.event.admin.dto.TransportDTO;
import connect.event.admin.entity.Lieu;
import connect.event.admin.entity.Ressource;
import connect.event.admin.entity.Transport;
import connect.event.admin.repository.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RessourceService {

    @Autowired
    private RessourceRepository ressourceRepository;

    public List<RessourceDTO> getAllRessources() {
        return ressourceRepository.findAll().stream()
                .map(RessourceDTO::fromEntity)
                .toList();
    }

    public Optional<RessourceDTO> getRessourceById(Long id) {
        return ressourceRepository.findById(id).map(RessourceDTO::fromEntity);
    }

    public LieuDTO createLieu(LieuDTO lieuDTO) {
        Lieu lieu = new Lieu();
        lieu.setNom(lieuDTO.getNom());
        lieu.setPrix(lieuDTO.getPrix());
        lieu.setImage(lieuDTO.getImage());
        lieu.setCapacite(lieuDTO.getCapacite());
        lieu.setDepartement(lieuDTO.getDepartement());
        lieu.setAdresse(lieuDTO.getAdresse());
        return LieuDTO.fromEntity(ressourceRepository.save(lieu));
    }

    public TransportDTO createTransport(TransportDTO transportDTO) {
        Transport transport = new Transport();
        transport.setNom(transportDTO.getNom());
        transport.setPrix(transportDTO.getPrix());
        transport.setImage(transportDTO.getImage());
        transport.setTypeTransport(transportDTO.getTypeTransport());
        transport.setNombrePlaces(transportDTO.getNombrePlaces());
        return TransportDTO.fromEntity(ressourceRepository.save(transport));
    }

    public boolean deleteRessource(Long id) {
        if (ressourceRepository.existsById(id)) {
            ressourceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
