package com.zapix.sync.zapix.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "company")
public class Company extends BaseEntity {
    private String name;
    private String document;
    private String email;
    private String phone;
    private boolean active = true;
}
