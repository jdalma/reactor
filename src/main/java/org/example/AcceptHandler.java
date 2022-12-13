package org.example;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptHandler implements Handler {
    final Selector selector;
    final ServerSocketChannel serverSocketChannel;

    public AcceptHandler(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void handle() {
        try {
            final SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                new EchoHandler(selector, socketChannel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
