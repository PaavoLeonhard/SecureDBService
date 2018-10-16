/*
 * Copyright 2018 Information Systems Engineering, TU Berlin, Germany
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * This is being developed for the DITAS Project: https://www.ditas-project.eu/
 */

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