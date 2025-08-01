package com.ecommerce.sb_ecom.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@ToString
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

     @NotBlank
     @Size(min = 3 ,message ="Product name must have contain atleast 3 character" )
    private  String productName;
    private String image;
  @NotBlank
   @Size(min = 6 ,message ="Product description must have contain atleast 6 character" )
    private String description;
    private Integer Quantity;
    private double price;
    private  double discount;
    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product" ,cascade = {CascadeType.PERSIST,CascadeType.MERGE} ,fetch = FetchType.EAGER)
    private List<CartItem> products= new ArrayList<>();
}
