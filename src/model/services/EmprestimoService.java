package model.services;

import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	public void updateDataAll() {
		List<Emprestimo> list = dao.findAll();
		
		for (Emprestimo emp : list) {
			Date newDate = new Date();
			Date date = emp.getDataDevolucao();
			
			if (date.before(newDate)) {
				emp.setStatusNaoDevolvido();
			}
			else {
				emp.setStatusPendente();
			}
			
			saveOrUpdate(emp);
		}
	}
	
	public List<Emprestimo> filtrar(String dataEmprestimo, String dataDevolucao) {
		return dao.filtrar(dataEmprestimo, dataDevolucao);
	}
	
	public List<Emprestimo> filtragemCompleta(String sql) {
		return dao.filtragemCompleta(sql);
	}
}