package turism.client.utils.events;
public class EntityChangeEvent<T> implements Event {
    private ChangeEventType changeEventType;
    private T data, oldData;

    public EntityChangeEvent(ChangeEventType changeEventType, T entity) {
        this.changeEventType = changeEventType;
        this.data = entity;
    }

    public EntityChangeEvent(ChangeEventType changeEventType, T entity, T oldEntity) {
        this.changeEventType = changeEventType;
        this.data = entity;
        this.oldData = oldEntity;
    }

    public ChangeEventType getType() {
        return changeEventType;
    }

    public T getData() {
        return data;
    }

    public T getOldData() {
        return oldData;
    }
}
