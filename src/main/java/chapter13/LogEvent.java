package chapter13;

import java.net.InetSocketAddress;

/**
 *        |              ChannelPipeline                 |
 * 로컬 -> | LogEvent -> LogEventEncoder -> DatagramPacket|
 */
public final class LogEvent {
    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final long received;

    // 나가는 메시지를 위한 생성자
    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    // 들어오는 메시지를 위한 생성자
    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    // LogEvent를 전송한 출처의 InetSocketAddress를 반환
    public InetSocketAddress getSource() {
        return source;
    }

    // LogEvent를 전송한 로그 파일의 이름을 반환
    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceivedTimestamp() {
        return received;
    }
}
