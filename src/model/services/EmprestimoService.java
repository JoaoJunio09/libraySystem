package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.EmprestimoDao;
import model.entities.Emprestimo;

public class EmprestimoService {
	
	private EmprestimoDao dao = DaoFactory.createEmprestimoDaoJDBC();
	
	public List<Emprestimo> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Emprestimo obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void deleteById(Emprestimo obj) {
		dao.deleteById(obj.getId());
	}
	
	public List<Emprestimo> filtrar(String dataEmprestimo, String dataDevolucao) {
		return dao.filtrar(dataEmprestimo, dataDevolucao);
	}
}