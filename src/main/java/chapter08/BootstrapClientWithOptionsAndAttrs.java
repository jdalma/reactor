package chapter08;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class BootstrapClientWithOptionsAndAttrs {

    public static void main(String[] args) {
        new BootstrapClientWithOptionsAndAttrs().bootstrap();
    }

    public void bootstrap() {
        final AttributeKey<Integer> id = AttributeKey.valueOf("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(
                    new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        public void channelRegistered(ChannelHandlerContext ctx) {
                            Integer idValue = ctx.channel().attr(id).get();
                            System.out.println("channelRegistered : " + idValue);
                            // do something with the idValue
                        }

                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
                            System.out.println("Received data");
                        }
                    }
                );

        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.attr(id, 10);
        bootstrap.attr(id, 20);

        ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
        future.syncUninterruptibly();
    }
}
