#include <stdio.h>
#include <stdlib.h>

// Estrutura para representar uma aresta na lista de adjacências
typedef struct Aresta {
    int destino;
    struct Aresta *prox;
} Aresta;

// Estrutura para representar o grafo
typedef struct {
    int n, m;                  // Número de vértices e arestas
    Aresta **adjacencia;       // Lista de adjacência
    int *visitado, *TD, *TT;   // Rastreamento para busca em profundidade
} Grafo;

int tempo = 0;

// Função para criar o grafo
Grafo* cria_grafo(int n, int m) {
    Grafo *grafo = (Grafo *)malloc(sizeof(Grafo));
    grafo->n = n;
    grafo->m = m;
    grafo->adjacencia = (Aresta **)calloc(n + 1, sizeof(Aresta *));
    grafo->visitado = (int *)calloc(n + 1, sizeof(int));
    grafo->TD = (int *)calloc(n + 1, sizeof(int)); // Tempo de Descoberta
    grafo->TT = (int *)calloc(n + 1, sizeof(int)); // Tempo de Término
    return grafo;
}

// Função para adicionar uma aresta ao grafo
void adiciona_aresta(Grafo *grafo, int origem, int destino) {
    Aresta *nova = (Aresta *)malloc(sizeof(Aresta));
    nova->destino = destino;
    nova->prox = grafo->adjacencia[origem];
    grafo->adjacencia[origem] = nova;
}

// Função para realizar a busca em profundidade (DFS)
void buscaProfundidade(Grafo *grafo, int v) {
    grafo->visitado[v] = 1;
    grafo->TD[v] = ++tempo;

    Aresta *aresta = grafo->adjacencia[v];
    while (aresta != NULL) {
        int u = aresta->destino;
        if (!grafo->visitado[u]) {
            // Exibe apenas as arestas de árvore
            printf("Aresta de árvore: (%d -> %d)\n", v, u);
            buscaProfundidade(grafo, u);
        }
        aresta = aresta->prox;
    }

    grafo->TT[v] = ++tempo;
}

// Função para classificar as arestas de um vértice específico
void classifica_arestas(Grafo *grafo, int v) {
    printf("\nClassificando as arestas do vértice %d:\n", v);

    if (grafo->adjacencia[v] == NULL) {
        printf("O vértice %d não possui arestas de saída.\n", v);
        return;
    }

    Aresta *aresta = grafo->adjacencia[v];
    while (aresta != NULL) {
        int u = aresta->destino;

        if (grafo->TD[u] && !grafo->TT[u]) {
            printf("Aresta de retorno: (%d -> %d)\n", v, u);
        } else if (grafo->TT[u] && grafo->TD[v] > grafo->TD[u]) {
            printf("Aresta de cruzamento: (%d -> %d)\n", v, u);
        } else if (grafo->TT[u] && grafo->TD[v] < grafo->TD[u]) {
            printf("Aresta de avanço: (%d -> %d)\n", v, u);
        }

        aresta = aresta->prox;
    }
}

// Função para liberar a memória do grafo
void free_grafo(Grafo *grafo) {
    for (int i = 1; i <= grafo->n; i++) {
        Aresta *aresta = grafo->adjacencia[i];
        while (aresta != NULL) {
            Aresta *temp = aresta;
            aresta = aresta->prox;
            free(temp);
        }
    }
    free(grafo->adjacencia);
    free(grafo->visitado);
    free(grafo->TD);
    free(grafo->TT);
    free(grafo);
}

int main() {
    char nome_arquivo[100];
    int vertice_escolhido;

    // Entrada do nome do arquivo e do vértice escolhido
    printf("Digite o nome do arquivo: ");
    scanf("%s", nome_arquivo);
    printf("Digite o vértice: ");
    scanf("%d", &vertice_escolhido);

    // Abrindo o arquivo
    FILE *arquivo = fopen(nome_arquivo, "r");
    if (!arquivo) {
        printf("Erro ao abrir o arquivo.\n");
        return 1;
    }

    int n, m;
    fscanf(arquivo, "%d %d", &n, &m);

    // Criando o grafo
    Grafo *grafo = cria_grafo(n, m);

    // Lendo as arestas
    for (int i = 0; i < m; i++) {
        int origem, destino;
        fscanf(arquivo, "%d %d", &origem, &destino);
        adiciona_aresta(grafo, origem, destino);
    }
    fclose(arquivo);

    // Realizando a DFS para classificar as arestas
    tempo = 0;
    for (int i = 1; i <= n; i++) {
        if (!grafo->visitado[i]) {
            buscaProfundidade(grafo, i);
        }
    }

    // Classificando as arestas do vértice escolhido
    classifica_arestas(grafo, vertice_escolhido);

    // Liberando memória do grafo
    free_grafo(grafo);

    return 0;
}
