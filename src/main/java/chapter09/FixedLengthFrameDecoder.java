package chapter09;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // (this.writerIndex - this.readerIndex) 와 동일한 읽기 가능한 바이트 수를 반환합니다.
        while (in.readableBytes() >= frameLength) {
            ByteBuf byteBuf = in.readBytes(frameLength);
            // 디코딩된 메시지의 List에 프레임을 추가
            out.add(byteBuf);
        }
    }
}
