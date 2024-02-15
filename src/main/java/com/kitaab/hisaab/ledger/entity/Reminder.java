package com.kitaab.hisaab.ledger.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "reminders")
public class Reminder {
    @Id
    private String _id;
    @Indexed(unique = true,direction = IndexDirection.DESCENDING)
    private String borrowId;
    private String borrower;
    private String borowee;
    private String message;
    private boolean read;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
