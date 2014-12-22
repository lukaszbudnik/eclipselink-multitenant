package io.github.lukaszbudnik.eclipselink.multitenant.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Context {
    private String tenant;
}
