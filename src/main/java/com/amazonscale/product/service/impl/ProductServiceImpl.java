package com.amazonscale.product.service.impl;

import com.amazonscale.product.dto.ProductRequest;
import com.amazonscale.product.dto.ProductResponse;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.mapper.ProductMapper;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.product.service.ProductService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository){
        this.repository = repository;
    }

    // CREATE PRODUCT
    @Override
    public ProductResponse createProduct(ProductRequest request){

        Product product = ProductMapper.toEntity(request); // creates product entity

        Product savedProduct = repository.save(product);  // hibernate execute SQL command INSERT INTO products ...

        return ProductMapper.toResponse(savedProduct);
    }


    // GET PRODUCT
    @Override
    @Transactional(readOnly = true)  // this transaction only reads data
    public ProductResponse getProduct(Long id){
        Product product = repository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(id));

        return ProductMapper.toResponse(product);
    }

    //GET ALL PRODUCTS
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts(){
        return repository.findAll().stream()
                .map(ProductMapper::toResponse).toList();  // this is stream API
        // this (Stream) converts every product into productResponse as Repository returns List<Product>
    }

    //UPDATE PRODUCT
    @Override
    public ProductResponse updateProduct(Long id , ProductRequest request){

        Product product = repository.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setStock(request.getStock());

        Product updated = repository.save(product);

        return ProductMapper.toResponse(updated);
    }

    //DELETE PRODUCT
    @Override
    public void deleteProduct(Long id){
        Product product = repository.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));

        repository.delete(product);
    }
}
