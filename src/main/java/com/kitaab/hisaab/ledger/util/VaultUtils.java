package com.kitaab.hisaab.ledger.util;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import jakarta.ws.rs.NotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * Utility class to fetch secrets from OCI Vault
 */
@Slf4j
public class VaultUtils {

    public VaultUtils() {
        throw new NotSupportedException("Creating a instance of this class is not supported");
    }

    public static String getSecretFromVault(String region, String secretOcid, AuthenticationDetailsProvider provider) {
        log.info("Fetching the vault secret from the region : {}", region);
        GetSecretBundleResponse getSecretBundleResponse;
        try (SecretsClient secretsClient = SecretsClient.builder().build(provider)) {
            secretsClient.setRegion(region);
            GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest
                    .builder()
                    .secretId(secretOcid)
                    .build();
            getSecretBundleResponse = secretsClient.getSecretBundle(getSecretBundleRequest);
        }
        log.debug("Started decoding the base64 secret bundle");
        Base64SecretBundleContentDetails base64SecretBundleContentDetails =
                (Base64SecretBundleContentDetails) getSecretBundleResponse.getSecretBundle().getSecretBundleContent();
        byte[] secretValueDecoded = Base64.decodeBase64(base64SecretBundleContentDetails.getContent());
        log.debug("Finished decoding the base64 secret bundle");
        return new String(secretValueDecoded);
    }
}
