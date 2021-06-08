package Handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;

public class MyNewHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String location;
    private static final File NotFound;

    static {
        location = "/home/mikasa/Study/Netty/src/main/web";
        NotFound = new File(location + "/404.html");
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri= msg.uri();
        if ("favicon.ico".equals(uri)){
            return;
        }
        String filepath=location+uri;
        File targetfile=new File(location+uri);
        if (HttpUtil.is100ContinueExpected(msg)){
        }
        if (!targetfile.exists()){
            targetfile=NotFound;
        }
        RandomAccessFile file=new RandomAccessFile(targetfile,"r");
        HttpResponse response=new DefaultHttpResponse(msg.protocolVersion(),HttpResponseStatus.OK);
        if (targetfile==NotFound){
            response.setStatus(HttpResponseStatus.NOT_FOUND);
        }
        if (filepath.endsWith(".html")){
            msg.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html; charset=UTF-8");
        }else if(filepath.endsWith(".js")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"application/x-javascript");

        }else if (filepath.endsWith(".css")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/css; charset=UTF-8");
        }
        boolean keepAlive= HttpHeaders.isKeepAlive(msg);
        if (keepAlive){
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        if (ctx.pipeline().get(SslHandler.class)==null){
            ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));

        }else {
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }
        ChannelFuture future=ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive){
            future.addListener(ChannelFutureListener.CLOSE);

        }
        file.close();
    }
    private static void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }
}
