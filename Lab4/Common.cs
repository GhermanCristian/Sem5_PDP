using System;
using System.Text;

namespace Lab4 {
    class Common {
        public static int BUFFER_SIZE = 1024;
        public static byte[] getRequestContent(string host) {
            string requestEndpoint = host.Contains("/") ? host[host.IndexOf("/")..] : "/";
            string getRequestAsString = "GET " + requestEndpoint + " HTTP/1.1\r\nHost: " + host.Split('/')[0] + "\r\nContent-Length: 0\r\n\r\n";
            return Encoding.ASCII.GetBytes(getRequestAsString);
        }

        public static void interpretResponse(byte[] response) {
            string content = Encoding.ASCII.GetString(response, 0, Common.BUFFER_SIZE);
            string[] splitContent = content.Split('\r', '\n');
            string header = "";
            foreach(string line in splitContent) {
                if (line.Length == 0) {
                    continue;
                }

                if (line.StartsWith("Content-Length:")) {
                    header += "Length: " + line.Split(":")[1] + " bytes\n";
                }
                else {
                    header += line + "\n";
                    if (line.StartsWith("Content-Type:")) {
                        break;
                    }
                }
            }
            Console.WriteLine(header + "\n\n\n");
        }
    }
}
