import java.io.*;
import java.util.*;

public class CaminhoMinimo{

    static class Aresta {
        int destino, peso;

        Aresta(int destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    static class Par implements Comparable<Par> {
        int vertice, custo, arestas;

        Par(int vertice, int custo, int arestas) {
            this.vertice = vertice;
            this.custo = custo;
            this.arestas = arestas;
        }

        public int compareTo(Par outro) {
            if (this.custo != outro.custo) return Integer.compare(this.custo, outro.custo);
            return Integer.compare(this.arestas, outro.arestas);
        }
    }

    static int[] dijkstraMenorAresta(List<List<Aresta>> grafo, int origem, int destino, int[] dist, int[] arestas, int[] anterior) {
        int n = grafo.size();
        boolean[] visitado = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(arestas, Integer.MAX_VALUE);
        Arrays.fill(anterior, -1);

        PriorityQueue<Par> fila = new PriorityQueue<>();
        dist[origem] = 0;
        arestas[origem] = 0;
        fila.offer(new Par(origem, 0, 0));

        while (!fila.isEmpty()) {
            Par atual = fila.poll();
            int u = atual.vertice;
            if (visitado[u]) continue;
            visitado[u] = true;

            for (Aresta a : grafo.get(u)) {
                int v = a.destino;
                int novoCusto = dist[u] + a.peso;
                int novasArestas = arestas[u] + 1;

                if (novoCusto < dist[v] || (novoCusto == dist[v] && novasArestas < arestas[v])) {
                    dist[v] = novoCusto;
                    arestas[v] = novasArestas;
                    anterior[v] = u;
                    fila.offer(new Par(v, dist[v], arestas[v]));
                }
            }
        }
        return anterior;
    }

    static void imprimirCaminho(int[] anterior, int destino) {
        List<Integer> caminho = new ArrayList<>();
        for (int at = destino; at != -1; at = anterior[at]) {
            caminho.add(at);
        }
        Collections.reverse(caminho);
        System.out.println("Caminho: " + caminho);
    }

    static void gerarGrafoCompleto(int n, String arquivo) throws IOException {
        Random rand = new Random();
        try (PrintWriter pw = new PrintWriter(arquivo)) {
            pw.println(n + " " + (n * (n - 1)));
            for (int u = 0; u < n; u++) {
                for (int v = 0; v < n; v++) {
                    if (u != v) {
                        int peso = 1 + rand.nextInt(20);
                        pw.println(u + " " + v + " " + peso);
                    }
                }
            }
            int origem = rand.nextInt(n);
            int destino = rand.nextInt(n);
            while (origem == destino) destino = rand.nextInt(n);
            pw.println(origem + " " + destino);
        }
        System.out.println("Grafo completo com " + n + " vertices gerado em: " + arquivo);
    }

    static void gerarGrafoComCiclos(int n, int extraArestas, String arquivo) throws IOException {
        Random rand = new Random();
        Set<String> arestas = new HashSet<>();
        try (PrintWriter pw = new PrintWriter(arquivo)) {
            List<String> lista = new ArrayList<>();

            // Criar um ciclo principal
            for (int i = 0; i < n - 1; i++) {
                int peso = 1 + rand.nextInt(20);
                lista.add(i + " " + (i + 1) + " " + peso);
                arestas.add(i + "," + (i + 1));
            }
            // Fechar o ciclo
            int peso = 1 + rand.nextInt(20);
            lista.add((n - 1) + " " + 0 + " " + peso);
            arestas.add((n - 1) + "," + 0);

            // Adicionar arestas extras aleatórias
            while (arestas.size() < n + extraArestas) {
                int u = rand.nextInt(n);
                int v = rand.nextInt(n);
                if (u != v && arestas.add(u + "," + v)) {
                    peso = 1 + rand.nextInt(20);
                    lista.add(u + " " + v + " " + peso);
                }
            }

            pw.println(n + " " + lista.size());
            for (String linha : lista) pw.println(linha);

            int origem = rand.nextInt(n);
            int destino = rand.nextInt(n);
            while (origem == destino) destino = rand.nextInt(n);
            pw.println(origem + " " + destino);
        }
        System.out.println("Grafo com ciclos com " + n + " vertices gerado em: " + arquivo);
    }

    public static void main(String[] args) throws IOException {
        int[] tamanhos = {100, 500, 1000, 1500};
        for (int tamanho : tamanhos) {
            String arquivoCompleto = "grafo_completo_" + tamanho + ".txt";
            String arquivoCiclos = "grafo_ciclos_" + tamanho + ".txt";

            gerarGrafoCompleto(tamanho, arquivoCompleto);
            gerarGrafoComCiclos(tamanho, tamanho / 2, arquivoCiclos);

            // Testar grafo completo
            long inicio = System.currentTimeMillis();
            executarTeste(arquivoCompleto);
            long fim = System.currentTimeMillis();
            System.out.println("Tempo para grafo completo (" + tamanho + "): " + (fim - inicio) + " ms\n");

            // Testar grafo com ciclos
            inicio = System.currentTimeMillis();
            executarTeste(arquivoCiclos);
            fim = System.currentTimeMillis();
            System.out.println("Tempo para grafo com ciclos (" + tamanho + "): " + (fim - inicio) + " ms\n");
        }
    }

    static void executarTeste(String nomeArquivo) throws IOException {
        Scanner sc = new Scanner(new File(nomeArquivo));

        int n = sc.nextInt();
        int m = sc.nextInt();
        List<List<Aresta>> grafo = new ArrayList<>();
        for (int i = 0; i < n; i++) grafo.add(new ArrayList<>());

        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();
            grafo.get(u).add(new Aresta(v, w));
        }

        int origem = sc.nextInt();
        int destino = sc.nextInt();

        int[] dist = new int[n];
        int[] arestas = new int[n];
        int[] anterior = new int[n];

        dijkstraMenorAresta(grafo, origem, destino, dist, arestas, anterior);

        if (dist[destino] == Integer.MAX_VALUE) {
            System.out.println("Nao existe caminho entre " + origem + " e " + destino);
        } else {
            System.out.println("Custo minimo: " + dist[destino]);
            System.out.println("Numero de arestas: " + arestas[destino]);
            imprimirCaminho(anterior, destino);
        }
    }
}
