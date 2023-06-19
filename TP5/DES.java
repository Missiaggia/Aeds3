import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class DES {

	public static void mostraAlgoritimo(String texto) throws IOException {
		try {
			// Informa qual criptografia sera usada
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
			// Gera a chave da criptografia
			SecretKey secretKey = keyGenerator.generateKey();
			Cipher DES;
			// Inicializa a cifra para o processo de criptografia
			DES = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// informa ao sistema que sera feita uma criptografia
			DES.init(Cipher.ENCRYPT_MODE, secretKey);
			// Texto puro
			byte[] textoPuro = texto.getBytes();
			// Texto criptografado
			byte[] textoCriptografado = DES.doFinal(textoPuro);
			RandomAccessFile arq1 = new RandomAccessFile("data/DES_criptografado.db", "rw");
			arq1.write(textoCriptografado);
			//informa ao sistema que sera feita uma decriptografia
			DES.init(Cipher.DECRYPT_MODE, secretKey);
			// Decriptografa o texto
			byte[] textoDecriptografado = DES.doFinal(textoCriptografado);
			RandomAccessFile arq2 = new RandomAccessFile("data/DES_Descriptografado.db", "rw");
			arq2.write(textoDecriptografado);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}

	}

}