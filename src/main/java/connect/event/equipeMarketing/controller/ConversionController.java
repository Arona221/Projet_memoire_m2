package connect.event.equipeMarketing.controller;

import connect.event.equipeMarketing.entity.Conversion;
import connect.event.equipeMarketing.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversions")
@CrossOrigin(origins = "http://localhost:4200")
public class ConversionController {

    @Autowired
    private ConversionService conversionService;

    @PostMapping
    public Conversion creerConversion(@RequestBody Conversion conversion) {
        return conversionService.creerConversion(conversion);
    }

    @GetMapping("/{id}")
    public Conversion getConversion(@PathVariable Long id) {
        return conversionService.getConversionById(id);
    }
}
