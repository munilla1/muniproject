package com.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

@Service
public class AzureBlobService {

    private final BlobServiceClient blobServiceClient;

    public AzureBlobService(
            @Value("${azure.storage.connection-string}") String connectionString
    ) {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    /**
     * Sube un archivo a un contenedor concreto (imagenes / productos)
     */
    public String subirArchivo(MultipartFile file, String containerName) throws IOException {

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(containerName);

        // Crear contenedor si no existe (robusto)
        if (!containerClient.exists()) {
            containerClient.create();
        }

        String nombreArchivo =
                UUID.randomUUID() + "-" + file.getOriginalFilename();

        BlobClient blobClient = containerClient.getBlobClient(nombreArchivo);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());

        blobClient.upload(file.getInputStream(), file.getSize(), true);
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }

    /**
     * Descarga directa (si el contenedor es privado)
     */
    public byte[] downloadFile(String containerName, String fileName) throws IOException {

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = containerClient.getBlobClient(fileName);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.download(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * Genera URL SAS temporal (para productos privados)
     */
    public String generarSasUrl(String containerName, String fileName) {

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = containerClient.getBlobClient(fileName);

        BlobSasPermission permission = new BlobSasPermission()
                .setReadPermission(true);

        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(10);

        BlobServiceSasSignatureValues sasValues =
                new BlobServiceSasSignatureValues(expiryTime, permission);

        String sasToken = blobClient.generateSas(sasValues);

        return blobClient.getBlobUrl() + "?" + sasToken;
    }
    
    public void eliminarArchivo(String containerName, String fileName) {

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(containerName);

        BlobClient blobClient = containerClient.getBlobClient(fileName);

        if (blobClient.exists()) {
            blobClient.delete();
        }
    }

    public void eliminarBlobDesdeUrl(String blobUrl, String containerName) {
        if (blobUrl == null) return;

        String fileName = blobUrl.substring(blobUrl.lastIndexOf("/") + 1);
        eliminarArchivo(containerName, fileName);
    }

}

