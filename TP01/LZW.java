import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

class LZW {

    // ---------------------------------------------------------------------------------------------- //

    public static void compress(String source, String destin) throws Exception {

        // --------------------------------------------------- //

        // 1. Convert original file to string
        String originString = "";
        int totalLenght = 0;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            // read length
            
            while(raf.getFilePointer() < raf.length() - 1) {
                int lenght = raf.readInt();

                // read Id
                originString += raf.readInt() + "~";

                // read Nome
                originString += raf.readUTF() + "~";
               
                // read Data
                originString += formatter.parse(raf.readUTF()) + "~";

                // read Nota
                originString += raf.readFloat() + "~";

                totalLenght += lenght + 4;

                raf.seek(totalLenght);
            }

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // --------------------------------------------------- //

        // 2. Create dictionary
        originString = originString.replaceAll(" ", "\\^");

        ArrayList<String> dictionary = new ArrayList<String>();

        for(int i = 0; i < originString.length(); i++) {

            String s = Character.toString(originString.charAt(i));
            
            if(!dictionary.contains(s)) dictionary.add(s);
        }

        // -------------------------------------------------------------- //

        // 3. Create dictionary output
        ArrayList<Integer> output = new ArrayList<Integer>();

        for(int i = 0; i < originString.length(); i++) {

            String s = Character.toString(originString.charAt(i));

            while(true) {

                if(i == originString.length() - 1) break;

                s += originString.charAt(i + 1);

                if(dictionary.contains(s)) {
                    
                    if(i == originString.length() - 2) {
                        
                        output.add(dictionary.indexOf(s));
                        break;
                    }
                    else i++;
                }
                else {

                    dictionary.add(s);
                    
                    if(i == originString.length() - 2) output.add(dictionary.indexOf(s));
                    else output.add(dictionary.indexOf(s.substring(0, s.length() - 1)));
                    break;
                }
            }

            // --------------- //

            if(i == originString.length() - 1) break;
        }

        // -------------------------------------------------------------- //

        // 4. Create compressed file
        RandomAccessFile raf = new RandomAccessFile(destin, "rw");

        raf.writeInt(dictionary.size());

        for(String str : dictionary) raf.writeUTF(str);

        raf.writeInt(output.size());

        if(dictionary.size() < 256) {

            for(int i : output) raf.writeByte(i);
        }
        else if(dictionary.size() < 65536) {

            for(int i : output) raf.writeShort(i);
        }
        else {

            for(int i : output) raf.writeInt(i);
        }

        raf.close();

        // -------------------------------------------------------------- //

        new File(source).delete();
    }

    // ---------------------------------------------------------------------------------------------- //
    
    public static void decompress(String source, String destin) {

        ArrayList<String> dictionary = new ArrayList<String>();
        ArrayList<Integer> output = new ArrayList<Integer>();

        // -------------------------------------------------------------- //

        // 1. Read dictionary and output
        try {

            RandomAccessFile raf = new RandomAccessFile(source, "rw");

            int dictionarySize = raf.readInt();

            for(int i = 0; i < dictionarySize; i++) dictionary.add(raf.readUTF());

            int outputSize = raf.readInt();

            if(dictionarySize < 256) {

                for(int i = 0; i < outputSize; i++) output.add((int)raf.readByte());
            }
            else if(dictionarySize < 65536) {

                for(int i = 0; i < outputSize; i++) output.add((int)raf.readShort());
            }
            else {

                for(int i = 0; i < outputSize; i++) output.add(raf.readInt());
            }

            // -------------------------------------------------------------- //

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // -------------------------------------------------------------- //

        // 2. Create descompact string
        String file = "";

        for(int i : output) file += dictionary.get(i);

        file = file.replaceAll("\\^", " ");

        // -------------------------------------------------------------- //

        // 3. Create descompact file
        String args[] = file.split("~");

        try {
            
            RandomAccessFile raf = new RandomAccessFile(destin, "rw");

            
            for(int i = 0; i < args.length; i=i+4) {
                raf.writeInt(Integer.parseInt(args[i]));
                raf.writeUTF(args[i+1]);
                raf.writeUTF(args[i+2]);
                raf.writeFloat(Float.parseFloat(args[i+3]));
            }

            raf.close();
        }
        catch(Exception e) { e.printStackTrace(); }

        // -------------------------------------------------------------- //

        new File(source).delete();
    }

    // ------------------------------------------------------------------------------------------------------------ //

}
