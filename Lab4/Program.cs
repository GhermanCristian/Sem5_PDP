using System.Collections.Generic;

namespace Lab4 {
    class Program {
        static void Main(string[] args) {
            List<string> hosts = new List<string>();
            hosts.Add("www.cs.ubbcluj.ro/~forest/");
            hosts.Add("www.cs.ubbcluj.ro/~motogna/LFTC");
            hosts.Add("www.cs.ubbcluj.ro/~rlupsa/edu/pdp/");
            hosts.Add("www.cs.ubbcluj.ro/~ilazar/ma/");
            SolveCallback solveCallback = new SolveCallback(hosts);
        }
    }
}
