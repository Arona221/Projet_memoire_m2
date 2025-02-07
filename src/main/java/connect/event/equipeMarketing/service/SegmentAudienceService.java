package connect.event.equipeMarketing.service;

import connect.event.equipeMarketing.entity.SegmentAudience;
import connect.event.equipeMarketing.repository.SegmentAudienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegmentAudienceService {

    @Autowired
    private SegmentAudienceRepository segmentRepository;

    public SegmentAudience creerSegment(SegmentAudience segment) {
        return segmentRepository.save(segment);
    }

    public SegmentAudience getSegmentById(Long id) {
        return segmentRepository.findById(id).orElse(null);
    }
}
