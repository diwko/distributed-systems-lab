package distributed_systems.jgroups.map;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

import java.io.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistributedStringMap extends ReceiverAdapter implements SimpleStringMap {
    private final Logger log = Logger.getLogger(getClass().getName());
    private final String address;
    private final String clusterName = "DistributedMap";
    private JChannel channel;
    private Map<String, String> map = new HashMap<>();

    public DistributedStringMap(String address) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        this.address = address;
        this.channel = getChannel();
        channel.connect(clusterName);
        channel.getState(null, 10*1000);
    }

    private JChannel getChannel() throws Exception {
        JChannel channel = new JChannel(false);
        ProtocolStack stack = getProtocolStack();
        channel.setProtocolStack(stack);
        stack.init();
        channel.setReceiver(this);
        return channel;
    }

    private ProtocolStack getProtocolStack() throws Exception {
        ProtocolStack stack = new ProtocolStack();

        stack.addProtocol(new UDP().setValue("mcast_group_addr",InetAddress.getByName(address)))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE());

        return stack;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        channel.close();
        log.log(Level.INFO, "Closing channel");
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        synchronized (map) {
            Util.objectToStream(map, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        log.log(Level.INFO, "Set state");

        synchronized (map) {
            map = (Map<String, String>) Util.objectFromStream(new DataInputStream(input));
        }
    }

    @Override
    public void viewAccepted(View newView) {
        if(newView instanceof MergeView) {
            log.log(Level.INFO, "Merge View accepted: " + newView);

            /*new Thread(() -> {
                List<View> subgroups = ((MergeView)newView).getSubgroups();
                View primaryView = subgroups.get(0);
                Address localAddr = channel.getAddress();

                if(!primaryView.getMembers().contains(localAddr)) {
                    log.log(Level.INFO,"Not member of the new primary partition (" + primaryView + "), will re-acquire the state");
                    try {
                        channel.getState(primaryView.getCoord(), 10000);
                    }
                    catch(Exception ex) {
                        log.info("ERROR " + ex.getMessage());
                    }
                } else {
                    log.log(Level.INFO,"Member of the new primary partition (" + primaryView + "), will do nothing");
                }
            }).start();*/

            new Thread(() -> {
                List<View> subgroups = ((MergeView)newView).getSubgroups();
                View firstView = subgroups.get(0);
                Address localAddr = channel.getAddress();

                if (firstView.getCoord().equals(localAddr)) {
                    View primaryView = subgroups.get((int)Math.floor(Math.random() * subgroups.size()));
                    log.info("Primary view: " + primaryView);
                    MapUpdateMessage msg = new MapUpdateMessage(MapOperation.UPDATE_STATE, primaryView.getCoord());

                    subgroups
                            .stream()
                            .filter(view -> view != primaryView)
                            .forEach(view ->
                                view.forEach(addr -> {
                                    try {
                                        channel.send(addr, msg);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                            }));
                }
            }).start();

        } else {
            log.log(Level.INFO, "View accepted: " + newView);
        }
    }

    @Override
    public void receive(Message msg) {
        if(channel.getAddress().equals(msg.getSrc()))
            return;

        MapUpdateMessage mapUpdateMessage = (MapUpdateMessage) msg.getObject();
        log.log(Level.INFO, "Receive message type: " + mapUpdateMessage.getMapOperation());

        switch (mapUpdateMessage.getMapOperation()) {
            case PUT:
                synchronized (map) {
                    map.put(mapUpdateMessage.getKey(), mapUpdateMessage.getValue());
                }
                break;
            case REMOVE:
                synchronized (map) {
                    map.remove(mapUpdateMessage.getKey());
                }
                break;
            case UPDATE_STATE:
                synchronized (map) {
                    new Thread(() -> {
                        try {
                            channel.getState(mapUpdateMessage.getAddress(), 10000);
                        } catch (Exception e) {
                            log.info("ERROR " + e.getMessage());
                        }
                    }).start();
                }
                break;
        }
    }

    @Override
    public boolean containsKey(String key) {
        log.log(Level.INFO, "Contains - key: " + key);

        synchronized (map) {
            return map.containsKey(key);
        }
    }

    @Override
    public String get(String key) {
        log.log(Level.INFO, "Get - key: " + key);

        synchronized (map) {
            return map.get(key);
        }
    }

    @Override
    public String put(String key, String value) {
        log.log(Level.INFO, "Put - {" + key + ", " + value + "}");

        MapUpdateMessage msg = new MapUpdateMessage(MapOperation.PUT, key, value);

        synchronized (map) {
            try {
                channel.send(null, msg);
                return map.put(key, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String remove(String key) {
        log.log(Level.INFO, "Remove - key: " + key);

        MapUpdateMessage msg = new MapUpdateMessage(MapOperation.REMOVE, key);

        synchronized (map) {
            try {
                channel.send(null, msg);
                return map.remove(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
