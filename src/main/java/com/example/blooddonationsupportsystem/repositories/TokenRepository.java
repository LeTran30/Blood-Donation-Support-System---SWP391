package com.example.blooddonationsupportsystem.repositories;

import com.example.blooddonationsupportsystem.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
           SELECT t FROM Token t 
           INNER JOIN User u ON t.user.id = u.id
           WHERE u.id = :id AND (t.expired = FALSE OR t.revoked = FALSE)
           """)
    List<Token> findAllUserTokenByUserId(Integer id);

    Optional<Token> findByToken(String token);

    @Query("SELECT t FROM Token t WHERE t.expired = true AND t.revoked = true")
    List<Token> findExpiredAndRevokedTokens();
}



