package com.clashbots.game.repository;

import com.clashbots.game.entity.Game;
import com.clashbots.game.entity.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {
    @Query("SELECT g from Game g where g.status = 'SEARCHING' AND g.user_id_human IS NULL AND g.user_id_robot IS NOT NULL ORDER BY g.created_at")
    List<Game> findRobotOpponent();
    @Query("SELECT g from Game g where g.status = 'SEARCHING' AND g.user_id_robot IS NULL AND g.user_id_human IS NOT NULL ORDER BY g.created_at")
    List<Game> findHumanOpponent();

    @Query("SELECT g from Game g where g.status = 'STARTING' AND g.user_id_robot IS NOT NULL AND g.user_id_human IS NOT NULL ORDER BY g.created_at")
    List<Game> findStartingGames();
    @Query("SELECT g from Game g where g.status = 'IN_PROGRESS' AND g.user_id_robot IS NOT NULL AND g.user_id_human IS NOT NULL ORDER BY g.created_at")
    List<Game> findInProgressGames();
}
