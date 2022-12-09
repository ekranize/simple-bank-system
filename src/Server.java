public class Server {
    private boolean isStarted = false;
    public void startServer (int portNum) {
        System.out.println("Server started on port " + portNum);
        isStarted = true;
    }
    public void stopServer () {
        System.out.println("Server stopped");
        isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }
}
