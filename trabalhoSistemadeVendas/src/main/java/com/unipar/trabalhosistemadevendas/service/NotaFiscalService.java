package com.unipar.trabalhosistemadevendas.service;

import java.util.List;
import com.unipar.trabalhosistemadevendas.model.Produto;

public interface NotaFiscalService {
    void emitirNota(double valor, List<Produto> produtos);
}
