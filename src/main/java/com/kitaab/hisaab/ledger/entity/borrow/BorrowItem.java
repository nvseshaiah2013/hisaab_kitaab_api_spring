package com.kitaab.hisaab.ledger.entity.borrow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder(setterPrefix = "with")
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Document(collection = "borrow")
@BsonDiscriminator(key = "type", value = "Items")
@AllArgsConstructor
@NoArgsConstructor
public class BorrowItem extends Borrow{

    private String itemName;

    private String description;

    @Override
    public String getType() {
        return "Items";
    }
}
