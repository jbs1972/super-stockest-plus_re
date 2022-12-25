package Useful;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GetClientIP {
    
    public static void main(String args[])
    {
        InetAddress IP=null;
        try {
            IP = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        System.out.println("IP of my system is := "+IP.getHostAddress());
    }
    
}
