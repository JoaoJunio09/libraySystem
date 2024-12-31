package model.services;

import java.util.List;

import model.dao.CRUD;
import model.dao.DaoFactory;
import model.entities.Cliente;

public class ClienteService {

	private final CRUD<Cliente> dao = DaoFactory.createClienteDaoJDBC();
	
	public List<Cliente> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Cliente obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deleteById(Cliente obj) {
		dao.deleteById(obj.getId());
	}
}
