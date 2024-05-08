package chapter08;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class BootstrapSharingEventLoopGroup {

    public void bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new SimpleChannelInboundHandler<ByteBuf>() {
                            ChannelFuture connectFuture;

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) {
                                // 원격 호스트에 연결할 Bootstrap 생성
                                Bootstrap bootstrap = new Bootstrap();
                                // Channel 구현 지정
                                bootstrap.channel(NioSocketChannel.class).handler(
                                    new SimpleChannelInboundHandler<ByteBuf>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
                                            System.out.println("Received data");
                                        }
                                    }
                                );
                                // 수락된 채널에 할당된 것과 동일한 EventLoop를 이용
                                bootstrap.group(ctx.channel().eventLoop());
                                connectFuture = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
                                if (connectFuture.isDone()) {
                                    // do something with the data
                                }
                            }
                        });

        // 구성된 Bootstrap을 통해 채널을 바인딩
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("Server bound");
            } else {
                System.err.println("Bind attempt failed");
                channelFuture.cause().printStackTrace();
            }
        });

    }
}
