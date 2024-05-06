package chapter06;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class SharableHandler extends ChannelInboundHandlerAdapter {

    private final int number;

    public SharableHandler(int number) {
        this.number = number;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        byte[] b = new byte[in.readableBytes()];
        in.getBytes(0, b, 0, in.readableBytes());
        String s = new String(b);

        System.out.println("SharableHandler" + number + " channel read message : " + s);
        if (number == 3) {
            throw new RuntimeException("3번 핸들러에서 예외 발생!!!");
        }
        // 다음 ChannelHandler로 전달
        ctx.fireChannelRead(msg);
    }
}
