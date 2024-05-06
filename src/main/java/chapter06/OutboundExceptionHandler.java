package chapter06;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {

    private final ChannelFutureListener listener = (ChannelFutureListener) f -> {
        if (!f.isSuccess()) {
            System.out.println("OutboundExceptionHandler의 ChannelFutureListener 호출!!!");
            f.cause().printStackTrace();
            f.channel().close();
        }
    };

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ByteBuf in = (ByteBuf) msg;
        byte[] b = new byte[in.readableBytes()];
        in.getBytes(0, b, 0, in.readableBytes());
        String s = new String(b);

        System.out.println("OutboundExceptionHandler 호출 !!! : " + s);

//        ChannelFuture future = ctx.write(msg);
//        future.addListener(listener);
        promise.addListener(listener);
        ctx.write(msg);
    }
}
