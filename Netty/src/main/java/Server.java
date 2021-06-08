//import Handler.HttpServerHandler;

import Handler.MyHttpHandler;
import Handler.MyNewHttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.HashMap;

public class Server implements Runnable {
    private int port;
    public Server(int port) {
        this.port = port;
    }

    NioEventLoopGroup boos = new NioEventLoopGroup();//用来接受链接的请求，接受到就注册到worker上
    NioEventLoopGroup worker = new NioEventLoopGroup();//处理已接受的链接
    public void run() {
        try {
        ServerBootstrap serverBootstrap = new ServerBootstrap();//启动NIO的辅助启动类，也可替换为Channel

        serverBootstrap.group(boos, worker)
                .channel(NioServerSocketChannel.class)//告诉channel怎么获取新的链接，使用NIO模型
                /*
                这里的事件处理类经常会被用来处理一个最近的已经接收的Channel。 ChannelInitializer是一个特殊的处理类，
             * 他的目的是帮助使用者配置一个新的Channel。
             * 也许你想通过增加一些处理类比如NettyServerHandler来配置一个新的Channel
             * 或者其对应的ChannelPipeline来实现你的网络程序。 当你的程序变的复杂时，可能你会增加更多的处理类到pipline上，
             * 然后提取这些匿名类到最顶层的类上。
                 */
                .childHandler(new ChannelInitializer<NioSocketChannel>() {//指定服务端启动逻辑
                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                          ch.pipeline().addLast(new FirstServerHandler());
//                        ch.pipeline().addLast(new HttpResponseEncoder());
//                        ch.pipeline().addLast(new HttpRequestDecoder());
//
//                        ch.pipeline().addLast(new HttpObjectAggregator(65535));
//                        ch.pipeline().addLast(new MyHttpHandler());
                        ch.pipeline().addLast(new HttpServerCodec());
                        //将HTTP消息的多个部分组合成一条完整的HTTP消息
                        ch.pipeline().addLast(new HttpObjectAggregator(64 * 1024));
                        ch.pipeline().addLast(new MyNewHttpHandler());
                        //ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                            /*
                            ctx:通道处理的上下文信息
                            msg：接收到的消息
                             */
                          //  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                            //    System.out.println(msg);
                            //}
                       // });
                    }
                });

            ChannelFuture channelFuture=serverBootstrap.bind(port).sync();//sync 一直循环，不让链接丢失
            if (channelFuture.isSuccess()){
                System.out.println("server 已启动");
            }

            channelFuture.channel().closeFuture().sync();
            System.out.println("server 即将关闭");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boos.shutdownGracefully();
            worker.shutdownGracefully();
            System.out.println("server 已关闭");
        }
    }

}
