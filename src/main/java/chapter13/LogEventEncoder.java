package chapter13;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress remoteAddress;

    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LogEvent logEvent, List<Object> out) {
        ByteBuf buf = channelHandlerContext.alloc().buffer();

        buf.writeBytes(logEvent.getLogfile().getBytes(CharsetUtil.UTF_8));
        buf.writeByte(LogEvent.SEPARATOR);
        buf.writeBytes(logEvent.getMsg().getBytes(CharsetUtil.UTF_8));
        // 새로운 DatagramPacket을 데이터 및 대상 주소와 함께 아웃바운드 메시지의 리스트에 추가
        out.add(new DatagramPacket(buf, remoteAddress));
    }
}
