package com.pragati.ecommerce.product;

import com.pragati.ecommerce.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // explicit strategy
    private Integer id;
    private String name;
    private String description;
    private double availableQuantity;
    private BigDecimal price;
    @ManyToOne(fetch = FetchType.LAZY) // lazy loading is better for performance
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
