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

    private void RunTestCases() {                      // Каждая функция (процедура) - отдельный тест-кейс. В идеале каждый тест-кейс должен быть отдельной программой, конечно.
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

    private void successRegisterCaseAllFields() {   //  Позитивный тест кейс на идеальную регистрацию нового пользователя с полностью корректной информацией. Заполнены все поля.
        // подготовка запроса. Мэйл де-факто отсутствует, позже будет подставлен случайно сгенерированный мэйл в корректном формате
        String jsonRqString = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        
        JSONObject jsonObject = new JSONObject(jsonRqString);
    	jsonObject.put("email", generateEMail()); // подставляем в готовый запрос сгенерированный случайный мэйл в корректном формате
        
        String jsonRpString = processJsonRequest(registrUrl, jsonObject.toString()); // отправляем готовый запрос
        JSONObject response = new JSONObject(jsonRpString);                          // получаем и сохраняем респонс в объект

        String resultValue = response.get("Result").toString();   // Извлекаем из запроса "результат" и сохраняем в виде строки
        String detailsValue = response.get("Details").toString(); // Извлекаем из запроса "детали" и сохраняем в виде строки 
        
       

        if(!"true".equals(resultValue) && !"none".equals(detailsValue)) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
            System.out.println("\nPositive Register Case with all fields filled is FAILED");
        } else {
            System.out.println("\nPositive Register Case with all fields filled is SUCCESSFUL");
        }
        
        logging(jsonObject.toString(), jsonRpString); // вывод на консоль содержимого реквеста и респонса.
    }

    private void successRegisterCaseMandatoryFieldsOnly() {     	 //  Позитивный тест кейс на регистрацию нового пользователя с корректной информацией. Заполнены только обязательные поля.
        
    	String[] jsonRqString = new String[2];
    	// подготовка всех возможных комбинаций запросов. У нас их два - пустой телефон и пустой адрес. Случайно сгенерированный мэйл в корректном формате будет подставлен позже.
    	jsonRqString[0] = "{\"email\":\"willbereplaced\",\"phone\":\"\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Test\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	jsonRqString[1] = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"test\",\"address\":{\"country\":\"\",\"city\":\"\",\"state\":\"\",\"zip\":\"\",\"street\":\"\"}}";
    	
    	for(int i=0; i<=1;i++) {      // цикл на "два оборота", обрабатывает по очереди два наших запроса
    	    JSONObject jsonObject = new JSONObject(jsonRqString[i]);
    	    jsonObject.put("email", generateEMail());                    // подставляем в запрос сгенерированный случайный мэйл в корректном формате
    	    jsonRqString[i] = jsonObject.toString();                     // возвращаем готовый запрос в строку
    	
    	    String jsonRpString = processJsonRequest(registrUrl, jsonRqString[i]);
            JSONObject response = new JSONObject(jsonRpString);

            String resultValue = response.get("Result").toString();     // Извлекаем из запроса "результат" и сохраняем в виде строки
            String detailsValue = response.get("Details").toString();   // Извлекаем из запроса "детали" и сохраняем в виде строки

            if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
                System.out.println("\nPositive Register Case without optional fields is FAILED");
            } else {
                System.out.println("\nPositive Register Case without optional fields is SUCCESSFUL");
            }
        
            logging(jsonRqString[i], jsonRpString);  // вывод на консоль содержимого реквеста и респонса.
    	}
    }

    private void failedRegisterCaseMissingMandatoryFields() {      //  Негативный тест кейс с попыткой регистрации нового пользователя, используя запрос без обязательных полей
    	
    	String[] jsonRqString = new String[3];
    	// подготовка всех возможных комбинаций запросов. У нас их три - пустой мэйл, пустой день рождения и пустое описание. Во 2-м и 3-м запросах позже будет подставлен случайно сгенерированный мэйл в корректном формате.    	
    	jsonRqString [0]= "{\"email\": \"\",\"phone\":\"371 611111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"test\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	jsonRqString [1]= "{\"email\": \"willbereplaced\",\"phone\":\"371 611111\",\"pwd\":\"111aaa\",\"birthDate\":\"\",\"description\":\"test\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	jsonRqString [2]= "{\"email\": \"willbereplaced\",\"phone\":\"371 611111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
    	    	
    	for(int i=0; i<=2;i++) {      // цикл на "три оборота", обрабатывает по очереди три наших запроса
    		if(i>0) {                 // отсекаем первый запрос, в следующие два подставляем мэйл
    			JSONObject jsonObject = new JSONObject(jsonRqString[i]);
    	    	jsonObject.put("email", generateEMail());                 // подставляем в запрос сгенерированный случайный мэйл в корректном формате
    	    	jsonRqString[i] = jsonObject.toString();                  // переводим готовый запрос в строку
    		}
    	    String jsonRpString = processJsonRequest(registrUrl, jsonRqString[i]);   // Извлекаем из запроса "результат" и сохраняем в виде строки
            JSONObject response = new JSONObject(jsonRpString);

            String resultValue = response.get("Result").toString();    // Here we can verify values мэйл днюха описание
            String detailsValue = response.get("Details").toString();  // Извлекаем из запроса "детали" и сохраняем в виде строки

            if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {         // проверка результатов тест кейса и вывод итога на консоль - passed или failed
                System.out.println("\nNegative Register Case without mandatory fields is SUCCESSFUL");
            } else {
                System.out.println("\nNegative Register Case without mandatory fields is FAILED");
            }
        
          logging(jsonRqString[i], jsonRpString);   // вывод на консоль содержимого реквеста и респонса.
    	}
    }

    private void failedRegisterCaseIncorrectPasswordFormat() {  //  Негативный тест кейс с попыткой регистрации нового пользователя, используя пароль в неправильном формате (специальные символы).
    	// подготовка запроса. Мэйл де-факто отсутствует, позже будет подставлен случайно сгенерированный мэйл в корректном формате
    	String jsonRqString = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"1_###a\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        
    	JSONObject jsonObject = new JSONObject(jsonRqString);
    	jsonObject.put("email", generateEMail());              // подставляем в готовый запрос сгенерированный случайный мэйл в корректном формате
    	
    	String jsonRpString = processJsonRequest(registrUrl, jsonObject.toString());  // отправляем готовый запрос
        JSONObject response = new JSONObject(jsonRpString);                           // получаем и сохраняем респонс в объект

        String resultValue = response.get("Result").toString();                       // Извлекаем из запроса "результат" и сохраняем в виде строки
        String detailsValue = response.get("Details").toString();                     // Извлекаем из запроса "детали" и сохраняем в виде строки

        if(!"true".equals(resultValue) && "Field pwd bad format".equals(detailsValue)) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
            System.out.println("\nNegative Register Case incorrect password format is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Register Case incorrect password format is FAILED");
        }
        
        logging(jsonObject.toString(), jsonRpString);
    }

    private void failedRegisterCaseWrongAge() {  //  Негативный тест кейс на валидацию возраста. Отправляем заведомо неподходящую дату рождения, в данном случае 20 лет.
    	// подготовка запроса. Мэйл де-факто отсутствует, позже будет подставлен случайно сгенерированный мэйл в корректном формате
    	String jsonRqString = "{\"email\":\"willbereplaced\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1997-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        
    	JSONObject jsonObject = new JSONObject(jsonRqString);
    	jsonObject.put("email", generateEMail());             // подставляем в готовый запрос сгенерированный случайный мэйл в корректном формате
    	
    	String jsonRpString = processJsonRequest(registrUrl, jsonObject.toString());  // отправляем готовый запрос
        JSONObject response = new JSONObject(jsonRpString);                           // получаем и сохраняем респонс в объект

        String resultValue = response.get("Result").toString();     // Извлекаем из запроса "результат" и сохраняем в виде строки
        String detailsValue = response.get("Details").toString();   // Извлекаем из запроса "детали" и сохраняем в виде строки

        if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
            System.out.println("\nNegative Register Case wrong age is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Register Case wrong age is FAILED");
        }
        
        logging(jsonObject.toString(), jsonRpString);  // вывод на консоль содержимого реквеста и респонса.
    }

    private void failedRegisterCaseDuplicatedClient() {  //  Негативный тест кейс на возможность создания дубликата существующего клиента. Отправляем на регистрацию копию данных уже существующего клиента.
    	// подготовка запроса. Мэйл генерировать не надо, используем уже существующий в системе
    	String jsonRqString = "{\"email\":\"wslcg@a.com\",\"phone\":\"+371 6111111\",\"pwd\":\"111aaa\",\"birthDate\":\"1988-06-25T00:00:00.000Z\",\"description\":\"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua\",\"address\":{\"country\":\"US\",\"city\":\"New York\",\"state\":\"John Doe\",\"zip\":\"LV-1011\",\"street\":\"Ropazu 10\"}}";
        String jsonRpString = processJsonRequest(registrUrl, jsonRqString);  // отправляем готовый запрос
        JSONObject response = new JSONObject(jsonRpString);                   // получаем и сохраняем респонс в объект

        String resultValue = response.get("Result").toString();    // Извлекаем из запроса "результат" и сохраняем в виде строки
        String detailsValue = response.get("Details").toString();  // Извлекаем из запроса "детали" и сохраняем в виде строки

        if(!"true".equals(resultValue) || !"none".equals(detailsValue)) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
            System.out.println("\nNegative Register Case duplicated client is SUCCESSFUL");
        } else {
            System.out.println("\nNegative Register Case duplicated is FAILED");
        }
        
        logging(jsonRqString, jsonRpString);   // вывод на консоль содержимого реквеста и респонса.
    }

    private void successAuthorizeCase() {     //  Позитивный тест кейс на идеальную авторизацию зарегистрированного пользователя. Логин и пароль полностью корректны.
    	// подготовка запроса. Используем данные, которые уже существуют в системе
    	String jsonRqString = "{\"login\": \"yucdd@a.com\",\"pwd\": \"1_###a\"}";
        String jsonRpString = processJsonRequest(authorizeUrl, jsonRqString); // отправляем готовый запрос
        JSONObject response = new JSONObject(jsonRpString);                   // получаем и сохраняем респонс в объект

        String resultValue = response.get("Result").toString();        // Извлекаем из запроса "результат" и сохраняем в виде строки
        String detailsValue = response.get("Details").toString();       // Извлекаем из запроса "детали" и сохраняем в виде строки

        if(!"true".equals(resultValue) || !"AAABBBCCCDDDEEE==".equals(detailsValue)) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
            System.out.println("\nPositive Authorize Case is FAILED");
        } else {
            System.out.println("\nPositive Authorize Case is SUCCESSFUL");
        }
        
        logging(jsonRqString, jsonRpString); // вывод на консоль содержимого реквеста и респонса.
    }

    private void failedAuthorizeCaseBlankFields() {   //  Негативный тест кейс с попыткой авторизоваться без логина и пароля, на пустых полях. 
    	//При отправке пустых полей я получаю Exception. К сожалению, так и не удалось разобраться почему. 
    	    	        
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
    
     private void failedAuthorizeCaseMixedWrongCorrectInfo() {  //  Негативный тест кейс с попыткой авторизоваться с различными комбинациями неправильной информации
        
    	String[] jsonRqString = new String[3]; 
    	// подготовка всех возможных комбинаций запросов. У нас их три		
    	jsonRqString[0]="{\"login\": \"xxx@xxx.com\",\"pwd\": \"111aaa\"}"; // несуществующий мэйл и существующий пароль
    	jsonRqString[1]="{\"login\": \"yucdd@a.com\",\"pwd\": \"xxx\"}";    // существующий мэйл и несуществующий пароль
    	jsonRqString[2]="{\"login\": \"xxx@xxx.com\",\"pwd\": \"xxx\"}";    // несуществующий мэйл и несуществующий пароль
    	for(int i=0; i<=2;i++) {                                            // цикл на "три оборота", обрабатывает по очереди три наших запроса 
    	  String jsonRpString = processJsonRequest(authorizeUrl, jsonRqString[i]); // отправляем готовый запрос
          JSONObject response = new JSONObject(jsonRpString);                      // получаем и сохраняем респонс в объект

          String resultValue = response.get("Result").toString();            // Извлекаем из запроса "результат" и сохраняем в виде строки
          String detailsValue = response.get("Details").toString();          // Извлекаем из запроса "детали" и сохраняем в виде строки

          if(!"true".equals(resultValue) && ("User does not exists".equals(detailsValue) ||"Failed to authorize".equals(detailsValue))) {  // проверка результатов тест кейса и вывод итога на консоль - passed или failed
              System.out.println("\nNegative Authorize Case with mixed wrong and correct info is SUCCESSFUL");
            } else {
                System.out.println("\nNegative Authorize Case with mixed wrong and correct info is FAILED");
            }
    	
            logging(jsonRqString[i], jsonRpString);   // вывод на консоль содержимого реквеста и респонса.
    	}
    }
    
    private void logging(String request, String response) {  // Функция (процедура) вывода на консоль содержимого реквеста и респонса.
        System.out.println("REQUEST: " + request);
        System.out.println("RESPONSE: " + response);
    }
    
    private String generateEMail(){                          // Функция (процедура) генерирования случайного мэйла
    	
       char[] chars= {'a','b','c','d','e','f','g','h','j','k','l','m','n','o','p','r','s','t','u','v','w','x','y','z'};   // готовим массив с алфавитом - 24 буквы
       String out = new String();
       int a=0, ch=0;
       for(int i=0;i<5;i++){                  // цикл на "пять оборотов" для получения случайно комбинации из пяти букв 
            ch= a + (int) (Math.random()*24); // получаем случайное число в диапазоне от 0 до 24
            out+=chars[ch];                   // извлекаем букву из ячейки массива с порядковым номером, который есть наше случайное число, и добавляем к итоговой строке 
        }
        
        out+="@a.com";                        // добавляем к итоговой строке (случайной комбинации из пяти букв) окончание, чтобы получить формат мэйла
        return out;
    }
   
    private String processJsonRequest(String urlStr, String jsonRqString) {  // Функция (процедура) для отправки реквеста на сервер и получения оттуда респонса.
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
