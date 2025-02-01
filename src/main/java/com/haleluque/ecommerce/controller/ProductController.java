package com.haleluque.ecommerce.controller;

import com.haleluque.ecommerce.dto.ProductDTO;
import com.haleluque.ecommerce.dto.ProductResponse;
import com.haleluque.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.haleluque.ecommerce.config.AppConstants.*;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(value = "pageNumber", required = false, defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = SORT_DIRECTION) String sortOrder
    ){
        return new ResponseEntity<>(productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder), HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "pageNumber", required = false, defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = SORT_DIRECTION) String sortOrder
    ){
        return new ResponseEntity<>(productService.getProductByCategory(pageNumber, pageSize, sortBy, sortOrder, categoryId), HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyWord(
            @PathVariable String keyword,
            @RequestParam(value = "pageNumber", required = false, defaultValue = PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = SORT_DIRECTION) String sortOrder
    ){
        return new ResponseEntity<>(productService.getProductByKeyword(pageNumber, pageSize, sortBy, sortOrder, keyword), HttpStatus.OK);
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO product,
                                                 @PathVariable Long categoryId){
        ProductDTO productDTO = productService.addProduct(categoryId, product);
        return new ResponseEntity<>(productDTO, HttpStatus.CREATED);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<String> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long productId){
        productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>("Category with id " + productId +" updated successfully", HttpStatus.OK);
    }

    @PatchMapping("/admin/products/{productId}/image")
    public ResponseEntity<String> updateProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) throws IOException {
        productService.updateProductImage(productId, image);
        return new ResponseEntity<>("Product's image with id " + productId +" was updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
        String status = productService.deleteProduct(productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
