package ro.mpp2024.model;

public interface Identifiable<Tid> {
    Tid getID();
    void setID(Tid id);
}
