/**
 * 
 */
package App;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;

import App.ErrorCode;
import App.KVStore;
import App.Result;

/**
 * This file will invoke all the clients and check the given server for the
 * Safety, Regularity and Atomicity of the stored values.
 *
 */
public class CheckServer {

	private static String HOST = "localhost";
	private static int PORT = 5000; // default

	/*
	 * HOST = command[1].split(":")[0]; PORT =
	 * Integer.parseInt(command[1].split(":")[1]); transport = new TSocket(HOST,
	 * PORT, 5000);// Will timeout after // 5 secs. transport.open(); protocol =
	 * new TBinaryProtocol(transport); client = new KVStore.Client(protocol);
	 */

	/**
	 * Class kvclient is used for creating an object for a client. Using this
	 * object, command posted by the buggy server is read and it's result is
	 * then used to create a thread for the client.
	 */

	public static void addTimeEdge(Map<Command, ArrayList<Command>> adjList, Map<String, Command> trackWritesMap,
			List<Command> list) {

		System.out.println("Adding Time Edge");
		ArrayList<Command> incStartTimeCommand = new ArrayList<>();
		ArrayList<Command> decEndTimeCommand = new ArrayList<>();

		// Tracking all the writes
		for (Command cmd : list) {
			if (cmd.requestType.equals("kvset"))
				trackWritesMap.put(cmd.value, cmd); // Who wrote this value.
			adjList.put(cmd, new ArrayList<Command>()); // add all vertex to the
														// adjacency list!
		}
		// Sort by Increasing start time.
		Collections.sort(list, new Comparator<Command>() {
			public int compare(Command cmd1, Command cmd2) {
				return cmd1.startTime - cmd2.startTime;
			}
		});
		incStartTimeCommand.addAll(list);
		// Sort by decreasing end time
		Collections.sort(list, new Comparator<Command>() {
			public int compare(Command cmd1, Command cmd2) {
				return cmd2.endTime - cmd1.endTime;
			}
		});
		decEndTimeCommand.addAll(list);

		for (Command cmd1 : incStartTimeCommand) {
			int time = Integer.MIN_VALUE;
			for (Command cmd2 : decEndTimeCommand) {
				if (cmd2.endTime < cmd1.startTime) {
					if (time < cmd2.endTime) {
						adjList.get(cmd2).add(cmd1);
						time = Math.max(time, cmd2.startTime);

					} else
						break;
				}
			}

		}

	}

	public static void addDataEdge(Map<Command, ArrayList<Command>> adjList, Map<String, Command> trackWritesMap,
			List<Command> list) {
		System.out.println("Adding Data Edge");
		for (Command cmd : list) {
			if (cmd.requestType.equals("kvget")) {
				Command writer = trackWritesMap.get(cmd.value);
				// System.out.println(cmd.value);
				adjList.get(writer).add(cmd); // add data edge in the main
												// adjacency list.

			}
		}
	}

	public static void addHybridEdge(Map<Command, ArrayList<Command>> adjList, Map<String, Command> trackWritesMap,
			List<Command> list) {
		System.out.println("Adding Hybrid Edge");
		HashMap<Command, ArrayList<Command>> adjList1 = new HashMap<>();
		adjList1.putAll(adjList);
		for (Command cmd1 : list) {
			if (cmd1.requestType.equals("kvset")) {
				for (Command cmd2 : list) {
					if (cmd2.requestType.equals("kvget") && pathExist(cmd1, cmd2, adjList)) {
						Command temp = trackWritesMap.get(cmd2.value);
						if (!temp.equals(cmd1)) {
							adjList1.get(cmd1).add(temp);

						}
					}
				}
			}
		}
		adjList = adjList1; // Because we need to find path which consists of
							// only data and time edge.
	}

	public static boolean pathExist(Command cmd1, Command cmd2, Map<Command, ArrayList<Command>> adjList) {
		if (cmd1 == null || cmd2 == null) // BFS implementation.
			return false;
		HashSet<Command> visited = new HashSet<>();
		Queue<Command> que = new LinkedList<>();
		que.add(cmd1);
		while (!que.isEmpty()) {
			Command cmd = que.poll();
			visited.add(cmd);
			for (Command cmd3 : adjList.get(cmd)) {
				if (cmd3.equals(cmd2)) {
					return true;
				}
				if (!visited.contains(cmd3))
					que.add(cmd3);
			}
		}
		return false;
	}

	public static boolean containsCycle(Map<Command, ArrayList<Command>> adjList) {
		for (Command node : adjList.keySet()) {
			if (containsCycle(adjList, new HashSet<Command>(), new HashSet<Command>(), node))
				return true;
		}
		return false;
	}

	public static boolean containsCycle(Map<Command, ArrayList<Command>> adjList, Set<Command> visited,
			Set<Command> stacked, Command startNode) {

		visited.add(startNode);
		stacked.add(startNode);

		for (Command next : adjList.get(startNode)) {
			if (!visited.contains(next)) {
				containsCycle(adjList, visited, stacked, next);
			} else if (stacked.contains(next)) {
				return true;
			}
		}
		stacked.remove(startNode);
		return false;

	}

	public static void main(String[] args) throws InterruptedException {

		List<Command> list = Collections.synchronizedList(new ArrayList<>());
		Map<Command, ArrayList<Command>> adjList = new HashMap<>();
		Map<String, Command> trackWritesMap = new HashMap<>();
		HOST = args[1].split(":")[0];
		PORT = Integer.valueOf(args[1].split(":")[1]);
		TTransport transport = new TSocket(HOST, PORT, 15000);
		try {
			transport.open();
		} catch (TTransportException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		TProtocol protocol = new TBinaryProtocol(transport);
		KVStore.Client client = new KVStore.Client(protocol);
		try {
			client.kvset("1", "-1");// Adding default value;
			Command obj = new Command(6000, 6001, "kvset", "1", "-1");
			// adjList.put(obj, new ArrayList<>());
			// trackWritesMap.put("-1", obj);
			list.add(obj);
		} catch (TException e1) {
			e1.printStackTrace();
		}
		transport.close();

		// create 255 Thread using for loop
		Thread[] clients = new Thread[10];
		for (int x = 0; x < clients.length; x++) {
			// Create Thread class and start accumulating logs in the list.
			clients[x] = new MyThread(list, "localhost", 5000, x + 1);
			clients[x].start();
		}
		for (int x = 0; x < clients.length; x++) {
			clients[x].join();
		}

		System.out.println("Logs: " + list.size());

		addTimeEdge(adjList, trackWritesMap, list);
		addDataEdge(adjList, trackWritesMap, list);
		addHybridEdge(adjList, trackWritesMap, list);

		System.out.println("Detecting cycle");
		if (containsCycle(adjList)) {
			System.out.println("cycle found");
			System.exit(1);
		} else {
			System.out.println("cycle not found");
			System.exit(0);
		}
	}
}
