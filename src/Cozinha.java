import java.rmi.*;
/*
 * CSV (int,string,float):
 * codigo,produto,valor
 */

public interface Cozinha extends Remote {
    public int novoPreparo(int comanda, String[] pedido) throws RemoteException;
    public int tempoPreparo(int preparo) throws RemoteException;
    public String[] pegarPreparo(int preparo) throws RemoteException;
}