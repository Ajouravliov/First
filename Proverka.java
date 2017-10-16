import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

public class Proverka {
    private String apyUrl = "http://207.154.242.0:8888";
    private String registrUrl = apyUrl + "/v1/register";
    private String authorizeUrl = apyUrl + "/v1/authorize";

    private void RunTestCases() {                      // ������ ������� (���������) - ��������� ����-����. � ������ ������ ����-���� ������ ���� ��������� ����������, �������.
        successRegisterCaseAllFields();                
        successRegisterCaseMandatoryFieldsOnly();
        failedRegisterCaseMissingMandatoryFields();
        failedRegisterCaseIncorrectPasswordFormat();
        failedRegisterCaseWrongAge();
        failedRegisterCaseDuplicatedClient();
        successAuthorizeCase();
        failedAuthorizeCaseBlankFields();
        failedAuthorizeCaseMixedWrongCorrectInfo();
    }

    private void successRegisterCaseAllFields() {   //  ���������� ���� ���� �� ��������� ����������� ������ ������������ � ��������� ���������� �����������. ��������� ��� ����.
        // ���������� �������. ���� ��-����� �����������, ����� ����� ���������� �������� ��������������� ���� � ���������� �������
        String jsonRqString = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        
        JSONObject jsonObject = new JSONObject(jsonRqString);
    	jsonObject.put("email", generateEMail()); // ����������� � ������� ������ ��������������� ��������� ���� � ���������� �������
        
        String jsonRpString = processJsonRequest(registrUrl, jsonObject.toString()); // ���������� ������� ������
        JSONObject response = new JSONObject(jsonRpString);                          // �������� � ��������� ������� � ������

        String resultValue = response.get("Result").toString();   // ��������� �� ������� "���������" � ��������� � ���� ������
        String detailsValue = response.get("Details").toString(); // ��������� �� ������� "������" � ��������� � ���� ������ 
        
       

        if(!"true".equals(resultValue) && !"none".equals(detailsValue)) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
            System.out.println("\nPositive Register Case with all fields filled is FAILED");
        } else {
            System.out.println("\nPositive Register Case with all fields filled is SUCCESSFUL");
        }
        
        logging(jsonObject.toString(), jsonRpString); // ����� �� ������� ����������� �������� � ��������.
    }

    private void successRegisterCaseMandatoryFieldsOnly() {     	 //  ���������� ���� ���� �� ����������� ������ ������������ � ���������� �����������. ��������� ������ ������������ ����.
        
    	String[] jsonRqString = new String[2];
    	// ���������� ���� ��������� ���������� ��������. � ��� �� ��� - ������ ������� � ������ �����. �������� ��������������� ���� � ���������� ������� ����� ���������� �����.
    	jsonRqString[0] = "{\"email\":\"willbereplaced\",\"phone\":\"\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Test\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	jsonRqString[1] = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"test\",\"address\":{\"country\":\"\",\"city\":\"\",\"state\":\"\",\"zip\":\"\",\"street\":\"\"}}";
    	
    	for(int i=0; i<=1;i++) {      // ���� �� "��� �������", ������������ �� ������� ��� ����� �������
    	    JSONObject jsonObject = new JSONObject(jsonRqString[i]);
    	    jsonObject.put("email", generateEMail());                    // ����������� � ������ ��������������� ��������� ���� � ���������� �������
    	    jsonRqString[i] = jsonObject.toString();                     // ���������� ������� ������ � ������
    	
    	    String jsonRpString = processJsonRequest(registrUrl, jsonRqString[i]);
            JSONObject response = new JSONObject(jsonRpString);

            String resultValue = response.get("Result").toString();     // ��������� �� ������� "���������" � ��������� � ���� ������
            String detailsValue = response.get("Details").toString();   // ��������� �� ������� "������" � ��������� � ���� ������

            if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
                System.out.println("\nPositive Register Case without optional fields is FAILED");
            } else {
                System.out.println("\nPositive Register Case without optional fields is SUCCESSFUL");
            }
        
            logging(jsonRqString[i], jsonRpString);  // ����� �� ������� ����������� �������� � ��������.
    	}
    }

    private void failedRegisterCaseMissingMandatoryFields() {      //  ���������� ���� ���� � �������� ����������� ������ ������������, ��������� ������ ��� ������������ �����
    	
    	String[] jsonRqString = new String[3];
    	// ���������� ���� ��������� ���������� ��������. � ��� �� ��� - ������ ����, ������ ���� �������� � ������ ��������. �� 2-� � 3-� �������� ����� ����� ���������� �������� ��������������� ���� � ���������� �������.    	
    	jsonRqString [0]= "{\"email\": \"\",\"phone\":\"371 611111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"test\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	jsonRqString [1]= "{\"email\": \"willbereplaced\",\"phone\":\"371 611111\",\"pwd\":\"111aaa\",\"birthDate\":\"\",\"description\":\"test\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	jsonRqString [2]= "{\"email\": \"willbereplaced\",\"phone\":\"371 611111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	    	
    	for(int i=0; i<=2;i++) {      // ���� �� "��� �������", ������������ �� ������� ��� ����� �������
    		if(i>0) {                 // �������� ������ ������, � ��������� ��� ����������� ����
    			JSONObject jsonObject = new JSONObject(jsonRqString[i]);
    	    	jsonObject.put("email", generateEMail());                 // ����������� � ������ ��������������� ��������� ���� � ���������� �������
    	    	jsonRqString[i] = jsonObject.toString();                  // ��������� ������� ������ � ������
    		}
    	    String jsonRpString = processJsonRequest(registrUrl, jsonRqString[i]);   // ��������� �� ������� "���������" � ��������� � ���� ������
            JSONObject response = new JSONObject(jsonRpString);

            String resultValue = response.get("Result").toString();    // Here we can verify values ���� ����� ��������
            String detailsValue = response.get("Details").toString();  // ��������� �� ������� "������" � ��������� � ���� ������

            if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {         // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
                System.out.println("\nNegative Register Case without mandatory fields is SUCCESSFUL");
            } else {
                System.out.println("\nNegative Register Case without mandatory fields is FAILED");
            }
        
          logging(jsonRqString[i], jsonRpString);   // ����� �� ������� ����������� �������� � ��������.
    	}
    }

    private void failedRegisterCaseIncorrectPasswordFormat() {  //  ���������� ���� ���� � �������� ����������� ������ ������������, ��������� ������ � ������������ ������� (����������� �������).
    	// ���������� �������. ���� ��-����� �����������, ����� ����� ���������� �������� ��������������� ���� � ���������� �������
    	String jsonRqString = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"1_###a\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        
    	JSONObject jsonObject = new JSONObject(jsonRqString);
    	jsonObject.put("email", generateEMail());              // ����������� � ������� ������ ��������������� ��������� ���� � ���������� �������
    	
    	String jsonRpString = processJsonRequest(registrUrl, jsonObject.toString());  // ���������� ������� ������
        JSONObject response = new JSONObject(jsonRpString);                           // �������� � ��������� ������� � ������

        String resultValue = response.get("Result").toString();                       // ��������� �� ������� "���������" � ��������� � ���� ������
        String detailsValue = response.get("Details").toString();                     // ��������� �� ������� "������" � ��������� � ���� ������

        if(!"true".equals(resultValue) && "Field pwd bad format".equals(detailsValue)) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
            System.out.println("\nNegative Register Case incorrect password format is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Register Case incorrect password format is FAILED");
        }
        
        logging(jsonObject.toString(), jsonRpString);
    }

    private void failedRegisterCaseWrongAge() {  //  ���������� ���� ���� �� ��������� ��������. ���������� �������� ������������ ���� ��������, � ������ ������ 20 ���.
    	// ���������� �������. ���� ��-����� �����������, ����� ����� ���������� �������� ��������������� ���� � ���������� �������
    	String jsonRqString = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1997-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        
    	JSONObject jsonObject = new JSONObject(jsonRqString);
    	jsonObject.put("email", generateEMail());             // ����������� � ������� ������ ��������������� ��������� ���� � ���������� �������
    	
    	String jsonRpString = processJsonRequest(registrUrl, jsonObject.toString());  // ���������� ������� ������
        JSONObject response = new JSONObject(jsonRpString);                           // �������� � ��������� ������� � ������

        String resultValue = response.get("Result").toString();     // ��������� �� ������� "���������" � ��������� � ���� ������
        String detailsValue = response.get("Details").toString();   // ��������� �� ������� "������" � ��������� � ���� ������

        if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
            System.out.println("\nNegative Register Case wrong age is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Register Case wrong age is FAILED");
        }
        
        logging(jsonObject.toString(), jsonRpString);  // ����� �� ������� ����������� �������� � ��������.
    }

    private void failedRegisterCaseDuplicatedClient() {  //  ���������� ���� ���� �� ����������� �������� ��������� ������������� �������. ���������� �� ����������� ����� ������ ��� ������������� �������.
    	// ���������� �������. ���� ������������ �� ����, ���������� ��� ������������ � �������
    	String jsonRqString = "{\"email\":\"wslcg@a.com\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        String jsonRpString = processJsonRequest(registrUrl, jsonRqString);  // ���������� ������� ������
        JSONObject response = new JSONObject(jsonRpString);                   // �������� � ��������� ������� � ������

        String resultValue = response.get("Result").toString();    // ��������� �� ������� "���������" � ��������� � ���� ������
        String detailsValue = response.get("Details").toString();  // ��������� �� ������� "������" � ��������� � ���� ������

        if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
            System.out.println("\nNegative Register Case duplicated client is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Register Case duplicated is FAILED");
        }
        
        logging(jsonRqString, jsonRpString);   // ����� �� ������� ����������� �������� � ��������.
    }

    private void successAuthorizeCase() {     //  ���������� ���� ���� �� ��������� ����������� ������������������� ������������. ����� � ������ ��������� ���������.
    	// ���������� �������. ���������� ������, ������� ��� ���������� � �������
    	String jsonRqString = "{\"login\": \"yucdd@a.com\",\"pwd\": \"1_###a\"}";
        String jsonRpString = processJsonRequest(authorizeUrl, jsonRqString); // ���������� ������� ������
        JSONObject response = new JSONObject(jsonRpString);                   // �������� � ��������� ������� � ������

        String resultValue = response.get("Result").toString();        // ��������� �� ������� "���������" � ��������� � ���� ������
        String detailsValue = response.get("Details").toString();       // ��������� �� ������� "������" � ��������� � ���� ������

        if(!"true".equals(resultValue) || !"AAABBBCCCDDDEEE==".equals(detailsValue)) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
            System.out.println("\nPositive Authorize Case is FAILED");
        } else {
            System.out.println("\nPositive Authorize Case is SUCCESSFUL");
        }
        
        logging(jsonRqString, jsonRpString); // ����� �� ������� ����������� �������� � ��������.
    }

    private void failedAuthorizeCaseBlankFields() {   //  ���������� ���� ���� � �������� �������������� ��� ������ � ������, �� ������ �����. 
    	//��� �������� ������ ����� � ������� Exception. � ���������, ��� � �� ������� ����������� ������. 
    	    	        
    	/*String jsonRqString = "{\"login\": \"\",\"pwd\": \"\"}";
    	JSONObject jsonObject = new JSONObject(jsonRqString);
    	
        String jsonRpString = processJsonRequest(authorizeUrl, jsonObject.toString());
        JSONObject response = new JSONObject(jsonRpString);
        

        String resultValue = response.get("Result").toString();  // Here we can verify values
        String detailsValue = response.get("Details").toString();

        if((!"true".equals(resultValue) || !"AAABBBCCCDDDEEE==".equals(detailsValue)) && !"Request body is invalid".equals(detailsValue)) {  // For example
            System.out.println("\nNegative Authorize Case with blank fields is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Authorize Case with blank fields is FAILED");
        }
       
        logging(jsonObject.toString(), jsonRpString);*/
    }
    
     private void failedAuthorizeCaseMixedWrongCorrectInfo() {  //  ���������� ���� ���� � �������� �������������� � ���������� ������������ ������������ ����������
        
    	String[] jsonRqString = new String[3]; 
    	// ���������� ���� ��������� ���������� ��������. � ��� �� ���		
    	jsonRqString[0]="{\"login\": \"xxx@xxx.com\",\"pwd\": \"111aaa\"}"; // �������������� ���� � ������������ ������
    	jsonRqString[1]="{\"login\": \"yucdd@a.com\",\"pwd\": \"xxx\"}";    // ������������ ���� � �������������� ������
    	jsonRqString[2]="{\"login\": \"xxx@xxx.com\",\"pwd\": \"xxx\"}";    // �������������� ���� � �������������� ������
    	for(int i=0; i<=2;i++) {                                            // ���� �� "��� �������", ������������ �� ������� ��� ����� ������� 
    	  String jsonRpString = processJsonRequest(authorizeUrl, jsonRqString[i]); // ���������� ������� ������
          JSONObject response = new JSONObject(jsonRpString);                      // �������� � ��������� ������� � ������

          String resultValue = response.get("Result").toString();            // ��������� �� ������� "���������" � ��������� � ���� ������
          String detailsValue = response.get("Details").toString();          // ��������� �� ������� "������" � ��������� � ���� ������

          if(!"true".equals(resultValue) && ("User does not exists".equals(detailsValue) ||"Failed to authorize".equals(detailsValue))) {  // �������� ����������� ���� ����� � ����� ����� �� ������� - passed ��� failed
              System.out.println("\nNegative Authorize Case with mixed wrong and correct info is SUCCESSFUL");
            } else {
                System.out.println("\nNegative Authorize Case with mixed wrong and correct info is FAILED");
            }
    	
            logging(jsonRqString[i], jsonRpString);   // ����� �� ������� ����������� �������� � ��������.
    	}
    }
    
    private void logging(String request, String response) {  // ������� (���������) ������ �� ������� ����������� �������� � ��������.
        System.out.println("REQUEST: " + request);
        System.out.println("RESPONSE: " + response);
    }
    
    private String generateEMail(){                          // ������� (���������) ������������� ���������� �����
    	
       char[] chars= {'a','b','c','d','e','f','g','h','j','k','l','m','n','o','p','r','s','t','u','v','w','x','y','z'};   // ������� ������ � ��������� - 24 �����
       String out = new String();
       int a=0, ch=0;
       for(int i=0;i<5;i++){                  // ���� �� "���� ��������" ��� ��������� �������� ���������� �� ���� ���� 
            ch= a + (int) (Math.random()*24); // �������� ��������� ����� � ��������� �� 0 �� 24
            out+=chars[ch];                   // ��������� ����� �� ������ ������� � ���������� �������, ������� ���� ���� ��������� �����, � ��������� � �������� ������ 
        }
        
        out+="@a.com";                        // ��������� � �������� ������ (��������� ���������� �� ���� ����) ���������, ����� �������� ������ �����
        return out;
    }
   
    private String processJsonRequest(String urlStr, String jsonRqString) {  // ������� (���������) ��� �������� �������� �� ������ � ��������� ������ ��������.
        String jsonRpString = null;

        try {
            StringEntity input = new StringEntity(jsonRqString);
            input.setContentType("application/json;charset=UTF-8");
            input.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            HttpPost postRequest = new HttpPost(urlStr);
            postRequest.setEntity(input);
            postRequest.setHeader("Accept", "application/json");
            postRequest.setEntity(input);

            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(postRequest);
            httpClient.getConnectionManager().shutdown();

            jsonRpString = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonRpString;
    }

    public static void main(String[] args) {
        new Proverka().RunTestCases();
    }

}
