package turism.services;

import turism.model.Client;
import turism.model.Rezervare;

public interface ITurismObserver {
    void rezervareReceived(Rezervare rezervare) throws Exception;
    void clientReceived(Client client) throws Exception;
}
