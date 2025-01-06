package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.CRUD;
import model.entities.Usuario;

public class UsuarioDaoJDBC implements CRUD<Usuario> {
	
	private Connection conn = null;
	
	public UsuarioDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public boolean insert(Usuario obj) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO tb_usuario "
					+ "(Login, Senha, Tipo) "
					+ "VALUES "
					+ "(?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, obj.getLogin());
			stmt.setString(2, obj.getSenha());
			stmt.setInt(3, obj.getTipo());
			
			int linhasAfetadas = stmt.executeUpdate();
			
			if (linhasAfetadas > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
					return true;
				}
			}
			else {
				throw new DbException("Erro: nenhuma linha foi afetada.");
			}
			return false;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(null);
		}
	}

	@Override
	public boolean update(Usuario obj) {
		PreparedStatement stmt = null;
		String sql = "UPDATE tb_usuario "
					+ "SET Nome = ?, Sigla = ?, Tipo = ? "
					+ "WHERE Id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, obj.getLogin());
			stmt.setString(2, obj.getSenha());
			stmt.setInt(3, obj.getTipo());
			stmt.setInt(4, obj.getId());
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
	public boolean deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM tb_usuario WHERE Id = ?";
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
	public Usuario findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM tb_usuario WHERE Id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				Usuario obj = instanciaAdmin(rs);
				return obj;
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
	public List<Usuario> findAll() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM tb_usuario";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			List<Usuario> list = new ArrayList<Usuario>();
			while (rs.next()) {
				Usuario obj = instanciaAdmin(rs);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(stmt);
			DB.closeResultSet(rs);
		}
	}
	
	private Usuario instanciaAdmin(ResultSet rs) throws SQLException {
		Usuario obj = new Usuario();
		obj.setId(rs.getInt("Id"));
		obj.setLogin(rs.getString("Login"));
		obj.setSenha(rs.getString("Senha"));
		obj.setTipo(rs.getInt("Tipo"));
		return obj;
	}
}