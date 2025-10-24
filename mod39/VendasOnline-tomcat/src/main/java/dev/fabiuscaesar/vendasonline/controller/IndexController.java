/**
 * 
 */
package dev.fabiuscaesar.vendasonline.controller;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 * @author FabiusCaesar
 * @date 14 de out. de 2025
 */

@Named
@ViewScoped
public class IndexController implements Serializable {

	private static final long serialVersionUID = 1L;

	public String redirectCliente() {
		return "/cliente/list.xhtml";
	}

	public String redirectProduto() {
		return "/produto/list.xhtml";
	}

	public String redirectVenda() {
		return "/venda/list.xhtml";
	}
}