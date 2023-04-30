import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.UUID;

public class Lookup {
    private DatagramSocket sock;
    private final byte[] buf = new byte[256];
    private InetSocketAddress rootAddr = new InetSocketAddress("127.0.0.1", 5000);

    public Lookup() {
        try {
            sock = new DatagramSocket();
            sock.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static InetSocketAddress parseAddr(String addrStr) {
        String[] split = addrStr.split(":");
        if (split.length != 2) return null;
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }

    public static String serAddr(InetSocketAddress addr) {
        return addr.getHostString() + ":" + addr.getPort();
    }

    private boolean sendPkt(String msg, InetAddress ip, int port) {
        byte[] sendBuf = msg.getBytes();
        DatagramPacket sendPkt = new DatagramPacket(sendBuf, sendBuf.length, ip, port);
        try {
            sock.send(sendPkt);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public InetSocketAddress recLookup(String url) {
        try {
            sock = new DatagramSocket();
        } catch (IOException e) {
            return null;
        }

        String uuid = UUID.randomUUID().toString();
        sendPkt("R " + uuid + " " + url, rootAddr.getAddress(), rootAddr.getPort());

        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
        try {
            sock.receive(pkt);
            String msg = new String(pkt.getData(), 0, pkt.getLength());
            msg = msg.trim();

            // split on space, first letter is command
            String[] split = msg.split(" ");
            if (split.length != 3 || !split[1].equals(uuid)) {
                return null;
            } else if (split[0].equals("D")) {
                return parseAddr(split[2]);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InetSocketAddress iterLookup(String url) {
        try {
            sock = new DatagramSocket();
        } catch (IOException e) {
            return null;
        }

        String uuid = UUID.randomUUID().toString();
        sendPkt("I " + uuid + " " + url, rootAddr.getAddress(), rootAddr.getPort());

        while (true) {
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            try {
                sock.receive(pkt);
                String msg = new String(pkt.getData(), 0, pkt.getLength());
                msg = msg.trim();

                // split on space, first letter is command
                String[] split = msg.split(" ");
                if (split.length != 3 || !split[1].equals(uuid)) {
                    return null;
                } else if (split[0].equals("N")) {
                    InetSocketAddress nextAddr = parseAddr(split[2]);
                    sendPkt("I " + uuid + " " + url, nextAddr.getAddress(), nextAddr.getPort());
                } else if (split[0].equals("D")) {
                    return parseAddr(split[2]);
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void main(String[] args) {
        Lookup l = new Lookup();
        System.out.println(l.recLookup("sample.com"));
        System.out.println(l.recLookup("seas.upenn.edu"));
        System.out.println(l.recLookup("blah.com"));

        System.out.println(l.iterLookup("sample.com"));
        System.out.println(l.iterLookup("seas.upenn.edu"));
        System.out.println(l.iterLookup("blah.com"));
    }
}
