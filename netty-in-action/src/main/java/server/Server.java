package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import server.codec.OrderFrameDecoder;
import server.codec.OrderFrameEncoder;
import server.codec.OrderProtocolDecoder;
import server.codec.OrderProtocolEncoder;
import server.handler.AuthHandler;
import server.handler.MetricsHandler;
import server.handler.OrderServerProcessHandler;
import server.handler.ServerIdleCheckHandler;

/**
 * auther : hurui
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws InterruptedException, ExecutionException, CertificateException, SSLException {

        NioEventLoopGroup work = new NioEventLoopGroup();
        NioEventLoopGroup boss = new NioEventLoopGroup();


        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .group(boss,work)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new OrderFrameDecoder());
                    pipeline.addLast(new OrderFrameEncoder());
                    pipeline.addLast(new OrderProtocolEncoder());
                    pipeline.addLast(new OrderProtocolDecoder());
                    pipeline.addLast(new OrderServerProcessHandler());
                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                }
            });

        ChannelFuture channelFuture = serverBootstrap.bind(8091).sync();

        channelFuture.channel().closeFuture().get();


    }

}
