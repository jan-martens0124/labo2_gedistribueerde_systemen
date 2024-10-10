import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends UnicastRemoteObject implements ChatInterface {
    private List<ClientInterface> clients;

    protected ChatServer() throws RemoteException {
        clients = new ArrayList<>();
    }

    @Override
    public synchronized void registerClient(ClientInterface client) throws RemoteException {
        this.clients.add(client);
        broadcastMessage(client.getUsername() + " has joined the chat!", "Server");
        System.out.println("Current active users:");
        for (ClientInterface c:clients) {
            System.out.println(c.getUsername());
        }
    }

    @Override
    public synchronized void broadcastMessage(String message, String username) throws RemoteException {
        for (ClientInterface client : clients) {
            client.receiveMessage(username + ": " + message);
        }
    }

    @Override
    public List<String> getActiveUsers() throws RemoteException {
        List<String> usernames = new ArrayList<>();
        for (ClientInterface client : clients) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }
}
