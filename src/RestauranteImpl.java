import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RestauranteImpl extends UnicastRemoteObject implements Restaurante {
    private Map<Integer, String> comandas;
    private Map<Integer, Integer> mesasComandas;
    private Map<Integer, List<String[]>> pedidosComanda;
    private Map<Integer, Float> valoresComanda;
    private int proximaComanda;
    private String[] cardapio;
    
    public RestauranteImpl() throws RemoteException {
        super();
        comandas = new HashMap<>();
        mesasComandas = new HashMap<>();
        pedidosComanda = new HashMap<>();
        valoresComanda = new HashMap<>();
        proximaComanda = 1;

        String cardapioPath = "menu_restaurante.csv";
        List<String> cardapioList = new ArrayList<>();

        try (Scanner scanner = new Scanner(new FileReader(cardapioPath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    cardapioList.add(line);
                }
            }
            cardapio = cardapioList.toArray(new String[0]);
            System.out.println("Cardápio carregado do arquivo: " + cardapioList.size() + " itens");
        } catch (IOException e) {
            System.err.println("Erro ao carregar cardápio do arquivo. Usando cardápio padrão.");
            // Cardápio padrão caso o arquivo não seja encontrado
            cardapio = new String[]{
                "1,Pizza Margherita,25.50",
                "2,Hambúrguer Clássico,18.90",
                "3,Salada Caesar,15.00",
                "4,Lasanha Bolonhesa,22.00",
                "5,Refrigerante,5.50",
                "6,Suco Natural,8.00"
            };
        }
    }
    
    @Override
    public int novaComanda(String nome, int mesa) throws RemoteException {
        int numeroComanda = proximaComanda++;
        comandas.put(numeroComanda, nome);
        mesasComandas.put(numeroComanda, mesa);
        pedidosComanda.put(numeroComanda, new ArrayList<>());
        valoresComanda.put(numeroComanda, 0.0f);
        
        System.out.println("Nova comanda criada: " + numeroComanda + " para " + nome + " na mesa " + mesa);
        return numeroComanda;
    }
    
    @Override
    public String[] consultarCardapio() throws RemoteException {
        System.out.println("Cardápio consultado");
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
        System.out.println("Pedido feito para comanda " + comanda + ". Valor total: R$ " + valorTotal);
        
        return resultado.toString();
    }
    
    @Override
    public float valorComanda(int comanda) throws RemoteException {
        if (!comandas.containsKey(comanda)) {
            throw new RemoteException("Comanda não encontrada");
        }
        
        float valor = valoresComanda.get(comanda);
        System.out.println("Valor consultado para comanda " + comanda + ": R$ " + valor);
        return valor;
    }
    
    @Override
    public boolean fecharComanda(int comanda) throws RemoteException {
        if (!comandas.containsKey(comanda)) {
            return false;
        }
        
        String cliente = comandas.get(comanda);
        int mesa = mesasComandas.get(comanda);
        float valor = valoresComanda.get(comanda);
        
        // Remove a comanda
        comandas.remove(comanda);
        mesasComandas.remove(comanda);
        pedidosComanda.remove(comanda);
        valoresComanda.remove(comanda);
        
        System.out.println("Comanda " + comanda + " fechada. Cliente: " + cliente + 
                          ", Mesa: " + mesa + ", Valor pago: R$ " + valor);
        return true;
    }
}
