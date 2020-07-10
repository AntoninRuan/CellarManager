package fr.womax.cavemanager.utils.mobile_sync;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.Enumeration;

/**
 * @author Antonin Ruan
 */
public class MobileSyncManager {

    public static final String VERSION = "1.0";

    protected static final int PORT = 24195;
    public static final String LINK_CODE = getLinkCode();

    private static int code = 0;

    private static String getLinkCode() {
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

        DecimalFormat format = new DecimalFormat("000,000");
        return format.format(code);
    }

    public static boolean isActivate() {
        return WebServer.run;
    }

    public static void toggleState() {
        if(isActivate()) {
            WebServer.stopWebServer();
        } else {
            WebServer.startWebServer();
        }
    }


}
