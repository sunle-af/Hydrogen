import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main{
//     //What is a token itself
   public static class Token {
        TokenType type;
        Optional<String> value;
    
        // Constructor
        public Token(TokenType type, Optional<String> value) {
            this.type = type;
            this.value = value;
        }
    
        // Override toString method to print Token objects
        @Override
        public String toString() {
            return "Token { " +
                    " type= " + type +
                    ", value=" + value +
                    '}';
        }
    }
//    //each of the token that we might find will be of (return type , semicolon type or the integer type)
    
    enum TokenType {
        _return,
        int_lit,
        semi,
    }

     
    public static  ArrayList<Token> contents(StringBuilder file){
        ArrayList<Token> Tokens = new ArrayList<Token>();

        StringBuilder stringBuffer = new StringBuilder();
        for(int i=0; i<file.length(); i++){
            char ch= file.charAt(i);
             
            if( Character.isAlphabetic(ch)){

                stringBuffer.append(ch);
                i++;
                while (i<file.length() && Character.isAlphabetic(file.charAt(i))) {
                    stringBuffer.append(file.charAt(i));
                    i++;
                }
                //if we found a valid token i.e. return
                if( stringBuffer.toString().equals("return") ){
                    //append this token to the token vector
                    Tokens.add(new Token(TokenType._return, Optional.empty()));
                    // System.out.println("Yay its a return");
                    stringBuffer.delete(0, stringBuffer.length());
                }  else{
                    System.out.println("Oops its an invalid Token");
                }              
                i--;

            }else if(Character.isDigit(ch)){
                stringBuffer.append(ch);
                i++;
                while (i<file.length() && Character.isDigit(file.charAt(i))) {
                    stringBuffer.append(file.charAt(i));
                    i++;
                }
                i--;
                Tokens.add(new Token(TokenType.int_lit, Optional.of(stringBuffer.toString())));
                stringBuffer.delete(0, stringBuffer.length());
                //we found a valid number push this number to Integer Literal
            }else if(ch ==';'){
                //push back the semicolon litral

                Tokens.add(new Token(TokenType.semi, Optional.empty()));
                // System.out.println(" Semi "+ch);
                
            }else if(Character.isWhitespace(ch)){
                continue;
            }
            else{
                System.out.println("Oops its an invalid Token");
            }
        }

        // System.out.println(Tokens);
        return Tokens;
    }

    public static StringBuilder tokens_to_asm( ArrayList<Token> Tokens){
         StringBuilder sb = new StringBuilder();

         sb.append("global _start\n");
         sb.append("_start: \n");

         int sizeOfTokesList = Tokens.size();

         for(int i =0; i<sizeOfTokesList; i++){
            Token token = Tokens.get(i);

            if(token.type == TokenType._return && i+1 < sizeOfTokesList ){
                if( Tokens.get(i+1).type == TokenType.int_lit && i+2 <sizeOfTokesList ){
                    if(Tokens.get(i+2).type == TokenType.semi ){
                        Optional<String> valueOfInteger = Tokens.get(i+1).value;
                        sb.append("    mov rax, 60\n");
                        sb.append("    mov rdi, ").append(valueOfInteger.get()).append('\n');
                        sb.append("    syscall");
                    }
                }

            }


         }

         return sb;
    }

    public static void genrateASMfile(StringBuilder asm_tokens){
        String asmFileContent = asm_tokens.toString();
        File file = new File("codegen.asm");

         try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(asmFileContent);
            System.out.println("File created successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void checkArguements(String[] args){

        if(args.length == 0){
            System.out.println("No Arguments ...");
            System.out.println("Correct Usage <filename>.hy");  
        }
        else{
            String filePath = args[0];//takes the first element of the arguement and stores it in String that is named as a file path

            //we have used try and catch block inoreder to read the file 
            //if we successfully find the file we are looking for (name and the extension) we read the file line by line and prints it 
            //else we will return with an error

            try(BufferedReader reader = new BufferedReader(new FileReader(filePath)) ){
                String line = reader.readLine();
                StringBuilder sb= new StringBuilder(line);
                // System.out.println(line);
                while((line = reader.readLine()) != null){
                    // System.out.println(line);
                    sb.append(line).append('\n');
                }


                //Genrate the tokens
                ArrayList<Token> tokens = contents(sb);


                StringBuilder tokens_to_asm = tokens_to_asm(tokens);

                 
                genrateASMfile(tokens_to_asm);

                // System.out.println(sb);

            } catch(IOException e){
                System.out.println("Error Reading the File " +e );
            }
        }
    }

    public static void main(String[] args) {

         checkArguements(args);

    }
}