package br.com.zup.edu.nossalojavirtual;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Endpoint(id = "metrica-teste", enableByDefault = true)
@Component
public class TesteMetrica {

    @ReadOperation
    public String say() {
        return "olá mundo";
    }
}
