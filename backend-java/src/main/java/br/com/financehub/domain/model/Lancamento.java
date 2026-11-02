package br.com.financehub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Lancamento {
    
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;

    public Lancamento(Long id, String descricao, BigDecimal valor, LocalDate data){
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
    }
    public abstract BigDecimal calcularImpacto();

    public String getResumo(){
        return this.descricao + " - R$ " + this.valor;
    }

    public Long getId() {
        return this.id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public BigDecimal getValor() {
        return this.valor;
    }

    public LocalDate getData() {
        return this.data;
    }
    
}
