import java.util.Scanner;

public class SistemaAdega2 {

    private static Scanner scanner = new Scanner(System.in);

    private static final int MAX_PRODUTOS = 100;
    private static int totalCadastrados = 0;

    // Vetores paralelos
    private static String[] nomes = new String[MAX_PRODUTOS];
    private static String[] categorias = new String[MAX_PRODUTOS];
    private static double[] precosCompra = new double[MAX_PRODUTOS]; 
    private static double[] precosVenda = new double[MAX_PRODUTOS];  
    private static int[] quantidades = new int[MAX_PRODUTOS];
    private static int[] estoquesMinimos = new int[MAX_PRODUTOS];
    private static double[] aliquotas = new double[MAX_PRODUTOS]; // % imposto

    private static double caixaDoDia = 0.0;
    private static double totalImpostos = 0.0;

    public static void main(String[] args) {
        carregarDadosDeTeste();

        int opcao = 0;

        do {
            System.out.println("\n==========================================");
            System.out.println("          SISTEMA GERENCIAL ADEGA         ");
            System.out.println("==========================================");
            System.out.println(" CAIXA ATUAL: R$ " + String.format("%.2f", caixaDoDia));
            System.out.println(" IMPOSTOS ARRECADADOS: R$ " + String.format("%.2f", totalImpostos));
            System.out.println("------------------------------------------");
            System.out.println("1. Vender Bebida (PDV)");
            System.out.println("2. Cadastrar Novo Produto");
            System.out.println("3. Relatório de Estoque Completo");
            System.out.println("4. Alerta de Estoque Baixo (Reposição)");
            System.out.println("5. Excluir Produto");
            System.out.println("6. Filtrar por Categoria");
            System.out.println("7. Relatório Tributário");
            System.out.println("8. Sair");
            
            while (true) {
                System.out.print("Escolha uma opção: ");
                try {
                    opcao = Integer.parseInt(scanner.nextLine());
                    if (opcao >= 1 && opcao <= 8) break;
                    else System.out.println("[Erro] Opção inválida! Escolha entre 1 e 8.");
                } catch (NumberFormatException e) {
                    System.out.println("[Erro] Digite apenas o número da opção desejada.");
                }
            }

            switch (opcao) {
                case 1: realizarVenda(); break;
                case 2: cadastrarProduto(); break;
                case 3: exibirEstoqueGeral(); break;
                case 4: exibirEstoqueBaixo(); break;
                case 5: excluirProduto(); break;
                case 6: filtrarPorCategoria(); break;
                case 7: relatorioTributario(); break;
                case 8: System.out.println("\nFechando o caixa... Até mais!"); break;
            }
        } while (opcao != 8);
    }

    public static void carregarDadosDeTeste() {
        nomes[0] = "Heineken 600ml"; categorias[0] = "Cerveja"; precosCompra[0] = 8.50; precosVenda[0] = 14.50; quantidades[0] = 5; estoquesMinimos[0] = 20; aliquotas[0] = 18.0;
        nomes[1] = "Vinho Casillero"; categorias[1] = "Vinho"; precosCompra[1] = 40.00; precosVenda[1] = 65.00; quantidades[1] = 12; estoquesMinimos[1] = 10; aliquotas[1] = 15.0;
        nomes[2] = "Gin Tanqueray"; categorias[2] = "Destilado"; precosCompra[2] = 85.00; precosVenda[2] = 120.00; quantidades[2] = 30; estoquesMinimos[2] = 5; aliquotas[2] = 20.0;
        totalCadastrados = 3;
    }

    // --------------------------------------------------------
    // VENDA COM CÁLCULO DE IMPOSTO E BLOQUEIO DE ESTOQUE ZERO
    // --------------------------------------------------------
    public static void realizarVenda() {
        if (totalCadastrados == 0) {
            System.out.println("\n[Aviso] Estoque vazio!");
            return;
        }

        System.out.println("\n--- Selecione a Bebida ---");
        for (int i = 0; i < totalCadastrados; i++) {
            System.out.println("[" + i + "] " + nomes[i] + 
                               " | R$ " + String.format("%.2f", precosVenda[i]) + 
                               " | Estoque: " + quantidades[i] + 
                               " | Imposto: " + aliquotas[i] + "%");
        }

        int indice = -1;
        while (true) {
            System.out.print("\nDigite o NÚMERO do produto: ");
            try {
                indice = Integer.parseInt(scanner.nextLine());
                if (indice >= 0 && indice < totalCadastrados) {
                    if (quantidades[indice] == 0) {
                        System.out.println("[Erro] O produto '" + nomes[indice] + "' está sem estoque e não pode ser vendido.");
                        return;
                    }
                    break;
                } else System.out.println("[Erro] Número não encontrado na lista.");
            } catch (NumberFormatException e) {
                System.out.println("[Erro] Digite apenas o número identificador inteiro.");
            }
        }

        while (true) {
            System.out.print("Vender quantas unidades de " + nomes[indice] + "? ");
            try {
                int qtdVenda = Integer.parseInt(scanner.nextLine());

                if (qtdVenda < 0) {
                    System.out.println("[Erro] A quantidade deve ser maior que zero.");
                } else if (qtdVenda > quantidades[indice]) {
                    System.out.println("[Erro] Estoque insuficiente! Restam apenas " + quantidades[indice] + " unidades.");
                } else {
                    quantidades[indice] -= qtdVenda;
                    double valorTotalVenda = qtdVenda * precosVenda[indice];
                    double imposto = valorTotalVenda * (aliquotas[indice] / 100.0);

                    caixaDoDia += valorTotalVenda + imposto;
                    totalImpostos += imposto;

                    System.out.println("\n[Sucesso] Venda concluída!");
                    System.out.println(" - Valor sem imposto: R$ " + String.format("%.2f", valorTotalVenda));
                    System.out.println(" - Imposto aplicado (" + aliquotas[indice] + "%): R$ " + String.format("%.2f", imposto));
                    System.out.println(" - Total com imposto: R$ " + String.format("%.2f", valorTotalVenda + imposto));
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Erro] Digite apenas números inteiros.");
            }
        }
    }

    // --------------------------------------------------------
    // CADASTRO DE PRODUTO COM ALÍQUOTA
    // --------------------------------------------------------
    public static void cadastrarProduto() {
        if (totalCadastrados >= MAX_PRODUTOS) {
            System.out.println("\n[Erro] Estoque cheio!"); return;
        }

        System.out.println("\n--- Cadastro de Produto ---");

        String nome = "";
        while (nome.trim().isEmpty()) {
            System.out.print("Nome do produto: ");
            nome = scanner.nextLine();
            if (nome.trim().isEmpty()) System.out.println("[Erro] O nome não pode ser vazio.");
        }
        nomes[totalCadastrados] = nome;

        while (true) {
            System.out.print("Categoria [C] Cerveja | [V] Vinho | [D] Destilado: ");
            String entradaCat = scanner.nextLine().toUpperCase();
            if (entradaCat.equals("C")) { categorias[totalCadastrados] = "Cerveja"; break; }
            else if (entradaCat.equals("V")) { categorias[totalCadastrados] = "Vinho"; break; }
            else if (entradaCat.equals("D")) { categorias[totalCadastrados] = "Destilado"; break; }
            else System.out.println("[Erro] Letra inválida! Digite apenas C, V ou D.");
        }

        while (true) {
            System.out.print("Preço de Compra (R$): ");
            try {
                double precoC = Double.parseDouble(scanner.nextLine().replace(',', '.'));
                if (precoC >= 0) { precosCompra[totalCadastrados] = precoC; break; }
                else System.out.println("[Erro] O preço não pode ser negativo.");
            } catch (Exception e) {
                System.out.println("[Erro] Digite um valor numérico válido.");
            }
        }

                while (true) {
            System.out.print("Preço de Venda (R$): ");
            try {
                double precoV = Double.parseDouble(scanner.nextLine().replace(',', '.'));
                if (precoV >= precosCompra[totalCadastrados]) { 
                    precosVenda[totalCadastrados] = precoV; 
                    break; 
                } else {
                    System.out.println("[Erro] O preço de venda não pode ser menor que o preço de custo (R$ " + precosCompra[totalCadastrados] + ").");
                }
            } catch (Exception e) {
                System.out.println("[Erro] Digite um valor numérico válido.");
            }
        }

        while (true) {
            System.out.print("Quantidade atual em estoque: ");
            try {
                int qtd = Integer.parseInt(scanner.nextLine());
                if (qtd >= 0) { quantidades[totalCadastrados] = qtd; break; }
                else System.out.println("[Erro] A quantidade não pode ser negativa.");
            } catch (Exception e) {
                System.out.println("[Erro] Digite um número inteiro.");
            }
        }

        while (true) {
            System.out.print("Estoque mínimo (Alerta de reposição): ");
            try {
                int min = Integer.parseInt(scanner.nextLine());
                if (min >= 0) { estoquesMinimos[totalCadastrados] = min; break; }
                else System.out.println("[Erro] O estoque mínimo não pode ser negativo.");
            } catch (Exception e) {
                System.out.println("[Erro] Digite um número inteiro.");
            }
        }

        while (true) {
            System.out.print("Alíquota de imposto (%): ");
            try {
                double aliq = Double.parseDouble(scanner.nextLine().replace(',', '.'));
                if (aliq >= 0 && aliq <= 100) { aliquotas[totalCadastrados] = aliq; break; }
                else System.out.println("[Erro] A alíquota deve estar entre 0 e 100.");
            } catch (Exception e) {
                System.out.println("[Erro] Digite um valor numérico válido.");
            }
        }

        totalCadastrados++;
        System.out.println("\n[Sucesso] Produto adicionado com segurança!");
    }

    // --------------------------------------------------------
    // RELATÓRIO DE ESTOQUE
    // --------------------------------------------------------
    public static void exibirEstoqueGeral() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== ESTOQUE GERAL ===");
        for (int i = 0; i < totalCadastrados; i++) {
            double lucroUnitario = precosVenda[i] - precosCompra[i];
            System.out.println(nomes[i] + " (" + categorias[i] + ") | Qtd: " + quantidades[i]);
            System.out.println("  -> Compra: R$ " + String.format("%.2f", precosCompra[i]) + 
                               " | Venda: R$ " + String.format("%.2f", precosVenda[i]) + 
                               " | Lucro por un.: R$ " + String.format("%.2f", lucroUnitario) +
                               " | Imposto: " + aliquotas[i] + "%");
        }
    }

    public static void exibirEstoqueBaixo() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== ALERTA DE REPOSIÇÃO (ESTOQUE BAIXO) ===");
        boolean temEstoqueBaixo = false;

        for (int i = 0; i < totalCadastrados; i++) {
            if (quantidades[i] <= estoquesMinimos[i]) {
                temEstoqueBaixo = true;
                System.out.println("-> COMPRAR: " + nomes[i] + " | Atual: " + quantidades[i] + " | Mínimo: " + estoquesMinimos[i]);
            }
        }
        if (!temEstoqueBaixo) System.out.println("Todos os produtos estão com estoque em nível seguro.");
    }

    public static void excluirProduto() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== EXCLUSÃO DE PRODUTO ===");
        for (int i = 0; i < totalCadastrados; i++) {
            System.out.println("[" + i + "] " + nomes[i]);
        }

        int idx = -1;
        while (true) {
            System.out.print("\nDigite o NÚMERO do produto que deseja excluir (ou -1 para cancelar): ");
            try {
                idx = Integer.parseInt(scanner.nextLine());
                if (idx == -1) { System.out.println("Exclusão cancelada."); return; }
                if (idx >= 0 && idx < totalCadastrados) break;
                else System.out.println("[Erro] Código inválido. Escolha um número da lista.");
            } catch (NumberFormatException e) {
                System.out.println("[Erro] Digite apenas o NÚMERO.");
            }
        }

        System.out.print("Tem certeza que deseja APAGAR '" + nomes[idx] + "'? (S/N): ");
        String confirmacao = scanner.nextLine();
        
        if (confirmacao.equalsIgnoreCase("S")) {
            String nomeExcluido = nomes[idx];
            for (int j = idx; j < totalCadastrados - 1; j++) {
                nomes[j] = nomes[j + 1];
                categorias[j] = categorias[j + 1];
                precosCompra[j] = precosCompra[j + 1];
                precosVenda[j] = precosVenda[j + 1];
                quantidades[j] = quantidades[j + 1];
                estoquesMinimos[j] = estoquesMinimos[j + 1];
                aliquotas[j] = aliquotas[j + 1];
            }
            totalCadastrados--;
            System.out.println("\n[Sucesso] '" + nomeExcluido + "' foi removido permanentemente.");
        } else {
            System.out.println("\n[Aviso] Exclusão cancelada.");
        }
    }

    public static void filtrarPorCategoria() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== FILTRO POR CATEGORIA ===");
        System.out.print("Qual categoria você quer listar? (Cerveja, Vinho, Destilado): ");
        String busca = scanner.nextLine().trim();

        boolean encontrou = false;
        System.out.println("\n--- Resultados para '" + busca + "' ---");

        for (int i = 0; i < totalCadastrados; i++) {
            if (categorias[i].equalsIgnoreCase(busca)) {
                encontrou = true;
                System.out.println("[" + i + "] " + nomes[i] + " | Estoque: " + quantidades[i] + " | Preço: R$ " + String.format("%.2f", precosVenda[i]) + " | Imposto: " + aliquotas[i] + "%");
            }
        }
        if (!encontrou) System.out.println("Nenhum produto cadastrado nessa categoria.");
    }

    // --------------------------------------------------------
    // RELATÓRIO TRIBUTÁRIO
    // --------------------------------------------------------
    public static void relatorioTributario() {
        System.out.println("\n=== RELATÓRIO TRIBUTÁRIO ===");
        System.out.println("Total de impostos arrecadados: R$ " + String.format("%.2f", totalImpostos));
        System.out.println("Total em caixa (com impostos): R$ " + String.format("%.2f", caixaDoDia));
        System.out.println("Total líquido (sem impostos): R$ " + String.format("%.2f", caixaDoDia - totalImpostos));
    }
}
