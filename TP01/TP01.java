import com.opencsv.CSVReader;
import java.io.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

interface Globalvals {
    int ID_Global = 1001;
}

class Filme implements Serializable {
    private Integer id;
    private static final long serialVersionUID = 1L;
    private String nome;
    private Date ano;
    private char[] certificado;
    private String[] genero;
    private Float nota;
    private Integer segmento;
    private boolean excluido;

    public Filme() {
        this.id = -1;
        this.nome = "";
        this.ano = null;
        this.nota = null;
        this.certificado = new char[3];
        this.genero = null;
        this.segmento = 0;
        this.excluido = false;
    }

    public Filme(Integer id, String nome, Date ano, char[] certificado, String[] genero, Float nota) {
        this.id = id;
        this.nome = nome;
        this.ano = ano;
        this.nota = nota;
        this.certificado = certificado;
        this.genero = genero;
        this.segmento = 0;
        this.excluido = false;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getAno() {
        return ano;
    }

    public void setAno(Date ano) {
        this.ano = ano;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }

    public char[] getCertificado() {
        return certificado;
    }

    public void setCertificado(char[] certificado) {
        this.certificado = certificado;
    }

    public String[] getGenero() {
        return genero;
    }

    public void setGenero(String[] genero) {
        this.genero = genero;
    }

    public Integer getSegmento() {
        return segmento;
    }

    public void setSegmento(Integer segmento) {
        this.segmento = segmento;
    }

    public boolean isExcluido() {
        return excluido;
    }

    public void setExcluido(boolean excluido) {
        this.excluido = excluido;
    }

    public int getTamanhoRegistro() {
        int tamanho = 0;
        tamanho += Integer.BYTES; // tamanho do campo "id"
        tamanho += Long.BYTES; // tamanho do campo "serialVersionUID"
        tamanho += Integer.BYTES + ((nome == null) ? 0 : nome.getBytes().length); // tamanho do campo "nome"
        tamanho += Long.BYTES; // tamanho do campo "ano"
        tamanho += Character.BYTES * certificado.length; // tamanho do campo "certificado"
        tamanho += Integer.BYTES + ((genero == null) ? 0 : genero.length * Integer.BYTES + getTamanhoTotalGeneros()); // tamanho
                                                                                                                      // do
                                                                                                                      // campo
                                                                                                                      // genero
        tamanho += Float.BYTES; // tamanho do campo "nota"
        tamanho += Integer.BYTES; // tamanho do campo "segmento"
        tamanho += 1; // tamanho do campo "excluido" que é um boolean
        return tamanho;
    }

    private int getTamanhoTotalGeneros() {
        int tamanhoTotal = 0;
        for (String g : genero) {
            tamanhoTotal += (g == null) ? 0 : g.getBytes().length;
        }
        return tamanhoTotal;
    }

    public String toString() {
        String resp = "";
        SimpleDateFormat Date = new SimpleDateFormat("yyyy");

        resp = this.id + ", " + this.nome + ", "
                + Date.format(this.ano) + ", " + this.nota + ", " + Arrays.toString(this.genero) + ", "
                + Arrays.toString(this.certificado);

        return resp;
    }
}

public class TP01 {
    public static Filme[] ReadCSV() throws Exception {
        SimpleDateFormat Data = new SimpleDateFormat("yyyy");
        File path = new File("./IMDB.csv");
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);

        Filme[] filmes = new Filme[1010];
        int id = 1;
        char[] certificado = new char[3];
        Date ano = null;

        try {
            CSVReader reader = new CSVReader(br);
            String[] linha = reader.readNext(); // passa direto pelo sumario
            while ((linha = reader.readNext()) != null) {
                // System.out.println(linha[1] + " " + linha[2] + " " + linha[3] + " " +
                // linha[5] + " " + linha[6] + " ");
                if (linha[3] == null || linha[3].length() > 3)
                    linha[3] = "NOT";
                for (int i = 0; i < linha[3].length(); i++) {
                    certificado[i] = linha[3].charAt(i);
                }
                if (linha[3].length() == 2) {
                    certificado[2] = ' ';
                }
                if (linha[3].length() == 1) {
                    certificado[1] = ' ';
                    certificado[2] = ' ';
                }

                if (linha[2] == null || linha[2].charAt(0) == 'P') {
                    linha[2] = "2000";
                }
                if (linha[6] == null) {
                    linha[2] = "1.0";
                }
                String[] generos = linha[5].split(",");
                ano = Data.parse(linha[2]);
                filmes[id - 1] = new Filme(id, linha[1], ano, certificado, generos,
                        Float.parseFloat(linha[6]));
                System.out.println(filmes[id - 1].toString());
                id++;
            }
            reader.close();
            System.out.println("Base de dados carregada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filmes;
    }

    public static void writeFile(Filme[] movie) throws Exception {
        if (movie[0] == null) {
            System.out.println("Seu banco de dados está vazio!");
            return;
        }
        String path = "data/Imdb.bin";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(movie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Filme buscaFilmePorId(int id) {
        String path = "data/Imdb.bin";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Filme[] filmes = (Filme[]) ois.readObject();
            for (Filme filme : filmes) {
                if (filme != null && filme.getId() == id && !filme.isExcluido()) {
                    return filme;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insereFilme(Filme filme) {
        String path = "data/Imdb.bin";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Filme[] filmes = (Filme[]) ois.readObject();
            // Encontra a posição onde o novo objeto deve ser adicionado
            int posicao = filmes.length;
            for (int i = 0; i < filmes.length; i++) {
                if (filmes[i] == null || filmes[i].isExcluido()) {
                    posicao = i;
                    break;
                }
            }
            // Adiciona o novo objeto ao final do vetor
            if (posicao < filmes.length) {
                filmes[posicao] = filme;
            } else {
                filmes = Arrays.copyOf(filmes, filmes.length + 1);
                filmes[posicao] = filme;
            }
            // Escreve o vetor atualizado no arquivo
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(filmes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void alterar() throws ParseException {
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat Data = new SimpleDateFormat("yyyy");
        int id = 0;

        System.out.println("Qual é ID do filme a ser atualizado?  ");
        if (sc.hasNextInt())
            id = sc.nextInt(); // Leitura do id
        sc.nextLine(); // Limpar o buffer do scanner

        System.out.println("Qual o nome do filme a ser atualizado?  ");
        String nome = sc.nextLine(); // Leitura do nome

        System.out.println("Qual ano de lançamento do filme a ser atualizado?  ");
        String ano1 = sc.nextLine();
        Date ano = Data.parse(ano1); // Leitura do ano

        System.out.println("Qual é certificado do filme a ser atualizado?  ");
        String certificadoString = sc.nextLine();
        char[] certificado = new char[3];
        for (int i = 0; i < certificadoString.length(); i++) {
            certificado[i] = certificadoString.charAt(i);
        }

        System.out.println("Quais sao os generos do filme a ser atualizado?  ");
        String generoString = sc.nextLine();
        String[] generos = generoString.split(",");

        System.out.println("Qual a nota do filme a ser atualizado?  ");
        Float nota = Float.parseFloat(sc.nextLine()); // Leitura da nota

        Filme alterado = new Filme(id, nome, ano, certificado, generos, nota);
        sc.close();
        atualizaFilme(alterado);
    }

    public static void atualizaFilme(Filme filme) {
        excluiFilme(filme.getId()); // exclui o registro antigo
        insereFilme(filme); // insere o novo registro no final do arquivo
    }

    public static void excluiFilme(int id) {
        String path = "data/Imdb.bin";
        String tempPath = "data/Imdb_temp.bin";
        Filme[] filmes = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            filmes = (Filme[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (filmes != null) {
            for (Filme f : filmes) {
                if (f != null && f.getId() == id) {
                    f.setExcluido(true);
                    break;
                }
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempPath))) {
                oos.writeObject(filmes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File file = new File(path);
            File tempFile = new File(tempPath);
            if (file.delete()) {
                tempFile.renameTo(file);
            }
        }
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        int choice = 0;
        int id = 0;
        Filme[] filmes = new Filme[1100];

        while (choice != 6) {
            // Exibição do menu de opçoes do programa
            System.out.println("Selecione uma operação:");
            System.out.println("1. Carregar base de dados");
            System.out.println("2. Ler um registro existente");
            System.out.println("3. Atualizar um registro existente");
            System.out.println("4. Excluir um registro existente");
            System.out.println("5. Ordenação externa");
            System.out.println("6. Sair");

            if (sc.hasNextInt())
                choice = sc.nextInt(); // Leitura da escolha do usuario
            sc.nextLine(); // Limpar o buffer do scanner

            switch (choice) {
                case 1:
                    System.out.println("Carregando base de dados...");
                    filmes = ReadCSV();
                    writeFile(filmes);
                    break;
                case 2:
                    System.out.println("Qual ID de filme voce quer buscar?  ");
                    id = sc.nextInt(); // Leitura do id
                    sc.nextLine(); // Limpar o buffer do scanner
                    if (buscaFilmePorId(id) != null)
                        System.out.println(buscaFilmePorId(id));
                    else
                        System.out.println("O filme não foi encontrado!");
                    break;
                case 3:
                    alterar();
                    break;
                case 4:
                    System.out.println("Qual ID do filme a ser excluido?   ");
                    id = sc.nextInt(); // Leitura do id
                    sc.nextLine(); // Limpar o buffer do scanner
                    excluiFilme(id);
                    break;
                case 5:
                    System.out.println("Caso 5..");
                    break;
                case 6:
                    // Saindo do programa
                    System.out.println("Saindo do programa...");
                    break;
                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
                    break;
            }
        }
        sc.close();
    }
}