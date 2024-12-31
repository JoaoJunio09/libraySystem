package model.services;

import java.util.List;

import model.dao.CRUD;
import model.dao.DaoFactory;
import model.entities.Estado;

public class EstadoService {
	
	private CRUD<Estado> dao = DaoFactory.createEstadoDaoJDBC();
	
	public List<Estado> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Estado obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deleteById(Estado obj) {
		dao.deleteById(obj.getId());
	}
}