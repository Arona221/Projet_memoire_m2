package connect.event.equipeMarketing.cotroller;

import connect.event.equipeMarketing.entity.SegmentAudience;
import connect.event.equipeMarketing.service.SegmentAudienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/segments")
public class SegmentAudienceController {

    @Autowired
    private SegmentAudienceService segmentService;

    @PostMapping
    public SegmentAudience creerSegment(@RequestBody SegmentAudience segment) {
        return segmentService.creerSegment(segment);
    }

    @GetMapping("/{id}")
    public SegmentAudience getSegment(@PathVariable Long id) {
        return segmentService.getSegmentById(id);
    }
}
