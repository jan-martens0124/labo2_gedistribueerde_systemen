import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatInterface extends Remote {
    boolean registerClient(ClientInterface client) throws RemoteException;
    void deregisterClient(ClientInterface client) throws RemoteException;
    void broadcastMessage(String message, String username, String roomName) throws RemoteException;
    boolean createRoom(String[] payload) throws RemoteException;
    boolean leaveRoom(ClientInterface client, String roomName)throws RemoteException;
    List<String> getActiveUsers() throws RemoteException;
}
