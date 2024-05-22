package chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class LineBasedHandlerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // LineBasedFrameDecoder 가 추출한 프레임을 다음 핸들러로 전달
        // 줄바꿈 문자 외의 다른 문자로 구분된 프레임을 이용하는 경우 비슷한 방식으로 DelimiterBasedFrameDecoder를 이용하고 특정한 구분 문자 시퀀스를 생성자에 지정하면 된다
        pipeline.addLast(new LineBasedFrameDecoder(65 * 1024));
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // Do something with the frame
        }
    }
}
