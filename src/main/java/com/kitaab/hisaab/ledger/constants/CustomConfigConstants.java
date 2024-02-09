package com.kitaab.hisaab.ledger.constants;

import jakarta.ws.rs.NotSupportedException;

public class CustomConfigConstants {

    public CustomConfigConstants() {
        throw new NotSupportedException("Cannot create instance of this class");
    }

    public static final String MONGO_DB_URL_SECRET = "mongo_db_secret";

    public static final String OCI_REGION="region";
}
