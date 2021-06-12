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
    private boolean isNotFound;
    private static final GetCache getCache;
    private char type;
    static {
        location = "/home/mikasa/Study/Netty/src/main/web";
        NotFound = new File(location + "/404.html");
        getCache=new GetCache();
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri= msg.uri();
        if ("favicon.ico".equals(uri)){
            return;
        }
        //先查询页面缓存
        String filepath=location+uri;
        char typetemp=getCache.getHashMap().get(filepath);
        if (getCache.getHashMap().get(filepath)!=null){
            switch (typetemp){
                case 'h':
            }
        }
        File targetfile=new File(location+uri);
        if (HttpUtil.is100ContinueExpected(msg)){
            send100Continue(ctx);
        }
        //如果文件不存在就返回自制的404页面

        if (!targetfile.exists()){
            targetfile=NotFound;
        }
        RandomAccessFile file=new RandomAccessFile(targetfile,"r");
        HttpResponse response=new DefaultHttpResponse(msg.protocolVersion(),HttpResponseStatus.OK);
        if (targetfile==NotFound){
            response.setStatus(HttpResponseStatus.NOT_FOUND);
            isNotFound=true;
        }
        if (filepath.endsWith(".html")){
            msg.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/html; charset=UTF-8");
            type='h';
        }else if(filepath.endsWith(".js")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"application/x-javascript");
            type='j';
        }else if (filepath.endsWith(".css")){
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE,"text/css; charset=UTF-8");
            type='c';
        }
        if (!isNotFound){
            getCache.getHashMap().put(filepath,type);
        }
        boolean keepAlive= HttpUtil.isKeepAlive(msg);
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
    private FullHttpRequest dealEndWithH(FullHttpRequest msg){


        return msg;
    }
}
