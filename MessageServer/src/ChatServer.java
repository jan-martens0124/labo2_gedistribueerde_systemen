import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class ChatServer extends UnicastRemoteObject implements ChatInterface {
    private ArrayList<ClientInterface> publicRoom;
    private HashMap<String, ArrayList<ClientInterface>> rooms = new HashMap<>();

    protected ChatServer() throws RemoteException {
        publicRoom = new ArrayList<>();
        rooms.put("publicRoom", publicRoom);
    }

    @Override
    public synchronized boolean registerClient(ClientInterface client) throws RemoteException {
        for (ClientInterface user : publicRoom) {
            if (client.getUsername().equals(user.getUsername())) {
                System.out.println("Username taken, registration blocked");
                return false;
            }
        }

        this.publicRoom.add(client);
        broadcastMessage(client.getUsername() + " has joined the chat!", "Server", "publicRoom");
        printActiveUsers();
        return true;
    }

    @Override
    public void deregisterClient(ClientInterface client) throws RemoteException {
        this.publicRoom.remove(client);
        broadcastMessage(client.getUsername() + " has left the chat!", "Server", "publicRoom");
        printActiveUsers();
    }

    @Override
    public synchronized void broadcastMessage(String message, String username, String roomName) throws RemoteException {
        boolean isMember = false;
        for (ClientInterface client : rooms.get(roomName)) {
            if (username.equals(client.getUsername())) {
                isMember = true;
            }
        }
        if (isMember) {
            String fullMessage;
            if (roomName.equals("publicRoom")) {
                fullMessage = username + ": " + message;
            } else {
                fullMessage = username + "->" + roomName + ": " + message;
            }
            for (ClientInterface client : rooms.get(roomName)) {
                client.receiveMessage(fullMessage);
            }
        }
        System.out.println("Message from " + username + " to " + roomName);
    }

    @Override
    public List<String> getActiveUsers() throws RemoteException {
        List<String> usernames = new ArrayList<>();
        for (ClientInterface client : publicRoom) {
            usernames.add(client.getUsername());
        }
        return usernames;
    }

    @Override
    public boolean leaveRoom(ClientInterface client, String roomName) {
        ArrayList<ClientInterface> room = rooms.get(roomName);
        if (room != null) {
            room.remove(room.indexOf(client));
            if (rooms.size() == 0) {
                rooms.remove(roomName);
            }
            try {
                System.out.println(client.getUsername() + " left " + roomName);
            } catch (Exception e) {
                // TODO: handle exception
            }
            return true;
        }
        try {
            System.out.println(client.getUsername() + " tried to leave " + roomName + ". Room does not exist.");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    @Override
    public boolean createRoom(String[] payload) {
        ArrayList<ClientInterface> temp = new ArrayList<ClientInterface>();

        try {
            // posities in payload zijn een deel verplaatsts tov het commando
            System.out.println("Creating room: " + payload[0]);
            if(rooms.containsKey(payload[0])){
                System.out.println("Room already exists");
                return false;
            }
            System.out.println("With members: ");

            for (ClientInterface client : publicRoom) {
                for (int i = 1; i < payload.length; i++) {
                    if (client.getUsername().equals(payload[i]) && !temp.contains(client)) {
                        temp.add(client);
                        System.out.println("Added: " + payload[i]);
                    }
                }
            }
            rooms.put(payload[0], temp);
            System.out.println("Creation succesful");
        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }

    public void printActiveUsers() throws RemoteException {
        System.out.println("Current active users:");
        for (ClientInterface c : publicRoom) {
            System.out.println(c.getUsername());
        }
    }
}
