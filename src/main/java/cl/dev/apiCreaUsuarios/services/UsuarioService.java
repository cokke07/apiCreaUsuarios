package cl.dev.apiCreaUsuarios.services;

import cl.dev.apiCreaUsuarios.dto.UsuarioCreatedDTO;
import cl.dev.apiCreaUsuarios.dto.UsuarioEncontradoDTO;
import cl.dev.apiCreaUsuarios.dto.UsuarioListDTO;
import cl.dev.apiCreaUsuarios.exceptions.ApiError;
import cl.dev.apiCreaUsuarios.model.Usuario;

import java.util.List;

public interface UsuarioService {
    public List<UsuarioListDTO> buscarTodos() throws ApiError;
    public UsuarioEncontradoDTO buscarPorId(Long id) throws ApiError;
    public UsuarioCreatedDTO crearUsuario(Usuario u) throws ApiError;
    public UsuarioCreatedDTO editarUsuario(Usuario u, Long id) throws ApiError;
    public void eliminarUsuario(Long id) throws ApiError;
}
