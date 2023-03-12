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

    // Inicio dos getters e setters
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
    // Fim dos getters e setters

    // Metodo para saber o tamanho total em BYTES de um determinado filme
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

    // Metodo para saber o tamanho em BYTES de um determinado genero
    private int getTamanhoTotalGeneros() {
        int tamanhoTotal = 0;
        for (String g : genero) {
            tamanhoTotal += (g == null) ? 0 : g.getBytes().length;
        }
        return tamanhoTotal;
    }

    // Metodo para printar na tela o filme selecionado
    public String toString() {
        String resp = "";
        SimpleDateFormat Date = new SimpleDateFormat("yyyy");

        resp = this.id + ", " + this.nome + ", "
                + Date.format(this.ano) + ", " + this.nota + ", " + Arrays.toString(this.genero) + ", "
                + Arrays.toString(this.certificado);

        return resp;
    }

    // Tranformar qualquer tipo em binario e retorna-lo em formado de array
    public byte[] toByte() throws IOException {
        SimpleDateFormat dataForm = new SimpleDateFormat("yyyy");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Junta todos os argumentos
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(dataForm.format(this.ano));
        dos.writeFloat(this.nota);
        dos.writeUTF(Arrays.toString(this.certificado));
        dos.writeUTF(Arrays.toString(this.genero));

        // Devolve esses argumentos no tipo Byte
        return baos.toByteArray();
    }

    // converter binario para seu respectivo tipo
    public void fromByte(byte ba[]) throws Exception {
        SimpleDateFormat dataForm = new SimpleDateFormat("yyyy");
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        // Converte todos os dados que estao em Byte e joga novamente dentro da classe
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.ano = dataForm.parse(dis.readUTF());
        this.nota = dis.readFloat();

        String[] certificadoString = dis.readUTF().split(",");

        for (int i = 0; i < certificado.length; i++) {
            if (certificadoString[i].charAt(0) != '[') {
                this.certificado[i] = certificadoString[i].charAt(0);
            } else {
                this.certificado[i] = certificadoString[i].charAt(1);
            }
        }

        String generoLinha = dis.readUTF();
        generoLinha = generoLinha.replace("[", "");
        generoLinha = generoLinha.replace("]", "");
        this.genero = generoLinha.split(",");

    }
}

public class TP01 {

    public static Filme[] ReadCSV() throws Exception {
        // Variaveis iniciais do metodo
        SimpleDateFormat Data = new SimpleDateFormat("yyyy");
        File path = new File("./IMDB.csv");
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);

        Filme[] filmes = new Filme[1010];
        int id = 1;
        char[] certificado = new char[3];
        Date ano = null;

        // esta pegando todas as informaçoes e jogando dentro das variaveis
        try {
            CSVReader reader = new CSVReader(br);
            String[] linha = reader.readNext(); // passa direto pelo sumario
            while ((linha = reader.readNext()) != null) {
                // System.out.println(linha[1] + " " + linha[2] + " " + linha[3] + " " +
                // linha[5] + " " + linha[6] + " ");
                // Validando o certificado
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
                // Caso o ano seja nulo ou "PG" (que ocorre uma unica vez não sei o porque), ele
                // coloca um valor apenas para popular o banco
                if (linha[2] == null || linha[2].charAt(0) == 'P') {
                    linha[2] = "2000";
                }
                // mesma coisa ocorre com a nota
                if (linha[6] == null) {
                    linha[2] = "1.0";
                }
                // dando split nos generos para serem alocados em um vetor
                String[] generos = linha[5].split(",");
                // passando a data pro tipo certo
                ano = Data.parse(linha[2]);
                // Apos pegar todas as informaçoes ele joga dentro do vetor de filmes
                filmes[id - 1] = new Filme(id, linha[1], ano, certificado, generos,
                        Float.parseFloat(linha[6]));
                System.out.println(filmes[id - 1].toString());
                // Aumenta o valor do id, para alocar o proximo filme
                id++;
            }
            reader.close();
            System.out.println("Base de dados carregada com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Retorna todos os filmes apos ler o CSV
        return filmes;
    }

    public static void writeFile(Filme[] movie) throws Exception {
        // Verificaçao inicial para ver se existe o banco de dados
        if (movie[0] == null) {
            System.out.println("Seu banco de dados está vazio!");
            return;
        }
        String path = "data/Imdb.bin";
        // Abre o arquivo e escreve todos os filmes por meio do vetor
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(movie);
            // catch para debuggar o codigo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Filme buscaFilmePorId(int id) {
        String path = "data/Imdb.bin";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Filme[] filmes = (Filme[]) ois.readObject();
            // Varre os filmes em busca do ID
            for (Filme filme : filmes) {
                if (filme != null && filme.getId() == id && !filme.isExcluido()) {
                    // retorna o filme encontrado
                    return filme;
                }
            }
            // Caso não ache nd ele cai nessa exceção
        } catch (EOFException e) {
            System.out.println("Não foi encontrado o registro");
            // Catch muito util pra debuggar o codigo
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // caso nao encontre nada, ele retorna um null
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
        // Abrindo variaveis padroes
        Scanner scan = new Scanner(System.in);
        SimpleDateFormat Data = new SimpleDateFormat("yyyy");
        int id = 0;

        // O console ira perguntar ao usuario cada uma das especificaçoes de um filme
        System.out.println("Qual é ID do filme a ser atualizado?  ");
        if (scan.hasNextInt())
            id = scan.nextInt(); // Leitura do id
        scan.nextLine(); // Limpar o buffer do scanner

        System.out.println("Qual o nome do filme a ser atualizado?  ");
        String nome = scan.nextLine(); // Leitura do nome

        System.out.println("Qual ano de lançamento do filme a ser atualizado?  ");
        String ano1 = scan.nextLine();
        Date ano = Data.parse(ano1); // Leitura do ano

        System.out.println("Qual é certificado do filme a ser atualizado?  ");
        String certificadoString = scan.nextLine();
        char[] certificado = new char[3];
        for (int i = 0; i < certificadoString.length(); i++) {
            certificado[i] = certificadoString.charAt(i);
        }

        System.out.println("Quais sao os generos do filme a ser atualizado?  ");
        String generoString = scan.nextLine();
        String[] generos = generoString.split(",");

        System.out.println("Qual a nota do filme a ser atualizado?  ");
        Float nota = Float.parseFloat(scan.nextLine()); // Leitura da nota

        // Criasse um novo Filme para pegar o lugar do antigo
        Filme alterado = new Filme(id, nome, ano, certificado, generos, nota);
        scan.close(); // fechamento do scanner
        atualizaFilme(alterado);
    }

    public static void atualizaFilme(Filme filme) {
        excluiFilme(filme.getId()); // exclui o registro antigo
        insereFilme(filme); // insere o novo registro no final do arquivo
    }

    // Metodo para excluir um determinado filme pelo ID dele
    public static void excluiFilme(int id) {
        // variaveis padroes do sistema
        String path = "data/Imdb.bin";
        String tempPath = "data/Imdb_temp.bin";
        Filme[] filmes = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            filmes = (Filme[]) ois.readObject(); // pega todos os registros do Imdb.bin e os separa em objetos
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); // Catch para debuggar o codigo
        }
        // Verifica se existe o array de filmes
        if (filmes != null) {
            // para cada objeto uma variavel f é usada
            for (Filme f : filmes) {
                if (f != null && f.getId() == id) {
                    f.setExcluido(true); // Marcaçao da lapide do registro
                    break;
                }
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempPath))) {
                oos.writeObject(filmes); // escreve um arquivo temporario que tem a lapide do registro excluido
            } catch (IOException e) {
                e.printStackTrace(); // Catch para debuggar o codigo
            }
            // Vai fazer a troca do arquivo imdb.bin pelo arquivo Imdb_temp.bin (arquivo
            // cujo a lapide foi feita)
            File file = new File(path);
            File tempFile = new File(tempPath);
            if (file.delete()) {
                tempFile.renameTo(file);
            }
        }
    }

    // Faz uma ordenaçao em booble sort
    public static Filme[] ordenar(Filme[] ordenados) {
        int menor = 0;
        for (int a = 1; a < ordenados.length - 1; a++) {
            menor = a;
            for (int b = a + 1; b < ordenados.length; b++) {
                if (ordenados[b] != null && ordenados[menor] != null) {
                    if (ordenados[b].getId() < ordenados[menor].getId()) {
                        menor = b;
                    }
                }
            }
            Filme temp = ordenados[menor];
            ordenados[menor] = ordenados[a];
            ordenados[a] = temp;
        }
        // retorna os filmes em ordem
        return ordenados;
    }

    // Intercalaçao balenceada comum
    public static Filme[] IntercalacaoBalanceada(int bloco, int n, Filme[] imdb) throws Exception {
        int contBloco = 0;
        int TAMANHO_BLOCO = bloco * n; // para saber o tamanho dos blocos
        Filme[] ordenados = new Filme[bloco];

        RandomAccessFile arq1 = new RandomAccessFile("Data/temp/arq1.tmp", "rw"); // Arquivo temporario 1
        RandomAccessFile arq2 = new RandomAccessFile("Data/temp/arq2.tmp", "rw"); // Arquivo temporario 2
        boolean switchArq1 = true; // boolean para saber a troca dos arquivos

        // For para fazer a separaçao das informaçoes em blocos menores
        for (int i = 0; i < imdb.length; i++) {
            if (imdb[i] != null) {
                if (contBloco == bloco) {
                    ordenados = ordenar(ordenados);
                    if (switchArq1) {
                        byte[] ba;
                        int cont = 0;
                        while (cont < ordenados.length) {
                            if (ordenados[cont] != null) {
                                ba = ordenados[cont].toByte();
                                arq1.writeInt(ba.length);
                                arq1.write(ba);
                            }
                            cont++;
                        }
                        for (int a = 0; a < ordenados.length; a++) {
                            ordenados[a] = null;
                        }
                        switchArq1 = false;
                        i--;
                        contBloco = 0;
                    } else {
                        byte[] ba;
                        int cont = 0;
                        while (cont < ordenados.length) {
                            if (ordenados[cont] != null) {
                                ba = ordenados[cont].toByte();
                                arq2.writeInt(ba.length);
                                arq2.write(ba);
                            }
                            cont++;
                        }
                        for (int a = 0; a < ordenados.length; a++) {
                            ordenados[a] = null;
                        }
                        switchArq1 = true;
                        i--;
                        contBloco = 0;
                    }
                } else {
                    ordenados[contBloco] = imdb[i];
                    contBloco++;
                }
            } else {
                if (ordenados[0] != null) {
                    ordenados = ordenar(ordenados);
                    if (switchArq1) {
                        byte[] ba;
                        int cont = 0;
                        while (cont < ordenados.length) {
                            if (ordenados[cont] != null) {
                                ba = ordenados[cont].toByte();
                                arq1.writeInt(ba.length);
                                arq1.write(ba);
                            }
                            cont++;
                        }
                        ordenados[0] = null;
                    } else {
                        byte[] ba;
                        int cont = 0;

                        while (cont < ordenados.length) {
                            if (ordenados[cont] != null) {
                                ba = ordenados[cont].toByte();
                                arq2.writeInt(ba.length);
                                arq2.write(ba);
                            }
                            cont++;
                        }
                        ordenados[0] = null;
                    }
                }
            }
        }

        Filme[] temp1 = lerTemp("Data/temp/arq1.tmp"); // Lendo o arquivo temporario 1
        Filme[] temp2 = lerTemp("Data/temp/arq2.tmp"); // Lendo o arquivo temporario 2

        RandomAccessFile arq3 = new RandomAccessFile("Data/temp/arq3.tmp", "rw");
        RandomAccessFile arq4 = new RandomAccessFile("Data/temp/arq4.tmp", "rw");
        boolean switchArq2 = true;

        int nBloco1 = 0; // Numero de blocos 1
        int nBloco2 = 0; // Numero de blocos 2
        int nEtapas = 0; // Numero de etapas
        int pont1 = 0; // ponteiro 1
        int pont2 = 0; // ponterio 2
        Filme[] etapa2 = new Filme[(TAMANHO_BLOCO) * 2];

        // Primeira intercalaçao
        // Se basei em juntar blocos menores em blocos maiores de forma a se agrupar em
        // ordem
        while (temp1[pont1] != null && temp2[pont2] != null) {
            while (nBloco1 < TAMANHO_BLOCO || nBloco2 < TAMANHO_BLOCO) {
                // Faz cada uma das verificaçoes para juntar ou trocas os objetos
                if (temp1[pont1] != null && temp2[pont2] == null && nBloco1 < TAMANHO_BLOCO) {
                    etapa2[nEtapas] = temp1[pont1];
                    nBloco1++;
                    nEtapas++;
                    pont1++;
                } else if (temp2[pont2] != null && temp1[pont1] == null && nBloco2 < TAMANHO_BLOCO) {
                    etapa2[nEtapas] = temp2[pont2];
                    nBloco2++;
                    nEtapas++;
                    pont2++;
                } else if (temp1[pont1] == null && temp2[pont2] == null) {
                    nBloco1 = TAMANHO_BLOCO;
                    nBloco2 = TAMANHO_BLOCO;
                } else if (nBloco1 < TAMANHO_BLOCO && nBloco2 >= TAMANHO_BLOCO) {
                    etapa2[nEtapas] = temp1[pont1];
                    nBloco1++;
                    nEtapas++;
                    pont1++;
                } else if (nBloco2 < TAMANHO_BLOCO && nBloco1 >= TAMANHO_BLOCO) {
                    etapa2[nEtapas] = temp2[pont2];
                    nBloco2++;
                    nEtapas++;
                    pont2++;
                } else if (temp1[pont1].getId() < temp2[pont2].getId() && temp1[pont1].getId() != -1) {
                    etapa2[nEtapas] = temp1[pont1];
                    nBloco1++;
                    nEtapas++;
                    pont1++;
                } else if (temp2[pont2].getId() < temp1[pont1].getId() && temp2[pont2].getId() != -1) {
                    etapa2[nEtapas] = temp2[pont2];
                    nBloco2++;
                    nEtapas++;
                    pont2++;
                }
            }

            // Faz a ordenaçao da etapa 2 que é a primeira intercaçao
            etapa2 = ordenar(etapa2);

            // Começa a escrever os registros nos arquivos temporarios 3 e 4
            for (int i = 0; i < etapa2.length; i++) {
                if (switchArq2) {
                    if (etapa2[i] != null) {
                        byte[] ba;
                        ba = etapa2[i].toByte();
                        arq3.writeInt(ba.length);
                        arq3.write(ba);
                    }
                } else {
                    if (etapa2[i] != null) {
                        byte[] ba;
                        ba = etapa2[i].toByte();
                        arq4.writeInt(ba.length);
                        arq4.write(ba);
                    }
                }
            }

            switchArq2 = !(switchArq2); // Inverte o boolean
            nBloco1 = 0; // Numero de blocos 1
            nBloco2 = 0; // Numero de blocos 2
            nEtapas = 0; // Numero de etapas

            // Tranforma todos os registros da etapa 2 em nulos
            for (int i = 0; i < etapa2.length; i++) {
                etapa2[i] = null;
            }
        }

        Filme[] temp3 = lerTemp("Data/temp/arq3.tmp"); // Leitura do Arquivo temporario 3
        Filme[] temp4 = lerTemp("Data/temp/arq4.tmp"); // Leitura do Arquivo temporario 4
        // Abertura do arquivo temporario 5 e 6
        RandomAccessFile arq5 = new RandomAccessFile("Data/temp/arq5.tmp", "rw");
        RandomAccessFile arq6 = new RandomAccessFile("Data/temp/arq6.tmp", "rw");

        switchArq2 = true; // Controle de troca setado para verdadeiro
        nBloco1 = 0;
        nBloco2 = 0;
        nEtapas = 0;
        pont1 = 0;
        pont2 = 0;
        Filme[] etapa3 = new Filme[(TAMANHO_BLOCO) * 4];

        // Segunda intercalaçao
        while (temp3[pont1] != null || temp4[pont2] != null) {
            while (nBloco1 < (TAMANHO_BLOCO) * 2 || nBloco2 < (TAMANHO_BLOCO) * 2) {

                if (temp3[pont1] != null && temp4[pont2] == null && nBloco1 < (TAMANHO_BLOCO) * 2) {
                    etapa3[nEtapas] = temp3[pont1];
                    nBloco1++;
                    nEtapas++;
                    pont1++;
                } else if (temp4[pont2] != null && temp3[pont1] == null && nBloco2 < (TAMANHO_BLOCO) * 2) {
                    etapa3[nEtapas] = temp4[pont2];
                    nBloco2++;
                    nEtapas++;
                    pont2++;
                } else if (temp3[pont1] == null && temp4[pont2] == null) {
                    nBloco1 = (TAMANHO_BLOCO) * 2;
                    nBloco2 = (TAMANHO_BLOCO) * 2;
                } else if (nBloco1 < (TAMANHO_BLOCO) * 2 && nBloco2 >= (TAMANHO_BLOCO) * 2) {
                    etapa3[nEtapas] = temp3[pont1];
                    nBloco1++;
                    nEtapas++;
                    pont1++;
                } else if (nBloco2 < (TAMANHO_BLOCO) * 2 && nBloco1 >= (TAMANHO_BLOCO) * 2) {
                    etapa3[nEtapas] = temp4[pont2];
                    nBloco2++;
                    nEtapas++;
                    pont2++;
                } else if (temp3[pont1].getId() < temp4[pont2].getId() && temp3[pont1].getId() != -1) {
                    etapa3[nEtapas] = temp3[pont1];
                    nBloco1++;
                    nEtapas++;
                    pont1++;
                } else if (temp4[pont2].getId() < temp3[pont1].getId() && temp4[pont2].getId() != -1) {
                    etapa3[nEtapas] = temp4[pont2];
                    nBloco2++;
                    nEtapas++;
                    pont2++;
                }
            }

            etapa3 = ordenar(etapa3);

            for (int i = 0; i < etapa3.length; i++) {
                if (switchArq2) {
                    if (etapa3[i] != null) {
                        byte[] ba;
                        ba = etapa3[i].toByte();
                        arq5.writeInt(ba.length);
                        arq5.write(ba);
                    }
                } else {
                    if (etapa3[i] != null) {
                        byte[] ba;
                        ba = etapa3[i].toByte();
                        arq6.writeInt(ba.length);
                        arq6.write(ba);
                    }
                }
            }

            switchArq2 = !(switchArq2);
            nBloco1 = 0;
            nBloco2 = 0;
            nEtapas = 0;

            for (int i = 0; i < etapa3.length; i++) {
                etapa3[i] = null;
            }
        }

        // Leitura dos arquivos temporarios 5 e 6
        Filme[] temp5 = lerTemp("Data/temp/arq5.tmp");
        Filme[] temp6 = lerTemp("Data/temp/arq6.tmp");
        RandomAccessFile arq7 = new RandomAccessFile("Data/temp/arq7.tmp", "rw");

        switchArq2 = true;
        nBloco1 = 0;
        nBloco2 = 0;
        nEtapas = 0;
        pont1 = 0;
        pont2 = 0;
        Filme[] etapa4 = new Filme[(TAMANHO_BLOCO) * 8];
        // Ultima intercalaçao
        while (temp5[pont1] != null || temp6[pont2] != null) {
            while (nBloco1 < (TAMANHO_BLOCO) * 4 || nBloco2 < (TAMANHO_BLOCO) * 4) {

                if (temp5[pont1] != null && temp6[pont2] == null && nBloco1 < (TAMANHO_BLOCO) * 4) {
                    etapa4[nEtapas] = temp5[pont1];
                    nBloco1++;
                    pont1++;
                    nEtapas++;
                } else if (temp6[pont2] != null && temp5[pont1] == null && nBloco2 < (TAMANHO_BLOCO) * 4) {
                    etapa4[nEtapas] = temp6[pont2];
                    nBloco2++;
                    pont2++;
                    nEtapas++;
                } else if (temp5[pont1] == null && temp6[pont2] == null) {
                    nBloco1 = (TAMANHO_BLOCO) * 4;
                    nBloco2 = (TAMANHO_BLOCO) * 4;
                } else if (nBloco1 < (TAMANHO_BLOCO) * 4 && nBloco2 >= (TAMANHO_BLOCO) * 4) {
                    etapa4[nEtapas] = temp5[pont1];
                    nBloco1++;
                    pont1++;
                    nEtapas++;
                } else if (nBloco2 < (TAMANHO_BLOCO) * 4 && nBloco1 >= (TAMANHO_BLOCO) * 4) {
                    etapa4[nEtapas] = temp6[pont2];
                    nBloco2++;
                    pont2++;
                    nEtapas++;
                } else if (temp5[pont1].getId() < temp6[pont2].getId() && temp5[pont1].getId() != -1) {
                    etapa4[nEtapas] = temp5[pont1];
                    nBloco1++;
                    pont1++;
                    nEtapas++;
                } else if (temp6[pont2].getId() < temp5[pont1].getId() && temp6[pont2].getId() != -1) {
                    etapa4[nEtapas] = temp6[pont2];
                    nBloco2++;
                    pont2++;
                    nEtapas++;
                }
            }
            etapa4 = ordenar(etapa4);
            for (int i = 0; i < etapa4.length; i++) {
                if (etapa4[i] != null) {
                    byte[] ba;
                    ba = etapa4[i].toByte();

                    arq7.writeInt(ba.length);
                    arq7.write(ba);
                }
            }
            nBloco1 = 0;
            nBloco2 = 0;
            nEtapas = 0;
            for (int i = 0; i < etapa4.length; i++) {
                etapa4[i] = null;
            }
        }

        Filme[] temp7 = lerTemp("Data/temp/arq7.tmp"); // Leitura do arquivo temporario 7

        // Printa na tela como ficou o resultado da intercaçao
        System.out.print("[");
        for (int i = 0; i < temp7.length; i++) {
            if (temp7[i] != null) {
                System.out.print(temp7[i].getId());
                System.out.print(" ");
            }
        }
        System.out.print("]");

        // Fechamento de todos os arquivos temporarios
        arq1.close();
        arq2.close();
        arq3.close();
        arq4.close();
        arq5.close();
        arq6.close();
        arq7.close();
        // retorna o arquivo temporario 7, onde esta a intercaçao completa
        return temp7;
    }

    public static Filme[] lerTemp(String path) throws Exception {
        int len;
        byte[] ba;
        int cont = 0;
        boolean controle = false;

        RandomAccessFile arq = new RandomAccessFile(path, "rw");
        Filme[] temps = new Filme[1100];

        while (!controle) {
            try {
                Filme filme_temp = new Filme();
                len = arq.readInt();
                ba = new byte[len];
                arq.read(ba);
                filme_temp.fromByte(ba);
                temps[cont] = filme_temp;
                cont++;
            } catch (Exception e) {
                break;
            }
        }
        arq.close();
        return temps;
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        int choice = 0;
        int id = 0;
        Filme[] filmes = new Filme[1100];

        while (choice != 6) {
            choice = 0;
            // Exibição do menu de opçoes do programa
            System.out.println("Selecione uma operação:");
            System.out.println("1. Carregar base de dados");
            System.out.println("2. Ler um registro existente");
            System.out.println("3. Atualizar um registro existente");
            System.out.println("4. Excluir um registro existente");
            System.out.println("5. Ordenação externa");
            System.out.println("6. Sair");

            if (sc.hasNextLine()) {
                choice = sc.nextInt(); // Leitura da escolha do usuario
                sc.nextLine(); // Limpar o buffer do scanner
            }

            switch (choice) {
                case 0:
                    break;
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
                    System.out.println("Qual o valor de m? ");
                    int m = sc.nextInt(); // lendo o valor de m
                    sc.nextLine(); // Limpar o buffer do scanner
                    System.out.println("Qual o valor de n? ");
                    int n = sc.nextInt(); // lendo o valor de n
                    sc.nextLine(); // Limpar o buffer do scanner
                    filmes = IntercalacaoBalanceada(m, n, filmes);
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