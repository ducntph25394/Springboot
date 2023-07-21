package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT p FROM Product p WHERE CONCAT(p.name, ' ', p.brand, ' ', p.madein, ' ', p.price) LIKE %?1%")
    public List<Product> search(String keyword);

    Page<Product> findProductByName(String keyword, Pageable pageable);

    Page<Product> findByNameContaining(String keyword, Pageable pageable);


}
