package com.ToyRentalService.service;

import com.ToyRentalService.entity.Account;
import com.ToyRentalService.entity.ChatRoom;
import com.ToyRentalService.entity.Message;
import com.ToyRentalService.repository.ChatRoomRepository;
import com.ToyRentalService.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    public ChatRoom findOrCreateChatRoom(Account user1, Account user2) {
        return chatRoomRepository.findByUser1AndUser2(user1, user2)
                .orElseGet(() -> {
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUser1(user1);
                    chatRoom.setUser2(user2);
                    return chatRoomRepository.save(chatRoom);
                });
    }

    public Message saveMessage(Long roomId, Account sender, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(new Date());
        return messageRepository.save(message);
    }
}
