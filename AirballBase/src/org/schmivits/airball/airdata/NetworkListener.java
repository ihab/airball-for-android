package org.schmivits.airball.airdata;

public abstract class NetworkListener implements Runnable {

    protected static final int TIMEOUT = 2000;  // milliseconds
    protected static final int BUFFER_SIZE = 4096;

    public interface PacketListener {
        void packetReceived(byte[] packet);
    }

    protected final int mPort;
    protected final PacketListener mListener;
    protected boolean mRunning = true;

    public NetworkListener(int port, PacketListener listener) {
        mPort = port;
        mListener = listener;
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        mRunning = false;
    }
}
