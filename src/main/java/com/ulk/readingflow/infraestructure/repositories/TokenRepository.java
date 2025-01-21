package com.ulk.readingflow.infraestructure.repositories;

import com.ulk.readingflow.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query("select t from TOKEN t inner join USER u on t.user.id = u.id where u.id = :uuid and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokensByUserId(@Param("uuid") UUID uuid);

}
