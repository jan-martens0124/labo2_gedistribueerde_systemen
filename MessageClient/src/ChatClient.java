import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ClientInterface {
    private String username;
    private ChatInterface chatServer;

    protected ChatClient(String username, ChatInterface chatServer) throws RemoteException {
        this.username = username;
        this.chatServer = chatServer;
        chatServer.registerClient(this);
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public String getUsername() throws RemoteException {
        return this.username;
    }

    public void sendMessage(String message) throws RemoteException {
        chatServer.broadcastMessage(message, this.username);
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            ChatInterface chatServer = (ChatInterface) java.rmi.Naming.lookup("//localhost/ChatServer");

            ChatClient client = new ChatClient(username, chatServer);

            System.out.println("Welcome to the chat, " + username + "!");
            String message;
            while (true) {
                message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) break;
                client.sendMessage(message);
            }
            chatServer.deregisterClient(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
