package com.google.airball.airdata;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import android.util.Log;

public class XPlanePacketInterpreter implements NetworkListener.PacketListener {

    public class XPlaneUdpBlock {

        public int mIndex;

        public float[] mData;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{ [").append(mIndex).append("] ");
            for (int i = 0; i < mData.length; i++) {
                sb.append(mData[i]).append(" ");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public interface BlockReceiver {

        void blockReceived(XPlaneUdpBlock block);
    }

    private final BlockReceiver mReceiver;

    public XPlanePacketInterpreter(BlockReceiver receiver) {
        mReceiver = receiver;
    }

    @Override
    public void packetReceived(byte[] packet) {
        interpretPacket(packet);
    }

    private void interpretPacket(byte[] packet) {
        int start = 5;  // Skip header "DATAb"
        int numMessages = (packet.length - start) / 36;

        for (int i = 0; i < numMessages; i++) {
            XPlaneUdpBlock block = new XPlaneUdpBlock();
            block.mIndex = intFromByteArray(packet, start);
            start += 4;
            block.mData = new float[8];
            for (int j = 0; j < 8; j++) {
                block.mData[j] = floatFromByteArray(packet, start);
                start += 4;
            }
            mReceiver.blockReceived(block);
        }
    }

    private static int intFromByteArray(byte[] bytes, int start) {
        try {
            return ByteBuffer.wrap(Arrays.copyOfRange(bytes, start, start + 4))
                    .order(ByteOrder.LITTLE_ENDIAN).getInt();
        } catch (Exception e) {
            Log.e(XPlanePacketInterpreter.class.getName(), "Int number conversion", e);
            return 0;
        }
    }

    private static float floatFromByteArray(byte[] bytes, int start) {
        try {
            return ByteBuffer.wrap(Arrays.copyOfRange(bytes, start, start + 4))
                    .order(ByteOrder.LITTLE_ENDIAN).getFloat();
        } catch (Exception e) {
            Log.e(XPlanePacketInterpreter.class.getName(), "Float number conversion", e);
            return Float.NaN;
        }
    }
}
