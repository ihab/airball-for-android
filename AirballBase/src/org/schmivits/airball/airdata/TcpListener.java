package org.schmivits.airball.airdata;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class TcpListener extends NetworkListener {

    private static final String TAG = TcpListener.class.getName();

    private static final byte[] TERMINATOR = "airball".getBytes();

    public TcpListener(int port, PacketListener listener) {
        super(port, listener);
    }

    @Override
    public void run() {
        while (mRunning) {
            openSocketAndListen();
        }
    }

    private void openSocketAndListen() {
        try {
            ServerSocket serverSocket = new ServerSocket(mPort);
            Socket s = serverSocket.accept();

            try {

                s.setSoTimeout(TIMEOUT);
                byte[] buffer = new byte[BUFFER_SIZE];
                int pos = 0, tpos = 0;

                while (mRunning) {

                    try {

                        int b = s.getInputStream().read();
                        if (b == -1) { break; }

                        buffer[pos++] = (byte) b;

                        if (b != TERMINATOR[tpos++]) {
                            tpos = 0;
                        }

                        if (tpos == TERMINATOR.length) {
                            mListener.packetReceived(
                                    Arrays.copyOf(buffer, pos - TERMINATOR.length));
                            pos = tpos = 0;
                        }
                    } catch (SocketTimeoutException e) {
                        // On exception, we keep trying in the loop until we are asked
                        // to stop() explicitly.
                    } catch (IOException e) {
                        Log.e(TAG, "error reading socket", e);
                        break;
                    }
                }
            } finally {
                s.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error creating server socket", e);
        }
    }
}
