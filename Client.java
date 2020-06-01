import java.net.*;
import java.util.ArrayList;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class Client {

	/*
	Initialize socket and input output streams
	Changed DataInputStream to BufferedReader due to deprecation in code
    BufferedReader is supposed to get info from the socket
	DataOutputStream is thus used to write to the socket
	Server[] sArr is supposed to help iterate through the values
	largeServer is as the nam suggest for largeServer meaning used to only print largeServer
	globalString helps with connection aspects
	*/

	private Socket socket = null;
	private BufferedReader in = null; // USED GET INFO FROM SOCKET
	private DataOutputStream out = null; // USED TO WRITE TO SOCKET
	private Server[] sArr = new Server[1];
	private ArrayList<Server> serverArrList = new ArrayList<Server>();
	private int largeServer = 0;
	private String globalString;
	private Boolean end = false;
	private String algorithmType = "ff";

	/*
	 * The constructor for the class. Need an address and port to set-up the
	 * connection with the server. Also sets up an input and output datastream so we
	 * can send/ receive data from the server.
	 */

	public Client(String address, int port) {
		// establish a connection
		try {
			socket = new Socket(address, port);
			System.out.println("HI COMP3100, WE'RE GROUP 8 AND WELCOME TO OUR CRIB :)");
			
			// takes input from terminal
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// sends output to the socket
			out = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException u) {
			System.out.println(u);
            System.out.println("Unknown Host Exception");
		} catch (IOException i) {
			System.out.println("IO Exception. Quickly! Unplug and plug it back in!");
            System.out.println(i);
		}
	}

	// To run client and set up connection
	public void run() {
		// Setting up the connection
		send("HELO");
		globalString = recv();
		send("AUTH " + System.getProperty("user.name"));
		globalString = recv();
		parseXML();
		send("REDY");
		globalString = recv();

		if (globalString.equals("NONE")) {
			quit();
		} else {
			while (!end) {
				// end variable is changed when we receive "NONE".
				if (globalString.equals("OK")) {
					send("REDY");
					globalString = recv(); 
					// this will be the job information
				}
				if (globalString.equals("NONE")) {
					end = true;
					break;
				}

				// need to parse the job here
				String[] jobString = globalString.split("\\s+"); 
				// break the job information up so we can create obj
				Job job = new Job(Integer.parseInt(jobString[1]), Integer.parseInt(jobString[2]),
						Integer.parseInt(jobString[3]), Integer.parseInt(jobString[4]), Integer.parseInt(jobString[5]),
						Integer.parseInt(jobString[6]));

				send("RESC All");
				globalString = recv();
				send("OK");

				globalString = recv();
				serverArrList = new ArrayList<Server>();
				while (!globalString.equals(".")) {
					// We know the server has stopped sending information when we get ".".
					// Therefore, we'll keeping reading information in and adding array until then.

					String[] serverInfo = globalString.split("\\s+");
					// Adding Server information to ArrayList for later use.
					serverArrList.add(
							new Server(
									serverInfo[0], Integer.parseInt(serverInfo[1]), Integer.parseInt(serverInfo[2]),
									Integer.parseInt(serverInfo[3]), Integer.parseInt(serverInfo[4]),
									Integer.parseInt(serverInfo[5]), Integer.parseInt(serverInfo[6])
									)
							);
					//System.out.println("ADDED SERVER");
					send("OK");
					globalString = recv();
				}

				Algo algo = new Algo(serverArrList, sArr);

				Server sendTo = null;
				if (algorithmType.equals("bf")) {
					sendTo = algo.bestFit(job);
					send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				} else if (algorithmType.equals("ff")) {
					sendTo = algo.firstFit(job);
					send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				} else if (algorithmType.equals("wf")) {
					sendTo = algo.worstFit(job);
					send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				} else if (algorithmType.equals("sj")) {
					sendTo = algo.sjf(job);
					send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				} else {
					String[] jobData = globalString.split("\\s+");
					int count = Integer.parseInt(jobData[2]);
					send("SCHD " + count + " " + sArr[largeServer].type + " " + "0");
				}

				globalString = recv();
			}
		}
		quit();
	}

	// Used to send messages from the server. Takes the message as a parameter.
	public void send(String message) {
		try {
			out.write(message.getBytes());
			// System.out.print("SENT: " + message);
			out.flush();
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	// Used to receive messages from the server.
	public String recv() {
		String message = "";
		try {
			while (!in.ready()) {
			}
			while (in.ready()) {
				message += (char) in.read();
			}
			// System.out.print("RCVD: " + message);
			globalString = message;
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
		return message;
	}

	// Terminate the connection with the server.
	public void quit() {
		try {
			send("QUIT");
			globalString = recv();
			if (globalString.equals("QUIT")) {
				in.close();
				out.close();
				socket.close();
			}
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	/*
	 * Used to parse information from the XML used in association with the server.
	 * We need to be able to break up the information and put it into a Server
	 * object so that we can use it.
	 */
	public void parseXML() {
		try {
			File systemXML = new File("system.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(systemXML);

			doc.getDocumentElement().normalize();
			NodeList servers = doc.getElementsByTagName("server");
			sArr = new Server[servers.getLength()];
			for (int i = 0; i < servers.getLength(); i++) {
				Element server = (Element) servers.item(i);
				String t = server.getAttribute("type");
				int l = Integer.parseInt(server.getAttribute("limit"));
				int b = Integer.parseInt(server.getAttribute("bootupTime"));
				float r = Float.parseFloat(server.getAttribute("rate"));
				int c = Integer.parseInt(server.getAttribute("coreCount"));
				int m = Integer.parseInt(server.getAttribute("memory"));
				int d = Integer.parseInt(server.getAttribute("disk"));
				Server temp = new Server(i, t, l, b, r, c, m, d);
				sArr[i] = temp;
			}
			largeServer = largeServer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/*
	 * Using the information from the XML file, we want to determine which Server
	 * object is the largest and return that.
	 */
	public int largeServer() {
		int largeServer = sArr[0].id;
		for (int i = 0; i < sArr.length; i++) {
			if (sArr[i].coreCount > sArr[largeServer].coreCount) {
				largeServer = sArr[i].id;
			}
		}
		return largeServer;
	}

	public static void main(String args[]) {
		Client ourClient = new Client("127.0.0.1", 50000);

		// Check for "-a" cmd argument and set algorithm type accordingly.
		if (args.length == 2) {
			if (args[0].equals("-a")) {
				if (args[1].equals("bf")) {
					ourClient.algorithmType = "bf";
				} else if (args[1].equals("wf")) {
					ourClient.algorithmType = "wf";
				} else if (args[1].equals("ff")) {
					ourClient.algorithmType = "ff";
				} else if (args[1].equals("sj")) {
					ourClient.algorithmType = "sj";
				}
			}
		}

		ourClient.run();
	}
}