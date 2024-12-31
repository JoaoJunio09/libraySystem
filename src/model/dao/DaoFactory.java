package model.dao;

import db.DB;
import model.dao.impl.CategoriaDaoJDBC;
import model.dao.impl.CidadeDaoJDBC;
import model.dao.impl.ClienteDaoJDBC;
import model.dao.impl.EmprestimoDaoJDBC;
import model.dao.impl.EstadoDaoJDBC;
import model.dao.impl.FornecedorDaoJDBC;
import model.dao.impl.LivroDaoJDBC;
import model.entities.Categoria;
import model.entities.Cidade;
import model.entities.Estado;
import model.entities.Fornecedor;

public class DaoFactory {

	public static ClienteDao createClienteDaoJDBC() {
		return new ClienteDaoJDBC(DB.getConnection());
	}
	
	public static CRUD<Cidade> createCidadeDaoJDBC() {	
		return new CidadeDaoJDBC(DB.getConnection());
	}
	
	public static CRUD<Estado> createEstadoDaoJDBC() {
		return new EstadoDaoJDBC(DB.getConnection());
	}
	
	public static CRUD<Categoria> createCategoriaDaoJDBC() {
		return new CategoriaDaoJDBC(DB.getConnection());
	}
	
	public static LivroDao createLivroDaoJDBC() {
		return new LivroDaoJDBC(DB.getConnection());
	}
	
	public static CRUD<Fornecedor> createFornecedorDaoJDBC() {
		return new FornecedorDaoJDBC(DB.getConnection());
	}
	
	public static EmprestimoDao createEmprestimoDaoJDBC() {
		return new EmprestimoDaoJDBC(DB.getConnection());
	}
}