package turism.services;

import turism.model.Client;
import turism.model.Rezervare;

public interface ITurismObserver {
    void rezervareReceived(Rezervare rezervare);
    void clientReceived(Client client);

}
