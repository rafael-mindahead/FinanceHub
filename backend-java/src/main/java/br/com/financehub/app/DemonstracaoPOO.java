
package br.com.financehub.app;

import br.com.financehub.domain.model.Lancamento;
import br.com.financehub.domain.model.Receita;
import br.com.financehub.domain.model.Despesa;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DemonstracaoPOO {

    public static void main(String[] args) {

        Lancamento lancamento = new Receita(
            1,
            "Salario",
            new BigDecimal("2500.00"),
            LocalDate.now()
        );

        System.out.println(lancamento.getResumo());
        System.out.println(lancamento.calcularImpacto());

        lancamento = new Despesa(
            2,
            "Mercado",
            new BigDecimal("300.00"),
            LocalDate.now()
        );

        System.out.println(lancamento.getResumo());
        System.out.println(lancamento.calcularImpacto());
    }
}
