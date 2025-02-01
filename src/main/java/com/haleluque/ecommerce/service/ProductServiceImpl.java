package com.haleluque.ecommerce.service;

import com.haleluque.ecommerce.dto.ProductDTO;
import com.haleluque.ecommerce.dto.ProductResponse;
import com.haleluque.ecommerce.exceptions.ProductException;
import com.haleluque.ecommerce.exceptions.ResourceNotFoundException;
import com.haleluque.ecommerce.model.Category;
import com.haleluque.ecommerce.model.Product;
import com.haleluque.ecommerce.repositories.CategoryRepository;
import com.haleluque.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.haleluque.ecommerce.utils.PagingUtils.getSortByAndOrder;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = getSortByAndOrder(sortBy, sortOrder);

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        ProductResponse response = new ProductResponse();
        BuildResponse(response, productPage);
        return response;
    }

    @Override
    public ProductResponse getProductByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId) {
        Category category = findCategoryById(categoryId);

        Sort sortByAndOrder = getSortByAndOrder(sortBy, sortOrder);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(pageDetails, category);
        ProductResponse response = new ProductResponse();
        BuildResponse(response, productPage);
        return response;
    }

    @Override
    public ProductResponse getProductByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sortByAndOrder = getSortByAndOrder(sortBy, sortOrder);
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameContainingIgnoreCase(pageDetails, keyword);
        ProductResponse response = new ProductResponse();
        BuildResponse(response, productPage);
        return response;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO product) {
        //checks if products name already exist
        boolean exists = productRepository.existsByProductName(product.getProductName());
        if (exists)
            throw new ProductException("Product with name: " + product.getProductName() + " already exists",
                    Product.class.getSimpleName(), product.getProductId());

        Category category = findCategoryById(categoryId);

        Product newProduct = modelMapper.map(product, Product.class);
        newProduct.setImage("default.png");
        newProduct.setCategory(category);
        newProduct.setSpecialPrice(calculateSpecialPrice(newProduct.getSpecialPrice(), newProduct.getDiscount()));
        Product savedProduct = productRepository.save(newProduct);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO product) {
        Product updateProduct = findProductById(productId);

        Long categoryId = product.getCategory().getCategoryId();
        Category category = findCategoryById(categoryId);

        updateProduct.setProductName(product.getProductName());
        updateProduct.setDescription(product.getDescription());
        updateProduct.setPrice(product.getPrice());
        updateProduct.setDiscount(product.getDiscount());
        updateProduct.setQuantity(product.getQuantity());
        updateProduct.setSpecialPrice(calculateSpecialPrice(product.getPrice(), product.getDiscount()));
        updateProduct.setCategory(category);

        updateProduct = productRepository.save(updateProduct);
        return modelMapper.map(updateProduct, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product product = findProductById(productId);
        productRepository.delete(product);
        return "Product with id: " + productId + " deleted successfully !!";
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = findProductById(productId);
        product.setImage(fileService.uploadImage(path, image));
        return modelMapper.map(product, ProductDTO.class);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Product.class.getSimpleName(), "productId", productId));
    }

    private static double calculateSpecialPrice(double price, double discount) {
        return price - ((discount * 0.01) * price);
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Category.class.getSimpleName(), "categoryId", categoryId));
    }

    private void BuildResponse(ProductResponse response, Page<Product> productPage) {
        response.setContent(convertEntitiesToDTOs(productPage));
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLastPage(productPage.isLast());
    }

    private List<ProductDTO> convertEntitiesToDTOs(Page<Product> products) {
        return products
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }
}
