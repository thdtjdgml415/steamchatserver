package com.example.steamchatserver.repository;

import com.example.steamchatserver.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {
}
