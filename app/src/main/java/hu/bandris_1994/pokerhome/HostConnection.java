package hu.bandris_1994.pokerhome;

import java.util.ArrayList;

/**
 * Created by András on 2016. 12. 28..
 */

public class HostConnection {
    private ArrayList<ClientConnection> clients;

    public HostConnection(){
        clients = new ArrayList<>();
    }

    public void AddClient(ClientConnection client){
        clients.add(client);
        client.setHost(this);
    }

    /**
     * Sends message to a client
     * @param name Name of the client to send to
     * @param mess The message
     */
    public void SendMessage(String name, String mess){
        ClientConnection client=null;
        for(ClientConnection c : clients){
            if (c.getName().equals(name)){
                client=c;
                break;
            }
        }
        if (client==null){
            throw new IllegalArgumentException("No client with this name found");
        }else{
            client.ReceiveMessage(mess);
        }
    }

    public void ReceiveMessage(String who, String mess){

    }
}
