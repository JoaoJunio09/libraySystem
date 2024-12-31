package model.services;

import java.util.List;

import model.dao.CRUD;
import model.dao.DaoFactory;
import model.entities.Livro;

public class LivroService {
	
	private CRUD<Livro> dao = DaoFactory.createLivroDaoJDBC();
	
	public List<Livro> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Livro obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deleteById(Livro obj) {
		dao.deleteById(obj.getId());
	}
}