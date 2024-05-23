package chapter12;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = path.contains("file:") ? path.substring(5) : path;
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    private final String wsUri;

    /**
     * @param wsUri
     */
    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    /**
     * @param ctx
     */
    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    /**
     * @param ctx
     * @param request
     * @throws Exception
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        // URI /ws를 참조하는 경우 FullHttpRequest의 retain을 호출하고 다음 핸들러로 전달
        // retain()을 호출해야 하는 이유는 channelRead()가 작업을 완료한 후 FullHttpRequest의 release()를 호출해 리소스를 해제하기 때문이다.
        if (wsUri.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            boolean keepAlive;
            try (RandomAccessFile file = new RandomAccessFile(INDEX, "r")) {
                HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                keepAlive = HttpUtil.isKeepAlive(request);
                if (keepAlive) {
                    response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.KEEP_ALIVE);
                }
                ctx.write(response);
                if (ctx.pipeline().get(SslHandler.class) == null) {
                    ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
                } else {
                    ctx.write(new ChunkedNioFile(file.getChannel()));
                }
                ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                if (!keepAlive) {
                    future.addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    /**
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
