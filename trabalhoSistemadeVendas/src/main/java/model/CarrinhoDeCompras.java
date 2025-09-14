package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CarrinhoDeCompras {
    private final List<ItemCarrinho> itens = new ArrayList<>();

    public void adicionarProduto(Produto produto, int quantidade) {
        if (produto == null || quantidade <= 0) {
            return; 
        }

        itens.removeIf(item -> item.getProduto().getId() == produto.getId());
        itens.add(new ItemCarrinho(produto, quantidade));
    }

    public void removerProduto(int produtoId) {
        this.itens.removeIf(item -> item.getProduto().getId() == produtoId);
    }

    public double calcularTotal() {
        return this.itens.stream()
                .mapToDouble(ItemCarrinho::getSubtotal)
                .sum();
    }

    public List<ItemCarrinho> getItens() {
        
        return Collections.unmodifiableList(itens);
    }
    
}
