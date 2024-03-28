package com.kitaab.hisaab.ledger.entity;

import com.kitaab.hisaab.ledger.entity.borrow.Borrow;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;

@Data
@Builder
@Document(collection = "reminders")
public class Reminder {
    @Id
    private String id;
    @Indexed(unique = true,direction = IndexDirection.DESCENDING)

    @DocumentReference
    private Borrow borrow;

    private String message;

    private String header;

    private boolean read;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}
