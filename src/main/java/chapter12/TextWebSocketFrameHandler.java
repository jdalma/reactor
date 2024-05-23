package chapter12;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 이벤트가 핸드셰이크 성공을 의미하는 경우
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // HTTP 메시지는 더 이상 수신하지 않으므로 ChannelPipeline에서 HttpRequestHandler 제거
            ctx.pipeline().remove(HttpRequestHandler.class);

            // 연결된 모든 웹 소켓 클라이언트에 새로운 클라이언트가 연결된 것을 알림
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            // 모든 메시지를 수신할 수 있게 새로운 웹소켓 채널을 ChannelGroup에 추가
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 메시지의 참조 카운트를 증가시키고 ChannelGroup의 연결된 모든 클라이언트로 기록
        group.writeAndFlush(msg.retain());
    }
}
