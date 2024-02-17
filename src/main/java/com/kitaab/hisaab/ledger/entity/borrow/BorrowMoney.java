package com.kitaab.hisaab.ledger.entity.borrow;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@SuperBuilder(setterPrefix = "with")
@Document(collection = "borrow")
@BsonDiscriminator(key = "type", value = "Money")
public class BorrowMoney extends Borrow{

    private Double amount;
}
