package chapter13;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, LogEvent event) throws Exception {
        String builder = event.getReceivedTimestamp() +
                " [" +
                event.getSource().toString() +
                "] [" +
                event.getLogfile() +
                "] : " +
                event.getMsg();

        System.out.println(builder);
    }
}
