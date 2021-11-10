using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Lab4 {
    class SolveTaskAsync {
        public SolveTaskAsync(List<string> hosts) {
            List<Task> tasks = new();
            hosts.ForEach(host => tasks.Add(Task.Factory.StartNew(this.run, host)));
            Task.WaitAll(tasks.ToArray());
            Thread.Sleep(2000);
        }

        private async void run(object host) {
            string hostAsString = (string)host;
            IPAddress IP = Dns.GetHostEntry(hostAsString.Split('/')[0]).AddressList[0];
            IPEndPoint endPoint = new(IP, 80);
            Socket client = new(IP.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];

            await this.connect(client, endPoint);
            await this.send(client, endPoint, hostAsString);
            await this.receive(client, buffer, bufferSize);
            Console.WriteLine(Encoding.ASCII.GetString(buffer, 0, bufferSize));
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }

        private async Task connect(Socket clientSocket, IPEndPoint endPoint) {
            TaskCompletionSource<int> promise = new();
            clientSocket.BeginConnect(endPoint, (IAsyncResult ar) => promise.SetResult(this.endConnectWrapper(clientSocket, ar)), null);
            await promise.Task;
        }

        private int endConnectWrapper(Socket clientSocket, IAsyncResult ar) {
            clientSocket.EndConnect(ar);
            return 1;
        }

        private async Task send(Socket clientSocket, IPEndPoint endPoint, string host) {
            TaskCompletionSource<int> promise = new();
            string requestEndpoint = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/";
            string getRequestAsString = "GET " + requestEndpoint + " HTTP/1.1\r\nHost: " + host.Split('/')[0] + "\r\nContent-Length: 0\r\n\r\n";
            byte[] getRequest = Encoding.ASCII.GetBytes(getRequestAsString);
            clientSocket.BeginSend(getRequest, 0, getRequest.Length, 0, (IAsyncResult ar) => promise.SetResult(clientSocket.EndSend(ar)), null);
            await promise.Task;
        }

        private async Task receive(Socket clientSocket, byte[] buffer, int bufferSize) {
            TaskCompletionSource<int> promise = new();
            clientSocket.BeginReceive(buffer, 0, bufferSize, 0, (IAsyncResult ar) => promise.SetResult(clientSocket.EndSend(ar)), null);
            await promise.Task;
        }
    }
}
