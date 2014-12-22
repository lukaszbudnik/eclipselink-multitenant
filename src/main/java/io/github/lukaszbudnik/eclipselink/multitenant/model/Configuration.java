package io.github.lukaszbudnik.eclipselink.multitenant.model;

import lombok.Data;
import org.eclipse.persistence.annotations.*;

import javax.persistence.*;


@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = "unique_name", columnNames = "name")})
@Data
@Multitenant(MultitenantType.TABLE_PER_TENANT)
@TenantTableDiscriminator(type = TenantTableDiscriminatorType.SCHEMA)
@Cacheable
public class Configuration {

    @Id
    @SequenceGenerator(name = "configuration_seq", sequenceName = "configuration_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuration_seq")
    private int id;

    @Basic(optional = false)
    private byte[] key;

    private String name;

    @ReadTransformer(transformerClass = DecryptionTransformer.class)
    @WriteTransformer(transformerClass = EncryptionTransformer.class)
    @Column(columnDefinition = "binary(1000)")
    private String username;

    @ReadTransformer(transformerClass = DecryptionTransformer.class)
    @WriteTransformer(transformerClass = EncryptionTransformer.class)
    @Column(columnDefinition = "binary(1000)")
    private String password;

}