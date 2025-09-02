import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;

public class ClienteRMI {
    private static Restaurante restaurante;
    private static Cozinha cozinha;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        try {
            // Conecta ao registry RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // Obtém as referências dos serviços remotos
            restaurante = (Restaurante) registry.lookup("Restaurante");
            cozinha = (Cozinha) registry.lookup("Cozinha");
            
            scanner = new Scanner(System.in);
            
            System.out.println("=== Cliente RMI - Sistema Restaurante ===");
            System.out.println("Conectado aos serviços remotos com sucesso!");
            
            // Menu principal
            while (true) {
                exibirMenu();
                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpa o buffer
                
                switch (opcao) {
                    case 1:
                        testarRestaurante();
                        break;
                    case 2:
                        testarCozinha();
                        break;
                    case 3:
                        testarFluxoCompleto();
                        break;
                    case 0:
                        System.out.println("Encerrando cliente...");
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro no cliente RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void exibirMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Testar Restaurante");
        System.out.println("2. Testar Cozinha");
        System.out.println("3. Testar Fluxo Completo");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }
    
    private static void testarRestaurante() {
        try {
            System.out.println("\n=== TESTANDO RESTAURANTE ===");
            
            // Consultar cardápio
            System.out.println("1. Consultando cardápio...");
            String[] cardapio = restaurante.consultarCardapio();
            System.out.println("Cardápio disponível:");
            for (String item : cardapio) {
                System.out.println("   " + item);
            }
            
            // Criar nova comanda
            System.out.println("\n2. Criando nova comanda...");
            int comanda = restaurante.novaComanda("João Silva", 5);
            System.out.println("Comanda criada: " + comanda);
            
            // Fazer pedido
            System.out.println("\n3. Fazendo pedido...");
            String[] pedido = {"1,Pizza Margherita,25.50", "5,Refrigerante,5.50"};
            String resultado = restaurante.fazerPedido(comanda, pedido);
            System.out.println("Resultado do pedido:\n" + resultado);
            
            // Consultar valor
            System.out.println("4. Consultando valor da comanda...");
            float valor = restaurante.valorComanda(comanda);
            System.out.println("Valor total: R$ " + valor);
            
            // Fechar comanda
            System.out.println("\n5. Fechando comanda...");
            boolean fechada = restaurante.fecharComanda(comanda);
            System.out.println("Comanda fechada: " + fechada);
            
        } catch (RemoteException e) {
            System.err.println("Erro ao testar restaurante: " + e.getMessage());
        }
    }
    
    private static void testarCozinha() {
        try {
            System.out.println("\n=== TESTANDO COZINHA ===");
            
            // Criar novo preparo
            System.out.println("1. Criando novo preparo...");
            String[] pedido = {"1,Pizza Margherita,25.50", "2,Hambúrguer Clássico,18.90"};
            int preparo = cozinha.novoPreparo(123, pedido);
            System.out.println("Preparo criado: " + preparo);
            
            // Consultar tempo de preparo
            System.out.println("\n2. Consultando tempo de preparo...");
            int tempo = cozinha.tempoPreparo(preparo);
            System.out.println("Tempo restante: " + tempo + " segundos");
            
            // Aguardar um pouco
            System.out.println("\n3. Aguardando 3 segundos...");
            Thread.sleep(3000);
            
            // Consultar tempo novamente
            tempo = cozinha.tempoPreparo(preparo);
            System.out.println("Tempo restante após espera: " + tempo + " segundos");
            
            // Tentar pegar o preparo
            System.out.println("\n4. Tentando pegar o preparo...");
            try {
                String[] preparoPronto = cozinha.pegarPreparo(preparo);
                System.out.println("Preparo entregue com sucesso!");
                System.out.println("Itens:");
                for (String item : preparoPronto) {
                    System.out.println("   " + item);
                }
            } catch (RemoteException e) {
                System.out.println("Preparo ainda não está pronto: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao testar cozinha: " + e.getMessage());
        }
    }
    
    private static void testarFluxoCompleto() {
        try {
            System.out.println("\n=== TESTANDO FLUXO COMPLETO ===");
            
            // 1. Cliente chega e cria comanda
            System.out.println("1. Cliente 'Maria' chega na mesa 3...");
            int comanda = restaurante.novaComanda("Maria", 3);
            System.out.println("Comanda criada: " + comanda);
            
            // 2. Cliente consulta cardápio
            System.out.println("\n2. Consultando cardápio...");
            String[] cardapio = restaurante.consultarCardapio();
            System.out.println("Primeiros itens do cardápio:");
            for (int i = 0; i < Math.min(3, cardapio.length); i++) {
                System.out.println("   " + cardapio[i]);
            }
            
            // 3. Cliente faz pedido
            System.out.println("\n3. Cliente faz pedido...");
            String[] pedido = {"2,Hambúrguer Clássico,18.90", "6,Suco Natural,8.00"};
            String resultadoPedido = restaurante.fazerPedido(comanda, pedido);
            System.out.println("Pedido realizado:\n" + resultadoPedido);
            
            // 4. Pedido vai para a cozinha
            System.out.println("4. Enviando pedido para a cozinha...");
            int preparo = cozinha.novoPreparo(comanda, pedido);
            System.out.println("Preparo iniciado: " + preparo);
            
            // 5. Monitorar preparo
            System.out.println("\n5. Monitorando preparo...");
            while (true) {
                int tempoRestante = cozinha.tempoPreparo(preparo);
                if (tempoRestante == 0) {
                    System.out.println("Preparo finalizado!");
                    break;
                }
                System.out.println("Aguardando... Tempo restante: " + tempoRestante + "s");
                Thread.sleep(2000);
            }
            
            // 6. Buscar preparo
            System.out.println("\n6. Buscando preparo na cozinha...");
            String[] preparoPronto = cozinha.pegarPreparo(preparo);
            System.out.println("Preparo entregue! Itens:");
            for (String item : preparoPronto) {
                System.out.println("   " + item);
            }
            
            // 7. Cliente pede a conta
            System.out.println("\n7. Cliente pede a conta...");
            float valor = restaurante.valorComanda(comanda);
            System.out.println("Valor total: R$ " + valor);
            
            // 8. Cliente paga e fecha comanda
            System.out.println("\n8. Cliente paga e fecha comanda...");
            boolean fechada = restaurante.fecharComanda(comanda);
            System.out.println("Comanda fechada: " + fechada);
            
            System.out.println("\n=== FLUXO COMPLETO FINALIZADO ===");
            
        } catch (Exception e) {
            System.err.println("Erro no fluxo completo: " + e.getMessage());
        }
    }
}
