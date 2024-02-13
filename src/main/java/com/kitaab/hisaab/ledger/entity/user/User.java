package com.kitaab.hisaab.ledger.entity.user;

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
@Builder(setterPrefix = "with")
@Document(collection = "users")
public class User {

    @Id
    private String _id;
    @Indexed(unique = true, direction = IndexDirection.DESCENDING)
    private String username;
    private String name;
    private String password;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
