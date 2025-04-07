package turism.network.rpcprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.model.Client;
import turism.model.Excursie;
import turism.model.Rezervare;
import turism.model.User;
import turism.services.ITurismObserver;
import turism.services.ITurismServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TurismClientRpcWorker implements Runnable, ITurismObserver {
    private ITurismServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Logger logger = LogManager.getLogger(TurismClientRpcWorker.class);

    public TurismClientRpcWorker(ITurismServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                logger.debug("Received request from client: "+request);
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
                connected = false;
            }
            /*
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }

             */
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error "+e);
        }
    }

    public void rezervareReceived(Rezervare rezervare) {
        Response resp = new Response.Builder()
                .type(ResponseType.NEW_REZERVARE)
                .data(rezervare)
                .build();
        logger.debug("Rezervare Received: "+rezervare);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            logger.error("Error sending response "+e);
        }
    }

    public void clientReceived(Client client) {
        Response resp = new Response.Builder()
                .type(ResponseType.NEW_CLIENT)
                .data(client)
                .build();
        logger.debug("clientReceived: "+client);
        try {
            sendResponse(resp);
        } catch (IOException e) {
            logger.error("Error sending response "+e);
        }
    }

    private static Response okResponse = new Response.Builder()
            .type(ResponseType.OK)
            .data(null)
            .build();

    private Response handleRequest(Request request) {
        Response response = null;
        if (request.type() == RequestType.LOGIN) {
            User user = (User) request.data();
            try {
                User user1 = server.login(user, this);
                response = new Response.Builder()
                        .type(ResponseType.OK)
                        .data(user1)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
                //connected = false;
            }
        } else if (request.type() == RequestType.LOGOUT) {
            User user = (User) request.data();
            try {
                server.logout(user, this);
                connected = false;
                logger.info("Logout response sent {}", connected);
                return okResponse;
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if(request.type() == RequestType.SEND_REZERVARE){
            logger.debug("SEND_REZERVARE request");
            List<Object> params = (List<Object>) request.data();
            Rezervare rezervare;
            try {
                rezervare = server.addRezervare((Excursie) params.get(0), (Client) params.get(1), (int) params.get(2), (User) params.get(3));
                response = new Response.Builder()
                        .type(ResponseType.NEW_REZERVARE)
                        .data(rezervare)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if(request.type() == RequestType.GET_REZERVARI_BY_EXCURSIE){
            Excursie excursie = (Excursie) request.data();
            try {
                List<Rezervare> rezervari = server.getRezervariByExcursie(excursie);
                response = new Response.Builder()
                        .type(ResponseType.OK)
                        .data(rezervari)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if(request.type() == RequestType.GET_EXCURSII){
            try {
                List<Excursie> excursies = server.getAllExcursie();
                response = new Response.Builder()
                        .type(ResponseType.OK)
                        .data(excursies)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if (request.type() == RequestType.GET_EXCURSII_BY_DESTINATION_AND_DATE) {
            try {
                List<Object> params = (List<Object>) request.data();

                List<Excursie> excursies = server.getAllExcursieByDestinationAndDate(params.get(0).toString(), (LocalDateTime) params.get(1), (LocalDateTime) params.get(2));
                response = new Response.Builder()
                        .type(ResponseType.OK)
                        .data(excursies)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if(request.type() == RequestType.GET_LOCURI_OCUPATE){
            Excursie excursie = (Excursie) request.data();
            try {
                int ocupate = server.getLocuriOcupateForExcursie(excursie);
                response = new Response.Builder()
                        .type(ResponseType.OK)
                        .data(ocupate)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if(request.type() == RequestType.SEND_CLIENT){
            try {
                Client client = server.addClient((Client) request.data());
                response = new Response.Builder()
                        .type(ResponseType.NEW_CLIENT)
                        .data(client)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        } else if (request.type() == RequestType.GET_CLIENT_BY_NAME_AND_PHONE) {
            try {
                List<Object> params = (List<Object>) request.data();
                Client client = server.findClientByNameAndPhoneNumber(params.get(0).toString(), params.get(1).toString());
                response = new Response.Builder()
                        .type(ResponseType.OK)
                        .data(client)
                        .build();
            } catch (Exception e) {
                logger.error(e);
                response = new Response.Builder()
                        .type(ResponseType.ERROR)
                        .data(e.getMessage())
                        .build();
            }
        }
        return response;
    }


    private void sendResponse(Response response) throws IOException{
        logger.debug("sending response "+response);
        synchronized (output) {
            output.writeObject(response);
            output.flush();
        }
    }
}
