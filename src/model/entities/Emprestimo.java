package model.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Emprestimo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String pendente = "Pendente";

	private Integer id;
	private Cliente cliente;
	private Livro livro;
	private Date dataEmprestimo;
	private Date dataDevolucao;
	private String descricao;
	private String status = "Pendente";
	
	public Emprestimo() {
	}

	public Emprestimo(Integer id, Cliente cliente, Livro livro, Date dataEmprestimo, Date dataDevolucao,
			String descricao) {
		this.id = id;
		this.cliente = cliente;
		this.livro = livro;
		this.dataEmprestimo = dataEmprestimo;
		this.dataDevolucao = dataDevolucao;
		this.descricao = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public Date getDataEmprestimo() {
		return dataEmprestimo;
	}

	public void setDataEmprestimo(Date dataEmprestimo) {
		this.dataEmprestimo = dataEmprestimo;
	}

	public Date getDataDevolucao() {
		return dataDevolucao;
	}

	public void setDataDevolucao(Date dataDevolucao) {
		this.dataDevolucao = dataDevolucao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setStatusDevolvido() {
		this.status = "Devolvido";
	}
	
	public void setStatusNaoDevolvido() {
		this.status = "Não Devolvido";
	}
	
	public void setStatusPendente() {
		this.status = pendente;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Emprestimo other = (Emprestimo) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Emprestimo [id=" + id + ", cliente=" + cliente + ", livro=" + livro + ", dataEmprestimo="
				+ dataEmprestimo + ", dataDevolucao=" + dataDevolucao + ", status=" + status + "]";
	}
}
