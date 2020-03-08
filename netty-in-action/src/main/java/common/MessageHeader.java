package common;

import lombok.Data;

@Data
public class MessageHeader {

    // 类似 HTTP 1.1 2.0
    private int version = 1;

    // opCode 区分不同的 operation 这样json 就知道 去解析哪种类型的数据
    private int opCode;
    // 类似 事务id
    private long streamId;

}
