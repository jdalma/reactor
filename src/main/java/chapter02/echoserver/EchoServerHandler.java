package chapter02.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 메시지가 들어올 때마다 호출된다.
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        byte[] b = new byte[in.readableBytes()];
        in.getBytes(0, b, 0, in.readableBytes());
        String s = new String(b);

        //System.out.println("Server received: " + ByteBufUtil.hexDump(in));
        System.out.println("Server received: " + s);
        ctx.write(in); // 아웃바운드 메시지를 플러시하지 않은 채로 받은 메시지를 발신자로 출력함
    }

    /**
     * channelRead의 마지막 호출에서 현재 일괄 처리의 마지막 메시지를 처리했음을 핸들러에 통보한다.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 대기 중인 메시지를 원격 피어로 플러시하고 채널을 닫음
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 읽기 작업 중 예외가 발생하면 호출된다.
     * 이 메서드를 재정의하면 모든 Throwable 하위 형식에 반응할 수 있다.
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
