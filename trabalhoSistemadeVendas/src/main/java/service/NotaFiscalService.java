package service;

import java.util.List;
import model.Produto;



public interface NotaFiscalService {
    
    void emitirNota(double valor, List<Produto> produtos);
    
}
