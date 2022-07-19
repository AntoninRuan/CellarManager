package fr.antoninruan.cellarmanager.utils.mobile_sync;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;

/**
 * @author Antonin Ruan
 */
public class MobileSyncManager {

    public static final String VERSION = "2.0";

    protected static final int PORT = 8080;
    public static final String LINK_CODE = getLinkCode();

    private static int code = 0;
    static {
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while(enumeration.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) enumeration.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if(i.getHostAddress().startsWith("192.168")) {
                        String[] parse = i.getHostAddress().split("\\.");
                        code = Integer.parseInt(parse[2]) * 1000 + Integer.parseInt(parse[3]);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static String getLinkCode() {
        DecimalFormat format = new DecimalFormat("000,000");
        return format.format(code);
    }

    private static boolean activate = false;
    private static final HttpServer server;

    static {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new WebServer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isActivate() {
        return activate;
    }

    public static void toggleState() {
        if(isActivate()) {
            server.stop(0);
            activate = false;
        } else {
            server.start();
            activate = true;
        }
    }


}
