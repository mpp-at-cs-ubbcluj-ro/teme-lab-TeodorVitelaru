package turism.model;

import java.io.Serializable;

public class MyEntity<ID> implements Serializable {

    private ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}
