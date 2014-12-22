package io.github.lukaszbudnik.eclipselink.multitenant.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.persistence.annotations.Multitenant;
import org.eclipse.persistence.annotations.MultitenantType;
import org.eclipse.persistence.annotations.TenantTableDiscriminator;
import org.eclipse.persistence.annotations.TenantTableDiscriminatorType;

import javax.persistence.*;

@Data
@EqualsAndHashCode(exclude = "testRun")
@ToString(exclude = "testRun")
@Entity
@Table(name = "test_case")
@Multitenant(MultitenantType.TABLE_PER_TENANT)
@TenantTableDiscriminator(type = TenantTableDiscriminatorType.SCHEMA)
public class TestCase {

    @Id
    @SequenceGenerator(name = "test_case_seq", sequenceName = "test_case_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_case_seq")
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "test_run_id", referencedColumnName = "id")
    private TestRun testRun;
}
