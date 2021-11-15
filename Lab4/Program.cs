using System.Collections.Generic;

namespace Lab4 {
    class Program {
        static void Main(string[] args) {
            List<string> hosts = new() {
                "www.cs.ubbcluj.ro/~forest/",
                "www.cs.ubbcluj.ro/~motogna/LFTC/",
                "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/",
                "www.cs.ubbcluj.ro/~ilazar/ma/"
            };
            //SolveCallback solveCallback = new (hosts);
            SolveTask solveTask = new (hosts);
            //SolveTaskAsync solveTaskAsync = new (hosts);
        }
    }
}
