package chapter13;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

public class LogEventBroadcaster {
    private final Bootstrap bootstrap;
    private final EventLoopGroup group;
    private final File file;
    private final InetSocketAddress address;

    public LogEventBroadcaster(int port, String logfile) throws Exception {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.file = new File(logfile);
        if (!file.exists()) {
            throw new Exception("logfile " + logfile + " not found");
        }
        this.address = new InetSocketAddress("255.255.255.255", port);
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String logfile = "src/main/resources/logfile";

        LogEventBroadcaster broadcaster = new LogEventBroadcaster(port, logfile);
        try {
            broadcaster.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            broadcaster.stop();
        }
    }

    public void run() throws IOException {
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(address));

        Channel ch = bootstrap.bind(0).syncUninterruptibly().channel();
        System.out.println("LogEventBroadcaster (" + file.getName() + ") running on port " + address.getPort() + ".");
        long pointer = 0;

        while (true) {
            long len = file.length();
            if (len < pointer) {
                pointer = len;
            } else if (len > pointer) {
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer);
                String line;
                while ((line = raf.readLine()) != null) {
                    ch.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }
                pointer = raf.getFilePointer();
                raf.close();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
                break;
            }
        }
    }

    public void stop() {
        if (null != group) {
            group.shutdownGracefully();
        }
    }
}
