import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            ChatServer chatServer = new ChatServer();
            Naming.rebind("//localhost/ChatServer", chatServer);
            System.out.println("Chat server started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
