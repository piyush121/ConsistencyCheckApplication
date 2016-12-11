package App;

import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class kvserver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int PORT = 5000;
		try {
			KVStoreServiceHandler handler_obj = new KVStoreServiceHandler();
			KVStore.Processor processor = new KVStore.Processor(handler_obj);
			TServerTransport serverTransport = new TServerSocket(PORT);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));
			System.out.println("Server Running on port " + PORT);
			server.serve();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
