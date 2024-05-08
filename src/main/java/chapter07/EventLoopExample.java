package chapter07;

import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

public class EventLoopExample {

    public static void test(Channel channel) {
        channel.eventLoop().schedule(
            (Runnable) () -> System.out.println("60 seconds later"),
            60,
            TimeUnit.SECONDS
        );

        channel.eventLoop().scheduleAtFixedRate(
            (Runnable) () -> System.out.println("Run every 60 seconds"),
            60,
            60,
            TimeUnit.SECONDS
        );
    }
}
