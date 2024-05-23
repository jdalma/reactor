package chapter12;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;
    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    /**
     * HttpServerCodec : 바이트를 HttpRequest. HttpContent. LastHttpContent로 디코딩한다. HttpRequest, HttpContent, LastHttpContent를 바이트로 인코딩한다.
     * ChunkedWriteHandler: 파일의 내용을 기록한다.
     * HttpObjectAggregator: HttpMessage 및 해당하는 후속 HttpContent를 집계해 요청이나 응답을 처리하는지에 따라 단일 FullHttpRequest 또는 FullHttpResponse를 생성한다. 이를 설치하면 파이프라인의 다음 ChannelHandler는 완전한 HTTP 요청만 받는다.
     * HttpRequestHandler: /ws UR로 보내지 않은 요청에 해당하는 FullHttpRequest를 처리한다
     * WebSocketServerProtocolHandler: 웹소켓 사양에서 요구하는 대로 웹소켓 업그레이드 핸드셰이크 PingWebSocketFrame.PongWebSocketFrame. CloseWebSocketFrame을 처리한다.
     * TextWebSocketFrameHandler: TextWebSocketFrame 및 핸드셰이크 완료 이벤트를 처리한다
     *
     * 업그레이드가 완료되면 WebSocketServerProtocolHandler는 HttpRequestDecoder와 HttpResponseEncoder를 각각 WebSocketFrameDecoder와 WebSocketFrameEncoder로 대체한다.
     * 그후 연결에 필요하지 않은 모든 ChannelHandler를 제거한다. (HttpObjectAggregator와 HttpRequestHandler가 제거된다.)
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel ch)
            throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpRequestHandler("/ws"));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}
