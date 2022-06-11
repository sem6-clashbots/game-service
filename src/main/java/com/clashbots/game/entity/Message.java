package com.clashbots.game.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private UUID gameId;
    private String senderAddress;
    private String receiverAddress;
    private String message;
}
