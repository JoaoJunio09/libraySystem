package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.EmprestimoDao;
import model.entities.Categoria;
import model.entities.Cidade;
import model.entities.Cliente;
import model.entities.Emprestimo;
import model.entities.Estado;
import model.entities.Livro;

public class EmprestimoDaoJDBC implements EmprestimoDao {
	
	private Connection conn = null;
	
	public EmprestimoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public boolean insert(Emprestimo obj) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO tb_emprestimo "
					+ "(ClienteId, LivroId, DataEmprestimo, DataDevolucao, Descrição, Status) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, obj.getCliente().getId());
			stmt.setInt(2, obj.getLivro().getId());
			stmt.setDate(3, new java.sql.Date(obj.getDataEmprestimo().getTime()));
			stmt.setDate(4, new java.sql.Date(obj.getDataDevolucao().getTime()));
			stmt.setString(5, obj.getDescricao());
			stmt.setString(6, obj.getStatus());
			int linhasAfetadas = stmt.executeUpdate();
			if (linhasAfetadas > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
					return true;
				}
				DB.closeResultSet(rs);
			}
			return false;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
		}
	}

	@Override
	public boolean update(Emprestimo obj) {
		PreparedStatement stmt = null;
		String sql = "UPDATE tb_emprestimo "
					+ "SET ClienteId = ?, LivroId = ?, DataEmprestimo = ?, DataDevolucao = ?, Descrição = ?, Status = ? "
					+ "WHERE Id = ?";
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, obj.getCliente().getId());
			stmt.setInt(2, obj.getLivro().getId());
			stmt.setDate(3, new java.sql.Date(obj.getDataEmprestimo().getTime()));
			stmt.setDate(4, new java.sql.Date(obj.getDataDevolucao().getTime()));
			stmt.setString(5, obj.getDescricao());
			stmt.setString(6, obj.getStatus());
			stmt.setInt(7, obj.getId());
			int linhasAfetadas = stmt.executeUpdate();
			if (linhasAfetadas > 0) {
				return true;
			}
			return false;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
		}
	}

	@Override
	public boolean deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM tb_emprestimo WHERE Id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, id);
			
			stmt.executeUpdate();
			
			return true;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
		}
	}

	@Override
	public Emprestimo findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT emp.*,cli.*,liv.*,cid.*,est.*, "
					+ "cid.Nome AS Cidade_nome, cid.Id AS Cidade_id, "
					+ "est.Nome AS Estado_nome, est.Id AS Estado_id, "
					+ "liv.Nome AS Livro_nome, liv.Id AS Livro_id, "
					+ "cat.Id AS Categoria_id, cat.Nome Categoria_nome, cat.Descrição AS Categoria_descrição, "
					+ "cli.Id AS Cliente_id "
					+ "FROM tb_emprestimo emp "
					+ "JOIN tb_cliente cli ON emp.ClienteId = cli.Id "
					+ "JOIN tb_livro liv ON emp.LivroId = liv.Id "
					+ "JOIN tb_categoria cat ON liv.CategoriaId = cat.Id "
					+ "JOIN tb_cidade cid ON cli.CidadeId = cid.Id "
					+ "JOIN tb_estado est ON cid.EstadoId = est.Id "
					+ "WHERE emp.Id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Estado estado = instanciaEstado(rs);
				Cidade cidade = instanciaCidade(rs, estado);
				Categoria cat = instanciaCategoria(rs);
				Livro livro = instanciaLivro(rs, cat);
				Cliente cliente = instanciaCliente(rs, cidade, estado);
				Emprestimo emp = instanciaEmprestimo(rs, cliente, livro);
				return emp;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Emprestimo> findAll() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT emp.*,cli.*,liv.*,cid.*,est.*, "
					+ "cid.Nome AS Cidade_nome, cid.Id AS Cidade_id, "
					+ "est.Nome AS Estado_nome, est.Id AS Estado_id, "
					+ "liv.Nome AS Livro_nome, liv.Id AS Livro_id, liv.Descrição As Livro_descrição, "
					+ "cat.Id AS Categoria_id, cat.Nome Categoria_nome, cat.Descrição AS Categoria_descrição, "
					+ "cli.Id AS Cliente_id "
					+ "FROM tb_emprestimo emp "
					+ "JOIN tb_cliente cli ON emp.ClienteId = cli.Id "
					+ "JOIN tb_livro liv ON emp.LivroId = liv.Id "
					+ "JOIN tb_categoria cat ON liv.CategoriaId = cat.Id "
					+ "JOIN tb_cidade cid ON cli.CidadeId = cid.Id "
					+ "JOIN tb_estado est ON cid.EstadoId = est.Id";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Emprestimo> emps = new ArrayList<Emprestimo>();
			Map<Integer, Cliente> mapCliente = new HashMap<>();
			Map<Integer, Livro> mapLivro= new HashMap<>();
			
			while (rs.next()) {
				
				Estado estado = instanciaEstado(rs);
				Cidade cidade = instanciaCidade(rs, estado);
				Categoria cat = instanciaCategoria(rs);
				
				Cliente cliente = mapCliente.get(rs.getInt("ClienteId"));
				if (cliente == null) {
					cliente = instanciaCliente(rs, cidade, estado);
					mapCliente.put(rs.getInt("ClienteId"), cliente);
				}
				
				Livro livro = mapLivro.get(rs.getInt("LivroId"));
				if (livro == null) {
					livro = instanciaLivro(rs, cat);
					mapLivro.put(rs.getInt("LivroId"), livro);
				}
				
				Emprestimo emp = instanciaEmprestimo(rs, cliente, livro);
				emps.add(emp);
			}
			return emps;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(rs);
		}
	}
	
	public List<Emprestimo> filtrar(String dataEmprestimo, String dataDevolucao) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT emp.*,cli.*,liv.*,cid.*,est.*, "
					+ "cid.Nome AS Cidade_nome, cid.Id AS Cidade_id, "
					+ "est.Nome AS Estado_nome, est.Id AS Estado_id, "
					+ "liv.Nome AS Livro_nome, liv.Id AS Livro_id, liv.Descrição As Livro_descrição, "
					+ "cat.Id AS Categoria_id, cat.Nome Categoria_nome, cat.Descrição AS Categoria_descrição, "
					+ "cli.Id AS Cliente_id "
					+ "FROM tb_emprestimo emp "
					+ "JOIN tb_cliente cli ON emp.ClienteId = cli.Id "
					+ "JOIN tb_livro liv ON emp.LivroId = liv.Id "
					+ "JOIN tb_categoria cat ON liv.CategoriaId = cat.Id "
					+ "JOIN tb_cidade cid ON cli.CidadeId = cid.Id "
					+ "JOIN tb_estado est ON cid.EstadoId = est.Id "
					+ "WHERE emp.DataEmprestimo BETWEEN '" + dataEmprestimo + "' and '" + dataDevolucao + "'";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Emprestimo> emps = new ArrayList<Emprestimo>();
			Map<Integer, Cliente> mapCliente = new HashMap<>();
			Map<Integer, Livro> mapLivro= new HashMap<>();
			
			while (rs.next()) {
				
				Estado estado = instanciaEstado(rs);
				Cidade cidade = instanciaCidade(rs, estado);
				Categoria cat = instanciaCategoria(rs);
				
				Cliente cliente = mapCliente.get(rs.getInt("ClienteId"));
				if (cliente == null) {
					cliente = instanciaCliente(rs, cidade, estado);
					mapCliente.put(rs.getInt("ClienteId"), cliente);
				}
				
				Livro livro = mapLivro.get(rs.getInt("LivroId"));
				if (livro == null) {
					livro = instanciaLivro(rs, cat);
					mapLivro.put(rs.getInt("LivroId"), livro);
				}
				
				Emprestimo emp = instanciaEmprestimo(rs, cliente, livro);
				emps.add(emp);
			}
			return emps;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(rs);
		}
	}
	
	@Override
	public List<Emprestimo> filtragemCompleta(String sqlComand) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT emp.*,cli.*,liv.*,cid.*,est.*, "
					+ "cid.Nome AS Cidade_nome, cid.Id AS Cidade_id, "
					+ "est.Nome AS Estado_nome, est.Id AS Estado_id, "
					+ "liv.Nome AS Livro_nome, liv.Id AS Livro_id, liv.Descrição As Livro_descrição, "
					+ "cat.Id AS Categoria_id, cat.Nome Categoria_nome, cat.Descrição AS Categoria_descrição, "
					+ "cli.Id AS Cliente_id "
					+ "FROM tb_emprestimo emp "
					+ "JOIN tb_cliente cli ON emp.ClienteId = cli.Id "
					+ "JOIN tb_livro liv ON emp.LivroId = liv.Id "
					+ "JOIN tb_categoria cat ON liv.CategoriaId = cat.Id "
					+ "JOIN tb_cidade cid ON cli.CidadeId = cid.Id "
					+ "JOIN tb_estado est ON cid.EstadoId = est.Id "
					+ sqlComand;
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Emprestimo> emps = new ArrayList<Emprestimo>();
			Map<Integer, Cliente> mapCliente = new HashMap<>();
			Map<Integer, Livro> mapLivro= new HashMap<>();
			
			while (rs.next()) {
				
				Estado estado = instanciaEstado(rs);
				Cidade cidade = instanciaCidade(rs, estado);
				Categoria cat = instanciaCategoria(rs);
				
				Cliente cliente = mapCliente.get(rs.getInt("ClienteId"));
				if (cliente == null) {
					cliente = instanciaCliente(rs, cidade, estado);
					mapCliente.put(rs.getInt("ClienteId"), cliente);
				}
				
				Livro livro = mapLivro.get(rs.getInt("LivroId"));
				if (livro == null) {
					livro = instanciaLivro(rs, cat);
					mapLivro.put(rs.getInt("LivroId"), livro);
				}
				
				Emprestimo emp = instanciaEmprestimo(rs, cliente, livro);
				emps.add(emp);
			}
			return emps;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(rs);
		}
	}
	
	@Override
	public Map<Integer, ArrayList<Integer>> listarQuantidadeEmprestimosPorMes() {
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT \n"
		            + "COUNT(Id) AS total_emprestimos, \n"
		            + "YEAR(DataEmprestimo) AS ano,\n"
		            + "MONTH(DataEmprestimo) AS mes\n"
		            + "FROM \n"
		            + "tb_emprestimo\n"
		            + "GROUP BY \n"
		            + "YEAR(DataEmprestimo),\n"
		            + "MONTH(DataEmprestimo)\n"
		            + "ORDER BY \n"
		            + "ano,\n"
		            + "mes;";

	    Map<Integer, ArrayList<Integer>> retorno = new HashMap<>();

	    try {
	        stmt = conn.prepareStatement(sql);
	        rs = stmt.executeQuery();

	        while (rs.next()) {
	            int ano = rs.getInt("ano");
	            int mes = rs.getInt("mes");
	            int totalEmprestimos = rs.getInt("total_emprestimos");

	            // Verifica se o ano já existe no mapa
	            if (!retorno.containsKey(ano)) {
	                // Cria uma nova lista para o ano e adiciona o mês e a quantidade
	                ArrayList<Integer> linha = new ArrayList<>();
	                linha.add(mes);
	                linha.add(totalEmprestimos);
	                retorno.put(ano, linha);
	            } else {
	                // Se o ano já existe, adiciona mês e quantidade ao ArrayList existente
	                ArrayList<Integer> linhaExistente = retorno.get(ano);
	                linhaExistente.add(mes);
	                linhaExistente.add(totalEmprestimos);
	            }
	        }
	        return retorno;
	    } 
	    catch (SQLException e) {
	        e.printStackTrace();
	        throw new DbException(e.getMessage());
	    }
	}
	
	@Override
	public Map<Integer, ArrayList<Integer>> listarQuantidadeEmprestimosDevolvidosPorMes() {
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT\n"
	    			+ "COUNT(Id) AS total_emprestimos, \n"
	    			+ "YEAR(DataEmprestimo) AS ano,\n"
	    			+ "MONTH(DataEmprestimo) AS mes\n"
	    			+ "FROM \n"
	    			+ "tb_emprestimo\n"
	    			+ "WHERE Status = 'Devolvido'\n"
	    			+ "GROUP BY \n"
	    			+ "YEAR(DataEmprestimo),\n"
	    			+ "MONTH(DataEmprestimo)\n"
	    			+ "ORDER BY \n"
	    			+ "ano,\n"
	    			+ "mes;";

	    Map<Integer, ArrayList<Integer>> retorno = new HashMap<>();

	    try {
	        stmt = conn.prepareStatement(sql);
	        rs = stmt.executeQuery();

	        while (rs.next()) {
	            int ano = rs.getInt("ano");
	            int mes = rs.getInt("mes");
	            int totalEmprestimos = rs.getInt("total_emprestimos");

	            // Verifica se o ano já existe no mapa
	            if (!retorno.containsKey(ano)) {
	                // Cria uma nova lista para o ano e adiciona o mês e a quantidade
	                ArrayList<Integer> linha = new ArrayList<>();
	                linha.add(mes);
	                linha.add(totalEmprestimos);
	                retorno.put(ano, linha);
	            } else {
	                // Se o ano já existe, adiciona mês e quantidade ao ArrayList existente
	                ArrayList<Integer> linhaExistente = retorno.get(ano);
	                linhaExistente.add(mes);
	                linhaExistente.add(totalEmprestimos);
	            }
	        }
	        return retorno;
	    } 
	    catch (SQLException e) {
	        e.printStackTrace();
	        throw new DbException(e.getMessage());
	    }
	}
	
	@Override
	public List<Emprestimo> findAllStatusPendente() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT emp.*,cli.*,liv.*,cid.*,est.*, "
					+ "cid.Nome AS Cidade_nome, cid.Id AS Cidade_id, "
					+ "est.Nome AS Estado_nome, est.Id AS Estado_id, "
					+ "liv.Nome AS Livro_nome, liv.Id AS Livro_id, liv.Descrição As Livro_descrição, "
					+ "cat.Id AS Categoria_id, cat.Nome Categoria_nome, cat.Descrição AS Categoria_descrição, "
					+ "cli.Id AS Cliente_id "
					+ "FROM tb_emprestimo emp "
					+ "JOIN tb_cliente cli ON emp.ClienteId = cli.Id "
					+ "JOIN tb_livro liv ON emp.LivroId = liv.Id "
					+ "JOIN tb_categoria cat ON liv.CategoriaId = cat.Id "
					+ "JOIN tb_cidade cid ON cli.CidadeId = cid.Id "
					+ "JOIN tb_estado est ON cid.EstadoId = est.Id "
					+ "WHERE emp.Status = 'Pendente'";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Emprestimo> emps = new ArrayList<Emprestimo>();
			Map<Integer, Cliente> mapCliente = new HashMap<>();
			Map<Integer, Livro> mapLivro= new HashMap<>();
			
			while (rs.next()) {
				
				Estado estado = instanciaEstado(rs);
				Cidade cidade = instanciaCidade(rs, estado);
				Categoria cat = instanciaCategoria(rs);
				
				Cliente cliente = mapCliente.get(rs.getInt("ClienteId"));
				if (cliente == null) {
					cliente = instanciaCliente(rs, cidade, estado);
					mapCliente.put(rs.getInt("ClienteId"), cliente);
				}
				
				Livro livro = mapLivro.get(rs.getInt("LivroId"));
				if (livro == null) {
					livro = instanciaLivro(rs, cat);
					mapLivro.put(rs.getInt("LivroId"), livro);
				}
				
				Emprestimo emp = instanciaEmprestimo(rs, cliente, livro);
				emps.add(emp);
			}
			return emps;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(rs);
		}
	}
	
	private Emprestimo instanciaEmprestimo(ResultSet rs, Cliente cliente, Livro livro) throws SQLException {
		Emprestimo emp = new Emprestimo();
		emp.setId(rs.getInt("Id"));
		emp.setCliente(cliente);
		emp.setLivro(livro);
		emp.setDataEmprestimo(new java.util.Date(rs.getTimestamp("DataEmprestimo").getTime()));
		emp.setDataDevolucao(new java.util.Date(rs.getTimestamp("DataDevolucao").getTime()));
		emp.setDescricao(rs.getString("Descrição"));
		emp.setStatus(rs.getString("Status"));
		return emp;
	}
	
	private Cliente instanciaCliente(ResultSet rs, Cidade cidade, Estado estado) throws SQLException {
		Cliente cliente = new Cliente();
		cliente.setId(rs.getInt("Cliente_id"));
		cliente.setNome(rs.getString("Nome"));
		cliente.setSobrenome(rs.getString("Sobrenome"));
		cliente.setDataNascimento(rs.getDate("DataNascimento"));
		cliente.setTelefone(rs.getString("Telefone"));
		cliente.setEmail(rs.getString("Email"));
		cliente.setEndereco(rs.getString("Endereco"));
		cliente.setCidade(cidade);
		return cliente;
	}
	
	private Livro instanciaLivro(ResultSet rs, Categoria cat) throws SQLException {
		Livro livro = new Livro();
		livro.setId(rs.getInt("Livro_id"));
		livro.setNome(rs.getString("Livro_nome"));
		livro.setDescricao(rs.getString("Livro_descrição"));
		livro.setAutor(rs.getString("Autor"));
		livro.setEstoque(rs.getInt("Estoque"));
		livro.setDisponibilidade(rs.getString("Disponibilidade"));
		livro.setCategoria(cat);
		return livro;
	}
	
	private Categoria instanciaCategoria(ResultSet rs) throws SQLException {
		Categoria cat = new Categoria();
		cat.setId(rs.getInt("Categoria_id"));
		cat.setNome(rs.getString("Categoria_nome"));
		cat.setDescricao(rs.getString("Categoria_descrição"));
		return cat;
	}
	
	private Cidade instanciaCidade(ResultSet rs, Estado estado) throws SQLException {
		Cidade cidade = new Cidade();
		cidade.setId(rs.getInt("Cidade_id"));
		cidade.setNome(rs.getString("Cidade_nome"));
		cidade.setCep(rs.getString("Cep"));
		cidade.setEstado(estado);
		return cidade;
	}
	
	private Estado instanciaEstado(ResultSet rs) throws SQLException {
		Estado estado = new Estado();
		estado.setId(rs.getInt("Estado_id"));
		estado.setNome(rs.getString("Estado_nome"));
		estado.setSigla(rs.getString("Sigla"));
		return estado;
	}
}