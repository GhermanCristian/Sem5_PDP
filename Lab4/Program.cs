using System.Threading;

namespace Lab4 {
    class Program {
        static void Main(string[] args) {
            SolveCallback solveCallback = new SolveCallback("www.cs.ubbcluj.ro/~forest/");
            solveCallback.start();
            Thread.Sleep(1500);
        }
    }
}
