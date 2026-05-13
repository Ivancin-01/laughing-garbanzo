package com.tfg.gestion_practicas.services;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    @Value("${supabase.storage.bucket}")
    private String bucket;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final tools.jackson.databind.ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

    public String subirCv(MultipartFile archivo, Long alumnoId) {
        try {
            validarArchivoCv(archivo);

            String nombreOriginal = limpiarNombreArchivo(archivo.getOriginalFilename());
            String extension = obtenerExtension(nombreOriginal);

            String rutaArchivo = "alumnos/" + alumnoId + "/cv-" + System.currentTimeMillis() + "." + extension;

            String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + codificarRuta(rutaArchivo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", obtenerContentType(extension, archivo.getContentType()))
                    .header("x-upsert", "true")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(archivo.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Error subiendo CV a Supabase: " + response.body());
            }

            return rutaArchivo;

        } catch (Exception e) {
            throw new RuntimeException("No se pudo subir el CV a Supabase Storage.", e);
        }
    }

    public String crearUrlFirmada(String rutaArchivo) {
        try {
            if (rutaArchivo == null || rutaArchivo.isBlank()) {
                throw new RuntimeException("El alumno no tiene CV asociado.");
            }

            String url = supabaseUrl + "/storage/v1/object/sign/" + bucket + "/" + codificarRuta(rutaArchivo);

            String body = """
                    {
                        "expiresIn": 300
                    }
                    """;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .header("apikey", serviceRoleKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Error creando URL firmada: " + response.body());
            }

            tools.jackson.databind.JsonNode json = objectMapper.readTree(response.body());

            tools.jackson.databind.JsonNode signedUrlNode = json.has("signedURL") ? json.get("signedURL") : json.get("signedUrl");

            if (signedUrlNode == null || signedUrlNode.isNull()) {
                throw new RuntimeException("Supabase no devolvió una URL firmada válida.");
            }

            String signedUrl = signedUrlNode.asString();

            if (signedUrl.startsWith("http")) {
                return signedUrl;
            }

            return supabaseUrl + "/storage/v1" + signedUrl;

        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar la URL firmada del CV.", e);
        }
    }

    private void validarArchivoCv(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new RuntimeException("Selecciona un archivo antes de subirlo.");
        }

        String nombre = archivo.getOriginalFilename();

        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El archivo no tiene nombre válido.");
        }

        String extension = obtenerExtension(nombre);

        boolean extensionValida = extension.equals("pdf")
                || extension.equals("doc")
                || extension.equals("docx");

        if (!extensionValida) {
            throw new RuntimeException("Formato no permitido. Sube un PDF, DOC o DOCX.");
        }

        long maxBytes = 10 * 1024 * 1024;

        if (archivo.getSize() > maxBytes) {
            throw new RuntimeException("El CV no puede superar los 10MB.");
        }
    }

    private String limpiarNombreArchivo(String nombreOriginal) {
        if (nombreOriginal == null) {
            return "cv";
        }

        return nombreOriginal
                .replace("\\", "")
                .replace("/", "")
                .replace(" ", "-")
                .toLowerCase();
    }

    private String obtenerExtension(String nombreArchivo) {
        int punto = nombreArchivo.lastIndexOf(".");

        if (punto == -1 || punto == nombreArchivo.length() - 1) {
            throw new RuntimeException("El archivo debe tener extensión.");
        }

        return nombreArchivo.substring(punto + 1).toLowerCase();
    }

    private String obtenerContentType(String extension, String contentTypeOriginal) {
        if (contentTypeOriginal != null && !contentTypeOriginal.isBlank()) {
            return contentTypeOriginal;
        }

        return switch (extension) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    private String codificarRuta(String ruta) {
        String[] partes = ruta.split("/");

        StringBuilder rutaCodificada = new StringBuilder();

        for (int i = 0; i < partes.length; i++) {
            if (i > 0) {
                rutaCodificada.append("/");
            }

            rutaCodificada.append(URLEncoder.encode(partes[i], StandardCharsets.UTF_8));
        }

        return rutaCodificada.toString();
    }
}