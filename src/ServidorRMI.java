import java.rmi.registry.*;

public class ServidorRMI {
    public static void main(String[] args) {
        try {
            System.out.println("=== SERVIDOR RMI ===");
            
            // Cria apenas a implementação do restaurante (sem conexão à cozinha)
            RestauranteImpl restaurante = new RestauranteImpl();
            System.out.println("RestauranteImpl criado com sucesso");
            
            // Tenta usar o registry existente, se não existir, cria um novo
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(1099);
                // Testa se o registry está funcionando
                registry.list();
                System.out.println("Usando registry RMI existente na porta 1099");
            } catch (Exception e) {
                System.out.println("Criando novo registry RMI na porta 1099");
                registry = LocateRegistry.createRegistry(1099);
            }
            
            // Registra apenas o serviço do restaurante primeiro
            System.out.println("Registrando servico Restaurante...");
            registry.rebind("Restaurante", restaurante);
            System.out.println("Restaurante registrado com sucesso!");
            
            // Agora cria e registra a cozinha
            System.out.println("Criando e registrando servico Cozinha...");
            CozinhaImpl cozinha = new CozinhaImpl();
            registry.rebind("Cozinha", cozinha);
            System.out.println("Cozinha registrada com sucesso!");
            
            System.out.println("\nSERVIDOR RMI INICIADO COM SUCESSO!");
            System.out.println("Servicos disponiveis:");
            System.out.println("   - Restaurante (porta 1099)");
            System.out.println("   - Cozinha (porta 1099)");
            System.out.println("\nServidor rodando... Pressione Ctrl+C para parar.");
            
            // Lista os serviços registrados para confirmar
            String[] servicos = registry.list();
            System.out.println("\nServicos registrados no registry:");
            for (String servico : servicos) {
                System.out.println("   - " + servico);
            }
            
            // Mantém o servidor rodando
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            System.err.println("Erro no servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
