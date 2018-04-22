package distributed_systems.jgroups.map;

import org.jgroups.Address;

import java.io.Serializable;

public class MapUpdateMessage implements Serializable {
    private final MapOperation mapOperation;
    private final String key;
    private final String value;
    private final Address address;

    public MapUpdateMessage() {
        mapOperation = null;
        key = null;
        value = null;
        address = null;
    }

    public MapUpdateMessage(MapOperation mapOperation, String key) {
        this.mapOperation = mapOperation;
        this.key = key;
        value = null;
        address = null;
    }

    public MapUpdateMessage(MapOperation mapOperation, String key, String value) {
        this.mapOperation = mapOperation;
        this.key = key;
        this.value = value;
        address = null;
    }

    public MapUpdateMessage(MapOperation mapOperation, Address address) {
        this.mapOperation = mapOperation;
        this.address = address;
        key = null;
        value = null;
    }

    public MapOperation getMapOperation() {
        return mapOperation;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Address getAddress() {
        return address;
    }
}
