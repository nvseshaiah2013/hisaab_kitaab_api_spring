package com.kitaab.hisaab.ledger.repository;


import com.kitaab.hisaab.ledger.entity.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);

    User findByUsername(String username);
}