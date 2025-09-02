import java.rmi.*;
import java.rmi.registry.*;

public class ServidorRMI {
    public static void main(String[] args) {
        try {
            // Cria as implementações dos serviços
            RestauranteImpl restaurante = new RestauranteImpl();
            CozinhaImpl cozinha = new CozinhaImpl();
            
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
            
            // Registra os serviços no registry
            registry.rebind("Restaurante", restaurante);
            registry.rebind("Cozinha", cozinha);
            
            System.out.println("Servidor RMI iniciado com sucesso!");
            System.out.println("Serviços disponíveis:");
            System.out.println("- Restaurante (porta 1099)");
            System.out.println("- Cozinha (porta 1099)");
            System.out.println("Pressione Ctrl+C para parar o servidor...");
            
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
