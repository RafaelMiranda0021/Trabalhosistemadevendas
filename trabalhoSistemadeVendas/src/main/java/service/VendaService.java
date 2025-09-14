package service;

import java.util.List;
import java.util.stream.Collectors;
import model.CarrinhoDeCompras;
import model.ItemCarrinho;
import model.Produto;

public class VendaService {
    private final DescontoService descontoService;
    private final PagamentoService pagamentoService;
    private final NotaFiscalService notaFiscalService;

    public VendaService(DescontoService descontoService, PagamentoService pagamentoService, NotaFiscalService notaFiscalService) {
        this.descontoService = descontoService;
        this.pagamentoService = pagamentoService;
        this.notaFiscalService = notaFiscalService;
    }

    public boolean realizarVenda(CarrinhoDeCompras carrinho) {
        
        for (ItemCarrinho item : carrinho.getItens()) {
            if (item.getProduto().getEstoque() < item.getQuantidade()) {
                System.err.println("Estoque insuficiente para o produto: " + item.getProduto().getNome());
                return false;
            }
        }

        
        double total = carrinho.calcularTotal();
        
        
        double valorComDesconto = descontoService.aplicarDesconto(total);

        try {
            
            boolean pagamentoAprovado = pagamentoService.processarPagamento(valorComDesconto);

            
            if (pagamentoAprovado) {
                
                for (ItemCarrinho item : carrinho.getItens()) {
                    item.getProduto().reduzirEstoque(item.getQuantidade());
                }

                
                List<Produto> produtosVendidos = carrinho.getItens().stream()
                        .map(ItemCarrinho::getProduto)
                        .collect(Collectors.toList());
                notaFiscalService.emitirNota(valorComDesconto, produtosVendidos);

                return true; 
            } else {
                return false; 
            }
        } catch (RuntimeException e) {
            
            System.err.println("Ocorreu um erro ao processar o pagamento: " + e.getMessage());
            return false;
        }
    }
    
}
