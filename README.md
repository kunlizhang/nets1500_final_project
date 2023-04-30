# NETS 1500 Final Project, Kunli Zhang (kunliz) and Daniel Xue (danxue)

To start a server, use the `Server.java` class. You must pass it a file directory, which serves as the root directory for the server. The root file is the `index.txt` file.

To start a client, use the `Client.java` class. You must pass it a server address, which is the domain of the server you want to connect to.

The `config` folder contains the DNS structure. Put the domain and the IP/port address in the relevant DNS file (`com.txt` for .com domains). The `root.txt` file contains the IP address and port of the root server.

We have included an `exampleServer` folder for an example server you can use.

To run the DNS server, use the `startDNS.sh` shell script.