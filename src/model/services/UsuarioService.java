package model.services;

import java.util.List;

import model.dao.CRUD;
import model.dao.DaoFactory;
import model.entities.Usuario;

public class UsuarioService {

	private CRUD<Usuario> dao = DaoFactory.createUsuarioDaoJDBC();
	
	public void saveOrUpdate(Usuario obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public List<Usuario> findAll() {
		return dao.findAll();
	}
	
	public boolean login(Usuario obj) {
		List<Usuario> list = findAll();
		
		for (Usuario admin : list) {
			if (admin.getLogin().equals(obj.getLogin()) && admin.getSenha().equals(obj.getSenha()) && 
					admin.getTipo().equals(obj.getTipo())) {
				return true;
			}
		}
		
		return false;
	}
}