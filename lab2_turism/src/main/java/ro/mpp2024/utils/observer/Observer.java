package ro.mpp2024.utils.observer;

import ro.mpp2024.utils.events.Event;

public interface Observer <E extends Event>{
    void update(E e);
}
