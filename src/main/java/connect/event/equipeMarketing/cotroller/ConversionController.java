package connect.event.equipeMarketing.cotroller;

import connect.event.equipeMarketing.entity.Conversion;
import connect.event.equipeMarketing.service.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversions")
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
