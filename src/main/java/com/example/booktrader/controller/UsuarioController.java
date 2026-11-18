package com.example.booktrader.controller;


import com.example.booktrader.DTO.AtualizaStatusUsuario;
import com.example.booktrader.DTO.UsuarioConsultaResponse;
import com.example.booktrader.DTO.UsuarioRequest;
import com.example.booktrader.DTO.UsuarioIdentidadeResponse;
import com.example.booktrader.DTO.UsuarioResponse;
import com.example.booktrader.entities.Usuario;
import com.example.booktrader.repository.UsuarioRepository;
import com.example.booktrader.utils.CpfUtils;
import com.example.booktrader.utils.OnlyNumber;
import com.example.booktrader.utils.SenhaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @GetMapping
    public List<UsuarioConsultaResponse> consultaUsuario(){

        return usuarioRepository.findAll().stream().map(UsuarioConsultaResponse::new).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioConsultaResponse> consultaUsuarioPorId(@PathVariable Long id){

        var usuario = usuarioRepository.findById(id).orElse(null);

        if(usuario == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado.");
        }
        return ResponseEntity.ok(new UsuarioConsultaResponse(usuario));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioIdentidadeResponse> consultarUsuarioAutenticado(HttpServletRequest request) {
        String cpf = (String) request.getAttribute("usuarioCpf");
        if (cpf == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Sua sessão expirou. Faça login novamente.");
        }
        return usuarioRepository.getUsuarioByCpf(cpf)
                .map(usuario -> ResponseEntity.ok(new UsuarioIdentidadeResponse(usuario)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Sua sessão expirou. Faça login novamente."));
    }

    @PostMapping("")
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@RequestBody UsuarioRequest usuarioRequest){

        if (usuarioRequest == null
                || usuarioRequest.getNome() == null || usuarioRequest.getNome().isBlank()
                || usuarioRequest.getDataNascimento() == null || usuarioRequest.getDataNascimento().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe nome e data de nascimento.");
        }

        String erroCpf = CpfUtils.mensagemDeErro(usuarioRequest.getCpf());
        if (erroCpf != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroCpf);
        }

        String erroSenha = SenhaUtils.mensagemDeErro(usuarioRequest.getSenha());
        if (erroSenha != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroSenha);
        }

        String cpf = OnlyNumber.apenasNumeros(usuarioRequest.getCpf());
        if (usuarioRepository.existsUsuarioByCpf(cpf)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma conta cadastrada com este CPF.");
        }

        Usuario usuarioBanco = new Usuario();

        usuarioBanco.setNome(usuarioRequest.getNome().trim());
        usuarioBanco.setCpf(cpf);
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento().trim());
        usuarioBanco.setSenha(usuarioRequest.getSenha());
        usuarioBanco.setDataCadastro(LocalDateTime.now());
        usuarioBanco.setStatus("A");


        //salvando no banco
        // a consulta previa melhora a resposta; o indice unico tambem cobre cadastros simultaneos com o mesmo cpf.
        try {
            usuarioRepository.save(usuarioBanco);
        } catch (DataIntegrityViolationException erro) {
            if (usuarioRepository.existsUsuarioByCpf(cpf)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Já existe uma conta cadastrada com este CPF.",
                        erro);
            }
            throw erro;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new UsuarioResponse(usuarioBanco.getId(), "Usuario cadastrado com sucesso."));

    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest){

        if (usuarioRequest == null
                || usuarioRequest.getNome() == null || usuarioRequest.getNome().isBlank()
                || usuarioRequest.getDataNascimento() == null || usuarioRequest.getDataNascimento().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe nome e data de nascimento.");
        }

        String erroCpf = CpfUtils.mensagemDeErro(usuarioRequest.getCpf());
        if (erroCpf != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroCpf);
        }

        String erroSenha = SenhaUtils.mensagemDeErro(usuarioRequest.getSenha());
        if (erroSenha != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, erroSenha);
        }

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        String cpf = OnlyNumber.apenasNumeros(usuarioRequest.getCpf());
        if (!cpf.equals(usuarioBanco.getCpf()) && usuarioRepository.existsUsuarioByCpf(cpf)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma conta cadastrada com este CPF.");
        }

        usuarioBanco.setNome(usuarioRequest.getNome().trim());
        usuarioBanco.setCpf(cpf);
        usuarioBanco.setDataNascimento(usuarioRequest.getDataNascimento().trim());
        usuarioBanco.setSenha(usuarioRequest.getSenha());

        usuarioBanco.setDataAtualizacao(LocalDateTime.now());

        usuarioRepository.save(usuarioBanco);
        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastro atualizado com sucesso."));


    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponse>
    AtualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusUsuario usuarioRequest){

        if (usuarioRequest == null || usuarioRequest.getStatus() == null || usuarioRequest.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o status do usuário.");
        }

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        usuarioBanco.setStatus(usuarioRequest.getStatus());

        usuarioRepository.save(usuarioBanco);
        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Cadastro atualizado com sucesso."));


    }


    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioResponse>
    AtualizarStatus(@PathVariable Long id){

        Usuario usuarioBanco = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        usuarioBanco.setStatus("D");
        usuarioRepository.save(usuarioBanco);
        return ResponseEntity.ok(new UsuarioResponse(usuarioBanco.getId(), "Usuário excluído com sucesso."));
    }


}
