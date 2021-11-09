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
            client.BeginConnect(endPoint, onConnected, new Payload(client, endPoint));
        }

        private void onConnected(IAsyncResult connectionPayload) {
            Payload payload = (Payload)connectionPayload.AsyncState;
            String requestEndpoint = this.hostURL.Contains("/") ? this.hostURL.Substring(this.hostURL.IndexOf("/")) : "/";
            String getRequestAsString = "GET " + requestEndpoint + " HTTP/1.1\r\nHost: " + this.hostURL.Split('/')[0] + "\r\nContent-Length: 0\r\n\r\n";
            byte[] getRequest = Encoding.ASCII.GetBytes(getRequestAsString);
            payload.clientSocket.BeginSend(getRequest, 0, getRequest.Length, 0, onSend, payload);
        }

        private void onSend(IAsyncResult connectionPayload) {
            Payload payload = (Payload)connectionPayload.AsyncState;
            int sentDataSize = payload.clientSocket.EndSend(connectionPayload);
            payload.clientSocket.BeginReceive(payload.buffer, 0, 4096, 0, onReceive, payload);
        }

        private void onReceive(IAsyncResult connectionPayload) {
            Payload payload = (Payload)connectionPayload.AsyncState;
            int receivedDataSize = payload.clientSocket.EndReceive(connectionPayload);
            Console.WriteLine(Encoding.ASCII.GetString(payload.buffer, 0, receivedDataSize));
            payload.clientSocket.Shutdown(SocketShutdown.Both);
            payload.clientSocket.Close();
        }
    }
}
