package chapter06;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class WriteHandler extends ChannelHandlerAdapter {

    // ChannelHandlerContext의 참조를 나중에 이용하기 위해 캐싱
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    // 이전에 저장한 ChannelHandlerContext를 이용한 메시지 전송
    public void send(String msg) {
        ctx.writeAndFlush(msg);
    }
}
