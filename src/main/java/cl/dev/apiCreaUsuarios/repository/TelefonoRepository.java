package cl.dev.apiCreaUsuarios.repository;

import cl.dev.apiCreaUsuarios.model.Telefono;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefonoRepository extends JpaRepository<Telefono, Long> {
}
