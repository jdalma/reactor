package chapter06;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class SimpleDiscardHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 다른 조치를 취할 필요가 없으며, SimpleChannelInboundHandler는 리소스를 자동으로 해제하므로 메시지의 참조도 무효화된다.
        // 즉, 메시지의 참조를 저장해 나중에 이용하려고 하면 안된다.

        // ChannelHandlerContext에서 Channel에 대한 참조를 얻어 버퍼를 기록
        Channel channel = ctx.channel();
        channel.write(Unpooled.copiedBuffer("Netty in Action", StandardCharsets.UTF_8));

        // ChannelHandlerContext에서 Pipeline에 대한 참조를 얻어 버퍼를 기록
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.write(Unpooled.copiedBuffer("Netty in Action", StandardCharsets.UTF_8));

        // 버퍼를 다음 ChannelHandler로 전송
        ctx.write(Unpooled.copiedBuffer("Netty in Action", StandardCharsets.UTF_8));
    }
}
