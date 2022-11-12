package br.com.zup.edu.nossalojavirtual.products;

import br.com.zup.edu.nossalojavirtual.users.User;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;

class NewQuestionRequest {

    @NotBlank
    private String title;

    @JsonCreator
    public NewQuestionRequest(String title) {
        this.title = title;
    }

    public NewQuestionRequest() {
    }

    public String getTitle() {
        return title;
    }

    public Question toQuestion(User user, Product product) {
        return new Question(title, user, product);
    }
}
