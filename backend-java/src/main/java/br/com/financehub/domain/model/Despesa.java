package br.com.financehub.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Despesa extends Lancamento {
    public Despesa(long id, String descricao, BigDecimal valor, LocalDate data){
        super(id, descricao, valor, data);
    }
    public BigDecimal calcularImpacto(){
        return this.getValor().negate();
    }
    public String getResumo(){
        return "Despesa: " + super.getResumo();
    }

}