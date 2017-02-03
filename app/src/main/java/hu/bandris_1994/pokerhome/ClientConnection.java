package hu.bandris_1994.pokerhome;

import java.util.Timer;

/**
 * Created by András on 2016. 12. 28..
 */

public class ClientConnection {
    private HostConnection host;
    private String name;

    public ClientConnection(String name){
        this.name = name;
    }

    public void setHost(HostConnection host){
        this.host = host;
    }

    public String getName(){
        return name;
    }

    public void SendMessage(String mess){
        host.ReceiveMessage(name, mess);
    }
    public void ReceiveMessage(String mess){

    }
}
