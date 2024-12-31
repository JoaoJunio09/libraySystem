package model.dao;

import java.util.List;

import model.entities.Cliente;

public interface ClienteDao {
	
	boolean insert(Cliente obj);

	boolean update(Cliente obj);

	boolean deleteById(Integer id);

	Cliente findById(Integer id);

	List<Cliente> findAll();
	
	List<Cliente> search(String name);
}