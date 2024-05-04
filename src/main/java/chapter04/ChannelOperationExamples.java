package chapter04;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChannelOperationExamples {

    public static void writingToChannel() {
        Channel channel = null;

        // 기록할 데이터를 포함하는 ByteBuf를 생성
        ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
        ChannelFuture cf = channel.writeAndFlush(buf);

        // 기록이 완료되면 알림을 받을 ChannelFutureListener를 추가
        cf.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("Write successful");
            } else {
                System.err.println("Write error");
                future.cause().printStackTrace();
            }
        });
    }

    public static void writingToChannelManyThreads() {
        final Channel channel = null;
        final ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
        Runnable writer = () -> channel.write(buf.duplicate());
        Executor executor = Executors.newCachedThreadPool();

        // write in one thread
        executor.execute(writer);

        // write in another thread
        executor.execute(writer);
    }

    public void directBuffer(ByteBuf directBuf) {
        if(!directBuf.hasArray()) {
            int length = directBuf.readableBytes(); // 읽을 수 있는 바이트 수
            byte[] array = new byte[length];
            directBuf.getBytes(directBuf.readerIndex(), array);
            // ...
        }
    }

    public void byteBuffer(ByteBuffer header, ByteBuffer body) {
        ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();
    }

    public void compositeByteBuf(ByteBuf headerBuf, ByteBuf bodyBuf) {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        messageBuf.addComponents(headerBuf, bodyBuf);
    }
}
