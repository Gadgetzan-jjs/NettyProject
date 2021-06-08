public class ServerMain {
    public static int port=8989;
    public static void main(String[] args) {
        Server server=new Server(port);
        server.run();
    }
}
