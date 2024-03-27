package com.kitaab.hisaab.ledger.dto.request.borrow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemBorrowRequest extends BorrowRequest {

    private String itemName;

    private String description;
}
