package TurismLab.proxy;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.model.Client;
import turism.model.Excursie;
import turism.model.Rezervare;
import turism.model.User;
import turism.network.grpc.*;
import turism.services.ITurismObserver;
import turism.services.ITurismServices;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TurismServicesGrpcProxy implements ITurismServices, AutoCloseable {
    private String host;
    private int port;

    private static Logger logger = LogManager.getLogger(TurismServicesGrpcProxy.class);

    private ManagedChannel channel;
    private TurismServiceGrpc.TurismServiceBlockingStub blockingStub;
    private TurismServiceGrpc.TurismServiceStub asyncStub;

    private turism.model.User currentUser;
    private volatile boolean finished;
    private final AtomicBoolean isSubscribed = new AtomicBoolean(false);
    private ITurismObserver observer;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TurismServicesGrpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        initializeGrpcConnection();
    }

    private void initializeGrpcConnection() {
        logger.debug("Initializing gRPC connection to {}:{}", host, port);
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = TurismServiceGrpc.newBlockingStub(channel);
        asyncStub = TurismServiceGrpc.newStub(channel);
        finished = false;
    }

    public void shutdown() throws InterruptedException {
        logger.debug("Shutting down gRPC connection");
        finished = true;
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public Client addClient(Client client) throws Exception {
        logger.debug("Adding client: {}", client);

        ClientRequest request = ClientRequest.newBuilder()
                .setClient(convertToGrpcClient(client))
                .build();

        ClientResponse response = blockingStub.addClient(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }

        return convertFromGrpcClient(response.getClient());
    }

    @Override
    public Client findClientByNameAndPhoneNumber(String name, String phoneNumber) throws Exception {
        logger.debug("Finding client by name: {} and phone: {}", name, phoneNumber);

        ClientSearchRequest request = ClientSearchRequest.newBuilder()
                .setName(name)
                .setPhoneNumber(phoneNumber)
                .build();

        ClientResponse response = blockingStub.findClientByNameAndPhone(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }

        return convertFromGrpcClient(response.getClient());
    }

    @Override
    public List<Excursie> getAllExcursieByDestinationAndDate(String destination, LocalDateTime date1, LocalDateTime date2) throws Exception {
        logger.debug("Getting excursii by destination: {} and dates: {} to {}", destination, date1, date2);

        DestinationDateRequest request = DestinationDateRequest.newBuilder()
                .setDestination(destination)
                .setDate1(date1.format(formatter))
                .setDate2(date2.format(formatter))
                .build();

        ExcursiiResponse response = blockingStub.getExcursiiByDestinationAndDate(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }
        logger.info("S a primiti cu succes response-ul: " + response.getExcursiiList().size());
        List<Excursie> result = new ArrayList<>();
        for (turism.network.grpc.Excursie excursieGrpc : response.getExcursiiList()) {
            System.out.println("S a primit excursia:" + excursieGrpc.getId());
            result.add(convertFromGrpcExcursie(excursieGrpc));
        }

        return result;
    }

    @Override
    public List<Excursie> getAllExcursie() throws Exception {
        logger.debug("Getting all excursii");

        Empty request = Empty.newBuilder().build();
        ExcursiiResponse response = blockingStub.getAllExcursii(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }
        List<Excursie> result = new ArrayList<>();
        for (turism.network.grpc.Excursie excursieGrpc : response.getExcursiiList()) {
            Excursie excursie = convertFromGrpcExcursie(excursieGrpc);
            result.add(excursie);
        }
        logger.info("Number of excursii: {}", result.size());
        return result;
    }

    @Override
    public List<Rezervare> getRezervariByExcursie(Excursie excursie) throws Exception {
        logger.debug("Getting rezervari for excursie: {}", excursie);

        ExcursieRequest request = ExcursieRequest.newBuilder()
                .setExcursieId(excursie.getId().toString())
                .build();

        RezervariResponse response = blockingStub.getRezervariByExcursie(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }

        List<Rezervare> result = new ArrayList<>();
        for (turism.network.grpc.Rezervare rezervareGrpc : response.getRezervariList()) {
            result.add(convertFromGrpcRezervare(rezervareGrpc));
        }

        return result;
    }

    @Override
    public List<Rezervare> getAllRezervari() throws Exception {
        // This method is not implemented in the proto definition
        // If needed, you would need to add it to the proto file and recompile
        logger.warn("getAllRezervari() not implemented in gRPC service");
        return new ArrayList<>();
    }

    @Override
    public int getLocuriOcupateForExcursie(Excursie excursie) throws Exception {
        logger.debug("Getting locuri ocupate for excursie: {}", excursie);

        ExcursieRequest request = ExcursieRequest.newBuilder()
                .setExcursieId(excursie.getId().toString())
                .build();

        LocuriOcupateResponse response = blockingStub.getLocuriOcupate(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }

        return response.getLocuriOcupate();
    }

    @Override
    public Rezervare addRezervare(Excursie excursie, Client client, int nrBilete, User user) throws Exception {
        logger.debug("Adding rezervare: excursie={}, client={}, nrBilete={}, user={}", excursie, client, nrBilete, user);

        RezervareRequest request = RezervareRequest.newBuilder()
                .setExcursieId(excursie.getId().toString())
                .setClient(convertToGrpcClient(client))
                .setNrBilete(nrBilete)
                .setUser(createUserRequest(currentUser))
                .build();

        RezervareResponse response = blockingStub.addRezervare(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }

        return convertFromGrpcRezervare(response.getRezervare());
    }

    public UserRequest createUserRequest(User user) {
        return UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();
    }

    @Override
    public User login(User user, ITurismObserver client) throws Exception {
        logger.debug("Login attempt for user: {}", user.getUsername());
        this.observer = client;

        UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();

        UserResponse response = blockingStub.login(request);

        if (!response.getSuccess()) {
            throw new Exception(response.getErrorMessage());
        }

        this.currentUser = convertFromGrpcUser(response);
        // Start listening for notifications
        startNotificationSubscription(this.currentUser.getId());


        return this.currentUser;
    }

    @Override
    public void logout(User user, ITurismObserver client) throws Exception {
        logger.debug("Logout for user: {}", user.getUsername());

        UserRequest request = UserRequest.newBuilder()
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .build();

        Empty response = blockingStub.logout(request);

        // Close the connection
        this.observer = null;
        this.currentUser = null;

        try {
            shutdown();
        } catch (InterruptedException e) {
            logger.error("Error during shutdown: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void subscribeToNotifications() {
        if (currentUser == null) {
            logger.warn("Cannot subscribe to notifications without a logged in user");
            return;
        }

        logger.debug("Subscribing to notifications for user: {}", currentUser.getUsername());

        NotificationRequest request = NotificationRequest.newBuilder()
                .setUserId(currentUser.getId().toString())
                .build();

        asyncStub.subscribeToNotifications(request, new StreamObserver<Notification>() {
            @Override
            public void onNext(Notification notification) {
                try {
                    processNotification(notification);
                } catch (Exception e) {
                    logger.error("Error processing notification: {}", e.getMessage());
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Error in notification stream: {}", t.getMessage());
                if (!finished) {
                    // Try to reconnect
                    try {
                        Thread.sleep(2000); // Wait before trying to reconnect
                        subscribeToNotifications();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            @Override
            public void onCompleted() {
                logger.debug("Notification stream completed");
            }
        });
    }

    private void processNotification(Notification notification) throws Exception {
        logger.debug("Received notification: {}", notification);

        if (observer == null) {
            logger.warn("No client registered for notifications");
            return;
        }


        switch (notification.getType()) {
            case NEW_REZERVARE:
                if (notification.hasRezervare()) {
                    logger.info("Received new rezervare notification in proxy");
                    Rezervare rezervare = convertFromGrpcRezervare(notification.getRezervare());
                    observer.rezervareReceived(rezervare);
                }
                break;

            case NEW_CLIENT:
                if (notification.hasClient()) {
                    Client newClient = convertFromGrpcClient(notification.getClient());
                    observer.clientReceived(newClient);
                }
                break;

            default:
                logger.warn("Unknown notification type: {}", notification.getType());
        }
    }

    // Conversion methods
    private turism.network.grpc.Client convertToGrpcClient(Client client) {
        turism.network.grpc.Client.Builder builder = turism.network.grpc.Client.newBuilder()
                .setName(client.getNume())
                .setPhoneNumber(client.getTelefon());

        if (client.getId() != null) {
            builder.setId(client.getId().toString());
        }

        return builder.build();
    }

    private Client convertFromGrpcClient(turism.network.grpc.Client grpcClient) {
        Client client = new Client(grpcClient.getName(), grpcClient.getPhoneNumber());

        if (!grpcClient.getId().isEmpty()) {
            client.setId(Long.parseLong(grpcClient.getId()));
        }

        return client;
    }

    private turism.model.Excursie convertFromGrpcExcursie(turism.network.grpc.Excursie grpcExcursie) {
        // Note: The grpcExcursie.getDataPlecare() returns a string in ISO format
        logger.info("Converting gRPC Excursie to model Excursie");
        System.out.println("Data plecarii este: "+ grpcExcursie.getDataPlecare());
        try{
            LocalDateTime dataPlecarii = LocalDateTime.parse(grpcExcursie.getDataPlecare(), formatter);

            Excursie excursie = new Excursie(
                    grpcExcursie.getObiectiv(),
                    grpcExcursie.getFirmaTransport(),
                    dataPlecarii,
                    grpcExcursie.getNrLocuri(),
                    grpcExcursie.getPret()
            );
            if (!grpcExcursie.getId().isEmpty()) {
                excursie.setId(Long.parseLong(grpcExcursie.getId()));
            }
            logger.info("Converted Excursie: {}", excursie.getId());
            return excursie;
        } catch (Exception e){
            logger.error("Error converting gRPC Excursie: {}", e.getMessage());
            throw new RuntimeException("Error converting gRPC Excursie: " + e.getMessage());
        }



    }

    private Rezervare convertFromGrpcRezervare(turism.network.grpc.Rezervare grpcRezervare){
        // This requires fetching the related entities
        // For a full implementation, we might need to cache these entities or perform additional requests

        // For simplicity, we'll create skeleton objects with just the IDs
        // In a real implementation, you would want to fetch the complete objects

        Excursie excursie = new Excursie("", "", LocalDateTime.now(), 0, 0);
        excursie.setId(Long.parseLong(grpcRezervare.getExcursieId()));

        Client client = new Client("", "");
        client.setId(Long.parseLong(grpcRezervare.getClient().getId()));

        User user = new User("", "");
        user.setId(Long.parseLong(grpcRezervare.getUserId()));

        Rezervare rezervare = new Rezervare(excursie, client, grpcRezervare.getNrBilete(), user);

        if (!grpcRezervare.getId().isEmpty()) {
            rezervare.setId(Long.parseLong(grpcRezervare.getId()));
        }

        return rezervare;
    }

    private turism.model.User convertFromGrpcUser(UserResponse userResponse) {
        User user = new User(userResponse.getUsername(), userResponse.getPassword());
        user.setId(Long.parseLong(userResponse.getId()));
        System.out.println("User id este: " + user.getId());
        return user;
    }

    private void startNotificationSubscription(long userId) {
        logger.info("Starting notification subscription for user id: " + userId);

        // Cancel any existing subscription
        stopNotificationSubscription();

        // Set the subscribed flag to true
        isSubscribed.set(true);

        // Create subscription request
        NotificationRequest request =  NotificationRequest.newBuilder()
                .setUserId(String.valueOf(userId))
                .build();

        // Start the notification stream
        asyncStub.subscribeToNotifications(request, new StreamObserver<Notification>() {
            @Override
            public void onNext(Notification notification) {
                try{
                    processNotification(notification);
                } catch (Exception e){
                    logger.error("Error processing notification: " + e.getMessage(), e);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (!isSubscribed.get()) {
                    logger.info("Notification subscription was cancelled");
                } else {
                    logger.error("Error in notification subscription: " + throwable.getMessage(), throwable);
                }
            }

            @Override
            public void onCompleted() {
                logger.info("Notification subscription completed");
            }
        });
    }

    private void stopNotificationSubscription() {
        if (isSubscribed.getAndSet(false)) {
            logger.info("Stopping notification subscription");
            // The flag will cause the error handler to ignore errors
        }
    }


    @Override
    public void close() {
        stopNotificationSubscription();
        if (channel != null) {
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Error shutting down channel: " + e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }
}