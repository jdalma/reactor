package chapter09;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbsIntegerEncoderTest {

    @Test
    void testEncoded() {
        ByteBuf buffer = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buffer.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        assertTrue(channel.writeOutbound(buffer));
        assertTrue(channel.finish());

        for (int i = 1; i< 10; i++) {
            assertEquals(i, (Integer) channel.readOutbound());
        }
        assertNull(channel.readOutbound());
    }
}
