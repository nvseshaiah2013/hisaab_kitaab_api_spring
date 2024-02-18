package com.kitaab.hisaab.ledger.entity.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * CustomUserDetails implementation.
 * It is created to store the metadata of the user when the user is logged in.
 *
 */
public class CustomUserDetails extends User {

    private final Map<String, Object> metadata;

    /**
     * Constructor to instantiate the CustomUserDetails
     *
     * @param username username of the user
     * @param password password of the user
     * @param authorities granted authorities
     */
    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.metadata = new HashMap<>();
    }

    /**
     * Method to add the metadata in the user object
     *
     * @param key name of the key of metadata
     * @param value value of the metadata
     * @return returns the current metadata map.
     */
    public Map<String, Object> put(String key, Object value) {
        metadata.put(key, value);
        return this.metadata;
    }

    /**
     * Method to extract the value of the metadata
     *
     * @param key name of the key of metadata
     * @return value if it is present in map or else null
     */
    public Object get(String key) {
        return metadata.get(key);
    }
}
