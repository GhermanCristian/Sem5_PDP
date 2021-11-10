﻿using System.Net;
using System.Net.Sockets;

namespace Lab4 {
    class Payload {
        public Socket clientSocket;
        public IPEndPoint endPoint;
        public string host;
        public byte[] buffer = new byte[4096];

        public Payload(Socket clientSocket, IPEndPoint endPoint, string host) {
            this.clientSocket = clientSocket;
            this.endPoint = endPoint;
            this.host = host;
        }
    }
}
