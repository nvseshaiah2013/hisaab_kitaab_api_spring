package com.kitaab.hisaab.ledger.entity.borrow;

import com.kitaab.hisaab.ledger.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Document(collection = "borrow")
@Data
@SuperBuilder(setterPrefix = "with")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class Borrow {

    @MongoId
    @EqualsAndHashCode.Include
    private String id;

    @DocumentReference
    private User borrower;

    @DocumentReference
    private User borowee;

    private Date expectedReturnDate;

    private Date actualReturnDate;

    private String place;

    private String occasion;

    private BorrowStatus status;

    private String type;
}
