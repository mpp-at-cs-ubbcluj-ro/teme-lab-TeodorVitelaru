package turism.client.utils.observer;

import turism.client.utils.events.Event;

public interface Observer <E extends Event>{
    void update(E e);
}
