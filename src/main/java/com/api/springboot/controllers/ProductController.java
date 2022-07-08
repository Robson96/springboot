package com.api.springboot.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.api.springboot.models.ProductModel;
import com.api.springboot.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ProductController {

    @Autowired
    ProductRepository repository;

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts() {
        List<ProductModel> productModels = repository.findAll();
        if (!productModels.isEmpty()) {
            productModels.forEach(productModel -> {
                UUID id = productModel.getIdProduct();
                productModel.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            });
        }
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductModel> getOneProduct(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> productModel = repository.findById(id);
        if (productModel.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.status(HttpStatus.FOUND).body(productModel.get());
    }

    @PostMapping("/products/create")
    public ResponseEntity<ProductModel> postProductModel(@RequestBody ProductModel productModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(productModel));
    }

    @PutMapping("/products/edit/{id}")
    public ResponseEntity<ProductModel> putProductModel(@PathVariable(value = "id") UUID id, @RequestBody ProductModel productModel) {

        Optional<ProductModel> modelOptional = repository.findById(id);
        if (modelOptional.isEmpty())
            return ResponseEntity.notFound().build();

        ProductModel productModel1 = modelOptional.get();
        productModel1.setName(productModel.getName());
        productModel1.setValue(productModel.getValue());

        return ResponseEntity.ok(repository.save(productModel1));
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<Object> deleteProductModel(@PathVariable(value = "id") UUID id) {
        Optional<ProductModel> model = repository.findById(id);
        if(model.isEmpty())
            return ResponseEntity.notFound().build();
        repository.delete(model.get());
        return ResponseEntity.ok("Product delete");
    }
}
