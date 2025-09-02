import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CozinhaImpl extends UnicastRemoteObject implements Cozinha {
    private Map<Integer, String[]> preparos;
    private Map<Integer, Integer> comandasPreparo;
    private Map<Integer, Long> temposInicio;
    private Map<Integer, Integer> temposPreparo;
    private int proximoPreparo;
    private Random random;
    
    public CozinhaImpl() throws RemoteException {
        super();
        preparos = new HashMap<>();
        comandasPreparo = new HashMap<>();
        temposInicio = new HashMap<>();
        temposPreparo = new HashMap<>();
        proximoPreparo = 1;
        random = new Random();
    }
    
    @Override
    public int novoPreparo(int comanda, String[] pedido) throws RemoteException {
        int codigoPreparo = proximoPreparo++;
        
        // Armazena o pedido
        preparos.put(codigoPreparo, pedido.clone());
        comandasPreparo.put(codigoPreparo, comanda);
        
        // Define tempo de preparo aleatório entre 1 e 10 segundos
        int tempoPreparo = random.nextInt(10) + 1;
        temposPreparo.put(codigoPreparo, tempoPreparo);
        
        // Marca o tempo de início
        temposInicio.put(codigoPreparo, System.currentTimeMillis());
        
        System.out.println("Novo preparo iniciado: " + codigoPreparo + 
                          " para comanda " + comanda + 
                          " (tempo estimado: " + tempoPreparo + " segundos)");
        
        // Exibe os itens do pedido
        System.out.println("Itens do pedido:");
        for (String item : pedido) {
            System.out.println("- " + item);
        }
        
        return codigoPreparo;
    }
    
    @Override
    public int tempoPreparo(int preparo) throws RemoteException {
        if (!preparos.containsKey(preparo)) {
            throw new RemoteException("Preparo não encontrado");
        }
        
        long tempoInicio = temposInicio.get(preparo);
        int tempoTotal = temposPreparo.get(preparo);
        long tempoDecorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
        
        int tempoRestante = Math.max(0, tempoTotal - (int)tempoDecorrido);
        
        System.out.println("Tempo restante para preparo " + preparo + ": " + tempoRestante + " segundos");
        
        return tempoRestante;
    }
    
    @Override
    public String[] pegarPreparo(int preparo) throws RemoteException {
        if (!preparos.containsKey(preparo)) {
            throw new RemoteException("Preparo não encontrado");
        }
        
        // Verifica se o tempo de preparo já passou
        long tempoInicio = temposInicio.get(preparo);
        int tempoTotal = temposPreparo.get(preparo);
        long tempoDecorrido = (System.currentTimeMillis() - tempoInicio) / 1000;
        
        if (tempoDecorrido < tempoTotal) {
            int tempoRestante = tempoTotal - (int)tempoDecorrido;
            throw new RemoteException("Preparo ainda não está pronto. Tempo restante: " + 
                                    tempoRestante + " segundos");
        }
        
        // Preparo está pronto, pode ser entregue
        String[] pedido = preparos.get(preparo);
        int comanda = comandasPreparo.get(preparo);
        
        // Remove o preparo da cozinha
        preparos.remove(preparo);
        comandasPreparo.remove(preparo);
        temposInicio.remove(preparo);
        temposPreparo.remove(preparo);
        
        System.out.println("Preparo " + preparo + " da comanda " + comanda + " foi entregue");
        
        return pedido;
    }
}
