package com.clashbots.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "games")
@EqualsAndHashCode(callSuper = true)
public class Game extends BaseEntity {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String user_id_human;
    private String user_id_robot;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'SEARCHING'")
    private GameStatus status;

    public void setPlayer(String userAddress, Side userSide) {
        if(userSide == Side.HUMANS){
            this.setUser_id_human(userAddress);
        }else if(userSide == Side.ROBOTS){
            this.setUser_id_robot(userAddress);
        }
    }
}
