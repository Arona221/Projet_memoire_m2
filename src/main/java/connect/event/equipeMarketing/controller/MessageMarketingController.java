package connect.event.equipeMarketing.controller;

import connect.event.equipeMarketing.DTO.MessageRequest;
import connect.event.equipeMarketing.entity.MessageMarketing;
import connect.event.equipeMarketing.service.MessageMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages")
public class MessageMarketingController {

    @Autowired
    private MessageMarketingService messageService;

    @PostMapping
    public MessageMarketing creerMessage(@RequestBody MessageRequest request) {
        return messageService.creerMessage(request.getIdEvenement(), request.getContenu(), request.getCanal());
    }

    @GetMapping("/{id}")
    public MessageMarketing getMessage(@PathVariable Long id) {
        return messageService.getMessageById(id);
    }
}