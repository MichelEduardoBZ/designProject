package br.com.testDesign.repositories;

import br.com.testDesign.entities.PasswordRecoverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PasswordRecoverRepository extends JpaRepository<PasswordRecoverEntity, Long> {

    @Query("SELECT obj FROM PasswordRecoverEntity obj WHERE obj.token = :token AND obj.expiration > :now")
    List<PasswordRecoverEntity> searchValidTokens(String token, Instant now);

}
