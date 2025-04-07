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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TurismServicesRpcProxy implements ITurismServices {
    private String host;
    private int port;

    private static Logger logger = LogManager.getLogger(TurismServicesRpcProxy.class);

    private ITurismObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public TurismServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }


    public Client addClient(Client client) throws Exception {
        //initializeConnection();
        Request request=new Request.Builder().type(RequestType.SEND_CLIENT).data(client).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            closeConnection();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.NEW_CLIENT){
            Client client1=(Client)response.data();
            return client1;
        }
        throw new Exception("Error adding client");
    }


    public Client findClientByNameAndPhoneNumber(String name, String phoneNumber) throws Exception {
        //initializeConnection();
        List<String> data=List.of(name,phoneNumber);
        Request request=new Request.Builder().type(RequestType.GET_CLIENT_BY_NAME_AND_PHONE).data(data).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.OK){
            Client client=(Client)response.data();
            return client;
        }
        throw new Exception("Error getting client");
    }


    public List<Excursie> getAllExcursieByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2) throws Exception {
        //initializeConnection();
        List<Object> data=List.of(destination,date1,date2);
        Request request=new Request.Builder().type(RequestType.GET_EXCURSII_BY_DESTINATION_AND_DATE).data(data).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.OK){
            List<Excursie> excursii=(List<Excursie>)response.data();
            return excursii;
        }
        throw new Exception("Error getting excursii");
    }

    public List<Excursie> getAllExcursie() throws Exception {
        logger.debug("Getting all excursii");
        initializeConnection();
        Request request=new Request.Builder().type(RequestType.GET_EXCURSII).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.OK){
            List<Excursie> excursii=(List<Excursie>)response.data();
            logger.debug("Got all excursii {}",excursii);
            return excursii;
        }

        logger.debug("Error getting all excursii {}", response);
        throw new Exception("Error getting excursii");

    }


    public List<Rezervare> getRezervariByExcursie(Excursie excursie) throws Exception {
        //initializeConnection();
        Request request=new Request.Builder().type(RequestType.GET_REZERVARI_BY_EXCURSIE).data(excursie).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.OK){
            List<Rezervare> rezervari=(List<Rezervare>)response.data();
            return rezervari;
        }
        throw new Exception("Error getting rezervari");
    }

    public List<Rezervare> getAllRezervari() throws Exception {
        return null;
    }


    public int getLocuriOcupateForExcursie(Excursie excursie) throws Exception {
        //initializeConnection();
        Request request=new Request.Builder().type(RequestType.GET_LOCURI_OCUPATE).data(excursie).build();
        sendRequest(request);
        logger.info("sending request for locuri ocupate {}",request);
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.OK){
            int locuriOcupate=(int)response.data();
            return locuriOcupate;
        }
        throw new Exception("Error getting locuri ocupate");
    }


    public Rezervare addRezervare(Excursie excursie, Client client, int nrBilete, User user) throws Exception {
        //initializeConnection();
        List<Object> data=List.of(excursie,client,nrBilete,user);
        Request request=new Request.Builder().type(RequestType.SEND_REZERVARE).data(data).build();
        sendRequest(request);
        /*
        Response response=readResponse();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
        if (response.type()==ResponseType.NEW_REZERVARE){
            Rezervare rezervare=(Rezervare)response.data();
            logger.debug("Rezervare added in proxy{}",rezervare);
            return rezervare;
        }
        logger.info("Response type: {}", response.type());
        throw new Exception("Error adding rezervare");

         */
        return null;
    }

    public User login(User user, ITurismObserver client) throws Exception {
        initializeConnection();
        Request request=new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response=readResponse();
        if (response.type()==ResponseType.OK){
            this.client=client;
            return (User)response.data();
        }
        if (response.type()==ResponseType.ERROR){
           String err = (String)response.data();
            closeConnection();
            throw new Exception(err);
        }
        throw new Exception("Login error");
    }


    public void logout(User user, ITurismObserver client) throws Exception {
        Request request=new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(request);
        Response response=readResponse();
        closeConnection();
        if (response.type()==ResponseType.ERROR){
            String err = (String)response.data();
            throw new Exception(err);
        }
    }

    private void closeConnection() {
        logger.debug("Closing connection");
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }

    }

    private void sendRequest(Request request)throws Exception {
        logger.debug("Sending request {} ",request);
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private Response readResponse() throws Exception {
        Response response=null;
        logger.debug("Waiting for response");
        try{
            response=qresponses.take();
            logger.debug("response received in proxy "+response);
        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }
    private void initializeConnection() throws Exception {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            logger.error("Error initializing connection "+e);
            logger.error(e.getStackTrace());
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response){
        if(response.type() == ResponseType.NEW_REZERVARE){
            Rezervare rezervare = (Rezervare) response.data();
            logger.debug("New rezervare received "+rezervare);
            try {
                client.rezervareReceived(rezervare);
            } catch (Exception e) {
                logger.error("Error notifying client about new rezervare "+e);
            }
        }
    }


    private boolean isUpdate(Response response){
        return response.type()== ResponseType.NEW_REZERVARE;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    logger.debug("response received "+response);
                    try {
                        if(isUpdate((Response)response)) {
                            handleUpdate((Response) response);
                        } else {
                            qresponses.put((Response) response);
                        }

                    } catch (InterruptedException e) {
                        logger.error(e);
                        logger.error(e.getStackTrace());
                        Thread.currentThread().interrupt();
                    }
                } catch (IOException|ClassNotFoundException e) {
                    logger.error("Reading error "+e);
                    finished = true;
                }
            }
        }
    }
}
