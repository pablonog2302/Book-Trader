
package com.example.booktrader.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//serve para dizer ao Jackson, biblioteca que o Spring usa para converter JSON em objetos Java:
//se o JSON recebido tiver campos que não existem nesta classe, ignore esses campos em vez de gerar um erro. basicamente isso
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnderecoResponse {

    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private Boolean erro;
    //o atributo erro, é responsavel por identificar o cep inexistente

    public EnderecoResponse() {
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String cidade) {
        this.localidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Boolean getErro() {
        return erro;
    }

    public void setErro(Boolean erro) {
        this.erro = erro;
    }
}
