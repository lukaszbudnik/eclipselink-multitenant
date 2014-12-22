package io.github.lukaszbudnik.eclipselink.multitenant.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.PublicKey;

@Data
@EqualsAndHashCode(callSuper = true)
public class WriteContext extends Context {
    private PublicKey publicKey;

    public WriteContext(String tenant, PublicKey publicKey) {
        super(tenant);
        this.publicKey = publicKey;
    }
}
