import java.io.*;
import java.util.*;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
    public static Hashtable<String, String> dest;
    public static Hashtable<String, String> comp;
    public static Hashtable<String, String> jumps;
    public static LinkedHashMap<String, Integer> builtin_symbols;
    public static LinkedHashMap<String, String> symbol_table;
    public static Set<String> bitsetter;
    public static int program_address = 0;
    public static int memory_address=16;

    public static LinkedHashMap<String,String > User_defined_Symbols;

    static {
        User_defined_Symbols=new LinkedHashMap<>();

    }
    static {
        dest = new Hashtable<>();
        dest.put("null","000");
        dest.put("M", "001");
        dest.put("D", "010");
        dest.put("MD", "011");
        dest.put("A", "100");
        dest.put("AM", "101");
        dest.put("AD", "110");
        dest.put("AMD", "111");
    }

    static {
        comp = new Hashtable<>();
        comp.put("0", "101010");
        comp.put("1", "111111");
        comp.put("-1", "111010");
        comp.put("D", "001100");
        comp.put("A", "110000");
        comp.put("!D", "001101");
        comp.put("!A", "110001");
        comp.put("-D", "001111");
        comp.put("-A", "110011");
        comp.put("D+1", "011111");
        comp.put("A+1", "110111");
        comp.put("D-1", "001110");
        comp.put("A-1", "110010");
        comp.put("D+A", "000010");
        comp.put("D-A", "010011");
        comp.put("A-D", "000111");
        comp.put("D&A", "000000");
        comp.put("D|A", "010101");
        comp.put("M", "110000");
        comp.put("!M", "110001");
        comp.put("-M", "110011");
        comp.put("M+1", "110111");
        comp.put("M-1", "110010");
        comp.put("D+M", "000010");
        comp.put("D-M", "010011");
        comp.put("M-D", "000111");
        comp.put("D&M", "000000");
        comp.put("D|M", "010101");
    }


    static {
        jumps = new Hashtable<>();
        jumps.put("null","000");
        jumps.put("JGT", "001");
        jumps.put("JEQ", "010");
        jumps.put("JGE", "011");
        jumps.put("JLT", "100");
        jumps.put("JNE", "101");
        jumps.put("JLE", "110");
        jumps.put("JMP", "111");
    }

    static {
        builtin_symbols = new LinkedHashMap<>();
        builtin_symbols.put("R0", 0);
        builtin_symbols.put("R1", 1);
        builtin_symbols.put("R2", 2);
        builtin_symbols.put("R3", 3);
        builtin_symbols.put("R4", 4);
        builtin_symbols.put("R5", 5);
        builtin_symbols.put("R6", 6);
        builtin_symbols.put("R7", 7);
        builtin_symbols.put("R8", 8);
        builtin_symbols.put("R9", 9);
        builtin_symbols.put("R10", 10);
        builtin_symbols.put("R11", 11);
        builtin_symbols.put("R12", 12);
        builtin_symbols.put("R13", 13);
        builtin_symbols.put("R14", 14);
        builtin_symbols.put("R15", 15);
        builtin_symbols.put("SP", 0);
        builtin_symbols.put("LCL", 1);
        builtin_symbols.put("ARG", 2);
        builtin_symbols.put("THIS", 3);
        builtin_symbols.put("THAT", 4);
        builtin_symbols.put("SCREEN", 16384);
        builtin_symbols.put("KBD", 24576);
    }

    static {
        symbol_table = new LinkedHashMap<>();
    }

    static {
        bitsetter = new HashSet<>();
        bitsetter.add("M");
        bitsetter.add("!M");
        bitsetter.add("-M");
        bitsetter.add("M+1");
        bitsetter.add("M-1");
        bitsetter.add("D+M");
        bitsetter.add("D-M");
        bitsetter.add("M-D");
        bitsetter.add("D&M");
        bitsetter.add("D|M");
    }

    public static String toBinary(int x, int len) {// this function convert String to binary
        if (len > 0) {
            return String.format("%" + len + "s", Integer.toBinaryString(x)).replaceAll(" ", "0");
        }
        return null;
    }

    public static boolean isNumeric(String string) {// checks if the string argument is numeric or not
        if (string == null || string.isEmpty()) {
            return false;
        }
        try {
            int intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Integer.");
        }
        return false;
    }

    public static boolean isAinstruction(String line) {
        return line.startsWith("@");
    }
    public static boolean is_Cinstruction(String line) {
        return line.contains("=")||line.contains(";");
    }
    public static String C_instruction(String line) {
        String a="0";
        String Dest = "";
        String Comp= "";
        String Jump = "";

        int eqIndex = line.indexOf('=');//takes the index of =
        int colIndex = line.indexOf(';');//takes the index of ;

        if (eqIndex != -1) {
            Dest = line.substring(0, eqIndex);
            if(colIndex!=-1){
                Comp=line.substring(eqIndex+1,colIndex);
                Jump=line.substring(colIndex+1);
            }
            else{
                Comp=line.substring(eqIndex+1);
                Jump="";
            }
            /* Basically this block of code check if there = symbol is present in the  current line and sets the String Dest
            * and it checks if ; index is  present in the line ,  and if it is present then String Comp and String Jump is set
            * if ; is not present Jump is set as an empty string*/
        }else{
            Dest="null";
            if(colIndex!=-1){
                Comp=line.substring(0,colIndex);
                Jump=line.substring(colIndex+1);
            }
            else{
                Comp=line;
                Jump="";
            }
        }/*This block checks if  = symbol is not present in the currentline then the String dest is set as NULL then it checks the presence of  ; in the code
        if it is present the Starting string is set to Strng Comp and the string after the ; is set to Jump
        if ; is not present then it sets Comp as the whole line this step is un necesaary  and it sets jump to an empty string*/

        if (bitsetter.contains(Comp)) {
            a = "1";
        }

        return "111" + a + comp.get(Comp) + dest.get(Dest) + (!Jump.equals("")? jumps.get(Jump): "000");
    }

    public static String A_instruction(String line) {
        String binary = "";
        if (line.startsWith("@")) {
            String x = line.substring(1);
            int value = 0;

            if (isNumeric(x)) {
                int y = Integer.parseInt(x);
                binary = toBinary(y, 16);/* checks  if String x is numeric then converts it to its corresponding  binary value */
                symbol_table.put(x,binary);
            } else if (builtin_symbols.containsKey(x)) {
                value = builtin_symbols.get(x);
                binary = toBinary(value, 16);/*Checks if the String x is initialized int eh builtin symbols if so then binary value of the value if x is returned*/
            } else if (symbol_table.containsKey(x)) {
                String b = (symbol_table.get(x));
                binary = b;/*cheks if the symbol table has the value if so it returns the value of the  the key String x in binary */
            } else {

                int tempValue = memory_address;
                binary = toBinary(tempValue, 16);
                User_defined_Symbols.put(x, binary);
                memory_address++;
                /*else this string x is appended into user defined symbols */
            }
        }
        return binary;
    }

    public static void main(String[] args) throws IOException {
        String normasm = "C:/Users/GS Adithya Krishna/Documents/java programmes/Assmebler/src/Asmcode.asm.txt";
        String nowhspace = "C:/Users/GS Adithya Krishna/Documents/java programmes/Assmebler/src/whtrem.asm.txt";
        String output = "C:/Users/GS Adithya Krishna/Documents/java programmes/Assmebler/src/bincode.asm.txt";
        String symbol="C:/Users/GS Adithya Krishna/Documents/java programmes/Assmebler/src/SymbolTable.txt.txt";

        try (BufferedReader ip = new BufferedReader(new FileReader(normasm));
             BufferedWriter np = new BufferedWriter(new FileWriter(nowhspace))) {
            String line;
            String label;
            while ((line = ip.readLine()) != null) {

                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }

                line = line.replaceAll("\\s+", " ");
                if (line.isEmpty() ) {
                    continue;
                }

                if (line.trim().startsWith("(") && line.trim().endsWith(")")) {
                    int par1index = line.indexOf("(");
                    int par2index = line.indexOf(")");
                    String x = line.substring(par1index + 1, par2index);
                    symbol_table.put(x, toBinary(program_address, 16));// labels are appended into the symbol table with this block of code
                } else {
                    np.write(line.trim());
                    np.newLine();
                    program_address++;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader np = new BufferedReader(new FileReader(nowhspace));
             BufferedWriter op = new BufferedWriter(new FileWriter(output))) {
            String line;
            while ((line = np.readLine()) != null) {
                if (is_Cinstruction(line)) {
                    op.write(C_instruction(line));
                    op.newLine();
                    program_address++;
                } else if (isAinstruction(line)) {
                    String binary = A_instruction(line);
                    if (!binary.isEmpty()) {
                        op.write(binary);
                        op.newLine();
                        program_address++;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        ;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(symbol))) {
            writer.write("Predefined Symbols");
            writer.newLine(); // Add newline for better formatting
            for (Map.Entry<String, Integer> entry : builtin_symbols.entrySet()) {
                writer.write(entry.getKey() + ":-" + entry.getValue());
                writer.newLine();
            }
            writer.newLine();
            writer.write("Symbol Table");
            writer.newLine(); // Add newline for better formatting
            for (Map.Entry<String, String> entry : symbol_table.entrySet()) {
                writer.write(entry.getKey() + ":- " + entry.getValue());
                writer.newLine();
            }

            System.out.println("Symboltable contents written to " + symbol);
            writer.newLine();
            writer.write("User Defined Symbols");
            writer.newLine();
            for(Map.Entry<String, String> entry : User_defined_Symbols.entrySet()){
                writer.write(entry.getKey() + ":- " + entry.getValue());
                writer.newLine();
            }
        }
        catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }}

    }
