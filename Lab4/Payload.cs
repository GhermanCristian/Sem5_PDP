using System.Net;
using System.Net.Sockets;

namespace Lab4 {
    class Payload {
        public Socket clientSocket;
        public IPEndPoint endPoint;
        public byte[] buffer = new byte[4096];

        public Payload(Socket clientSocket, IPEndPoint endPoint) {
            this.clientSocket = clientSocket;
            this.endPoint = endPoint;
        }
    }
}
