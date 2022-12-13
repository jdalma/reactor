package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class EchoHandler implements Handler {
    static final int READING = 0, SENDING = 1;

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    final ByteBuffer buffer = ByteBuffer.allocate(256);
    int state = READING;

    public EchoHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);

        this.selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        this.selectionKey.attach(this);
        selector.wakeup();
    }


    @Override
    public void handle() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void read() throws IOException {
        int readCount = socketChannel.read(buffer);
        if (readCount > 0) {
            buffer.flip();
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        state = SENDING;
    }

    void send() throws IOException {
        socketChannel.write(buffer);
        buffer.clear();
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READING;
    }
}
