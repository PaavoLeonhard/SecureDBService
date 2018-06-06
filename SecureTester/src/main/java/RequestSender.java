import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.Logger;
import java.io.*;

public class RequestSender {
    static Logger log = Logger.getLogger(RequestSender.class.getName());

    public static void main(String[] args){
        test("172.17.0.2",8080,"./../SecureService" );
    }

    public static void test(String ip, int port,  String directory){
        String address = "http://"+ip+":"+ port+"/";
        String dockerName = buildDocker(directory);
        Process pro = runDocker(dockerName, directory);
        String container = getIDfromContainer(pro);
        if(waitForDocker(address,240000)==false){
            log.info("Timeout in block 1");
        }
        int rand = (int) (Math.random() *100000);
        String content = Integer.toString(rand);
        String multiRes = sendRandomMultipartFile(address , content);
        String getRes = getFile(multiRes,address);
        log.info("File content same Docker instance: \""+getRes+"\"");
        if(waitForDocker(address,240000)==false){
            log.info("Timeout in block 2");
        }
        stopContainer(container);
        pro.destroy();
        pro = runDocker(dockerName,directory);
        container = getIDfromContainer(pro);
        if(waitForDocker(address,240000)==false){
            log.info("Timeout in block 3");
        }
        getRes = getFile(multiRes,address);
        log.info("File content different Docker instance: \""+ getRes+"\""+ " Expected: \" \"");
        stopContainer(container);
        pro.destroy();
        }

    public static String sendMultipartFile(String address) {
        File multiFile = new File("/home/paavo/Work/TestFiles/MinioMulti");
        HttpResponse<JsonNode> jsonResponse = null;
        HttpResponse<String> response = null;
        try {
            //jsonResponse =
                     response = Unirest.post(address)
                    .field("type", multiFile)
                    .asString();
                    //.asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response.getBody();//jsonResponse.getBody().toString();
    }

    public static String sendRandomMultipartFile(String address, String content) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("testFile.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(content);
        log.info("Content of the random file: \""+content+"\"");
        writer.close();
        File multiFile = new File("testFile.txt");
        HttpResponse<String> response = null;
        try {
            response = Unirest.post(address)
                    .field("type", multiFile)
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response.getBody();
    }

    public static String buildDocker(String directory){
        ProcessBuilder pb = new ProcessBuilder("docker", "build" ,"-f","Dockerfile.artifact", ".");
        pb.directory(new File(directory));
        try {
            Process buildIt = pb.start();
            buildIt.waitFor();
            InputStream in = buildIt.getInputStream();
            String res = inputStreamToString(in);
            String [] ress = res.split(     " ");
            String name = ress[ress.length-1];
            buildIt.destroy();
            return name.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Process runDocker(String name, String directory){
        ProcessBuilder pbr = new ProcessBuilder("docker", "run" , "--detach", name);
        pbr.directory(new File(directory));
        try{
            Process runIt = pbr.start();
            return runIt;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void stopContainer(String name){
        ProcessBuilder pbr = new ProcessBuilder("docker", "stop" , name);
        try{
            Process stopIt = pbr.start();
            stopIt.waitFor();
            stopIt.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean waitForDocker(String address, int timeout){
        boolean temp = false;
        while(!temp) {
            temp = true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Unirest.get(address + "/d").asString();
            } catch (UnirestException e) {
                temp = false;
            }
        }
        return temp;
    }

    public static String getFile(String url, String address) {
        HttpResponse<String> response = null;
        try {
            response = Unirest.get(address+url).asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        response.getStatus();
        return response.getBody();
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF8");
            //return result.toString(UTF_8);
        }
    }

    public static String getIDfromContainer(Process pro){
        String res= null;
        try {
            InputStream in = pro.getInputStream();
            res = inputStreamToString(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res.trim();
    }

}