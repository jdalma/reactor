package chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

public class SslChannelInitializer extends ChannelInitializer<Channel> {

    private final SSLContext context;
    private final boolean client;
    private final boolean startTls;

    public SslChannelInitializer(SSLContext context, boolean client, boolean startTls) {
        this.context = context;
        this.client = client;
        this.startTls = startTls;
    }

    @Override
    protected void initChannel(Channel ch) {
        // 각 SslHandler 인스턴스마다 Channel의 ByteBufAllocator를 이용해 SslContext에서 새로운 SSLEngine을 얻음
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(client);

        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));
    }
}
