package chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DecodeEncodeTest {

    @Test
    void testDecoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buffer.writeByte(i);
        }
        ByteBuf input = buffer.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(
            new InboundLogging(),
//            new IntegerToStringDecoder()
            new SafeByteToMessageDecoder()
        );

        channel.writeInbound(input.readBytes(4));
        channel.writeInbound(input.readBytes(4));
        channel.finish();

        Object o = channel.readInbound();
        System.out.println(o);
        o = channel.readInbound();
        System.out.println(o);

//        ByteBuf read = (ByteBuf) o;
//        System.out.println(read.readInt());
//        read.release();

        // readInbound 하지 않고 바로 readInt하면 예외가 발생함
        // readerIndex(4) + length(4) exceeds writerIndex(4): UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeDirectByteBuf(ridx: 4, widx: 4, cap: 4)
//        read = channel.readInbound();
//        System.out.println(read.readInt());
    }

    @Test
    void testEncoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buffer.writeShort(i);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new ShortToByteEncoder());
        assertTrue(channel.writeOutbound(buffer));
        assertTrue(channel.finish());

        ByteBuf byteBuf = channel.readOutbound();
        System.out.println(byteBuf);
        for (int i = 1; i< 10; i++) {
            System.out.println(byteBuf.readShort());
        }
    }
}
