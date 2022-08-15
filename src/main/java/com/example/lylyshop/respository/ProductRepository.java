package com.example.lylyshop.respository;

import com.example.lylyshop.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByProductName(String productName);
    @Query ("{ 'isDeleted': ?0 }")
    List<Product> findDeletedProducts(boolean isDeleted);
}
