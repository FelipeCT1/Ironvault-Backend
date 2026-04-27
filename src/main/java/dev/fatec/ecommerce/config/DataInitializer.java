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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Override
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
    }

    private void inicializarCategorias() {
        Categoria suplemento = new Categoria();
        suplemento.setNome("SUPLEMENTO");
        suplemento.setDescricao("Suplementos alimentares e nutricionais");
        categoriaRepository.save(suplemento);

        Categoria acessorio = new Categoria();
        acessorio.setNome("ACESSORIO");
        acessorio.setDescricao("Acessórios para academia");
        categoriaRepository.save(acessorio);

        Categoria medicamento = new Categoria();
        medicamento.setNome("MEDICAMENTO_CONTROLADO");
        medicamento.setDescricao("Medicamentos com retenção de receita");
        categoriaRepository.save(medicamento);
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
        Categoria suplemento = categoriaRepository.findByNome("SUPLEMENTO").orElseThrow();
        Categoria acessorio = categoriaRepository.findByNome("ACESSORIO").orElseThrow();
        GrupoPrecificacao standard = grupoPrecificacaoRepository.findByNome("Standard").orElseThrow();
        GrupoPrecificacao premium = grupoPrecificacaoRepository.findByNome("Premium").orElseThrow();

        criarProduto("Whey Protein Concentrate", "Growth", "Proteína do soro do leite concentrada",
            suplemento, standard, new BigDecimal("69.15"), 50);

        criarProduto("Creatina Micronizada", "Max Titanium", "Creatina monohidratada micronizada",
            suplemento, standard, new BigDecimal("46.07"), 30);

        criarProduto("Pré-Treino Caffeine", "Insanity Labs", "Pré-treino com alta dose de cafeína",
            suplemento, premium, new BigDecimal("79.93"), 20);

        criarProduto("BCAA 2:1:1", "Growth", "Aminoácidos de cadeia ramificada",
            suplemento, standard, new BigDecimal("53.77"), 40);

        criarProduto("Glutamina", "Probiótica", "Glutamina livre para recuperação",
            suplemento, standard, new BigDecimal("38.38"), 35);

        criarProduto("Multivitamínico", "Universal", "Complexo de vitaminas e minerais",
            suplemento, standard, new BigDecimal("30.69"), 60);

        criarProduto("Ômega 3", "Madre Labs", "Óleo de peixe concentrado",
            suplemento, standard, new BigDecimal("42.23"), 25);

        criarProduto("Shaker 600ml", "IronVault", "Shaker com divisória",
            acessorio, standard, new BigDecimal("23.00"), 100);
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
}
