package pl.urban.taw_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = true)
    private String ingredients;

    @Column(nullable = false)
    private double price;

    @Lob
    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean available = true;

    @JsonIgnore
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMenu> orderMenus;

}
