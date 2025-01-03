package model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.entities.Emprestimo;

public interface EmprestimoDao {
	
	boolean insert(Emprestimo obj);

	boolean update(Emprestimo obj);

	boolean deleteById(Integer id);

	Emprestimo findById(Integer id);

	List<Emprestimo> findAll();
	
	List<Emprestimo> filtrar(String dataInicial, String dataFinal);

	List<Emprestimo> filtragemCompleta(String sql);

	Map<Integer, ArrayList<Integer>> listarQuantidadeVendasPorMes();
}