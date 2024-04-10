package chapter01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

    // 새로운 연결이 이뤄지면 해당 메서드가 호출된다.
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFutureListener callback = (ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                // 3. 작업이 성공적인 경우 데이터를 저장할 ByteBuf를 생성
                ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());

                // 4. 데이터를 비동기식으로 원격 피어로 전송하고 ChannelFuture를 반환
                ChannelFuture wf = future.channel().writeAndFlush(buffer);
                // ...
            } else {
                // 3. 작업이 성공적이지 않다면 Future에서 예외를 꺼낸다.
                Throwable cause = future.cause();
                cause.printStackTrace();
            }
        };

        System.out.println("Client " + ctx.channel().remoteAddress() + " connected");

        Channel channel = null;

        // 1. 원격 피어로 비동기 연결을 만듦
        ChannelFuture future = channel.connect(new InetSocketAddress("192.68.0.01", 25));

        // 2. 작업이 완료되면 알림을 받음
        future.addListener(callback);

        super.channelActive(ctx);
    }
}
