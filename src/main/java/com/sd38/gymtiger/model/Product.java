package com.sd38.gymtiger.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Table(name = "Product")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Code")
    private String code;

    @Column(name = "Name")
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "CreateDate")
    private Date createDate;

    @Column(name = "UpdateDate")
    private Date updateDate;

    @Column(name = "Status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "CategoryId", referencedColumnName = "Id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "BrandId", referencedColumnName = "Id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "MaterialId", referencedColumnName = "Id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "FormId", referencedColumnName = "Id")
    private Form form;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> listProductDetail;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> listImage;
}
