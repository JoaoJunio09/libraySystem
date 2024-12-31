package model.dao;

import java.util.List;

import model.entities.Livro;

public interface LivroDao {
	
	boolean insert(Livro obj);

	boolean update(Livro obj);

	boolean deleteById(Integer id);

	Livro findById(Integer id);

	List<Livro> findAll();
	
	List<Livro> search(String name);
}