import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;

public class Mesa {
    private static Restaurante restaurante;
    private static Scanner scanner;
    private static int numeroMesa;
    private static List<Integer> comandasDaMesa;
    
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            restaurante = (Restaurante) registry.lookup("Restaurante");
            
            scanner = new Scanner(System.in);
            comandasDaMesa = new ArrayList<>();
            
            System.out.print("Digite o numero da mesa: ");
            numeroMesa = scanner.nextInt();
            scanner.nextLine();
            
            System.out.println("\n=== SISTEMA DA MESA " + numeroMesa + " ===");
            System.out.println("Conectado ao servidor do restaurante.");
            
            while (true) {
                exibirMenu();
                int opcao = lerOpcao();
                
                switch (opcao) {
                    case 1: consultarCardapio(); break;
                    case 2: criarNovaComanda(); break;
                    case 3: fazerPedido(); break;
                    case 4: consultarValorComanda(); break;
                    case 5: listarComandas(); break;
                    case 6: verificarPedidosProntos(); break;
                    case 7: fecharComanda(); break;
                    case 0: 
                        System.out.println("Sistema desconectado.");
                        return;
                    default: 
                        System.out.println("Opcao invalida!");
                }
                
                System.out.println("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
            
        } catch (Exception e) {
            System.err.println("Erro no sistema: " + e.getMessage());
        }
    }
    
    private static void exibirMenu() {
        System.out.println("\n" + repeatString("=", 50));
        System.out.println("        SISTEMA DA MESA " + numeroMesa);
        System.out.println(repeatString("=", 50));
        System.out.println("Comandas ativas: " + comandasDaMesa.size());
        
        if (!comandasDaMesa.isEmpty()) {
            System.out.print("Numeros: ");
            for (int i = 0; i < comandasDaMesa.size(); i++) {
                System.out.print("#" + comandasDaMesa.get(i));
                if (i < comandasDaMesa.size() - 1) System.out.print(", ");
            }
            System.out.println();
        }
        
        System.out.println("\n1. Consultar Cardapio");
        System.out.println("2. Criar Nova Comanda");
        System.out.println("3. Fazer Pedido");
        System.out.println("4. Consultar Valor da Comanda");
        System.out.println("5. Listar Comandas");
        System.out.println("6. Verificar Pedidos Prontos");
        System.out.println("7. Fechar Comanda");
        System.out.println("0. Sair");
        System.out.println(repeatString("=", 50));
        System.out.print("Opcao: ");
    }
    
    private static int lerOpcao() {
        try {
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }
    
    private static void consultarCardapio() {
        try {
            System.out.println("\nConsultando cardapio...");
            String[] cardapio = restaurante.consultarCardapio();
            
            System.out.println("\nCardapio (" + cardapio.length + " itens):");
            System.out.println(repeatString("-", 60));
            System.out.printf("%-6s %-30s %s%n", "CODIGO", "PRODUTO", "PRECO");
            System.out.println(repeatString("-", 60));
            
            for (String item : cardapio) {
                String[] partes = item.split(",");
                if (partes.length >= 3) {
                    System.out.printf("%-6s %-30s R$ %s%n", 
                        partes[0], partes[1], partes[2]);
                }
            }
            System.out.println(repeatString("-", 60));
            
        } catch (RemoteException e) {
            System.err.println("Erro ao consultar cardapio: " + e.getMessage());
        }
    }
    
    private static void criarNovaComanda() {
        try {
            System.out.println("\nCriando nova comanda para mesa " + numeroMesa);
            System.out.print("Nome do responsavel: ");
            String nome = scanner.nextLine();
            
            int novaComanda = restaurante.novaComanda(nome, numeroMesa);
            comandasDaMesa.add(novaComanda);
            
            System.out.println("Comanda #" + novaComanda + " criada com sucesso.");
            System.out.println("Responsavel: " + nome);
            System.out.println("Mesa: " + numeroMesa);
            
        } catch (RemoteException e) {
            System.err.println("Erro ao criar comanda: " + e.getMessage());
        }
    }
    
    private static void fazerPedido() {
        if (comandasDaMesa.isEmpty()) {
            System.out.println("Nenhuma comanda ativa. Crie uma comanda primeiro.");
            return;
        }
        
        try {
            int comandaSelecionada = selecionarComanda("fazer pedido");
            if (comandaSelecionada == -1) return;
            
            System.out.println("\nFazendo pedido para comanda #" + comandaSelecionada);
            System.out.println("Digite os codigos dos itens separados por virgula:");
            System.out.print("Codigos: ");
            
            String entrada = scanner.nextLine().trim();
            if (entrada.isEmpty()) {
                System.out.println("Nenhum item informado.");
                return;
            }
            
            String[] codigos = entrada.split(",");
            List<String> itensPedido = new ArrayList<>();
            String[] cardapio = restaurante.consultarCardapio();
            
            for (String codigo : codigos) {
                codigo = codigo.trim();
                for (String item : cardapio) {
                    if (item.startsWith(codigo + ",")) {
                        itensPedido.add(item);
                        break;
                    }
                }
            }
            
            if (itensPedido.isEmpty()) {
                System.out.println("Nenhum item valido encontrado.");
                return;
            }
            
            String[] pedidoArray = itensPedido.toArray(new String[0]);
            String resultado = restaurante.fazerPedido(comandaSelecionada, pedidoArray);
            System.out.println(resultado);
            
        } catch (RemoteException e) {
            System.err.println("Erro ao fazer pedido: " + e.getMessage());
        }
    }
    
    private static void consultarValorComanda() {
        if (comandasDaMesa.isEmpty()) {
            System.out.println("Nenhuma comanda ativa.");
            return;
        }
        
        try {
            int comandaSelecionada = selecionarComanda("consultar valor");
            if (comandaSelecionada == -1) return;
            
            float valor = restaurante.valorComanda(comandaSelecionada);
            System.out.println("\nValor da comanda #" + comandaSelecionada + ": R$ " + 
                             String.format("%.2f", valor));
            
        } catch (RemoteException e) {
            System.err.println("Erro ao consultar valor: " + e.getMessage());
        }
    }
    
    private static void listarComandas() {
        if (comandasDaMesa.isEmpty()) {
            System.out.println("Nenhuma comanda ativa nesta mesa.");
            return;
        }
        
        System.out.println("\nComandas da mesa " + numeroMesa + ":");
        for (int comanda : comandasDaMesa) {
            try {
                float valor = restaurante.valorComanda(comanda);
                System.out.println("Comanda #" + comanda + " - Valor: R$ " + 
                                 String.format("%.2f", valor));
            } catch (RemoteException e) {
                System.out.println("Comanda #" + comanda + " - Erro ao consultar valor");
            }
        }
    }
    
    private static void verificarPedidosProntos() {
        if (comandasDaMesa.isEmpty()) {
            System.out.println("Nenhuma comanda ativa.");
            return;
        }
        
        try {
            System.out.println("\nVerificando pedidos prontos...");
            boolean algumPronto = false;
            
            for (int comanda : comandasDaMesa) {
                try {
                    boolean pronto = restaurante.pedidoPronto(comanda);
                    
                    if (pronto) {
                        algumPronto = true;
                        System.out.println("Comanda #" + comanda + " - PEDIDO PRONTO!");
                        
                        System.out.print("Deseja buscar o pedido agora? (s/n): ");
                        String resposta = scanner.nextLine().toLowerCase();
                        
                        if (resposta.equals("s") || resposta.equals("sim")) {
                            String[] pedidoPronto = restaurante.buscarPedidoPronto(comanda);
                            
                            System.out.println("Pedido da comanda #" + comanda + " entregue!");
                            System.out.println("Itens:");
                            for (String item : pedidoPronto) {
                                String[] partes = item.split(",");
                                if (partes.length >= 2) {
                                    System.out.println("  - " + partes[1]);
                                }
                            }
                        }
                    } else {
                        try {
                            int tempoRestante = restaurante.consultarTempoPreparo(comanda);
                            System.out.println("Comanda #" + comanda + " - Em preparo (" + 
                                             tempoRestante + "s restantes)");
                        } catch (Exception e) {
                            System.out.println("Comanda #" + comanda + " - Sem pedido em preparo");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Comanda #" + comanda + " - Sem pedido em preparo");
                }
            }
            
            if (!algumPronto) {
                System.out.println("Nenhum pedido pronto no momento.");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao verificar pedidos: " + e.getMessage());
        }
    }
    
    private static void fecharComanda() {
        if (comandasDaMesa.isEmpty()) {
            System.out.println("Nenhuma comanda ativa.");
            return;
        }
        
        try {
            int comandaSelecionada = selecionarComanda("fechar");
            if (comandaSelecionada == -1) return;
            
            System.out.println("\nFechando comanda #" + comandaSelecionada);
            
            float valor = restaurante.valorComanda(comandaSelecionada);
            System.out.println("Valor total: R$ " + String.format("%.2f", valor));
            
            System.out.print("Confirma o pagamento? (s/n): ");
            String confirmacao = scanner.nextLine().toLowerCase();
            
            if (confirmacao.equals("s") || confirmacao.equals("sim")) {
                boolean fechada = restaurante.fecharComanda(comandaSelecionada);
                
                if (fechada) {
                    System.out.println("Comanda fechada com sucesso!");
                    System.out.println("Pagamento: R$ " + String.format("%.2f", valor));
                    comandasDaMesa.remove(Integer.valueOf(comandaSelecionada));
                } else {
                    System.out.println("Erro ao fechar comanda!");
                }
            } else {
                System.out.println("Pagamento cancelado.");
            }
            
        } catch (RemoteException e) {
            System.err.println("Erro ao fechar comanda: " + e.getMessage());
        }
    }
    
    private static int selecionarComanda(String acao) {
        if (comandasDaMesa.size() == 1) {
            return comandasDaMesa.get(0);
        }
        
        System.out.println("\nComandas disponiveis para " + acao + ":");
        for (int i = 0; i < comandasDaMesa.size(); i++) {
            System.out.println((i + 1) + ". Comanda #" + comandasDaMesa.get(i));
        }
        
        System.out.print("Selecione a comanda (1-" + comandasDaMesa.size() + "): ");
        try {
            int selecao = scanner.nextInt();
            scanner.nextLine();
            
            if (selecao >= 1 && selecao <= comandasDaMesa.size()) {
                return comandasDaMesa.get(selecao - 1);
            } else {
                System.out.println("Selecao invalida!");
                return -1;
            }
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Entrada invalida!");
            return -1;
        }
    }
    
    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
