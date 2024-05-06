package chapter04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 네티는 모든 전송의 구현에 동일한 API를 노출하므로 OIO 또는 NIO를 구현하는 것은 코드의 영향을 거의 받지 않는다.
 * 즉, 모든 구현이 Channel, ChannelPipeline, ChannelHandler
 */
public class NettyNioServer {
    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", StandardCharsets.UTF_8));
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelInboundHandlerAdapter channelInboundHandlerAdapter = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
            }
        };

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      public void initChannel(SocketChannel ch) {
                          ch.pipeline().addLast(channelInboundHandlerAdapter);
                      }
                  }
                );
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
