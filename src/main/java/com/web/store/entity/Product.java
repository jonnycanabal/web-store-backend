package com.web.store.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Integer price;

    @Lob
    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    public byte[] photo;

    @ManyToMany
    @JsonIgnoreProperties("products")
    private List<Brand> brand = new ArrayList<>();

    @ManyToMany(mappedBy = "products")
    @JsonIgnoreProperties("products")
    @JsonIgnore
    private Set<ShoppingCart> shoppingCarts = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Bogota")
    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @PrePersist
    public void prePersist() {
        this.createAt = new Date();
    }

    public Integer getPhotoHashCode() {
        return (this.photo != null) ? this.photo.hashCode() : null;
    }

}
