package model;

import com.unipar.trabalhosistemadevendas.model.Produto;
import com.unipar.trabalhosistemadevendas.model.CarrinhoDeCompras;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unipar.trabalhosistemadevendas.service.DescontoService;
import com.unipar.trabalhosistemadevendas.service.NotaFiscalService;
import com.unipar.trabalhosistemadevendas.service.PagamentoService;
import com.unipar.trabalhosistemadevendas.service.VendaService;

/**
 * @author Rafael, Kenji
 */
@ExtendWith(MockitoExtension.class)
class VendaServiceTest {
    @Mock
    private DescontoService descontoServiceMock;
    @Mock
    private PagamentoService pagamentoServiceMock;
    @Mock
    private NotaFiscalService notaFiscalServiceMock;

    @InjectMocks
    private VendaService vendaService;

    private CarrinhoDeCompras carrinho;
    private Produto produtoComEstoque;
    private Produto produtoSemEstoque;

    @BeforeEach
    void setUp() {
        carrinho = new CarrinhoDeCompras();
        produtoComEstoque = new Produto(1, "Teclado Mecânico", 350.0, 10);
        produtoSemEstoque = new Produto(2, "Monitor 4K", 2000.0, 0);
    }

    @Test
    void deveAplicarDescontoCorretamente() {
        carrinho.adicionarProduto(produtoComEstoque, 1);
        double valorComDesconto = 315.0;

        when(descontoServiceMock.aplicarDesconto(350.0)).thenReturn(valorComDesconto);
        when(pagamentoServiceMock.processarPagamento(valorComDesconto)).thenReturn(true);

        vendaService.realizarVenda(carrinho);

        verify(pagamentoServiceMock).processarPagamento(valorComDesconto);
    }

    @Test
    void deveManterValorOriginalComDescontoZero() {
        carrinho.adicionarProduto(produtoComEstoque, 2);

        when(descontoServiceMock.aplicarDesconto(700.0)).thenReturn(700.0);
        when(pagamentoServiceMock.processarPagamento(700.0)).thenReturn(true);

        vendaService.realizarVenda(carrinho);

        verify(pagamentoServiceMock).processarPagamento(700.0);
    }

    @Test
    void deveFalharVendaPorEstoqueInsuficiente() {
        carrinho.adicionarProduto(produtoComEstoque, 11); // Tenta comprar 11, mas só tem 10

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertFalse(resultado);

        verify(pagamentoServiceMock, never()).processarPagamento(anyDouble());
        verify(notaFiscalServiceMock, never()).emitirNota(anyDouble(), anyList());
    }

    @Test
    void deveConcluirVendaComPagamentoAprovado() {
        carrinho.adicionarProduto(produtoComEstoque, 5);
        int estoqueOriginal = produtoComEstoque.getEstoque();

        when(descontoServiceMock.aplicarDesconto(anyDouble())).thenReturn(1750.0);
        when(pagamentoServiceMock.processarPagamento(1750.0)).thenReturn(true);

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertTrue(resultado);
        assertEquals(estoqueOriginal - 5, produtoComEstoque.getEstoque(), "O estoque deve ser reduzido.");
        verify(notaFiscalServiceMock).emitirNota(eq(1750.0), anyList());
    }

    @Test
    void deveFalharVendaComPagamentoRecusado() {
        carrinho.adicionarProduto(produtoComEstoque, 5);
        int estoqueOriginal = produtoComEstoque.getEstoque();

        when(descontoServiceMock.aplicarDesconto(anyDouble())).thenReturn(1750.0);
        when(pagamentoServiceMock.processarPagamento(1750.0)).thenReturn(false);

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertFalse(resultado);
        assertEquals(estoqueOriginal, produtoComEstoque.getEstoque(), "O estoque não deve ser alterado.");
        verify(notaFiscalServiceMock, never()).emitirNota(anyDouble(), anyList());
    }

    @Test
    void deveTratarExcecaoDoServicoDePagamento() {
        carrinho.adicionarProduto(produtoComEstoque, 1);
        int estoqueOriginal = produtoComEstoque.getEstoque();

        when(descontoServiceMock.aplicarDesconto(anyDouble())).thenReturn(350.0);

        when(pagamentoServiceMock.processarPagamento(350.0))
            .thenThrow(new RuntimeException("Gateway de pagamento fora do ar"));

        boolean resultado = vendaService.realizarVenda(carrinho);

        assertFalse(resultado);
        assertEquals(estoqueOriginal, produtoComEstoque.getEstoque(), "O estoque não deve ser alterado em caso de exceção.");
        verify(notaFiscalServiceMock, never()).emitirNota(anyDouble(), anyList());
    }
}
