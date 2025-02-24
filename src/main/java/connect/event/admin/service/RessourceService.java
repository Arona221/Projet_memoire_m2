package connect.event.admin.service;

import connect.event.admin.dto.*;
import connect.event.admin.entity.*;
import connect.event.admin.repository.*;
import connect.event.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RessourceService {

    private final RessourceRepository ressourceRepository;
    private final LieuRepository lieuRepository;
    private final TransportRepository transportRepository;
    private final EquipementRepository equipementRepository;
    private final FileStorageService fileStorageService;

    public RessourceService(RessourceRepository ressourceRepository,
                            LieuRepository lieuRepository,
                            TransportRepository transportRepository,
                            EquipementRepository equipementRepository,
                            FileStorageService fileStorageService) {
        this.ressourceRepository = ressourceRepository;
        this.lieuRepository = lieuRepository;
        this.transportRepository = transportRepository;
        this.equipementRepository = equipementRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public Page<RessourceDTO> searchRessources(RessourceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Direction.fromString(filter.getSortDirection()),
                filter.getSortField()
        ));

        return ressourceRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getSearchTerm() != null) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("nom")), "%" + filter.getSearchTerm().toLowerCase() + "%"));
            }

            if (filter.getType() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), filter.getType()));
            }

            if (filter.getPrixMin() != null) {
                predicates = cb.and(predicates, cb.ge(root.get("prix"), filter.getPrixMin()));
            }

            if (filter.getPrixMax() != null) {
                predicates = cb.and(predicates, cb.le(root.get("prix"), filter.getPrixMax()));
            }

            return predicates;
        }, pageable).map(this::convertToDTO);
    }

    private RessourceDTO convertToDTO(Ressource ressource) {
        return switch (ressource.getType()) {
            case LIEU -> LieuDTO.fromEntity((Lieu) ressource);
            case TRANSPORT -> TransportDTO.fromEntity((Transport) ressource);
            case EQUIPEMENT -> EquipementDTO.fromEntity((Equipement) ressource);
        };
    }

    @Transactional
    public LieuDTO createLieu(LieuDTO lieuDTO) {
        Lieu lieu = new Lieu();
        mapCommonAttributes(lieu, lieuDTO);
        lieu.setCapacite(lieuDTO.getCapacite());
        lieu.setDepartement(lieuDTO.getDepartement());
        lieu.setAdresse(lieuDTO.getAdresse());
        return LieuDTO.fromEntity(lieuRepository.save(lieu));
    }

    @Transactional
    public EquipementDTO createEquipement(EquipementDTO dto) {
        Equipement equipement = new Equipement();
        mapCommonAttributes(equipement, dto);
        equipement.setTypeEquipement(dto.getTypeEquipement());
        equipement.setQuantite(dto.getQuantite());
        equipement.setSpecifications(dto.getSpecifications());
        return EquipementDTO.fromEntity(equipementRepository.save(equipement));
    }

    private void mapCommonAttributes(Ressource ressource, RessourceDTO dto) {
        ressource.setNom(dto.getNom());
        ressource.setPrix(dto.getPrix());
        ressource.setImage(dto.getImage());
    }

    @Transactional
    public void deleteRessource(Long id) {
        ressourceRepository.findById(id).ifPresentOrElse(
                ressource -> {
                    if (ressource.getImage() != null) {
                        try {
                            fileStorageService.deleteFile(ressource.getImage());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    ressourceRepository.delete(ressource);
                },
                () -> { throw new ResourceNotFoundException("Ressource non trouvée"); }
        );
    }
    @Transactional
    public TransportDTO createTransport(TransportDTO transportDTO) {
        Transport transport = new Transport();
        mapCommonAttributes(transport, transportDTO);
        transport.setTypeTransport(transportDTO.getTypeTransport());
        transport.setNombrePlaces(transportDTO.getNombrePlaces());
        return TransportDTO.fromEntity(transportRepository.save(transport));
    }

    @Transactional
    public RessourceDTO updateRessource(Long id, RessourceDTO ressourceDTO) {
        return ressourceRepository.findById(id)
                .map(ressource -> {
                    updateCommonAttributes(ressource, ressourceDTO);
                    switch (ressource.getType()) {
                        case LIEU -> updateLieu((Lieu) ressource, (LieuDTO) ressourceDTO);
                        case TRANSPORT -> updateTransport((Transport) ressource, (TransportDTO) ressourceDTO);
                        case EQUIPEMENT -> updateEquipement((Equipement) ressource, (EquipementDTO) ressourceDTO);
                    }
                    return convertToDTO(ressourceRepository.save(ressource));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Ressource non trouvée"));
    }

    private void updateLieu(Lieu lieu, LieuDTO dto) {
        lieu.setCapacite(dto.getCapacite());
        lieu.setDepartement(dto.getDepartement());
        lieu.setAdresse(dto.getAdresse());
    }

    private void updateTransport(Transport transport, TransportDTO dto) {
        transport.setTypeTransport(dto.getTypeTransport());
        transport.setNombrePlaces(dto.getNombrePlaces());
    }

    private void updateEquipement(Equipement equipement, EquipementDTO dto) {
        equipement.setTypeEquipement(dto.getTypeEquipement());
        equipement.setQuantite(dto.getQuantite());
        equipement.setSpecifications(dto.getSpecifications());
    }

    private void updateCommonAttributes(Ressource ressource, RessourceDTO dto) {
        if (dto.getNom() != null) ressource.setNom(dto.getNom());
        if (dto.getPrix() > 0) ressource.setPrix(dto.getPrix());
        if (dto.getImage() != null) ressource.setImage(dto.getImage());
    }

    @Transactional(readOnly = true)
    public RessourceDTO getRessourceById(Long id) {
        return ressourceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ressource non trouvée"));
    }
    @Transactional(readOnly = true)
    public Page<RessourceDTO> getAllRessourcesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ressourceRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    @Transactional(readOnly = true)
    public List<RessourceDTO> getAllRessources() {
        return ressourceRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
}