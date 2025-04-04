package cl.dev.apiCreaUsuarios.repository;

import cl.dev.apiCreaUsuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Override
    List<Usuario> findAll();

    //Agregamos la validacion para buscar el correo agregado
    Optional<Usuario> findByEmail(String email);
}
