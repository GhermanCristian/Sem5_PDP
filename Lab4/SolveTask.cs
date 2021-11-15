using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace Lab4 {
    class SolveTask {
        public SolveTask(List<string> hosts) {
            List<Task> tasks = new();
            hosts.ForEach(host => tasks.Add(Task.Factory.StartNew(this.run, host)));
            Task.WaitAll(tasks.ToArray());
        }

        private void run(object host) {
            string hostAsString = (string)host;
            IPAddress IP = Dns.GetHostEntry(hostAsString.Split('/')[0]).AddressList[0];
            IPEndPoint endPoint = new(IP, 80);
            Socket client = new(IP.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            byte[] buffer = new byte[Common.BUFFER_SIZE];

            this.connect(client, endPoint).Wait();
            this.send(client, endPoint, hostAsString).Wait();
            this.receive(client, buffer).Wait();
            client.Shutdown(SocketShutdown.Both);
            client.Close();

            Common.interpretResponse(buffer);
        }

        private Task connect(Socket clientSocket, IPEndPoint endPoint) {
            TaskCompletionSource<int> promise = new();
            clientSocket.BeginConnect(endPoint, (IAsyncResult ar) => promise.SetResult(this.endConnectWrapper(clientSocket, ar)), null);
            return promise.Task;
        }

        private int endConnectWrapper(Socket clientSocket, IAsyncResult ar) {
            clientSocket.EndConnect(ar);
            return 1;
        }

        private Task send(Socket clientSocket, IPEndPoint endPoint, string host) {
            TaskCompletionSource<int> promise = new();
            byte[] getRequest = Common.getRequestContent(host);
            clientSocket.BeginSend(getRequest, 0, getRequest.Length, 0, (IAsyncResult ar) => promise.SetResult(clientSocket.EndSend(ar)), null);
            return promise.Task;
        }

        private Task receive(Socket clientSocket, byte[] buffer) {
            TaskCompletionSource<int> promise = new ();
            clientSocket.BeginReceive(buffer, 0, Common.BUFFER_SIZE, 0, (IAsyncResult ar) => promise.SetResult(clientSocket.EndReceive(ar)), null);
            return promise.Task;
        }
    }
}
