package cl.dev.apiCreaUsuarios.services

import cl.dev.apiCreaUsuarios.dto.UsuarioCreatedDTO
import cl.dev.apiCreaUsuarios.dto.UsuarioEncontradoDTO
import cl.dev.apiCreaUsuarios.dto.UsuarioListDTO
import cl.dev.apiCreaUsuarios.exceptions.ApiError
import cl.dev.apiCreaUsuarios.model.Telefono
import cl.dev.apiCreaUsuarios.model.Usuario
import cl.dev.apiCreaUsuarios.repository.UsuarioRepository
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDateTime

class UsuarioServiceImplSpec extends Specification {

    // Mock del repositorio
    UsuarioRepository usuarioRepository = Mock()

    // Se inyecta el servicio con el mock del repositorio
    @Subject
    UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(usuarioRepository: usuarioRepository)

    def setup() {
        usuarioService.usuarioRepository = usuarioRepository
        // Inyectamos las propiedades del YML que normalmente vienen con @Value
        ReflectionTestUtils.setField(usuarioService, 'regexEmail', '^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$')
        ReflectionTestUtils.setField(usuarioService, 'regexPassword', '^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$')
    }

    def "buscarTodos - debería retornar lista de usuarios cuando hay datos"() {
        given:
        def telefono= [
                new Telefono(id:1L, citycode: "9", number: "12345678", countrycode: "56"),
                new Telefono(id:2L, citycode: "9", number: "87654321", countrycode: "56"),
                new Telefono(id:3L, citycode: "9", number: "12345678", countrycode: "56")
        ]
        def usuarios = [
                new Usuario(id: 1L, name: "Juan", email: "juan@example.com", phones: telefono),
                new Usuario(id: 2L, name: "Maria", email: "maria@example.com", phones: telefono)
        ]
        usuarioRepository.findAll() >> usuarios

        when:
        List<UsuarioListDTO> resultado = usuarioService.buscarTodos()

        then:
        resultado.size() == 2
        resultado[0].nombre == "Juan"
        resultado[1].email == "maria@example.com"
    }

    def "buscarTodos - debería lanzar ApiError cuando no hay usuarios"() {
        given:
        usuarioRepository.findAll() >> []

        when:
        usuarioService.buscarTodos()

        then:
        def ex = thrown(ApiError)
        ex.message == "La lista de usuarios esta vacia"
        ex.status == HttpStatus.NOT_FOUND
    }

    def "buscarPorId - debería retornar un usuario cuando existe"() {
        given:
        def usuario = new Usuario(id: 1L, name: "Juan", email: "juan@example.com", created: LocalDateTime.now(), active: true)
        usuarioRepository.findById(1L) >> Optional.of(usuario)

        when:
        UsuarioEncontradoDTO resultado = usuarioService.buscarPorId(1L)

        then:
        resultado.id == "1"
        resultado.nombre == "Juan"
        resultado.email == "juan@example.com"
        resultado.estado == "activo"
    }

    def "buscarPorId - debería lanzar ApiError si el usuario no existe"() {
        given:
        usuarioRepository.findById(1L) >> Optional.empty()

        when:
        usuarioService.buscarPorId(1L)

        then:
        def ex = thrown(ApiError)
        ex.message == "El usuario no fue encontrado"
        ex.status == HttpStatus.NOT_FOUND
    }

    def "crearUsuario - debería guardar un usuario válido y retornar DTO"() {
        given:
        def telefono= [
                new Telefono(id:1L, citycode: "9", number: "12345678", countrycode: "56"),
                new Telefono(id:2L, citycode: "9", number: "87654321", countrycode: "56")
        ]
        def usuario = new Usuario(name: "Pedro", email: "pedro@example.com", password: "Password1", phones: telefono)
        usuarioRepository.save(_ as Usuario) >> { Usuario u -> u.id = 1L; u }

        when:
        UsuarioCreatedDTO resultado = usuarioService.crearUsuario(usuario)

        then:
        resultado.id == 1L
        resultado.name == "Pedro"
        resultado.email == "pedro@example.com"
        resultado.active == true
    }

    @Unroll
    def "crearUsuario - debería lanzar ApiError si el email o password es inválido"() {
        given:
        def telefono= [
                new Telefono(id:1L, citycode: "9", number: "12345678", countrycode: "56"),
                new Telefono(id:2L, citycode: "9", number: "87654321", countrycode: "56")
        ]
        def usuario = new Usuario(name: "Pedro", email: email, password: password, phones: telefono)

        when:
        usuarioService.crearUsuario(usuario)

        then:
        def ex = thrown(ApiError)
        ex.message == mensajeEsperado
        ex.status == HttpStatus.BAD_REQUEST

        where:
        email                  | password      || mensajeEsperado
        "pedroArrobamal"        | "Password1"  || "El correo no tiene el formato correcto"
        "pedro@example.com"    | "abc123"     || "El password debe tener al menos 1 Mayuscula, 1 numero y 8 digitos minimo"
    }

    def "editarUsuario - debería actualizar un usuario existente"() {
        given:
        def telefono= [
                new Telefono(id:1L, citycode: "9", number: "12345678", countrycode: "56"),
                new Telefono(id:2L, citycode: "9", number: "87654321", countrycode: "56")
        ]
        def usuarioExistente = new Usuario(id: 1L, name: "Pedro", email: "pedro@example.com", password: "Password1", phones: telefono, created: LocalDateTime.now())
        usuarioRepository.findById(1L) >> Optional.of(usuarioExistente)
        usuarioRepository.save(_ as Usuario) >> { Usuario u -> u }

        def usuarioEditado = new Usuario(name: "Pedro Modificado", email: "pedro.new@example.com", password: "NewPassword1", phones: telefono)

        when:
        UsuarioCreatedDTO resultado = usuarioService.editarUsuario(usuarioEditado, 1L)

        then:
        resultado.id == 1L
        resultado.name == "Pedro Modificado"
        resultado.email == "pedro.new@example.com"
    }


    def "eliminarUsuario - debería eliminar usuario existente"() {
        given:
        usuarioRepository.findById(1L) >> Optional.of(new Usuario(id: 1L))
        usuarioRepository.deleteById(1L) >> {}

        when:
        usuarioService.eliminarUsuario(1L)

        then:
        noExceptionThrown()
    }

    def "eliminarUsuario - debería lanzar ApiError si el usuario no existe"() {
        given:
        usuarioRepository.findById(1L) >> Optional.empty()

        when:
        usuarioService.eliminarUsuario(1L)

        then:
        def ex = thrown(ApiError)
        ex.message == "No se encontro el Id para eliminar el Usuario"
        ex.status == HttpStatus.NOT_FOUND
    }
}
