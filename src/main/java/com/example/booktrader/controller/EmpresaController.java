package com.example.booktrader.controller;

import com.example.booktrader.DTO.EmpresaRequest;
import com.example.booktrader.DTO.EmpresaResponse;
import com.example.booktrader.entities.Empresa;
import com.example.booktrader.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> consultaEmpresaPorId(@PathVariable Long id) {
        var empresa = empresaRepository.findById(id).orElse(null);

        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empresa);
    }



    @PostMapping("")
    public ResponseEntity<EmpresaResponse> cadastrarEmpresa(@RequestBody EmpresaRequest empresaRequest){

        Empresa empresaBanco = new Empresa();

        empresaBanco.setRazaoSocial(empresaRequest.getRazaoSocial());
        empresaBanco.setCnpj(empresaRequest.getCnpj());
        empresaBanco.setInscricaoEstadual(empresaRequest.getInscricaoEstadual());
        empresaBanco.setNomeFantasia(empresaRequest.getNomeFantasia());

        empresaRepository.save(empresaBanco);

        return ResponseEntity.ok(new EmpresaResponse(empresaBanco.getId()
                , "Empresa Cadastrada"));
    }

}
