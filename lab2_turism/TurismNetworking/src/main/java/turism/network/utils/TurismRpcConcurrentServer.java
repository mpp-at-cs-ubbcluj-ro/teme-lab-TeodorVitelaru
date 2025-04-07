package turism.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.network.rpcprotocol.TurismClientRpcWorker;
import turism.services.ITurismServices;

import java.net.Socket;

public class TurismRpcConcurrentServer extends AbsConcurrentServer {
    private ITurismServices chatServer;
    private static Logger logger = LogManager.getLogger(TurismRpcConcurrentServer.class);
    public TurismRpcConcurrentServer(int port, ITurismServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        logger.info("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        // ChatClientRpcWorker worker=new ChatClientRpcWorker(chatServer, client);
        TurismClientRpcWorker worker=new TurismClientRpcWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        logger.info("Stopping services ...");
    }
}

