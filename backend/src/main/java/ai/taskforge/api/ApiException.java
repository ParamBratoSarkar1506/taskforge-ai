package ai.taskforge.api; public class ApiException extends RuntimeException { public final String code; public ApiException(String c,String m){super(m);code=c;} }
