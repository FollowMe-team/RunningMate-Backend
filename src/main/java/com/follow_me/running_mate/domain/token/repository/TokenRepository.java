package com.follow_me.running_mate.domain.token.repository;

import com.follow_me.running_mate.domain.token.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {

}
