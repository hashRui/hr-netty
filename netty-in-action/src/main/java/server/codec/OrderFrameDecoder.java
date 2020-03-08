package server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author: r.hu
 * @create: 2020-03-06 11:44
 **/

public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder{


    public OrderFrameDecoder() {
        // 1 最大长度是多少  2 长度字段的位移设置为多少 从0开始   3长度是多少 是2
        // 4 要不要调整 length  5 解析头字段的时候  头字段需不需要去掉  这里要去掉 设置成2
        super(Integer.MAX_VALUE, 0, 2, 0,
            2);
    }
}
