/* Generated By:JavaCC: Do not edit this line. ExpressionParser.java */
package org.relique.jdbc.csv;
import java.util.Map;
import org.relique.jdbc.csv.Expression;
class NumericConstant extends Expression{
  Number value;
  public NumericConstant(Number d){
    value = d;
  }
  public Object eval(Map env){
    return value;
  }
  public String toString(){
    return value.toString();
  }
}
class StringConstant extends Expression{
  String value;
  public StringConstant(String s){
    value = s;
  }
  public Object eval(Map env){
    return value;
  }
  public String toString(){
    return "'"+value+"'";
  }
}
class ColumnName extends Expression{
  String columnName;
  public ColumnName(String columnName){
    this .columnName = columnName.toUpperCase();
  }
  public Object eval(Map env){
    return env.get(columnName);
  }
  public String toString(){
    return "["+columnName+"]";
  }
}
class QueryEnvEntry extends Expression{
  String key;
  Expression expression;
  public QueryEnvEntry(String fieldName){
    this .key = fieldName.toUpperCase();
    this .expression = new ColumnName(fieldName);
  }
  public QueryEnvEntry(String fieldName, Expression exp){
    this .key = fieldName.toUpperCase();
    this .expression = exp;
  }
  public Object eval(Map env){
    return expression.eval(env);
  }
  public String toString(){
    return key+": "+expression.toString();
  }
}
class BinaryOperation extends Expression{
  String op;
  Expression left, right;
  public BinaryOperation(String op, Expression left, Expression right){
    this .op = op;
    this .left = left;
    this .right = right;
  }
  public Object eval(Map env){
    if (!op.equals("+")) return null;
    try {
      String rightEval = (String)right.eval(env);
      String leftEval = (String)left.eval(env);
      return leftEval+rightEval;
    }
    catch (ClassCastException e){
    }
    return null;
  }
  public String toString(){
    return op+" "+left+" "+right;
  }
}
abstract class LogicalExpression extends Expression{
  public boolean isTrue(Map env){
    return false;
  }
}
class ParsedExpression extends LogicalExpression{
  Expression content;
  public ParsedExpression(Expression left){
    content = left;
  }
  public boolean isTrue(Map env){
    return ((LogicalExpression)content).isTrue(env);
  }
  public Object eval(Map env){
    return content.eval(env);
  }
  public String toString(){
    return content.toString();
  }
}
class NotExpression extends LogicalExpression{
  LogicalExpression arg;
  public NotExpression(LogicalExpression arg){
    this .arg = arg;
  }
  public boolean isTrue(Map env){
    return !arg.isTrue(env);
  }
  public String toString(){
    return "NOT "+arg;
  }
}
class OrExpression extends LogicalExpression{
  LogicalExpression left, right;
  public OrExpression(LogicalExpression left, LogicalExpression right){
    this .left = left;
    this .right = right;
  }
  public boolean isTrue(Map env){
    return left.isTrue(env) || right.isTrue(env);
  }
  public String toString(){
    return "OR "+left+" "+right;
  }
}
class AndExpression extends LogicalExpression{
  LogicalExpression left, right;
  public AndExpression(LogicalExpression left, LogicalExpression right){
    this .left = left;
    this .right = right;
  }
  public boolean isTrue(Map env){
    return left.isTrue(env) && right.isTrue(env);
  }
  public String toString(){
    return "AND "+left+" "+right;
  }
}
class RelopExpression extends LogicalExpression{
  String op;
  Expression left, right;
  public RelopExpression(String op, Expression left, Expression right){
    this .op = op;
    this .left = left;
    this .right = right;
  }
  public boolean isTrue(Map env){
    Comparable leftValue = (Comparable)left.eval(env);
    Comparable rightValue = (Comparable)right.eval(env);
    boolean result = false;
    Integer leftComparedToRightObj = null;
    try {
      leftComparedToRightObj = new Integer(leftValue.compareTo(rightValue));
    }
    catch (ClassCastException e){}try {
      Double leftDouble = new Double(((Number)leftValue).toString());
      Double rightDouble = new Double(((Number)rightValue).toString());
      leftComparedToRightObj = new Integer(leftDouble.compareTo(rightDouble));
    }
    catch (ClassCastException e){}catch (NumberFormatException e){}if (leftComparedToRightObj != null){
      int leftComparedToRight = leftComparedToRightObj.intValue();
      if (leftValue != null && rightValue != null){
        if (op.equals("=")){
          result = leftComparedToRight == 0;
        }
        else if (op.equals("<>") || op.equals("!=")){
          result = leftComparedToRight != 0;
        }
        else if (op.equals(">")){
          result = leftComparedToRight>0;
        }
        else if (op.equals("<")){
          result = leftComparedToRight<0;
        }
        else if (op.equals("<=") || op.equals("=<")){
          result = leftComparedToRight <= 0;
        }
        else if (op.equals(">=") || op.equals("=>")){
          result = leftComparedToRight >= 0;
        }
      }
    }
    return result;
  }
  public String toString(){
    return op+" "+left+" "+right;
  }
}
class BetweenExpression extends LogicalExpression{
  Expression obj, left, right;
  public BetweenExpression(Expression obj, Expression left, Expression right){
    this .obj = obj;
    this .left = left;
    this .right = right;
  }
  public boolean isTrue(Map env){
    Comparable leftValue = (Comparable)left.eval(env);
    Comparable rightValue = (Comparable)right.eval(env);
    Comparable objValue = (Comparable)obj.eval(env);
    boolean result = true;
    try {
      if (objValue.compareTo(leftValue)<0)result = false;
      if (objValue.compareTo(rightValue)>0)result = false;
    }
    catch (ClassCastException e){}return result;
  }
  public String toString(){
    return "B "+obj+" "+left+" "+right;
  }
}
class IsNullExpression extends LogicalExpression{
  Object arg;
  public IsNullExpression(Object arg){
    this .arg = arg;
  }
  public boolean isTrue(Map env){
    if (env.get(arg) == null)return true;
    return false;
  }
  public String toString(){
    return "N "+arg;
  }
}
class LikeExpression extends LogicalExpression{
  Object arg1, arg2;
  public LikeExpression(Object arg1, Object arg2){
    this .arg1 = arg1;
    this .arg2 = arg2;
  }
  public boolean isTrue(Map env){
    return true;
  }
  public String toString(){
    return "L "+arg1+" "+arg2;
  }
}
public class ExpressionParser implements ExpressionParserConstants {
  ParsedExpression content;
  public void parseLogicalExpression()throws ParseException{
    content = logicalExpression();
  }
  public void parseQueryEnvEntry()throws ParseException{
    content = queryEnvEntry();
  }
  public boolean eval(Map env){
    return content.isTrue(env);
  }
  public String toString(){
    return ""+content;
  }

  final public ParsedExpression logicalExpression() throws ParseException {
  LogicalExpression left;
    left = logicalOrExpression();
    jj_consume_token(0);
    {if (true) return new ParsedExpression(left);}
    throw new Error("Missing return statement in function");
  }

  final public ParsedExpression queryEnvEntry() throws ParseException {
  Expression expression, alias, result;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NUMBER:
    case NULL:
    case NAME:
    case STRING:
    result = null;
      expression = binaryOperation();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AS:
      case NAME:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case AS:
          jj_consume_token(AS);
          break;
        default:
          jj_la1[0] = jj_gen;
          ;
        }
        alias = columnName();
    result = new QueryEnvEntry(((ColumnName)alias).columnName, expression);
        break;
      default:
        jj_la1[1] = jj_gen;
        ;
      }
      jj_consume_token(0);
    if (result == null){
      try {
        result = new QueryEnvEntry(((ColumnName)expression).columnName, expression);
      }
      catch (ClassCastException e){
        {if (true) throw new ParseException("can't accept expression '"+expression+"' without an alias");}
      }
    }
    {if (true) return new ParsedExpression(result);}
      break;
    case ASTERISK:
      jj_consume_token(ASTERISK);
    {if (true) return null;}
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public LogicalExpression logicalOrExpression() throws ParseException {
  LogicalExpression left, right;
    left = logicalAndExpression();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_1;
      }
      jj_consume_token(OR);
      right = logicalAndExpression();
    left = new OrExpression(left, right);
    }
    {if (true) return left;}
    throw new Error("Missing return statement in function");
  }

  final public LogicalExpression logicalAndExpression() throws ParseException {
  LogicalExpression left, right;
    left = logicalUnaryExpression();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_2;
      }
      jj_consume_token(AND);
      right = logicalUnaryExpression();
    left = new AndExpression(left, right);
    }
    {if (true) return left;}
    throw new Error("Missing return statement in function");
  }

  final public LogicalExpression logicalUnaryExpression() throws ParseException {
  LogicalExpression arg;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      arg = logicalUnaryExpression();
    {if (true) return new NotExpression(arg);}
      break;
    case 19:
      jj_consume_token(19);
      arg = logicalOrExpression();
      jj_consume_token(20);
    {if (true) return arg;}
      break;
    case NUMBER:
    case NULL:
    case NAME:
    case STRING:
      arg = relationalExpression();
    {if (true) return arg;}
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public LogicalExpression relationalExpression() throws ParseException {
  Expression arg1, arg2, arg3;
  String op;
  Token t;
    arg1 = simpleExpression();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case RELOP:
      op = relOp();
      arg2 = simpleExpression();
    {if (true) return new RelopExpression(op, arg1, arg2);}
      break;
    case BETWEEN:
      jj_consume_token(BETWEEN);
      arg2 = simpleExpression();
      jj_consume_token(AND);
      arg3 = simpleExpression();
    {if (true) return new BetweenExpression(arg1, arg2, arg3);}
      break;
    case IS:
      jj_consume_token(IS);
      jj_consume_token(NULL);
    {if (true) return new IsNullExpression(arg1);}
      break;
    case LIKE:
      jj_consume_token(LIKE);
      t = jj_consume_token(STRING);
    {if (true) return new LikeExpression(arg1, new StringConstant(t.image.substring(1, t.image.length()-1)));}
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String relOp() throws ParseException {
  Token t;
    t = jj_consume_token(RELOP);
    {if (true) return new String(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public String binOp() throws ParseException {
  Token t;
    t = jj_consume_token(BINOP);
    {if (true) return new String(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public Expression binaryOperation() throws ParseException {
  Expression left, right;
  String op;
    left = simpleExpression();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BINOP:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_3;
      }
      op = binOp();
      right = simpleExpression();
    left = new BinaryOperation(op, left, right);
    }
    {if (true) return left;}
    throw new Error("Missing return statement in function");
  }

  final public Expression simpleExpression() throws ParseException {
  Expression arg;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NAME:
      arg = columnName();
    {if (true) return arg;}
      break;
    case NUMBER:
      arg = numericConstant();
    {if (true) return arg;}
      break;
    case STRING:
      arg = stringConstant();
    {if (true) return arg;}
      break;
    case NULL:
      jj_consume_token(NULL);
    {if (true) return null;}
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Expression columnName() throws ParseException {
  Token t;
    t = jj_consume_token(NAME);
    {if (true) return new ColumnName(t.image);}
    throw new Error("Missing return statement in function");
  }

  final public Expression numericConstant() throws ParseException {
  Token t;
    t = jj_consume_token(NUMBER);
    Number value = null;
    try {
      value = new Integer(t.image);
    }
    catch (NumberFormatException e){
      value = new Double(t.image);
    }
    {if (true) return new NumericConstant(value);}
    throw new Error("Missing return statement in function");
  }

  final public Expression stringConstant() throws ParseException {
  String left, right;
    left = stringConstantAtom();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case STRING:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_4;
      }
      right = stringConstantAtom();
    left = left+"'"+right;
    }
    {if (true) return new StringConstant(left);}
    throw new Error("Missing return statement in function");
  }

  final public String stringConstantAtom() throws ParseException {
  Token t;
    t = jj_consume_token(STRING);
    {if (true) return t.image.substring(1, t.image.length()-1);}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public ExpressionParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[10];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x800,0x4800,0x2c050,0x100,0x80,0x8c250,0x13400,0x40000,0xc050,0x8000,};
   }

  /** Constructor with InputStream. */
  public ExpressionParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ExpressionParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public ExpressionParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public ExpressionParser(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[21];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 10; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 21; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}