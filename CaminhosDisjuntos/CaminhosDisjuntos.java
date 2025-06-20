import java.io.*;
import java.util.*;

public class CaminhosDisjuntos {

    static class Aresta {
        int to;
        boolean used;
        Aresta(int to) {
            this.to = to;
            this.used = false;
        }
    }

    static class Grafo {
        int V;
        List<Aresta>[] adj;

        @SuppressWarnings("unchecked")
        Grafo(int V) {
            this.V = V;
            adj = new ArrayList[V];
            for (int i = 0; i < V; i++) {
                adj[i] = new ArrayList<>();
            }
        }

        void addAresta(int u, int v) {
            adj[u].add(new Aresta(v)); // Grafo direcionado
        }
    }

    static boolean bfs(Grafo g, int s, int t, int[] parent, int[] parentAresta) {
        Arrays.fill(parent, -1);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        parent[s] = s;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int i = 0; i < g.adj[u].size(); i++) {
                Aresta e = g.adj[u].get(i);
                if (!e.used && parent[e.to] == -1) {
                    parent[e.to] = u;
                    parentAresta[e.to] = i;
                    if (e.to == t) return true;
                    queue.add(e.to);
                }
            }
        }
        return false;
    }

    static List<List<Integer>> findDisjointPaths(Grafo g, int s, int t) {
        int[] parent = new int[g.V];
        int[] parentAresta = new int[g.V];
        List<List<Integer>> paths = new ArrayList<>();

        while (bfs(g, s, t, parent, parentAresta)) {
            List<Integer> path = new ArrayList<>();
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                Aresta e = g.adj[u].get(parentAresta[v]);
                e.used = true;
                path.add(v);
            }
            path.add(s);
            Collections.reverse(path);
            paths.add(path);
        }
        return paths;
    }

    static Grafo readGrafoFromFile(String filename, int[] sourceTarget) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String[] header = br.readLine().split(" ");
        int V = Integer.parseInt(header[0]);
        int A = Integer.parseInt(header[1]);
        Grafo g = new Grafo(V);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                String[] st = line.substring(1).trim().split(" ");
                sourceTarget[0] = Integer.parseInt(st[0]);
                sourceTarget[1] = Integer.parseInt(st[1]);
            } else {
                String[] parts = line.split(" ");
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                g.addAresta(u, v);
            }
        }
        br.close();
        return g;
    }

    public static void main(String[] args) throws IOException {
        String[] arquivos = {
            "random_100.txt",
            "random_1444.txt",
            "random_10000.txt",
            "random_49729.txt",
            "grid_100.txt",
            "grid_1444.txt",
            "grid_10000.txt",
            "grid_49729.txt"
        };

        for (String arquivo : arquivos) {
            System.out.println("\nArquivo: " + arquivo);
            int[] sourceTarget = new int[2];
            Grafo g = readGrafoFromFile(arquivo, sourceTarget);
            int source = sourceTarget[0], target = sourceTarget[1];

            long start = System.currentTimeMillis();
            List<List<Integer>> paths = findDisjointPaths(g, source, target);
            long end = System.currentTimeMillis();

            //System.out.println("Origem: " + source + " → Destino: " + target);
            System.out.println("Caminhos disjuntos encontrados: " + paths.size());
            for (List<Integer> path : paths) {
                System.out.println(path);
            }
            System.out.println("Tempo de execução: " + (end - start) + " ms");
        }
    }
}