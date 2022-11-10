package br.com.zup.edu.nossalojavirtual.products;

import br.com.zup.edu.nossalojavirtual.users.User;
import br.com.zup.edu.nossalojavirtual.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/api/products/{id}/questions")
class QuestionController {

    Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final ProductRepository productRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher publisher;

    private final UserRepository userRepository;

    QuestionController(ProductRepository productRepository,
                       QuestionRepository questionRepository,
                       ApplicationEventPublisher publisher, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.questionRepository = questionRepository;
        this.publisher = publisher;
        this.userRepository = userRepository;
    }

    @PostMapping
    ResponseEntity<?> askQuestion(@PathVariable("id") UUID id, @RequestBody @Valid NewQuestionRequest newQuestion,
                                  @AuthenticationPrincipal Jwt jwt, UriComponentsBuilder uriBuilder) {

        User user = userRepository.findByEmail(jwt.getClaim("email")).orElseThrow(() -> {
            logger.warn("user unauthorized");
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "usuário não autenticado");
        });


        Optional<Product> possibleProduct = productRepository.findById(id);

        if (possibleProduct.isEmpty()) {
            logger.warn("Product id {} not found", id);
            return notFound().build();
        }

        Product product = possibleProduct.get();
        var question = newQuestion.toQuestion(user, product);
        questionRepository.save(question);

        publisher.publishEvent(new QuestionEvent(question, uriBuilder));

        var location = URI.create("/api/products/" + id.toString() + "/questions/" + question.getId());
        List<Question> questions = questionRepository.findByProduct(possibleProduct.get());

        List<QuestionResponse> response = QuestionResponse.from(questions);

        logger.info("Created new question: {}.", question.getTitle());

        return created(location).body(response);

    }
}
