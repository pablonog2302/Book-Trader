
package com.example.booktrader.utils;

import com.example.booktrader.DTO.EnderecoResponse;
import org.springframework.web.client.RestClient;

public class ConsultaCepUtils {

    // cria um objeto para fazer requisicoes ao viacep, restclient é uma ferramenta dentro do springboot
    private RestClient restClient = RestClient.create();


    public EnderecoResponse consultarCep(String cep) {

        EnderecoResponse endereco = restClient.get()

                // monta a URL usando o cep informado
                .uri("https://viacep.com.br/ws/" + cep + "/json/")

                // envia a requisicao e recebe a resposta
                .retrieve()

                // converte a resposta para um objeto EnderecoResponse
                .body(EnderecoResponse.class);

        return endereco;
    }
}
