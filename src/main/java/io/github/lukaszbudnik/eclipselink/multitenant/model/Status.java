package io.github.lukaszbudnik.eclipselink.multitenant.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(schema = "ref", uniqueConstraints = {@UniqueConstraint(name = "unique_name", columnNames = "name")})
@Data
@Cacheable
public class Status {

    @Id
    @SequenceGenerator(name = "status_seq", sequenceName = "status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_seq")
    private int id;
    private String name;

    public static enum Name {
        Created, Queued, Executed
    }
}
