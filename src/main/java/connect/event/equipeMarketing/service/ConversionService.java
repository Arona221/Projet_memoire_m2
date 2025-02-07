package connect.event.equipeMarketing.service;

import connect.event.equipeMarketing.entity.Conversion;
import connect.event.equipeMarketing.repository.ConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {

    @Autowired
    private ConversionRepository conversionRepository;

    public Conversion creerConversion(Conversion conversion) {
        return conversionRepository.save(conversion);
    }

    public Conversion getConversionById(Long id) {
        return conversionRepository.findById(id).orElse(null);
    }
}