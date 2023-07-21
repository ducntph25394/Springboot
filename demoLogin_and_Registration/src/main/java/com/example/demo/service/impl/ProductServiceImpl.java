package com.example.demo.service.impl;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository repository;

    @Override
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Override
    public void save(Product product) {
        repository.save(product);
    }

    @Override
    public Product getOne(Long id) {
        return repository.findById(id).get();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Product> listAll(String keyword) {
        if (keyword != null) {
            return repository.search(keyword);
        }
        return repository.findAll();
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return repository.findByNameContaining(keyword, pageable);
    }

    @Override
    public Page<Product> listAll(int pageNum, String sortField, String sortDir) {

        Pageable pageable = PageRequest.of(pageNum - 1, 2,
                sortDir.equals("asc") ? Sort.by(sortField).ascending()
                        : Sort.by(sortField).descending()
        );

        return repository.findAll(pageable);
    }

    @Override
    public Page<Product> findProductByName(String ten, Pageable pageable) {
        return repository.findProductByName(ten,pageable);
    }

//    @Override
//    public Page<Product> searchUsers(String keyword, int page, int size, String sortField, String sortDirection) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortField));
//        return repository.findProductByName(keyword, pageable);
//    }
}
