package com.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;

    @Autowired
    public AzureBlobService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container-name}") String containerName) {

        BlobServiceClient serviceClient = 
                new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        containerClient = serviceClient.getBlobContainerClient(containerName);
    }

    public void uploadFile(String fileName, InputStream fileStream, long size, String contentType) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.upload(fileStream, size, true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
    }

    public byte[] downloadFile(String fileName) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.download(outputStream);
        return outputStream.toByteArray();
    }
    
    public String generarSasUrl(String fileName) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        BlobSasPermission permission = new BlobSasPermission()
                .setReadPermission(true);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(10);

        BlobServiceSasSignatureValues sasValues =
                new BlobServiceSasSignatureValues(expiryTime, permission);

        String sasToken = blobClient.generateSas(sasValues);

        return blobClient.getBlobUrl() + "?" + sasToken;
    }

}
