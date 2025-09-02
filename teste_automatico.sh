#!/bin/bash

echo "=== TESTE AUTOMATIZADO DO SISTEMA RMI ==="
echo "Demonstrando todas as funcionalidades do sistema..."
echo ""

# Define o Java 17 explicitamente
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Navega para o diretório src
cd src

# Cria um cliente de teste automatizado
cat > TesteAutomatico.java << 'EOF'
import java.rmi.*;
import java.rmi.registry.*;

public class TesteAutomatico {
    private static Restaurante restaurante;
    private static Cozinha cozinha;
    
    public static void main(String[] args) {
        try {
            System.out.println("=== INICIANDO TESTE AUTOMATIZADO ===");
            
            // Conecta ao registry RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            restaurante = (Restaurante) registry.lookup("Restaurante");
            cozinha = (Cozinha) registry.lookup("Cozinha");
            
            System.out.println("✅ Conectado aos serviços RMI com sucesso!");
            System.out.println("");
            
            // Teste 1: Consultar cardápio
            testarCardapio();
            
            // Teste 2: Criar comanda e fazer pedido
            testarRestaurante();
            
            // Teste 3: Testar cozinha
            testarCozinha();
            
            // Teste 4: Fluxo completo
            testarFluxoCompleto();
            
            System.out.println("=== TODOS OS TESTES CONCLUÍDOS COM SUCESSO! ===");
            
        } catch (Exception e) {
            System.err.println("Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testarCardapio() throws RemoteException {
        System.out.println("🍽️  TESTE 1: CONSULTANDO CARDÁPIO");
        String[] cardapio = restaurante.consultarCardapio();
        System.out.println("📋 Cardápio carregado com " + cardapio.length + " itens");
        System.out.println("🔸 Primeiros 5 itens do cardápio:");
        for (int i = 0; i < Math.min(5, cardapio.length); i++) {
            System.out.println("   " + (i+1) + ". " + cardapio[i]);
        }
        System.out.println("");
    }
    
    private static void testarRestaurante() throws RemoteException {
        System.out.println("🏪 TESTE 2: OPERAÇÕES DO RESTAURANTE");
        
        // Criar comanda
        int comanda = restaurante.novaComanda("João Silva", 5);
        System.out.println("✅ Nova comanda criada: " + comanda + " para João Silva na mesa 5");
        
        // Fazer pedido
        String[] pedido = {"2,Macarronada,32.16", "5,Cerveja IPA,47.79"};
        String resultado = restaurante.fazerPedido(comanda, pedido);
        System.out.println("🛒 Pedido realizado:");
        System.out.println(resultado);
        
        // Consultar valor
        float valor = restaurante.valorComanda(comanda);
        System.out.println("💰 Valor total da comanda: R$ " + String.format("%.2f", valor));
        
        // Fechar comanda
        boolean fechada = restaurante.fecharComanda(comanda);
        System.out.println("🧾 Comanda fechada: " + (fechada ? "✅ Sim" : "❌ Não"));
        System.out.println("");
    }
    
    private static void testarCozinha() throws RemoteException, InterruptedException {
        System.out.println("👨‍🍳 TESTE 3: OPERAÇÕES DA COZINHA");
        
        // Criar preparo
        String[] pedido = {"8,Moqueca de Peixe,30.99", "18,Risoto de Camarão,13.18"};
        int preparo = cozinha.novoPreparo(999, pedido);
        System.out.println("🔥 Novo preparo iniciado: " + preparo);
        
        // Consultar tempo inicial
        int tempoInicial = cozinha.tempoPreparo(preparo);
        System.out.println("⏱️  Tempo estimado de preparo: " + tempoInicial + " segundos");
        
        // Aguardar um pouco
        System.out.println("⏳ Aguardando 3 segundos...");
        Thread.sleep(3000);
        
        // Consultar tempo restante
        int tempoRestante = cozinha.tempoPreparo(preparo);
        System.out.println("⏱️  Tempo restante: " + tempoRestante + " segundos");
        
        // Aguardar até ficar pronto
        while (tempoRestante > 0) {
            System.out.println("🕐 Aguardando preparo ficar pronto... (" + tempoRestante + "s restantes)");
            Thread.sleep(2000);
            tempoRestante = cozinha.tempoPreparo(preparo);
        }
        
        // Pegar preparo
        String[] preparoPronto = cozinha.pegarPreparo(preparo);
        System.out.println("🍽️  Preparo finalizado e entregue!");
        System.out.println("📦 Itens entregues:");
        for (String item : preparoPronto) {
            System.out.println("   • " + item);
        }
        System.out.println("");
    }
    
    private static void testarFluxoCompleto() throws RemoteException, InterruptedException {
        System.out.println("🔄 TESTE 4: FLUXO COMPLETO DO RESTAURANTE");
        
        // Cliente chega
        System.out.println("👤 Cliente 'Maria Santos' chega na mesa 3");
        int comanda = restaurante.novaComanda("Maria Santos", 3);
        System.out.println("📝 Comanda " + comanda + " criada");
        
        // Faz pedido
        String[] pedido = {"28,Pizza Margherita,59.88", "36,Suco Natural de Laranja,20.24"};
        System.out.println("🛒 Cliente faz pedido...");
        restaurante.fazerPedido(comanda, pedido);
        
        // Pedido vai para cozinha
        System.out.println("👨‍🍳 Pedido enviado para a cozinha...");
        int preparo = cozinha.novoPreparo(comanda, pedido);
        
        // Aguarda preparo
        System.out.println("⏳ Aguardando preparo...");
        int tempo = cozinha.tempoPreparo(preparo);
        while (tempo > 0) {
            System.out.println("🕐 Preparando... " + tempo + "s restantes");
            Thread.sleep(Math.min(2000, tempo * 1000));
            tempo = cozinha.tempoPreparo(preparo);
        }
        
        // Entrega pedido
        cozinha.pegarPreparo(preparo);
        System.out.println("🍽️  Pedido entregue ao cliente!");
        
        // Cliente paga
        float valor = restaurante.valorComanda(comanda);
        System.out.println("💰 Cliente paga R$ " + String.format("%.2f", valor));
        restaurante.fecharComanda(comanda);
        System.out.println("✅ Atendimento finalizado!");
        System.out.println("");
    }
}
EOF

# Compila o teste
echo "Compilando teste automatizado..."
$JAVA_HOME/bin/javac TesteAutomatico.java

if [ $? -eq 0 ]; then
    echo "✅ Teste compilado com sucesso!"
    echo ""
    echo "Executando teste automatizado..."
    echo "=================================================="
    $JAVA_HOME/bin/java TesteAutomatico
else
    echo "❌ Erro na compilação do teste!"
fi
