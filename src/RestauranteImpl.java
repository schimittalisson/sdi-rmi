import java.io.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RestauranteImpl extends UnicastRemoteObject implements Restaurante {
    private Map<Integer, String> comandas;
    private Map<Integer, Integer> mesasComandas;
    private Map<Integer, List<String[]>> pedidosComanda;
    private Map<Integer, Float> valoresComanda;
    private Map<Integer, Integer> preparosComanda;
    private Map<Integer, Long> temposEntrega;
    private Map<Integer, Integer> duracaoEntrega;
    private Map<Integer, Boolean> pedidosEntregues;
    private int proximaComanda;
    private String[] cardapio;
    private Cozinha cozinha;
    private Random random;
    
    public RestauranteImpl() throws RemoteException {
        super();
        comandas = new HashMap<>();
        mesasComandas = new HashMap<>();
        pedidosComanda = new HashMap<>();
        valoresComanda = new HashMap<>();
        preparosComanda = new HashMap<>();
        temposEntrega = new HashMap<>();
        duracaoEntrega = new HashMap<>();
        pedidosEntregues = new HashMap<>();
        proximaComanda = 1;
        random = new Random();
        
        cozinha = null;

        String cardapioPath = "menu_restaurante.csv";
        List<String> cardapioList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new FileReader(cardapioPath))) {
            boolean primeiraLinha = true;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    if (primeiraLinha) {
                        primeiraLinha = false;
                        continue;
                    }
                    cardapioList.add(line);
                }
            }
            cardapio = cardapioList.toArray(new String[0]);
            System.out.println("Cardapio carregado: " + cardapioList.size() + " itens");
        } catch (IOException e) {
            System.err.println("Erro ao carregar cardapio. Usando cardapio padrao.");
        }
    }
    
    @Override
    public int novaComanda(String nome, int mesa) throws RemoteException {
        int numeroComanda = proximaComanda++;
        comandas.put(numeroComanda, nome);
        mesasComandas.put(numeroComanda, mesa);
        pedidosComanda.put(numeroComanda, new ArrayList<>());
        valoresComanda.put(numeroComanda, 0.0f);
        
        System.out.println("Comanda " + numeroComanda + " criada para " + nome + " na mesa " + mesa);
        return numeroComanda;
    }
    
    @Override
    public String[] consultarCardapio() throws RemoteException {
        System.out.println("Cardapio consultado");
        return cardapio.clone();
    }
    
    @Override
    public String fazerPedido(int comanda, String[] pedido) throws RemoteException {
        if (!comandas.containsKey(comanda)) {
            return "Erro: Comanda não encontrada";
        }
        
        List<String[]> pedidosExistentes = pedidosComanda.get(comanda);
        float valorTotal = valoresComanda.get(comanda);
        
        StringBuilder resultado = new StringBuilder("Pedido adicionado à comanda " + comanda + ":\n");
        
        for (String item : pedido) {
            String[] partes = item.split(",");
            if (partes.length >= 3) {
                try {
                    int codigo = Integer.parseInt(partes[0]);
                    String produto = partes[1];
                    float valor = Float.parseFloat(partes[2]);
                    
                    pedidosExistentes.add(new String[]{String.valueOf(codigo), produto, String.valueOf(valor)});
                    valorTotal += valor;
                    resultado.append("- ").append(produto).append(": R$ ").append(valor).append("\n");
                } catch (NumberFormatException e) {
                    resultado.append("- Erro no item: ").append(item).append("\n");
                }
            }
        }
        
        valoresComanda.put(comanda, valorTotal);
        System.out.println("Pedido registrado para comanda " + comanda + ". Valor: R$ " + valorTotal);
        
        if (conectarCozinha()) {
            try {
                System.out.println("Enviando pedido para cozinha...");
                int preparo = cozinha.novoPreparo(comanda, pedido);
                preparosComanda.put(comanda, preparo);
                resultado.append("\nPedido enviado para cozinha (Preparo #").append(preparo).append(")");
                System.out.println("Pedido da comanda " + comanda + " enviado para cozinha. Preparo: " + preparo);
            } catch (RemoteException e) {
                resultado.append("\nErro ao enviar pedido para cozinha: ").append(e.getMessage());
                System.err.println("Erro ao enviar pedido para cozinha: " + e.getMessage());
            }
        } else {
            resultado.append("\nCozinha nao disponivel no momento");
        }
        
        return resultado.toString();
    }
    
    @Override
    public float valorComanda(int comanda) throws RemoteException {
        if (!comandas.containsKey(comanda)) {
            throw new RemoteException("Comanda não encontrada");
        }
        
        float valor = valoresComanda.get(comanda);
        System.out.println("Valor da comanda " + comanda + ": R$ " + valor);
        return valor;
    }
    
    @Override
    public boolean fecharComanda(int comanda) throws RemoteException {
        if (!comandas.containsKey(comanda)) {
            return false;
        }
        
        if (preparosComanda.containsKey(comanda)) {
            System.out.println("Não é possível fechar comanda " + comanda + " - pedido ainda em preparo");
            return false;
        }
        
        String cliente = comandas.get(comanda);
        int mesa = mesasComandas.get(comanda);
        float valor = valoresComanda.get(comanda);
        
        comandas.remove(comanda);
        mesasComandas.remove(comanda);
        pedidosComanda.remove(comanda);
        valoresComanda.remove(comanda);
        
        System.out.println("Comanda " + comanda + " fechada. Cliente: " + cliente + ", Mesa: " + mesa + ", Valor: R$ " + valor);
        return true;
    }
    
    private boolean conectarCozinha() {
        if (cozinha != null) {
            return true;
        }
        
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            cozinha = (Cozinha) registry.lookup("Cozinha");
            System.out.println("Conectado ao servico da Cozinha");
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao conectar com a Cozinha: " + e.getMessage());
            return false;
        }
    }
    
    public int consultarTempoPreparo(int comanda) throws RemoteException {
        if (!preparosComanda.containsKey(comanda)) {
            throw new RemoteException("Comanda não possui preparo ativo");
        }
        
        if (!conectarCozinha()) {
            throw new RemoteException("Cozinha não disponível");
        }
        
        try {
            int preparo = preparosComanda.get(comanda);
            return cozinha.tempoPreparo(preparo);
        } catch (RemoteException e) {
            throw new RemoteException("Erro ao consultar tempo de preparo: " + e.getMessage());
        }
    }
    
    public String[] buscarPedidoPronto(int comanda) throws RemoteException {
        if (!preparosComanda.containsKey(comanda)) {
            throw new RemoteException("Comanda não possui preparo ativo");
        }
        
        if (!conectarCozinha()) {
            throw new RemoteException("Cozinha não disponível");
        }
        
        try {
            int preparo = preparosComanda.get(comanda);
            String[] pedidoPronto = cozinha.pegarPreparo(preparo);
            preparosComanda.remove(comanda);
            System.out.println("Pedido da comanda " + comanda + " retirado da cozinha");
            return pedidoPronto;
        } catch (RemoteException e) {
            throw new RemoteException("Erro ao buscar pedido pronto: " + e.getMessage());
        }
    }
    
    public boolean pedidoPronto(int comanda) throws RemoteException {
        if (!preparosComanda.containsKey(comanda)) {
            return false;
        }
        
        conectarCozinha();
        try {
            int preparo = preparosComanda.get(comanda);
            int tempoRestante = cozinha.tempoPreparo(preparo);
            
            if (tempoRestante == 0 && !temposEntrega.containsKey(comanda)) {
                int tempoEntrega = random.nextInt(6) + 5;
                temposEntrega.put(comanda, System.currentTimeMillis());
                duracaoEntrega.put(comanda, tempoEntrega);
                pedidosEntregues.put(comanda, false);
                System.out.println("Pedido da comanda " + comanda + " pronto! Tempo de entrega: " + tempoEntrega + "s");
                return true;
            }
            
            return tempoRestante == 0;
        } catch (RemoteException e) {
            return false;
        }
    }
    
    public boolean pedidoEntregue(int comanda) throws RemoteException {
        if (!temposEntrega.containsKey(comanda)) {
            return false;
        }
        
        if (pedidosEntregues.get(comanda)) {
            return true; // Já foi marcado como entregue
        }
        
        long tempoInicio = temposEntrega.get(comanda);
        int duracao = duracaoEntrega.get(comanda);
        long tempoDecorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
        
        if (tempoDecorrido >= duracao) {
            pedidosEntregues.put(comanda, true);
            System.out.println("Pedido da comanda " + comanda + " foi entregue na mesa");
            return true;
        }
        
        return false;
    }
    
    public int tempoRestanteEntrega(int comanda) throws RemoteException {
        if (!temposEntrega.containsKey(comanda)) {
            return -1;
        }
        
        if (pedidosEntregues.get(comanda)) {
            return 0;
        }
        
        long tempoInicio = temposEntrega.get(comanda);
        int duracao = duracaoEntrega.get(comanda);
        long tempoDecorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
        
        return Math.max(0, duracao - (int)tempoDecorrido);
    }
}
