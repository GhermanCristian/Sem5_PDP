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
            byte[] buffer = new byte[Common.BUFFER_SIZE];

            await this.connect(client, endPoint);
            await this.send(client, endPoint, hostAsString);
            await this.receive(client, buffer);
            Console.WriteLine(Encoding.ASCII.GetString(buffer, 0, Common.BUFFER_SIZE));
            Console.WriteLine("\n\n\n");
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
            byte[] getRequest = Common.getRequestContent(host);
            clientSocket.BeginSend(getRequest, 0, getRequest.Length, 0, (IAsyncResult ar) => promise.SetResult(clientSocket.EndSend(ar)), null);
            await promise.Task;
        }

        private async Task receive(Socket clientSocket, byte[] buffer) {
            TaskCompletionSource<int> promise = new();
            clientSocket.BeginReceive(buffer, 0, Common.BUFFER_SIZE, 0, (IAsyncResult ar) => promise.SetResult(clientSocket.EndSend(ar)), null);
            await promise.Task;
        }
    }
}
