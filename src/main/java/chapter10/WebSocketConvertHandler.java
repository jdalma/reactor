package chapter10;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.*;

import java.util.List;

@ChannelHandler.Sharable
public class WebSocketConvertHandler extends MessageToMessageCodec<WebSocketFrame, WebSocketConvertHandler.WebSocketFrame> {
    public static final WebSocketConvertHandler INSTANCE = new WebSocketConvertHandler();

    /**
     * OUTBOUND_IN 형식의 각 메시지를 처리할 때마다 호출한다.
     * 처리된 메시지는 INBOUND_IN 형식의 메시지로 인코딩된 후 파이프라인 내의 다음 ChannelOutboundHandler로 전달된다.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, WebSocketFrame msg, List<Object> out) {
        ByteBuf payload = msg.getData().duplicate().retain();
        switch (msg.getType()) {
            case BINARY:
                out.add(new BinaryWebSocketFrame(payload));
                return;
            case TEXT:
                out.add(new TextWebSocketFrame(payload));
                return;
            case CLOSE:
                out.add(new CloseWebSocketFrame(true, 0, payload));
                return;
            case CONTINUATION:
                out.add(new ContinuationWebSocketFrame(payload));
                return;
            case PONG:
                out.add(new PongWebSocketFrame(payload));
                return;
            case PING:
                out.add(new PingWebSocketFrame(payload));
            default:
                throw new IllegalStateException("Unsupported websocket msg " + msg);
        }
    }

    /**
     * INBOUND_IN 형식의 메시지를 받으면 이를 OUTBOUND_IN 형식의 메시지로 디코딩한다.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, io.netty.handler.codec.http.websocketx.WebSocketFrame msg, List<Object> out) {
        if (msg instanceof BinaryWebSocketFrame) {
            out.add(new WebSocketFrame(WebSocketFrame.FrameType.BINARY, msg.content().copy()));
            return;
        }
        if (msg instanceof CloseWebSocketFrame) {
            out.add(new WebSocketFrame(WebSocketFrame.FrameType.CLOSE, msg.content().copy()));
            return;
        }
        if (msg instanceof PingWebSocketFrame) {
            out.add(new WebSocketFrame(WebSocketFrame.FrameType.PING, msg.content().copy()));
            return;
        }
        if (msg instanceof PongWebSocketFrame) {
            out.add(new WebSocketFrame(WebSocketFrame.FrameType.PONG, msg.content().copy()));
            return;
        }
        if (msg instanceof TextWebSocketFrame) {
            out.add(new WebSocketFrame(WebSocketFrame.FrameType.TEXT, msg.content().copy()));
            return;
        }
        if (msg instanceof ContinuationWebSocketFrame) {
            out.add(new WebSocketFrame(WebSocketFrame.FrameType.CONTINUATION, msg.content().copy()));
            return;
        }
        throw new IllegalStateException("Unsupported websocket msg " + msg);
    }

    public static final class WebSocketFrame {
        private final FrameType type;
        private final ByteBuf data;

        public WebSocketFrame(FrameType type, ByteBuf data) {
            this.type = type;
            this.data = data;
        }

        public FrameType getType() {
            return type;
        }

        public ByteBuf getData() {
            return data;
        }

        public enum FrameType {
            BINARY,
            CLOSE,
            PING,
            PONG,
            TEXT,
            CONTINUATION
        }
    }
}
