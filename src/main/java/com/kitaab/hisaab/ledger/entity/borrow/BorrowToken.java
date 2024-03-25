package com.kitaab.hisaab.ledger.entity.borrow;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;

@Document(collection = "borrow_tokens")
@Data
@Builder
public class BorrowToken {

    @Id
    private String id;

    @DocumentReference
    private Borrow borrow;

    private String secretToken;

    private Long validity;

    @CreatedDate
    private LocalDateTime createdDate;
}
