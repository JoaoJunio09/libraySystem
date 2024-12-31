package model.services;

import java.util.List;

import model.dao.CRUD;
import model.dao.DaoFactory;
import model.entities.Categoria;

public class CategoriaService {
	
	private CRUD<Categoria> dao = DaoFactory.createCategoriaDaoJDBC();
	
	public List<Categoria> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Categoria obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deleteById(Categoria obj) {
		dao.deleteById(obj.getId());
	}
}