using System;
using System.Net;
using System.Net.Sockets;
using System.Text;

namespace Lab4 {
    class SolveCallback {
        private String hostURL;
        public SolveCallback(String hostURL) {
            this.hostURL = hostURL;
        }

        public void start() {
            IPAddress IP = Dns.GetHostEntry(this.hostURL.Split('/')[0]).AddressList[0];
            IPEndPoint endPoint = new IPEndPoint(IP, 80);
            Socket client = new Socket(IP.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            Console.WriteLine("before begin connect");
            client.BeginConnect(endPoint, onConnected, new Payload(client, endPoint));
            Console.WriteLine("after begin connect");
        }

        private void onConnected(IAsyncResult connectionPayload) {
            Console.WriteLine("on connected");
            Payload payload = (Payload)connectionPayload.AsyncState;
            byte[] getRequest = Encoding.ASCII.GetBytes("GET " + payload.endPoint + " HTTP/1.0\n" + "Host: " + this.hostURL.Split('/')[0] + "\n" + "Content-Length: 0\n");
            payload.clientSocket.BeginSend(getRequest, 0, getRequest.Length, 0, onSend, payload);
        }

        private void onSend(IAsyncResult connectionPayload) {
            Console.WriteLine("on send");
            Payload payload = (Payload)connectionPayload.AsyncState;
            int sentDataSize = payload.clientSocket.EndSend(connectionPayload);
            payload.clientSocket.BeginReceive(payload.buffer, 0, 1024, 0, onReceive, payload);
        }

        private void onReceive(IAsyncResult connectionPayload) {
            Console.WriteLine("on receive");
            Payload payload = (Payload)connectionPayload.AsyncState;
            int receivedDataSize = payload.clientSocket.EndReceive(connectionPayload);
            Console.WriteLine(Encoding.ASCII.GetString(payload.buffer, 0, receivedDataSize));
            payload.clientSocket.Shutdown(SocketShutdown.Both);
            payload.clientSocket.Close();
        }
    }
}
