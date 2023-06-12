// Max-Heap data structure in Java

import java.util.ArrayList;

class Heap {
  void organiza(ArrayList<Filme> arranjo, int i) {
    /* Funcao que organiza um array composto por segmentos 0 */
    int size = arranjo.size();
    int menor = i;
    int l = 2 * i + 1;
    int r = 2 * i + 2;
    if (l < size && arranjo.get(l).getId() < arranjo.get(menor).getId())
      menor = l;
    if (r < size && arranjo.get(r).getId() < arranjo.get(menor).getId())
      menor = r;

    if (menor != i && arranjo.get(menor).getSegmento() == 0) {
      Filme temp = arranjo.get(menor);
      arranjo.set(menor, arranjo.get(i));
      arranjo.set(i, temp);

      organiza(arranjo, menor);
    }
  }

  void organizaComSegmento(ArrayList<Filme> arranjo, int i) {
    /* Funcao que organiza um array composto por segmentos 1 */
    int size = arranjo.size();
    int menor = i;
    int l = 2 * i + 1;
    int r = 2 * i + 2;
    if (l < size && arranjo.get(l).getId() < arranjo.get(menor).getId())
      menor = l;
    if (r < size && arranjo.get(r).getId() < arranjo.get(menor).getId())
      menor = r;

    if (menor != i && arranjo.get(menor).getSegmento() == 1) {
      Filme temp = arranjo.get(menor);
      arranjo.set(menor, arranjo.get(i));
      arranjo.set(i, temp);

      organiza(arranjo, menor);
    }
  }

  void insert(ArrayList<Filme> arranjo, Filme newFilme, boolean segmento) {
    /* Funcao que insere elemento no array passado */
    int size = arranjo.size();
    if (size == 0) {
      arranjo.add(newFilme);
    } else {
      arranjo.add(newFilme);
      if(segmento == false){
        for (int i = size / 2 - 1; i >= 0; i--) {
          organiza(arranjo, i);
        }
      } else{
        for (int i = size / 2 - 1; i >= 0; i--) {
          organizaComSegmento(arranjo, i);
        }
      }
    }
  }

  void delete(ArrayList<Filme> arranjo, int num, boolean segmento)
  /* Funcao que deleta elemento no array passado */
  {
    int size = arranjo.size();
    int i;
    boolean trocouSegmento = true; 
    for (i = 0; i < size; i++)
    {
      if (num == arranjo.get(i).getId())
        break;
    }

    Filme temp = arranjo.get(i);
    if(arranjo.get(size-1).getSegmento() == 0){
      arranjo.set(i, arranjo.get(size-1));
      arranjo.set(size-1, temp);
      arranjo.remove(size-1);
      trocouSegmento = false;
    } else {
      for (int b = 0; b<size; b++){
        if(arranjo.get(b).getId() != arranjo.get(i).getId() &&
        arranjo.get(b).getSegmento() == 0){
          arranjo.set(i, arranjo.get(b));
          arranjo.set(b, temp);
          arranjo.remove(b);
          trocouSegmento = false;
          break;
        }
      }
    }

    if(trocouSegmento == true){
        arranjo.set(i, arranjo.get(size-1));
        arranjo.set(size-1, temp);
        arranjo.remove(size-1);
    }

    if(segmento == false){
      for (int j = size / 2 - 1; j >= 0; j--)
      {
        organiza(arranjo, j);
      }
    } else{
      for (int j = size / 2 - 1; j >= 0; j--)
      {
        organizaComSegmento(arranjo, j);
      }
    }
  }

  void printArray(ArrayList<Filme> array, int size) {
    /* Funcao que printa o array passado */
    for (Filme i : array) {
      System.out.print(i.getId() + " ");
    }
    System.out.println();
  }
}
