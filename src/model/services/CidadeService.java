package model.services;

import java.util.List;

import model.dao.CRUD;
import model.dao.DaoFactory;
import model.entities.Cidade;

public class CidadeService {
	
	private CRUD<Cidade> dao = DaoFactory.createCidadeDaoJDBC();
	
	public List<Cidade> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Cidade obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deleteById(Cidade obj) {
		dao.deleteById(obj.getId());
	}
}