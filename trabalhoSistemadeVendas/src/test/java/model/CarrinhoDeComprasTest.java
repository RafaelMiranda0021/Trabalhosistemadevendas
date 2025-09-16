package model;

import com.unipar.trabalhosistemadevendas.model.Produto;
import com.unipar.trabalhosistemadevendas.model.CarrinhoDeCompras;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Rafael, Kenji
 */
public class CarrinhoDeComprasTest {
    private CarrinhoDeCompras carrinho;
    private Produto p1;
    private Produto p2;

    @BeforeEach
    void setUp() {
        carrinho = new CarrinhoDeCompras();
        p1 = new Produto(1, "Notebook Gamer", 5000.0, 10);
        p2 = new Produto(2, "Mouse Sem Fio", 250.0, 30);
    }

    @Test
    void deveAdicionarProdutosAoCarrinho() {
        carrinho.adicionarProduto(p1, 1);
        carrinho.adicionarProduto(p2, 2);

        assertEquals(2, carrinho.getItens().size(), "O carrinho deve ter 2 itens.");
        assertEquals(p1, carrinho.getItens().get(0).getProduto());
        assertEquals(p2, carrinho.getItens().get(1).getProduto());
        assertEquals(1, carrinho.getItens().get(0).getQuantidade());
        assertEquals(2, carrinho.getItens().get(1).getQuantidade());
    }

    @Test
    void deveRemoverProdutoDoCarrinho() {
        carrinho.adicionarProduto(p1, 1);
        carrinho.adicionarProduto(p2, 2);

        carrinho.removerProduto(p1.getId());

        assertEquals(1, carrinho.getItens().size(), "O carrinho deve ter apenas 1 item após a remoção.");
        assertEquals(p2, carrinho.getItens().get(0).getProduto());
    }

    @Test
    void deveCalcularOValorTotalCorretamente() {
        carrinho.adicionarProduto(p1, 1);
        carrinho.adicionarProduto(p2, 2);

        double totalEsperado = 5500.0;
        assertEquals(totalEsperado, carrinho.calcularTotal());
    }

    @Test
    void deveRetornarTotalZeroParaCarrinhoVazio() {
        assertEquals(0.0, carrinho.calcularTotal());
    }
}
