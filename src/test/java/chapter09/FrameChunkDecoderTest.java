package chapter09;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrameChunkDecoderTest {

    @Test
    void testFrameDecoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0 ; i < 9 ; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        assertTrue(channel.writeInbound(input.readBytes(2)));
        // 해당 4바이트는 폐기됨, input.readBytes 만큼 읽기 인덱스가 증가됨
        assertThrowsExactly(TooLongFrameException.class, () -> channel.writeInbound(input.readBytes(4)));

        assertTrue(channel.writeInbound(input.readBytes(3)));
        assertTrue(channel.finish());

        ByteBuf read = channel.readInbound();
        assertEquals(buffer.readSlice(2), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buffer.skipBytes(4).readSlice(3), read);
        read.release();
        buffer.release();
    }
}
