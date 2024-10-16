import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatInterface extends Remote {
    void registerClient(ClientInterface client) throws RemoteException;
    void deregisterClient(ClientInterface client) throws RemoteException;
    void broadcastMessage(String message, String username) throws RemoteException;
    List<String> getActiveUsers() throws RemoteException;
}
