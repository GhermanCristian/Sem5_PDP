using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Lab4 {
    class SolveCallback {
        public SolveCallback(List<string> hosts) => hosts.ForEach(host => {
            this.start(host);
            Thread.Sleep(1000);
        });

        private void start(string host) {
            IPAddress IP = Dns.GetHostEntry(host.Split('/')[0]).AddressList[0];
            IPEndPoint endPoint = new(IP, 80);
            Socket client = new(IP.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            client.BeginConnect(endPoint, this.onConnected, new Payload(client, endPoint, host));
        }

        private void onConnected(IAsyncResult connectionPayload) {
            Payload payload = (Payload)connectionPayload.AsyncState;
            byte[] getRequest = Common.getRequestContent(payload.host);
            payload.clientSocket.BeginSend(getRequest, 0, getRequest.Length, 0, this.onSend, payload);
        }

        private void onSend(IAsyncResult connectionPayload) {
            Payload payload = (Payload)connectionPayload.AsyncState;
            int sentDataSize = payload.clientSocket.EndSend(connectionPayload);
            payload.clientSocket.BeginReceive(payload.buffer, 0, 4096, 0, this.onReceive, payload);
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
