package de.tub.secureService.api;

import de.tub.secureService.service.HashService;
import de.tub.secureService.service.MinioUploader;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class UploadingView {
    @Autowired
    MinioUploader uploader;
    /**
     * Takes the url of the file that is requested. Takes the fiel out  Minio and returns an outputStream
     * @param fileName
     * @param response
     */
    @RequestMapping(value = "/{file_name}", method = RequestMethod.GET)
    public void getFile(
            @PathVariable("file_name") String fileName,
            HttpServletResponse response) {
        try {
            InputStream is = uploader.getObject(fileName);
            if(is == null ){
                response.getOutputStream().print(" ");
                response.flushBuffer();
            }else{
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            }
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    /**
     *  Takes files that are uploaded to the service and stores them in Minio. Returns the url und which it can later be retrieved
     * @param uploadingFiles
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.POST, value= "/",consumes = "multipart/form-data")
    public javax.ws.rs.core.Response uploadingPost(@RequestBody MultipartFile[] uploadingFiles) throws IOException {
        StreamSizePair pair = multiPartFileToStream(uploadingFiles);
        //Hash the current Time to create a url and object name of the file
        String objectName = HashService.createObjectName();
        try {
            uploader.insertObject(objectName ,pair.getStream(), pair.getSize(),"application/octet-stream");
            return javax.ws.rs.core.Response.ok(objectName).build();

        } catch (Exception e) {
            return javax.ws.rs.core.Response.status(500).build();
        }
    }

    private StreamSizePair multiPartFileToStream(MultipartFile[] uploadingFiles) throws IOException {
        //Convert the MultipartFile into a Stream
        InputStream completeStream = null;
        //Change the MultipartFile into a stream that can be uploaded too the MinioDB
        if(uploadingFiles.length < 1){
            throw new NullPointerException();
        }
        //Is done outside of the loop first bc the SequenceinputStream method doesnt allow for an empty Stream
        completeStream = uploadingFiles[0].getInputStream();
        long size = 0;
        for(int i=1; i<uploadingFiles.length;i++) {
            completeStream = new java.io.SequenceInputStream(completeStream, uploadingFiles[i].getInputStream());
            uploadingFiles[i].getContentType();
            size = size + uploadingFiles[i].getSize();
        }
        return new StreamSizePair(completeStream,size);
    }

    private class StreamSizePair{
        private InputStream stream;
        private long size;
        public StreamSizePair(InputStream stream, long size){
            this.stream=stream;
            this.size=size;
        }
        public InputStream getStream() {
            return stream;
        }
        public long getSize() {
            return size;
        }
    }
}