package Handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.*;


public class MyHttpHandler extends ChannelInboundHandlerAdapter {
    private GetCache getCache=new GetCache();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
//        System.out.println(body);
//        System.out.println("************************");
        String[] reqcontent;
        reqcontent=body.split("\n");
        String[] reqHead;
        reqHead=reqcontent[0].split(" ");
        if (reqHead[0].equals("GET")){
            if (reqHead[1].equals("/favicon.ico")){
                ctx.writeAndFlush(" ");
            }
            ctx.writeAndFlush(getHandler(reqHead[1]));
        }else if (reqHead[0].equals("POST")){
            postHandler(reqHead[1]);
        }
    }
    public File html(String url){
        String apath = "/home/mikasa/Study/Netty/src/main/web";
        File file=new File(apath+url);
        return file;

    }
    public ByteBuf getHandler(String url) {
        System.out.println(url);
        String apath = "/home/mikasa/Study/Netty/src/main/web";
//        File file = new File(apath + url);
        ByteBuf res=null;
        String filecontent;
        if ((filecontent = getCache.getHashMap().get(apath + url)) != null) {
             res = Unpooled.wrappedBuffer((new String("HTTP/1.1 200 OK\n" +
                    "Sat, 31 Dec 2005 23:59:59 GMT\n" + "Connection:Keep-Alive\n" +
                    "Content-Type:text/html:charset=UTF-8\n" +
                    "Content-Length:117\n" + "\n" + "你好").getBytes()));
            return res;
        } else {
//            try {
//                File file=new File(apath+url);
//                FileReader fr = new FileReader(apath + url);
//                BufferedReader br = new BufferedReader(fr);
//                String content =new String();
//                String cont;
//                while((cont=br.readLine())!=null){
//                    System.out.println(cont);
//                    content+=cont;
//                }
//                System.out.println(content);
//                getCache.getHashMap().put(apath+url,content);
                String input="<input type=\"button\" value=\"点击\">";
                res = Unpooled.wrappedBuffer((new String("HTTP/1.1 200 OK\n" + "Sat, 31 Dec 2005 23:59:59 GMT\n" + "Connection:Keep-Alive\n" + "Content-Type:text/html:charset=UTF-8\n" + "Content-Length:"+(input.length()+4)+"\n"+"\n"+input).getBytes()));
                    return res;
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        return res;
    }
    public void postHandler(String url){

    }
}
