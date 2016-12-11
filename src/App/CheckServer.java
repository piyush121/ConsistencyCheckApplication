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
	private static int PORT = 9091; // default
	private static TTransport transport;
	private static TProtocol protocol;
	private static KVStore.Client client;

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

	public static void addTimeEdge(Map<Command, ArrayList<Command>> adjList, HashMap<String, Command> trackWritesMap,
			ArrayList<Command> list) {
		ArrayList<Command> incStartTimeCommand = new ArrayList<>();
		ArrayList<Command> decEndTimeCommand = new ArrayList<>();
		// Tracking all the writes
		for (Command cmd : list) {
			if (cmd.requestType.equals("kvset"))
				trackWritesMap.put(cmd.value, cmd);
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
			int time = -1;
			for (Command cmd2 : decEndTimeCommand) {
				if (cmd2.startTime > cmd1.endTime) {
					if (time < cmd2.endTime) {
						adjList.get(cmd2).add(cmd1);
						time = Math.max(time, cmd2.startTime);
					} else
						break;
				}
			}

		}

	}

	public static void addDataEdge(Map<Command, ArrayList<Command>> adjList, HashMap<String, Command> trackWritesMap,
		ArrayList<Command> list) {
		for (Command cmd : list) {
			if (cmd.requestType.equals("kvget"))
				adjList.get(trackWritesMap.get(cmd.value)).add(cmd); // add data edge in the main adjacency list.
		}
	}
	
	public static void addHybridEdge(Map<Command, ArrayList<Command>> adjList, HashMap<String, Command> trackWritesMap,
			ArrayList<Command> list) {
		for (Command cmd1 : list) {
			if (cmd1.value.equals("kvset")) {
				for (Command cmd2 : list) {
					if (cmd2.value.equals("kvget") && pathExist(cmd1, cmd2, adjList)) {
						Command temp = trackWritesMap.get(cmd2.value);
						adjList.get(cmd1).add(temp);
					}
				}
			}
		}
	}
	
	public static boolean pathExist(Command cmd1, Command cmd2, Map<Command, ArrayList<Command>> adjList) {
		if (cmd1 == null || cmd2 == null) // BFS implementation.
			return false;
		HashSet<Command> visited = new HashSet<>();
		Queue<Command> que = new LinkedList<>();
		que.add(cmd1);

		while (!que.isEmpty()) {
			Command cmd = que.poll();
			if (visited.contains(cmd))
				return false;
			
			visited.add(cmd);
			for (Command cmd3 : adjList.get(cmd)) {
				if (cmd.equals(cmd2))
					return true;
				que.add(cmd3);
				
			}
		}

		return false;
	}
	
	public static boolean containsCycle(Map<Command, ArrayList<Command>> adjList) {
		for (Command node : adjList.keySet()) {
	        final ArrayList<Command> directNeighbors = adjList.get(node);
	        for (Command directNeighbor : directNeighbors) {
	        	ArrayList<Command> next = adjList.get(directNeighbor);
	            while (next != null) {
	                for (Command n : next) {
	                	next = adjList.get(n);
	                    if (next != null && (next.contains(n) || next.contains(node))) {
	                        return true;
	                    }
	                }
	            }
	        }
	    }
	    return false;
	}

	public static void main(String[] args) {

		try {
			HOST = "localhost";
			PORT = 5000;
			transport = new TSocket(HOST, PORT, 5000);// Will timeout after
														// 5 secs.
			transport.open();
			protocol = new TBinaryProtocol(transport);
			client = new KVStore.Client(protocol);
			ArrayList<Command> list = new ArrayList<>();
			// create 255 Thread using for loop
			for (int x = 0; x < 256; x++) {
				// Create Thread class and start accumulating logs in the list.
				TimeServer timeObject = new TimeServer();
				KVStore.Client kvc_obj = new KVStore.Client(protocol);
				MyThread client = new MyThread(timeObject, kvc_obj, list, x);
				client.start();
			}
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
}
