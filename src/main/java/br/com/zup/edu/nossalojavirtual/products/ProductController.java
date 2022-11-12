package br.com.zup.edu.nossalojavirtual.products;

import br.com.zup.edu.nossalojavirtual.categories.CategoryRepository;
import br.com.zup.edu.nossalojavirtual.shared.validators.ObjectIsRegisteredValidator;
import br.com.zup.edu.nossalojavirtual.users.User;
import br.com.zup.edu.nossalojavirtual.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/products")
class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PhotoUploader photoUploader;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductRepository productRepository,
                             CategoryRepository categoryRepository,
                             PhotoUploader photoUploader, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.photoUploader = photoUploader;
        this.userRepository = userRepository;
    }

    @PostMapping
    ResponseEntity<?> create(@RequestBody @Valid NewProductRequest newProduct, @AuthenticationPrincipal Jwt jwt) {

        User user = userRepository.findByEmail(jwt.getClaim("email")).orElseThrow(() -> {
            logger.warn("user unauthorized");
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "usuario não autenticado");
        });


        Product product = newProduct.toProduct(photoUploader, categoryRepository::findCategoryById, user);
        productRepository.save(product);

        logger.info("Created new product: {}.", product.getName());

        URI location = URI.create("/api/products/" + product.getId());
        return created(location).build();
    }

    @InitBinder(value = {"newProductRequest"})
    void initBinder(WebDataBinder binder) {

        binder.addValidators(
                new ObjectIsRegisteredValidator<>("categoryId",
                        "category.id.dontExist",
                        NewProductRequest.class,
                        categoryRepository::existsById));
    }
}
