package io.github.lukaszbudnik.eclipselink.multitenant.dao;

import io.github.lukaszbudnik.eclipselink.multitenant.model.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.PrivateKey;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReadContext extends Context {
    private PrivateKey privateKey;

    public ReadContext(String tenant, PrivateKey privateKey) {
        super(tenant);
        this.privateKey = privateKey;
    }
}
