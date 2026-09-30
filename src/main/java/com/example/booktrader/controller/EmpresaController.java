package com.example.booktrader.controller;

import com.example.booktrader.DTO.EmpresaConsultaResponse;
import com.example.booktrader.DTO.EmpresaRequest;
import com.example.booktrader.DTO.EmpresaResponse;
import com.example.booktrader.DTO.UsuarioConsultaResponse;
import com.example.booktrader.entities.Empresa;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    @GetMapping
    public List<Empresa> consultaEmpresa() {
        return empresaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> consultaEmpresaPorId(@PathVariable Long id) {
        var empresa = empresaRepository.findById(id).orElse(null);

        if (empresa == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empresa);
    }

    //29092026
    @GetMapping("/cnpj/{cnpj}/usuarios")
    public ResponseEntity<List<UsuarioConsultaResponse>> buscarUsuariosPorCnpjEmpresa(@PathVariable String cnpj){

        var empresaBanco = empresaRepository.getEmpresaByCnpj(cnpj).orElse(null);
        if (empresaBanco == null)
            return ResponseEntity.notFound().build();

        var usuarioEmpresaBanco = empresaBanco.getUsuarios()
                .stream()
                .map(UsuarioConsultaResponse::new)
                .toList();

        return ResponseEntity.ok(usuarioEmpresaBanco);
    }

    @GetMapping
    public List<EmpresaConsultaResponse> listarTodos (){
        return empresaRepository.findAll().stream().map(EmpresaConsultaResponse::new).toList();
    }
//@pathvariable é quando vem da url, @requestbody é quando vem do json



    @PostMapping("")
    public ResponseEntity<EmpresaResponse> cadastrarEmpresa(@RequestBody EmpresaRequest empresaRequest){

        Empresa empresaBanco = new Empresa();

        empresaBanco.setRazaoSocial(empresaRequest.getRazaoSocial());
        empresaBanco.setCnpj(empresaRequest.getCnpj());
        empresaBanco.setInscricaoEstadual(empresaRequest.getInscricaoEstadual());
        empresaBanco.setNomeFantasia(empresaRequest.getNomeFantasia());

        empresaRepository.save(empresaBanco);

        return ResponseEntity.ok(new EmpresaResponse(empresaBanco.getId(), "Empresa Cadastrada"));
    }

}
