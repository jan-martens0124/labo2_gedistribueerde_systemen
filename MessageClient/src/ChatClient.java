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

    public void sendMessage(String message, String roomName) throws RemoteException {
        chatServer.broadcastMessage(message, this.username, roomName);
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            ChatInterface chatServer = (ChatInterface) java.rmi.Naming.lookup("//localhost/ChatServer");

            while (chatServer.getActiveUsers().contains(username)) {
                System.out.println("Username taken");
                System.out.print("Enter other username: ");
                username = scanner.nextLine();
            }

            ChatClient client = new ChatClient(username, chatServer);

            System.out.println("Welcome to the chat, " + username + "!");
            String message;
            boolean online = true;
            while (online) {
                message = scanner.nextLine();
                String[] messageParsed = message.split(" ");

                switch (messageParsed[0].toLowerCase()) {
                    case "/exit":
                        if (messageParsed.length == 1) {
                            online = false;
                        } else {
                            chatServer.leaveRoom(client, messageParsed[1]);
                        }
                        break;
                    case "/room":
                        if (messageParsed.length >= 3) {
                            // verschuiving om minder code te moeten herschrijven
                            messageParsed[0] = messageParsed[1];
                            messageParsed[1] = username;
                            chatServer.createRoom(messageParsed);
                        } else {
                            System.out.println(
                                    "[Server] /room <naam ruimte> <naam deelnemer1> (<naam deelnemer 2> ...) ");
                            System.out.println(
                                    "Maakt een ruimte aan waar naar kan gestuurd worden met /msg en waar de de vermelde deelnemers in zitten");
                        }
                        break;
                    case "/msg":
                        if (messageParsed.length >= 3) {
                            String msg = "";
                            for (int i = 2; i < messageParsed.length; i++) {
                                msg = msg + messageParsed[i] + " ";
                            }
                            client.sendMessage(msg.trim(), messageParsed[1]);
                        } else {
                            System.out.println("[Server] /msg <naam ruimte> <bericht>");
                            System.out.println(
                                    "Stuurt een bericht naar de vermelde groep, als men deel is van die groep.");
                        }
                        break;

                    case "/users":
                        System.out.println("Online users: ");
                        for (String user : chatServer.getActiveUsers()) {
                            System.out.println(" " + user);
                        }
                        break;
                    case "/help":
                        System.out.println("Commandos: \n /users \n /room \n /msg \n /exit");
                        break;
                    default:
                        client.sendMessage(message, "publicRoom");
                        break;
                }
            }
            chatServer.deregisterClient(client);
            // scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
