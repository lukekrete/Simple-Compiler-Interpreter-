import java.util.*;
import java.io.IOException;
class Symbol{
    public String value;
    public String token;
    public String color;
    public Symbol(String value,String token, String color){
        this.value=value;
        this.token=token;
        this.color=color;
    }
}
class Token{
    public String type;
    public String value;
    public Token(String type, String value){
        this.type = type;
        this.value = value;
    }
}
class TableEntry{
    public String name;
    public String type;
    public String characteristic;
    public TableEntry(String name, String type, String characteristic){
        this.name = name;
        this.type = type;
        this.characteristic = characteristic;
    }
    @Override
    public String toString() {
        return (name+" : "+type+" : "+characteristic+"\n");
    }
}
class symbolTable  {
    public String name;
    public String returnType;
    public ArrayList<TableEntry> tableEntries;
    public ArrayList<symbolTable> childrenOps;
    public symbolTable(String name){
        this.name = name;
        this.tableEntries = new ArrayList<TableEntry>();
        this.childrenOps = new ArrayList<symbolTable>();
    }
    public symbolTable( symbolTable table){
        this.name = table.name;
        this.returnType = table.returnType;
        this.tableEntries = new ArrayList<TableEntry>(table.tableEntries);
        this.childrenOps = new ArrayList<symbolTable>(table.childrenOps);
    }
}
class ParseNode{
    public String value;
    public String type;
    public ParseNode left;
    public ParseNode right;
    public ParseNode specialElse;
    public ArrayList<ParseNode>childrenOps;
    public ParseNode(){
        this.childrenOps = new ArrayList<ParseNode>();
    }
}
class FunctionTable{
    public ParseNode current_node;
    public symbolTable functionTable;
    public FunctionTable(){

    }
}
public class Interpreter{
    static ArrayList<Symbol> symbolTable = new ArrayList<Symbol>();
    static ArrayList<Token> tokenList = new ArrayList<Token>();
    static ArrayList<FunctionTable> localfunction = new ArrayList<FunctionTable>();
    static symbolTable main_table = new symbolTable("main");
    static char character;
    static int symbol;
    static String token= "";
    static boolean end = false;
    static String value;
    static String color;
    static String output = "";
    static String html="";
    static String lookahead;
    static int tabs = 0;
    static ParseNode start = new ParseNode();
    
    public static void main(String [] args) throws IOException{
        main_table.returnType = "int";
        fillSymbolTables();
        symbol = System.in.read();
        String [] array = new String [2];
        while( (symbol != -1) && (end == false)){
            getNextToken();
            if(output.length() > 0){
                output = output.substring(1,output.length()-1);
                array = output.split(",");
                if (array.length == 1){
                    tokenList.add(new Token(array[0].trim(),"null"));
                }else{
                    tokenList.add(new Token(array[0].trim(),array[1].trim()));
                }
                output = "";
            }
        }
         parse(start);
    }
    static String get_token(ArrayList<Symbol> array, String character){
        for (int i = 0; i < array.size();i++){
            if (array.get(i).value.equals(character)){
                return array.get(i).token;
            }
        }
        return null;
    }
    static void parse(ParseNode parent){
        if( (parent=program(main_table,parent)) != null ){
            printTables(main_table);
            Eval(parent, main_table);   
        }
        else{
            System.out.println("Parser Error: Syntax Error");
        }
    }
    
    static String get_color(ArrayList<Symbol> array, String character){
        for (int i = 0; i < array.size();i++){
            if (array.get(i).value.equals(character)){
                return array.get(i).color;
            }
        }
        return null;
    }

    static void getNextToken()throws IOException{
        character = (char)symbol;
        switch(character){
            case '.':
                end = true;
                token = get_token(symbolTable, ""+character);
                output = output+ token;
                html+="<font color='white'>"+character+"</font>";
                break;
            case '%':
            case '(':
            case ')':
            case ',':
            case ';':
            case '[':
            case ']':
            case '!':
                token = get_token(symbolTable, ""+character);
                output = output+ token;
                html+="<font color='white'>"+character+"</font>";
                symbol = System.in.read();
                break;
            case '\r':
                symbol = System.in.read();
                break;
            case '\n':
                html+="<br>";
                symbol = System.in.read();
                break;
            
            case '\t':
                html+="&nbsp;&nbsp;&nbsp;&nbsp;";
                symbol = System.in.read();
                break;
            case ' ':
                html+="<font> </font>";
                symbol = System.in.read();
                break;
            case '<':
            case '=':
            case '/':
            case '+':
            case '-':
            case '*':
            case '>':
                value = ""+character;
                symbol = System.in.read();
                character = (char)symbol;
                token = get_token(symbolTable, value+character);
                if(token!=null){
                    output+= token;
                    html+="<font color='white'>"+value+character+"</font>";
                    symbol = System.in.read();
                }else{
                    token = get_token(symbolTable, value);
                    output = output+ token;
                    html+="<font color='white'>"+value+"</font>";
                }
                break;
            case '&':
            case '|':
                value = ""+character;
                symbol = System.in.read();
                character = (char)symbol;
                token = get_token(symbolTable, value+character);
                if(token != null){
                    output+= token;
                    html+="<font color='white'>"+value+character+"</font>";
                    symbol = System.in.read();
                }else{
                   output+="<ERROR>";
                   html+="<font color='red'>"+value+"</font>";
                }
                break;
            default :
                if (Character.isLetter(character) || character == '_'){
                    value = ""+character;
                    symbol = System.in.read();
                    character = (char)symbol;
                    while( Character.isLetterOrDigit(character) || character =='_'){
                        value+=character;
                        symbol = System.in.read();
                        character = (char)symbol;
                    }
                    token = get_token(symbolTable, value);
                    color = get_color(symbolTable,value);
                    if(token != null){
                        output+=token;
                        html+="<font color='"+color+"'>"+value+"</font>";
                    }
                    else{
                        int position = symbolTable.size();
                        symbolTable.add(new Symbol(value,"<ID, "+position+">","dccd79"));
                        output+="<ID, "+position+">";
                        html+="<font color ='#dccd79'>"+value+"</font>";
                    }
                }
                else if(Character.isDigit(character)){
                    value = ""+character;
                    symbol = System.in.read();
                    character = (char)symbol;
                    while(Character.isDigit(character)){
                        value+=character;
                        symbol=System.in.read();
                        character = (char)symbol;
                    }
                    if (Character.isLetter(character)){
                        value+=character;
                        symbol = System.in.read();
                        character = (char)symbol;
                        while(Character.isLetterOrDigit(character)){
                            value+=character;
                            symbol = System.in.read();
                            character = (char)symbol;
                        }
                        output+="<ERROR>";
                        html+="<font color='red'>"+value+"</font>";
                    }
                    else if(character == '.'){
                        value+=character;
                        symbol=System.in.read();
                        character=(char)symbol;
                        if (Character.isDigit(character)){
                            value+=character;
                            symbol = System.in.read();
                            character = (char)symbol;
                            while(Character.isDigit(character) || character =='e' || character =='E'){
                                if (character == 'e' || character == 'E'){
                                    value+=character;
                                    symbol = System.in.read();
                                    character = (char)symbol;
                                    if (character == '+' || character == '-'|| Character.isDigit(character)){
                                        value+=character;
                                        symbol = System.in.read();
                                        character = (char)symbol;
                                        while(Character.isDigit(character)){
                                            value+=character;
                                            symbol = System.in.read();
                                            character = (char)symbol;
                                        }
                                    }
                                    else{
                                        value+=character;
                                        symbol = System.in.read();
                                        character = (char)symbol;
                                        while(Character.isLetterOrDigit(character)){
                                            value+=character;
                                            symbol = System.in.read();
                                            character = (char)symbol;
                                        }
                                        output+="<ERROR>";
                                        html+="<font color='red'>"+value+"</font>";
                                    }
                                }
                                else{
                                    value+=character;
                                    symbol = System.in.read();
                                    character = (char)symbol;
                                }   
                            }
                            output+="<Double,"+value+">";
                            html+="<font color='#36a3f0'>"+value+"</font>";
                        }
                        else{
                            value+=character;
                            symbol = System.in.read();
                            character = (char)symbol;
                            while(Character.isLetterOrDigit(character)){
                                value+=character;
                                symbol = System.in.read();
                                character = (char)symbol;
                            }
                            output+="<ERROR>";
                            html+="<font color='red'>"+value+"</font>";
                        }
                    }
                    else{
                        output+="<Integer,"+value+">";
                        html+="<font color='#36a3f0'>"+value+"</font>";
                    }
                }
        }
        return; 
    }

    public static void fillSymbolTables(){
        symbolTable.add(new Symbol( ".","<DOT>","white"));
        symbolTable.add(new Symbol("%","<OPERATOR,'%'>","white"));
        symbolTable.add(new Symbol( "&&","<AND>","white"));
        symbolTable.add(new Symbol("!","<NOT>", "white"));
        symbolTable.add(new Symbol( "*","<OPERATOR,'*'>","white"));
        symbolTable.add(new Symbol( "+","<OPERATOR,'+'>","white"));
        symbolTable.add(new Symbol( "(","<OPEN_BRACKET,'('>","white"));
        symbolTable.add(new Symbol( ")","<CLOSE_BRACKET,')'>","white"));
        symbolTable.add(new Symbol("[","<SQUARE_OPEN_BRACKET,'['>","white"));
        symbolTable.add(new Symbol("]","<SQUARE_CLOSE_BRACKET,']'>","white"));
        symbolTable.add(new Symbol( "-","<OPERATOR,'-'>","white"));
        symbolTable.add(new Symbol( ",","<COMMA>","white"));
        symbolTable.add(new Symbol( ";","<SEMICOLON>","white"));
        symbolTable.add(new Symbol("=", "<OPERATOR,'='>","white"));
        symbolTable.add(new Symbol("/","<OPERATOR,'/'>","white"));
        symbolTable.add(new Symbol("<","<OPERATOR,'<'>","white"));
        symbolTable.add(new Symbol(">","<OPERATOR,'>'>","white"));
        symbolTable.add(new Symbol("<=","<OPERATOR,'<='>","white"));
        symbolTable.add(new Symbol(">=","<OPERATOR,'>='>","white"));
        symbolTable.add(new Symbol("==","<OPERATOR,'=='>","white"));
        symbolTable.add(new Symbol("//","<OPERATOR,'//'>","white"));
        symbolTable.add(new Symbol("++","<OPERATOR,'++'>","white"));
        symbolTable.add(new Symbol("--","<OPERATOR,'--'>","white"));
        symbolTable.add(new Symbol("**","<OPERATOR,'**'>","white"));
        symbolTable.add(new Symbol("||","<OR>","white"));
        symbolTable.add(new Symbol("<>","<OPERATOR, '<>'>","white"));
        symbolTable.add(new Symbol("def","<DEF>","purple"));
        symbolTable.add(new Symbol("int","<INT>","aquamarine"));
        symbolTable.add(new Symbol("double","<DOUBLE>","aquamarine"));
        symbolTable.add(new Symbol("while","<WHILE>","purple"));
        symbolTable.add(new Symbol("od","<OD>","purple"));
        symbolTable.add(new Symbol("do","<DO>","purple"));
        symbolTable.add(new Symbol("print","<PRINT>","purple"));
        symbolTable.add(new Symbol("if","<IF>","purple"));
        symbolTable.add(new Symbol("then","<THEN>","purple"));
        symbolTable.add(new Symbol("return","<RETURN>","purple"));
        symbolTable.add(new Symbol("else","<ELSE>","purple"));
        symbolTable.add(new Symbol("fi","<FI>","purple"));
        symbolTable.add(new Symbol("fed","<FED>","purple"));
    }

    public static boolean isInteger(String value) {
        boolean isValidInteger = false;
        try{
           Integer.parseInt(value);
           isValidInteger = true;
        }catch (NumberFormatException ex){}
        return isValidInteger;
    }

    public static boolean isDouble(String value) {
    boolean isValidDouble = false;
    try{
       Double.parseDouble(value);
       isValidDouble = true;
    }catch (NumberFormatException ex){}
    return isValidDouble;
    }

    public static boolean lookup(ArrayList<TableEntry> array, String item){
        for(int i = 0; i < array.size();i++){
            if( array.get(i).name.equals(item)){
                return true;
            }
        }
        return false;
    }

    public static String retrieve(symbolTable table, String item){
        for(int i = 0; i < table.tableEntries.size(); i++){
            if( table.tableEntries.get(i).name.equals(item)){
                return table.tableEntries.get(i).characteristic;
            }
        }
        return "error";
    }

    public static void set(symbolTable table, String item, String value){
        for(int i=0; i < table.tableEntries.size(); i++){
            if(table.tableEntries.get(i).name.equals(item)){
                table.tableEntries.get(i).characteristic = value;
                return;
            }
        }
    }

    static void printTables(symbolTable table){
        System.out.println("Symbol symbolTable: "+table.name);
        System.out.println("______________________________");
        for (int x = 0; x < table.tableEntries.size(); x ++) {
            System.out.println(table.tableEntries.get(x).name+" : "+ table.tableEntries.get(x).type +" : "+ table.tableEntries.get(x).characteristic);
        }
        System.out.println();
        for(int x = 0; x < table.childrenOps.size();x++){
            printTables(table.childrenOps.get(x));
        }    
    }
    //RECURSIVE DESCENT PARSER localfunction ----------------------------------------------
    static ParseNode program(symbolTable table, ParseNode current_node){
        try{
            if(fdecls(table)){
                if(declarations(table)){
                    if ((current_node=statement_seq(table, current_node))!= null){
                        lookahead= tokenList.get(0).type;
                        if(lookahead.equals("DOT")){
                            System.out.println("\n.");
                            tokenList.remove(0);

                            return current_node;
                        }
                    }
                }
            }
        }catch(Exception e){}
        return null;
    }
    
    static String var(Token token){
        String variableName = null;
        try{
            if(ID(token)){
                variableName = symbolTable.get(Integer.parseInt(token.value)).value;
                System.out.print(variableName+"");
                lookahead = tokenList.get(0).value;
                if (lookahead.equals("'['")){
                    System.out.print("[");
                    tokenList.remove(0);
                    if (true){
                        lookahead = tokenList.get(0).value;
                        if(lookahead.equals("']'")){
                            tokenList.remove(0);
                            System.out.print("]");
                        }
                        else{
                            System.out.print("Error:  Expected ']' ");
                            variableName = null;
                        }
                    }
                }  
            }
        }
        catch(IndexOutOfBoundsException e){}
        return variableName;
    }

    static ParseNode expr(symbolTable table, ParseNode current_node){
        ParseNode term = term(table);
        if(term != null){
            if ((current_node=expr_recurse(table, current_node, term)) != null){
                return current_node;
            }  
        }
        return null; 
    }

    static ParseNode expr_recurse(symbolTable table, ParseNode current_node, ParseNode leftChild){
        if (tokenList.size() > 0){
        lookahead = tokenList.get(0).value;
        if (lookahead.equals("'+'")){
            System.out.print("+");
            tokenList.remove(0);
            current_node.value = "+";
            current_node.left = leftChild;
            current_node.right = new ParseNode();
            current_node.right = expr(table, current_node.right);
            if(current_node.right != null){
                return current_node;
            }
            return null;
        }else if(lookahead.equals("'-'")){
            System.out.print("-");
            current_node.value="-";
            current_node.left = leftChild;
            current_node.right = new ParseNode();
            tokenList.remove(0);
            current_node.right = expr(table,current_node.right);
            if(current_node.right != null){
                return current_node;
            }
            return null;
        }
    }
    current_node=leftChild;
    return current_node;
    }

    static ParseNode term(symbolTable table){
        ParseNode factor = factor(table);
        ParseNode current = new ParseNode();
        if(factor != null){
            current = term_recurse(table,current,factor);
            if(current!=null){
                return current;
            }
        }
        return null;
    }

    static ParseNode term_recurse(symbolTable table, ParseNode current_node, ParseNode leftChild){
        try{
            if (tokenList.size() > 0){
            lookahead = tokenList.get(0).value;
                if(lookahead.equals("'*'")){
                    System.out.print("*");
                    tokenList.remove(0);
                    current_node.value="*";
                    current_node.left = leftChild;
                    ParseNode factor = factor(table);
                    if(factor != null){
                        current_node.right = factor;
                        return current_node;
                    }
                    return null;
                }else if(lookahead.equals("'/'")){
                    System.out.print("/");
                    tokenList.remove(0);
                    current_node.value="/";
                    current_node.left = leftChild;
                    ParseNode factor = factor(table);
                    if(factor != null){
                        current_node.right = factor;
                        return current_node;
                    }
                    return null;
                }else if(lookahead.equals("'%'")){
                    System.out.print("%");
                    tokenList.remove(0);
                    current_node.value="%";
                    current_node.left = leftChild;
                    ParseNode factor = factor(table);
                    if(factor != null){
                        current_node.right = factor;
                        return current_node;
                    }
                    return null;
                }
            }
        }catch(IndexOutOfBoundsException e){
            return null;
        }
        current_node = leftChild;
        return current_node;
    }

    static ParseNode factor(symbolTable table){
        String variable;
        try{
            Token token = tokenList.remove(0);
            if(number(token, table)){
                ParseNode current_node = new ParseNode();
                current_node.value = token.value;
                current_node.type= token.type;
                return current_node;
            }
            else if((variable=var(token))!= null){
                lookahead = tokenList.get(0).value;
                if (lookahead.equals("'('")){
                    System.out.print("(");
                    tokenList.remove(0);

                    ParseNode current_node = new ParseNode();
                    current_node.value = variable;
                    current_node.type = "function_call";

                    if((current_node=exprseq(table, current_node))!=null){
                        token = tokenList.remove(0);
                        if (token.value.equals("')'")){
                            System.out.print(")");
                            return current_node;
                        }
                        else{
                            System.out.println("Error: Missing ')'");
                            return null;
                        }
                    }
                    else{
                        return null;
                    }
                }
                else{
                    ParseNode current_node = new ParseNode();
                    current_node.value=variable;
                    current_node.type= "variable";
                    return current_node;
                }
                
            }
            else if( token.value.equals("'('")){
                System.out.print("(");
                ParseNode current_node = new ParseNode();
                if((current_node=expr(table,current_node))!= null){
                    token = tokenList.remove(0);
                    if(token.value.equals("')'")){
                        System.out.print(")");
                        return current_node;
                    }else{
                        System.out.println("Error: Missing ')'");
                        return null;
                    }
                }else{
                    System.out.println("Error: Invalid Expression");
                    return null;
                }
    
            }
            else{
                return null;
            }
        }catch(IndexOutOfBoundsException e){
            System.out.print("Error: Unrecognized error");
            return null;
        }
    }

    static ParseNode exprseq(symbolTable table, ParseNode current_node){
        Token token = tokenList.get(0);
        if(!ID(token) && !token.type.equals("Integer") && !(token.type.equals("Double")) ){return current_node;}
        ParseNode temp = new ParseNode();
        if((temp=expr(table, temp)) != null){
            current_node.childrenOps.add(temp);
            exprseq_a(table, current_node);
            return current_node;
        }else{
            return null;
        }
        
    }

    static ParseNode exprseq_a(symbolTable table, ParseNode current_node){
        if(tokenList.get(0).type.equals("COMMA")){
            System.out.print(", ");
            tokenList.remove(0);
            if((current_node=exprseq(table, current_node))!= null){
                exprseq_a(table,current_node);
                return current_node;
            } 
        }
        return current_node;
    }
    static String fname(Token token){
        if(ID(token)){
            String variableName = symbolTable.get(Integer.parseInt(token.value)).value;
            System.out.print(variableName+"");
            return variableName;
        }
        return null; 
    }

    static String varlist(){
        String returnString= null;
        try{
            Token token = tokenList.get(0);
            String variable = var(token);
            if(variable != null){
                tokenList.remove(0);
                returnString = variable;
                String result = varlist_a();
                if(result != null){
                    returnString+= result;
                }
                else{
                    returnString = null;
                }
            }
        }
        catch(IndexOutOfBoundsException e){
            System.out.print("Error: Unrecognized Error");
            returnString = null;
        }
        return returnString;
    }
    static String varlist_a(){
        String returnString = "";
        try{ 
            if(tokenList.get(0).type.equals("COMMA")){
                tokenList.remove(0);
                System.out.print(", ");
                String result = varlist();
                if(result != null){
                    returnString = ","+ result;
                }
                else{
                    returnString = null;
                }
            }
        }
        catch(IndexOutOfBoundsException e){
            System.out.print("Error: Unrecognized Error");
        }
        return returnString;
    }

    static String type(){
        String result = null;
        try{
            Token token = tokenList.get(0);
            if(token.type.equals("INT")){
            tokenList.remove(0);
                System.out.print("int ");
                result= "int";
            }
            else if(token.type.equals("DOUBLE")){
                tokenList.remove(0);
                System.out.print("double ");
                result = "double";
            }
        }
        catch(IndexOutOfBoundsException e){
            System.out.print("Error: Unrecognized Error");
        }
        return result;
    }

    static boolean decl(symbolTable table){
        String typ = type();
        if (typ!= null){
            String vars = varlist();
            String []array = vars.split(",");
            for(int x = 0; x < array.length;x++){
                table.tableEntries.add(new TableEntry(array[x],"variable",typ));
            }
            return true;
        }
        return false;
    }

    static boolean declarations(symbolTable table){
        try{
            lookahead = tokenList.get(0).type;
            if (!lookahead.equals("INT") && !lookahead.equals("DOUBLE")){
                return true;
            }
            if(decl(table)){
                lookahead = tokenList.get(0).type;
                if (lookahead.equals("SEMICOLON")){
                    tokenList.remove(0);
                    System.out.println(";");
                    if(declarations_recurse(table)){
                        return true;
                    }
                }else{
                    System.out.print("Error:  expecting ;");
                    return false;
                }
            }
        }
        catch(IndexOutOfBoundsException e){
            System.out.print("Error: Unrecognized Error");
        }
       return false;
    }

    static boolean declarations_recurse(symbolTable table){
        try{
            if(decl(table)){
                lookahead = tokenList.get(0).type;
                if (lookahead.equals("SEMICOLON")){
                    tokenList.remove(0);
                    System.out.println(";");
                    if(declarations_recurse(table)){
                        return true;
                    }
                }else{
                    System.out.print("Error:  expecting ;");
                    return false;
                }
            }
        }
        catch(IndexOutOfBoundsException e){
            System.out.println("Error: Unrecognized Error");
        }
        return true;
    }

    static ParseNode bexpr(symbolTable table, ParseNode current_node){
        ParseNode term = bterm(table);
        if(term != null){
        current_node = bexpr_recurse(table, current_node, term);
            return current_node;
        }else{
            return null;
        }
    }

    static ParseNode bexpr_recurse(symbolTable table, ParseNode current_node, ParseNode leftChild){
        try{
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("OR")){
                System.out.print("||");
                tokenList.remove(0);
                current_node.value = "OR";
                current_node.left = leftChild;
                ParseNode term = new ParseNode();
                term = bexpr(table, term);
                if(term != null){
                    current_node.right = term;
                    return current_node;
                }
                else{
                    return null;
                }
            }
        }catch(Exception e){}   
        current_node = leftChild;
        return current_node;

    }

    static ParseNode bterm(symbolTable table){
        ParseNode factor = bfactor(table);
        if(factor!= null){
            ParseNode current = new ParseNode();
            current = bterm_recurse(table, current, factor);
            return current;
        }
        else{
            return null;
        } 
    }

    static ParseNode bterm_recurse(symbolTable table, ParseNode current_node, ParseNode factor){
        try{
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("AND")){
                System.out.print("&&");
                tokenList.remove(0);
                current_node.value="AND";
                current_node.left = factor;
                current_node.right = bterm(table);
                if(current_node.right != null){
                    return current_node;
                }
               else{
                   return null;
               }
            }
        }catch(Exception e){}
        current_node = factor;
        return current_node; 
    }
    static ParseNode bfactor(symbolTable table){
        try{
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("OPEN_BRACKET")){
                tokenList.remove(0);
                System.out.print("(");
                return bfactor_a(table);
            }
            else if(lookahead.equals("NOT")){
                tokenList.remove(0);
                System.out.print("!");
                ParseNode current_node = new ParseNode();
                current_node.value = "NOT";
                current_node.left = bfactor(table);
                if(current_node.left != null){
                    return current_node;
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }catch(Exception e){
            return null;
        }  
    }

    static ParseNode bfactor_a(symbolTable table){
        try{
            ParseNode current_node = new ParseNode();
            ParseNode temp = new ParseNode();
        if( (current_node=bexpr(table, current_node)) != null){
            lookahead = tokenList.get(0).type;
            if (lookahead.equals("CLOSE_BRACKET")){
                tokenList.remove(0);
                System.out.print(")");
                return current_node;
            }else{
                return null;
            }
        }else if ((temp=expr(table, temp)) != null){
            current_node = new ParseNode();
            current_node.left = temp;
            if((current_node.value=comp())!= null){
                if( (temp=expr(table,temp)) != null){
                    current_node.right= temp;
                    lookahead = tokenList.get(0).type;
                    if (lookahead.equals("CLOSE_BRACKET")){
                        tokenList.remove(0);
                        System.out.print(")");
                        return current_node;
                    }else{
                        return null;
                    }
                } 
            }
        }
    }catch(Exception e){return null;}
    return null;
    }

    static String comp(){
        lookahead = tokenList.get(0).value;
        switch(lookahead){
            case "'<'":
            case "'>'":
            case "'<>'":
            case "'<='":
            case "'>='":
            case "'=='":
                System.out.print(lookahead.substring(1,lookahead.length()-1));
                tokenList.remove(0);
                return lookahead;
            default:
                //nothing happens 
        }
        return null;
    }

    static ParseNode statement(symbolTable table, ParseNode current_node){
        String tab = "\t";
        for(int i=0; i < tabs; i++){
            tab+="\t";
        }
        System.out.print(tab);
        current_node.value = "statements";
        try{
            Token token = tokenList.remove(0);
            String variable;
            if( (variable=var(token)) != null){
                ParseNode temp = new ParseNode();
                temp.left = new ParseNode();
                temp.left.value = variable;
                lookahead = tokenList.get(0).value;
                if(lookahead.equals("'='")){
                    tokenList.remove(0);
                    System.out.print("=");
                    temp.value = "=";
                    temp.right = new ParseNode();
                    temp.right = expr(table, temp.right);
                    if(temp.right != null){
                        current_node.childrenOps.add(temp);
                        return current_node;
                    }   
                }else{return null;}
            }else if(token.type.equals("IF")){
                System.out.print("if ");
                ParseNode temp = new ParseNode();
                temp.value = "IF";
                temp.left = new ParseNode();
                temp.left = bexpr(table, temp.left);
                if(temp.left != null){
                    lookahead = tokenList.get(0).type;
                    if(lookahead.equals("THEN")){
                        tokenList.remove(0);
                        System.out.print("then\n");
                        tabs += 1;
                        temp.right = new ParseNode();
                        temp.right = statement_seq(table, temp.right);
                        temp.right = statement_a(table, temp.right);
                        current_node.childrenOps.add(temp);
                        return current_node;     
                    }
                }
            }else if(token.type.equals("WHILE")){
                System.out.print("while ");
                ParseNode temp = new ParseNode();
                temp.value ="WHILE";
                temp.left = new ParseNode();
                temp.left = bexpr(table, temp.left);
                if(temp.left != null){
                    lookahead = tokenList.get(0).type;
                    if(lookahead.equals("DO")){
                        System.out.print("do\n");
                        tabs += 1;
                        tokenList.remove(0);
                        temp.right = new ParseNode();
                        temp.right = statement_seq(table, temp.right);
                            lookahead = tokenList.get(0).type;
                            if(lookahead.equals("OD")){
                                tabs-=1;
                                System.out.print("\n"+tab+"od\n");
                                tokenList.remove(0);
                                current_node.childrenOps.add(temp);
                                return current_node;
                            }
                    }
                }
            }else if(token.type.equals("PRINT")){
                System.out.print("print ");
                ParseNode temp = new ParseNode();
                temp.value = "PRINT";
                temp.left = new ParseNode();
                temp.left = expr(table, temp.left);
                current_node.childrenOps.add(temp);
                return current_node;
                
            }else if(token.type.equals("RETURN")){
                System.out.print("return ");
                ParseNode temp = new ParseNode();
                temp.value = "RETURN";
                temp.left = new ParseNode();
                temp.left = expr(table, temp.left);
                current_node.childrenOps.add(temp);
                return current_node;
            }else{
                tokenList.add(0, token);
                return current_node;
            }
        }catch(Exception e){return null;}
        return null;
    }
    static ParseNode statement_a(symbolTable table, ParseNode current_node){
        try{
            String tab = "\t";
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("FI")){
                tabs-=1;
                for(int i=0; i < tabs; i++){
                    tab+="\t";
                }
                System.out.print("\n"+tab+"fi");
                tokenList.remove(0);
                return current_node;
            }else if(lookahead.equals("ELSE")){
                tokenList.remove(0);
                tabs-=1;
                for(int i=0; i < tabs; i++){
                    tab+="\t";
                }
                System.out.print("\n"+tab+"else"+"\n");
                tabs+=1;
                current_node.specialElse = new ParseNode();
                current_node.specialElse.value = "ELSE";
                current_node.specialElse.left = new ParseNode();
                current_node.specialElse.left = statement_seq(table, current_node.specialElse.left);
                if(current_node.specialElse.left != null){
                    lookahead = tokenList.get(0).type;
                    if(lookahead.equals("FI")){
                        tab="\t";
                        tabs-=1;
                        for(int i=0; i < tabs; i++){
                            tab+="\t";
                        }
                        System.out.print("\n"+tab+"fi");
                        tokenList.remove(0);
                        return current_node;
                    }
                }
            }
        }catch(Exception e){return null;}
        return null;
    }
    static ParseNode statement_seq(symbolTable table, ParseNode current_node){
        current_node = statement(table, current_node);
        if(current_node != null){
            current_node = statement_seq_a(table, current_node);
            if( current_node != null){
                return current_node;
            }
        }
        return null;
    }

    static ParseNode statement_seq_a(symbolTable table, ParseNode current_node){
        try{
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("SEMICOLON")){
                tokenList.remove(0);
                System.out.print(";\n");
                lookahead = tokenList.get(0).type;
                if (lookahead.equals("FED") || lookahead.equals("DOT")){
                    return current_node;
                }
                current_node = statement_seq(table, current_node);
                return current_node;
            }
            return current_node;
        }
        catch(Exception e){return null;}
    }

    static boolean params(symbolTable table){
        try{
            String typ = type();
            if(typ==null){
                return true;
            }
            if (typ != null){
                Token token = tokenList.remove(0);
                String variable = var(token);
                if(variable != null){
                    if(!lookup(table.tableEntries,variable)){
                        table.tableEntries.add(new TableEntry(variable,"variable", typ));
                    }
                    return(params_a(table));
                }  
            }
    }catch(Exception e){}
        return false;
    }

    static boolean params_a(symbolTable table){
        lookahead = tokenList.get(0).type;
        if(lookahead.equals("COMMA")){
            System.out.print(", ");
            tokenList.remove(0);
            return(params(table));

        }
        return true;
    }

    static boolean fdec(symbolTable table, ParseNode current_node){
        try{
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("DEF")){
                System.out.print("def ");
                tokenList.remove(0);
                String typ = type();
                if(typ != null){
                    Token token = tokenList.remove(0);
                    String name = fname(token);
                    if(name != null){
                        if(!lookup(table.tableEntries,name)){
                            table.tableEntries.add(new TableEntry(name, "function", typ));
                        }
                        current_node.value=name;
                        current_node.type="function";
                        lookahead = tokenList.get(0).type;
                        if(lookahead.equals("OPEN_BRACKET")){
                            System.out.print("(");
                            tokenList.remove(0);
                            table.childrenOps.add(new symbolTable(name));
                            if(params(table.childrenOps.get(table.childrenOps.size()-1))){
                                lookahead = tokenList.get(0).type;
                                if(lookahead.equals("CLOSE_BRACKET")){
                                    tokenList.remove(0);
                                    System.out.print(")\n");
                                    if(declarations(table.childrenOps.get(table.childrenOps.size()-1))){
                                        if((current_node=statement_seq(table.childrenOps.get(table.childrenOps.size()-1), current_node))!=  null  ){
                                            lookahead = tokenList.get(0).type;
                                            if(lookahead.equals("FED")){
                                                tokenList.remove(0);
                                                System.out.print("fed");
                                                FunctionTable function = new FunctionTable();
                                                function.current_node = current_node;
                                                function.functionTable = table.childrenOps.get(table.childrenOps.size()-1);
                                                function.functionTable.returnType = typ;
                                                localfunction.add(function);
                                                return true;
                                            }else{
                                                System.out.println("Error:  Expected fed");
                                            }
                                        }
                                    } 
                                }else{
                                    System.out.println("Error:  Expected )");
                                }
                            }
                        }else{
                            System.out.println("Error:  Expected (");
                        }
                    }
                }
            }else{
                return true;
            }
        }catch(Exception e){}
        return false;
    }

    static boolean fdecls(symbolTable table){
        try{
            return fdecls_recurse(table);
        }catch(Exception e){}
        return false;
    }
    static boolean fdecls_recurse(symbolTable table){
        try{
            lookahead = tokenList.get(0).type;
            if(lookahead.equals("DEF")){
                ParseNode current_node = new ParseNode();
                if(fdec(table, current_node)){
                    lookahead = tokenList.get(0).type;
                    if(lookahead.equals("SEMICOLON")){
                        System.out.print("; ");
                        tokenList.remove(0);
                        return fdecls_recurse(table);
                    }
                }else{
                    return false;
                }
            }
            return true;
        }catch(Exception e){return false;}
    }
    static boolean integer(Token token){
        if (token.type.equals("Integer") && isInteger(token.value)){
            return true;
        }
        return false;
    }
    static boolean double_(Token token){
        if(token.type.equals("Double") && isDouble(token.value)){
            return true;
        }
        
        return false;
    }
    static boolean number(Token token, symbolTable table){
        if(integer(token)){
            if(!lookup(table.tableEntries,token.value)){
                table.tableEntries.add(new TableEntry(token.value, "int", "primitive"));
            }
            System.out.print(token.value+"");
            return true;
        }
        else if(double_(token)){
            if(!lookup(table.tableEntries,token.value)){
            table.tableEntries.add(new TableEntry(token.value, "double", "primitive"));
            }
            System.out.print(token.value+"");
            return true;
        }
        return false;
    }
    static boolean ID(Token token){
        if(token.type.equals("ID")){
            return true;
        }
        return false;
    }
    

    static String Eval(ParseNode current_node, symbolTable table){
        String val = current_node.value;
        String type = current_node.type;
        switch(val){
            case "statements":
                return evalSubstatements(current_node,table);
            case "=":
                return evalSubassignment(current_node, table);
            case "+":
            case "-":
            case "/":
            case "*":
            case "%":
                return operator(current_node, table);
            case "OR":
            case "AND":
            case "NOT":
            case "'<'":
            case "'>'":
            case "'<>'":
            case "'<='":
            case "'>='":
            case "'=='":
                return comparator(current_node, table);
            case "IF":
                return evalSubif(current_node,table);
            case "WHILE":
                return evalSubwhile(current_node,table);
            case "PRINT":
                return evalSubprint(current_node, table);
            case "RETURN":
                return evalSubreturn(current_node, table);   
        }
        switch(type){
            case "variable":
                return retrieve(table, current_node.value);
            case "function_call":
                return evalSubfunction(current_node, table);
        }
        return val;
    }
    static String evalSubprint(ParseNode current_node, symbolTable table){
        System.out.println(Eval(current_node.left, table));
        return "finished";
    }
    static String evalSubreturn(ParseNode current_node, symbolTable table){
        if(table.returnType.equals("int")){
            return (int)Double.parseDouble(Eval(current_node.left,table))+"";
        }
        return Eval(current_node.left, table); 
    }
    static String operator(ParseNode current_node, symbolTable table){
        String val = current_node.value;
        switch(val){
            case "+":
                return (Double.parseDouble(Eval(current_node.left, table))  + Double.parseDouble(Eval(current_node.right, table)))+"";
            case "-":
                return (Double.parseDouble(Eval(current_node.left, table))  - Double.parseDouble(Eval(current_node.right, table)))+"";
            case "/":
                return (Double.parseDouble(Eval(current_node.left, table))  / Double.parseDouble(Eval(current_node.right, table)))+"";
            case "*":
                return (Double.parseDouble(Eval(current_node.left, table))  * Double.parseDouble(Eval(current_node.right, table)))+"";
            case "%":
                return (Double.parseDouble(Eval(current_node.left, table))  % Double.parseDouble(Eval(current_node.right, table)))+"";  
        }
        return "error";
    }
    static String evalSubfunction(ParseNode current_node, symbolTable t){
        String name = current_node.value;
        for(int i = 0; i < localfunction.size(); i++){
            if(localfunction.get(i).functionTable.name.equals(name)){
                symbolTable table = new symbolTable (localfunction.get(i).functionTable);
                int x =0;
                for (ParseNode item : current_node.childrenOps) {
                    set(table, table.tableEntries.get(x).name,Eval(item, t));
                    x++;
                }
                String result = Eval(localfunction.get(i).current_node,table);
                table = null;
                return result;
            }
        }
        return "function ended";
    }
    static String evalSubwhile(ParseNode current_node, symbolTable table){
        while(Eval(current_node.left,table).equals("true")){
            String returnVal = Eval(current_node.right, table);
            if(returnVal != "finished"){
                return(returnVal);
            }
        }
        return "finished";
    }
    static String evalSubif(ParseNode current_node, symbolTable table){
        if( Eval(current_node.left,table).equals("true")){
            return(Eval( current_node.right,table));
        }else if (current_node.right.specialElse != null){
            return(Eval(current_node.right.specialElse.left,table));
        }
        return "finished";
    }
    static String evalSubassignment(ParseNode current_node, symbolTable table){
        String variable = current_node.left.value;
        String right = Eval(current_node.right,table);
        set(table, variable, right);
        return "finished";
    }
    static String comparator(ParseNode current_node, symbolTable table){
        String val = current_node.value;
        switch(val){
            case "OR":
                return Boolean.toString(  (Eval(current_node.left, table).equals("true")) || (Eval(current_node.right, table).equals("true")));
            case "AND":
                return Boolean.toString(  (Eval(current_node.left, table).equals("true")) && (Eval(current_node.right, table).equals("true")));
            case "NOT":
                return Boolean.toString(!Boolean.parseBoolean(Eval(current_node.left,table)));
            case "'<'":
                return Boolean.toString(Double.parseDouble(Eval(current_node.left, table))  < Double.parseDouble(Eval(current_node.right, table)));
            case "'>'":
                return Boolean.toString(Double.parseDouble(Eval(current_node.left, table))  > Double.parseDouble(Eval(current_node.right, table)));
            case "'<>'":
                return Boolean.toString(Double.parseDouble(Eval(current_node.left, table))  != Double.parseDouble(Eval(current_node.right, table)));
            case "'<='":
                return Boolean.toString(Double.parseDouble(Eval(current_node.left, table))  <= Double.parseDouble(Eval(current_node.right, table)));
            case "'>='":
                return Boolean.toString(Double.parseDouble(Eval(current_node.left, table))  >= Double.parseDouble(Eval(current_node.right, table)));
            case "'=='":
                return Boolean.toString(Double.parseDouble(Eval(current_node.left, table))  == Double.parseDouble(Eval(current_node.right, table)));
        }
        return  "false";
    }
    static String evalSubstatements(ParseNode current_node,symbolTable table){
        for (ParseNode statement : current_node.childrenOps) {
            String returnVal = Eval(statement, table);
            if(returnVal != "finished"){
                return(returnVal);
            }
        }
        return "finished";
    }
    
}
