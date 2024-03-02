package com.kitaab.hisaab.ledger.entity.borrow;

import com.kitaab.hisaab.ledger.entity.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "borrow_tokens")
@Data
public class BorrowToken {

    @Id
    private String id;

    private User borrower;

    private Borrow borrow;

    private String secretToken;

    private long validity;
}
