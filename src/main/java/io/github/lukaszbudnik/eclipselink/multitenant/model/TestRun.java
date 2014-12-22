package io.github.lukaszbudnik.eclipselink.multitenant.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;
import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "testCases")
@ToString(exclude = "testCases")
@Entity
@Table(name = "test_run")
@Multitenant(MultitenantType.TABLE_PER_TENANT)
@TenantTableDiscriminator(type = TenantTableDiscriminatorType.SCHEMA)
public class TestRun {

    @Id
    @SequenceGenerator(name = "test_run_seq", sequenceName = "test_run_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_run_seq")
    private int id;

    private String name;

    @ManyToOne(optional = true)
    @JoinColumn(name = "configuration_id", referencedColumnName = "id")
    private Configuration configuration;

    @OneToMany(mappedBy = "testRun", cascade = CascadeType.ALL)
    private List<TestCase> testCases;
}
