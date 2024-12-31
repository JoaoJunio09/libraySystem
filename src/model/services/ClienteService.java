package model.services;

import java.util.List;

import model.dao.ClienteDao;
import model.dao.DaoFactory;
import model.entities.Cliente;

public class ClienteService {

	private final ClienteDao dao = DaoFactory.createClienteDaoJDBC();
	
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
	
	public List<Cliente> search(String name) {
		return dao.search(name);
	}
}