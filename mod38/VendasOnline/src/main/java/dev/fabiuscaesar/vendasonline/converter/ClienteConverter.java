/**
 * 
 */
package dev.fabiuscaesar.vendasonline.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import dev.fabiuscaesar.vendasonline.domain.Cliente;

/**
 * Converte entre a String do componente e o objeto Cliente.
 * Estratégia idêntica ao ProdutoConverter.
 * 
 * @author FabiusCaesar
 * @date 17 de out. de 2025
 */

@FacesConverter(value = "clienteConverter", managed = true)
public class ClienteConverter implements Converter<Cliente> {

    private static final String VIEW_KEY = ClienteConverter.class.getName();

    @Override
    public Cliente getAsObject(FacesContext ctx, UIComponent comp, String key) {
        if (key == null || key.isBlank()) return null;
        Object obj = getStore(ctx).get(key);
        return (obj instanceof Cliente) ? (Cliente) obj : null;
    }

    @Override
    public String getAsString(FacesContext ctx, UIComponent comp, Cliente value) {
        if (value == null) return "";
        String k = (value.getId() != null) ? ("id:" + value.getId()) : ("tmp:" + System.identityHashCode(value));
        getStore(ctx).put(k, value);
        return k;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getStore(FacesContext ctx) {
        Map<String, Object> viewMap = ctx.getViewRoot().getViewMap();
        return (Map<String, Object>) viewMap.computeIfAbsent(VIEW_KEY, _k -> new HashMap<>());
    }
}
