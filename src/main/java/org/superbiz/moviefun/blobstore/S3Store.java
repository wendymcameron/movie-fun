package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private final AmazonS3Client amazonS3Client;
    private final String bucketName;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.amazonS3Client = s3Client;
        this.bucketName = photoStorageBucket;

//        if (StringUtils.stripToNull(amazonS3Client.getBucketLocation(photoStorageBucket)) == null) {
//            amazonS3Client.createBucket(photoStorageBucket);
//        }
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(blob.contentType);
        amazonS3Client.putObject(bucketName, blob.name, blob.inputStream, objectMetadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        try {
            S3Object s3Obj = amazonS3Client.getObject(bucketName, name);
            return Optional.of(new Blob(s3Obj.getKey(), s3Obj.getObjectContent(), s3Obj.getObjectMetadata().getContentType()));
        } catch (AmazonS3Exception e) {
            if(e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return Optional.empty();
            }
            throw e;
        }
    }

    @Override
    public void deleteAll() {
        amazonS3Client.deleteBucket(bucketName);
    }
}
