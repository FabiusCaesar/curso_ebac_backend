/**
 * 
 */
package dev.fabiuscaesar.vendasonline.domain;

/**
 * Marker/base para entidades persistentes.
 * Facilita a criação de DAOs e Services genéricos.
 * 
 * @author FabiusCaesar
 * @date 10 de out. de 2025
 */

public interface Persistente {
	
    public Long getId();
    
    public void setId(Long id);

}
