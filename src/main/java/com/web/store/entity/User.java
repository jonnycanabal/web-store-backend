package com.web.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.web.store.validation.ExistsByUsername;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Getter
@Setter
@Table(name = "users")
@JsonPropertyOrder({"id", "firstsName", "middleName", "lastName", "secondLastName", "phoneNumber", "email", "username", "password", "roles", "shoppingCart", "enabled", "admin", "createAt"})
public class User {

    @Id // Llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Con @GeneratedValue e Identity el ID sera "Autoincremental"
    private Long id;

    private String firstsName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private String PhoneNumber;
    private String email;

    @ExistsByUsername //anotacion de validacion personalizada
    @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 12)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String password;

    @JsonIgnoreProperties({"users", "handler", "hibernateLazy/initializer"})
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})})
    private List<Role> roles;

    public User() {
        roles = new ArrayList<>();
    }

    @Column(name = "enabled")
    private Boolean enabled;

    //@JsonProperty(access = Access.WRITE_ONLY)
    @Transient
    private boolean admin;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    private ShoppingCart shoppingCart;

    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @PrePersist
    public void prePersist() {
        this.createAt = new Date();
    }
}
