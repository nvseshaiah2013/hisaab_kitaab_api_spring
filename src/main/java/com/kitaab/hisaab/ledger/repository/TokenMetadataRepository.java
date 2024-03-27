package com.kitaab.hisaab.ledger.repository;

import com.kitaab.hisaab.ledger.entity.user.TokenMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TokenMetadataRepository extends MongoRepository<TokenMetadata, String> {
    TokenMetadata findByUsernameOrderByCreatedAtDesc(String username);
}
