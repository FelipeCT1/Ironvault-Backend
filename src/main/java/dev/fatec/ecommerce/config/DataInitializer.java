package dev.fatec.ecommerce.config;

import dev.fatec.ecommerce.cliente.model.*;
import dev.fatec.ecommerce.cliente.repository.ClienteRepository;
import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import dev.fatec.ecommerce.produto.model.Categoria;
import dev.fatec.ecommerce.produto.model.GrupoPrecificacao;
import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.repository.CategoriaRepository;
import dev.fatec.ecommerce.produto.repository.GrupoPrecificacaoRepository;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import dev.fatec.ecommerce.venda.model.ItemVenda;
import dev.fatec.ecommerce.venda.model.StatusVenda;
import dev.fatec.ecommerce.venda.model.Venda;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final GrupoPrecificacaoRepository grupoPrecificacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final CupomRepository cupomRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final VendaRepository vendaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Random random = new Random(42);

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoriaRepository.count() == 0) {
            inicializarCategorias();
        }
        if (grupoPrecificacaoRepository.count() == 0) {
            inicializarGruposPrecificacao();
        }
        if (produtoRepository.count() == 0) {
            inicializarProdutos();
        }
        if (cupomRepository.count() == 0) {
            inicializarCupons();
        }
        if (clienteRepository.count() == 0) {
            inicializarClientes();
        }
        if (vendaRepository.count() == 0) {
            inicializarVendas();
        }
    }

    private void inicializarCategorias() {
        Categoria proteinas = new Categoria();
        proteinas.setNome("PROTEINAS");
        proteinas.setDescricao("Proteínas e suplementos proteicos");
        categoriaRepository.save(proteinas);

        Categoria aminoacidos = new Categoria();
        aminoacidos.setNome("AMINOACIDOS");
        aminoacidos.setDescricao("Aminoácidos e desempenho muscular");
        categoriaRepository.save(aminoacidos);

        Categoria preTreino = new Categoria();
        preTreino.setNome("PRE_TREINO");
        preTreino.setDescricao("Pré-treinos e energéticos");
        categoriaRepository.save(preTreino);

        Categoria vitaminas = new Categoria();
        vitaminas.setNome("VITAMINAS");
        vitaminas.setDescricao("Vitaminas, minerais e saudabilidade");
        categoriaRepository.save(vitaminas);

        Categoria acessorios = new Categoria();
        acessorios.setNome("ACESSORIOS");
        acessorios.setDescricao("Acessórios para academia");
        categoriaRepository.save(acessorios);
    }

    private void inicializarGruposPrecificacao() {
        GrupoPrecificacao standard = new GrupoPrecificacao();
        standard.setNome("Standard");
        standard.setMargemLucro(new BigDecimal("30.00"));
        standard.setMargemMinima(new BigDecimal("20.00"));
        grupoPrecificacaoRepository.save(standard);

        GrupoPrecificacao premium = new GrupoPrecificacao();
        premium.setNome("Premium");
        premium.setMargemLucro(new BigDecimal("50.00"));
        premium.setMargemMinima(new BigDecimal("35.00"));
        grupoPrecificacaoRepository.save(premium);

        GrupoPrecificacao promocional = new GrupoPrecificacao();
        promocional.setNome("Promocional");
        promocional.setMargemLucro(new BigDecimal("15.00"));
        promocional.setMargemMinima(new BigDecimal("10.00"));
        grupoPrecificacaoRepository.save(promocional);
    }

    private void inicializarProdutos() {
        Categoria proteinas = categoriaRepository.findByNome("PROTEINAS").orElseThrow();
        Categoria aminoacidos = categoriaRepository.findByNome("AMINOACIDOS").orElseThrow();
        Categoria preTreino = categoriaRepository.findByNome("PRE_TREINO").orElseThrow();
        Categoria vitaminas = categoriaRepository.findByNome("VITAMINAS").orElseThrow();
        Categoria acessorios = categoriaRepository.findByNome("ACESSORIOS").orElseThrow();
        GrupoPrecificacao standard = grupoPrecificacaoRepository.findByNome("Standard").orElseThrow();
        GrupoPrecificacao premium = grupoPrecificacaoRepository.findByNome("Premium").orElseThrow();

        criarProduto("Whey Protein Concentrate", "Growth", "Proteína do soro do leite concentrada",
            proteinas, standard, new BigDecimal("69.15"), 50);

        criarProduto("Creatina Micronizada", "Max Titanium", "Creatina monohidratada micronizada",
            aminoacidos, standard, new BigDecimal("46.07"), 30);

        criarProduto("Pré-Treino Caffeine", "Insanity Labs", "Pré-treino com alta dose de cafeína",
            preTreino, premium, new BigDecimal("79.93"), 20);

        criarProduto("BCAA 2:1:1", "Growth", "Aminoácidos de cadeia ramificada",
            aminoacidos, standard, new BigDecimal("53.77"), 40);

        criarProduto("Glutamina", "Probiótica", "Glutamina livre para recuperação",
            aminoacidos, standard, new BigDecimal("38.38"), 35);

        criarProduto("Multivitamínico", "Universal", "Complexo de vitaminas e minerais",
            vitaminas, standard, new BigDecimal("30.69"), 60);

        criarProduto("Ômega 3", "Madre Labs", "Óleo de peixe concentrado",
            vitaminas, standard, new BigDecimal("42.23"), 25);

        criarProduto("Shaker 600ml", "IronVault", "Shaker com divisória",
            acessorios, standard, new BigDecimal("23.00"), 100);
    }

    private void criarProduto(String nome, String marca, String descricao,
            Categoria categoria, GrupoPrecificacao grupo, BigDecimal custo, int estoque) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setMarca(marca);
        produto.setDescricao(descricao);
        produto.setCategoria(categoria);
        produto.setGrupoPrecificacao(grupo);
        produto.setValorCusto(custo);
        produto.setEstoque(estoque);
        produto.setIngredientesAtivos("Ingredientes variados");
        produto.setFormaFarmaceutica("CAPSULA");
        produto.setFabricante(marca);
        produto.setCodigoBarras("789" + System.currentTimeMillis() % 10000000000L);
        produto.setDataFabricacao(LocalDate.now().minusMonths(6));
        produto.setDataValidade(LocalDate.now().plusYears(2));
        produto.setPeso(new BigDecimal("0.3"));
        produto.setAltura(new BigDecimal("10"));
        produto.setLargura(new BigDecimal("8"));
        produto.setProfundidade(new BigDecimal("8"));
        produto.setImagemUrl("https://via.placeholder.com/300x300?text=" + nome.replace(" ", "+"));
        produtoRepository.save(produto);
    }

    private void inicializarCupons() {
        Cupom promocional = new Cupom();
        promocional.setCodigo("PRIMEIRA10");
        promocional.setTipo(TipoCupom.PROMOCIONAL);
        promocional.setValor(new BigDecimal("10.00"));
        promocional.setValidoAte(LocalDate.of(2026, 12, 31));
        cupomRepository.save(promocional);

        Cupom promocional2 = new Cupom();
        promocional2.setCodigo("SUPER20");
        promocional2.setTipo(TipoCupom.PROMOCIONAL);
        promocional2.setValor(new BigDecimal("20.00"));
        promocional2.setValidoAte(LocalDate.of(2026, 6, 30));
        cupomRepository.save(promocional2);

        Cupom troca1 = new Cupom();
        troca1.setCodigo("TROCA25");
        troca1.setTipo(TipoCupom.TROCA);
        troca1.setValor(new BigDecimal("25.00"));
        troca1.setValidoAte(LocalDate.of(2026, 12, 31));
        troca1.setClienteId(1L);
        cupomRepository.save(troca1);

        Cupom troca2 = new Cupom();
        troca2.setCodigo("TROCA50");
        troca2.setTipo(TipoCupom.TROCA);
        troca2.setValor(new BigDecimal("50.00"));
        troca2.setValidoAte(LocalDate.of(2026, 12, 31));
        troca2.setClienteId(1L);
        cupomRepository.save(troca2);

        Cupom troca3 = new Cupom();
        troca3.setCodigo("TROCA15");
        troca3.setTipo(TipoCupom.TROCA);
        troca3.setValor(new BigDecimal("15.00"));
        troca3.setValidoAte(LocalDate.of(2026, 12, 31));
        troca3.setClienteId(1L);
        cupomRepository.save(troca3);
    }

    private void inicializarClientes() {
        // Admin
        Cliente admin = new Cliente();
        admin.setNome("Admin IronVault");
        admin.setGenero("MASCULINO");
        admin.setDataNascimento(LocalDate.of(1990, 1, 1));
        admin.setCpf("229.873.678-21");
        admin.setEmail("admin@ironvault.com");
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setPapel(Papel.ADMIN);
        admin.setAtivo(true);
        admin.setTipoTelefone("CELULAR");
        admin.setDdd("11");
        admin.setNumeroTelefone("99999-0000");

        Endereco endAdmin = new Endereco();
        endAdmin.setApelido("Matriz");
        endAdmin.setTipoResidencia("COMERCIAL");
        endAdmin.setTipoLogradouro("AVENIDA");
        endAdmin.setLogradouro("Paulista");
        endAdmin.setNumero("1000");
        endAdmin.setBairro("Bela Vista");
        endAdmin.setCep("01310-100");
        endAdmin.setCidade("São Paulo");
        endAdmin.setEstado("SP");
        endAdmin.setPais("Brasil");
        endAdmin.setEhEntrega(true);
        endAdmin.setEhCobranca(true);
        admin.getEnderecos().add(endAdmin);

        CartaoCredito cartaoAdmin = new CartaoCredito();
        cartaoAdmin.setNumero("4532 **** **** 0001");
        cartaoAdmin.setNomeImpresso("ADMIN IRONVAULT");
        cartaoAdmin.setBandeira("VISA");
        cartaoAdmin.setCodigoSeguranca("123");
        cartaoAdmin.setPreferencial(true);
        admin.getCartoes().add(cartaoAdmin);

        clienteRepository.save(admin);

        // Cliente de teste
        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setGenero("MASCULINO");
        cliente.setDataNascimento(LocalDate.of(1990, 5, 15));
        cliente.setCpf("697.507.430-59");
        cliente.setEmail("joao.silva@email.com");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setPapel(Papel.CLIENTE);
        cliente.setAtivo(true);
        cliente.setTipoTelefone("CELULAR");
        cliente.setDdd("11");
        cliente.setNumeroTelefone("99999-8888");

        Endereco end1 = new Endereco();
        end1.setApelido("Casa");
        end1.setTipoResidencia("CASA");
        end1.setTipoLogradouro("RUA");
        end1.setLogradouro("das Flores");
        end1.setNumero("123");
        end1.setBairro("Jardim Primavera");
        end1.setCep("01310-100");
        end1.setCidade("São Paulo");
        end1.setEstado("SP");
        end1.setPais("Brasil");
        end1.setEhEntrega(true);
        end1.setEhCobranca(true);
        cliente.getEnderecos().add(end1);

        Endereco end2 = new Endereco();
        end2.setApelido("Trabalho");
        end2.setTipoResidencia("COMERCIAL");
        end2.setTipoLogradouro("AVENIDA");
        end2.setLogradouro("Paulista");
        end2.setNumero("1000");
        end2.setBairro("Bela Vista");
        end2.setCep("01310-100");
        end2.setCidade("São Paulo");
        end2.setEstado("SP");
        end2.setPais("Brasil");
        end2.setEhEntrega(true);
        end2.setEhCobranca(false);
        cliente.getEnderecos().add(end2);

        CartaoCredito cartao1 = new CartaoCredito();
        cartao1.setNumero("4532 **** **** 1234");
        cartao1.setNomeImpresso("JOAO SILVA");
        cartao1.setBandeira("VISA");
        cartao1.setCodigoSeguranca("456");
        cartao1.setPreferencial(true);
        cliente.getCartoes().add(cartao1);

        CartaoCredito cartao2 = new CartaoCredito();
        cartao2.setNumero("5500 **** **** 5678");
        cartao2.setNomeImpresso("JOAO SILVA");
        cartao2.setBandeira("MASTERCARD");
        cartao2.setCodigoSeguranca("789");
        cartao2.setPreferencial(false);
        cliente.getCartoes().add(cartao2);

        clienteRepository.save(cliente);
    }

    private void inicializarVendas() {
        List<Produto> produtos = produtoRepository.findAll();
        List<Long> clientes = List.of(1L, 2L);

        int contador = 0;
        int ano = 2025;
        int mes = 1;

        while (ano < 2027) {
            int diasNoMes = java.time.YearMonth.of(ano, mes).lengthOfMonth();
            int vendasNoMes = 4 + random.nextInt(5);

            for (int i = 0; i < vendasNoMes; i++) {
                int dia = 1 + random.nextInt(diasNoMes);
                LocalDateTime data = LocalDateTime.of(ano, mes, dia,
                        random.nextInt(8) + 8, random.nextInt(60));

                Long clienteId = clientes.get(random.nextInt(clientes.size()));
                Venda venda = new Venda();
                venda.setClienteId(clienteId);
                venda.setClienteNome(clienteId == 1L ? "Admin IronVault" : "João Silva");
                venda.setCodigoPedido("PED-" + String.format("%04d", ++contador));

                int qtdItens = 1 + random.nextInt(3);
                BigDecimal subtotal = BigDecimal.ZERO;
                for (int j = 0; j < qtdItens; j++) {
                    Produto p = produtos.get(random.nextInt(produtos.size()));
                    int qtd = 1 + random.nextInt(3);

                    ItemVenda item = new ItemVenda();
                    item.setVenda(venda);
                    item.setProdutoId(p.getId());
                    item.setProdutoNome(p.getNome());
                    item.setQuantidade(qtd);
                    item.setPrecoUnitario(p.getValorVenda());
                    item.setSubtotal(p.getValorVenda().multiply(BigDecimal.valueOf(qtd)));
                    venda.getItens().add(item);
                    subtotal = subtotal.add(item.getSubtotal());
                }

                venda.setSubtotal(subtotal);
                venda.setValorFrete(BigDecimal.ZERO);
                venda.setDescontoPromocional(BigDecimal.ZERO);
                venda.setDescontoTroca(BigDecimal.ZERO);

                StatusVenda[] statuses = {StatusVenda.APROVADA, StatusVenda.ENTREGUE, StatusVenda.EM_TRANSITO, StatusVenda.REPROVADA};
                StatusVenda status = statuses[random.nextInt(statuses.length)];
                if (status == StatusVenda.REPROVADA && random.nextInt(10) > 2) {
                    status = StatusVenda.ENTREGUE;
                }
                venda.setStatus(status);
                venda.setTotal(subtotal);

                vendaRepository.save(venda);
                entityManager.flush();

                entityManager.createNativeQuery(
                        "UPDATE venda SET data_criacao = :data WHERE id = :id")
                        .setParameter("data", data)
                        .setParameter("id", venda.getId())
                        .executeUpdate();
            }

            mes++;
            if (mes > 12) {
                mes = 1;
                ano++;
            }
        }
    }
}
