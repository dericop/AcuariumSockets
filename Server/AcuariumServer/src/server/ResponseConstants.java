
package server;


/**
 *
 * @author dericop
 */
public class ResponseConstants{
    
    
    public static final String SUCCESS_200 = "{status:200, detail:Respuesta Exitosa}";
    public static final String FORBIDDEN_403 = "{status:403, detail:Revise las credenciales}";
    public static final String NOT_FOUND_404 = "{status:404, detail:Comando inexistente o mal formado}";
    
    public static String SUCCESS_100(String id){
        String SUCCESS_100 = "{\"status\":100,\"detail\":{\"data\":"+id+"}" +"}";
        
        return SUCCESS_100;
    }
    
    public static String SUCCESS_200(String fishes){
        //String SUCCESS_200 = "{\"status\":100,\"detail\":{\"data\":[""}"+"}";
        return "";
    }
}
