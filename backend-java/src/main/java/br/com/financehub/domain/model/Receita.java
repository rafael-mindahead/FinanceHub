package br.com.financehub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Receita extends Lancamento {
    public Receita(long id, String descricao, BigDecimal valor, LocalDate data){
        super(id, descricao, valor, data);
    }
    public BigDecimal calcularImpacto(){
        return this.getValor();
    }
    public String getResumo(){
        return "Receita: " + super.getResumo();
    }
}