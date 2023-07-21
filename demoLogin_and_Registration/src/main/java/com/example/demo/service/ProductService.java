package com.example.demo.service;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    void save(Product product);

    Product getOne(Long id);

    void delete(Long id);

    List<Product> listAll(String keyword);

//    Page<Product> searchUsers(String keyword, Pageable pageable)

//    Page<Product> searchUsers(String keyword, int page, int size, String sortField, String sortDirection);

    Page<Product> findProductByName(String ten, Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> listAll(int pageNum, String sortField, String sortDir);

}
