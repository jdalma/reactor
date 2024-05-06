package chapter06;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelPipeline;

public class ModifyChannelPipeline {

    public static void modifyPipeline(ChannelPipeline pipeline) {
        FirstHandler firstHandler = new FirstHandler();
        pipeline.addLast("handler1", firstHandler);
        pipeline.addFirst("handler2", new SecondHandler());
        pipeline.addLast("handler3", new ThirdHandler());
        pipeline.remove("handler3");
        pipeline.remove(firstHandler);
        pipeline.replace("handler2", "handler4", new FourthHandler());
    }

    private static final class FirstHandler extends ChannelHandlerAdapter { }
    private static final class SecondHandler extends ChannelHandlerAdapter { }
    private static final class ThirdHandler extends ChannelHandlerAdapter { }
    private static final class FourthHandler extends ChannelHandlerAdapter { }
}
