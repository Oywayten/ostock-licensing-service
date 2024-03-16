package com.optimagrowth.license.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/**
 * Oywayten 27.01.2024.
 */

@Getter
@Setter
@ToString
@Entity
@Table(name = "licenses")
public class License extends RepresentationModel<License> {

    @Id
    @Column(nullable = false)
    private String licenseId;

    private String description;

    @Column(nullable = false)
    private String organizationId;

    @Transient
    private String organizationName;

    @Transient
    private String contactName;

    @Transient
    private String contactEmail;

    @Transient
    private String contactPhone;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String licenseType;

    private String comment;

    public License withComment(String comment) {
        setComment(comment);
        return this;
    }
}
