package chapter06;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class DiscardHandler extends ChannelInboundHandlerAdapter {

    /**
     * 풀링된 ByteBuf 인스턴스의 메모리를 명시적으로 해제하는 역할을 맡는다.
     * 메모리를 해제할 수 있는 ReferenceCountUtil.release() 메서드를 사용할 수 있다.
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }
}
