package com.google.airball.airdata;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import android.util.Log;

public class UdpListener extends NetworkListener {

    private static final String TAG = UdpListener.class.getName();

    public UdpListener(int port, PacketListener listener) {
        super(port, listener);
    }

    @Override
    public void run() {
        final DatagramSocket s;

        try {
            s = new DatagramSocket(mPort);
        } catch (SocketException e) {
            Log.e(TAG, "socket setup threw", e);
            return;
        }

        try {
            s.setSoTimeout(TIMEOUT);

            final byte[] buffer = new byte[BUFFER_SIZE];
            final DatagramPacket p = new DatagramPacket(buffer, buffer.length);

            while (mRunning) {
                try {
                    s.receive(p);
                    mListener.packetReceived(
                            Arrays.copyOfRange(buffer, p.getOffset(), p.getLength()));
                } catch (SocketTimeoutException e) {
                    // On exception, we keep trying in the loop until we are asked
                    // to stop() explicitly.
                } catch (IOException e) {
                    break;
                }
            }
        } catch (SocketException e) {
            // Fall through
        } finally {
            s.close();
        }
    }
}
