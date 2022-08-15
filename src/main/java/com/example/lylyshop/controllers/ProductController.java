package com.example.lylyshop.controllers;

import com.example.lylyshop.models.Product;
import com.example.lylyshop.models.ResponseObject;
import com.example.lylyshop.respository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/Products")
@CrossOrigin("http://localhost:8080")
//@AllArgsConstructor
public class ProductController {
    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
        //this request is: http://localhost:8080/api/v1/Products
    List<Product> getAllProducts(){
        return repository.findAll();
    }
    @GetMapping("/Deleted")
    List<Product> getDeletedProducts() {
        return repository.findDeletedProducts(true);
    }
    @GetMapping("/{id}")
    ResponseEntity<ResponseObject> findById(@PathVariable String id) {
        Optional<Product> foundProduct = repository.findById(id);
       // return foundProduct.get();
        if (foundProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Query product successfully", foundProduct)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Cannot find with id =" + id,"")
            );
        }
    }
    @PostMapping("/insert")
    ResponseEntity<ResponseObject> insertProduct (@RequestBody Product newProduct) {
        List<Product> foundProducts = repository.findByProductName(newProduct.getProductName().trim());
        if(foundProducts.size() > 0){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Product name already taken", "")
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","insert product successfully", repository.save(newProduct))
        );
    }
    @PutMapping("/{id}")
    ResponseEntity<ResponseObject> updateProduct (@RequestBody Product newProduct, @PathVariable String id ){
        Product updateProduct = repository.findById(id)
                .map(product -> {
                    product.setProductName(newProduct.getProductName());
                    product.setPrice(newProduct.getPrice());
                    product.setDescription(newProduct.getDescription());
                    return repository.save(product);
                }).orElseGet(() -> {
                    newProduct.setId(id);
                    return repository.save(newProduct);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","update product successfully", updateProduct)
        );
    }
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseObject> deleteProduct ( @PathVariable String id ){
        Optional<Product> foundProduct = repository.findById(id);
        if (foundProduct.isPresent()){
            foundProduct.map(product -> {
                product.setIsDeleted(true);
                return repository.save(product);
            });
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Delete product successfully", "")
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed","Product not found!", "")
            );
    }
    @PostMapping("/Restore/{id}")
    ResponseEntity<ResponseObject> restore(@PathVariable String id) {
        Optional<Product> foundProduct = repository.findById(id);
        if (foundProduct.isPresent()){
            foundProduct.map(product -> {
                product.setIsDeleted(false);
                return repository.save(product);
            });
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok","Restore product successfully", "")
            );
        }
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed","Product not found!", "")
            );
    }
}

