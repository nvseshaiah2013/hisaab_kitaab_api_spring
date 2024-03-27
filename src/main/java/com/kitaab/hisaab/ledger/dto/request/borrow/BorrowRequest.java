package com.kitaab.hisaab.ledger.dto.request.borrow;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class BorrowRequest {

    private String occasion;

    private String place;

    @DateTimeFormat(pattern = "YYYY-MM-DD")
    private Date expectedReturnDate;

    String borowee;
}
