package client;

import client.codec.*;
import client.handler.dispatcher.OperationResultFuture;
import client.handler.dispatcher.RequestPendingCenter;
import client.handler.dispatcher.ResponseDispatcherHandler;
import common.OperationResult;
import common.RequestMessage;
import common.order.OrderOperation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import util.IdUtil;

import java.util.concurrent.ExecutionException;

/**
 * @author: r.hu
 * @create: 2020-03-06 20:28
 **/

public class Client {

    public static void main(String[] args)
        throws InterruptedException, ExecutionException{
        NioEventLoopGroup work = new NioEventLoopGroup();

        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
        RequestPendingCenter pendingCenter = new RequestPendingCenter();


        
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.channel(NioSocketChannel.class)
                .group(work)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new OrderFrameDecoder());
                        pipeline.addLast(new OrderFrameEncoder());

                        pipeline.addLast(new OrderProtocolEncoder());
                        pipeline.addLast(new OrderProtocolDecoder());

                        pipeline.addLast(new ResponseDispatcherHandler(pendingCenter));

                        pipeline.addLast(new OperationToRequestMessageEncoder());
                        pipeline.addLast(loggingHandler);

                    }
                });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8091);
            channelFuture.sync();
            long streamId = IdUtil.nextId();
            // OrderOperation operation = new OrderOperation(1, "小炒肉");
            RequestMessage requestMessage = new RequestMessage(
                streamId, new OrderOperation(1001, "tudou"));

            OperationResultFuture resultFuture = new OperationResultFuture();

            pendingCenter.add(streamId,resultFuture);

            channelFuture.channel().writeAndFlush(requestMessage);
            OperationResult operationResult = resultFuture.get();

            System.out.println(operationResult);


            channelFuture.channel().closeFuture().sync();
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            work.shutdownGracefully();
        }



    }
}
