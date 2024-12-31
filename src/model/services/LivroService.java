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
	
	public void updateDataAll() {
		List<Livro> list = dao.findAll();
		
		for (Livro livro : list) {
			if (livro.getEstoque() < 1) {
				livro.setLivroIndisponivel();
				saveOrUpdate(livro);
			}
			if (livro.getEstoque() > 0) {
				livro.setLivroDisponivel();
				saveOrUpdate(livro);
			}
		}
	}
}