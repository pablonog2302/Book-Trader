package com.example.booktrader.config;

import com.example.booktrader.DTO.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


//ver com a ia o pq esta dando conflito esta linha com o swagger
//@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    // preserva mensagens de negocio e substitui detalhes internos nas respostas de erro 5xx.
    public ResponseEntity<ApiErrorResponse> tratarErroDeRegra(ResponseStatusException erro) {
        HttpStatusCode status = erro.getStatusCode();
        if (status.is5xxServerError()) {
            logger.error("Erro interno durante uma requisição.", erro);
            return resposta(status, "Não foi possível concluir a operação. Tente novamente mais tarde.");
        }

        String mensagem = erro.getReason();
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = mensagemPadrao(status);
        }
        logger.info("Requisição rejeitada ({}): {}", status.value(), mensagem);
        return resposta(status, mensagem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> tratarValidacao(MethodArgumentNotValidException erro) {
        logger.info("Requisição rejeitada por dados inválidos: {}", erro.getMessage());
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Revise os dados informados e tente novamente.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> tratarCorpoInvalido(HttpMessageNotReadableException erro) {
        logger.info("Requisição rejeitada por corpo inválido: {}", erro.getMessage());
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Os dados enviados estão incompletos ou em formato inválido. Revise o formulário.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> tratarArgumentoInvalido(IllegalArgumentException erro) {
        logger.info("Requisição rejeitada por argumento inválido: {}", erro.getMessage());
        return resposta(
                HttpStatus.BAD_REQUEST,
                "Os dados informados não são válidos. Revise os campos e tente novamente.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> tratarConflitoDeDados(DataIntegrityViolationException erro) {
        logger.warn("A operação foi rejeitada por uma restrição de integridade.", erro);
        return resposta(
                HttpStatus.CONFLICT,
                "Não foi possível salvar os dados porque eles entram em conflito com um registro existente.");
    }

    @ExceptionHandler(Exception.class)
    // registra os detalhes no servidor sem expo-los ao cliente da api.
    public ResponseEntity<ApiErrorResponse> tratarErroInesperado(Exception erro) {
        logger.error("Erro inesperado durante uma requisição.", erro);
        return resposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível concluir a operação. Tente novamente mais tarde.");
    }

    private ResponseEntity<ApiErrorResponse> resposta(HttpStatusCode status, String mensagem) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(mensagem));
    }

    private String mensagemPadrao(HttpStatusCode status) {
        return switch (status.value()) {
            case 400 -> "Revise os dados informados e tente novamente.";
            case 401 -> "Sua sessão expirou ou os dados de acesso são inválidos. Faça login novamente.";
            case 403 -> "Você não tem permissão para realizar esta ação.";
            case 404 -> "O item solicitado não foi encontrado.";
            case 409 -> "A operação não pode ser concluída porque os dados estão em conflito.";
            default -> "Não foi possível concluir a operação. Tente novamente mais tarde.";
        };
    }
}
