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

    private Integer human_hp = 5;
    private Integer robot_hp = 5;

    private Move human_move = Move.WAIT;
    private Move robot_move = Move.WAIT;

    private Side winner;

    public void setPlayer(String userAddress, Side userSide) {
        if (userSide == Side.HUMANS) {
            this.setUser_id_human(userAddress);
        } else if (userSide == Side.ROBOTS) {
            this.setUser_id_robot(userAddress);
        }
    }

    public void setMove(String userAddress, Move move) {
        if (move != Move.WAIT) {
            if (userAddress.equals(this.user_id_human)) {
                this.setHuman_move(move);
            } else if (userAddress.equals(this.user_id_robot)) {
                this.setRobot_move(move);
            }
        }
    }

    public void endRound(Side winner) {
        boolean endGame = false;
        if (winner == Side.ROBOTS) {
            int newHp = this.getHuman_hp() - 1;
            this.setHuman_hp(newHp);
            if (newHp <= 0){
                endGame = true;
            }
        }
        if (winner == Side.HUMANS) {
            int newHp = this.getRobot_hp() - 1;
            this.setRobot_hp(newHp);
            if (newHp <= 0){
                endGame = true;
            }
        }

        if(endGame){
            this.setWinner(winner);
            this.setStatus(GameStatus.FINISHED);
        }
    }
}
