
package com.example.booktrader.controller;

import com.example.booktrader.DTO.EnderecoResponse;
import com.example.booktrader.utils.ConsultaCepUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/cep")
public class CepController {

    // cria um objeto para consultar o viacep
    private ConsultaCepUtils consultaCepUtils = new ConsultaCepUtils();

    @GetMapping("/{cep}")
    public ResponseEntity<?> consultarCep(@PathVariable String cep) {


        if (cep.length() != 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O CEP deve possuir exatamente 8 números.");
        }

        // consulta o viacep e guarda o endereco encontrado
        EnderecoResponse endereco = consultaCepUtils.consultarCep(cep);

        return ResponseEntity.ok(endereco);
    }
}
