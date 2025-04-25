/*
OBS.: código de lista adaptado da matéria de AEDS2
*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINHA 50


typedef struct No {
    int vertice;
    struct No* prox;
} No;

typedef struct {
    int numVertices;
    No** listaAdj;
    int* grauEntrada;
    int* grauSaida;
} Grafo;

Grafo* criarGrafo(int vertices) {
    Grafo* grafo = (Grafo*)malloc(sizeof(Grafo));
    grafo->numVertices = vertices;
    grafo->listaAdj = (No**)malloc(vertices * sizeof(No*));
    grafo->grauEntrada = (int*)calloc(vertices, sizeof(int));
    grafo->grauSaida = (int*)calloc(vertices, sizeof(int));
    
    for (int i = 0; i < vertices; i++) {
        grafo->listaAdj[i] = NULL;
    }
    return grafo;
}

void adicionarAresta(Grafo* grafo, int origem, int destino) {
    No* novoNo = (No*)malloc(sizeof(No));
    novoNo->vertice = destino;
    novoNo->prox = grafo->listaAdj[origem];
    grafo->listaAdj[origem] = novoNo;
    
    grafo->grauSaida[origem]++;
    grafo->grauEntrada[destino]++;
}

Grafo* lerGrafoArquivo(const char* nomeArquivo) {
    FILE* arquivo = fopen(nomeArquivo, "r");
    if (!arquivo) {
        printf("Erro ao abrir o arquivo!\n");
        return NULL;
    }
    
    int n, m;
    char linha[MAX_LINHA];
    
    fgets(linha, MAX_LINHA, arquivo);
    sscanf(linha, "%d %d", &n, &m);
    
    Grafo* grafo = criarGrafo(n);
    
    while (fgets(linha, MAX_LINHA, arquivo)) {
        int origem, destino;
        sscanf(linha, "%d %d", &origem, &destino);
        adicionarAresta(grafo, origem - 1, destino - 1);
    }
    
    fclose(arquivo);
    return grafo;
}

void imprimirListaAdjacencia(Grafo* grafo) {
    printf("Lista de Adjacencia:\n");
    for (int i = 0; i < grafo->numVertices; i++) {
        printf("%d -> ", i + 1);
        No* temp = grafo->listaAdj[i];
        while (temp) {
            printf("%d ", temp->vertice + 1);
            temp = temp->prox;
        }
        printf("\n");
    }
    printf("\n");
}

void imprimirInformacoes(Grafo* grafo, int vertice) {
    printf("\nInformacoes do vertice %d:\n", vertice + 1);
    printf("Grau de saida: %d\n", grafo->grauSaida[vertice]);
    printf("Grau de entrada: %d\n", grafo->grauEntrada[vertice]);
    
    printf("Sucessores: ");
    for (No* temp = grafo->listaAdj[vertice]; temp; temp = temp->prox) {
        printf("%d ", temp->vertice + 1);
    }
    printf("\n");
    
    printf("Predecessores: ");
    for (int i = 0; i < grafo->numVertices; i++) {
        for (No* temp = grafo->listaAdj[i]; temp; temp = temp->prox) {
            if (temp->vertice == vertice) {
                printf("%d ", i + 1);
                break;
            }
        }
    }
    printf("\n\n");
}

void liberarGrafo(Grafo* grafo) {
    for (int i = 0; i < grafo->numVertices; i++) {
        No* temp = grafo->listaAdj[i];
        while (temp) {
            No* prox = temp->prox;
            free(temp);
            temp = prox;
        }
    }
    free(grafo->listaAdj);
    free(grafo->grauEntrada);
    free(grafo->grauSaida);
    free(grafo);
}

int main() {
    char nomeArquivo[100];
    int vertice;
    
    printf("Digite o nome do arquivo: ");
    scanf("%s", nomeArquivo);
    printf("Digite o vertice desejado: ");
    scanf("%d", &vertice);
    
    Grafo* grafo = lerGrafoArquivo(nomeArquivo);
    if (!grafo) return 1;
    
    imprimirListaAdjacencia(grafo);
    
    if (vertice < 1 || vertice > grafo->numVertices) {
        printf("Vertice invalido!\n");
    } else {
        imprimirInformacoes(grafo, vertice - 1);
    }
    
    liberarGrafo(grafo);
    return 0;
}
