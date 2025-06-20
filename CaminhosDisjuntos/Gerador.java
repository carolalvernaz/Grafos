import java.io.*;
import java.util.*;

public class Gerador {

    public static void gerarAleatorio(String filename, int V, int density, int source, int target) throws IOException {
        Random rand = new Random();
        Set<String> arestas = new HashSet<>();
        int E = V * density;

        while (arestas.size() < E) {
            int u = rand.nextInt(V);
            int v = rand.nextInt(V);
            if (u != v) {
                arestas.add(u + " " + v);
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(V + " " + arestas.size());
            for (String e : arestas) {
                out.println(e);
            }
            out.println("# " + source + " " + target);
        }
    }

    public static void gerarGrid(String filename, int V, int source, int target) throws IOException {
        int side = (int) Math.sqrt(V);
        int adjustedV = side * side;
        List<String> arestas = new ArrayList<>();

        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                int node = i * side + j;
                if (j + 1 < side) arestas.add(node + " " + (node + 1));
                if (i + 1 < side) arestas.add(node + " " + (node + side));
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(adjustedV + " " + arestas.size());
            for (String e : arestas) {
                out.println(e);
            }
            out.println("# " + source + " " + target);
        }
    }

    public static void main(String[] args) {
        int[] sizes = {100, 1444, 10000, 49729};

        try {
            for (int size : sizes) {
                gerarAleatorio("random_" + size + ".txt", size, 3, 0, size - 1);
                int adjusted = (int) Math.pow((int) Math.sqrt(size), 2);
                gerarGrid("grid_" + adjusted + ".txt", adjusted, 0, adjusted - 1);
            }
            System.out.println("Arquivos de grafos gerados com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivos: " + e.getMessage());
        }
    }
}
