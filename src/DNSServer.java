import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNSServer {
    private DatagramSocket sock;
    private final byte[] buf = new byte[256];

    List<NameAddrPair> domains;
    List<NameAddrPair> refs;

    public static class NameAddrPair {
        public String name;
        public InetSocketAddress addr;

        NameAddrPair(String name, InetSocketAddress addr) {
            this.name = name;
            this.addr = addr;
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

    public DNSServer(int port, List<NameAddrPair> domains, List<NameAddrPair> refs) {
        try {
            sock = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.domains = domains;
        this.refs = refs;
    }

    private String extractDomain(String url) {
        // Regex source: https://stackoverflow.com/questions/25703360/regular-expression-extract-subdomain-domain
        Pattern pattern = Pattern.compile("^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/?\\n]+)");
        Matcher matcher = pattern.matcher(url);
        String match = "";
        if (matcher.find()) {
            match = matcher.group(1);
        }
        return match;
    }

    /**
     * Checks if url2 is a subdomain of or matches url1
     *
     * @param url1 domain known by DNS server
     * @param url2 given url to match with domain
     * @return 0 if not a match, 1 if exact domain, 2 if subdomain
     */
    public int domainComp(String url1, String url2) {
        String name1 = extractDomain(url1);
        String name2 = extractDomain(url2);

        if (name1.equals(name2)) {
            return 1;
        } else if (name2.endsWith(name1)) {
            return 2;
        }
        return 0;
    }

    public InetSocketAddress getDomainAddr(String url) {
        for (NameAddrPair pair : domains) {
            if (domainComp(pair.name, url) == 1) {
                return pair.addr;
            }
        }
        return null;
    }

    public InetSocketAddress getRefAddr(String url) {
        for (NameAddrPair pair : refs) {
            if (domainComp(pair.name, url) == 2) {
                return pair.addr;
            }
        }
        return null;
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

    public void run() {
        HashMap<String, InetSocketAddress> recCalls = new HashMap<>();

        while (true) {
            // read in next packet
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            try {
                sock.receive(pkt);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            InetAddress ip = pkt.getAddress();
            int port = pkt.getPort();
            String msg = new String(pkt.getData(), 0, pkt.getLength());
            msg = msg.trim();
            System.out.println(msg);

            // split on space, first letter is command
            String[] split = msg.split(" ");
            String cmd = split[0];
            if (cmd.equals("I")) {
                // iterative lookup [F [uuid] [url]]
                String uuid = split[1];
                String url = split[2];
                InetSocketAddress addr = getDomainAddr(url);
                if (addr != null) {
                    // return final domain location
                    sendPkt("D " + uuid + " " + serAddr(addr), ip, port);
                } else {
                    addr = getRefAddr(url);
                    if (addr != null) {
                        // return referral to next DNS server
                        sendPkt("N " + uuid + " " + serAddr(addr), ip, port);
                    } else {
                        // error if name not found
                        sendPkt("E " + uuid + " ", ip, port);
                    }
                }
            } else if (cmd.equals("R")) {
                // recursive lookup [R [uuid] [url]]
                String uuid = split[1];
                String url = split[2];
                InetSocketAddress addr = getDomainAddr(url);
                if (addr != null) {
                    sendPkt("D " + uuid + " " + serAddr(addr), ip, port);
                } else {
                    addr = getRefAddr(url);
                    if (addr != null) {
                        recCalls.put(uuid, new InetSocketAddress(ip, port));
                        sendPkt("R " + uuid + " " + url, addr.getAddress(), addr.getPort());
                    } else {
                        sendPkt("E " + uuid, ip, port);
                    }
                }
            } else if (cmd.equals("D") || cmd.equals("E")) {
                String uuid = split[1];
                if (recCalls.containsKey(uuid)) {
                    InetSocketAddress addr = recCalls.get(uuid);
                    sendPkt(msg, addr.getAddress(), addr.getPort());
                    recCalls.remove(uuid);
                }
            }
        }

        sock.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Improper number of arguments");
            return;
        }

        InetSocketAddress addr;
        ArrayList<DNSServer.NameAddrPair> domains = new ArrayList<>();
        ArrayList<DNSServer.NameAddrPair> refs = new ArrayList<>();
        try {
            BufferedReader config = new BufferedReader(new FileReader(args[0]));
            addr = parseAddr(config.readLine());
            if (addr == null) {
                System.out.println("Improperly formatted config file");
                return;
            }
            String line;
            boolean flag = false;
            while ((line = config.readLine()) != null) {
                if (line.isEmpty()) {
                    flag = true;
                }
                InetSocketAddress tmpAddr;
                String[] split = line.split(" ");
                if (split.length != 2 || (tmpAddr = parseAddr(split[1])) == null) {
                    continue;
                }
                if (flag) {
                    refs.add(new NameAddrPair(split[0], tmpAddr));
                } else {
                    domains.add(new NameAddrPair(split[0], tmpAddr));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading config file");
            return;
        }

        DNSServer server = new DNSServer(addr.getPort(), domains, refs);
        server.run();
    }
}
