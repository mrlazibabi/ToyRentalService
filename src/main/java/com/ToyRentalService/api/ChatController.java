package com.ToyRentalService.api;

import com.ToyRentalService.Dtos.Response.MessageDTO;
import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.Message;
import com.ToyRentalService.service.AccountService;
import com.ToyRentalService.service.AuthenticationService;
import com.ToyRentalService.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private AuthenticationService authenticationService;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/{roomId}")
    public Message sendMessage(@Payload MessageDTO messageDTO, @PathVariable Long roomId) {
        Account sender = authenticationService.getCurrentAccount();
        return chatService.saveMessage(roomId, sender, messageDTO.getContent());
    }
}
