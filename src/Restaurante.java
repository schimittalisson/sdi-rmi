import java.rmi.*;

public interface Restaurante extends Remote {
    public int novaComanda(String nome, int mesa) throws RemoteException;
    public String[] consultarCardapio() throws RemoteException;
    public String fazerPedido(int comanda, String[] pedido) throws RemoteException;
    public float valorComanda(int comanda) throws RemoteException;
    public boolean fecharComanda(int comanda) throws RemoteException;
    public int consultarTempoPreparo(int comanda) throws RemoteException;
    public boolean pedidoPronto(int comanda) throws RemoteException;
    public String[] buscarPedidoPronto(int comanda) throws RemoteException;
}