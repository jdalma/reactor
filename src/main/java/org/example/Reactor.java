package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

public class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        this.selector = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        selectionKey.attach(new AcceptHandler(selector, serverSocketChannel));
    }

    @Override
    public void run() {
        try {
            while (true) {
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                for (SelectionKey selectionKey : selected) {
                    dispatch(selectionKey);
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey selectionKey) {
        Handler handler = (Handler) selectionKey.attachment();
        handler.handle();
    }
}
