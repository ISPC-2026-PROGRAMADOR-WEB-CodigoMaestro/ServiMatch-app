package api;

import com.ispc.servimatch.model.LoginRequest;
import com.ispc.servimatch.model.LoginResponse;
import com.ispc.servimatch.model.Oficio;
import com.ispc.servimatch.model.Ubicacion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.Assert.*;

import com.ispc.servimatch.api.ApiService;

public class ApiServiceMockWebServerTest {

    private MockWebServer mockWebServer;
    private ApiService apiService;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    // AUT-NET-01: Respuesta HTTP 200
    @Test
    public void obtenerUbicaciones_respuesta200() throws IOException {

        // Arrange: preparar respuesta simulada
        String json = "["
                + "{\"id\":1,\"ciudad\":\"Cordoba\",\"provincia\":\"Cordoba\"},"
                + "{\"id\":2,\"ciudad\":\"Jesus Maria\",\"provincia\":\"Cordoba\"}"
                + "]";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(json)
        );

        // Act: ejecutar solicitud
        Response<List<Ubicacion>> response =
                apiService.obtenerUbicaciones().execute();

        // Assert: verificar resultado
        assertEquals(200, response.code());
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(2, response.body().size());

        assertEquals("Cordoba", response.body().get(0).getCiudad());
        assertEquals("Jesus Maria", response.body().get(1).getCiudad());
        assertEquals("Cordoba", response.body().get(0).getProvincia());
    }

    // AUT-NET-02: Respuesta HTTP 500
    @Test
    public void obtenerUbicaciones_respuesta500() throws IOException {

        // Arrange: simular error del servidor
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setHeader("Content-Type", "application/json")
                        .setBody("{\"error\":\"Error interno del servidor\"}")
        );

        // Act: ejecutar solicitud
        Response<List<Ubicacion>> response =
                apiService.obtenerUbicaciones().execute();

        // Assert: verificar manejo de la respuesta
        assertEquals(500, response.code());
        assertFalse(response.isSuccessful());
        assertNull(response.body());
        assertNotNull(response.errorBody());
    }

    // AUT - NET-03: Login exitoso - HTTP 200
    @Test
    public void login_respuesta200() throws IOException {

        // Arrange: simular respuesta exitosa
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody("{\"id\":1,\"nombre\":\"Carlos\",\"access\":\"token123\"}")
        );

        // Act: realizar login
        LoginRequest datos = new LoginRequest("test@gmail.com", "123456");

        Response<LoginResponse> response =
                apiService.login(datos).execute();

        // Assert: verificar resultado
        assertEquals(200, response.code());
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
    }

    // AUT - NET-04: Consulta de oficios - HTTP 200

    @Test
    public  void obtenerOficios_respuesta200() throws  IOException {

        // Arrange: simular respuesta exitosa
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody("[{\"id\":1,\"nombre\":\"Electricista\"}]")
        );

        // Act: Obtener oficios
        Response<List<Oficio>> response =
                apiService.obtenerOficios().execute();

        // Assert: verificar resultados
        assertEquals(200, response.code());
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(1, response.body().size());
    }

    // AUT - NET-05: Login Invalido - HTTP 401
    @Test
    public  void login_respuesta401() throws IOException {

        // Arrange: simular credenciales incorrectas
        mockWebServer.enqueue(
                new MockResponse()
                .setResponseCode(401)
                        .setBody("\"error\":\"Credenciales incorrectas\"}")
        );

        // Act: realizar login
        LoginRequest datos = new LoginRequest("test@gmail.com", "incorrecta");

        Response<LoginResponse> response =
                apiService.login(datos).execute();

        // Assert: verificar resultado
        assertEquals(401, response.code());
        assertFalse(response.isSuccessful());
        assertNotNull(response.errorBody());
    }
}