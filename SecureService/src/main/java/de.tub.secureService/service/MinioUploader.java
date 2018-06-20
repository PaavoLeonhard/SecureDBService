package de.tub.secureService.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import javax.crypto.*;

public class MinioUploader {
    private MinioClient minioClient;
    private String endpoint ="http://172.17.0.2:9000";//"http://172.17.0.2:9000"; //"http://172.17.0.2:9000"; 172.17.0.2:9000
    private final String bucketName  = "bucket";
    private static HashMap<String, SecretKey> fileMapping = new HashMap<>();

    public MinioUploader() {
        String accessKey = System.getenv("MINIO_ACCESS_KEY");
        String secretKey = System.getenv("MINIO_SECRET_KEY");
        try {
            // Create a minioClient with the Minio Server name, Port, Access key and Secret key.
            minioClient = new MinioClient(endpoint, accessKey, secretKey);
            //Check if the Bucket is already existing
            if(!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }

    /**
     * Wrapper method for the put method of the minioClient. Maps the url to the responding key in a hashMap
     * @param objectName
     * @param input
     * @param size
     * @param contentType
     */
    public void insertObject (String objectName, InputStream input,long size, String contentType) throws XmlPullParserException, InvalidBucketNameException, NoSuchAlgorithmException, InvalidArgumentException, InsufficientDataException, InvalidKeyException, ErrorResponseException, InvalidAlgorithmParameterException, NoSuchPaddingException, IOException, NoResponseException, IllegalBlockSizeException, InternalException, BadPaddingException {
        //Create a secret key to encrypt the file
        SecretKey key = null;
        try {
            key = KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        fileMapping.put(objectName, key);
        minioClient.putObject(bucketName,objectName,input, size, contentType, key);
    }

    public InputStream getObject(String objectName) {
        try {
            SecretKey key =fileMapping.get(objectName);
            return minioClient.getObject(bucketName, objectName, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}