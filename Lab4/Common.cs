using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lab4 {
    class Common {
        public static int BUFFER_SIZE = 1024;
        public static byte[] getRequestContent(string host) {
            string requestEndpoint = host.Contains("/") ? host.Substring(host.IndexOf("/")) : "/";
            string getRequestAsString = "GET " + requestEndpoint + " HTTP/1.1\r\nHost: " + host.Split('/')[0] + "\r\nContent-Length: 0\r\n\r\n";
            return Encoding.ASCII.GetBytes(getRequestAsString);
        }
    }
}
