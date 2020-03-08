package client.codec;

import common.Operation;
import common.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import util.IdUtil;

public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);

        out.add(requestMessage);
    }
}
