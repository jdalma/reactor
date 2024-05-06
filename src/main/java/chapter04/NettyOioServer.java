package chapter04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class NettyOioServer {
    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", StandardCharsets.UTF_8));
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            ChannelInboundHandlerAdapter channelInboundHandlerAdapter = new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) {
                    ctx.write(buf.duplicate())
                        .addListener(
                            // 클라이언트로 메시지를 출력하고 ChannelFutureListener를 추가해 메시지가 출력되면 연결을 닫음
                            ChannelFutureListener.CLOSE
                        );
                }
            };

            serverBootstrap.group(group)
                    .channel(OioServerSocketChannel.class)  // OioEventLoopGroup을 이용해 블로킹 모드를 허용
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 연결이 수락될 때 마다 호출될 ChannelInitializer를 지정
                        @Override
                        public void initChannel(SocketChannel ch) {
                            // 이벤트를 가로채고 처리할 ChannelInboundHandlerAdapter 추가
                            ch.pipeline().addLast(channelInboundHandlerAdapter);
                        }
                    });

            ChannelFuture f = serverBootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
